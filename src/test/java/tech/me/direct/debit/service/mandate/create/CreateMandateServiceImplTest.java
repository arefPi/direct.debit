package tech.me.direct.debit.service.mandate.create;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.user.User;
import tech.me.direct.debit.persistence.user.UserRepository;
import tech.me.direct.debit.service.mandate.create.exception.UserNotFoundException;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateRequest;
import tech.me.direct.debit.service.mandate.create.reference.provider.MandateReferenceIdProvider;

import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateMandateServiceImplTest {
    private static final String USER_ID = "test-user-id";
    private static final String MANDATE_REFERENCE = "test-mandate-reference";

    @Mock
    private UserRepository userRepository;

    @Mock 
    private MandateReferenceIdProvider mandateReferenceProvider;

    @Mock
    private MandateRepository mandateRepository;

    @InjectMocks
    private CreateMandateServiceImpl createMandateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateMandate_WhenUserExists() {
        // Given
        var user = new User();
        user.setUserId(USER_ID);

        var request = new CreateMandateRequest(USER_ID);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));
        when(mandateReferenceProvider.generate()).thenReturn(MANDATE_REFERENCE);

        // When
        createMandateService.create(request);

        // Then
        verify(mandateRepository).save(mandateArgumentCaptor.capture());
        
        var savedMandate = mandateArgumentCaptor.getValue();
        assertThat(savedMandate.getUser()).isEqualTo(user);
        assertThat(savedMandate.getReferenceId()).isEqualTo(MANDATE_REFERENCE);
        assertThat(savedMandate.getStatus()).isEqualTo(MandateStatus.INITIAL);
    }

    @Test
    void shouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Given
        var request = new CreateMandateRequest(USER_ID);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> createMandateService.create(request))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("USER_NOT_FOUND_EXCEPTION");

        assertThat(((UserNotFoundException) catchThrowable(() -> createMandateService.create(request)))
            .getUserId()).isEqualTo(USER_ID);
    }

    @Captor
    private ArgumentCaptor<Mandate> mandateArgumentCaptor;
}
