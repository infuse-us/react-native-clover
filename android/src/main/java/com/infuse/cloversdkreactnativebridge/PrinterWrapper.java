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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrinterWrapper {

    private static final String TAG = "PrinterWrapper";
    private PrinterConnector printerConnector;
    private Promise promise;

    public PrinterWrapper(Promise promise){
        this.promise = promise;
    }

    public Printer getPrinter(Activity currentActivity, Account account) {
        printerConnector = new PrinterConnector(currentActivity, account, null);
        try {
            List<Printer> printers = printerConnector.getPrinters();
            if (printers != null && !printers.isEmpty()) {
                return printers.get(0);
            }else{
                Log.d(TAG, "No printer found");
                promise.reject("Printing Error","Printing job did not finish properly.");
            }
        } catch (Exception e) {
            promise.reject("Printing Error","getPrinter error");
            e.printStackTrace();
        }
        return null;
    }

    public void print(final Activity currentActivity, final Account account, final String imagePath) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    InputStream is = (InputStream) new URL(imagePath).getContent();
                    Bitmap b = BitmapFactory.decodeStream(is);
                    PrintJob imagePrintJob = new ImagePrintJob.Builder().bitmap(b).build();
                    Printer p = getPrinter(currentActivity, account);
                    PrintJobsConnector printerJobsConnector = new PrintJobsConnector(currentActivity);
                    printerJobsConnector.print( p,imagePrintJob);
                    List<String> ids = printerJobsConnector.getPrintJobIds(PrintJobsContract.STATE_IN_QUEUE);

                    while (printerJobsConnector.getState(ids.get(0)) != PrintJobsContract.STATE_DONE) {
                        Log.d(TAG, "Printing");
                        if(printerJobsConnector.getState(ids.get(0)) == PrintJobsContract.STATE_ERROR){
                            return "Error while printing";
                        }
                    }

                    Log.d(TAG, "Printing finished");

                    String imageToDelete = imagePath.replace("file:///","");
                    File file = new File(imageToDelete);

                    if(file.delete()) {
                        Log.d(TAG, "File deleted successfully");
                    }else{
                        Log.d(TAG, "File was not deleted");
                    };

                    return "";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error printing";
                }
            }

            @Override
            protected void onPostExecute(final String response) {
               if (response == "") {
                   promise.resolve("Printed successfully");
               } else {
                   promise.reject("Printing Error", "Printing job did not finish properly.");
               }
            }
        }.execute();
    }
}

