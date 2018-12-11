package com.sergeant_matatov.drawingsteps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Yurka on 11.10.2016.
 */

public class PictureView extends View {

    final String LOG_TAG = "myLogs";

    PictureView view;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint paint, mBitmapPaint;
    private float canvasSize;
    private final int horizontalCountOfCells, verticalCountOfCells;

    private final ScaleGestureDetector scaleGestureDetector;
    private final int viewSize;
    private float mScaleFactor;

    private final GestureDetector detector;

    private boolean isLock = false;

    int pointX1, pointY1;
    int pointX2, pointY2;

    double locationFirstX = 0.0, locationFirstY = 0.0;
    double locationSecondX = 0.0, locationSecondY = 0.0;

    double pointResX = 0.0, pointResY = 0.0;


    int resX;
    int resY;

    String strResX;
    String strResY;


    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        //    pointX = displaymetrics.widthPixels / 2;
        //    pointY = displaymetrics.heightPixels / 2;

        //размер игрового поля
        horizontalCountOfCells = 10;
        verticalCountOfCells = 10;
        //в xml разметке позднее пропишем размер вьюхи равный 300dp
        viewSize = (int) convertDpToPixel(displaymetrics.heightPixels, context);
        mScaleFactor = 1f;//значение зума по умолчанию
        canvasSize = (int) (viewSize * mScaleFactor);//определяем размер канваса

        //вытаскиваем размер экрана
        screenSizeDisplay();

        mBitmap = Bitmap.createBitmap((int) canvasSize, (int) canvasSize, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        //определяем параметры кисти, которой будем рисовать сетку и атомы
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xffff0505);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        scaleGestureDetector = new ScaleGestureDetector(context, new MyScaleGestureListener());

