package tech.me.direct.debit.controller.mandate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponse;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponseMapper;
import tech.me.direct.debit.service.mandate.create.CreateMandateService;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateRequest;

@RestController
@RequestMapping("/api/v1/mandates")
@RequiredArgsConstructor
public class MandateController {
    private final CreateMandateService createMandateService;
    private final CreateMandateResponseMapper createMandateResponseMapper;

    @PostMapping("/create")
    public ResponseEntity<CreateMandateResponse> createMandate(@AuthenticationPrincipal Jwt userJwt) {
        final var userId = userJwt.getSubject();

        final var createMandateRequest = new CreateMandateRequest(userId);

        final var createMandateResponse = createMandateService.create(createMandateRequest);

        return ResponseEntity.ok(createMandateResponseMapper.map(createMandateResponse));
    }
}
