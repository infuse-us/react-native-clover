package com.infuse.clover.bridge;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.Optional;
import android.view.WindowManager;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.util.CustomerMode;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.printer.job.StaticPaymentPrintJob;
import com.clover.sdk.v1.printer.job.StaticReceiptPrintJob;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;
import com.clover.sdk.v3.order.OrderType;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.scanner.BarcodeResult;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.NoSuchKeyException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UnexpectedNativeTypeException;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.infuse.clover.bridge.payments.BridgePaymentConnector;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

class RNCloverBridgeModule extends ReactContextBaseJavaModule {

    static final String TAG = "RNCloverBridge";
    private ReactApplicationContext mContext;

    private static final int CHOOSE_ACCOUNT_REQUEST = 41920;

    private BridgePaymentConnector bridgePaymentConnector;

    private Promise accountPromise;

    RNCloverBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        reactContext.addActivityEventListener(activityEventListener);
        bridgePaymentConnector = new BridgePaymentConnector();
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Map<String, Object> getConstants() {
        return new Constants.Builder()
                .put("isFlex", isFlex())
                .put("isMini", isMini())
                .put("getSpaVersion", getSpaVersion())
                .build();
    }

    @ReactMethod
    public void print(final String imagePath, final Promise promise) {
        PrinterWrapper printerWrapper = new PrinterWrapper(promise);
        Activity currentActivity = getCurrentActivity();
        printerWrapper.print(currentActivity, getAccount(), imagePath);
    }

    @ReactMethod
    public void enableCustomerMode() { CustomerMode.enable(getCurrentActivity()); }

    @ReactMethod
    public void disableCustomerMode(boolean requirePasscode) {
        CustomerMode.disable(getCurrentActivity(), requirePasscode);
    }

    @ReactMethod
    public void getMerchant(final Promise promise) {
        new BridgeServiceConnector().getMerchantConnector(mContext).getMerchant(new MerchantCallbackTask(promise));
    }

    @ReactMethod
    public void authenticate(final boolean forceValidateToken, final int timeout, final Promise promise) {
        Thread authThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CloverAuth.AuthResult result = CloverAuth.authenticate(
                            getCurrentActivity(),
                            forceValidateToken,
                            (long) timeout,
                            TimeUnit.MILLISECONDS
                    );
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", result.errorMessage == null);
                    map.putString("authToken", result.authToken);
                    map.putString("message", result.errorMessage);
                    promise.resolve(map);
                } catch (Exception e) {
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
    public void registerScanner() {
        mContext.addLifecycleEventListener(listener);
    }

    @ReactMethod
    public void unregisterScanner() {
        unregisterReceiver();
        mContext.removeLifecycleEventListener(listener);
    }

    @ReactMethod
    public void initializePaymentConnector(String raid) {
        bridgePaymentConnector.initialize(mContext, raid);
    }

    @ReactMethod
    public void sale(ReadableMap options, Promise promise) {
        bridgePaymentConnector.sale(options, promise);
    }

    @ReactMethod
    public void manualRefund(ReadableMap options, Promise promise) {
        bridgePaymentConnector.manualRefund(options, promise);
    }

    @ReactMethod
    public void refundPayment(ReadableMap options, Promise promise) {
        bridgePaymentConnector.refundPayment(options, promise);
    }

    @ReactMethod
    public void voidPayment(ReadableMap options, Promise promise) {
        bridgePaymentConnector.voidPayment(options, promise);
    }

    @ReactMethod
    public void voidPaymentRefund(ReadableMap options, Promise promise) {
        bridgePaymentConnector.voidPaymentRefund(options, promise);
    }

    @ReactMethod
    public void getOrder(final String orderId, final Promise promise) {
        try {
            OrderConnector orderConnector = new BridgeServiceConnector().getOrderConnector(mContext);
            Order order = orderConnector.getOrder(orderId);

            WritableMap responseMap = Arguments.createMap();
            WritableMap orderMap = Arguments.createMap();
            WritableMap orderTypeMap = Arguments.createMap();
            OrderType orderType = order.getOrderType();

            orderMap.putString("id", order.getId());
            orderMap.putString("currency", order.getCurrency());
            orderMap.putDouble("total", order.getTotal());
            orderMap.putString("state", order.getState());
            orderMap.putBoolean("testMode", order.getTestMode());
            if (orderType != null) {
                orderTypeMap.putString("id", orderType.getId());
                orderTypeMap.putString("label", orderType.getLabel());
                orderMap.putMap("type", orderTypeMap);
            }

            responseMap.putBoolean("success", true);
            responseMap.putMap("order", orderMap);
            promise.resolve(responseMap);
        } catch (RemoteException | ClientException | ServiceException | BindingException e) {
            Log.e(TAG, "", e);
            promise.reject("order_error", e.getMessage());
        } catch (NoSuchKeyException | UnexpectedNativeTypeException e) {
            Log.e(TAG, "RN", e);
            promise.reject("order_error", e.getMessage());
        }
    }

    @ReactMethod
    public void printPayment(ReadableMap options) {
        try {
            OrderConnector orderConnector = new BridgeServiceConnector().getOrderConnector(mContext);
            String orderId = options.getString("orderId");
            String paymentId = options.getString("paymentId");
            Order order = orderConnector.getOrder(orderId);
            Payment payment = BridgePaymentConnector.findPayment(order.getPayments(), paymentId);
            if (payment != null) {
                StaticReceiptPrintJob.Builder builder = new StaticPaymentPrintJob.Builder().payment(payment).paymentId(paymentId);
                if (options.hasKey("flags")) {
                    ReadableArray flags = options.getArray("flags");
                    for (int i = 0; i < flags.size(); i++) {
                        builder.flag(flags.getInt(i));
                    }
                }
                builder.build().print(mContext, getAccount());
            }
        } catch (RemoteException | ClientException | ServiceException | BindingException e) {
            Log.e(TAG, "", e);
        } catch (NoSuchKeyException | UnexpectedNativeTypeException e) {
            Log.e(TAG, "RN", e);
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
        if (getAccount() != null) {
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

    private Account getAccount() {
        return BridgeServiceConnector.getAccount(mContext);
    }

    private void unregisterReceiver() {
        try {
            mContext.unregisterReceiver(barcodeReceiver);
        } catch (IllegalArgumentException e) { }
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

    private BroadcastReceiver barcodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BarcodeResult barcodeResult = new BarcodeResult(intent);
            if (barcodeResult.isBarcodeAction()) {
                String barcode = barcodeResult.getBarcode();
                Log.d(TAG, barcode);
                mContext
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit(Constants.EVENT_BARCODE_SCANNER, barcode);
            }
        }
    };

    private LifecycleEventListener listener = new LifecycleEventListener() {
        @Override
        public void onHostResume() {
            mContext.registerReceiver(barcodeReceiver, new IntentFilter(BarcodeResult.INTENT_ACTION));
        }

        @Override
        public void onHostPause() {
            unregisterReceiver();
        }

        @Override
        public void onHostDestroy() { }
    };
}