        detector = new GestureDetector(context, new MyGestureListener());

    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);//зумируем канвас
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }


    public String formatDoubleNum(double num1, double num2) {
        Log.d(LOG_TAG, "num1 = " + num1);
        Log.d(LOG_TAG, "num2 = " + num2);
        String str = String.format("%1$.7f", (num1 - num2));
        Log.d(LOG_TAG, "format = " + str);

        return String.format("%1$.7f", (num1 - num2));
    }

    public void drawSteps(double lat, double lon) {
        Log.d(LOG_TAG, "drawSteps");
        Log.d(LOG_TAG, "lat = " + lat);
        Log.d(LOG_TAG, "lon = " + lon);



       if (locationFirstX == 0 && locationFirstY == 0){
           locationFirstX = lat;
           locationFirstY = lon;

           locationSecondX = lat;
           locationSecondY = lon;
           mCanvas.drawPoint(pointX1, pointY1, paint);
       }
       else {

           locationFirstX = lat;
           locationFirstY = lon;

           getDirection();

       }



      /*
        if (pointFirstX == 0 && pointFirstY == 0) {
            pointFirstX = lat;
            pointFirstY = lon;
            Log.d(LOG_TAG, "if pointFirstX " + pointFirstX);
            mCanvas.drawPoint(pointX, pointY, paint);

        } else {
            strResX = formatDoubleNum(pointFirstX, lat);
            strResY = formatDoubleNum(pointFirstY, lon);
            //     Log.d(LOG_TAG, "***** 2 point rex else pointResX " + strResX + " len " + strResX.length());
            //x
            int start = 0;
            int end = strResX.length();
            char[] buf = new char[strResX.length()];
            strResX.getChars(start, end, buf, 0);

            String strTemp = buf[strResX.length() - 2] + "" + buf[strResX.length() - 1];
            resX = Integer.parseInt(strTemp);

            //y
            start = 0;
            end = strResY.length();
            buf = new char[strResY.length()];
            strResY.getChars(start, end, buf, 0);


            strTemp = buf[strResY.length() - 2] + "" + buf[strResY.length() - 1];
            resY = Integer.parseInt(strTemp);

            Log.d(LOG_TAG, "resX = " + resX);
            Log.d(LOG_TAG, "resY = " + resY);
            if (resX >= 6) {
                if (strResX.contains("-")) {
                    pointX1 = pointX - 2;
                } else {
                    pointX1 = pointX + 2;
                }
                pointFirstX = lat;
            } else {
                pointX1 = pointX;
                pointFirstX = pointFirstX;
            }


            if (resY >= 6) {
                if (strResY.contains("-")) {
                    pointY1 = pointY - 2;
                } else {
                    pointY1 = pointY + 2;
                }
                pointFirstY = lon;
            } else {
                pointY1 = pointY;
                pointFirstY = pointFirstY;
            }

            //    Log.d(LOG_TAG, "v lat = "+lat+" lon = "+lon);
            // рисуем линию
            mCanvas.drawLine(pointX, pointY, pointX1, pointY1, paint);
            pointX = pointX1;
            pointY = pointY1;

        }
*/
        invalidate();//перерисовываем канвас
    }

    private int getDirection(){

        if (locationFirstX <= locationSecondX){

        }
        else  if (locationFirstX >= locationSecondX){

        }

        if (locationFirstY <= locationSecondY){

        }
        else  if (locationFirstY >= locationSecondY){

        }

        return 0;
    }


    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */

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
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(LOG_TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {

                            Log.d(LOG_TAG, " get latitude:  " + location.getLatitude());
                            Log.d(LOG_TAG, "get longitude:  " + location.getLongitude());

                            drawSteps(location.getLatitude(), location.getLongitude());
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }



    public void pictureStop() {
        Log.d(LOG_TAG, "************stop");
   //     locationManager.removeUpdates(this);
    }


    //переводим dp в пиксели
    public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public boolean isLock() {
        return isLock;
    }

    public void lock() {
        isLock = true;
    }

    public void unlock() {
        isLock = false;
    }

    //унаследовались от ScaleGestureDetector.SimpleOnScaleGestureListener, чтобы не писать пустую реализацию ненужных методов интерфейса OnScaleGestureListener
    private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        //обрабатываем "щипок" пальцами
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();//получаем значение зума относительно предыдущего состояния
            //получаем координаты фокальной точки - точки между пальцами
            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();
            //следим чтобы канвас не уменьшили меньше исходного размера и не допускаем увеличения больше чем в 2 раза
            if (mScaleFactor * scaleFactor > 1 && mScaleFactor * scaleFactor < 2) {
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                canvasSize = viewSize * mScaleFactor;//изменяем хранимое в памяти значение размера канваса
                //используется при расчетах
                //по умолчанию после зума канвас отскролит в левый верхний угол. Скролим канвас так, чтобы на экране оставалась обасть канваса, над которой был
                //жест зума
                //Для получения данной формулы достаточно школьных знаний математики (декартовы координаты).
                int scrollX = (int) ((getScrollX() + focusX) * scaleFactor - focusX);
                scrollX = Math.min(Math.max(scrollX, 0), (int) canvasSize - viewSize);
                int scrollY = (int) ((getScrollY() + focusY) * scaleFactor - focusY);
                scrollY = Math.min(Math.max(scrollY, 0), (int) canvasSize - viewSize);
                scrollTo(scrollX, scrollY);
            }
            //вызываем перерисовку принудительно
            invalidate();
            return true;
        }
    }

    //унаследовались от GestureDetector.SimpleOnGestureListener, чтобы не писать пустую
    //реализацию ненужных методов интерфейса OnGestureListener
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //обрабатываем скролл (перемещение пальца по экрану)
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //не даем канвасу показать края по горизонтали
            if (getScrollX() + distanceX < canvasSize - viewSize && getScrollX() + distanceX > 0) {
                scrollBy((int) distanceX, 0);
            }
            //не даем канвасу показать края по вертикали
            if (getScrollY() + distanceY < canvasSize - viewSize && getScrollY() + distanceY > 0) {
                scrollBy(0, (int) distanceY);
            }
            return true;
        }

        //обрабатываем одиночный тап
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            if (isLock) return true;
            //получаем координаты ячейки, по которой тапнули
            float eventX = (event.getX() + getScrollX()) / mScaleFactor;
            float eventY = (event.getY() + getScrollY()) / mScaleFactor;
            //        logic.addAtom((int)(horizontalCountOfCells *eventX/viewSize), (int)(verticalCountOfCells *eventY/viewSize));
            return true;
        }

        //обрабатываем двойной тап
        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            //зумируем канвас к первоначальному виду
            mScaleFactor = 1f;
            canvasSize = viewSize;
            scrollTo(0, 0);//скролим, чтобы не было видно краев канваса.
            invalidate();//перерисовываем канвас
            return true;
        }
    }
/*
    public void setLogic(GameLogic logic) {
        this.logic = logic;
    }
*/

    // Узнаем размеры экрана из ресурсов
    public void screenSizeDisplay() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        pointX = displaymetrics.widthPixels / 2;
        pointY = displaymetrics.heightPixels / 2;

        Log.d(LOG_TAG, "size w = " + displaymetrics.widthPixels);
        Log.d(LOG_TAG, "size h = " + displaymetrics.heightPixels);

    }
}
