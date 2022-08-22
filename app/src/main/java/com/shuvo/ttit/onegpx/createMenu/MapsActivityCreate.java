package com.shuvo.ttit.onegpx.createMenu;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.shuvo.ttit.onegpx.MapsMenu.MapsActivity;
import com.shuvo.ttit.onegpx.R;
import com.shuvo.ttit.onegpx.arrayLists.ArrrayFile;
import com.shuvo.ttit.onegpx.dialogue.SaveMulti;

import java.util.ArrayList;
import java.util.List;

import static com.shuvo.ttit.onegpx.MapsMenu.MapsActivity.multiGpxList;
import static com.shuvo.ttit.onegpx.gpxConverter.DistanceCalculator.CalculationByDistance;

public class MapsActivityCreate extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;

    private EditText search;
    private ImageButton button;
    private Spinner selection;

    private Button addTrack, deleteTrack, atStart, back, atEnd, saveTrack, reset, undo;
    private Button saveGpx;

    public static String length = "";

    private Boolean lineValue = true;
    private Boolean autoLine = false;
    private Boolean addTrackValue = false;
    private Boolean deleteTrackValue = false;
    private Boolean deleteOrigin = false;
    private Boolean atStartValue = false;
    private Boolean atEndValue = false;
    public static ArrayList<LatLng> gpxCreator;
    public static ArrayList<LatLng> gpxEditor;
    public static ArrayList<ArrrayFile> resetToNewGpx;
    public static ArrayList<Location> atStartAdd;
    public static ArrayList<Polyline> polylineList;
    public static ArrayList<Polyline> fromStartPoly;
    public static ArrayList<Marker> markers;
    public static ArrayList<Marker> fromStartMarkers;
    public static ArrayList<String> editedTrk;
    private int numbertrigger = 0;
    //    private String fileExten = ".gpx";
//    File myExternalFile;
    final LatLng[] postLatLng = {new LatLng(0, 0)};
    final LatLng[] firstPoint = {new LatLng(0,0)};
    final LatLng[] lastPoint = {new LatLng(0,0)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_create);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        addTrack = findViewById(R.id.add_track);
        deleteTrack = findViewById(R.id.delete_track);
        atStart = findViewById(R.id.at_start);
        atEnd = findViewById(R.id.at_end);
        reset = findViewById(R.id.reset_all);
        undo = findViewById(R.id.undo_last);
        saveTrack = findViewById(R.id.save_edited_track);
        selection = findViewById(R.id.spinnnnn);
        MapsActivity.editOrNot = true;

        back = findViewById(R.id.go_back);
        saveGpx = findViewById(R.id.save_existing_gpx_file);

        gpxCreator = new ArrayList<>();
        resetToNewGpx = new ArrayList<>();
        atStartAdd = new ArrayList<>();
        gpxEditor = new ArrayList<>();
        polylineList = new ArrayList<>();
        fromStartPoly = new ArrayList<>();
        markers = new ArrayList<>();
        fromStartMarkers = new ArrayList<>();
        editedTrk = new ArrayList<>();

        List<String> categories = new ArrayList<String>();
        categories.add("NORMAL");
        categories.add("SATELLITE");
        categories.add("TERRAIN");
        categories.add("HYBRID");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selection.setAdapter(spinnerAdapter);
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

        final PolylineOptions[] optionNew = {new PolylineOptions().width(10).color(Color.BLUE).geodesic(true)};
        MarkerOptions optionsNew = new MarkerOptions();
        final Marker[] mm = {null, null};
        final Polyline[] polylines = {null};



        final PolylineOptions[] nop = {new PolylineOptions().width(10).color(Color.BLUE).geodesic(true)};
        MarkerOptions mp = new MarkerOptions();
        mMap.getUiSettings().setZoomControlsEnabled(true);

        final Double[] dj = {0.0};
        final int[] as = {0};
        final int[] asd = {0};
        final Double[] k = {0.0};
        final LatLng[] preLatLng = {new LatLng(0, 0)};
        final LatLng[] firstLatLng = {new LatLng(0, 0)};

        final int[] local = {0};
        //final LatLng[] postLatLng = {new LatLng(0, 0)};
        final LatLng[] lastLatLongitude = {new LatLng(0,0)};
        final Marker[] dm = {null};
        final Polyline[] dp = {null};
        final Double[] w = {0.0};

        showTrack();

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
                    case "TERRAIN" :
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


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                optionNew[0] = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
                mm[0] = null;
                mm[1] = null;
                polylines[0] = null;
                dj[0] = 0.0;
                as[0] = 0;
                k[0] = 0.0;
                asd[0] = 0;
                preLatLng[0] = new LatLng(0, 0);
                firstLatLng[0] = new LatLng(0, 0);
                lastLatLongitude[0] = new LatLng(0,0);
                saveTrack.setVisibility(View.GONE);
                undo.setVisibility(View.GONE);
                atStart.setVisibility(View.GONE);
                atEnd.setVisibility(View.GONE);
                addTrack.setVisibility(View.VISIBLE);
                deleteTrack.setText("DELETE TRACK");
                deleteTrack.setVisibility(View.VISIBLE);
                saveGpx.setVisibility(View.GONE);
                atStartValue = false;
                addTrackValue = false;
                deleteTrackValue = false;
                deleteOrigin = false;
                atEndValue = false;
                numbertrigger = 0;

                gpxEditor = new ArrayList<>();
                gpxCreator = new ArrayList<>();
                atStartAdd = new ArrayList<>();
                polylineList = new ArrayList<>();
                fromStartPoly = new ArrayList<>();
                markers = new ArrayList<>();
                fromStartMarkers = new ArrayList<>();

                for (int a = 0; a < resetToNewGpx.size(); a++) {
                    ArrayList<Location> gpxList = resetToNewGpx.get(a).getMyLatlng();
                    String lengthh = resetToNewGpx.get(a).getDescc();

                    int index = lengthh.indexOf(" ");
                    int index2 = lengthh.indexOf(" KM");
                    Log.i("Index of 1st:", String.valueOf(index));
                    Log.i("Index of 2nd:", String.valueOf(index2));
                    String substr=lengthh.substring(index + 1, index2);
                    System.out.println(substr);

                    MarkerOptions options = new MarkerOptions();

                    PolylineOptions option = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true).clickable(true).zIndex(Float.parseFloat(substr));
                    for (int z = 0; z < gpxList.size(); z++) {
                        LatLng point = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                        option.add(point);
                        polylineList.add(mMap.addPolyline(option));
                        Log.i("ppoll", String.valueOf(polylineList.size()));
                    }

