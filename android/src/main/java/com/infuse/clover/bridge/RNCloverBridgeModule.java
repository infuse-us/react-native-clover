package com.infuse.clover.bridge;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.util.CustomerMode;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.clover.sdk.v3.order.VoidReason;
import com.clover.sdk.v3.payments.DataEntryLocation;
import com.clover.sdk.v3.payments.TipMode;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.infuse.clover.bridge.payments.BridgePaymentConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

class RNCloverBridgeModule extends ReactContextBaseJavaModule implements ServiceConnector.OnServiceConnectedListener {

    static final String TAG = "RNCloverBridge";
    private ReactApplicationContext mContext;

    private static final int CHOOSE_ACCOUNT_REQUEST = 41920;

    private Account account;
    private MerchantConnector merchantConnector;
    private BridgePaymentConnector bridgePaymentConnector;

    private Promise accountPromise;

    RNCloverBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        reactContext.addActivityEventListener(activityEventListener);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        // Expose card entry methods
        WritableMap cardEntryMethods = Arguments.createMap();
        cardEntryMethods.putInt("ICC_CONTACT", Intents.CARD_ENTRY_METHOD_ICC_CONTACT);
        cardEntryMethods.putInt("MAG_STRIPE", Intents.CARD_ENTRY_METHOD_MAG_STRIPE);
        cardEntryMethods.putInt("MANUAL", Intents.CARD_ENTRY_METHOD_MANUAL);
        cardEntryMethods.putInt("NFC_CONTACTLESS", Intents.CARD_ENTRY_METHOD_NFC_CONTACTLESS);
        cardEntryMethods.putInt("VAULTED_CARD", Intents.CARD_ENTRY_METHOD_VAULTED_CARD);
        cardEntryMethods.putInt("ALL", Intents.CARD_ENTRY_METHOD_ALL);
        constants.put("CARD_ENTRY_METHOD", cardEntryMethods);

        // Expose DataEntryLocation Enum
        // https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/payments/DataEntryLocation.html
        WritableMap dataEntryLocations = Arguments.createMap();
        for (DataEntryLocation dataEntryLocation : DataEntryLocation.values()) {
            dataEntryLocations.putString(dataEntryLocation.name(), dataEntryLocation.name());
        }
        constants.put("DATA_ENTRY_LOCATION", dataEntryLocations);

        // Expose VoidReason Enum
        // https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/order/VoidReason.html
        WritableMap voidReasons = Arguments.createMap();
        for (VoidReason voidReason : VoidReason.values()) {
            voidReasons.putString(voidReason.name(), voidReason.name());
        }
        constants.put("VOID_REASON", voidReasons);

        // Expose TipMode Enum
        // https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/payments/TipMode.html
        WritableMap tipModes = Arguments.createMap();
        for (TipMode tipMode : TipMode.values()) {
            tipModes.putString(tipMode.name(), tipMode.name());
        }
        constants.put("TIP_MODE", tipModes);

        // Expose misc constants
        constants.put("isFlex", isFlex());
        constants.put("isMini", isMini());
        constants.put("getSpaVersion", getSpaVersion());

