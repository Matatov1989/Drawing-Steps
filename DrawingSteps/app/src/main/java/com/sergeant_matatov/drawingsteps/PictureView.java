package com.sergeant_matatov.drawingsteps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Yurka on 11.10.2016.
 */

public class PictureView extends View {

    final String LOG_TAG = "myLogs";

    LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2 * 1000; /* 2 sec */

    PictureView view;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint paint, mBitmapPaint;
    private float canvasSize;
    private final int horizontalCountOfCells, verticalCountOfCells;

    private final int viewSize;
    private float mScaleFactor;

    int pointX, pointY;
    int pointResX = 0, pointResY = 0;

    double locationFirstX = 0.0, locationFirstY = 0.0;
    double locationSecondX = 0.0, locationSecondY = 0.0;

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        //size of margin
        horizontalCountOfCells = 10;
        verticalCountOfCells = 10;
        viewSize = (int) convertDpToPixel(displaymetrics.heightPixels, context);
        mScaleFactor = 1f;                              //default zoom
        canvasSize = (int) (viewSize * mScaleFactor);   //size of canvas

        //get size display
        screenSizeDisplay();

        mBitmap = Bitmap.createBitmap((int) canvasSize, (int) canvasSize, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        //set parameterts to brush
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xffff0505);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);//зумируем канвас
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    public void drawSteps(double lat, double lon) {
        Log.d(LOG_TAG, "---drawSteps---");
        Log.d(LOG_TAG, "lat = " + lat);
        Log.d(LOG_TAG, "lon = " + lon);

        ///9 4 6
        ///2 1 3
        ///8 5 7
        if (locationFirstX == 0 && locationFirstY == 0) {
            locationFirstX = lat;
            locationFirstY = lon;

            locationSecondX = lat;
            locationSecondY = lon;
            mCanvas.drawPoint(pointResX, pointResY, paint);
        } else {

            locationFirstX = lat;
            locationFirstY = lon;

            switch (getDirection()){
                case 1:
                    pointResX = pointX;
                    pointResY = pointY;
                    break;

                case 2:
                    pointResX -= 2;
                    break;

                case 3:
                    pointResX += 2;
                    break;

                case 4:
                    pointResY -= 2;
                    break;

                case 5:
                    pointResY += 2;
                    break;

                case 6:
                    pointResX += 2;
                    pointResY -= 2;
                    break;

                case 7:
                    pointResX += 2;
                    pointResY += 2;
                    break;

                case 8:
                    pointResX -= 2;
                    pointResY += 2;
                    break;

                case 9:
                    pointResX -= 2;
                    pointResY -= 2;
                    break;
            }

            locationSecondX = lat;
            locationSecondY = lon;
            mCanvas.drawPoint(pointResX, pointResY, paint);

        }

        invalidate();//перерисовываем канвас
    }

    private int getDirection() {

        int dirVertical = 0;
        int dirHorizontal = 0;



        Log.d(LOG_TAG, "=== Direction ===");
        if (locationFirstX < locationSecondX) {
            Log.d(LOG_TAG, "left");
            dirVertical = 2;
        } else if (locationFirstX > locationSecondX) {
            Log.d(LOG_TAG, "rigth");
            dirVertical = 3;
        } else {
            Log.d(LOG_TAG, "0 horizont");
            dirVertical = 0;
        }

        if (locationFirstY < locationSecondY) {
            Log.d(LOG_TAG, "up");
            dirHorizontal = 4;
        } else if (locationFirstY > locationSecondY) {
            Log.d(LOG_TAG, "down");
            dirHorizontal = 5;
        } else {
            Log.d(LOG_TAG, "0 vertical");
            dirHorizontal = 0;
        }

        if (dirVertical == 0 && dirHorizontal == 0){
            return 1;
        }
        else
        {
            if (dirVertical == 2 && dirHorizontal == 4){
                return 9;
            }
            else if (dirVertical == 2 && dirHorizontal == 5){
                return 8;
            }
            else if (dirVertical == 2 && dirHorizontal == 0)
            {
                return 2;
            }
            else if (dirVertical == 0 && dirHorizontal == 4)
            {
                return 4;
            }
            else if (dirVertical == 3 && dirHorizontal == 4){
                return 6;
            }
            else if (dirVertical == 3 && dirHorizontal == 5){
                return 7;
            }
            else if (dirVertical == 3 && dirHorizontal == 0){
                return 3;
            }
            else if (dirVertical == 0 && dirHorizontal == 5){
                return 5;
            }
        }
        return 0;
    }

    public void getLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "getLocation: stopping the location service.");

            return;
        }
        Log.d(LOG_TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy,locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(LOG_TAG, "=== Location ===");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {

                            Log.d(LOG_TAG, " get latitude:  " + location.getLatitude());
                            Log.d(LOG_TAG, "get longitude:  " + location.getLongitude());

                            drawSteps(location.getLatitude(), location.getLongitude());
                        }
                    }
                },
                Looper.myLooper());     // Looper.myLooper tells this to repeat forever until thread is destroyed
    }


    public void pictureStop() {
        Log.d(LOG_TAG, "************stop");
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    //переводим dp в пиксели
    public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    // Узнаем размеры экрана из ресурсов
    public void screenSizeDisplay() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        pointX = displaymetrics.widthPixels / 2;
        pointY = displaymetrics.heightPixels / 2;

        Log.d(LOG_TAG, "size w = " + displaymetrics.widthPixels);
        Log.d(LOG_TAG, "size h = " + displaymetrics.heightPixels);

        pointResX = pointX;
        pointResY = pointY;


        Log.d(LOG_TAG, "pointX1 = " + pointX);
        Log.d(LOG_TAG, "pointY1 = " + pointY);

    }
}