package service;

import model.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @InjectMocks
    private PaymentService service;

    @BeforeEach
    void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPaymentFailures() {
        assertEquals("Payment details are required.", service.processPayment(null));

        PaymentRequest r = mock(PaymentRequest.class);

        when(r.getAmount()).thenReturn(0.0);
        assertEquals("Payment amount must be greater than zero.", service.processPayment(r));

        when(r.getAmount()).thenReturn(50.0);
        when(r.getCardNumber()).thenReturn("123");
        assertEquals("Card number must contain 16 digits.", service.processPayment(r));

        when(r.getCardNumber()).thenReturn("1234123412341234");
        when(r.getCardHolder()).thenReturn(" ");
        assertEquals("Cardholder name is required.", service.processPayment(r));

        when(r.getCardHolder()).thenReturn("John");
        when(r.getExpiry()).thenReturn("13/25");
        assertEquals("Expiry date must be in MM/YY format.", service.processPayment(r));

        when(r.getExpiry()).thenReturn("12/25");
        when(r.getCvv()).thenReturn("12");
        assertEquals("CVV must contain 3 digits.", service.processPayment(r));
    }

    @Test
    void testProcessPaymentSuccess() {
        PaymentRequest r = mock(PaymentRequest.class);
        when(r.getAmount()).thenReturn(100.0);
        when(r.getCardNumber()).thenReturn("1234-5678-9012-3456");
        when(r.getCardHolder()).thenReturn("dato okmela");
        when(r.getExpiry()).thenReturn("05/28");
        when(r.getCvv()).thenReturn("123");

        assertNull(service.processPayment(r));
    }

    @Test
    void testGeneratePaymentReference() {
        String ref = service.generatePaymentReference();
        assertNotNull(ref);
        assertTrue(ref.startsWith("PAY-"));
        assertEquals(12, ref.length());
    }
}