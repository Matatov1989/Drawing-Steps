package com.sergeant_matatov.drawingsteps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import static com.sergeant_matatov.drawingsteps.R.id.fab;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";
    boolean checkEnable = false;

    PictureView picView;

    Snackbar snackbar;


    public static final int MULTIPLE_PERMISSIONS = 1; // code you want.

    String[] permissions = new String[]{

            Manifest.permission.ACCESS_FINE_LOCATION,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);

        checkPermissions();

        picView = (PictureView) findViewById(R.id.game_view);

        final FloatingActionButton fabBtnStartStop = (FloatingActionButton) findViewById(R.id.fabBtnStartStop);
        fabBtnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkEnable) {

                    picView.getLocation();
                    fabBtnStartStop.setImageResource(android.R.drawable.ic_media_pause);
                    checkEnable = true;
                } else {
                    picView.pictureStop();
                    fabBtnStartStop.setImageResource(android.R.drawable.ic_media_play);
                    checkEnable = false;

                    snackbar = Snackbar
                            .make(view, getString(R.string.strSnakeBar), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.btnSnakeBarYes), snackbarOnClickListener)
                            .setCallback(new Snackbar.Callback()
                            {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    // do some action on dismiss
                                    //сделали скрин
                                    Bitmap bitmap = Screenshot.getInstance().takeScreenshotForScreen(MainActivity.this);
                                    //сохранили скрин на sd карте
                                    String path = Environment.getExternalStorageDirectory().toString();
                                    String nemeP = FileUtils.getInstance().storeBitmap(bitmap, path, getApplicationContext());

                                    // Create the new Intent using the 'Send' action.
                                    Intent intent = new Intent(Intent.ACTION_SEND);

                                    intent.putExtra(Intent.EXTRA_TEXT, "Drawing Steps");
                                    intent.setType("text/plain");
                                    // Set the MIME type
                                    intent.setType("image/*");

                                    // Create the URI from the media
                                    File media = new File(path + "/" + nemeP);
                                    Uri uri = Uri.fromFile(media);

                                    // Add the URI to the Intent.
                                    intent.putExtra(Intent.EXTRA_STREAM, uri);

                                    // Broadcast the Intent.
                                    startActivity(Intent.createChooser(intent, "Share to"));
                                }

                                @Override
                                public void onShown(Snackbar snackbar) {
                                    // do some action when snackbar is showed
                                }
                            });
                    snackbar.show();
                }
            }
        });
    }

    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

      //      snackbar.dismiss();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    //              FLAGPERMISSION = true;
                } else {
                    // no permissions granted.
                    //              FLAGPERMISSION = false;
                }
                return;
            }
        }
    }
}
