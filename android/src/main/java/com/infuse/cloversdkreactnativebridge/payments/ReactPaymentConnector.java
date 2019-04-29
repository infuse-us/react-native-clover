package com.infuse.cloversdkreactnativebridge.payments;

import android.accounts.Account;
import android.content.Context;

import com.clover.connector.sdk.v3.PaymentConnector;
import com.clover.sdk.v3.connector.ExternalIdUtils;
import com.clover.sdk.v3.remotepay.ManualRefundRequest;
import com.clover.sdk.v3.remotepay.RefundPaymentRequest;
import com.clover.sdk.v3.remotepay.SaleRequest;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

public class ReactPaymentConnector {
    private PaymentConnector paymentConnector;
    private Promise paymentPromise;

    public ReactPaymentConnector(Context context, Account account) {
        ReactPaymentListener reactPaymentListener = new ReactPaymentListener() {
            @Override
            public void onPaymentConnectorEvent(WritableMap map) {
                paymentPromise.resolve(map);
            }
        };
        ReactPaymentConnectorListener reactPaymentConnectorListener = new ReactPaymentConnectorListener(reactPaymentListener);
        paymentConnector = new PaymentConnector(context, account, reactPaymentConnectorListener);
    }

    public void sale(ReadableMap options, Promise promise) {
        paymentPromise = promise;
        if (!options.hasKey("amount")) {
            promise.reject("error", "Missing amount");
        } else {
            int amount = options.getInt("amount");
            String id = ExternalIdUtils.generateNewID();
            if (options.hasKey("paymentId")) {
                id = options.getString("paymentId");
            }

            SaleRequest saleRequest = new SaleRequest();
            saleRequest.setAmount((long) amount);
            saleRequest.setExternalId(id);

            paymentConnector.sale(saleRequest);
        }
    }

    public void refundPayment(ReadableMap options, Promise promise) {
        paymentPromise = promise;
        String paymentId = options.getString("paymentId");
        String orderId = options.getString("orderId");

        RefundPaymentRequest refundPaymentRequest = new RefundPaymentRequest();
        if (options.hasKey("amount")) {
            refundPaymentRequest.setAmount((long) options.getInt("amount"));
        } else {
            refundPaymentRequest.setFullRefund(true);
        }
        refundPaymentRequest.setOrderId(orderId);
        refundPaymentRequest.setPaymentId(paymentId);

        paymentConnector.refundPayment(refundPaymentRequest);
    }

    public void manualRefund(ReadableMap options, Promise promise) {
        paymentPromise = promise;
        String paymentId = options.getString("paymentId");
        int amount = options.getInt("amount");

        ManualRefundRequest manualRefundRequest = new ManualRefundRequest();
        manualRefundRequest.setAmount((long) amount);
        manualRefundRequest.setExternalId(paymentId);

        paymentConnector.manualRefund(manualRefundRequest);
    }
}
