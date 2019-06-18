package com.infuse.clover.bridge;

import android.util.Log;

import com.clover.sdk.v1.ResultStatus;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantAddress;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

public class MerchantCallbackTask extends MerchantConnector.MerchantCallback<Merchant> {
    private Promise promise;

    MerchantCallbackTask(Promise promise) {
        this.promise = promise;
    }

    @Override
    public void onServiceSuccess(Merchant result, ResultStatus status) {
        MerchantAddress mAddress = result.getAddress();

        WritableMap map = Arguments.createMap();
        map.putString("merchantId", result.getId());
        map.putString("name", result.getName());
        map.putString("email", result.getSupportEmail());
        map.putMap("location", this.mapLocation(mAddress));

        sendResponse(true, map, null);
    }

    @Override
    public void onServiceFailure(ResultStatus status) {
        Log.d(RNCloverBridgeModule.TAG, "onServiceFailure");
        Log.d(RNCloverBridgeModule.TAG, String.valueOf(status.getStatusCode()));
        Log.d(RNCloverBridgeModule.TAG, status.getStatusMessage());
        sendResponse(false, null, status.getStatusMessage());
    }

    @Override
    public void onServiceConnectionFailure() {
        Log.d(RNCloverBridgeModule.TAG, "onServiceConnectionFailure");
        sendResponse(false, null, null);
    }

    private WritableMap mapLocation(MerchantAddress address) {
        WritableMap map = Arguments.createMap();

        map.putString("country", address.getCountry());
        map.putString("city", address.getCity());
        map.putString("region", address.getState());
        return map;
    }

    private void sendResponse(boolean success, WritableMap data, String statusMessage) {
        WritableMap map = Arguments.createMap();

        map.putBoolean("success", success);
        if (!success) map.putString("statusMessage", statusMessage);
        map.putMap("data", data);

        promise.resolve(map);
    }
}
