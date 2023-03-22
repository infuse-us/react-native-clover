package com.infuse.clover.bridge;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.clover.sdk.v3.order.OrderConnector;

public class BridgeServiceConnector implements ServiceConnector.OnServiceConnectedListener {
    private MerchantConnector merchantConnector;
    private OrderConnector orderConnector;

    static public Account getAccount(Context context) {
        return CloverAccount.getAccount(context);
    }

    public OrderConnector getOrderConnector(Context context) {
        Account account = getAccount(context);
        if (orderConnector != null) {
            orderConnector.disconnect();
            orderConnector = null;
        }
        orderConnector = new OrderConnector(context, account, this);
        orderConnector.connect();
        return orderConnector;
    }

    public MerchantConnector getMerchantConnector(Context context) {
        Account account = getAccount(context);
        if (merchantConnector != null) {
            merchantConnector.disconnect();
            merchantConnector = null;
        }
        merchantConnector = new MerchantConnector(context, account, this);
        merchantConnector.connect();
        return merchantConnector;
    }

    @Override
    public void onServiceConnected(ServiceConnector connector) {
        Log.i(RNCloverBridgeModule.TAG, "service connected: " + connector);
    }

    @Override
    public void onServiceDisconnected(ServiceConnector connector) {
        Log.i(RNCloverBridgeModule.TAG, "service disconnected: " + connector);
    }
}
