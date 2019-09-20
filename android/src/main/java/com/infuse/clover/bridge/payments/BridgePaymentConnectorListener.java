package com.infuse.clover.bridge.payments;

import com.clover.sdk.v3.payments.Credit;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.Refund;
import com.facebook.react.bridge.WritableMap;

interface BridgePaymentConnectorListener {
    void onResult(WritableMap result);
    void onResult(WritableMap result, Payment payment);
    void onResult(WritableMap result, Credit credit);
    void onResult(WritableMap result, Refund refund);
    void onResult(WritableMap result, String paymentId);
}

