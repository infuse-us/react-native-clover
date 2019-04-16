package com.infuse.cloversdkreactnativebridge.payments;

import com.facebook.react.bridge.WritableMap;

public abstract class ReactPaymentListener {
    public abstract void onPaymentConnectorEvent(WritableMap map);
}

