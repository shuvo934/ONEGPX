package com.shuvo.ttit.onegpx.dialogue;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shuvo.ttit.onegpx.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    NestedScrollView fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    String address = "";

    AlertDialog dialog;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    Logger logger = Logger.getLogger(ImageDialogue.class.getName());

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.image_view, null);

        imageView = view.findViewById(R.id.image_captured);
        fileName = view.findViewById(R.id.editTextImage);
        fileLayout = view.findViewById(R.id.editTextImage_layout);
        save = view.findViewById(R.id.save_image);
        cancel = view.findViewById(R.id.cancel_save_image);
        fullLayout = view.findViewById(R.id.image_load_full_layout);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_image_load);
        circularProgressIndicator.setVisibility(View.GONE);

        builder.setView(view);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        fileName.setText(imageFileName);
//        if (bitmap != null)
//        imageView.setImageBitmap(bitmap);


        cancel.setOnClickListener(v -> {
            File deletefile = new File(currentPhotoPath);
            boolean deleted = deletefile.delete();
            if (deleted) {
                System.out.println("deleted");
            }
            dialog.dismiss();
        });

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
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        save.setOnClickListener(v -> {
            if (fileName.getText() == null || fileName.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Please Write Your File Name", Toast.LENGTH_SHORT).show();
            } else {
                // deleting file
                if (currentPhotoPath != null) {
                    File deleteFile = new File(currentPhotoPath);
                    if (deleteFile.exists()) {
                        boolean deleted = deleteFile.delete();
                        if (deleted) {
                            Log.i("ImageDialogue", "Temp photo deleted");
                        }
                    }
                }

                String imageFileName = fileName.getText().toString().trim() + ".jpg";
                Context context = requireContext();

                circularProgressIndicator.setVisibility(View.VISIBLE);
                fullLayout.setVisibility(View.GONE);
                executor.execute(() -> {
                    try {
                        // Step 1️⃣: Create temp file in app cache dir
                        File tempFile = new File(context.getCacheDir(), imageFileName);
                        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        }
                        // Step 2️⃣: Write EXIF metadata to temp file
                        ExifInterface exif = new ExifInterface(tempFile.getAbsolutePath());
                        setExifLocation(exif);
                        exif.saveAttributes();

                        // Step 3️⃣: Insert final image with EXIF into MediaStore
                        ContentResolver resolver = context.getContentResolver();
                        Uri imageUri;
                        OutputStream outStream;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
                            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/Camera");

                            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                            if (imageUri == null) throw new IOException("Failed to insert MediaStore record.");

                            outStream = resolver.openOutputStream(imageUri);
                        }
                        else {
                            // Fallback for old Android
                            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
                            if (!dir.exists()) dir.mkdirs();

                            File destFile = new File(dir, imageFileName);
                            outStream = new FileOutputStream(destFile);
                            imageUri = Uri.fromFile(destFile);
                            currentPhotoPath = destFile.getAbsolutePath();
                        }

                        // Step 4️⃣: Copy temp file (with EXIF) into MediaStore
                        try (InputStream inStream = new FileInputStream(tempFile)) {
                            byte[] buffer = new byte[4096];
                            int read;
                            while ((read = inStream.read(buffer)) != -1) {
                                outStream.write(buffer, 0, read);
                            }
                            outStream.flush();
                        }
                        outStream.close();

                        // Step 5️⃣: Cleanup
                        tempFile.delete();

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            galleryAddPic(); // optional
                        }

                        // Step 6️⃣: Notify success on UI thread
                        mainHandler.post(() -> {
                            circularProgressIndicator.setVisibility(View.GONE);
                            fullLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Photo saved to Camera", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                    }
                    catch (Exception e) {
                        logger.log(Level.WARNING,e.getMessage(),e);
                        mainHandler.post(() -> {
                            circularProgressIndicator.setVisibility(View.GONE);
                            fullLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Could not save photo", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
//                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + imageFileName);
//                try {
//                    file.createNewFile();
//                    currentPhotoPath = file.getAbsolutePath();
//                    fileOutputStream = new FileOutputStream(file);
//
//                }
//                catch (Exception e) {
//                    logger.log(Level.WARNING,e.getMessage(),e);
//                }
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//                try {
//                    fileOutputStream.flush();
//                }
//                catch (Exception e) {
//                    logger.log(Level.WARNING,e.getMessage(),e);
//                }
//                try {
//                    fileOutputStream.close();
//                }
//                catch (Exception e) {
//                    logger.log(Level.WARNING,e.getMessage(),e);
//                }




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
//                }
//                catch (Exception e) {
//                    logger.log(Level.WARNING,e.getMessage(),e);
//                }
//
//                scanFile(getContext(),currentPhotoPath, null);
//                Toast.makeText(getContext(), "Photo is saved", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();

            }


        });

        runGetAddress();
        return dialog;
    }

//    public void saveToGallery() {
//        if (currentPhotoPath != null) {
//            File deleteFile = new File(currentPhotoPath);
//            if (deleteFile.exists()) {
//                boolean deleted = deleteFile.delete();
//                if (deleted) {
//                    Log.i("ImageDialogue", "Temp photo deleted");
//                }
//            }
//        }
//
//        FileOutputStream fileOutputStream = null;
//        String imageFileName = fileName.getText().toString().trim() + ".jpg";
//        OutputStream outputStream;
//        Uri imageUri;
//
//        try {
//            ContentResolver resolver = requireContext().getContentResolver();
//            String relativeLocation = Environment.DIRECTORY_DCIM + File.separator + "Camera";
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // Use MediaStore on Android 10+
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
//                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);
//
//                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//                if (imageUri == null) {
//                    throw new IOException("Failed to create new MediaStore record.");
//                }
//                outputStream = resolver.openOutputStream(imageUri);
//            } else {
//                // Legacy path for older Android
//                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
//                if (!dir.exists()) {
//                    boolean created = dir.mkdirs();
//                    Log.i("ImageDialogue", "Created Camera dir: " + created);
//                }
//                File imageFile = new File(dir, imageFileName);
//                outputStream = new FileOutputStream(imageFile);
//                imageUri = Uri.fromFile(imageFile);
//                currentPhotoPath = imageFile.getAbsolutePath();
//            }
//
//            if (outputStream != null) {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                outputStream.flush();
//                outputStream.close();
//            }
//
//            // If you have a path, write EXIF GPS info
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && currentPhotoPath != null) {
//                writeExifLocation(currentPhotoPath);
//            }
//            else if (imageUri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // On Q+, EXIFInterface can work with streams
//                try (InputStream in = resolver.openInputStream(imageUri);
//                     OutputStream out = resolver.openOutputStream(imageUri)) {
//                    if (in != null && out != null) {
//                        ExifInterface exif = new ExifInterface(in);
//                        setExifLocation(exif);
//                        exif.saveAttributes();
//                    }
//                }
//                catch (Exception e) {
//                    logger.log(Level.WARNING,e.getMessage(),e);
//                }
//            }
//
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                galleryAddPic(); // optional
//            }
//
//            Toast.makeText(getContext(), "Photo saved to Camera", Toast.LENGTH_SHORT).show();
//            dialog.dismiss();
//        }
//        catch (Exception e) {
//            logger.log(Level.WARNING,e.getMessage(),e);
//            Toast.makeText(getContext(), "Could not save photo", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void writeExifLocation(String path) {
//        try {
//            ExifInterface exif = new ExifInterface(path);
//            setExifLocation(exif);
//            exif.saveAttributes();
//        } catch (IOException e) {
//            logger.log(Level.WARNING,e.getMessage(),e);
//        }
//    }

    private void setExifLocation(ExifInterface exif) {
        if (targetLocation == null) return;
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convert(targetLocation.getLatitude()));
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(targetLocation.getLatitude()));
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convert(targetLocation.getLongitude()));
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(targetLocation.getLongitude()));
    }

    private void closeKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            System.out.println("FOCUS Check");
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        requireContext().sendBroadcast(mediaScanIntent);

    }

    private static String convert(double latitude) {
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

//    public static void scanFile(Context context, String path, String mimeType ) {
//        Client client = new Client(path, mimeType);
//        MediaScannerConnection connection =
//                new MediaScannerConnection(context, client);
//        client.connection = connection;
//        connection.connect();
//    }

//    private static final class Client implements MediaScannerConnection.MediaScannerConnectionClient {
//        private final String path;
//        private final String mimeType;
//        MediaScannerConnection connection;
//
//        public Client(String path, String mimeType) {
//            this.path = path;
//            this.mimeType = mimeType;
//        }
//
//        @Override
//        public void onMediaScannerConnected() {
//            connection.scanFile(path, mimeType);
//        }
//
//        @Override
//        public void onScanCompleted(String path, Uri uri) {
//            connection.disconnect();
//        }
//    }

    private void runGetAddress() {
        // onPreExecute
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);

        executor.execute(() -> {
            // doInBackground
            double lat = targetLocation.getLatitude();
            double lng = targetLocation.getLongitude();

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (Geocoder.isPresent()) {
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
                }
                else {
                    address = "Address Not Found";
                }
            }
            catch (IOException e) {
                address = "Address Not Found";
            }

            // onPostExecute (on main thread)
            mainHandler.post(() -> {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy, hh:mm:ss a", Locale.getDefault());
                Date c = Calendar.getInstance().getTime();
                String dd = simpleDateFormat.format(c);
                System.out.println(dd);
                String timeLatLng = "Time: " + dd + "\n" + "Latitude: " + targetLocation.getLatitude() + "\n" + "Longitude: " + targetLocation.getLongitude();
                address = timeLatLng + "\n"+ "Address: " + address;
                System.out.println(address);
                Resources resources = getResources();
                float scale = resources.getDisplayMetrics().density;
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
//            float x = (bitmap.getWidth() - textWidth)/2;
//            float y = (bitmap.getHeight() - textHeight)/2;


                // draw text to the Canvas center
                int yyyy = bitmap.getHeight() - textHeight - 16;
                canvas.save();
                canvas.translate(5, yyyy);
                textLayout.draw(canvas);
                canvas.restore();
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);
            });
        });
    }
}
