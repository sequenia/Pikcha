package com.sequenia.file;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.sequenia.photo.listeners.GetPathCallback;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.sequenia.ErrorCodes.EXCEPTION;

public class GetPathAsyncTask extends AsyncTask<Void, Void, String> {

    private WeakReference<Context> weakReferenceContext;

    private Uri uri;
    private GetPathCallback callback;

    GetPathAsyncTask(Context context, Uri uri, GetPathCallback callback) {
        weakReferenceContext = new WeakReference<>(context);
        this.uri = uri;
        this.callback = callback;
    }

    private Context getContext() {
        if (weakReferenceContext == null) {
            return null;
        }

        return weakReferenceContext.get();
    }

    @Override
    protected String doInBackground(Void... uris) {
        try {
            return UriUtils.getPath(getContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return IOException.class.getName() + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.startsWith(IOException.class.getName())) {
            callback.onError(EXCEPTION);
            return;
        }

        callback.onSuccess(result);
    }
}
