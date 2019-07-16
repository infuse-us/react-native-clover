package com.infuse.clover.bridge.payments;

import com.clover.sdk.v3.payments.Credit;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.Refund;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

public class Payments {
    // Bridge constants of Clover per-transaction settings.
    static final String CARD_ENTRY_METHODS = "cardEntryMethods";
    static final String DISABLE_DUPLICATE_CHECKING = "disableDuplicateChecking";
    static final String DISABLE_RESTART_TRANSACTION_ON_FAIL = "disableRestartTransactionOnFail";
    static final String DISABLE_PRINTING = "disablePrinting";
    static final String DISABLE_RECEIPT_SELECTION = "disableReceiptSelection";
    static final String SIGNATURE_THRESHOLD = "signatureThreshold";
    static final String SIGNATURE_ENTRY_LOCATION = "signatureEntryLocation";

    static WritableMap mapPayment(Payment payment) {
        WritableMap map = Arguments.createMap();

        map.putString("id", payment.getId());
        map.putString("externalPaymentId", payment.getExternalPaymentId());
        map.putInt("amount", payment.getAmount().intValue());
        map.putString("createdTime", payment.getCreatedTime().toString());

        map.putBoolean("offline", payment.getOffline());
        map.putInt("tipAmount", payment.getTipAmount().intValue());
        // clientCreatedTime seems to be null
        // modifiedTime seems to be null

        // Add in order reference id
        WritableMap orderMap = Arguments.createMap();
        orderMap.putString("id", payment.getOrder().getId());
        map.putMap("order", orderMap);

        return map;
    }

    static WritableMap mapRefund(Refund refund) {
        WritableMap map = Arguments.createMap();

        map.putString("id", refund.getId());
        map.putInt("amount", refund.getAmount().intValue());
        map.putString("createdTime", refund.getCreatedTime().toString());

        // Add in payment reference id
        WritableMap paymentMap = Arguments.createMap();
        paymentMap.putString("id", refund.getPayment().getId());
        map.putMap("payment", paymentMap);

        return map;
    }

    static WritableMap mapCredit(Credit credit) {
        WritableMap map = Arguments.createMap();

        map.putString("id", credit.getId());
        map.putInt("amount", credit.getAmount().intValue());
        map.putString("createdTime", credit.getCreatedTime().toString());

        return map;
    }

    private Payments() {
        throw new AssertionError();
    }
}
