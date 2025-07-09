package com.shuvo.ttit.onegpx.fileChooser;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shuvo.ttit.onegpx.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.shuvo.ttit.onegpx.fileChooser.FragmentFileChooser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFileChooser extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    TextInputEditText buttonBrowse;
//    private TextInputLayout editTextPath;
    private static final String LOG_TAG = "AndroidExample";
    private String fileLocation = null;


    public FragmentFileChooser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFileChooser.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFileChooser newInstance(String param1, String param2) {
        FragmentFileChooser fragment = new FragmentFileChooser();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_file_chooser, container, false);
//        editTextPath = rootView.findViewById(R.id.editTextTextPersonName);
//        buttonBrowse = rootView.findViewById(R.id.button);

//        buttonBrowse.setTag(buttonBrowse.getKeyListener());
//
//        buttonBrowse.setKeyListener(null);
//
//        buttonBrowse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                askPermissionAndBrowseFile();
//            }
//        });
        return rootView;
    }

//    private void askPermissionAndBrowseFile()  {
//        // With Android Level >= 23, you have to ask the user
//        // for permission to access External Storage.
//        // Level 23
//
//        // Check if we have Call permission
//        int permisson = ActivityCompat.checkSelfPermission(this.getContext(),
//                Manifest.permission.READ_EXTERNAL_STORAGE);
//
//        if (permisson != PackageManager.PERMISSION_GRANTED) {
//            // If don't have permission so prompt the user.
//            this.requestPermissions(
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    MY_REQUEST_CODE_PERMISSION
//            );
//            return;
//        }
//        this.doBrowseFile();
//    }

//    private void doBrowseFile()  {
//        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        chooseFileIntent.setType("*/*");
//        // Only return URIs that can be opened with ContentResolver
//        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
//        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case MY_REQUEST_CODE_PERMISSION: {
//
//                // Note: If request is cancelled, the result arrays are empty.
//                // Permissions granted (CALL_PHONE).
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    Log.i( LOG_TAG,"Permission granted!");
//                    Toast.makeText(this.getContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
//
//                    this.doBrowseFile();
//                }
//                // Cancelled or denied.
//                else {
//                    Log.i(LOG_TAG,"Permission denied!");
//                    Toast.makeText(this.getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        switch (requestCode) {
//            case MY_RESULT_CODE_FILECHOOSER:
//                if (resultCode == Activity.RESULT_OK ) {
//                    if(data != null)  {
//                        Uri fileUri = data.getData();
//                        Log.i(LOG_TAG, "Uri: " + fileUri);
//
//                        String filePath = null;
//                        try {
////                            File file = new File(fileUri.getPath());
////                            filePath = file.getPath();
//
//                            filePath = FileUtils.getPath(this.getContext(),fileUri);
//                        } catch (Exception e) {
//                            Log.e(LOG_TAG,"Error: " + e);
//                            Toast.makeText(this.getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
//                        }
//                        int sub = filePath.lastIndexOf("/");
//                        int all = filePath.length();
//                        System.out.println(sub);
//                        System.out.println(all);
//                        String subss = filePath.substring(sub + 1, all);
//                        System.out.println(subss);
//                        this.buttonBrowse.setText(subss);
//                        this.editTextPath.setHint("ফাইলের নাম");
//                        fileLocation = filePath;
//
//                    }
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public String getPath()  {
//        return fileLocation;
//    }
}