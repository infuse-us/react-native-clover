package com.infuse.cloversdkreactnativebridge;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.clover.sdk.v1.Intents;
import com.clover.sdk.v3.payments.DataEntryLocation;
import com.clover.sdk.v3.payments.TransactionSettings;
import com.facebook.react.bridge.ReadableMap;

import java.io.Serializable;
import java.util.HashMap;

public class ExternalSecurePaymentTask extends AsyncTask<Void, Void, Intent> {

    public static final int SECURE_PAY_REQUEST = 0;
    private Activity currentActivity;
    private ReadableMap options;

    public ExternalSecurePaymentTask(Activity currentActivity, ReadableMap options) {
        this.currentActivity = currentActivity;
        this.options = options;
    }

    @Override
    protected Intent doInBackground(Void... voids) {
        int amount = options.getInt("amount");
        int cardEntryFlag = Intents.CARD_ENTRY_METHOD_ALL;
        String externalService = options.getString("externalService");

        if (options.hasKey("cardEntryFlag")) {
            cardEntryFlag = options.getInt("cardEntryFlag");
        }

        Intent payIntent = createExternalSecurePayIntent((long) amount, cardEntryFlag, externalService);

        if (options.hasKey("isRefund")) {
            boolean isRefund = options.getBoolean("isRefund");
            if (isRefund) {
                payIntent.putExtra(Intents.EXTRA_TRANSACTION_TYPE, Intents.TRANSACTION_TYPE_CREDIT);
            }
        }

        return payIntent;
    }

    @Override
    protected void onPostExecute(Intent payIntent) {
        super.onPostExecute(payIntent);
        currentActivity.startActivityForResult(payIntent, SECURE_PAY_REQUEST);
    }

    private Intent createExternalSecurePayIntent(long amount, int cardEntryFlag, String externalService) {
        Intent payIntent = new Intent("clover.intent.action.START_SECURE_PAYMENT");
        payIntent.putExtra(Intents.EXTRA_AMOUNT, amount);
        payIntent.putExtra(Intents.EXTRA_ORDER_ID, "2RQX89RBFSV8T");

        TransactionSettings transactionSettings = new TransactionSettings();
        transactionSettings.setSignatureEntryLocation(DataEntryLocation.NONE);
        transactionSettings.setDisableDuplicateCheck(true);
        transactionSettings.setDisableReceiptSelection(true);
        transactionSettings.setDisableRestartTransactionOnFailure(true);
        transactionSettings.setCardEntryMethods(cardEntryFlag);

        payIntent.putExtra(Intents.EXTRA_TRANSACTION_SETTINGS, transactionSettings);

        HashMap extraValues = new HashMap<String, String>();
        extraValues.put("PROCESS_PAYMENT_EXTERNAL_AUTH_SERVICE", externalService);
        payIntent.putExtra(Intents.EXTRA_APPLICATION_SPECIFIC_VALUES, (Serializable) extraValues);

        return payIntent;
    }
}
