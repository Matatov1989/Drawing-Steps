package com.sergeant_matatov.drawingsteps;

import android.content.Context;
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
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Yurka on 11.10.2016.
 */

public class PictureView extends View implements LocationListener {

    final String LOG_TAG = "myLogs";

    PictureView view;
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location;      // location
    public LocationListener locationListener;   //для работы GPS
    String msg;

    // Declaring a Location Manager
    protected LocationManager locationManager;

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

    int pointX, pointY;
    int pointX1, pointY1;
    double pointResX = 0.0, pointResY = 0.0;
    double pointFirstX = 0.0, pointFirstY = 0.0;
    double pointLastX = 0, pointLastY = 0;

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

        invalidate();//перерисовываем канвас
    }

    public void pictureStart() {
        Log.d(LOG_TAG, "picture");

        try {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled)
                msg = "error";
            else {
                this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    Log.d(LOG_TAG, "gps");
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000, 0, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        if (location != null)
                            this.onLocationChanged(location);
           //                 onLocationChanged(location);

                    }
                }
                // First get location from Network Provider
                else if (isNetworkEnabled) {
                    Log.d(LOG_TAG, "net");
                    locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        if (location != null)
                            this.onLocationChanged(location);
                //            onLocationChanged(location);
              //              drawSteps(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getCoordinates() {
        try {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                msg = "error";

            } else if (isGPSEnabled && isNetworkEnabled)
            {
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        locationListener.onLocationChanged(location);
                    }
                } else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 2000, 0, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        locationListener.onLocationChanged(location);
                    }
                } else {
                    msg = "error";
                }
            } else if (isGPSEnabled) {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 0, locationListener);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                    locationListener.onLocationChanged(location);
                } else {
                    locationListener.onProviderDisabled(location.getProvider());
                }
            } else if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 2000, 0, locationListener);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                    locationListener.onLocationChanged(location);
                } else
                    locationListener.onProviderDisabled(location.getProvider());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 //       locationManager.removeUpdates(locationListener);
    }

    public void pictureStop() {
        Log.d(LOG_TAG, "************stop");
        locationManager.removeUpdates(this);
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "************onLocationChanged");
        if (location != null)
            drawSteps(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(LOG_TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOG_TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(LOG_TAG, "onProviderDisabled");
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
