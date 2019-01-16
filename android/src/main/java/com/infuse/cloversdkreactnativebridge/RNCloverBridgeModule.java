package com.infuse.cloversdkreactnativebridge;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CustomerMode;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ResultStatus;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

class RNCloverBridgeModule extends ReactContextBaseJavaModule implements ServiceConnector.OnServiceConnectedListener {

    private static final String TAG = "RNCloverBridge";
    private static final int SECURE_PAY_REQUEST = 0;
    private static final int REQUEST_PERMISSIONS = 1;
    private static final int REQUEST_ACCOUNT = 2;
    private ReactContext mContext;

    private Account account;
    private MerchantConnector merchantConnector;

    private Promise accountPromise;
    private Promise paymentPromise;

    public RNCloverBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        WritableMap cardEntryMethods = Arguments.createMap();
        cardEntryMethods.putInt("ICC_CONTACT", Intents.CARD_ENTRY_METHOD_ICC_CONTACT);
        cardEntryMethods.putInt("MAG_STRIPE", Intents.CARD_ENTRY_METHOD_MAG_STRIPE);
        cardEntryMethods.putInt("MANUAL", Intents.CARD_ENTRY_METHOD_MANUAL);
        cardEntryMethods.putInt("NFC_CONTACTLESS", Intents.CARD_ENTRY_METHOD_NFC_CONTACTLESS);
        cardEntryMethods.putInt("VAULTED_CARD", Intents.CARD_ENTRY_METHOD_VAULTED_CARD);

        constants.put("CARD_ENTRY_METHOD", cardEntryMethods);

        return constants;
    }

    @ReactMethod
    public void enableCustomerMode() { CustomerMode.enable(this.mContext); }

    @ReactMethod
    public void disableCustomerMode() {
        CustomerMode.disable(this.mContext, false);
    }

    @ReactMethod
    public void getMerchant(final Promise promise) {
        startAccountChooser();
        connect();
        if (merchantConnector != null) {
            merchantConnector.getMerchant(new MerchantConnector.MerchantCallback<Merchant>() {
                @Override
                public void onServiceSuccess(Merchant result, ResultStatus status) {
                    WritableMap map = Arguments.createMap();
                    map.putString("merchantId", result.getId());
                    promise.resolve(map);
                }

                @Override
                public void onServiceFailure(ResultStatus status) {
                    Log.d(TAG, "onServiceFailure");
                    Log.d(TAG, String.valueOf(status.getStatusCode()));
                    Log.d(TAG, status.getStatusMessage());
                    promise.resolve(null);
                }

                @Override
                public void onServiceConnectionFailure() {
                    Log.d(TAG, "onServiceConnectionFailure");
                    promise.resolve(null);
                }
            });
        } else {
            Log.d(TAG, "No merchantConnector");
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void getSSID(Promise promise) {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ssid = wifiManager.getConnectionInfo().getSSID();
        promise.resolve(ssid);
    }

    @ReactMethod
    public void getConnectionStrength(Promise promise) {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int rssi = wifiManager.getConnectionInfo().getRssi();
        promise.resolve(rssi);
    }

    @ReactMethod
    public void startExternalSecurePayment(ReadableMap options, Promise promise) {
        if (options.hasKey("amount") && options.hasKey("externalService")) {
            int amount = options.getInt("amount");
            String externalService = options.getString("externalService");

            Intent payIntent = createExternalSecurePayIntent((long) amount, externalService);

            if (options.hasKey("cardEntryFlag")) {
                int cardEntryFlag = options.getInt("cardEntryFlag");
                payIntent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, cardEntryFlag);
            }

            paymentPromise = promise;
            getCurrentActivity().startActivityForResult(payIntent, SECURE_PAY_REQUEST);
        } else {
            Log.e(TAG, "Missing amount or externalService");
        }
    }

    @ReactMethod
    @TargetApi(27)
    public void startAccountChooserIfNeeded(Promise promise) {
        startAccountChooser();
        if (account != null) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", true);
            promise.resolve(map);
        } else {
            accountPromise = promise;
            Intent accountIntent = AccountManager.newChooseAccountIntent(
                    null,
                    null,
                    new String[]{CloverAccount.CLOVER_ACCOUNT_TYPE},
                    null,
                    null,
                    null,
                    null);
            getCurrentActivity().startActivityForResult(accountIntent, REQUEST_ACCOUNT);
        }
    }

    @ReactMethod
    public void enableSound() {
        AudioManager audioManager;
        audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
    }

    @ReactMethod
    public void disableSound() {
        AudioManager audioManager;
        audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
    }

    private void startAccountChooser() {
        account = CloverAccount.getAccount(mContext);
    }

    private void connect() {
        disconnect();
        if (account != null) {
            merchantConnector = new MerchantConnector(mContext, account, this);
            merchantConnector.connect();
        }
    }

    private void disconnect() {
        if (merchantConnector != null) {
            merchantConnector.disconnect();
            merchantConnector = null;
        }
    }

    private Intent createExternalSecurePayIntent(long amount, String externalService) {
        Intent payIntent = new Intent("clover.intent.action.START_SECURE_PAYMENT");
        payIntent.putExtra(Intents.EXTRA_AMOUNT, amount);
        payIntent.putExtra(Intents.EXTRA_ORDER_ID, "2RQX89RBFSV8T");

        HashMap extraValues = new HashMap<String, String>();
        extraValues.put("PROCESS_PAYMENT_EXTERNAL_AUTH_SERVICE", externalService);
        payIntent.putExtra(Intents.EXTRA_PASS_THROUGH_VALUES, (Serializable) extraValues);

        return payIntent;
    }

    @Override
    public void onServiceConnected(ServiceConnector connector) {
        Log.i(TAG, "service connected: " + connector);
    }

    @Override
    public void onServiceDisconnected(ServiceConnector connector) {
        Log.i(TAG, "service disconnected: " + connector);
    }

    private final ActivityEventListener mActivityEventListener = new ActivityEventListener() {
        @Deprecated
        public void onActivityResult(int requestCode, int resultCode, Intent data) { }

        public void onActivityResult(Activity activity,  int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_ACCOUNT) {
                WritableMap map = Arguments.createMap();
                if (resultCode == RESULT_OK) {
                    map.putBoolean("success", true);
                    accountPromise.resolve(map);
                } else {
                    map.putBoolean("success", false);
                    accountPromise.resolve(map);
                }
            } else if (requestCode == SECURE_PAY_REQUEST) {
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", resultCode == RESULT_OK);
                paymentPromise.resolve(map);
            }
        }

        public void onNewIntent(Intent intent) { }
    };
}
