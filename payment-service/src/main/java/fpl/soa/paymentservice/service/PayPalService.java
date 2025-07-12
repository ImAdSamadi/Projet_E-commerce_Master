package fpl.soa.paymentservice.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import fpl.soa.common.events.PaymentFailedEvent;
import fpl.soa.common.events.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PayPalService {

    private final APIContext apiContext;

    public String createPayment(Double total, String currency, String orderId) throws PayPalRESTException {
        String platformName = "MultiShop Store";
        String returnUrl = "http://localhost:4200/payment-success?orderId=" + orderId;
        String cancelUrl = "http://localhost:4200/payment-cancel?orderId=" + orderId;

        // Amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.US, "%.2f", total));

        // Transaction with branding
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Secure payment for Order #" + orderId + " on " + platformName);
        transaction.setCustom("OrderId: " + orderId); // optional field (for traceability)

        // Payer
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // Payment
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(List.of(transaction));

        // Return/cancel URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(returnUrl);
        redirectUrls.setCancelUrl(cancelUrl);
        payment.setRedirectUrls(redirectUrls);

        // Execute
        Payment created = payment.create(apiContext);

        // Return approval link
        return created.getLinks().stream()
                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
                .map(Links::getHref)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No approval URL found"));
    }


    // Capture a PayPal payment after user approval
    public Payment capturePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);

        return payment.execute(apiContext, execution);
    }

}
