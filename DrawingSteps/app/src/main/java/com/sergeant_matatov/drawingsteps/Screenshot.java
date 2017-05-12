package com.sergeant_matatov.drawingsteps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

/**
 * Created by Yurka on 22.10.2016.
 */

public class Screenshot {

    final String LOG_TAG = "myLogs";

    private static Screenshot mInstance;

    private Screenshot() {
    }

    public static Screenshot getInstance() {
        if (mInstance == null) {
            synchronized (Screenshot.class) {
                if (mInstance == null) {
                    mInstance = new Screenshot();
                }
            }
        }
        return mInstance;
    }

    /**
     * Measures and takes a screenshot of the provided {@link View}.
     *
     * @param view The view of which the screenshot is taken
     * @return A {@link Bitmap} for the taken screenshot.
     */
    public Bitmap takeScreenshotForView(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
        view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }

    public Bitmap takeScreenshotForScreen(Activity activity) {
        return takeScreenshotForView(activity.getWindow().getDecorView().getRootView());
    }

}
