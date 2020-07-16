package com.sequenia.file;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Расширение функционала файла
 */
public class FileExtension {

    private Uri uri;
    private Context context;

    public FileExtension(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    /**
     * Получить расширение файла
     *
     * @return расширение файла
     */
    public String getExp() {
        String type = getContentResolver().getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
    }

    /**
     * Удалить файл
     */
    public void delete() {
        getContentResolver().delete(uri, null, null);
    }

    /**
     * Проверка файла на существование и пустоту
     *
     * @return - true - файл существует и информация в нем корректна
     */
    public boolean exist() throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        boolean fileExist = inputStream != null && inputStream.available() > 0;

        if (inputStream != null) {
            inputStream.close();
        }

        return fileExist;
    }

    /**
     * Копировать файл
     *
     * @param outputFile файл, в который сохранить копию
     */
    public void copyTo(File outputFile, @NonNull CopyFileListener listener) {

        new CopyFileAsyncTask(
                context,
                outputFile,
                listener
        ).execute(uri);

    }

    private ContentResolver getContentResolver() {
        return context.getContentResolver();
    }

}
