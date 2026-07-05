package service;

import model.PaymentRequest;

import java.util.UUID;

public class PaymentService {

    public String processPayment(PaymentRequest request) {
        if (request == null) {
            return "Payment details are required.";
        }

        if (request.getAmount() <= 0) {
            return "Payment amount must be greater than zero.";
        }

        String cardNumber = normalizeDigits(request.getCardNumber());
        if (cardNumber.length() != 16) {
            return "Card number must contain 16 digits.";
        }

        if (request.getCardHolder() == null || request.getCardHolder().trim().length() < 2) {
            return "Cardholder name is required.";
        }

        if (!isValidExpiry(request.getExpiry())) {
            return "Expiry date must be in MM/YY format.";
        }

        String cvv = normalizeDigits(request.getCvv());
        if (cvv.length() != 3) {
            return "CVV must contain 3 digits.";
        }

        return null;
    }

    public String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private static String normalizeDigits(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }

    private static boolean isValidExpiry(String expiry) {
        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            return false;
        }

        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        return month >= 1 && month <= 12;
    }
}
