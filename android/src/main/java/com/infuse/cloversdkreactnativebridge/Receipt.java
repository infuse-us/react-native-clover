package com.infuse.cloversdkreactnativebridge;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.accounts.Account;

import com.clover.sdk.v1.printer.Printer;
import com.clover.sdk.v1.printer.PrinterConnector;
import android.app.Activity;
import com.clover.sdk.v1.printer.job.ImagePrintJob;
import com.clover.sdk.v1.printer.job.PrintJob;
import com.clover.sdk.v1.printer.job.PrintJobsConnector;
import com.clover.sdk.v1.printer.job.PrintJobsContract;
import com.facebook.react.bridge.Promise;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class Receipt {

    private static final String TAG = "Receipt";

    private PrinterConnector printerConnector;

    public Printer getPrinter(Activity currentActivity, Account account) {
        printerConnector = new PrinterConnector(currentActivity, account, null);
        Log.d(TAG, "GOT TO GET PRINTER!+!+!+!+!+! ");
        try {
            List<Printer> printers = printerConnector.getPrinters();
            if (printers != null && !printers.isEmpty()) {
                Log.d(TAG, "YES?!!");
                return printers.get(0);
            }else{
                Log.d(TAG, "No printer found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void print(final Activity currentActivity, final Account account, final Promise promise, final String receiptPath) {
        Log.d(TAG, "got the tag" + receiptPath);
        Log.d(TAG, "Got to print inside the class");

        Log.d(TAG, "Got to print inside the class");

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    InputStream is = (InputStream) new URL(receiptPath).getContent();
                    Bitmap b = BitmapFactory.decodeStream(is);
                    PrintJob imagePrintJob = new ImagePrintJob.Builder().bitmap(b).build();
                    Printer p = getPrinter(currentActivity, account);
                    PrintJobsConnector printerJobsConnector = new PrintJobsConnector(currentActivity);
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
                    e.printStackTrace();
                    return "Error printing";
                }
            }

            @Override
            protected void onPostExecute(String id) {

                promise.resolve("THIS WORKED!");
                Log.d(TAG, "Got to onPostExecute");
//                if (id != null) {
//                    return "Print job has finished";
//                } else {
//                    return "Printing error";
//                }
            }
        }.execute();


    }
}

