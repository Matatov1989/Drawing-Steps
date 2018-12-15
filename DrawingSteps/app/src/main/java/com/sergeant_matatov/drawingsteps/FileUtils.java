package com.sergeant_matatov.drawingsteps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Yurka on 22.10.2016.
 */

public class FileUtils {

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

        Time time = new Time();
        time.setToNow();

        try {
            filePath += "/"+context.getString(R.string.textNameScreen)+"/";     //create a path to a picture
            File file = new File(Environment.getExternalStorageDirectory(), "Drawing Steps");
            //   File file = new File(context.getFilesDir(), "Drawing Steps");
            file.mkdir();
            file = new File(filePath, context.getString(R.string.textNameScreen) + "_" + Integer.toString(time.year) + "_" + Integer.toString(time.month + 1) + "_" + Integer.toString(time.monthDay) + "_" + Integer.toString(time.hour) + "_" + Integer.toString(time.minute) + ".jpg"); // создать уникальное имя для файла основываясь на дате сохранения

            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);  //save a picture to .jpeg with 85% of the compress.
            fOut.flush();
            fOut.close();
            adress = file.getName();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, file.getName(), file.getName());  //photo album registration
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adress;
    }
}