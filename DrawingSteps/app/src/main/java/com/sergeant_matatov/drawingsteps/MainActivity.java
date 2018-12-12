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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        checkPermissions();

        picView = (PictureView) findViewById(R.id.game_view);

        final FloatingActionButton fabBtnStartStop = (FloatingActionButton) findViewById(R.id.fabBtnStartStop);
        fabBtnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkEnable) {
                    getSupportActionBar().hide();
                    picView.getLocation();
                    fabBtnStartStop.setImageResource(android.R.drawable.ic_media_pause);
                    checkEnable = true;
                } else {
                    getSupportActionBar().show();
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
