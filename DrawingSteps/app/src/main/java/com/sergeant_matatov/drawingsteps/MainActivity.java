package com.sergeant_matatov.drawingsteps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {

//    final String LOG_TAG = "myLogs";

    boolean checkEnable = false;

    PictureView picView;
    FloatingActionButton fabBtnStartStop;
    public static final int CODE_LOCATION = 1;
    public static final int CODE_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);

        //     Log.d(LOG_TAG, "---onCreate---");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        picView = (PictureView) findViewById(R.id.game_view);

        fabBtnStartStop = (FloatingActionButton) findViewById(R.id.fabBtnStartStop);
        fabBtnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start or stop drawing (location)
                if (!checkEnable)
                    checkPermissionLocation();
                else {
                    picView.pictureStop();
                    checkEnable = false;
                    fabBtnStartStop.setImageResource(android.R.drawable.ic_media_play);
                    dialogScreenshot();
                }
            }
        });
    }

    private void dialogScreenshot() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage(getString(R.string.textDialog));
        adb.setPositiveButton(R.string.btnYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                checkPermissionStorage();
            }
        });
        adb.setNegativeButton(R.string.btnNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getSupportActionBar().show();
            }
        });
        adb.show();
    }

    //create and save a screenshot
    private void createScreenshot() {
        //create a screenshot
        Bitmap bitmap = Screenshot.getInstance().takeScreenshotForScreen(MainActivity.this);
        //save a screenshot
        String pathScreen = Environment.getExternalStorageDirectory().toString();
        FileUtils.getInstance().storeBitmap(bitmap, pathScreen, getApplicationContext());

        getSupportActionBar().show();

        Toast.makeText(MainActivity.this, R.string.toastScreenshot, Toast.LENGTH_LONG).show();
    }

    //dialog with list workers (I am and my brathers)
    //user can to write message on worker
    private void dialogDevelopers() {
        final String[] developers = getResources().getStringArray(R.array.arrWorkers);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.actionBtnDevelopers));
        adb.setItems(developers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                switch (item) {
                    case 0:
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Matatov1989@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Remote Secyrity Phone");
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.toastSendMail)));
                        dialog.dismiss();
                        break;
                    case 1:
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Docmat63@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Remote Secyrity Phone");
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.toastSendMail)));
                        dialog.dismiss();
                        break;
                }
            }
        });
        adb.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_developers:        //dialog developers
                dialogDevelopers();
                break;

            case R.id.action_from_developer:    //from developers
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Yury%20Matatov&hl"));
                startActivity(intent);
                break;

            case R.id.action_advise_friend:     //advise a program to friend
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.sergeant_matatov.drawingsteps&hl");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.action_feedback:      //feedback a programm
                Intent intentFeedback = new Intent(Intent.ACTION_VIEW);
                intentFeedback.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sergeant_matatov.drawingsteps&hl"));
                startActivity(intentFeedback);
                break;

        /*    case R.id.action_privacy_policy:
                startActivity(new Intent(MainActivity.this, PrivacyPolicy.class));
                break;*/

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        exit(0);
    }

    private void checkPermissionStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                dialogPermissionStorage();

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_STORAGE);
            }
        } else {
            createScreenshot();
        }
    }


    private void checkPermissionLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                dialogPermissionLocation();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, CODE_LOCATION);
            }
        } else {
            picView.getLocation();
            getSupportActionBar().hide();
            fabBtnStartStop.setImageResource(android.R.drawable.ic_media_pause);
            checkEnable = true;
        }
    }

    private void dialogPermissionLocation() {
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setMessage(R.string.dialogPermissionLocation);
        adb.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, CODE_LOCATION);
                dialog.dismiss();
            }
        });
        adb.show();
    }

    private void dialogPermissionStorage() {
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setMessage(R.string.dialogPermissionLocation);
        adb.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_STORAGE);
                dialog.dismiss();
            }
        });
        adb.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    picView.getLocation();
                    getSupportActionBar().hide();

                    fabBtnStartStop.setImageResource(android.R.drawable.ic_media_pause);
                    checkEnable = true;
                } else {
                    checkPermissionLocation();
                }
                return;
            }

            case CODE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    createScreenshot();

                } else {
                    checkPermissionStorage();
                }
                return;
            }
        }
    }
}