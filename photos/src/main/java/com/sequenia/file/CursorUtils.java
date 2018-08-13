package com.sequenia.file;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Методы по работе с Cursor
 */
public class CursorUtils {

    /**
     * @return путь к последнему доступному изображению
     */
    public static String getLastImageFile(Context context) {
        String path = null;
        // Find the last picture
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        final Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        // Put it in the image view
        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(1);
            cursor.close();
        }

        return path;
    }
}
