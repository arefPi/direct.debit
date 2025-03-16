package tech.me.direct.debit.service.mandate.callback.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.service.mandate.callback.InitialMandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.exception.InvalidStateException;
import tech.me.direct.debit.service.mandate.exception.MandateCallbackInternalException;
import tech.me.direct.debit.util.encryption.AESStateEncryptionService;
import tech.me.direct.debit.util.encryption.RedirectState;

@Service
@Transactional
@RequiredArgsConstructor
public class InitialMandateCallbackServiceImpl implements InitialMandateCallbackService {
    private final MandateRepository mandateRepository;
    private final AESStateEncryptionService aesStateEncryptionService;

    @Override
    public Mandate handleInitialCallback(MandateCallbackRequest request) {
        final var state = decryptState(request.state());
        final var mandate = validateAndGetMandate(state);

        if (request.error() != null) {
            handleError(mandate, request.error());
        }

        updateMandateToWaitingForVerification(mandate);
        return mandate;
    }

    private RedirectState decryptState(String encryptedState) {
        return aesStateEncryptionService.decrypt(encryptedState);
    }

    private Mandate validateAndGetMandate(RedirectState state) {
        final var mandate = mandateRepository.findByReferenceId(state.mandateReferenceId())
                .orElseThrow(() -> new MandateNotFoundException(state.mandateReferenceId()));

        if (!state.userId().equals(mandate.getUser().getUserId())) {
            throw new InvalidStateException(mandate.getReferenceId());
        }

        return mandate;
    }

    private void updateMandateToWaitingForVerification(Mandate mandate) {
        mandate.setStatus(MandateStatus.WAITING_FOR_VERIFICATION);
        mandateRepository.save(mandate);
    }

    private void handleError(Mandate mandate, String error) {
        switch (error) {
            case "canceled" -> {
                mandate.setStatus(MandateStatus.CANCELLED);
                mandateRepository.save(mandate);
                throw new MandateCallbackInternalException(error);
            }
            case "failed" -> {
                mandate.setStatus(MandateStatus.FAILED);
                mandateRepository.save(mandate);
                throw new MandateCallbackInternalException(error);
            }
            default -> throw new MandateCallbackInternalException(error);
        }
    }
} 