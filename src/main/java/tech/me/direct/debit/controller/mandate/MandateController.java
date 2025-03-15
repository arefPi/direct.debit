package tech.me.direct.debit.controller.mandate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponse;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponseMapper;
import tech.me.direct.debit.service.mandate.create.CreateMandateService;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateRequest;
import tech.me.direct.debit.controller.mandate.complete.CompleteMandateRequest;
import tech.me.direct.debit.controller.mandate.complete.CompleteMandateRequestMapper;
import tech.me.direct.debit.service.mandate.complete.CompleteMandateService;
import tech.me.direct.debit.service.mandate.redirect.RedirectToProviderService;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderRequest;
import tech.me.direct.debit.controller.mandate.redirect.RedirectToProviderResponse;
import tech.me.direct.debit.service.mandate.callback.MandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;

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

    @PostMapping("/create")
    public ResponseEntity<CreateMandateResponse> createMandate(@AuthenticationPrincipal Jwt userJwt) {
        final var userId = userJwt.getSubject();

        final var createMandateRequest = new CreateMandateRequest(userId);

        final var createMandateResponse = createMandateService.create(createMandateRequest);

        return ResponseEntity.ok(createMandateResponseMapper.map(createMandateResponse));
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> completeMandate(@RequestBody CompleteMandateRequest requestDto) {
        final var request = completeMandateRequestMapper.map(requestDto);
        
        completeMandateService.completeMandate(request);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{mandateReferenceId}/redirect")
    public ResponseEntity<RedirectToProviderResponse> redirectToProvider(
            @PathVariable String mandateReferenceId) {
        final var request = new RedirectToProviderRequest(mandateReferenceId);
        final var response = redirectToProviderService.redirect(request);
        
        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .body(new RedirectToProviderResponse(response.redirectUrl()));
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> handleCallback(
            @RequestParam String code,
            @RequestParam String state,
            @RequestParam(required = false) String error) {
        
        final var request = new MandateCallbackRequest(code, state, error);
        mandateCallbackService.handleCallback(request);
        
        return ResponseEntity.ok().build();
    }
}
