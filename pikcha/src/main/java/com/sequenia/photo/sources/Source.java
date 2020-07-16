package com.sequenia.photo.sources;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sequenia.ErrorCode;
import com.sequenia.photo.listeners.PhotoDifferentResultsListener;
import com.sequenia.photo.listeners.PhotoErrorListener;
import com.sequenia.photo.listeners.PhotoResultListener;
import com.sequenia.photo.listeners.StartIntentForResult;
import com.sequenia.photo.sources.permissions.PermissionDeniedListener;
import com.sequenia.photo.sources.permissions.PermissionGrantedListener;
import com.sequenia.photo.sources.permissions.PhotoPermissionChecker;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.sequenia.ErrorCode.CONTEXT_NOT_FOUND;
import static com.sequenia.ErrorCode.INTENT_NOT_SET;
import static com.sequenia.ErrorCode.PERMISSION_DENIED;

/**
 * Источники изображений
 */
public abstract class Source {
    /*
     * Хранение контекста
     */
    private WeakReference<Context> weakReferenceContext;

    /**
     * Слушатели на ошибки
     */
    private PhotoErrorListener errorsListener;

    /**
     * Возвращение рузультата с камеры
     */
    private PhotoResultListener resultListener;

    /**
     * Интерфейс на реализацию метода открытия intent
     */
    private StartIntentForResult intentForResult;

    /**
     * Интерфейс на реализацию метода открытия intent
     */
    protected PhotoDifferentResultsListener differentResultsListener;

    public Source(Context context) {
        weakReferenceContext = new WeakReference<>(context);
    }

    protected abstract void checkPermission(PhotoPermissionChecker permissionChecker);

    protected abstract void parseResultData(Intent data);

    protected abstract int getRequestCode();

    protected abstract Intent getIntent();

    public void setErrorsListener(PhotoErrorListener errorsListener) {
        this.errorsListener = errorsListener;
    }

    public void setResultListener(PhotoResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void setIntentForResult(StartIntentForResult intentForResult) {
        this.intentForResult = intentForResult;
    }

    public void setDifferentResultsListener(PhotoDifferentResultsListener listener) {
        this.differentResultsListener = listener;
    }

    public void open() {
        Context context = getContext();
        if (context == null) {
            showError(CONTEXT_NOT_FOUND);
            return;
        }

        PhotoPermissionChecker permissionChecker = new PhotoPermissionChecker(context);
        permissionChecker.setPermissionDeniedListener(getPermissionDeniedListener());
        permissionChecker.setPermissionGrantedListener(getPermissionGrantedListener());

        checkPermission(permissionChecker);
    }

    public void onResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || requestCode != getRequestCode()) {
            return;
        }

        parseResultData(data);
    }

    protected Context getContext() {
        if (weakReferenceContext == null) {
            return null;
        }

        return weakReferenceContext.get();
    }

    protected void showError(ErrorCode errorCode) {
        if (getContext() != null && errorsListener != null) {
            errorsListener.onError(errorCode);
        }
    }

    protected void returnResult(Uri uri) {
        if (getContext() != null && resultListener != null) {
            resultListener.getFilePath(uri);
        }
    }

    private void openWithoutPermissionCheck() {
        if (getContext() == null) {
            return;
        }

        if (intentForResult == null) {
            showError(INTENT_NOT_SET);
            return;
        }

        Intent intent = getIntent();

        if (intent == null) {
            return;
        }

        intentForResult.startIntentForPhoto(getIntent(), getRequestCode());
    }

    private PermissionGrantedListener getPermissionGrantedListener() {
        return new PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                openWithoutPermissionCheck();
            }
        };
    }

    private PermissionDeniedListener getPermissionDeniedListener() {
        return new PermissionDeniedListener() {
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                showError(PERMISSION_DENIED);
            }
        };
    }
}
