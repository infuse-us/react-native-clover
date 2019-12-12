package com.infuse.clover.bridge.orders;

import com.clover.sdk.v3.order.OrderContract;

public class OrderUtils {

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
        // Custom SQL for Search by UPC
        if(searchCategory.equals("UPC")) {
            searchCategory = "Note";
            selection = String.format("select rtrim(trim(ltrim(replace(%s, replace(%s, rtrim(%s, replace(%s, '|||', '' ) ), ''), ''), 'UPCs:')), '|||') LIKE '%%%s%%'",
                    searchCategory, searchCategory, searchCategory, searchCategory, searchTerm);
        }
        // Custom SQL for Search by Item Name
        if(searchCategory.equals("Item Name")) {
            searchCategory = "Note";
            selection = String.format("select trim(ltrim(trim(replace(%s, rtrim(%s, replace(%s, '|||', '' ) ), '')), 'Names:')) LIKE '%%%s%%'",
                    searchCategory, searchCategory, searchCategory, searchTerm);
        }

        // Search by modified date
        if(searchCategory.equals("LAST_MODIFIED")) {
            String[] timeSplits = searchTerm.split("-");
            String startTime = timeSplits[0];
            String endTime = timeSplits[1];
            selection = String.format("%s BETWEEN %s AND %s", searchCategory, startTime, endTime);
        }

        return selection;
    }
}
