package com.shuvo.ttit.onegpx.dialogue;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shuvo.ttit.onegpx.R;

import java.io.File;
import java.io.FileOutputStream;

import static com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti.bitmap;
import static com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti.currentPhotoPath;
import static com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti.imageFileName;
import static com.shuvo.ttit.onegpx.createMenu.MapsActivityForMulti.targetLocation;


public class ImageDialogue extends AppCompatDialogFragment {

    ImageView imageView;
    TextInputEditText fileName;
    TextInputLayout fileLayout;
    Button cancel;
    Button save;

    AlertDialog dialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.image_view, null);

        imageView = view.findViewById(R.id.image_captured);
        fileName = view.findViewById(R.id.editTextImage);
        fileLayout = view.findViewById(R.id.editTextImage_layout);
        save = view.findViewById(R.id.save_image);
        cancel = view.findViewById(R.id.cancel_save_image);

        builder.setView(view);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        fileName.setText(imageFileName);
        if (bitmap != null)
        imageView.setImageBitmap(bitmap);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File deletefile = new File(currentPhotoPath);
                boolean deleted = deletefile.delete();
                if (deleted) {
                    System.out.println("deleted");
                }
                dialog.dismiss();
            }
        });

        fileName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fileLayout.setHint("?????????????????? ????????? / File Name");
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
                        closeKeyBoard();

                        return false; // consume.
                    }
                }
                return false;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please Write Your File Name", Toast.LENGTH_SHORT).show();
                } else {
                    // deleting file
                    File deletefile = new File(currentPhotoPath);
                    boolean deleted = deletefile.delete();
                    if (deleted) {
                        System.out.println("deleted");
                    }

                    FileOutputStream fileOutputStream = null;
//                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//                    System.out.println(timeStamp);
                    String imageFileName = fileName.getText().toString() +".jpg";
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + imageFileName);
                    try {
                        file.createNewFile();
                        currentPhotoPath = file.getAbsolutePath();
                        fileOutputStream = new FileOutputStream(file);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    try {
                        fileOutputStream.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    galleryAddPic();

                    String imaaa = file.getAbsolutePath();

                    //Writes Exif Information to the Image
                    try {
                        ExifInterface exif = new ExifInterface(imaaa);
                        Log.w("Location", String.valueOf(targetLocation));

                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convert(targetLocation.getLatitude()));
                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(targetLocation.getLatitude()));
                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convert(targetLocation.getLongitude()));
                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(targetLocation.getLongitude()));

                        exif.saveAttributes();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    scanFile(getContext(),currentPhotoPath, null);
                    Toast.makeText(getContext(), "Photo is saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();


                }


            }
        });

        return dialog;
    }

    private void closeKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            System.out.println("FOCUS Check");
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);

    }

    private static final String convert(double latitude) {
        latitude= Math.abs(latitude);
        int degree = (int) latitude;
        latitude *= 60;
        latitude -= (degree * 60.0d);
        int minute = (int) latitude;
        latitude *= 60;
        latitude -= (minute * 60.0d);
        int second = (int) (latitude*1000.0d);

        StringBuilder sb = new StringBuilder(20);
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000");
        return sb.toString();
    }

    private static String latitudeRef(double latitude) {
        return latitude<0.0d?"S":"N";
    }

    private static String longitudeRef(double longitude) {
        return longitude<0.0d?"W":"E";
    }

    public static void scanFile(Context context, String path, String mimeType ) {
        Client client = new Client(path, mimeType);
        MediaScannerConnection connection =
                new MediaScannerConnection(context, client);
        client.connection = connection;
        connection.connect();
    }

    private static final class Client implements MediaScannerConnection.MediaScannerConnectionClient {
        private final String path;
        private final String mimeType;
        MediaScannerConnection connection;

        public Client(String path, String mimeType) {
            this.path = path;
            this.mimeType = mimeType;
        }

        @Override
        public void onMediaScannerConnected() {
            connection.scanFile(path, mimeType);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            connection.disconnect();
        }
    }
}
