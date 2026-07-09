package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentRequestTest {

    @Test
    public void testPaymentRequestSettersAndGetters() {
        PaymentRequest request = new PaymentRequest();

        double amount = 150.50;
        String cardNumber = "1234567812345678";
        String cardHolder = "dato okmela";
        String expiry = "12/29";
        String cvv = "123";

        request.setAmount(amount);
        request.setCardNumber(cardNumber);
        request.setCardHolder(cardHolder);
        request.setExpiry(expiry);
        request.setCvv(cvv);

        assertEquals(amount, request.getAmount());
        assertEquals(cardNumber, request.getCardNumber());
        assertEquals(cardHolder, request.getCardHolder());
        assertEquals(expiry, request.getExpiry());
        assertEquals(cvv, request.getCvv());
    }
}