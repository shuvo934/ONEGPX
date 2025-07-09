package com.shuvo.ttit.onegpx.dialogue;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.shuvo.ttit.onegpx.createMenu.MapsActivityCreate.editedTrk;
import static com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti.shareMulti;
import static com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti.trk;


public class SaveMulti extends AppCompatDialogFragment {


    Button cancel;
    Button save;
    TextInputEditText fileName;
    TextInputLayout fileLayout;

    Logger logger = Logger.getLogger(SaveMulti.class.getName());

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_multi, null);

        fileName = view.findViewById(R.id.editTextMulti);
        fileLayout = view.findViewById(R.id.editTextMulti_layout);
        save = view.findViewById(R.id.save_multiSave);
        cancel = view.findViewById(R.id.cancel_multiSAve);


        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        fileName.setOnTouchListener((view1, motionEvent) -> {
            fileLayout.setHint("ফাইলের নাম / File Name");
            return false;
        });

        fileName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    fileName.clearFocus();
                    InputMethodManager mgr = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    return false; // consume.
                }
            }
            return false;
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        save.setOnClickListener(v -> {
            if (!MapsActivity.editOrNot) {
                if (Objects.requireNonNull(fileName.getText()).toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please Write Your File Name", Toast.LENGTH_SHORT).show();
                } else {
                    MapsActivityForMulti.fileName = fileName.getText().toString();
                    String fileNameInput = fileName.getText().toString() + ".gpx";
                    try {
                        OutputStream out = openGpxFileOutputStream(getContext(), fileNameInput);
                        GPXWriterForMultiple.new_writeGpxFile("TTITGenerator", trk, out);
                        Toast.makeText(getContext(), "Saved to Download", Toast.LENGTH_SHORT).show();
                        shareMulti.setVisibility(View.VISIBLE);
//                        trk.clear();

                    } catch (IOException e) {
                        logger.log(Level.WARNING,e.getMessage(),e);
                        Toast.makeText(getContext(), "Could Not Save", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            } else {
                if (Objects.requireNonNull(fileName.getText()).toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please Write Your File Name", Toast.LENGTH_SHORT).show();
                } else {
                    //MapsActivityForMulti.fileName = fileName.getText().toString();
                    String fileNameInput = fileName.getText().toString() + ".gpx";
                    try {
                        OutputStream out = openGpxFileOutputStream(getContext(), fileNameInput);
                        GPXWriterForMultiple.new_writeGpxFile("TTITGenerator", editedTrk, out);
                        Toast.makeText(getContext(), "Saved to Download", Toast.LENGTH_SHORT).show();
//                        trk.clear();

                    } catch (IOException e) {
                        logger.log(Level.WARNING,e.getMessage(),e);
                        Toast.makeText(getContext(), "Could Not Save", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            }

        });

        return dialog;
    }

    public static OutputStream openGpxFileOutputStream(Context context, String fileNameWithExt) throws IOException {
        OutputStream outputStream;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // API 29+ uses MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileNameWithExt);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/gpx+xml");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri itemUri = resolver.insert(collection, values);
            if (itemUri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }
            outputStream = resolver.openOutputStream(itemUri);
        } else {
            // Legacy method for API <29
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileNameWithExt);
            outputStream = new FileOutputStream(file);
        }

        return outputStream;
    }
}
