package com.infuse.clover.bridge.payments;

import com.facebook.react.bridge.WritableMap;

public abstract class ReactPaymentListener {
    public abstract void onPaymentConnectorEvent(WritableMap map);
}

