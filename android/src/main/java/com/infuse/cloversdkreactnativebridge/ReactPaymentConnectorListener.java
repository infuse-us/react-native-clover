package com.infuse.cloversdkreactnativebridge;

import android.util.Log;

import com.clover.sdk.v3.connector.IPaymentConnectorListener;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.remotepay.AuthResponse;
import com.clover.sdk.v3.remotepay.CapturePreAuthResponse;
import com.clover.sdk.v3.remotepay.CloseoutResponse;
import com.clover.sdk.v3.remotepay.ConfirmPaymentRequest;
import com.clover.sdk.v3.remotepay.ManualRefundResponse;
import com.clover.sdk.v3.remotepay.PreAuthResponse;
import com.clover.sdk.v3.remotepay.ReadCardDataResponse;
import com.clover.sdk.v3.remotepay.RefundPaymentResponse;
import com.clover.sdk.v3.remotepay.RetrievePaymentResponse;
import com.clover.sdk.v3.remotepay.RetrievePendingPaymentsResponse;
import com.clover.sdk.v3.remotepay.SaleResponse;
import com.clover.sdk.v3.remotepay.TipAdded;
import com.clover.sdk.v3.remotepay.TipAdjustAuthResponse;
import com.clover.sdk.v3.remotepay.VaultCardResponse;
import com.clover.sdk.v3.remotepay.VerifySignatureRequest;
import com.clover.sdk.v3.remotepay.VoidPaymentRefundResponse;
import com.clover.sdk.v3.remotepay.VoidPaymentResponse;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

public class ReactPaymentConnectorListener implements IPaymentConnectorListener {

    private Promise mPromise;

    ReactPaymentConnectorListener(Promise promise) {
        mPromise = promise;
    }

    @Override
    public void onPreAuthResponse(PreAuthResponse response) {
        Log.d("ReactPaymentConnector", "onPreAuthResponse");
    }

    @Override
    public void onAuthResponse(AuthResponse response) {
        Log.d("ReactPaymentConnector", "onAuthResponse");
    }

    @Override
    public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {
        Log.d("ReactPaymentConnector", "onTipAdjustAuthResponse");
    }

    @Override
    public void onCapturePreAuthResponse(CapturePreAuthResponse response) {
        Log.d("ReactPaymentConnector", "onCapturePreAuthResponse");
    }

    @Override
    public void onVerifySignatureRequest(VerifySignatureRequest request) {
        Log.d("ReactPaymentConnector", "onVerifySignatureRequest");
    }

    @Override
    public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
        Log.d("ReactPaymentConnector", "onConfirmPaymentRequest");
    }

    @Override
    public void onSaleResponse(SaleResponse response) {
        Log.d("ReactPaymentConnector", "onSaleResponse");
        WritableMap map = Arguments.createMap();
        if (response.getSuccess()) {
            Payment payment = response.getPayment();
            map.putBoolean("success", response.getSuccess());
            map.putString("paymentId", payment.getId());
            map.putString("externalPaymentId", payment.getExternalPaymentId());
            map.putString("externalReferenceId", payment.getExternalReferenceId());
        } else {
            map.putBoolean("success", response.getSuccess());
            map.putString("reason", response.getReason());
            map.putString("message", response.getMessage());
        }
        mPromise.resolve(map);
    }

    @Override
    public void onManualRefundResponse(ManualRefundResponse response) {
        Log.d("ReactPaymentConnector", "onManualRefundResponse");
    }

    @Override
    public void onRefundPaymentResponse(RefundPaymentResponse response) {
        Log.d("ReactPaymentConnector", "onRefundPaymentResponse");
    }

    @Override
    public void onTipAdded(TipAdded tipAdded) {
        Log.d("ReactPaymentConnector", "onTipAdded");
    }

    @Override
    public void onVoidPaymentResponse(VoidPaymentResponse response) {
        Log.d("ReactPaymentConnector", "onVoidPaymentResponse");
    }

    @Override
    public void onVaultCardResponse(VaultCardResponse response) {
        Log.d("ReactPaymentConnector", "onVaultCardResponse");
    }

    @Override
    public void onRetrievePendingPaymentsResponse(RetrievePendingPaymentsResponse retrievePendingPaymentResponse) {
        Log.d("ReactPaymentConnector", "onRetrievePendingPaymentResponse");
    }

    @Override
    public void onReadCardDataResponse(ReadCardDataResponse response) {
        Log.d("ReactPaymentConnector", "onReadCardDataResponse");
    }

    @Override
    public void onCloseoutResponse(CloseoutResponse response) {
        Log.d("ReactPaymentConnector", "onCloseoutResponse");
    }

    @Override
    public void onRetrievePaymentResponse(RetrievePaymentResponse response) {
        Log.d("ReactPaymentConnector", "onRetrievePaymentResponse");
    }

    @Override
    public void onVoidPaymentRefundResponse(VoidPaymentRefundResponse response) {
        Log.d("ReactPaymentConnector", "onVoidPaymentRefundResponse");
    }

    @Override
    public void onDeviceDisconnected() {
        Log.d("ReactPaymentConnector", "onDeviceDisconnected");
    }

    @Override
    public void onDeviceConnected() {
        Log.d("ReactPaymentConnector", "onDeviceConnected");
    }
}
