package com.clicklabs.androidlogin.Utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.clicklabs.androidlogin.R;


/**
 * Created by Krishika on 16-02-2016.
 */
public class CommonProgress {
    private static ProgressDialog loading = null;

    public static void showProgressDialog(Context context, String message) {
            loading = new ProgressDialog(context);

        if (message == null) {
            loading.setMessage("Loading...");
        } else {
            loading.setMessage(message);
        }

       // loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(true);
        loading.show();
        loading.setContentView(R.layout.progressbar_layout);
    }

    public static void dismissProgressDialog() {
        if (loading != null && loading.isShowing())
            loading.dismiss();
    }
}
