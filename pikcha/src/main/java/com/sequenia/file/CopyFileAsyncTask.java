package com.sequenia.file;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Копирование файла не в основном потоке
 */
class CopyFileAsyncTask extends AsyncTask<Uri, Void, Boolean> {

    private CopyFileListener listener;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    private File outputFile;

    CopyFileAsyncTask(Context context, File outputFile, CopyFileListener listener) {
        this.context = context;
        this.outputFile = outputFile;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.startCopyFile();
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {

        if (uris.length < 1) {
            return false;
        }

        Uri inputFileUri = uris[0];

        try {

            ContentResolver contentResolver = context.getContentResolver();
            ParcelFileDescriptor inputFileDescriptor = contentResolver
                    .openFileDescriptor(inputFileUri, "r");

            if (inputFileDescriptor == null) {
                return false;
            }

            InputStream inputStream = new FileInputStream(inputFileDescriptor.getFileDescriptor());
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
