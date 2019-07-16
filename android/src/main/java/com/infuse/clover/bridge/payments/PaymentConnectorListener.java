package com.infuse.clover.bridge.payments;

import android.util.Log;

import com.clover.sdk.v3.connector.IPaymentConnectorListener;
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
import com.facebook.react.bridge.WritableMap;

public class PaymentConnectorListener implements IPaymentConnectorListener {

    private final String TAG = "RNCloverBridge";
    private BridgePaymentConnectorListener mBridgePaymentConnectorListener;

    PaymentConnectorListener(BridgePaymentConnectorListener bridgePaymentConnectorListener) {
        mBridgePaymentConnectorListener = bridgePaymentConnectorListener;
    }

    @Override
    public void onPreAuthResponse(PreAuthResponse response) {
        Log.d(TAG, "onPreAuthResponse");
    }

    @Override
    public void onAuthResponse(AuthResponse response) {
        Log.d(TAG, "onAuthResponse");
    }

    @Override
    public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {
        Log.d(TAG, "onTipAdjustAuthResponse");
    }

    @Override
    public void onCapturePreAuthResponse(CapturePreAuthResponse response) {
        Log.d(TAG, "onCapturePreAuthResponse");
    }

    @Override
    public void onVerifySignatureRequest(VerifySignatureRequest request) {
        Log.d(TAG, "onVerifySignatureRequest");
    }

    @Override
    public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
        Log.d(TAG, "onConfirmPaymentRequest");
    }

    @Override
    public void onSaleResponse(SaleResponse response) {
        Log.d(TAG, "onSaleResponse");
        WritableMap map = Arguments.createMap();
        boolean success = response.getSuccess();
        map.putBoolean("success", success);
        if (success) {
            map.putMap("payment", Payments.mapPayment(response.getPayment()));
        } else {
            map.putString("reason", response.getReason());
            map.putString("message", response.getMessage());
        }
        mBridgePaymentConnectorListener.onPaymentConnectorEvent(map);
    }

    @Override
    public void onManualRefundResponse(ManualRefundResponse response) {
        Log.d(TAG, "onManualRefundResponse");
        WritableMap map = Arguments.createMap();
        boolean success = response.getSuccess();
        map.putBoolean("success", success);
        if (success) {
            map.putMap("credit", Payments.mapCredit(response.getCredit()));
        } else {
            map.putString("reason", response.getReason());
            map.putString("message", response.getMessage());
        }
        mBridgePaymentConnectorListener.onPaymentConnectorEvent(map);
    }

    @Override
    public void onRefundPaymentResponse(RefundPaymentResponse response) {
        Log.d(TAG, "onRefundPaymentResponse");
        WritableMap map = Arguments.createMap();
        boolean success = response.getSuccess();
        map.putBoolean("success", success);
        if (success) {
            // response.orderId seems to be null
            // response.paymentId seems to be null
            map.putMap("refund", Payments.mapRefund(response.getRefund()));
        } else {
            map.putString("reason", response.getReason());
            map.putString("message", response.getMessage());
        }
        mBridgePaymentConnectorListener.onPaymentConnectorEvent(map);
    }

    @Override
    public void onTipAdded(TipAdded tipAdded) {
        Log.d(TAG, "onTipAdded");
    }

    @Override
    public void onVoidPaymentResponse(VoidPaymentResponse response) {
        Log.d(TAG, "onVoidPaymentResponse");
        WritableMap map = Arguments.createMap();
        boolean success = response.getSuccess();
        map.putBoolean("success", success);
        if (success) {
            String paymentId = response.getPaymentId();
            map.putString("paymentId", paymentId);
        } else {
            map.putString("reason", response.getReason());
            map.putString("message", response.getMessage());
        }
        mBridgePaymentConnectorListener.onPaymentConnectorEvent(map);
    }

    @Override
    public void onVaultCardResponse(VaultCardResponse response) {
        Log.d(TAG, "onVaultCardResponse");
    }

    @Override
    public void onRetrievePendingPaymentsResponse(RetrievePendingPaymentsResponse retrievePendingPaymentResponse) {
        Log.d(TAG, "onRetrievePendingPaymentResponse");
    }

    @Override
    public void onReadCardDataResponse(ReadCardDataResponse response) {
        Log.d(TAG, "onReadCardDataResponse");
    }

    @Override
    public void onCloseoutResponse(CloseoutResponse response) {
        Log.d(TAG, "onCloseoutResponse");
    }

    @Override
    public void onRetrievePaymentResponse(RetrievePaymentResponse response) {
        Log.d(TAG, "onRetrievePaymentResponse");
    }

    @Override
    public void onVoidPaymentRefundResponse(VoidPaymentRefundResponse response) {
        Log.d(TAG, "onVoidPaymentRefundResponse");
        WritableMap map = Arguments.createMap();
        boolean success = response.getSuccess();
        map.putBoolean("success", success);
        if (success) {
            map.putString("paymentId", response.getPaymentId());
            map.putString("refundId", response.getRefundId());
        } else {
            map.putString("reason", response.getReason());
            map.putString("message", response.getMessage());
        }
        mBridgePaymentConnectorListener.onPaymentConnectorEvent(map);
    }

    @Override
    public void onDeviceDisconnected() {
        Log.d(TAG, "onDeviceDisconnected");
    }

    @Override
    public void onDeviceConnected() {
        Log.d(TAG, "onDeviceConnected");
    }
}
