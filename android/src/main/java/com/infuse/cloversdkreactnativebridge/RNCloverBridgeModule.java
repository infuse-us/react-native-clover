package com.infuse.cloversdkreactnativebridge;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;

import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.os.AsyncTask;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.BitmapFactory;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CustomerMode;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ResultStatus;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import com.clover.sdk.v1.printer.Category;
import com.clover.sdk.v1.printer.Printer;
import com.clover.sdk.v1.printer.PrinterConnector;
import com.clover.sdk.v1.printer.Type;

import com.clover.sdk.v1.printer.job.ImagePrintJob;
import com.clover.sdk.v1.printer.job.PrintJob;
import com.clover.sdk.v1.printer.job.PrintJobsConnector;
import com.clover.sdk.v1.printer.job.PrintJobsContract;
import com.clover.sdk.v1.printer.job.TextPrintJob;
import com.clover.sdk.v1.printer.job.ViewPrintJob;

import java.io.Serializable;
import java.io.InputStream;
import java.io.File;
import java.net.URL;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.zip.Inflater;
import java.util.Enumeration;

import static android.app.Activity.RESULT_OK;

class RNCloverBridgeModule extends ReactContextBaseJavaModule implements ServiceConnector.OnServiceConnectedListener {

    private static final String TAG = "RNCloverBridge";
    private static final int SECURE_PAY_REQUEST = 0;
    private static final int REQUEST_PERMISSIONS = 1;
    private static final int REQUEST_ACCOUNT = 2;
    private ReactContext mContext;

    private Account account;
    private MerchantConnector merchantConnector;

    private Promise accountPromise;
    private Promise paymentPromise;

    private PrinterConnector connector;

    private EditText editPrinterId;

    private EditText editPrintOrderId;
    private Button buttonPrintOrderReceipt;

    private EditText editPrintImageUrl;
    private Button buttonPrintImage;
    private Button buttonPrintImageSync;

    private EditText editPrintText;
    private Button buttonPrintText;

    private PrinterConnector printerConnector;

