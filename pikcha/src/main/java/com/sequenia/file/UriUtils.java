package com.sequenia.file;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Методы по работе с URI
 */
public class UriUtils {

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

        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                file
        );
    }

    /**
     * Получение URI из данных
     *
     * @param data - данные, откуда достаются URI
     * @return - URI
     */
    public static Uri getUriFromData(Intent data) {
        Uri uri = data.getData();

        if (uri != null) {
            return uri;
        }

        ClipData clipdata = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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
