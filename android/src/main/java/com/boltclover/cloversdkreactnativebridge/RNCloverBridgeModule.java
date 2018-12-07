package com.boltclover.cloversdkreactnativebridge;

import android.accounts.Account;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CustomerMode;
import com.clover.sdk.v1.ResultStatus;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

class RNCloverBridgeModule extends ReactContextBaseJavaModule implements ServiceConnector.OnServiceConnectedListener {

    private static final String TAG = "RNCloverBridge";
    private ReactContext mContext;

    private Account account;
    private MerchantConnector merchantConnector;

    public RNCloverBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    @ReactMethod
    public void enableCustomerMode() {
        Log.d("RNCloverBridge", "enabling customer mode");
        CustomerMode.enable(this.mContext);
    }

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
                    super.onServiceSuccess(result, status);

                    WritableMap map = Arguments.createMap();
                    map.putString("merchantId", result.getId());
                    promise.resolve(map);
                }

                @Override
                public void onServiceFailure(ResultStatus status) {
                    super.onServiceFailure(status);
                    Log.i(TAG, "onServiceFailure" + status);
//                    promise.reject("onServiceFailure", "onServiceFailure");
                    promise.resolve(null);
                }

                @Override
                public void onServiceConnectionFailure() {
                    super.onServiceConnectionFailure();
                    Log.i(TAG, "onServiceConnectionFailure");
//                    promise.reject("onServiceConnectionFailure", "onServiceConnectionFailure");
                    promise.resolve(null);
                }
            });
        } else {
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

    @Override
    public void onServiceConnected(ServiceConnector connector) {
        Log.i(TAG, "service connected: " + connector);
    }

    @Override
    public void onServiceDisconnected(ServiceConnector connector) {
        Log.i(TAG, "service disconnected: " + connector);
    }
}