    private Printer getPrinter() {
        printerConnector = new PrinterConnector(getCurrentActivity(), account, null);
        Log.d(TAG, "GOT TO GET PRINTER!+!+!+!+!+! ");

    try {
       List<Printer> printers = printerConnector.getPrinters();
       if (printers != null && !printers.isEmpty()) {
        Log.d(TAG, "YES?!!");
         return printers.get(0);
       }else{
        Log.d(TAG, "I Guess it is empty");
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
     return null;

    }


    public RNCloverBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        WritableMap cardEntryMethods = Arguments.createMap();
        cardEntryMethods.putInt("ICC_CONTACT", Intents.CARD_ENTRY_METHOD_ICC_CONTACT);
        cardEntryMethods.putInt("MAG_STRIPE", Intents.CARD_ENTRY_METHOD_MAG_STRIPE);
        cardEntryMethods.putInt("MANUAL", Intents.CARD_ENTRY_METHOD_MANUAL);
        cardEntryMethods.putInt("NFC_CONTACTLESS", Intents.CARD_ENTRY_METHOD_NFC_CONTACTLESS);
        cardEntryMethods.putInt("VAULTED_CARD", Intents.CARD_ENTRY_METHOD_VAULTED_CARD);

        constants.put("CARD_ENTRY_METHOD", cardEntryMethods);

        return constants;
    }

    @ReactMethod
    public void print(final String receiptPath, final Promise promise) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    InputStream is = (InputStream) new URL(receiptPath).getContent();
                    Bitmap b = BitmapFactory.decodeStream(is);
                    PrintJob imagePrintJob = new ImagePrintJob.Builder().bitmap(b).build();
                    Printer p = getPrinter();
                    PrintJobsConnector printerJobsConnector = new PrintJobsConnector(getCurrentActivity());
                    printerJobsConnector.print( p,imagePrintJob);
                    List<String> ids = printerJobsConnector.getPrintJobIds(PrintJobsContract.STATE_IN_QUEUE);

                    while (printerJobsConnector.getState(ids.get(0)) != PrintJobsContract.STATE_DONE) {
                        Log.d(TAG, "Receipt is printing");
                    }

                    Log.d(TAG, "Printing finished");

                    File file = new File(receiptPath);

                    if(file.delete()) {
                       Log.d(TAG, "File deleted successfully");
                    }else{
                        Log.d(TAG, "File was not deleted");
                    };

                    return ids.get(0);
                } catch (Exception e) {
                    promise.reject("Error printing");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String id) {
                if (id != null) {
                    promise.resolve("Print job: " + id + " has finished");
                } else {
                    promise.resolve("Printing error");
                }
            }
        }.execute();
    }

    @ReactMethod
    public void enableCustomerMode() { CustomerMode.enable(this.mContext); }

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
                    WritableMap map = Arguments.createMap();
                    map.putString("merchantId", result.getId());
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
            });
        } else {
            Log.d(TAG, "No merchantConnector");
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

    @ReactMethod
    public void getIPAddress(Promise promise) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Service.CONNECTIVITY_SERVICE);
        List addresses = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getLinkAddresses();
        String ipAddress = addresses.get(1).toString();
        promise.resolve(ipAddress);
    }

    @ReactMethod
    public static void getEthernetMacAddress(Promise promise) {
        try {
            List<NetworkInterface> allNetworkInterfaces = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface nif : allNetworkInterfaces) {
                if (!nif.getName().equalsIgnoreCase("eth0"))
                    continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    promise.reject("404", "Not Found");
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                promise.resolve(res1.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            promise.reject("404", "Interface Error");
        }
    }

    @ReactMethod
    public void startExternalSecurePayment(ReadableMap options, Promise promise) {
        if (options.hasKey("amount") && options.hasKey("externalService")) {
            int amount = options.getInt("amount");
            String externalService = options.getString("externalService");

            Intent payIntent = createExternalSecurePayIntent((long) amount, externalService);

            if (options.hasKey("cardEntryFlag")) {
                int cardEntryFlag = options.getInt("cardEntryFlag");
                payIntent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, cardEntryFlag);
            }

            paymentPromise = promise;
            getCurrentActivity().startActivityForResult(payIntent, SECURE_PAY_REQUEST);
        } else {
            Log.e(TAG, "Missing amount or externalService");
        }
    }

    @ReactMethod
    @TargetApi(27)
    public void startAccountChooserIfNeeded(Promise promise) {
        startAccountChooser();
        if (account != null) {
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
            getCurrentActivity().startActivityForResult(accountIntent, REQUEST_ACCOUNT);
        }
    }

    @ReactMethod
    public void enableSound() {
        AudioManager audioManager;
        audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
    }

    @ReactMethod
    public void disableSound() {
        AudioManager audioManager;
        audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
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

    private Intent createExternalSecurePayIntent(long amount, String externalService) {
        Intent payIntent = new Intent("clover.intent.action.START_SECURE_PAYMENT");
        payIntent.putExtra(Intents.EXTRA_AMOUNT, amount);
        payIntent.putExtra(Intents.EXTRA_ORDER_ID, "2RQX89RBFSV8T");

        HashMap extraValues = new HashMap<String, String>();
        extraValues.put("PROCESS_PAYMENT_EXTERNAL_AUTH_SERVICE", externalService);
        payIntent.putExtra(Intents.EXTRA_PASS_THROUGH_VALUES, (Serializable) extraValues);

        return payIntent;
    }

    @Override
    public void onServiceConnected(ServiceConnector connector) {
        Log.i(TAG, "service connected: " + connector);
    }

    @Override
    public void onServiceDisconnected(ServiceConnector connector) {
        Log.i(TAG, "service disconnected: " + connector);
    }

    private final ActivityEventListener mActivityEventListener = new ActivityEventListener() {
        @Deprecated
        public void onActivityResult(int requestCode, int resultCode, Intent data) { }

        public void onActivityResult(Activity activity,  int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_ACCOUNT) {
                WritableMap map = Arguments.createMap();
                if (resultCode == RESULT_OK) {
                    map.putBoolean("success", true);
                    accountPromise.resolve(map);
                } else {
                    map.putBoolean("success", false);
                    accountPromise.resolve(map);
                }
            } else if (requestCode == SECURE_PAY_REQUEST) {
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", resultCode == RESULT_OK);
                paymentPromise.resolve(map);
            }
        }

        public void onNewIntent(Intent intent) { }
    };
}
