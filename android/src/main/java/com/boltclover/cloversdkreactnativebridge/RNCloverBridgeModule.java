package com.boltclover.cloversdkreactnativebridge;

import android.util.Log;

import com.clover.sdk.util.CustomerMode;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

class RNCloverBridgeModule extends ReactContextBaseJavaModule {

  private ReactContext mContext;


  public RNCloverBridgeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    mContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNCloverBridge";
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
}
