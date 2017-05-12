package com.sergeant_matatov.drawingsteps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Yurka on 22.10.2016.
 */

public class FileUtils {

    final String LOG_TAG = "myLogs";

    private static FileUtils mInstance;
    FileOutputStream fOut;

    String adress;

    private FileUtils() {
    }

    public static FileUtils getInstance() {
        if (mInstance == null) {
            synchronized (FileUtils.class) {
                if (mInstance == null) {
                    mInstance = new FileUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * Stores the given {@link Bitmap} to a path on the device.
     *
     * @param bitmap   The {@link Bitmap} that needs to be stored
     * @param filePath The path in which the bitmap is going to be stored.
     */

    public String storeBitmap(Bitmap bitmap, String filePath, Context context) {
        Log.d(LOG_TAG, "storeBitmap = " + filePath);
        Log.d(LOG_TAG, "storeBitmap = " + context.getString(R.string.app_name));

        Time time = new Time();
        time.setToNow();

        try {
            filePath += "/Drawing Steps/";
            File file = new File(Environment.getExternalStorageDirectory(),"Drawing Steps");
            file.mkdir();
            file = new File(filePath, context.getString(R.string.app_name) + "_" + Integer.toString(time.year) + "_" + Integer.toString(time.month + 1) + "_" + Integer.toString(time.monthDay) + "_" + Integer.toString(time.hour) + "_" + Integer.toString(time.minute) + ".jpg"); // создать уникальное имя для файла основываясь на дате сохранения

            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
            fOut.flush();
            fOut.close();
            adress = file.getName();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName()); // регистрация в фотоальбоме

          //  Log.d(LOG_TAG, "11storeBitmap = " + MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName()).toString()); // регистрация в фотоальбоме

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adress;
    }
}