//                    mMap.addPolyline(option);

                    Double j = 0.0;

                    for (int i = 0; i< gpxList.size(); i++) {

                        LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                        //markerPoints.add(gpx);
                        options.position(gpx);

                        if (i == 0 ) {
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_start));
                            options.anchor((float) 0.5,(float) 0.5);
                            options.snippet("0 KM");
                            options.title("Starting Road");
                            markers.add(mMap.addMarker(options));
                        }else if (i == gpxList.size()-1){
                            LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                            options.anchor((float) 0.5,(float) 0.5);
                            Double diss = CalculationByDistance(preGpx, gpx);
                            j  = j + diss;
                            options.snippet(String.format("%.3f", j) + " KM");
                            options.title("Ending Road");
                            markers.add(mMap.addMarker(options));
                        } else {
                            LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            options.anchor((float) 0.5,(float) 0.5);
                            Double diss = CalculationByDistance(preGpx, gpx);
                            j  = j + diss;
                            options.snippet(String.format("%.3f", j) + " KM");
                            options.title("On Going Road");
                            markers.add(mMap.addMarker(options));
                        }

                    }

                    int i = (gpxList.size() - 1) / 2;
                    LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 16));

                }

            }
        });

        Log.i("Markers", String.valueOf(markers.size()));
        Log.i("Polt", String.valueOf(polylineList.size()));

        addTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrack.setVisibility(View.GONE);
                addTrack.setVisibility(View.GONE);
                addTrackValue = true;
                atStart.setVisibility(View.VISIBLE);
                atEnd.setVisibility(View.VISIBLE);
            }
        });
        atStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addTrackValue) {
                    atStart.setVisibility(View.GONE);
                    atEnd.setVisibility(View.GONE);
                    atStartValue = true;
                    saveTrack.setVisibility(View.VISIBLE);
                } else if (deleteTrackValue) {
                    atStart.setVisibility(View.GONE);
                    atEnd.setVisibility(View.GONE);
                    atStartValue = true;
                    saveTrack.setVisibility(View.VISIBLE);
                    deleteTrack.setText("Delete");
                    deleteTrack.setVisibility(View.VISIBLE);
                    numbertrigger = 1;
                }
            }
        });

        atEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addTrackValue) {
                    atStart.setVisibility(View.GONE);
                    atEnd.setVisibility(View.GONE);
                    atEndValue = true;
                    saveTrack.setVisibility(View.VISIBLE);
                } else if (deleteTrackValue) {
                    atStart.setVisibility(View.GONE);
                    atEnd.setVisibility(View.GONE);
                    atEndValue = true;
                    saveTrack.setVisibility(View.VISIBLE);
                    deleteTrack.setText("Delete");
                    deleteTrack.setVisibility(View.VISIBLE);
                    numbertrigger = 1;
                }
            }
        });

        deleteTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!deleteOrigin) {
                    deleteTrackValue = true;
                    deleteTrack.setVisibility(View.GONE);
                    addTrack.setVisibility(View.GONE);
                    atStart.setVisibility(View.VISIBLE);
                    atEnd.setVisibility(View.VISIBLE);
                    deleteOrigin = true;
                } else if (deleteOrigin) {
                    if (atStartValue) {
                        if (multiGpxList.size() == 1) {
                            for (int i = 0; i < multiGpxList.size(); i++) {
                                atStartAdd = multiGpxList.get(i).getMyLatlng();
                            }
                            if (gpxCreator.size() == 0) {
                                for (int i = 0; i < atStartAdd.size(); i++) {
                                    gpxCreator.add(new LatLng(atStartAdd.get(i).getLatitude(), atStartAdd.get(i).getLongitude()));
                                }
                            }
                            undo.setVisibility(View.VISIBLE);
                            if (numbertrigger == 1) {
                                asd[0] = gpxCreator.size()-1;
                                numbertrigger = 0;
                                gpxEditor = gpxCreator;
                                showDeleteAtFirstTrack();
                            }
                            Log.i("GPX", String.valueOf(gpxCreator.size()));

                            if (asd[0] >= 0) {

                                Log.i("NUmber:", String.valueOf(asd[0]));
                                dm[0] = fromStartMarkers.get(asd[0]);
                                fromStartMarkers.get(asd[0]).remove();
                                System.out.println(fromStartPoly.get(asd[0]).getPoints());
                                dp[0] = fromStartPoly.get(asd[0]);
                                fromStartPoly.get(asd[0]).remove();
                                lastLatLongitude[0] = gpxEditor.get(asd[0]);
                                gpxEditor.remove(asd[0]);
                                asd[0]--;
                            }
                        }
                    }
                    else if (atEndValue) {
                        if (multiGpxList.size() == 1) {
                            for (int i = 0; i < multiGpxList.size(); i++) {
                                atStartAdd = multiGpxList.get(i).getMyLatlng();
                            }
                            if (gpxCreator.size() == 0) {
                                for (int i = 0; i < atStartAdd.size(); i++) {
                                    gpxCreator.add(new LatLng(atStartAdd.get(i).getLatitude(), atStartAdd.get(i).getLongitude()));
                                }
                            }
                            undo.setVisibility(View.VISIBLE);
                            if (numbertrigger == 1) {
                                asd[0] = gpxCreator.size()-1;
                                numbertrigger = 0;
                                gpxEditor = gpxCreator;
                                showDeleteAtEndTrack();
                            }
                            if (asd[0] >= 0) {
                                Log.i("NUmber:", String.valueOf(asd[0]));
                                fromStartMarkers.get(asd[0]).remove();
                                System.out.println(polylineList.get(asd[0]).getPoints());
                                fromStartPoly.get(asd[0]).remove();
                                lastLatLongitude[0] = gpxEditor.get(asd[0]);
                                gpxCreator.remove(asd[0]);
                                asd[0]--;
                            }
                        }
                    }
                }
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (atStartValue && addTrackValue) {
                    Toast.makeText(getApplicationContext(), "Selected", Toast.LENGTH_SHORT).show();
                    if (multiGpxList.size() == 1) {
                        for (int i = 0; i < multiGpxList.size(); i++) {
                            atStartAdd = multiGpxList.get(i).getMyLatlng();
                        }
                        if (gpxCreator.size() == 0) {
                            for (int i = 0; i< atStartAdd.size(); i++) {
                                gpxCreator.add(new LatLng(atStartAdd.get(i).getLatitude(),atStartAdd.get(i).getLongitude()));
                            }
                            firstPoint[0] = gpxCreator.get(0);
                            mMap.addMarker(new MarkerOptions().position(firstPoint[0]).title("Started Here"));
                        }


                        if (as[0] == 0) {
                            LatLng startLatLng = gpxCreator.get(0);
                            //LatLng startLatLng = new LatLng(atStartAdd.get(0).getLatitude(), atStartAdd.get(0).getLongitude());
                            optionNew[0].add(startLatLng);
                            optionsNew.position(startLatLng);
                            optionsNew.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            optionsNew.anchor((float) 0.5,(float) 0.5);
                            mm[1] = mMap.addMarker(optionsNew);
                            polylines[0] = mMap.addPolyline(optionNew[0]);

                            preLatLng[0] = startLatLng;
                            optionsNew.position(latLng);
                            Double distance = CalculationByDistance(preLatLng[0], latLng);
                            k[0] = distance;
                            dj[0] = dj[0] + distance;
                            optionsNew.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            optionsNew.anchor((float) 0.5,(float) 0.5);
                            optionsNew.snippet(String.format("%.3f", dj[0]) + " KM");
                            optionsNew.title("Point");
                            firstLatLng[0] = latLng;
                            gpxEditor.add(latLng);
//                            mMap.addMarker(options).setTitle("Starting Point");
                            mm[0] = mMap.addMarker(optionsNew);
                            optionNew[0].add(latLng);
                            as[0]++;
                            undo.setVisibility(View.VISIBLE);
//                            mMap.addPolyline(option[0]);
                            polylines[0] = mMap.addPolyline(optionNew[0]);
                        } else {
                            as[0]++;
                            preLatLng[0] = firstLatLng[0];
                            optionsNew.position(latLng);
                            Double distance = CalculationByDistance(preLatLng[0], latLng);
                            k[0] = distance;
                            dj[0] = dj[0] + distance;
                            optionsNew.snippet(String.format("%.3f", dj[0]) + " KM");
                            optionsNew.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            optionsNew.anchor((float) 0.5,(float) 0.5);
                            optionsNew.title("Road Point");
                            gpxEditor.add(latLng);
////                            mMap.addMarker(options).setTitle("Road Point");
                            mm[0] = mMap.addMarker(optionsNew);
                            firstLatLng[0] = latLng;
                            optionNew[0].add(latLng);
                            undo.setVisibility(View.VISIBLE);
////                            mMap.addPolyline(option[0]);
                            polylines[0] = mMap.addPolyline(optionNew[0]);

                        }
                    }
                } else if (addTrackValue && atEndValue) {
                    Toast.makeText(getApplicationContext(), "Selected End", Toast.LENGTH_SHORT).show();
                    if (multiGpxList.size() == 1) {
                        for (int i = 0; i < multiGpxList.size(); i++) {
                            atStartAdd = multiGpxList.get(i).getMyLatlng();
                        }
                        if (gpxCreator.size() == 0) {
                            for (int i = 0; i< atStartAdd.size(); i++) {
                                gpxCreator.add(new LatLng(atStartAdd.get(i).getLatitude(),atStartAdd.get(i).getLongitude()));
                            }
                            lastPoint[0] = gpxCreator.get(0);
                            mMap.addMarker(new MarkerOptions().position(lastPoint[0]).title("Ended Here"));
                        }

                        if (as[0] == 0) {
                            LatLng startLatLng = gpxCreator.get(gpxCreator.size()-1);
                            //LatLng startLatLng = new LatLng(atStartAdd.get(0).getLatitude(), atStartAdd.get(0).getLongitude());
                            optionNew[0].add(startLatLng);
                            optionsNew.position(startLatLng);
                            optionsNew.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            optionsNew.anchor((float) 0.5,(float) 0.5);
                            mm[1] = mMap.addMarker(optionsNew);
                            polylines[0] = mMap.addPolyline(optionNew[0]);

                            preLatLng[0] = startLatLng;
                            optionsNew.position(latLng);
                            Double distance = CalculationByDistance(preLatLng[0], latLng);
                            k[0] = distance;
                            dj[0] = dj[0] + distance;
                            optionsNew.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            optionsNew.anchor((float) 0.5,(float) 0.5);
                            optionsNew.snippet(String.format("%.3f", dj[0]) + " KM");
                            optionsNew.title("Point");
                            firstLatLng[0] = latLng;
                            gpxEditor.add(latLng);
//                            mMap.addMarker(options).setTitle("Starting Point");
                            mm[0] = mMap.addMarker(optionsNew);
                            optionNew[0].add(latLng);
                            as[0]++;
                            undo.setVisibility(View.VISIBLE);
//                            mMap.addPolyline(option[0]);
                            polylines[0] = mMap.addPolyline(optionNew[0]);
                        } else {
                            as[0]++;
                            preLatLng[0] = firstLatLng[0];
                            optionsNew.position(latLng);
                            Double distance = CalculationByDistance(preLatLng[0], latLng);
                            k[0] = distance;
                            dj[0] = dj[0] + distance;
                            optionsNew.snippet(String.format("%.3f", dj[0]) + " KM");
                            optionsNew.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            optionsNew.anchor((float) 0.5,(float) 0.5);
                            optionsNew.title("Road Point");
                            gpxEditor.add(latLng);
////                            mMap.addMarker(options).setTitle("Road Point");
                            mm[0] = mMap.addMarker(optionsNew);
                            firstLatLng[0] = latLng;
                            optionNew[0].add(latLng);
                            undo.setVisibility(View.VISIBLE);
////                            mMap.addPolyline(option[0]);
                            polylines[0] = mMap.addPolyline(optionNew[0]);

                        }
                    }
                }
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (atStartValue && addTrackValue) {
                    as[0]--;
                    if (mm[0] != null && polylines[0] != null && as[0] == 0) {
                        mm[0].remove();
                        polylines[0].remove();
                        int index = gpxEditor.lastIndexOf(firstLatLng[0]);
                        gpxEditor.remove(index);
                        dj[0] = dj[0] - k[0];
                        optionNew[0] = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
//                        optionNew[0].add(preLatLng[0]);
//                        firstLatLng[0] = preLatLng[0];
                        undo.setVisibility(View.GONE);
                    } else if (mm[0] != null && polylines[0] != null) {
                        Log.i("Pai", "na");
                        int index = gpxEditor.lastIndexOf(firstLatLng[0]);
                        gpxEditor.remove(index);
                        mm[0].remove();
                        polylines[0].remove();
                        optionNew[0] = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
                        optionNew[0].add(preLatLng[0]);
                        firstLatLng[0] = preLatLng[0];
                        dj[0] = dj[0] - k[0];
                        undo.setVisibility(View.GONE);
                    }

                } else if (addTrackValue && atEndValue) {
                    as[0]--;
                    if (mm[0] != null && polylines[0] != null && as[0] == 0) {
                        mm[0].remove();
                        polylines[0].remove();
                        int index = gpxEditor.lastIndexOf(firstLatLng[0]);
                        gpxEditor.remove(index);
                        dj[0] = dj[0] - k[0];
                        optionNew[0] = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
//                        optionNew[0].add(preLatLng[0]);
//                        firstLatLng[0] = preLatLng[0];
                        undo.setVisibility(View.GONE);
                    } else if (mm[0] != null && polylines[0] != null) {
                        Log.i("Pai", "na");
                        int index = gpxEditor.lastIndexOf(firstLatLng[0]);
                        gpxEditor.remove(index);
                        mm[0].remove();
                        polylines[0].remove();
                        optionNew[0] = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
                        optionNew[0].add(preLatLng[0]);
                        firstLatLng[0] = preLatLng[0];
                        dj[0] = dj[0] - k[0];
                        undo.setVisibility(View.GONE);
                    }
                } else if (deleteOrigin && atStartValue) {

                    fromStartMarkers = new ArrayList<>();

                    fromStartPoly = new ArrayList<>();

                    gpxEditor.add(lastLatLongitude[0]);

                    asd[0] = gpxEditor.size()-1;
                    mMap.clear();
                    System.out.println(firstPoint[0]);
                    mMap.addMarker(new MarkerOptions().position(firstPoint[0]).title("Started Here"));
                    MarkerOptions options = new MarkerOptions();
                    PolylineOptions option = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true).clickable(true);
                    for (int z = 0; z < gpxEditor.size(); z++) {
                        LatLng point = gpxEditor.get(z);
                        option.add(point);
                        fromStartPoly.add(mMap.addPolyline(option));
                    }
                    Double j = 0.0;

                    for (int i = 0; i< gpxEditor.size(); i++) {

                        LatLng gpx = gpxEditor.get(i);
                        //markerPoints.add(gpx);
                        options.position(gpx);

                        if (i == 0 ) {
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                            options.anchor((float) 0.5,(float) 0.5);
                            //options.snippet("0 KM");
                            options.title("Starting Road");
                            fromStartMarkers.add(mMap.addMarker(options));
                        }else if (i == gpxEditor.size()-1){
                            //LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            options.anchor((float) 0.5,(float) 0.5);
                            //Double diss = CalculationByDistance(preGpx, gpx);
                            //j  = j + diss;
                            //options.snippet(String.format("%.3f", j) + " KM");
                            options.title("Ending Road");
                            fromStartMarkers.add(mMap.addMarker(options));
                        } else {
                            //LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            options.anchor((float) 0.5,(float) 0.5);
                            //Double diss = CalculationByDistance(preGpx, gpx);
                            //j  = j + diss;
                            //options.snippet(String.format("%.3f", j) + " KM");
                            options.title("On Going Road");
                            fromStartMarkers.add(mMap.addMarker(options));
                        }

                    }
                    undo.setVisibility(View.GONE);
                } else if (deleteOrigin && atEndValue) {
                    fromStartMarkers = new ArrayList<>();

                    fromStartPoly = new ArrayList<>();

                    gpxEditor.add(lastLatLongitude[0]);

                    asd[0] = gpxEditor.size()-1;
                    mMap.clear();
                    System.out.println(lastPoint[0]);
                    mMap.addMarker(new MarkerOptions().position(lastPoint[0]).title("Ended Here"));
                    MarkerOptions options = new MarkerOptions();
                    PolylineOptions option = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true).clickable(true);
                    for (int z = 0; z < gpxEditor.size(); z++) {
                        LatLng point = gpxEditor.get(z);
                        option.add(point);
                        fromStartPoly.add(mMap.addPolyline(option));
                    }
                    Double j = 0.0;

                    for (int i = 0; i< gpxEditor.size(); i++) {

                        LatLng gpx = gpxEditor.get(i);
                        //markerPoints.add(gpx);
                        options.position(gpx);

                        if (i == 0 ) {
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_start));
                            options.anchor((float) 0.5,(float) 0.5);
                            //options.snippet("0 KM");
                            options.title("Starting Road");
                            fromStartMarkers.add(mMap.addMarker(options));
                        }else if (i == gpxEditor.size()-1){
                            //LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            options.anchor((float) 0.5,(float) 0.5);
                            //Double diss = CalculationByDistance(preGpx, gpx);
                            //j  = j + diss;
                            //options.snippet(String.format("%.3f", j) + " KM");
                            options.title("Ending Road");
                            fromStartMarkers.add(mMap.addMarker(options));
                        } else {
                            //LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                            options.anchor((float) 0.5,(float) 0.5);
                            //Double diss = CalculationByDistance(preGpx, gpx);
                            //j  = j + diss;
                            //options.snippet(String.format("%.3f", j) + " KM");
                            options.title("On Going Road");
                            fromStartMarkers.add(mMap.addMarker(options));
                        }

                    }
                    undo.setVisibility(View.GONE);
                }
            }
        });

        saveTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (atStartValue && addTrackValue) {
                    atStartValue = false;
                    addTrackValue = false;
                    undo.setVisibility(View.GONE);
                    saveTrack.setVisibility(View.GONE);
                    deleteTrack.setVisibility(View.VISIBLE);
                    addTrack.setVisibility(View.VISIBLE);
                    saveGpx.setVisibility(View.VISIBLE);
                    editedTrk.clear();
                    if (gpxEditor.size() == 0) {
                        if (mm[1] != null) {
                            mm[1].remove();
                        }
                        Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_SHORT).show();
                    } else {
                        mm[0].remove();
//                        LatLng lastLatLng = gpxEditor.get(gpxEditor.size()-1);
//                        optionsNew.position(lastLatLng);
//                        optionsNew.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_start));
//                        optionsNew.anchor((float) 0.5,(float) 0.5);
//                        optionsNew.snippet(String.format("%.3f", dj[0]) + " KM");
//                        optionsNew.title("Finish Point");
//                        mMap.addMarker(optionsNew);
                        ArrayList<LatLng> temp1 = new ArrayList<>();
                        ArrayList<LatLng> temp2 = new ArrayList<>();
                        temp2 = gpxEditor;
                        for (int i = temp2.size()-1 ; i >= 0 ; i--) {
                            LatLng latLng = temp2.get(i);
                            temp1.add(latLng);
                        }
                        gpxEditor = temp1;
                        gpxEditor.addAll(gpxCreator);
                        gpxCreator = gpxEditor;
                        gpxEditor = new ArrayList<>();
                    }

                    as[0] = 0;
                    k[0] = 0.0;
                    dj[0] = 0.0;
                    mm[0] = null;
                    mm[1] = null;
                    polylines[0] = null;
                    optionNew[0] = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
                    showSavedTrack();

                    String start = "\t<trk>\n" +
                            "\t\t<name>TTIT</name>\n";
                    String desc = "\t\t<desc>Length: " + length + "</desc>\n";
                    String trkseg = "\t\t<trkseg>\n";
                    String trkpt = "";
                    for (int b = 0; b < gpxCreator.size(); b++) {
                        Log.i("Latlng :", gpxCreator.get(b).toString());
                        trkpt += "\t\t\t<trkpt lat=\"" + gpxCreator.get(b).latitude + "\" lon=\"" + gpxCreator.get(b).longitude + "\"></trkpt>\n";
                    }
                    String trksegFinish = "\t\t</trkseg>\n";
                    String finish = "\t</trk>\n";

                    editedTrk.add(start + desc + trkseg + trkpt + trksegFinish + finish);
                }
                else if ( atEndValue && addTrackValue) {
                    atEndValue = false;
                    addTrackValue = false;
                    undo.setVisibility(View.GONE);
                    saveTrack.setVisibility(View.GONE);
                    deleteTrack.setVisibility(View.VISIBLE);
                    addTrack.setVisibility(View.VISIBLE);
                    saveGpx.setVisibility(View.VISIBLE);
                    editedTrk.clear();
                    if (gpxEditor.size() == 0) {
                        if (mm[1] != null) {
                            mm[1].remove();
                        }
                        Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_SHORT).show();
                    } else {
                        mm[0].remove();
//                        LatLng lastLatLng = gpxEditor.get(gpxEditor.size() - 1);
//                        optionsNew.position(lastLatLng);
//                        optionsNew.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
//                        optionsNew.anchor((float) 0.5, (float) 0.5);
//                        optionsNew.snippet(String.format("%.3f", dj[0]) + " KM");
//                        optionsNew.title("Finish Point");
//                        mMap.addMarker(optionsNew);
                        gpxCreator.addAll(gpxEditor);
                        gpxEditor = new ArrayList<>();
                    }
                    as[0] = 0;
                    k[0] = 0.0;
                    dj[0] = 0.0;
                    mm[0] = null;
                    mm[1] = null;
                    polylines[0] = null;
                    optionNew[0] = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
                    showSavedTrack();

                    String start = "\t<trk>\n" +
                            "\t\t<name>TTIT</name>\n";
                    String desc = "\t\t<desc>Length: " + length + "</desc>\n";
                    String trkseg = "\t\t<trkseg>\n";
                    String trkpt = "";
                    for (int b = 0; b < gpxCreator.size(); b++) {
                        Log.i("Latlng :", gpxCreator.get(b).toString());
                        trkpt += "\t\t\t<trkpt lat=\"" + gpxCreator.get(b).latitude + "\" lon=\"" + gpxCreator.get(b).longitude + "\"></trkpt>\n";
                    }
                    String trksegFinish = "\t\t</trkseg>\n";
                    String finish = "\t</trk>\n";

                    editedTrk.add(start + desc + trkseg + trkpt + trksegFinish + finish);
                }
                else if (deleteOrigin && atStartValue) {
                    deleteOrigin = false;
                    atStartValue = false;
                    deleteTrackValue = false;
                    undo.setVisibility(View.GONE);
                    saveTrack.setVisibility(View.GONE);
                    addTrack.setVisibility(View.VISIBLE);
                    deleteTrack.setText("DELETE TRACK");
                    deleteTrack.setVisibility(View.VISIBLE);
                    saveGpx.setVisibility(View.VISIBLE);
                    editedTrk.clear();
                    ArrayList<LatLng> temp1 = new ArrayList<>();
                    ArrayList<LatLng> temp2 = new ArrayList<>();
                    temp2 = gpxEditor;
                    for (int i = temp2.size()-1 ; i >= 0 ; i--) {
                        LatLng latLng = temp2.get(i);
                        temp1.add(latLng);
                    }
                    gpxEditor = temp1;
                    gpxCreator = gpxEditor;
                    gpxEditor = new ArrayList<>();
                    asd[0] = 0;
                    numbertrigger = 0;
                    fromStartPoly = new ArrayList<>();
                    fromStartMarkers = new ArrayList<>();
                    showSavedTrack();

                    String start = "\t<trk>\n" +
                            "\t\t<name>TTIT</name>\n";
                    String desc = "\t\t<desc>Length: " + length + "</desc>\n";
                    String trkseg = "\t\t<trkseg>\n";
                    String trkpt = "";
                    for (int b = 0; b < gpxCreator.size(); b++) {
                        Log.i("Latlng :", gpxCreator.get(b).toString());
                        trkpt += "\t\t\t<trkpt lat=\"" + gpxCreator.get(b).latitude + "\" lon=\"" + gpxCreator.get(b).longitude + "\"></trkpt>\n";
                    }
                    String trksegFinish = "\t\t</trkseg>\n";
                    String finish = "\t</trk>\n";

                    editedTrk.add(start + desc + trkseg + trkpt + trksegFinish + finish);
                }
                else if (deleteOrigin && atEndValue) {
                    deleteOrigin = false;
                    atEndValue = false;
                    deleteTrackValue = false;
                    undo.setVisibility(View.GONE);
                    saveTrack.setVisibility(View.GONE);
                    addTrack.setVisibility(View.VISIBLE);
                    deleteTrack.setText("DELETE TRACK");
                    deleteTrack.setVisibility(View.VISIBLE);
                    saveGpx.setVisibility(View.VISIBLE);
                    editedTrk.clear();
                    gpxCreator = gpxEditor;
                    gpxEditor = new ArrayList<>();
                    asd[0] = 0;
                    numbertrigger = 0;
                    fromStartPoly = new ArrayList<>();
                    fromStartMarkers = new ArrayList<>();
                    showSavedTrack();

                    String start = "\t<trk>\n" +
                            "\t\t<name>TTIT</name>\n";
                    String desc = "\t\t<desc>Length: " + length + "</desc>\n";
                    String trkseg = "\t\t<trkseg>\n";
                    String trkpt = "";
                    for (int b = 0; b < gpxCreator.size(); b++) {
                        Log.i("Latlng :", gpxCreator.get(b).toString());
                        trkpt += "\t\t\t<trkpt lat=\"" + gpxCreator.get(b).latitude + "\" lon=\"" + gpxCreator.get(b).longitude + "\"></trkpt>\n";
                    }
                    String trksegFinish = "\t\t</trkseg>\n";
                    String finish = "\t</trk>\n";

                    editedTrk.add(start + desc + trkseg + trkpt + trksegFinish + finish);
                }

            }
        });

        saveGpx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveMulti saveMulti = new SaveMulti();
                saveMulti.show(getSupportFragmentManager(),"Edited");
            }
        });




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showDeleteAtFirstTrack() {
        ArrayList<LatLng> temp1 = new ArrayList<>();
        ArrayList<LatLng> temp2 = new ArrayList<>();
        temp2 = gpxEditor;
        for (int i = temp2.size()-1 ; i >= 0 ; i--) {
            LatLng latLng = temp2.get(i);
            temp1.add(latLng);
        }
        gpxEditor = temp1;
        mMap.clear();
        firstPoint[0] = gpxEditor.get(gpxEditor.size()-1);
        System.out.println(firstPoint[0]);
        mMap.addMarker(new MarkerOptions().position(firstPoint[0]).title("Started Here"));
        MarkerOptions options = new MarkerOptions();
        PolylineOptions option = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true).clickable(true);
        for (int z = 0; z < gpxEditor.size(); z++) {
            LatLng point = gpxEditor.get(z);
            option.add(point);
            fromStartPoly.add(mMap.addPolyline(option));
        }
        Double j = 0.0;

        for (int i = 0; i< gpxEditor.size(); i++) {

            LatLng gpx = gpxEditor.get(i);
            //markerPoints.add(gpx);
            options.position(gpx);

            if (i == 0 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                options.anchor((float) 0.5,(float) 0.5);
                //options.snippet("0 KM");
                options.title("Starting Road");
                fromStartMarkers.add(mMap.addMarker(options));
            }else if (i == gpxEditor.size()-1){
                //LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_start));
                options.anchor((float) 0.5,(float) 0.5);
                //Double diss = CalculationByDistance(preGpx, gpx);
                //j  = j + diss;
                //options.snippet(String.format("%.3f", j) + " KM");
                options.title("Ending Road");
                fromStartMarkers.add(mMap.addMarker(options));
            } else {
                //LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                options.anchor((float) 0.5,(float) 0.5);
                //Double diss = CalculationByDistance(preGpx, gpx);
                //j  = j + diss;
                //options.snippet(String.format("%.3f", j) + " KM");
                options.title("On Going Road");
                fromStartMarkers.add(mMap.addMarker(options));
            }

        }

