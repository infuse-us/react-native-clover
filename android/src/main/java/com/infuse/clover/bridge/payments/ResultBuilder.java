package com.infuse.clover.bridge.payments;

import com.clover.sdk.v3.remotepay.BaseResponse;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

class ResultBuilder {

    private WritableMap result;

    ResultBuilder(BaseResponse response) {
        result = Arguments.createMap();
        setSuccess(response.getSuccess());
        if (!response.getSuccess()) {
            setReason(response.getReason());
            setMessage(response.getMessage());
        }
    }

    void setRefundId(String refundId) {
        result.putString("refundId", refundId);
    }

    void setPaymentId(String paymentId) {
        result.putString("paymentId", paymentId);
    }

    void putMap(String key, WritableMap map) {
        result.putMap(key, map);
    }

    WritableMap build() {
        return result;
    }

    private void setSuccess(boolean success) {
        result.putBoolean("success", success);
    }

    private void setReason(String reason) {
        result.putString("reason", reason);
    }

    private void setMessage(String message) {
        result.putString("message", message);
    }
}
