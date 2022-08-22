package com.shuvo.ttit.onegpx.createMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shuvo.ttit.onegpx.MapsMenu.MapsActivity;
import com.shuvo.ttit.onegpx.R;
import com.shuvo.ttit.onegpx.dialogue.ImageDialogue;
import com.shuvo.ttit.onegpx.dialogue.SaveMulti;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.shuvo.ttit.onegpx.gpxConverter.DistanceCalculator.CalculationByDistance;
import static com.shuvo.ttit.onegpx.login.Login.CameraDeactivate;
import static com.shuvo.ttit.onegpx.login.Login.TrackDeactivate;
import static com.shuvo.ttit.onegpx.login.Login.WayPointDeactivate;

public class MapsActivityForMulti extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public static String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    FusedLocationProviderClient fusedLocationProviderClient;
    FusedLocationProviderClient fusedLocationProviderClientCamera;

    private TextInputEditText search;
    private TextInputLayout searchLayout;
    private ImageButton button;
    private ImageButton camera;
    private Spinner selection;
    private Button drawLine, drawWayPoint, clearMap, back, autoLine, removeLast;
    private Button saveFile;
    private Button shareMulti;
    private Button instantWay;

    public static String length_multi = "";
    public static String fileName = null;
    public static Location targetLocation = null;

    private Boolean lineValue = true;
    private Boolean autoLineValue = false;
    private Boolean wayPointValue = false;
    private Boolean isStart = false;
    private Boolean isWayStart = false;
    private Boolean fromMap = false;
    private Boolean fromButton = false;
    String address = "";

    public static Bitmap bitmap = null;
    public static String imageFileName = "";


    private String lineStart = "0";

    public static ArrayList<LatLng> gpxLatLng;

    public static ArrayList<LatLng> autoGpxLatLng;

    public static ArrayList<LatLng> wpLatLng;

    public static ArrayList<String> trk;

    LocationManager locationManager;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    LocationRequest locationRequestCamera;
    LocationCallback locationCallbackCamera;

    final LatLng[] cameraLatLng = {null};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_for_multi);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClientCamera = LocationServices.getFusedLocationProviderClient(this);
        search = findViewById(R.id.search_bar_new_location_multi);
        searchLayout = findViewById(R.id.search_bar_new_location_multi_layout);
        button = findViewById(R.id.search_butt_multi);
        camera = findViewById(R.id.camera_button);

        if (CameraDeactivate == 0) {
            camera.setEnabled(true);
            camera.setImageAlpha(255);
            camera.setBackgroundResource(R.drawable.quit_button_design);
        } else if (CameraDeactivate == 1) {
            camera.setEnabled(false);
            camera.setImageAlpha(75);
            camera.setBackgroundResource(R.drawable.quit_button_design_2);
        }

        selection = findViewById(R.id.spinnnnn_multi);
        drawLine = findViewById(R.id.line_draw_save_multi);
        drawWayPoint = findViewById(R.id.waypoint_save_multi);
        clearMap = findViewById(R.id.map_clear_multi);
        back = findViewById(R.id.go_back_multi);
        autoLine = findViewById(R.id.auto_line_multi);
        removeLast = findViewById(R.id.last_point_remove_multi);
        saveFile = findViewById(R.id.save_gpx_file);
        shareMulti = findViewById(R.id.share_file_multi);
        instantWay = findViewById(R.id.instant_way);

        if (TrackDeactivate == 0) {
            autoLine.setEnabled(true);
            drawLine.setEnabled(true);
        } else if (TrackDeactivate == 1) {
            autoLine.setEnabled(false);
            drawLine.setEnabled(false);
        }

        if (WayPointDeactivate == 0) {
            instantWay.setEnabled(true);
            drawWayPoint.setEnabled(true);
        } else if (WayPointDeactivate == 1) {
            instantWay.setEnabled(false);
            drawWayPoint.setEnabled(false);
        }


        MapsActivity.editOrNot = false;
        gpxLatLng = new ArrayList<>();
        autoGpxLatLng = new ArrayList<>();
        wpLatLng = new ArrayList<>();
        trk = new ArrayList<>();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);

        locationRequestCamera = LocationRequest.create();
        locationRequestCamera.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequestCamera.setInterval(5000);
        locationRequestCamera.setFastestInterval(1000);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        List<String> categories = new ArrayList<String>();
        categories.add("NORMAL");
        categories.add("SATELLITE");
        categories.add("TERRAIN");
        categories.add("HYBRID");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, categories);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        selection.setAdapter(spinnerAdapter);

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                search.setText("");
                searchLayout.setHint("Zilla / Upazila");
                return false;
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                        event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        Log.i("Let see", "Come here");
                        closeKeyBoard();



                        return false; // consume.
                    }
                }
                return false;
            }
        });
    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        zoomToUserLocation();
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Variable for Draw Line (Manual Line)
        final int[] i = {0};
        final PolylineOptions[] option = {new PolylineOptions().width(5).color(Color.RED).geodesic(true)};
        MarkerOptions options = new MarkerOptions();
        final Marker[] mm = {null};
        final Polyline[] polylines = {null};
        final Double[] j = {0.0};
        final Double[] k = {0.0};
        final LatLng[] preLatLng = {new LatLng(0, 0)};
        final LatLng[] previousLatlng = {new LatLng(0, 0)};

        // Variable for Auto Line (Automatic Line)
        final PolylineOptions[] nop = {new PolylineOptions().width(5).color(Color.RED).geodesic(true)};
        MarkerOptions mp = new MarkerOptions();
        final int[] local = {0};
        final LatLng[] autoPreLatlng = {new LatLng(0, 0)};
        final LatLng[] lastLatLongitude = {new LatLng(0, 0)};
        final Double[] w = {0.0};

        // Variable for WayPoint (Marker)
        MarkerOptions wp = new MarkerOptions();
        final LatLng[] preWayPoint = {new LatLng(0, 0)};
        final Marker[] wwpp = {null};
        final LatLng[] instantWayLatLng = {new LatLng(0,0)};
        final LatLng[] checkLatLng = {new LatLng(0,0)};


        selection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                switch (name) {
                    case "NORMAL":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case "SATELLITE":
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case "TERRAIN":
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case "HYBRID":
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = null;

                location = search.getText().toString();
                Log.i("Hobe ", location);

                List<Address> addressList = null;

                if (!location.isEmpty()) {
                    Log.i("Hobe na  ", "hobe dekhi");
                    Geocoder geocoder = new Geocoder(MapsActivityForMulti.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                        System.out.println(addressList);

                    } catch (IOException e) {
                        e.printStackTrace();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
                        Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (addressList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
                    } else {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    }
                }
                if (search.isFocused()) {
                    search.clearFocus();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(button.getWindowToken(), 0);


            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (isWayStart) {
                        instantWayLatLng[0] = new LatLng(location.getLatitude(),location.getLongitude());
                    }
                    if (isStart) {

                        Log.i("LocationFused ", location.toString());
                        lastLatLongitude[0] = new LatLng(location.getLatitude(), location.getLongitude());
                        if (local[0] == 0) {

                            local[0]++;
                            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                            autoPreLatlng[0] = ll;
                            nop[0].add(ll);
                            mMap.addPolyline(nop[0]);
                            mp.position(ll);
                            mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                            mp.anchor((float) 0.5, (float) 0.5);
                            autoGpxLatLng.add(ll);
                            mp.snippet("0 KM");
                            mp.title("Starting Point");
                            mMap.addMarker(mp);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 18));
                        } else {

                            local[0]++;
                            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                            Double distance = CalculationByDistance(autoPreLatlng[0], ll);

                            Log.i("Distance", distance.toString());

                            if (distance >= 0.01) {

                                nop[0].add(ll);
                                mMap.addPolyline(nop[0]);
                                mp.position(ll);
                                mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                                mp.anchor((float) 0.5, (float) 0.5);
                                w[0] = w[0] + distance;
                                mp.snippet(String.format("%.3f", w[0]) + " KM");
                                mp.title("Road Point");
                                mMap.addMarker(mp);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 18));
                                autoGpxLatLng.add(ll);
                                autoPreLatlng[0] = ll;
                            }

                        }

                    }
                }
            }
        };

        autoLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lineStart.equals("0")) {
                    shareMulti.setVisibility(View.GONE);
                    drawLine.setVisibility(View.GONE);
                    drawWayPoint.setVisibility(View.GONE);
                    autoLine.setText("রাস্তা শুরু করুন / Start Track");
                    autoGpxLatLng = new ArrayList<>();
                    lineStart = "1";
                    Toast.makeText(getApplicationContext(),"আরও ভাল পজিশন পেতে অনুগ্রহ করে ৫ থেকে ১০ সেকেন্ড অপেক্ষা করুন",Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                } else if (!autoLineValue && lineStart.equals("1")) {

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    shareMulti.setVisibility(View.GONE);
                    drawLine.setVisibility(View.GONE);
                    drawWayPoint.setVisibility(View.GONE);
                    autoGpxLatLng = new ArrayList<>();
                    lineStart = "2";
                    isStart = true;
                    autoLine.setText("রাস্তা শেষ এবং সংরক্ষণ করুন / Stop and Save Track");
                    autoLineValue = true;
                    //fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    //locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
                    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                } else if (lineStart.equals("2") && autoLineValue) {
                    saveFile.setVisibility(View.VISIBLE);
                    drawLine.setVisibility(View.VISIBLE);
                    drawWayPoint.setVisibility(View.VISIBLE);
                    autoLine.setText("রাস্তা/Track (Automatic)");
                    local[0] = 0;
                    autoLineValue = false;
                    lineStart = "0";
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    isStart = false;

                    Log.i("Last Location", lastLatLongitude.toString());

                    LatLng lastLatlng = lastLatLongitude[0];
                    nop[0].add(lastLatlng);
                    mMap.addPolyline(nop[0]);
                    mp.position(lastLatlng);
                    autoGpxLatLng.add(lastLatlng);
                    mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                    mp.anchor((float) 0.5, (float) 0.5);
                    Double distance = CalculationByDistance(autoPreLatlng[0], lastLatlng);
                    w[0] = w[0] + distance;
                    mp.snippet(String.format("%.3f", w[0]) + " KM");
                    mp.title("End Point");
                    mMap.addMarker(mp);

                    length_multi = (String.format("%.3f", w[0]) + " KM");

                    String start = "\t<trk>\n" +
                            "\t\t<name>TTIT</name>\n";
                    String desc = "\t\t<desc>Length: " + length_multi + "</desc>\n";
                    String trkseg = "\t\t<trkseg>\n";
                    String trkpt = "";
                    for (int b = 0; b < autoGpxLatLng.size(); b++) {
                        Log.i("Latlng :", autoGpxLatLng.get(b).toString());
                        trkpt += "\t\t\t<trkpt lat=\"" + autoGpxLatLng.get(b).latitude + "\" lon=\"" + autoGpxLatLng.get(b).longitude + "\"></trkpt>\n";
                    }
                    String trksegFinish = "\t\t</trkseg>\n";
                    String finish = "\t</trk>\n";

                    trk.add(start + desc + trkseg + trkpt + trksegFinish + finish);


                    nop[0] = new PolylineOptions().width(5).color(Color.RED).geodesic(true);

                    //locationManager.removeUpdates(locationListener);
                    w[0] = 0.0;
                    autoGpxLatLng.clear();
                    autoGpxLatLng = new ArrayList<>();
                    local[0] = 0;
                    autoPreLatlng[0] = new LatLng(0, 0);
                    lastLatLongitude[0] = new LatLng(0,0);
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!lineValue) {
                    shareMulti.setVisibility(View.GONE);
                    removeLast.setVisibility(View.VISIBLE);
                    clearMap.setVisibility(View.VISIBLE);
                    if (i[0] == 0) {
                        preLatLng[0] = latLng;
                        Log.i("Hobe ", "hobe");

                        options.position(latLng);
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                        options.anchor((float) 0.5, (float) 0.5);
                        options.snippet("0 KM");
                        options.title("Starting Point");
                        previousLatlng[0] = latLng;
                        gpxLatLng.add(latLng);
//                            mMap.addMarker(options).setTitle("Starting Point");
                        mm[0] = mMap.addMarker(options);
                        option[0].add(latLng);
                        i[0]++;
//                            mMap.addPolyline(option[0]);
                        polylines[0] = mMap.addPolyline(option[0]);
                    } else {
                        i[0]++;
                        Log.i("Hobe ", "hobe");
                        previousLatlng[0] = preLatLng[0];
                        options.position(latLng);
                        Double distance = CalculationByDistance(preLatLng[0], latLng);
                        k[0] = distance;
                        j[0] = j[0] + distance;
                        options.snippet(String.format("%.3f", j[0]) + " KM");
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                        options.anchor((float) 0.5, (float) 0.5);
                        options.title("Road Point");
                        gpxLatLng.add(latLng);
//                            mMap.addMarker(options).setTitle("Road Point");
                        mm[0] = mMap.addMarker(options);

                        preLatLng[0] = latLng;
                        option[0].add(latLng);
//                            mMap.addPolyline(option[0]);
                        polylines[0] = mMap.addPolyline(option[0]);

                    }

                }
                if (wayPointValue) {
                    removeLast.setVisibility(View.VISIBLE);
                    clearMap.setVisibility(View.VISIBLE);
                    preWayPoint[0] = latLng;
                    wp.position(latLng);
                    wp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    wp.title("TTIT");
                    wpLatLng.add(latLng);
                    fromMap = true;
                    fromButton = false;
                    wwpp[0] = mMap.addMarker(wp);
                } else {
                    Log.i("Hobe na", "Kissu");
                }
            }
        });

        instantWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wayPointValue && isWayStart) {
                    removeLast.setVisibility(View.VISIBLE);
                    clearMap.setVisibility(View.VISIBLE);
                    if (checkLatLng[0] != instantWayLatLng[0]) {
                        System.out.println(instantWayLatLng[0]);
                        checkLatLng[0] = instantWayLatLng[0];
                        preWayPoint[0] = instantWayLatLng[0];
                        wp.position(instantWayLatLng[0]);
                        fromButton = true;
                        fromMap = false;
                        wp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                        wp.title("TTIT");
                        wpLatLng.add(instantWayLatLng[0]);
                        wwpp[0] = mMap.addMarker(wp);
                    } else {
//                        Toast.makeText(getApplicationContext(), "This Way Point matches the previous one",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "এই জায়গাটি আগে আরেকবার নেয়া হয়েছে",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        removeLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i[0]--;
                if (mm[0] != null && polylines[0] != null && i[0] == 0) {
                    Log.i("Pai", "na");
                    mm[0].remove();
                    polylines[0].remove();
                    int index = gpxLatLng.lastIndexOf(previousLatlng[0]);
                    gpxLatLng.remove(index);
                    option[0] = new PolylineOptions().width(8).color(Color.RED).geodesic(true);
                    removeLast.setVisibility(View.GONE);
                }
                if (mm[0] != null && polylines[0] != null && i[0] != 0) {
                    Log.i("Pai", "na");
                    int index = gpxLatLng.lastIndexOf(preLatLng[0]);
                    gpxLatLng.remove(index);
                    mm[0].remove();
                    polylines[0].remove();
                    option[0] = new PolylineOptions().width(8).color(Color.RED).geodesic(true);
                    option[0].add(previousLatlng[0]);
                    preLatLng[0] = previousLatlng[0];
                    j[0] = j[0] - k[0];
                    removeLast.setVisibility(View.GONE);

                }
                if (wayPointValue && wwpp[0] != null && fromMap) {
                    wwpp[0].remove();
                    fromMap = false;
                    int index = wpLatLng.lastIndexOf(preWayPoint[0]);
                    wpLatLng.remove(index);
                    removeLast.setVisibility(View.GONE);
                }
                if (wayPointValue && wwpp[0] != null && fromButton) {
                    wwpp[0].remove();
                    fromButton = false;
                    checkLatLng[0] = new LatLng(0,0);
                    int index = wpLatLng.lastIndexOf(preWayPoint[0]);
                    wpLatLng.remove(index);
                    removeLast.setVisibility(View.GONE);
                }
            }
        });

        drawLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineValue) {
                    shareMulti.setVisibility(View.GONE);
                    gpxLatLng = new ArrayList<>();
                    lineValue = false;
                    autoLine.setVisibility(View.GONE);
                    drawWayPoint.setVisibility(View.GONE);
                    drawLine.setText("রাস্তা সংরক্ষণ করুন / Save Track");
                    option[0] = new PolylineOptions().width(8).color(Color.RED).geodesic(true);

                } else {
                    if (gpxLatLng.size() == 0) {
//                        Toast.makeText(getApplicationContext(), "Please Draw Line to Save", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "আগে রাস্তা চিহ্নিত করুন তারপরে সংরক্ষণ করুন", Toast.LENGTH_SHORT).show();

                    } else {
                        saveFile.setVisibility(View.VISIBLE);
                        autoLine.setVisibility(View.VISIBLE);
                        drawWayPoint.setVisibility(View.VISIBLE);
                        lineValue = true;
                        removeLast.setVisibility(View.GONE);

                        drawLine.setText("রাস্তা/Track (Manual)");
                        length_multi = (String.format("%.3f", j[0]) + " KM");
                        preLatLng[0] = new LatLng(0, 0);

                        String start = "\t<trk>\n" +
                                "\t\t<name>TTIT</name>\n";
                        String desc = "\t\t<desc>Length: " + length_multi + "</desc>\n";
                        String trkseg = "\t\t<trkseg>\n";
                        String trkpt = "";
                        for (int b = 0; b < gpxLatLng.size(); b++) {
                            Log.i("Latlng :", gpxLatLng.get(b).toString());
                            trkpt += "\t\t\t<trkpt lat=\"" + gpxLatLng.get(b).latitude + "\" lon=\"" + gpxLatLng.get(b).longitude + "\"></trkpt>\n";
                        }
                        String trksegFinish = "\t\t</trkseg>\n";
                        String finish = "\t</trk>\n";

                        trk.add(start + desc + trkseg + trkpt + trksegFinish + finish);

                        gpxLatLng.clear();;
                        gpxLatLng = new ArrayList<>();

                        i[0] = 0;
                        j[0] = 0.0;
                        option[0] = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
                        mm[0] = null;
                        polylines[0] = null;
                        k[0] = 0.0;
                        preLatLng[0] = new LatLng(0, 0);
                        previousLatlng[0] = new LatLng(0, 0);

                    }
                }
            }
        });


        drawWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wayPointValue) {
                    shareMulti.setVisibility(View.GONE);
                    wpLatLng = new ArrayList<>();
                    wayPointValue = true;
                    isWayStart = true;
                    autoLine.setVisibility(View.GONE);
                    drawLine.setVisibility(View.GONE);
                    instantWay.setVisibility(View.VISIBLE);
                    drawWayPoint.setText("সংরক্ষণ করুন / Save WayPoint");
                    Toast.makeText(getApplicationContext(),"আরও ভাল পজিশন পেতে অনুগ্রহ করে ৫ থেকে ১০ সেকেন্ড অপেক্ষা করুন",Toast.LENGTH_LONG).show();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                } else {
                    if (wpLatLng.size() == 0) {
                        Toast.makeText(getApplicationContext(), "আগে চিহ্নিত করুন তারপরে সংরক্ষণ করুন", Toast.LENGTH_SHORT).show();
                    }else {
                        wayPointValue = false;
                        saveFile.setVisibility(View.VISIBLE);
                        autoLine.setVisibility(View.VISIBLE);
                        drawLine.setVisibility(View.VISIBLE);
                        instantWay.setVisibility(View.GONE);
                        removeLast.setVisibility(View.GONE);

                        drawWayPoint.setText("স্থাপনা/WayPoint");
                        // notun code
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        //
                        for (int i = 0; i < wpLatLng.size(); i++) {

                            String wpt = "\t<wpt lat=\""+ wpLatLng.get(i).latitude +"\" lon=\""+ wpLatLng.get(i).longitude+"\">\n" +
                                    "\t\t<name>TTIT</name>\n" +
                                    "\t</wpt>";
                            trk.add(wpt);
                        }

                        wpLatLng.clear();
                        wpLatLng = new ArrayList<>();

                        preWayPoint[0] = new LatLng(0,0);
                        checkLatLng[0] = new LatLng(0,0);
                        instantWayLatLng[0] = new LatLng(0,0);
                        wwpp[0] = null;


                    }


                }
            }
        });

        clearMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                i[0] = 0;
                option[0] = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
                mm[0] = null;
                polylines[0] = null;
                nop[0] = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
                j[0] = 0.0;
                k[0] = 0.0;
                preLatLng[0] = new LatLng(0, 0);
                previousLatlng[0] = new LatLng(0, 0);
                local[0] = 0;
                autoPreLatlng[0] = new LatLng(0, 0);
                lastLatLongitude[0] = new LatLng(0,0);
                preWayPoint[0] = new LatLng(0,0);
                checkLatLng[0] = new LatLng(0,0);
                instantWayLatLng[0] = new LatLng(0,0);
                wwpp[0] = null;
                w[0] = 0.0;
                gpxLatLng.clear();
                gpxLatLng = new ArrayList<>();
                autoGpxLatLng.clear();
                autoGpxLatLng= new ArrayList<>();
                wpLatLng.clear();
                wpLatLng = new ArrayList<>();
                trk.clear();
                trk = new ArrayList<>();

                lineValue = true;
                autoLineValue = false;
                wayPointValue= false;
                isStart = false;
                isWayStart = false;
                fromMap = false;
                fromButton = false;
                lineStart = "0";

                drawLine.setVisibility(View.VISIBLE);
                drawLine.setText("রাস্তা/Track (Manual)");
                drawWayPoint.setVisibility(View.VISIBLE);
                drawWayPoint.setText("স্থাপনা/WayPoint");
                autoLine.setVisibility(View.VISIBLE);
                autoLine.setText("রাস্তা/Track (Automatic)");
                removeLast.setVisibility(View.GONE);
                instantWay.setVisibility(View.GONE);
                clearMap.setVisibility(View.VISIBLE);
                saveFile.setVisibility(View.GONE);
                shareMulti.setVisibility(View.GONE);
            }
        });

        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareMulti.setVisibility(View.VISIBLE);
                SaveMulti saveMulti = new SaveMulti();
                saveMulti.show(getSupportFragmentManager(),"Multi");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivityForMulti.this);
                builder.setTitle("পিছনে যান/GO BACK")
                        .setMessage("আপনি কি আগের পেইজ এ যেতে চান?/Are you sure want to go back?")
                        .setPositiveButton("হ্যাঁ/YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fusedLocationProviderClientCamera.removeLocationUpdates(locationCallbackCamera);
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                finish();
                            }
                        })
                        .setNegativeButton("না/NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.setCancelable(false);
                alert.show();
            }
        });

        shareMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringFIle = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +  fileName +".gpx";

                File file = new File(stringFIle);
                if (!file.exists()) {
                    Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("*/*");
                intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));

                startActivity(Intent.createChooser(intentShare, "Share the File..."));
            }
        });

        locationCallbackCamera = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    targetLocation = location;
                    cameraLatLng[0] = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.i("Camera LocationUpdate:",cameraLatLng[0].toString());
                }
            }
        };

        fusedLocationProviderClientCamera.requestLocationUpdates(locationRequestCamera, locationCallbackCamera, Looper.getMainLooper());

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraLatLng[0] != null) {
                    //Toast.makeText(getApplicationContext(), "Open Camera",Toast.LENGTH_SHORT).show();
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            System.out.println("PhotoFile: " + ex.getLocalizedMessage());
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                    "com.example.android.shuvoCameraProviderGPX",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            try {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                Log.i("Activity:", "Shuru hoise");

                            } catch (ActivityNotFoundException e) {
                                // display error state to the user
                                System.out.println("Activity: "+e.getLocalizedMessage());
                            }

                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Location Not Found",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
        System.out.println(timeStamp);
        String imageFileName = "IMGLC_" + timeStamp + "_";
        System.out.println(imageFileName);
        Boolean exists = false;
        File file = null;
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapsActivityForMulti.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String adds = obj.getAddressLine(0);
            String add = "Address from GeoCODE: ";
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address: " + add);
            Log.v("NEW ADD", "Address: " + adds);
            address = adds;
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            address = "Address Not Found";
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            double latitude = cameraLatLng[0].latitude;
            double longitude = cameraLatLng[0].longitude;

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

            getAddress(latitude, longitude);

            // Getting ImageFile Name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
            System.out.println(timeStamp);
            imageFileName = "IMG_" + timeStamp;
            System.out.println(imageFileName);

            //fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy, hh:mm:ss a", Locale.getDefault());
            Date c = Calendar.getInstance().getTime();
            String dd = simpleDateFormat.format(c);
            System.out.println(dd);
            String timeLatLng = "Time: " + dd + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude;
            address = timeLatLng + "\n"+ "Address: " + address;
            System.out.println(address);

            File imgFile = new  File(currentPhotoPath);
            if(imgFile.exists()) {
                //cameraImage.setImageURI(Uri.fromFile(imgFile));
                System.out.println(currentPhotoPath);

                bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                try {
                    bitmap = modifyOrientation(bitmap, currentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Resources resources = getResources();
                float scale = resources.getDisplayMetrics().density;
                //Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);

                android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
                // set default bitmap config if none
                if(bitmapConfig == null) {
                    bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
                }
                // resource bitmaps are imutable,
                // so we need to convert it to mutable one
                bitmap = bitmap.copy(bitmapConfig, true);

                Canvas canvas = new Canvas(bitmap);

                // new antialiased Paint
                TextPaint paint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
                // text color - #3D3D3D
                paint.setColor(Color.WHITE);
                // text size in pixels
                paint.setTextSize((int) (36 * scale));
                // text shadow
                paint.setShadowLayer(4f, 0f, 2f, Color.BLACK);
                paint.setFakeBoldText(true);

                // set text width to canvas width minus 16dp padding
                int textWidth = canvas.getWidth() - (int) (16 * scale);

                // init StaticLayout for text

                StaticLayout textLayout = new StaticLayout(
                        address, paint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                // get height of multiline text
                int textHeight = textLayout.getHeight();

                // get position of text's top left corner
                float x = (bitmap.getWidth() - textWidth)/2;
                float y = (bitmap.getHeight() - textHeight)/2;


                // draw text to the Canvas center
                int yyyy = bitmap.getHeight() - textHeight - 16;
                canvas.save();
                canvas.translate(5, yyyy);
                textLayout.draw(canvas);
                canvas.restore();

                //cameraImage.setImageBitmap(bitmap);

                // ekhan theke purata jabe imagedialogue e
//                File deletefile = new File(currentPhotoPath);
//                boolean deleted = deletefile.delete();
//                if (deleted) {
//                    System.out.println("deleted");
//                }


                // Saving Photo

//                FileOutputStream fileOutputStream = null;
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
//                System.out.println(timeStamp);
//                String imageFileName = "IMGLC_" + timeStamp +".jpg";
//                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + imageFileName);
//                try {
//                    file.createNewFile();
//                    currentPhotoPath = file.getAbsolutePath();
//                    fileOutputStream = new FileOutputStream(file);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//                try {
//                    fileOutputStream.flush();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//                    fileOutputStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                Toast.makeText(getApplicationContext(), "Photo is saved", Toast.LENGTH_SHORT).show();
//                galleryAddPic();
//
//                String imaaa = file.getAbsolutePath();
//
//                //Writes Exif Information to the Image
//                try {
//                    ExifInterface exif = new ExifInterface(imaaa);
//                    Log.w("Location", String.valueOf(targetLocation));
//
//                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convert(targetLocation.getLatitude()));
//                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(targetLocation.getLatitude()));
//                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convert(targetLocation.getLongitude()));
//                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(targetLocation.getLongitude()));
//
//                    exif.saveAttributes();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                scanFile(getApplicationContext(),currentPhotoPath, null);

                ImageDialogue imageDialogue = new ImageDialogue();
                imageDialogue.show(getSupportFragmentManager(),"Image");
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("পিছনে যান/GO BACK")
                .setMessage("আপনি কি আগের পেইজ এ যেতে চান?/Are you sure want to go back?")
                .setPositiveButton("হ্যাঁ/YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MapsActivityForMulti.this.finish();
                        fusedLocationProviderClientCamera.removeLocationUpdates(locationCallbackCamera);
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    }
                })
                .setNegativeButton("না/NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }

    public void zoomToUserLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.i("Ekhane", "1");
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
//                Log.i("lattt", location.toString());
                LatLng latLng = new LatLng(23.6850, 90.3563);


                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    System.out.println(latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                } else {
                    latLng = new LatLng(23.6850, 90.3563);
                    System.out.println(latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
                }

            }
        });



    }

    private static StringBuilder createDebugStringBuilder(File filePath) {
        return new StringBuilder("Set Exif to file='").append(filePath.getAbsolutePath()).append("'\n\t");
    }

    public LatLng getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("Permission:", "Checked");
        }
        final LatLng[] latLng = {null};
        fusedLocationProviderClientCamera.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.

                        if (location != null) {
                            // Logic to handle location object
                            System.out.println("Location Camera: " +location.toString());
                            latLng[0] = new LatLng(location.getLatitude(),location.getLongitude());
                        } else {
                            System.out.println("Location Camera Null");
                        }
                    }
                });
        System.out.println(latLng[0]);
        return latLng[0];
    }
}