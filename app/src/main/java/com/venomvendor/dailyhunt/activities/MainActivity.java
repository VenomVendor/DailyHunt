package com.venomvendor.dailyhunt.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.network.NetworkHandler;

public class MainActivity extends BaseActivity {

    private static final int RUNTIME_PERMISSIONS_CODE = 255;
    private static final int UPDATE_PERMISSIONS = 254;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final String[] requiredPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isPermissionGranted()) {
            sendDataRequest();
        } else {
            showPermissions();
        }

    }

    @Override
    protected int contentView() {
        return R.layout.activity_main;
    }


    @Override
    protected boolean isDrawerEnabled() {
        return true;
    }

    private void sendDataRequest() {
        Log.d(TAG, "sendDataRequest");
        NetworkHandler.getInstance().getPosts();
        NetworkHandler.getInstance().getApiCount();
    }

    private boolean isPermissionGranted() {
        return Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission(this, requiredPermissions[0])
                        == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissions() {
        //Explain user if required.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            //Educate User
            showCustomDialog("Required Permission", "External storage access is required to cache " +
                            "images for your smooth experience.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //ask permissions.
                            ActivityCompat.requestPermissions(MainActivity.this, requiredPermissions,
                                    RUNTIME_PERMISSIONS_CODE);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            deadLock();
                        }
                    }
            );

        } else {
            //ask permissions.
            ActivityCompat.requestPermissions(this, requiredPermissions, RUNTIME_PERMISSIONS_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case RUNTIME_PERMISSIONS_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendDataRequest();
                } else {
                    //Disable the functionality.
                    permissionsDenied();
                }
            }
        }
    }

    private void permissionsDenied() {
        showCustomDialog("Required Permission", "DailyHunt requires access to \"External Storage\"" +
                " to load images.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, UPDATE_PERMISSIONS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int ignoreResultCode, Intent data) {
        super.onActivityResult(requestCode, ignoreResultCode, data);

        //don't worry about result code.
        if (requestCode == UPDATE_PERMISSIONS) {
            if (isPermissionGranted()) {
                sendDataRequest();
            } else {
                deadLock();
            }
        }
    }

    private void deadLock() {
        showCustomDialog("Permissions Revoked", "Without External Storage permission, app cannot run.\n" +
                        " App will exit on press of \"OK\".", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                }
        );
    }

}
