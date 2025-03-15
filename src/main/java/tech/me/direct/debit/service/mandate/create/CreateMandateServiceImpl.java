package tech.me.direct.debit.service.mandate.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.user.User;
import tech.me.direct.debit.persistence.user.UserRepository;
import tech.me.direct.debit.service.mandate.create.exception.UserNotFoundException;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateRequest;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateResponse;
import tech.me.direct.debit.service.mandate.create.reference.provider.MandateReferenceIdProvider;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateMandateServiceImpl implements CreateMandateService {
    private final UserRepository userRepository;
    private final MandateReferenceIdProvider mandateReferenceProvider;
    private final MandateRepository mandateRepository;

    @Override
    public CreateMandateResponse create(CreateMandateRequest request) {

        final var userId = request.userId();

        final var user = getUser(userId);
 
        final var mandate = createMandate(user);

        mandateRepository.save(mandate);
        
        return new CreateMandateResponse(mandate.getReferenceId());
    }

    private User getUser(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Mandate createMandate(User user ) {
        final var mandate = new Mandate();
        mandate.setReferenceId(mandateReferenceProvider.generate());
        mandate.setStatus(MandateStatus.INITIAL);
        mandate.setUser(user);
        return mandate;
    }

}