        return constants;
    }

    @ReactMethod
    public void print(final String imagePath, final Promise promise) {
        PrinterWrapper printerWrapper = new PrinterWrapper(promise);
        Activity currentActivity = getCurrentActivity();
        printerWrapper.print(currentActivity, account, imagePath);
    }

    @ReactMethod
    public void enableCustomerMode() { CustomerMode.enable(getCurrentActivity()); }

    @ReactMethod
    public void disableCustomerMode() {
        CustomerMode.disable(getCurrentActivity(), false);
    }

    @ReactMethod
    public void getMerchant(final Promise promise) {
        startAccountChooser();
        connect();
        if (merchantConnector != null) {
            merchantConnector.getMerchant(new MerchantCallbackTask(promise));
        } else {
            promise.reject("error", "failure to initialize merchantConnector");
        }
    }

    @ReactMethod
    public void authenticate(final boolean forceValidateToken, final int timeout, final Promise promise) {
        startAccountChooser();
        connect();
        Thread authThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CloverAuth.AuthResult result = CloverAuth.authenticate(
                            getCurrentActivity(),
                            account,
                            forceValidateToken,
                            (long) timeout,
                            TimeUnit.MILLISECONDS
                    );
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", result.errorMessage == null);
                    map.putString("authToken", result.authToken);
                    map.putString("message", result.errorMessage);
                    promise.resolve(map);
                } catch (OperationCanceledException | AuthenticatorException | IOException e) {
                    Log.e(TAG, "authentication_error", e);
                    promise.reject("authentication_error", e.getMessage());
                }
            }
        });
        authThread.start();
    }

    private String getSpaVersion() {
        try {
            PackageManager pManager = mContext.getPackageManager();
            PackageInfo pInfo = pManager.getPackageInfo("com.clover.payment.executor.secure", 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private boolean isFlex() {
        return getWidthInDP() == 360;
    }

    private boolean isMini() {
        return getWidthInDP() >= 960;
    }

    private int getWidthInDP() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return Math.round(displayMetrics.widthPixels / displayMetrics.density);
    }

    @ReactMethod
    public void initializePaymentConnector(String raid) {
        startAccountChooser();
        bridgePaymentConnector = new BridgePaymentConnector(mContext, account, raid);
    }

    @ReactMethod
    public void sale(ReadableMap options, Promise promise) {
        if (bridgePaymentConnector != null) {
            bridgePaymentConnector.sale(options, promise);
        } else {
            paymentConnectorReject(promise);
        }
    }

    @ReactMethod
    public void manualRefund(ReadableMap options, Promise promise) {
        if (bridgePaymentConnector != null) {
            bridgePaymentConnector.manualRefund(options, promise);
        } else {
            paymentConnectorReject(promise);
        }
    }

    @ReactMethod
    public void refundPayment(ReadableMap options, Promise promise) {
        if (bridgePaymentConnector != null) {
            bridgePaymentConnector.refundPayment(options, promise);
        } else {
            paymentConnectorReject(promise);
        }
    }

    @ReactMethod
    public void voidPayment(ReadableMap options, Promise promise) {
        if (bridgePaymentConnector != null) {
            bridgePaymentConnector.voidPayment(options, promise);
        } else {
            paymentConnectorReject(promise);
        }
    }

    @ReactMethod
    public void voidPaymentRefund(ReadableMap options, Promise promise) {
        if (bridgePaymentConnector != null) {
            bridgePaymentConnector.voidPaymentRefund(options, promise);
        } else {
            paymentConnectorReject(promise);
        }
    }

    @ReactMethod
    public void cancelSPA() {
        Intent intent = new Intent("com.clover.remote.terminal.securepay.action.V1_BREAK");
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.sendBroadcast(intent);
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
            getCurrentActivity().startActivityForResult(accountIntent, CHOOSE_ACCOUNT_REQUEST);
        }
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

    private void paymentConnectorReject(Promise promise) {
        promise.reject("error", "PaymentConnector not initialized.");
    }

    @Override
    public void onServiceConnected(ServiceConnector connector) {
        Log.i(TAG, "service connected: " + connector);
    }

    @Override
    public void onServiceDisconnected(ServiceConnector connector) {
        Log.i(TAG, "service disconnected: " + connector);
    }

    private final ActivityEventListener activityEventListener = new ActivityEventListener() {
        @Deprecated
        public void onActivityResult(int requestCode, int resultCode, Intent data) { }

        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == CHOOSE_ACCOUNT_REQUEST) {
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", resultCode == RESULT_OK);
                accountPromise.resolve(map);
            }
        }

        public void onNewIntent(Intent intent) { }
    };
}
