package com.sequenia.photo.sources;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.sequenia.file.FilesUtils;
import com.sequenia.file.UriUtils;
import com.sequenia.photo.listeners.PhotoWaitListener;
import com.sequenia.photo.repository.Repository;
import com.sequenia.photo.sources.permissions.PhotoPermissionChecker;

import java.io.File;
import java.io.IOException;

import static com.sequenia.ErrorCode.CAN_NOT_CREATE_FILE;
import static com.sequenia.ErrorCode.CONTEXT_NOT_FOUND;
import static com.sequenia.ErrorCode.EXCEPTION;
import static com.sequenia.ErrorCode.FILE_NOT_FOUND;
import static com.sequenia.ErrorCode.FILE_PATH_NOT_FOUND;
import static com.sequenia.ErrorCode.NO_CAMERA_ON_THE_DEVICE;

/**
 * Изображения с камеры
 */
public class CameraSource extends Source {

    private static final int TAKE_PHOTO_REQUEST = 20202;

    private static final int COUNT_REPEAT = 10;
    private static final int REPEAT_DELAY = 1000;

    /**
     * Показатель ожидания
     */
    private PhotoWaitListener waitListener;

    /**
     * Путь к файлу при добавление фотографии с камеры
     */
    private Uri filePathFromCamera;

    public CameraSource(Context context) {
        super(context);
    }

    public void setWaitListener(PhotoWaitListener waitListener) {
        this.waitListener = waitListener;
    }

    @Override
    protected void checkPermission(PhotoPermissionChecker permissionChecker) {
        permissionChecker.permissionForCamera();
    }

    @Override
    protected void parseResultData(Intent data) {
        boolean waitMore = returnResultIfPhotoAvailable();

        if (!waitMore) {
            return;
        }

        setWaitState(true);
        getPhotoPathAsync(0);
    }

    @Override
    protected int getRequestCode() {
        return TAKE_PHOTO_REQUEST;
    }

    @Override
    protected Intent getIntent() {
        try {
            Context context = getContext();
            if (context == null) {
                showError(CONTEXT_NOT_FOUND);
                return null;
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) == null) {
                showError(NO_CAMERA_ON_THE_DEVICE);
                return null;
            }

            File image = FilesUtils.createJPGFile(context);

            if (image == null) {
                showError(CAN_NOT_CREATE_FILE);
                return null;
            }

            filePathFromCamera = UriUtils.getFileUri(context, image);
            // Сохраняем, чтобы восстановить, если экран потеряется
            Repository.savePath(context, filePathFromCamera.toString());

            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePathFromCamera);

            return takePictureIntent;

        } catch (IOException exc) {
            showError(EXCEPTION);
        }

        return null;
    }

    protected void returnDifferentResult(Uri uri) {
        if (getContext() != null && differentResultsListener != null) {
            differentResultsListener.getPathFromCamera(uri);
        }
    }

    /**
     * Ожидание появление фотографии в альбоме
     *
     * @param tryCount - количество попыток 10
     */
    private void getPhotoPathAsync(final int tryCount) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean waitMore = returnResultIfPhotoAvailable();

                if (!waitMore) {
                    return;
                }

                if (tryCount >= COUNT_REPEAT) {
                    setWaitState(false);
                    showError(FILE_NOT_FOUND);
                    return;
                }

                getPhotoPathAsync(tryCount + 1);
            }
        }, REPEAT_DELAY);
    }

    /**
     * Метод по возвращению результата, если файл доступен (существует и не пустой)
     *
     * @return true необходимо дальше ждать результат
     */
    private boolean returnResultIfPhotoAvailable() {

        Uri filePath = getFilePathFromCamera();

        if (filePath == null) {
            showError(FILE_PATH_NOT_FOUND);
            return false;
        }

        Context context = getContext();
        if (context == null) {
            showError(CONTEXT_NOT_FOUND);
            return false;
        }

        try {
            if (FilesUtils.fileExists(context, filePath)) {
                setWaitState(false);
                returnResult(filePath);
                returnDifferentResult(filePath);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();

            showError(FILE_NOT_FOUND);

            return false;
        }

        return true;
    }

    private Uri getFilePathFromCamera() {
        Context context = getContext();
        if (context == null) {
            showError(CONTEXT_NOT_FOUND);
            return null;
        }

        // возможно, экран пересоздался
        if (filePathFromCamera == null) {
            filePathFromCamera = Uri.parse(Repository.getPath(context));
            // подчищаем все, что хранится во временном хранилище
            Repository.removePath(context);
        }

        return filePathFromCamera;
    }

    private void setWaitState(boolean state) {
        if (getContext() != null && waitListener != null) {
            waitListener.visibilityWait(state);
        }
    }
}
