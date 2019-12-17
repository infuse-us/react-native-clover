package com.infuse.clover.bridge.orders;

import com.clover.sdk.v3.order.OrderContract;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import android.database.Cursor;

public class OrderUtils {

    public static String[] searchableColumns() {
        return new String[] {
                OrderContract.Summaries.AMOUNT_CREDITED,
                OrderContract.Summaries.AMOUNT_PAID,
                OrderContract.Summaries.AMOUNT_REFUNDED,
                OrderContract.Summaries.CREATED,
                OrderContract.Summaries.CURRENCY,
                OrderContract.Summaries.CUSTOMER_ID,
                OrderContract.Summaries.CUSTOMER_NAME,
                OrderContract.Summaries.DELETED,
                OrderContract.Summaries.EMPLOYEE_NAME,
                OrderContract.Summaries.ID,
                OrderContract.Summaries.LAST_MODIFIED,
                OrderContract.Summaries.NOTE,
                OrderContract.Summaries.ORDER_TYPE,
                OrderContract.Summaries.PAYMENT_STATE,
                OrderContract.Summaries.STATE,
                OrderContract.Summaries.TENDERS,
                OrderContract.Summaries.TITLE,
                OrderContract.Summaries.TOTAL,
        };
    }

    public static String buildSortOrder(int limit, int offset, String sortCategory, String sortOrder) {
        if(sortOrder == null || sortOrder.isEmpty()) {
            sortCategory = OrderContract.Summaries.LAST_MODIFIED;
            sortOrder = "DESC";
        }
        return String.format("CASE WHEN %s IS NULL THEN 1 ELSE 0 END, %s", sortCategory, sortCategory)
                + String.format(" %s LIMIT %s OFFSET %s", sortOrder, limit, offset);
    }

    public static String buildSearchSelection(String searchCategory, String searchTerm) {
        if((searchCategory == null || searchCategory.isEmpty())
                || (searchTerm == null || searchTerm.isEmpty())) {
            return null;
        }

        String selection = String.format("%s LIKE '%%%s%%'", searchCategory, searchTerm);

        // Search by modified date
        if(searchCategory.equals("LAST_MODIFIED")) {
            String[] timeSplits = searchTerm.split("-");
            String startTime = timeSplits[0];
            String endTime = timeSplits[1];
            selection = String.format("%s BETWEEN %s AND %s", searchCategory, startTime, endTime);
        }
        // Search by created time
        if(searchCategory.equals("CREATED_TIME")) {
            String[] timeSplits = searchTerm.split("-");
            String startTime = timeSplits[0];
            String endTime = timeSplits[1];
            selection = String.format("%s BETWEEN %s AND %s", searchCategory, startTime, endTime);
        }

        return selection;
    }


    public static WritableArray buildAllColumnsData(Cursor cursor) {

        WritableArray result = Arguments.createArray();
        WritableMap row = Arguments.createMap();

        String amountCredited = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.AMOUNT_CREDITED));
        String amountRefund = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.AMOUNT_REFUNDED));
        String created = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.CREATED));
        String currency = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.CURRENCY));
        String customerId = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.CUSTOMER_ID));
        String customerName = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.CUSTOMER_NAME));
        String deleted = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.DELETED));
        String employeeName = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.EMPLOYEE_NAME));
        String id = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.ID));
        String lastModified = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.LAST_MODIFIED));
        String note = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.NOTE));
        String orderType = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.ORDER_TYPE));
        String paymentState = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.PAYMENT_STATE));
        String tender = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.TENDERS));
        String title = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.TITLE));
        String total = cursor.getString(cursor.getColumnIndex(OrderContract.Summaries.TOTAL));

        row.putString("amountCredited", amountCredited);
        row.putString("amountRefund", amountRefund);
        row.putString("created", created);
        row.putString("currency", currency);
        row.putString("customerId", customerId);
        row.putString("customerName", customerName);
        row.putString("deleted", deleted);
        row.putString("employeeName", employeeName);
        row.putString("id", id);
        row.putString("lastModified", lastModified);
        row.putString("note", note);
        row.putString("orderType", orderType);
        row.putString("paymentState", paymentState);
        row.putString("tender", tender);
        row.putString("title", title);
        row.putString("total", total);

        result.pushMap(row);
        return result;
    }
}
