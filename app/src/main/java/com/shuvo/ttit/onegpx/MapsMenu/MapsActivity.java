package com.shuvo.ttit.onegpx.MapsMenu;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shuvo.ttit.onegpx.R;
import com.shuvo.ttit.onegpx.arrayLists.ArrrayFile;
import com.shuvo.ttit.onegpx.createMenu.MapsActivityCreate;
import com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti;
import com.shuvo.ttit.onegpx.fileChooser.FileUtils;
import com.shuvo.ttit.onegpx.gpxConverter.GPXFileDecoder;
import com.shuvo.ttit.onegpx.login.Login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.shuvo.ttit.onegpx.gpxConverter.DistanceCalculator.CalculationByDistance;
import static com.shuvo.ttit.onegpx.login.Login.dateUptoFromDB;
import static com.shuvo.ttit.onegpx.login.Login.userDesignations;
import static com.shuvo.ttit.onegpx.login.Login.userInfoLists;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
//    private FragmentFileChooser fragment;
    private Button buttonShowInfo;
    Button normal, terrain, satellite, hybrid;
    Button create, createMulti;
    private Button shareFile;
    public static ArrayList<Location> wptList;

    public static ArrayList<ArrrayFile> multiGpxList;
    public static Boolean editOrNot = false;
    String fileLocation;
    File myFile;

    ImageView logout;
    TextView userName;
    private Boolean dateVerification = false;

    LocationManager lm;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    long timeInMilliSeconds = 0;
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;

    TextView timeLeftText;

    TextInputEditText buttonBrowse;
    TextInputLayout editTextPath;

    private ActivityResultLauncher<Intent> fileChooserLauncher;
    Uri file_uri;

    Logger logger = Logger.getLogger(MapsActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        View navScrim = findViewById(R.id.nav_bar_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_page_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            ViewGroup.LayoutParams lp = navScrim.getLayoutParams();
            lp.height = systemBars.bottom;
            navScrim.setLayoutParams(lp);
            return insets;
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
//        FragmentManager fragmentManager = this.getSupportFragmentManager();

        editTextPath = findViewById(R.id.editTextTextPersonName);
        buttonBrowse = findViewById(R.id.button);
//        fragment = (FragmentFileChooser) fragmentManager.findFragmentById(R.id.fragment);

        buttonShowInfo = findViewById(R.id.track_view);
        timeLeftText = findViewById(R.id.subscription_time);
        normal = findViewById(R.id.normalView);
        terrain = findViewById(R.id.terrainView);
        satellite = findViewById(R.id.satelliteView);
        hybrid = findViewById(R.id.hybridView);
        create = findViewById(R.id.edit_track);
        createMulti = findViewById(R.id.create_multi_track);
        shareFile = findViewById(R.id.share_file);
        logout = findViewById(R.id.log_out_icon_main_menu);
        userName = findViewById(R.id.user_full_name);
        lm =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!userInfoLists.isEmpty()) {
            String firstname = userInfoLists.get(0).getUser_fname();
            String lastName = userInfoLists.get(0).getUser_lname();
            if (firstname == null) {
                firstname = "";
            }
            if (lastName == null) {
                lastName = "";
            }

            String district = userInfoLists.get(0).getDistrict_name();
            String thana = userInfoLists.get(0).getThana_name();

            if (district == null) {
                district = "";
            }

            if (thana == null) {
                thana = "";
            }
            String empFullName;
            if (district.isEmpty() && thana.isEmpty()) {
                empFullName = firstname+" "+lastName + " ( )" ;
            } else if (district.isEmpty()){
                empFullName = firstname+" "+lastName + " ("+thana +")" ;
            } else if (thana.isEmpty()) {
                empFullName = firstname+" "+lastName + " ("+district +")" ;
            } else {
                empFullName = firstname+" "+lastName + " ("+thana +", "+ district+")";
            }

            userName.setText(empFullName);
        }

        wptList = new ArrayList<>();
        multiGpxList = new ArrayList<>();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        create.setOnClickListener(v -> {
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                System.out.println(gps_enabled);
                Log.i("GPS", String.valueOf(gps_enabled));
            } catch(Exception ex) {
                logger.log(Level.WARNING,ex.getMessage(),ex);
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                System.out.println(network_enabled);
                Log.i("Network", String.valueOf(network_enabled));
            } catch(Exception ex) {
                logger.log(Level.WARNING,ex.getMessage(),ex);
            }

            if(!gps_enabled && !network_enabled) {
                System.out.println(gps_enabled);
                Log.i("GPS1", String.valueOf(gps_enabled));
                Log.i("Network1", String.valueOf(network_enabled));
                System.out.println(network_enabled);
                // notify user
                Toast.makeText(getApplicationContext(),"Please switch your GPS on!!", Toast.LENGTH_SHORT).show();

            } else {
                //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(), MapsActivityCreate.class);
                startActivity(in);
            }
        });

        createMulti.setOnClickListener(v -> {
            if (dateVerification) {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//                try {
//                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                    System.out.println(gps_enabled);
//                    Log.i("GPS", String.valueOf(gps_enabled));
//                } catch(Exception ex) {
//                    logger.log(Level.WARNING,ex.getMessage(),ex);
//                }

//                try {
//                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//                    System.out.println(network_enabled);
//                    Log.i("Network", String.valueOf(network_enabled));
//                } catch(Exception ex) {
//                    logger.log(Level.WARNING,ex.getMessage(),ex);
//                }

                if(!gps_enabled) {
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
                    materialAlertDialogBuilder.setMessage("You need to switch on your GPS to create track or waypoint. Please switch your GPS on!")
                            .setPositiveButton("OK",(dialog, which) -> enableGPS())
                            .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());
                    AlertDialog alert = materialAlertDialogBuilder.create();
                    alert.show();
                }
                else if (!network_enabled){
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
                    materialAlertDialogBuilder.setMessage("You need to switch on your Internet to get accurate data and save track or waypoint. Please switch your Internet on!")
                            .setPositiveButton("OK",(dialog, which) -> dialog.dismiss());
                    AlertDialog alert = materialAlertDialogBuilder.create();
                    alert.show();
                }
                else {
                    Intent in = new Intent(getApplicationContext(), MapsActivityForMulti.class);
                    startActivity(in);
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Your Subscription Runs Out. Please Subscribe For Getting Access",Toast.LENGTH_SHORT).show();
            }

        });

        logout.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
            builder1.setTitle("LOG OUT!")
                    .setMessage("Do you want to Log Out?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        userInfoLists.clear();
                        userDesignations.clear();
                        userInfoLists = new ArrayList<>();
                        userDesignations = new ArrayList<>();

                        if (timerRunning) {
                            countDownTimer.cancel();
                        }
                        Intent intent = new Intent(MapsActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                        //System.exit(0);
                    })
                    .setNegativeButton("NO", (dialog, which) -> {

                    });
            AlertDialog alert = builder1.create();
            alert.show();

        });

        buttonBrowse.setOnClickListener(v -> openFileChooser());

        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            // Handle the URI
                            handleFileUri(uri);
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dateUptoFromDB != null) {
            if (!dateUptoFromDB.isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a", Locale.getDefault());

                Date date = null;
                try {
                    date = simpleDateFormat.parse(dateUptoFromDB);
                } catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }

                Date nowDate = Calendar.getInstance().getTime();

                if (nowDate.equals(date) || nowDate.after(date)) {
                    dateVerification = false;
                    //timeLeftText.setVisibility(View.VISIBLE);
                    timeLeftText.setVisibility(View.GONE);
                    String tlt = "TIME LEFT: 00S";
                    timeLeftText.setText(tlt);
                } else {
                    assert date != null;
                    timeInMilliSeconds = date.getTime() - nowDate.getTime();

                    dateVerification = true;
                    //timeLeftText.setVisibility(View.VISIBLE);
                    timeLeftText.setVisibility(View.GONE);
                    timerRunning = true;
                    startTimer();

                }
            } else {
                timerRunning = false;
                dateVerification = true;
                timeLeftText.setVisibility(View.GONE);
            }
        } else {
            timerRunning = false;
            dateVerification = true;
            timeLeftText.setVisibility(View.GONE);
        }

        System.out.println("RESUMED");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timerRunning) {
            countDownTimer.cancel();
        }

        System.out.println("PAUSED");
    }

    public void startTimer() {

        countDownTimer = new CountDownTimer(timeInMilliSeconds, 1000) {
            @Override
            public void onTick(long l) {
                timeInMilliSeconds = l;
                UpdateTimer();
            }

            @Override
            public void onFinish() {

                dateVerification = false;
                String tlt = "TIME LEFT: 00S";
                timeLeftText.setText(tlt);
                timerRunning = false;
            }
        }.start();
    }

    public void UpdateTimer() {
        long timeeeee = timeInMilliSeconds;
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = timeeeee / daysInMilli;
        timeeeee = timeeeee % daysInMilli;

        long elapsedHours = timeeeee / hoursInMilli;
        timeeeee = timeeeee % hoursInMilli;

        long elapsedMinutes = timeeeee / minutesInMilli;
        timeeeee = timeeeee % minutesInMilli;

        long elapsedSeconds = timeeeee / secondsInMilli;

        String timeLeft = "TIME LEFT: ";
        if (elapsedDays != 0) {
            if (elapsedDays < 10) {
                timeLeft += "0";
            }
            timeLeft += elapsedDays+"D";
            timeLeft += ": ";

        }

        if (elapsedHours != 0) {
            if (elapsedHours < 10 ) {
                timeLeft += "0";
            }
            timeLeft += elapsedHours + "H";
            timeLeft += ": ";
        }

        if (elapsedMinutes != 0) {
            if (elapsedMinutes < 10) {
                timeLeft += "0";
            }
            timeLeft += elapsedMinutes + "M";
            timeLeft += ": ";
        }

        if (elapsedSeconds < 10) {
            timeLeft += "0";
        }
        timeLeft += elapsedSeconds + "S";

        timeLeftText.setText(timeLeft);

        //System.out.println( elapsedDays+ "D:, "+ elapsedHours+" hours, "+ elapsedMinutes+" mins, "+ elapsedSeconds+ " seconds ");

    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent chooser = Intent.createChooser(intent, "Choose a file");
        fileChooserLauncher.launch(chooser);
    }

    private void handleFileUri(Uri uri) {
        // Do something with the URI
        Log.d("FileChooser", "Selected URI: " + uri.toString());
        file_uri = uri;
        String filePath = null;
        try {
            filePath = FileUtils.getPath(this,uri);
        } catch (Exception e) {
            Log.e("E_TAG","Error: " + e);
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
        if (filePath != null) {
            int sub = filePath.lastIndexOf("/");
            int all = filePath.length();
            System.out.println(sub);
            System.out.println(all);
            String subss = filePath.substring(sub + 1, all);
            System.out.println(subss);
            buttonBrowse.setText(subss);
            editTextPath.setHint("ফাইলের নাম");
            fileLocation = filePath;
        }
        else {
            filePath = FileUtils.copyUriToCache(this, uri);
            if (filePath != null) {
                int sub = filePath.lastIndexOf("/");
                int all = filePath.length();
                System.out.println(sub);
                System.out.println(all);
                String subss = filePath.substring(sub + 1, all);
                System.out.println(subss);
                buttonBrowse.setText(subss);
                editTextPath.setHint("ফাইলের নাম");
                fileLocation = filePath;
            }
        }
    }

    private void showInfo()  {
        if (fileLocation != null) {
            myFile = new File(fileLocation);
            System.out.println(myFile);
            System.out.println(fileLocation);
        }

        //String path = this.fragment.getPath();
        //Toast.makeText(this, "Path: " + path, Toast.LENGTH_LONG).show();
    }

    public void onNormalMap(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void onSatelliteMap(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void onTerrainMap(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void onHybridMap(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng bangladesh = new LatLng(23.6850, 90.3563);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Bangladesh"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangladesh, 7));

        buttonShowInfo.setOnClickListener(v -> {
            showInfo();
            mMap.clear();
            if (fileLocation != null && fileLocation.contains(".gpx")) {
                shareFile.setVisibility(View.VISIBLE);
                //create.setVisibility(View.VISIBLE);
                Log.i("Ekhane ki", "Ashe??");
                try {
                    InputStream in = getContentResolver().openInputStream(file_uri);
                    wptList = GPXFileDecoder.decodeWPT(in);
                } catch (FileNotFoundException e) {
                    System.out.println(e.getLocalizedMessage());
                }
                try {
                    InputStream in = getContentResolver().openInputStream(file_uri);
                    multiGpxList = GPXFileDecoder.multiLine(in);
                } catch (FileNotFoundException e) {
                    System.out.println(e.getLocalizedMessage());
                }

                if (multiGpxList.isEmpty() && wptList.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Please select valid file",Toast.LENGTH_SHORT).show();
                } else {

                    if (wptList.size() == 1) {
                        Log.i("Ekhane", "1 ta");
                        for (int i = 0; i< wptList.size(); i++) {
                            LatLng wpt = new LatLng(wptList.get(i).getLatitude(), wptList.get(i).getLongitude());
                            mMap.addMarker(new MarkerOptions().position(wpt).title("TTIT Project"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wpt, 18));
                        }
                    }else {
                        Log.i("Ekhane", "Onek");
                        for (int i = 0; i< wptList.size(); i++) {
                            LatLng wpt = new LatLng(wptList.get(i).getLatitude(), wptList.get(i).getLongitude());
                            mMap.addMarker(new MarkerOptions().position(wpt).title("TTIT Project"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wpt, 14));
                        }
                    }



                    String total = "";
                    try {
                        InputStream in = getContentResolver().openInputStream(file_uri);
                        total = GPXFileDecoder.decoder(in);
                    } catch (FileNotFoundException e) {
                        System.out.println(e.getLocalizedMessage());
                    }

                    if (!total.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Total: " + total , Toast.LENGTH_LONG).show();
                    }

                    for (int a = 0; a < multiGpxList.size(); a++) {
                        ArrayList<Location> gpxList = multiGpxList.get(a).getMyLatlng();
                        String lengthh = multiGpxList.get(a).getDescc();
                        System.out.println(lengthh);

                        int index = lengthh.indexOf(" ");
                        int index2 = lengthh.indexOf(" KM");
                        if (index2 < 0) {
                            index2 = lengthh.indexOf(" km");
                        }
                        String substr;
                        if (index < 0 || index2 < 0) {
                            substr = "0";
                        } else {
                            Log.i("Index of 1st:", String.valueOf(index));
                            Log.i("Index of 2nd:", String.valueOf(index2));
                            substr=lengthh.substring(index + 1, index2);
                            //System.out.println(substr);
                        }

                        MarkerOptions options = new MarkerOptions();

                        PolylineOptions option = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true).clickable(true).zIndex(Float.parseFloat(substr));
                        for (int z = 0; z < gpxList.size(); z++) {
                            LatLng point = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                            option.add(point);
                        }


                        mMap.addPolyline(option);



                        Double j = 0.0;

                        for (int i = 0; i< gpxList.size(); i++) {

                            LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                            //markerPoints.add(gpx);
                            options.position(gpx);

                            if (i == 0 ) {
                                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_start));
                                options.anchor((float) 0.5,(float) 0.5);
                                options.snippet("0 KM");
                                mMap.addMarker(options).setTitle("Starting Road");
                            }else if (i == gpxList.size()-1){
                                LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                                options.anchor((float) 0.5,(float) 0.5);
                                Double diss = CalculationByDistance(preGpx, gpx);
                                j  = j + diss;
                                options.snippet(String.format(Locale.ENGLISH,"%.3f", j) + " KM");
                                mMap.addMarker(options).setTitle("Ending Road");
                            } else {
                                LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.transparent_circle));
                                options.anchor((float) 0.5,(float) 0.5);
                                Double diss = CalculationByDistance(preGpx, gpx);
                                j  = j + diss;
                                options.snippet(String.format(Locale.ENGLISH,"%.3f", j) + " KM");
                                mMap.addMarker(options).setTitle("On Going Road");
                            }




                        }

                        int i = (gpxList.size() - 1) / 2;
                        LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 16));

                    }

                }

            }
            else if (fileLocation == null){
                Toast.makeText(getApplicationContext(), "প্রথমে ফাইল নির্বাচন করুন", Toast.LENGTH_SHORT).show();
            } else if (!fileLocation.contains(".gpx")) {
                Toast.makeText(getApplicationContext(), "অবৈধ ফাইল", Toast.LENGTH_SHORT).show();
            }

        });

        mMap.setOnPolylineClickListener(polyline -> {
            System.out.println(polyline.getPoints());
            Snackbar.make(findViewById(R.id.layout_for), "Length: " + polyline.getZIndex() + " KM", Snackbar.LENGTH_LONG)
                    .setDuration(10000)
                    .show();
        });


        shareFile.setOnClickListener(v -> {

            showInfo();
            if (fileLocation != null && fileLocation.contains(".gpx")) {
                File file = new File(fileLocation);
                Uri contentUri = FileProvider.getUriForFile(
                        getApplicationContext(),
                        "com.example.android.shuvoCameraProviderGPX",
                        file
                );
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("*/*");
                intentShare.putExtra(Intent.EXTRA_STREAM, contentUri);
                intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));

                startActivity(Intent.createChooser(intentShare, "Share the File..."));

            }else if (fileLocation != null && !fileLocation.contains(".gpx")) {
                Toast.makeText(getApplicationContext(), "This File is Invalid", Toast.LENGTH_SHORT).show();
            } else if (fileLocation == null) {
                Toast.makeText(getApplicationContext(), "File is not Selected", Toast.LENGTH_SHORT).show();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("LOG OUT!")
                        .setMessage("Do you want to Log Out?")
                        .setPositiveButton("YES", (dialog, which) -> {


                            userInfoLists.clear();
                            userDesignations.clear();
                            userInfoLists = new ArrayList<>();
                            userDesignations = new ArrayList<>();
                            if (timerRunning) {
                                countDownTimer.cancel();
                            }
                            Intent intent = new Intent(MapsActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                            //System.exit(0);
                        })
                        .setNegativeButton("NO", (dialog, which) -> {

                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void enableGPS() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(2000)
                .build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {});

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MapsActivity.this,
                            1000);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("Hoise ", "1");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Hoise ", "2");
            }
        }
    }
}