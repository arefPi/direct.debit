package tech.me.direct.debit.controller.mandate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import tech.me.direct.debit.controller.mandate.create.CreateMandateResponseMapper;
import tech.me.direct.debit.service.mandate.create.CreateMandateService;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateRequest;
import tech.me.direct.debit.controller.mandate.complete.CompleteMandateRequest;
import tech.me.direct.debit.controller.mandate.complete.CompleteMandateRequestMapper;
import tech.me.direct.debit.service.mandate.complete.CompleteMandateService;
import tech.me.direct.debit.service.mandate.redirect.RedirectToProviderService;
import tech.me.direct.debit.service.mandate.callback.MandateCallbackService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MandateControllerTest {
    @Mock
    private CreateMandateService createMandateService;

    @Mock
    private CreateMandateResponseMapper createMandateResponseMapper;

    @Mock
    private CompleteMandateService completeMandateService;

    @Mock
    private CompleteMandateRequestMapper completeMandateRequestMapper;

    @Mock
    private RedirectToProviderService redirectToProviderService;

    @Mock
    private MandateCallbackService mandateCallbackService;

    @Mock
    private Jwt userJwt;

    private MandateController mandateController;

    @BeforeEach
    void setUp() {
        mandateController = new MandateController(
            createMandateService,
            createMandateResponseMapper,
            completeMandateService,
            completeMandateRequestMapper,
            redirectToProviderService,
            mandateCallbackService
        );
    }

    @Test
    void createMandate_Success() {
        // Given
        String userId = "test-user-id";
        when(userJwt.getSubject()).thenReturn(userId);

        var serviceResponse = new tech.me.direct.debit.service.mandate.create.model.CreateMandateResponse("mandate-id");
        when(createMandateService.create(any(CreateMandateRequest.class))).thenReturn(serviceResponse);

        var mappedResponse = new tech.me.direct.debit.controller.mandate.create.CreateMandateResponse("mandate-id");
        when(createMandateResponseMapper.map(serviceResponse)).thenReturn(mappedResponse);

        // When
        var response = mandateController.createMandate(userJwt);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mandate-id", response.getBody().referenceId());

        verify(userJwt).getSubject();
        verify(createMandateService).create(any(CreateMandateRequest.class));
        verify(createMandateResponseMapper).map(serviceResponse);
    }

    @Test
    void completeMandate_Success() {
        // Given
        var requestDto = new CompleteMandateRequest(
            "mandate-ref-123",
            1,
            1000.0f,
            10,
            100.0f
        );
        var serviceRequest = new tech.me.direct.debit.service.mandate.complete.CompleteMandateRequest(
            "mandate-ref-123",
            1,
            1000.0f,
            10,
            100.0f
        );
        when(completeMandateRequestMapper.map(requestDto)).thenReturn(serviceRequest);

        // When
        var response = mandateController.completeMandate(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(completeMandateService).completeMandate(serviceRequest);
        verify(completeMandateRequestMapper).map(requestDto);
    }
}