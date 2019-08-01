package com.infuse.clover.bridge.payments;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.clover.connector.sdk.v3.PaymentConnector;
import com.clover.sdk.v3.connector.ExternalIdUtils;
import com.clover.sdk.v3.merchant.TipSuggestion;
import com.clover.sdk.v3.payments.DataEntryLocation;
import com.clover.sdk.v3.payments.TipMode;
import com.clover.sdk.v3.remotepay.ManualRefundRequest;
import com.clover.sdk.v3.remotepay.RefundPaymentRequest;
import com.clover.sdk.v3.remotepay.SaleRequest;
import com.clover.sdk.v3.remotepay.VoidPaymentRefundRequest;
import com.clover.sdk.v3.remotepay.VoidPaymentRequest;
import com.facebook.react.bridge.NoSuchKeyException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.UnexpectedNativeTypeException;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BridgePaymentConnector {
    private PaymentConnector paymentConnector;
    private Promise paymentPromise;
    private ExecutorService executor;

    public BridgePaymentConnector(Context context, Account account, String raid) {
        BridgePaymentConnectorListener bridgePaymentConnectorListener = new BridgePaymentConnectorListener() {
            @Override
            public void onResult(WritableMap result) {
                paymentPromise.resolve(result);
            }
        };
        PaymentConnectorListener paymentConnectorListener = new PaymentConnectorListener(bridgePaymentConnectorListener);
        paymentConnector = new PaymentConnector(context, account, paymentConnectorListener, raid);
        executor = Executors.newSingleThreadExecutor();
    }

    public void sale(final ReadableMap options, Promise promise) {
        paymentPromise = promise;

        startConnector(new Runnable() {
            @Override
            public void run() {
                SaleRequest saleRequest = new SaleRequest();

                // Set required transaction settings
                saleRequest.setAmount((long) options.getInt(Payments.AMOUNT));
                saleRequest.setExternalId(getExternalPaymentId(options));

                // Set optional transaction settings
                if (options.hasKey(Payments.CARD_ENTRY_METHODS)) {
                    saleRequest.setCardEntryMethods(options.getInt(Payments.CARD_ENTRY_METHODS));
                }
                if (options.hasKey(Payments.DISABLE_RESTART_TRANSACTION_ON_FAIL)) {
                    saleRequest.setDisableRestartTransactionOnFail(
                            options.getBoolean(Payments.DISABLE_RESTART_TRANSACTION_ON_FAIL)
                    );
                }
                if (options.hasKey(Payments.DISABLE_DUPLICATE_CHECKING)) {
                    saleRequest.setDisableDuplicateChecking(options.getBoolean(Payments.DISABLE_DUPLICATE_CHECKING));
                }
                if (options.hasKey(Payments.DISABLE_PRINTING)) {
                    saleRequest.setDisablePrinting(options.getBoolean(Payments.DISABLE_PRINTING));
                }
                if (options.hasKey(Payments.DISABLE_RECEIPT_SELECTION)) {
                    saleRequest.setDisableReceiptSelection(options.getBoolean(Payments.DISABLE_RECEIPT_SELECTION));
                }
                if (options.hasKey(Payments.SIGNATURE_ENTRY_LOCATION)) {
                    DataEntryLocation location = DataEntryLocation.valueOf(options.getString(Payments.SIGNATURE_ENTRY_LOCATION));
                    saleRequest.setSignatureEntryLocation(location);
                }
                if (options.hasKey(Payments.SIGNATURE_THRESHOLD)) {
                    saleRequest.setSignatureThreshold((long) options.getInt(Payments.SIGNATURE_THRESHOLD));
                }
                if (options.hasKey(Payments.AUTO_ACCEPT_SIGNATURE)) {
                    saleRequest.setAutoAcceptSignature(options.getBoolean(Payments.AUTO_ACCEPT_SIGNATURE));
                }
                if (options.hasKey(Payments.TIP_AMOUNT)) {
                    saleRequest.setTipAmount((long) options.getInt(Payments.TIP_AMOUNT));
                }
                if (options.hasKey(Payments.TIPPABLE_AMOUNT)
                        && !(options.getType(Payments.TIPPABLE_AMOUNT) == ReadableType.Null)) {
                    saleRequest.setTippableAmount((long) options.getInt(Payments.TIPPABLE_AMOUNT));
                }
                if (options.hasKey(Payments.TIP_MODE)) {
                    TipMode tipMode = TipMode.valueOf(options.getString(Payments.TIP_MODE));
                    saleRequest.setTipMode(tipMode);
                }
                if (options.hasKey(Payments.TIP_SUGGESTIONS)) {
                    ReadableArray tipSuggestions = options.getArray(Payments.TIP_SUGGESTIONS);
                    saleRequest.setTipSuggestions(buildTipSuggestions(tipSuggestions));
                }

                paymentConnector.sale(saleRequest);
            }
        }, promise);
    }

    public void refundPayment(final ReadableMap options, Promise promise) {
        paymentPromise = promise;

        startConnector(new Runnable() {
            @Override
            public void run() {
                RefundPaymentRequest refundPaymentRequest = new RefundPaymentRequest();

                // Set required transaction settings
                refundPaymentRequest.setOrderId(options.getString(Payments.ORDER_ID));
                refundPaymentRequest.setPaymentId(options.getString(Payments.PAYMENT_ID));

                // Set optional transaction settings
                if (options.hasKey(Payments.AMOUNT)) {
                    refundPaymentRequest.setAmount((long) options.getInt(Payments.AMOUNT));
                }
                if (options.hasKey(Payments.SET_FULL_REFUND)) {
                    refundPaymentRequest.setFullRefund(options.getBoolean(Payments.SET_FULL_REFUND));
                }
                // disablePrinting - permanent default true
                // disableReceiptSelection - permanent default true

                paymentConnector.refundPayment(refundPaymentRequest);
            }
        }, promise);
    }

    public void manualRefund(final ReadableMap options, Promise promise) {
        paymentPromise = promise;

        startConnector(new Runnable() {
            @Override
            public void run() {
                ManualRefundRequest manualRefundRequest = new ManualRefundRequest();

                // Set required transaction settings
                manualRefundRequest.setAmount((long) options.getInt(Payments.AMOUNT));
                manualRefundRequest.setExternalId(getExternalPaymentId(options));

                // Set optional transaction settings
                if (options.hasKey(Payments.CARD_ENTRY_METHODS)) {
                    manualRefundRequest.setCardEntryMethods(options.getInt(Payments.CARD_ENTRY_METHODS));
                }
                if (options.hasKey(Payments.DISABLE_RESTART_TRANSACTION_ON_FAIL)) {
                    manualRefundRequest.setDisableRestartTransactionOnFail(
                            options.getBoolean(Payments.DISABLE_RESTART_TRANSACTION_ON_FAIL)
                    );
                }
                // disablePrinting - permanent default true
                // disableReceiptSelection - permanent default true
                // disableDuplicateChecking - permanent default false

                paymentConnector.manualRefund(manualRefundRequest);
            }
        }, promise);
    }

    public void voidPayment(final ReadableMap options, Promise promise) {
        paymentPromise = promise;

        startConnector(new Runnable() {
            @Override
            public void run() {
                VoidPaymentRequest voidPaymentRequest = new VoidPaymentRequest();

                // Set required transaction settings
                voidPaymentRequest.setOrderId(options.getString(Payments.ORDER_ID));
                voidPaymentRequest.setPaymentId(options.getString(Payments.PAYMENT_ID));
                voidPaymentRequest.setVoidReason(options.getString(Payments.VOID_REASON));

                paymentConnector.voidPayment(voidPaymentRequest);
            }
        }, promise);
    }

    public void voidPaymentRefund(final ReadableMap options, Promise promise) {
        paymentPromise = promise;

        startConnector(new Runnable() {
            @Override
            public void run() {
                VoidPaymentRefundRequest voidPaymentRefundRequest = new VoidPaymentRefundRequest();

                // Set required transaction settings
                voidPaymentRefundRequest.setOrderId(options.getString(Payments.ORDER_ID));
                voidPaymentRefundRequest.setRefundId(options.getString(Payments.REFUND_ID));

                paymentConnector.voidPaymentRefund(voidPaymentRefundRequest);
            }
        }, promise);
    }

    private String getExternalPaymentId(ReadableMap options) {
        if (options.hasKey(Payments.EXTERNAL_PAYMENT_ID)) {
            return options.getString(Payments.EXTERNAL_PAYMENT_ID);
        }
        if (options.hasKey(Payments.GENERATE_EXTERNAL_PAYMENT_ID)
                && options.getBoolean(Payments.GENERATE_EXTERNAL_PAYMENT_ID)) {
            return ExternalIdUtils.generateNewID();
        }
        throw new NoSuchKeyException("Missing required externalPaymentId or generateExternalPaymentId flag");
    }

    private List<TipSuggestion> buildTipSuggestions(ReadableArray tips) {
        final String NAME_PARAMETER = "name";
        final String PERCENTAGE_PARAMETER = "percentage";
        List<TipSuggestion> tipSuggestions = new ArrayList<>();
        for (int i = 0; i < tips.size(); i++) {
            ReadableMap tip = tips.getMap(i);
            try {
                TipSuggestion tipSuggestion = new TipSuggestion();
                tipSuggestion.setName(tip.getString(NAME_PARAMETER));
                tipSuggestion.setPercentage((long) tip.getInt(PERCENTAGE_PARAMETER));
                tipSuggestions.add(tipSuggestion);
            } catch(NoSuchKeyException | UnexpectedNativeTypeException e) {
                Log.e("ReactNativeClover", "Skipping invalid TipSuggestion at index: " + i, e);
            }
        }
        return tipSuggestions;
    }

    private void startConnector(Runnable runner, Promise promise) {
        try {
            executor.submit(runner).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("ReactNativeClover", "", e);
            promise.reject(e.getCause().getClass().getSimpleName(), e.getMessage());

        }
    }
}
