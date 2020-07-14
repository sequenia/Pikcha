package com.sequenia.file;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public void copyTo(File outputFile, @NonNull CopyFileListener listener) throws IOException {

        new CopyFileAsyncTask(
                context,
                uri,
                outputFile,
                listener
        ).execute();

    }

    private ContentResolver getContentResolver() {
        return context.getContentResolver();
    }

    private static class CopyFileAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private FileDescriptor inputFileDescriptor;
        private CopyFileListener listener;
        private File outputFile;

        CopyFileAsyncTask(Context context, Uri inputFileUri, File outputFile,
                          CopyFileListener listener) throws FileNotFoundException {

            ContentResolver contentResolver = context.getContentResolver();
            ParcelFileDescriptor inputFileDescriptor = contentResolver
                    .openFileDescriptor(inputFileUri, "r");

            if (inputFileDescriptor != null) {
                this.inputFileDescriptor = inputFileDescriptor.getFileDescriptor();
            }

            this.outputFile = outputFile;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listener.startCopyFile();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                if (inputFileDescriptor == null) {
                    return false;
                }

                InputStream inputStream = new FileInputStream(inputFileDescriptor);
                OutputStream outStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }

                outStream.flush();
                inputStream.close();
                outStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isFiledCopied) {
            super.onPostExecute(isFiledCopied);

            listener.endCopyFile();

            if (isFiledCopied) {
                listener.copiedFile(outputFile);
            } else {
                listener.fileNotCopied();
            }
        }
    }
}