//        int i = (gpxEditor.size() - 1) / 2;
//        LatLng gpx = gpxEditor.get(i);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 16));
    }

    public void showDeleteAtEndTrack() {
        mMap.clear();
        lastPoint[0] = gpxEditor.get(gpxEditor.size()-1);
        System.out.println(lastPoint[0]);
        mMap.addMarker(new MarkerOptions().position(lastPoint[0]).title("Ended Here"));
        MarkerOptions options = new MarkerOptions();
        PolylineOptions option = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true).clickable(true);
        for (int z = 0; z < gpxEditor.size(); z++) {
            LatLng point = gpxEditor.get(z);
            option.add(point);
            fromStartPoly.add(mMap.addPolyline(option));
        }
        Double j = 0.0;

        for (int i = 0; i< gpxEditor.size(); i++) {

            LatLng gpx = gpxEditor.get(i);
            //markerPoints.add(gpx);
            options.position(gpx);

            if (i == 0 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_start));
                options.anchor((float) 0.5,(float) 0.5);
                //options.snippet("0 KM");
                options.title("Starting Road");
                fromStartMarkers.add(mMap.addMarker(options));
            }else if (i == gpxEditor.size()-1){
                //LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                options.anchor((float) 0.5,(float) 0.5);
                //Double diss = CalculationByDistance(preGpx, gpx);
                //j  = j + diss;
                //options.snippet(String.format("%.3f", j) + " KM");
                options.title("Ending Road");
                fromStartMarkers.add(mMap.addMarker(options));
            } else {
                //LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                options.anchor((float) 0.5,(float) 0.5);
                //Double diss = CalculationByDistance(preGpx, gpx);
                //j  = j + diss;
                //options.snippet(String.format("%.3f", j) + " KM");
                options.title("On Going Road");
                fromStartMarkers.add(mMap.addMarker(options));
            }

        }

