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

public class PrinterWrapper {

    private static final String TAG = "PrinterWrapper";
    private PrinterConnector printerConnector;

    public Printer getPrinter(Activity currentActivity, Account account) {
        printerConnector = new PrinterConnector(currentActivity, account, null);
        try {
            List<Printer> printers = printerConnector.getPrinters();
            if (printers != null && !printers.isEmpty()) {
                return printers.get(0);
            }else{
                Log.d(TAG, "No printer found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void print(final Activity currentActivity, final Account account, final Promise promise, final String imagePath) {
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
                    }

                    Log.d(TAG, "Printing finished!");

                    String imageToDelete = imagePath.replace("file:///","");
                    File file = new File(imageToDelete);

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
                Log.d(TAG, "Got to onPostExecute");
               if (id != null) {
                    promise.resolve("Print job has finished");
               } else {
                    promise.reject("Printing Error","Printing job did not finish properly.");
               }
            }
        }.execute();
    }
}

