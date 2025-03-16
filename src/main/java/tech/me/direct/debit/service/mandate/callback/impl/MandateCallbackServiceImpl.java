package tech.me.direct.debit.service.mandate.callback.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.me.direct.debit.service.mandate.callback.InitialMandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.MandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.TokenMandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;

@Service
@RequiredArgsConstructor
public class MandateCallbackServiceImpl implements MandateCallbackService {
    private final InitialMandateCallbackService initialMandateCallbackService;
    private final TokenMandateCallbackService tokenMandateCallbackService;

    @Override
    public void handleCallback(MandateCallbackRequest request) {
        final var mandate = initialMandateCallbackService.handleInitialCallback(request);
        tokenMandateCallbackService.handleTokenCallback(mandate, request.code());
    }
} 