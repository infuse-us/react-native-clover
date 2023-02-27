package com.infuse.clover.bridge;

import android.util.Log;

import com.clover.sdk.v1.ResultStatus;
import com.clover.sdk.v3.inventory.Item;
import com.clover.sdk.v3.inventory.InventoryConnector;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;


public class InventoryItemsCallbackTask {
    private Promise promise;

    public void onServiceSuccess(Item result, ResultStatus status) {
        Log.d("Inventory Success", String.format(Locale.US, "on service success: %s", status));
    }

    public void onServiceFailure(ResultStatus status) {
        Log.w("Inventory Error", String.format(Locale.US, "on service failure: %s", status));
    }

    public void onServiceConnectionFailure() {
        Log.w("Inventory Connect Error", String.format(Locale.US, "on service connect failure"));
    }
}
