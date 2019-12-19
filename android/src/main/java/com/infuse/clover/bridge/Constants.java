package com.infuse.clover.bridge;

import android.os.Build;

import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.printer.job.PrintJob;
import com.clover.sdk.v3.order.OrderContract;
import com.clover.sdk.v3.order.VoidReason;
import com.clover.sdk.v3.payments.DataEntryLocation;
import com.clover.sdk.v3.payments.TipMode;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    static final String HARDWARE_SERIAL_NUMBER = "HARDWARE_SERIAL_NUMBER";
    static final String CARD_ENTRY_METHOD = "CARD_ENTRY_METHOD";
    static final String DATA_ENTRY_LOCATION = "DATA_ENTRY_LOCATION";
    static final String VOID_REASON = "VOID_REASON";
    static final String TIP_MODE = "TIP_MODE";
    static final String PRINT_JOB_FLAG = "PRINT_JOB_FLAG";

    static final String EVENT_BARCODE_SCANNER = "EVENT_BARCODE_SCANNER";

    static public class Builder {
        private Map<String, Object> constants;

        public Builder() {
            constants = new HashMap<>();
        }

        public Builder put(String key, Object value) {
            constants.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            // Expose Clover Hardware Serial Number
            constants.put(HARDWARE_SERIAL_NUMBER, Build.SERIAL);

            // Build Event Names
            WritableMap bridgeEvents = Arguments.createMap();
            bridgeEvents.putString("BARCODE_SCANNER", EVENT_BARCODE_SCANNER);
            constants.put("EVENT", bridgeEvents);

            // Expose card entry methods
            WritableMap cardEntryMethods = Arguments.createMap();
            cardEntryMethods.putInt("ICC_CONTACT", Intents.CARD_ENTRY_METHOD_ICC_CONTACT);
            cardEntryMethods.putInt("MAG_STRIPE", Intents.CARD_ENTRY_METHOD_MAG_STRIPE);
            cardEntryMethods.putInt("MANUAL", Intents.CARD_ENTRY_METHOD_MANUAL);
            cardEntryMethods.putInt("NFC_CONTACTLESS", Intents.CARD_ENTRY_METHOD_NFC_CONTACTLESS);
            cardEntryMethods.putInt("VAULTED_CARD", Intents.CARD_ENTRY_METHOD_VAULTED_CARD);
            cardEntryMethods.putInt("ALL", Intents.CARD_ENTRY_METHOD_ALL);
            cardEntryMethods.putInt("DEFAULT", Intents.CARD_ENTRY_METHOD_ICC_CONTACT
                    | Intents.CARD_ENTRY_METHOD_MAG_STRIPE
                    | Intents.CARD_ENTRY_METHOD_NFC_CONTACTLESS);
            constants.put("CARD_ENTRY_METHOD", cardEntryMethods);

            // Expose DataEntryLocation Enum
            // https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/payments/DataEntryLocation.html
            WritableMap dataEntryLocations = Arguments.createMap();
            for (DataEntryLocation dataEntryLocation : DataEntryLocation.values()) {
                dataEntryLocations.putString(dataEntryLocation.name(), dataEntryLocation.name());
            }
            constants.put("DATA_ENTRY_LOCATION", dataEntryLocations);

            // Expose VoidReason Enum
            // https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/order/VoidReason.html
            WritableMap voidReasons = Arguments.createMap();
            for (VoidReason voidReason : VoidReason.values()) {
                voidReasons.putString(voidReason.name(), voidReason.name());
            }
            constants.put("VOID_REASON", voidReasons);

            // Expose TipMode Enum
            // https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/payments/TipMode.html
            WritableMap tipModes = Arguments.createMap();
            for (TipMode tipMode : TipMode.values()) {
                tipModes.putString(tipMode.name(), tipMode.name());
            }
            constants.put("TIP_MODE", tipModes);

            // Expose PrintJob Flags
            // https://clover.github.io/clover-android-sdk/com/clover/sdk/v1/printer/job/PrintJob.html
            WritableMap printJobFlags = Arguments.createMap();
            printJobFlags.putInt("FLAG_BILL", PrintJob.FLAG_BILL);
            printJobFlags.putInt("FLAG_CUSTOMER", PrintJob.FLAG_CUSTOMER);
            printJobFlags.putInt("FLAG_FORCE_SIGNATURE", PrintJob.FLAG_FORCE_SIGNATURE);
            printJobFlags.putInt("FLAG_MERCHANT", PrintJob.FLAG_MERCHANT);
            printJobFlags.putInt("FLAG_NO_SIGNATURE", PrintJob.FLAG_NO_SIGNATURE);
            printJobFlags.putInt("FLAG_NONE", PrintJob.FLAG_NONE);
            printJobFlags.putInt("FLAG_REFUND", PrintJob.FLAG_REFUND);
            printJobFlags.putInt("FLAG_REPRINT", PrintJob.FLAG_REPRINT);
            constants.put("PRINT_JOB_FLAG", printJobFlags);

            // Expose searchable order columns
            WritableMap ordersContractColumns = Arguments.createMap();
            ordersContractColumns.putString("AMOUNT_CREDITED", OrderContract.Summaries.AMOUNT_CREDITED);
            ordersContractColumns.putString("AMOUNT_PAID", OrderContract.Summaries.AMOUNT_PAID);
            ordersContractColumns.putString("AMOUNT_REFUNDED", OrderContract.Summaries.AMOUNT_REFUNDED);
            ordersContractColumns.putString("CREATED_TIME", OrderContract.Summaries.CREATED);
            ordersContractColumns.putString("CURRENCY", OrderContract.Summaries.CURRENCY);
            ordersContractColumns.putString("CUSTOMER_ID", OrderContract.Summaries.CUSTOMER_ID);
            ordersContractColumns.putString("CUSTOMER_NAME", OrderContract.Summaries.CUSTOMER_NAME);
            ordersContractColumns.putString("DELETED", OrderContract.Summaries.DELETED);
            ordersContractColumns.putString("EMPLOYEE_NAME", OrderContract.Summaries.EMPLOYEE_NAME);
            ordersContractColumns.putString("ID", OrderContract.Summaries.ID);
            ordersContractColumns.putString("LAST_MODIFIED", OrderContract.Summaries.LAST_MODIFIED);
            ordersContractColumns.putString("NOTE", OrderContract.Summaries.NOTE);
            ordersContractColumns.putString("ORDER_TYPE", OrderContract.Summaries.ORDER_TYPE);
            ordersContractColumns.putString("PAYMENT_STATE", OrderContract.Summaries.PAYMENT_STATE);
            ordersContractColumns.putString("STATE", OrderContract.Summaries.STATE);
            ordersContractColumns.putString("TENDERS",OrderContract.Summaries.TENDERS);
            ordersContractColumns.putString("TITLE", OrderContract.Summaries.TITLE);
            ordersContractColumns.putString("TOTAL", OrderContract.Summaries.TOTAL);
            constants.put("SEARCHABLE_ORDERS_COLUMNS", ordersContractColumns);

            // Expose Sort Order
            WritableMap ordersSortOrder = Arguments.createMap();
            ordersSortOrder.putString("ASC", "ASC");
            ordersSortOrder.putString("DESC", "DESC");
            constants.put("SEARCHABLE_ORDERS_SORT_ORDER", ordersSortOrder);

            return constants;
        }
    }
}
