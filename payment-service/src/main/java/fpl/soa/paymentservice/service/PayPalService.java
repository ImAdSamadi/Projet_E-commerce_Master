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
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PayPalService {

    private final APIContext apiContext;

//    public String createPayment(Double total, String currency, String orderId) throws PayPalRESTException {
//        String platformName = "MultiShop Store";
//        String returnUrl = "http://localhost:4200/payment-success?orderId=" + orderId;
//        String cancelUrl = "http://localhost:4200/payment-cancel?orderId=" + orderId;
//
//        // Amount
//        Amount amount = new Amount();
//        amount.setCurrency(currency);
//        amount.setTotal(String.format(Locale.US, "%.2f", total));
//
//        // Shipping address only (no items)
//        ShippingAddress shipping = new ShippingAddress();
//        shipping.setRecipientName("Imad SAMADI"); // You can replace this dynamically
//        shipping.setLine1("17 Rue Ibn Khaldoun, Appt 4");
//        shipping.setCity("Casablanca");
//        shipping.setState("Grand Casablanca");
//        shipping.setPostalCode("20250");
//        shipping.setCountryCode("MA"); // ISO 2-letter country code
//
//        // Empty item list with shipping address
//        ItemList itemList = new ItemList();
//        itemList.setShippingAddress(shipping);
//
//        // Transaction
//        Transaction transaction = new Transaction();
//        transaction.setAmount(amount);
//        transaction.setDescription("Secure payment for Order #" + orderId + " on " + platformName);
//        transaction.setCustom("OrderId: " + orderId);
//        transaction.setItemList(itemList); // attach only shipping address
//
//        // Payer
//        Payer payer = new Payer();
//        payer.setPaymentMethod("paypal");
//
//        // Payment
//        Payment payment = new Payment();
//        payment.setIntent("sale");
//        payment.setPayer(payer);
//        payment.setTransactions(List.of(transaction));
//
//        // Redirect URLs
//        RedirectUrls redirectUrls = new RedirectUrls();
//        redirectUrls.setReturnUrl(returnUrl);
//        redirectUrls.setCancelUrl(cancelUrl);
//        payment.setRedirectUrls(redirectUrls);
//
//        // Create Payment
//        Payment created = payment.create(apiContext);
//
//        // Extract approval URL
//        return created.getLinks().stream()
//                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
//                .map(Links::getHref)
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("No approval URL found"));
//    }

    public String createPayment(Double total, String currency, String orderId) throws PayPalRESTException {
        String platformName = "MultiShop Store";
        String returnUrl = "http://localhost:4200/payment-success?orderId=" + orderId;
        String cancelUrl = "http://localhost:4200/payment-cancel?orderId=" + orderId;

        // Amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.US, "%.2f", total));

        // Shipping address
        ShippingAddress shipping = new ShippingAddress();
        shipping.setRecipientName("Imad SAMADI");
        shipping.setLine1("17 Rue Ibn Khaldoun, Appt 4");
        shipping.setCity("Casablanca");
        shipping.setState("Grand Casablanca");
        shipping.setPostalCode("20250");
        shipping.setCountryCode("MA");

        // ItemList with shipping address only
        ItemList itemList = new ItemList();
        itemList.setShippingAddress(shipping);

        // Transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Secure payment for Order #" + orderId + " on " + platformName);
        transaction.setCustom("OrderId: " + orderId);
        transaction.setItemList(itemList);

        // Payer
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // Payment
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(List.of(transaction));

        // Redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(returnUrl);
        redirectUrls.setCancelUrl(cancelUrl);
        payment.setRedirectUrls(redirectUrls);

        // Experience profile for brand name
        WebProfile profile = new WebProfile();
        profile.setName("MultiShopProfile-" + UUID.randomUUID()); // must be unique
        Presentation presentation = new Presentation();
        presentation.setBrandName(platformName);
        profile.setPresentation(presentation);

        // Create the profile and set it on the payment
        String profileId = profile.create(apiContext).getId();
        payment.setExperienceProfileId(profileId);

        // Create payment
        Payment created = payment.create(apiContext);

        // Return approval URL
        return created.getLinks().stream()
                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
                .map(Links::getHref)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No approval URL found"));
    }


    // Capture payment after user approves
    public Payment capturePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);

        return payment.execute(apiContext, execution);
    }
}