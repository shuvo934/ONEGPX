package com.shuvo.ttit.onegpx.fileChooser;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    private static final String LOG_TAG = "FileUtils";

//    private static Uri contentUri = null;

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        // Check here to KITKAT or new version
        final boolean isKitKat = true;
        String selection = null;
        String[] selectionArgs = null;
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                String fullPath = getPathFromExtSD(split);
                if (!fullPath.isEmpty()) {
                    return fullPath;
                } else {
                    return null;
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                {
                    final String id;
                    Cursor cursor = null;
                    try {
                        cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[] {
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };
                        for (String contentUriPrefix : contentUriPrefixesToTry) {
                            try {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));

                                // final Uri contentUri = ContentUris.withAppendedId(
                                //        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                                return getDataColumn(context, contentUri, null, null);
                            } catch (NumberFormatException e) {
                                // In Android 8 and Android P the id is not a number
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            } else if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
        }


        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
            if( Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                // return getFilePathFromURI(context,uri);
                return getMediaFilePathForN(uri, context);
                // return getRealPathFromURI(context,uri);
            } else {
                return getDataColumn(context, uri, null, null);
            }
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Check if a file exists on device
     *
     * @param filePath The absolute file path
     */
    private static boolean fileExists(String filePath) {
        File file = new File(filePath);

        return file.exists();
    }


    /**
     * Get full file path from external storage
     *
     * @param pathData The storage type and the relative path
     */
    private static String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath = "";

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        return fullPath;
    }
    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor returnCursor = contentResolver.query(returnUri, null, null, null, null);

        // Get the column indexes of the data in the Cursor,
        // move to the first row in the Cursor, get the data,
        // and display it.

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            // int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return file.getPath();
    }

    private static String getMediaFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);

        // Get the column indexes of the data in the Cursor,
        // move to the first row in the Cursor, get the data,
        // and display it.

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return file.getPath();
    }

    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) //
                || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    public static String copyUriToCache(Context context, Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            String name = getFileName(resolver, uri);
            if (name == null) {
                name = "tempfile";
            }
            File file = new File(context.getCacheDir(), name);

            try (InputStream in = resolver.openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("FileCopy", "Failed to copy", e);
            return null;
        }
    }

    private static String getFileName(ContentResolver resolver, Uri uri) {
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        return cursor.getString(nameIndex);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }
}
