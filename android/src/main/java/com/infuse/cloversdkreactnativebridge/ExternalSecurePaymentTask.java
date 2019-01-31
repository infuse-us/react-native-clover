package com.infuse.cloversdkreactnativebridge;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.clover.sdk.v1.Intents;
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
        String externalService = options.getString("externalService");

        Intent payIntent = createExternalSecurePayIntent((long) amount, externalService);

        if (options.hasKey("cardEntryFlag")) {
            int cardEntryFlag = options.getInt("cardEntryFlag");
            payIntent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, cardEntryFlag);
        }

        return payIntent;
    }

    @Override
    protected void onPostExecute(Intent payIntent) {
        super.onPostExecute(payIntent);
        currentActivity.startActivityForResult(payIntent, SECURE_PAY_REQUEST);
    }

    private Intent createExternalSecurePayIntent(long amount, String externalService) {
        Intent payIntent = new Intent("clover.intent.action.START_SECURE_PAYMENT");
        payIntent.putExtra(Intents.EXTRA_AMOUNT, amount);
        payIntent.putExtra(Intents.EXTRA_ORDER_ID, "2RQX89RBFSV8T");

        HashMap extraValues = new HashMap<String, String>();
        extraValues.put("PROCESS_PAYMENT_EXTERNAL_AUTH_SERVICE", externalService);
        payIntent.putExtra(Intents.EXTRA_APPLICATION_SPECIFIC_VALUES, (Serializable) extraValues);

        return payIntent;
    }
}
