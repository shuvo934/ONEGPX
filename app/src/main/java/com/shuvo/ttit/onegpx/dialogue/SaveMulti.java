package com.shuvo.ttit.onegpx.dialogue;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shuvo.ttit.onegpx.MapsMenu.MapsActivity;
import com.shuvo.ttit.onegpx.R;
import com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti;
import com.shuvo.ttit.onegpx.gpxConverter.GPXWriterForMultiple;

import java.io.File;
import java.io.IOException;

import static com.shuvo.ttit.onegpx.createMenu.MapsActivityCreate.editedTrk;
import static com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti.trk;


public class SaveMulti extends AppCompatDialogFragment {


    Button cancel;
    Button save;
    TextInputEditText fileName;
    TextInputLayout fileLayout;
    String fileExten = ".gpx";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_multi, null);

        fileName = view.findViewById(R.id.editTextMulti);
        fileLayout = view.findViewById(R.id.editTextMulti_layout);
        save = view.findViewById(R.id.save_multiSave);
        cancel = view.findViewById(R.id.cancel_multiSAve);


        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        fileName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fileLayout.setHint("ফাইলের নাম / File Name");
                return false;
            }
        });

        fileName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                        event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        Log.i("Let see", "Come here");
                        fileName.clearFocus();
                        InputMethodManager mgr = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        return false; // consume.
                    }
                }
                return false;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MapsActivity.editOrNot) {
                    if (fileName.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Please Write Your File Name", Toast.LENGTH_SHORT).show();
                    } else {
                        MapsActivityForMulti.fileName = fileName.getText().toString();
                        File myExternalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName.getText().toString()+fileExten);
                        try {
                            GPXWriterForMultiple.writeGpxFile("TTITGenerator", trk, myExternalFile);
                            Toast.makeText(getContext(), "Saved to Download", Toast.LENGTH_SHORT).show();
//                        trk.clear();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Could Not Save", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                } else {
                    if (fileName.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Please Write Your File Name", Toast.LENGTH_SHORT).show();
                    } else {
                        //MapsActivityForMulti.fileName = fileName.getText().toString();
                        File myExternalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName.getText().toString()+fileExten);
                        try {
                            GPXWriterForMultiple.writeGpxFile("TTITGenerator", editedTrk, myExternalFile);
                            Toast.makeText(getContext(), "Saved to Download", Toast.LENGTH_SHORT).show();
//                        trk.clear();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Could Not Save", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }

            }
        });

        return dialog;
    }


}
