package com.sequenia.photo.sources;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.sequenia.photo.listeners.PhotoDifferentResultsListener;
import com.sequenia.photo.listeners.PhotoErrorListener;
import com.sequenia.photo.listeners.PhotoResultListener;
import com.sequenia.photo.listeners.PhotoWaitListener;
import com.sequenia.photo.listeners.StartIntentForResult;
import com.sequenia.photo.sources.permissions.PhotoPermissionChecker;

/**
 * Выбираемый пользователь источник изображений
 */
public class SelectableSource extends Source {

    private static final int SELECT_METHOD_OF_ADDING_PHOTO_REQUEST = 30303;

    private CameraSource cameraSource;
    private GallerySource gallerySource;

    public SelectableSource(Context context) {
        super(context);
        cameraSource = new CameraSource(context);
        gallerySource = new GallerySource(context);
    }

    @Override
    public void setDifferentResultsListener(PhotoDifferentResultsListener listener) {
        super.setDifferentResultsListener(listener);
        cameraSource.setDifferentResultsListener(listener);
        gallerySource.setDifferentResultsListener(listener);
    }

    @Override
    public void setErrorsListener(PhotoErrorListener listener) {
        super.setErrorsListener(listener);
        cameraSource.setErrorsListener(listener);
        gallerySource.setErrorsListener(listener);
    }

    @Override
    public void setResultListener(PhotoResultListener listener) {
        super.setResultListener(listener);
        cameraSource.setResultListener(listener);
        gallerySource.setResultListener(listener);
    }

    @Override
    public void setIntentForResult(StartIntentForResult intentForResult) {
        super.setIntentForResult(intentForResult);
        cameraSource.setIntentForResult(intentForResult);
        gallerySource.setIntentForResult(intentForResult);
    }

    public void setWaitListener(PhotoWaitListener waitListener) {
        cameraSource.setWaitListener(waitListener);
    }

    @Override
    protected void checkPermission(PhotoPermissionChecker permissionChecker) {
        permissionChecker.permissionForChooser();
    }

    @Override
    protected void parseResultData(Intent data) {
        if (data == null) {
            cameraSource.parseResultData(data);
            return;
        }

        gallerySource.parseResultData(data);
    }

    @Override
    protected int getRequestCode() {
        return SELECT_METHOD_OF_ADDING_PHOTO_REQUEST;
    }

    @Override
    protected Intent getIntent() {
        Intent cameraIntent = cameraSource.getIntent();
        Intent galleryIntent = gallerySource.getIntent();

        Parcelable[] initialIntents = cameraIntent == null ?
                new Parcelable[0] : new Parcelable[]{cameraIntent};

        Intent chooserIntent = Intent.createChooser(galleryIntent, null);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents);

        return chooserIntent;
    }
}
