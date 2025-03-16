package tech.me.direct.debit.controller.mandate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tech.me.direct.debit.controller.mandate.complete.CompleteMandateRequest;
import tech.me.direct.debit.controller.mandate.complete.CompleteMandateRequestMapper;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponse;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponseMapper;
import tech.me.direct.debit.controller.mandate.redirect.RedirectToProviderResponse;
import tech.me.direct.debit.service.mandate.callback.MandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;
import tech.me.direct.debit.service.mandate.complete.CompleteMandateService;
import tech.me.direct.debit.service.mandate.create.CreateMandateService;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateRequest;
import tech.me.direct.debit.service.mandate.redirect.RedirectToProviderService;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderRequest;

@RestController
@RequestMapping("/api/v1/mandates")
@RequiredArgsConstructor
public class MandateController {
    private final CreateMandateService createMandateService;
    private final CreateMandateResponseMapper createMandateResponseMapper;
    private final CompleteMandateService completeMandateService;
    private final CompleteMandateRequestMapper completeMandateRequestMapper;
    private final RedirectToProviderService redirectToProviderService;
    private final MandateCallbackService mandateCallbackService;

    @Operation(
        summary = "Create a new mandate",
        description = "Creates a new direct debit mandate for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mandate created successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<CreateMandateResponse> createMandate(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt userJwt) {
        final var userId = userJwt.getSubject();

        final var createMandateRequest = new CreateMandateRequest(userId);

        final var createMandateResponse = createMandateService.create(createMandateRequest);

        return ResponseEntity.ok(createMandateResponseMapper.map(createMandateResponse));
    }

    @Operation(
        summary = "Complete mandate setup",
        description = "Completes the mandate setup process with provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mandate completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Mandate not found or Provider not found"),
        @ApiResponse(responseCode = "400", description = "Mandate not in expected status"),
        @ApiResponse(responseCode = "409", description = "The mandate was modified by another operation")
    })
    @PostMapping("/complete")
    public ResponseEntity<Void> completeMandate(@RequestBody CompleteMandateRequest requestDto) {
        final var request = completeMandateRequestMapper.map(requestDto);
        
        completeMandateService.completeMandate(request);
        
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Redirect to provider",
        description = "Redirects the user to the payment provider for mandate authorization"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "307", description = "Temporary redirect to provider"),
        @ApiResponse(responseCode = "404", description = "Mandate not found"),
        @ApiResponse(responseCode = "400", description = "Mandate not in expected status"),
        @ApiResponse(responseCode = "500", description = "Provider service internal error or redirect service internal error"),
        @ApiResponse(responseCode = "409", description = "The mandate was modified by another operation")
    })
    @PostMapping("/{mandateReferenceId}/redirect")
    public ResponseEntity<RedirectToProviderResponse> redirectToProvider(
            @Parameter(description = "The mandate reference ID") 
            @PathVariable String mandateReferenceId) {
        final var request = new RedirectToProviderRequest(mandateReferenceId);
        final var response = redirectToProviderService.redirect(request);
        
        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .body(new RedirectToProviderResponse(response.redirectUrl()));
    }

    @Operation(
        summary = "Handle provider callback",
        description = "Processes the callback from the payment provider after mandate authorization"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Callback processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid callback parameters or invalid state"),
        @ApiResponse(responseCode = "404", description = "Mandate not found"),
        @ApiResponse(responseCode = "500", description = "Callback internal error or provider access token error"),
        @ApiResponse(responseCode = "409", description = "The mandate was modified by another operation")
    })
    @GetMapping("/callback")
    public ResponseEntity<Void> handleCallback(
            @Parameter(description = "Authorization code from provider") 
            @RequestParam String code,
            @Parameter(description = "State parameter for security verification") 
            @RequestParam String state,
            @Parameter(description = "Error message if authorization failed") 
            @RequestParam(required = false) String error) {
        
        final var request = new MandateCallbackRequest(code, state, error);
        mandateCallbackService.handleCallback(request);
        
        return ResponseEntity.ok().build();
    }
}
