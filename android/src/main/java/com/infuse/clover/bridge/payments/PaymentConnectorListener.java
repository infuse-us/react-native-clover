package com.infuse.clover.bridge.payments;

import android.util.Log;

import com.clover.sdk.v3.connector.IPaymentConnectorListener;
import com.clover.sdk.v3.payments.Credit;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.Refund;
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
        ResultBuilder mapper = new ResultBuilder(response);
        if (response.getSuccess()) {
            Payment payment = response.getPayment();
            mapper.putMap("payment", Payments.mapPayment(payment));
            mBridgePaymentConnectorListener.onResult(mapper.build(), payment);
        } else {
            mBridgePaymentConnectorListener.onResult(mapper.build());
        }
    }

    @Override
    public void onManualRefundResponse(ManualRefundResponse response) {
        Log.d(TAG, "onManualRefundResponse");
        ResultBuilder mapper = new ResultBuilder(response);
        if (response.getSuccess()) {
            Credit credit = response.getCredit();
            mapper.putMap("credit", Payments.mapCredit(credit));
            mBridgePaymentConnectorListener.onResult(mapper.build(), credit);
        } else {
            mBridgePaymentConnectorListener.onResult(mapper.build());
        }
    }

    @Override
    public void onRefundPaymentResponse(RefundPaymentResponse response) {
        Log.d(TAG, "onRefundPaymentResponse");
        ResultBuilder mapper = new ResultBuilder(response);
        if (response.getSuccess()) {
            // response.orderId seems to be null
            // response.paymentId seems to be null
            Refund refund = response.getRefund();
            mapper.putMap("refund", Payments.mapRefund(refund));
            mBridgePaymentConnectorListener.onResult(mapper.build(), refund);
        } else {
            mBridgePaymentConnectorListener.onResult(mapper.build());
        }
    }

    @Override
    public void onTipAdded(TipAdded tipAdded) {
        Log.d(TAG, "onTipAdded");
    }

    @Override
    public void onVoidPaymentResponse(VoidPaymentResponse response) {
        Log.d(TAG, "onVoidPaymentResponse");
        ResultBuilder mapper = new ResultBuilder(response);
        if (response.getSuccess()) {
            String paymentId = response.getPaymentId();
            mapper.setPaymentId(paymentId);
            mBridgePaymentConnectorListener.onResult(mapper.build(), paymentId);
        } else {
            mBridgePaymentConnectorListener.onResult(mapper.build());
        }
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
        ResultBuilder mapper = new ResultBuilder(response);
        if (response.getSuccess()) {
            mapper.setPaymentId(response.getPaymentId());
            mapper.setRefundId(response.getRefundId());
        }
        mBridgePaymentConnectorListener.onResult(mapper.build());
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
