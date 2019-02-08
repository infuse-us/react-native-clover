package com.infuse.cloversdkreactnativebridge;

import android.util.Log;

import com.clover.sdk.v1.ResultStatus;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantAddress;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

public class MerchantCallbackTask extends MerchantConnector.MerchantCallback<Merchant> {
    private static final String TAG = "RNCloverBridge";
    Promise promise;

    public MerchantCallbackTask(Promise promise) {
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

    private WritableMap mapLocation(MerchantAddress address) {
        WritableMap map = Arguments.createMap();

        map.putString("country", address.getCountry());
        map.putString("city", address.getCity());
        map.putString("region", address.getState());
        return map;
    }
}
