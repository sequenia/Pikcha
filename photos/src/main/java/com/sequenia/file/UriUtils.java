package com.sequenia.file;

import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.sequenia.photo.listeners.GetPathCallback;

import java.io.File;
import java.io.IOException;

/**
 * Методы по работе с URI
 */
public class UriUtils {

    /**
     * Получить абсолютный путь к файлу по uri
     *
     * @param context  - контекст
     * @param uri      - uri к файлу
     * @param callback - функция обратного вызовы для обработки результата
     */
    public static void getPath(Context context, Uri uri, GetPathCallback callback) {
        new GetPathAsyncTask(context, uri, callback).execute();
    }

    /**
     * Получает путь к файлу по URI
     *
     * @param context - контекст
     * @param uri     - файла
     * @return - путь к файлы
     */
    static String getPath(Context context, Uri uri) throws IOException {

        Uri contentUri = null;
        String selection = null;
        String[] selectionArgs = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                String id = DocumentsContract.getDocumentId(uri);

                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }

                String[] contentUriPrefixesToTry = new String[]{
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads",
                        "content://downloads/all_downloads"
                };

                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix),
                            Long.valueOf(id));
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            contentUri = uri;
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        if (contentUri != null) {
            try {
                String path = getDataColumn(context, contentUri, selection, selectionArgs);
                if (path != null) {
                    return path;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Попробуем перекопировать в другую директорию файл
        return FilesUtils.copyFile(context, uri);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Получение URI для файла
     *
     * @param context - контекст
     * @param file    - файл
     * @return - URI
     */
    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(file);
        }
        return FileProvider.getUriForFile(context,
                context.getPackageName() + ".provider", file);
    }

    /**
     * Получение URIS из данных
     *
     * @param data - данные, откуда достаются URI
     * @return - список URI
     */
    public static Uri getUrisFromData(Intent data) {
        Uri uri = data.getData();

        if (uri != null) {
            return uri;
        }

        ClipData clipdata = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            clipdata = data.getClipData();
        }

        if (clipdata == null) {
            return null;
        }

        if (clipdata.getItemCount() > 0) {
            return clipdata.getItemAt(0).getUri();
        }

        return null;
    }
}
