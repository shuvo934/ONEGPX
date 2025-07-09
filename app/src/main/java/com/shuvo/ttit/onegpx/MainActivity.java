package com.shuvo.ttit.onegpx;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shuvo.ttit.onegpx.login.Login;

public class MainActivity extends AppCompatActivity{

    TextView info;
    Button quit;
    private final Handler mHandler = new Handler();
    boolean perm = false;

    private final ActivityResultLauncher<String[]> locationResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        System.out.println("OnActivityResult: " +result);
        boolean allGranted = true;
        for (String key: result.keySet()) {
            allGranted = allGranted && Boolean.TRUE.equals(result.get(key));
        }
        if (allGranted) {
            System.out.println("HOLA1");
            goToActivityMap();
        }
        else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showDialog("Location Permission!", "This app needs the precise location permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                    perm = true;
                });
            }
            else {
                System.out.println("HOLA2");
                enableLocation();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        quit = findViewById(R.id.quit);
        quit.setVisibility(View.GONE);
        info = findViewById(R.id.permission_info);

        hideSystemUI();

        perm = false;
        enableLocation();

        quit.setOnClickListener(v -> System.exit(0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (perm) {
            perm = false;
            enableLocation();
        }
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Android 4.4 to 10 (API 19–29)
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void goToActivityMap() {
        mHandler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }, 2000);
    }

    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Log.i("Ekhane", "1");

            goToActivityMap();

        }
        else {
            Log.i("Ekhane", "2");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Log.i("Ekhane", "3");
                showDialog("Location Permission!", "This app needs the location permission for functioning.", "OK", (dialogInterface, i) -> locationResultLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}));
            }
            else {
                Log.i("Ekhane", "4");
                locationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION});
            }
        }
    }

    public void showDialog(String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveListener) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setIcon(R.drawable.activity_icon3)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, positiveListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}