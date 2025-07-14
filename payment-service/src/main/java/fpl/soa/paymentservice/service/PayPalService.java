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
//        // Shipping address
//        ShippingAddress shipping = new ShippingAddress();
//        shipping.setRecipientName("Imad SAMADI");
//        shipping.setLine1("17 Rue Ibn Khaldoun, Appt 4");
//        shipping.setCity("Casablanca");
//        shipping.setState("Grand Casablanca");
//        shipping.setPostalCode("20250");
//        shipping.setCountryCode("MA");
//
//        // ItemList with shipping address only
//        ItemList itemList = new ItemList();
//        itemList.setShippingAddress(shipping);
//
//        // Transaction
//        Transaction transaction = new Transaction();
//        transaction.setAmount(amount);
//        transaction.setDescription("Secure payment for Order #" + orderId + " on " + platformName);
//        transaction.setCustom("OrderId: " + orderId);
//        transaction.setItemList(itemList);
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
//        // Experience profile for brand name
//        WebProfile profile = new WebProfile();
//
//        // FIXED: Ensure name is under 50 characters
//        String uniqueProfileName = "Shop-" + System.currentTimeMillis(); // e.g. "Shop-1720964660588"
//        profile.setName(uniqueProfileName);
//
//        Presentation presentation = new Presentation();
//        presentation.setBrandName(platformName);
//        profile.setPresentation(presentation);
//
//        // Create the profile and set it on the payment
//        String profileId = profile.create(apiContext).getId();
//        payment.setExperienceProfileId(profileId);
//
//        // Create payment
//        Payment created = payment.create(apiContext);
//
//        // Return approval URL
//        return created.getLinks().stream()
//                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
//                .map(Links::getHref)
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("No approval URL found"));
//    }


//    public String createPayment(Double total, String currency, String orderId, Double discount) throws PayPalRESTException {
//        String platformName = "MultiShop Store";
//        String returnUrl = "http://localhost:4200/payment-success?orderId=" + orderId;
//        String cancelUrl = "http://localhost:4200/payment-cancel?orderId=" + orderId;
//
//        // Calculate discounted total
//        double discountedTotal = total - (discount != null ? discount : 0.0);
//        if (discountedTotal < 0) discountedTotal = 0.0;
//
//        // Amount
//        Amount amount = new Amount();
//        amount.setCurrency(currency);
//        amount.setTotal(String.format(Locale.US, "%.2f", discountedTotal));
//
//        // Shipping address
//        ShippingAddress shipping = new ShippingAddress();
//        shipping.setRecipientName("Imad SAMADI");
//        shipping.setLine1("17 Rue Ibn Khaldoun, Appt 4");
//        shipping.setCity("Casablanca");
//        shipping.setState("Grand Casablanca");
//        shipping.setPostalCode("20250");
//        shipping.setCountryCode("MA");
//
//        // ItemList with optional discount item
//        ItemList itemList = new ItemList();
//        itemList.setShippingAddress(shipping);
//
//        if (discount != null && discount > 0) {
//            Item discountItem = new Item();
//            discountItem.setName("Discount");
//            discountItem.setCurrency(currency);
//            discountItem.setPrice(String.format(Locale.US, "-%.2f", discount));
//            discountItem.setQuantity("1");
//            itemList.setItems(List.of(discountItem));
//        }
//
//        // Transaction
//        Transaction transaction = new Transaction();
//        transaction.setAmount(amount);
//        transaction.setDescription("Secure payment for Order #" + orderId + " on " + platformName);
//        transaction.setCustom("OrderId: " + orderId);
//        transaction.setItemList(itemList);
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
//        // Experience profile
//        WebProfile profile = new WebProfile();
//        String uniqueProfileName = "Shop-" + System.currentTimeMillis(); // unique profile name under 50 chars
//        profile.setName(uniqueProfileName);
//
//        Presentation presentation = new Presentation();
//        presentation.setBrandName(platformName);
//        profile.setPresentation(presentation);
//
//        // Create the profile and assign to payment
//        String profileId = profile.create(apiContext).getId();
//        payment.setExperienceProfileId(profileId);
//
//        // Create payment
//        Payment created = payment.create(apiContext);
//
//        // Return approval URL
//        return created.getLinks().stream()
//                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
//                .map(Links::getHref)
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("No approval URL found"));
//    }


    public String createPayment(Double total, String currency, String orderId, Double discount) throws PayPalRESTException {
        String platformName = "MultiShop Store";
        String returnUrl = "http://localhost:4200/payment-success?orderId=" + orderId;
        String cancelUrl = "http://localhost:4200/payment-cancel?orderId=" + orderId;

        // Calculate discounted total
        double discountedTotal = total - (discount != null ? discount : 0.0);
        if (discountedTotal < 0) discountedTotal = 0.0;

        // Amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.US, "%.2f", discountedTotal));

        // Shipping address
        ShippingAddress shipping = new ShippingAddress();
        shipping.setRecipientName("Imad SAMADI");
        shipping.setLine1("17 Rue Ibn Khaldoun, Appt 4");
        shipping.setCity("Casablanca");
        shipping.setState("Grand Casablanca");
        shipping.setPostalCode("20250");
        shipping.setCountryCode("MA");

        // ItemList WITHOUT items (just use address, no negative-price items!)
        ItemList itemList = new ItemList();
        itemList.setShippingAddress(shipping);
        // Don't set itemList.setItems(...) if you're not sending valid positive-price items

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

        // Experience profile
        WebProfile profile = new WebProfile();
        String uniqueProfileName = "Shop-" + System.currentTimeMillis(); // unique profile name under 50 chars
        profile.setName(uniqueProfileName);

        Presentation presentation = new Presentation();
        presentation.setBrandName(platformName);
        profile.setPresentation(presentation);

        // Create the profile and assign to payment
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