//        int i = (gpxEditor.size() - 1) / 2;
//        LatLng gpx = gpxEditor.get(i);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 16));
    }

    public void showTrack() {
        if (multiGpxList.size() != 0) {
            resetToNewGpx = multiGpxList;
            for (int a = 0; a < multiGpxList.size(); a++) {
                ArrayList<Location> gpxList = multiGpxList.get(a).getMyLatlng();
                String lengthh = multiGpxList.get(a).getDescc();

                int index = lengthh.indexOf(" ");
                int index2 = lengthh.indexOf(" KM");
                Log.i("Index of 1st:", String.valueOf(index));
                Log.i("Index of 2nd:", String.valueOf(index2));
                String substr=lengthh.substring(index + 1, index2);
                System.out.println(substr);

                MarkerOptions options = new MarkerOptions();

                PolylineOptions option = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true).clickable(true).zIndex(Float.parseFloat(substr));
                for (int z = 0; z < gpxList.size(); z++) {
                    LatLng point = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                    option.add(point);
                    polylineList.add(mMap.addPolyline(option));
                }

                Double j = 0.0;

                for (int i = 0; i< gpxList.size(); i++) {

                    LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                    //markerPoints.add(gpx);
                    options.position(gpx);

                    if (i == 0 ) {
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_start));
                        options.anchor((float) 0.5,(float) 0.5);
                        options.snippet("0 KM");
                        options.title("Starting Road");
                        markers.add(mMap.addMarker(options));
                    }else if (i == gpxList.size()-1){
                        LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                        options.anchor((float) 0.5,(float) 0.5);
                        Double diss = CalculationByDistance(preGpx, gpx);
                        j  = j + diss;
                        options.snippet(String.format("%.3f", j) + " KM");
                        options.title("Ending Road");
                        markers.add(mMap.addMarker(options));
                    } else {
                        LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                        options.anchor((float) 0.5,(float) 0.5);
                        Double diss = CalculationByDistance(preGpx, gpx);
                        j  = j + diss;
                        options.snippet(String.format("%.3f", j) + " KM");
                        options.title("On Going Road");
                        markers.add(mMap.addMarker(options));
                    }

                }

                int i = (gpxList.size() - 1) / 2;
                LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 16));

            }
        } else {
            LatLng bangladesh = new LatLng(23.6850, 90.3563);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangladesh, 7));
        }

    }

    public void showSavedTrack() {
        mMap.clear();

        if (firstPoint[0] != new LatLng(0,0)) {
            mMap.addMarker(new MarkerOptions().position(firstPoint[0]).title("Started Here"));
        }
        if (lastPoint[0] != new LatLng(0,0)) {
            mMap.addMarker(new MarkerOptions().position(lastPoint[0]).title("Ended Here"));
        }

        MarkerOptions options = new MarkerOptions();

        PolylineOptions option = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true).clickable(true);
        for (int z = 0; z < gpxCreator.size(); z++) {
            LatLng point = gpxCreator.get(z);
            option.add(point);
        }

        mMap.addPolyline(option);

        Double j = 0.0;
        for (int i = 0; i< gpxCreator.size(); i++) {

            LatLng gpx = gpxCreator.get(i);
            //markerPoints.add(gpx);
            options.position(gpx);

            if (i == 0 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_start));
                options.anchor((float) 0.5,(float) 0.5);
                options.snippet("0 KM");
                mMap.addMarker(options).setTitle("Starting Road");
            }else if (i == gpxCreator.size()-1){
                LatLng preGpx = gpxCreator.get(i-1);

                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
                options.anchor((float) 0.5,(float) 0.5);
                Double diss = CalculationByDistance(preGpx, gpx);
                j  = j + diss;
                options.snippet(String.format("%.3f", j) + " KM");
                mMap.addMarker(options).setTitle("Ending Road");
            } else {
                LatLng preGpx = gpxCreator.get(i-1);

                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_middle));
                options.anchor((float) 0.5,(float) 0.5);
                Double diss = CalculationByDistance(preGpx, gpx);
                j  = j + diss;
                options.snippet(String.format("%.3f", j) + " KM");
                mMap.addMarker(options).setTitle("On Going Road");
            }

        }

        length = String.format("%.3f", j) + " KM";

//        int i = (gpxCreator.size() - 1) / 2;
//        LatLng gpx = gpxCreator.get(i);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 16));
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                } else {
                    latLng = new LatLng(23.6850, 90.3563);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
                }

            }
        });



    }
}