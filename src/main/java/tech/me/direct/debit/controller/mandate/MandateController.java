package tech.me.direct.debit.controller.mandate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponse;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponseMapper;
import tech.me.direct.debit.service.mandate.create.CreateMandateService;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateRequest;
import tech.me.direct.debit.controller.mandate.complete.CompleteMandateRequest;
import tech.me.direct.debit.controller.mandate.complete.CompleteMandateRequestMapper;
import tech.me.direct.debit.service.mandate.complete.CompleteMandateService;

@RestController
@RequestMapping("/api/v1/mandates")
@RequiredArgsConstructor
public class MandateController {
    private final CreateMandateService createMandateService;
    private final CreateMandateResponseMapper createMandateResponseMapper;
    private final CompleteMandateService completeMandateService;
    private final CompleteMandateRequestMapper completeMandateRequestMapper;

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
}
