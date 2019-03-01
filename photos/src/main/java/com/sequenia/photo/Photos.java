package com.sequenia.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.gun0912.tedpermission.PermissionListener;
import com.sequenia.file.CursorUtils;
import com.sequenia.file.FilesUtils;
import com.sequenia.file.UriUtils;
import com.sequenia.photo.listeners.GetPathCallback;
import com.sequenia.photo.listeners.PhotoDifferentResultsListener;
import com.sequenia.photo.listeners.PhotoErrorListener;
import com.sequenia.photo.listeners.PhotoResultListener;
import com.sequenia.photo.listeners.PhotoWaitListener;
import com.sequenia.photo.listeners.StartIntentForResult;
import com.sequenia.photo.repository.Repository;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.sequenia.ErrorCodes.CAN_NOT_CREATE_FILE;
import static com.sequenia.ErrorCodes.EXCEPTION;
import static com.sequenia.ErrorCodes.FILE_PATH_NOT_FOUND;
import static com.sequenia.ErrorCodes.INTENT_NOT_SET;
import static com.sequenia.ErrorCodes.NO_CAMERA_ON_THE_DEVICE;
import static com.sequenia.ErrorCodes.NO_FILE_IN_THE_SPECIFIED_PATH;
import static com.sequenia.ErrorCodes.PERMISSION_DENIED;

/**
 * Класс, осуществляющий всю работу с изображениями
 * <p>
 * - добавить фотографию из галереи
 * - сделать фотографию с камеры
 */
public class Photos {

    private static final int GALLERY_REQUEST = 10101;
    private static final int TAKE_PHOTO_REQUEST = 20202;

    private static final int COUNT_REPEAT = 10;
    private static final int REPEAT_DELAY = 1000;

    /**
     * Последнее действие (нужно для перезапуска, после выставления пермишенов)
     */
    private int lastEvent;

    /**
     * Абсолютный путь к файлу при добавление фотографии с камеры
     */
    private String filePath;

    /**
     * Слушатели на ошибки
     */
    private PhotoErrorListener errorsListener;

    /**
     * Возвращение рузультата с камеры
     */
    private PhotoResultListener resultListener;

    /**
     * Показатель ожидания
     */
    private PhotoWaitListener waitListener;

    /**
     * Интерфейс на реализацию метода открытия intent
     */
    private StartIntentForResult intentForResult;

    /**
     * Интерфейс на реализацию метода открытия intent
     */
    private PhotoDifferentResultsListener differentResultsListener;

    /**
     * Хранение контекста
     */
    private WeakReference<Context> weakReferenceContext;

    public Photos(Activity activity) {
        setContext(activity);

        if (activity instanceof PhotoResultListener) {
            resultListener = (PhotoResultListener) activity;
        }

        if (activity instanceof PhotoErrorListener) {
            errorsListener = (PhotoErrorListener) activity;
        }

        if (activity instanceof PhotoWaitListener) {
            waitListener = (PhotoWaitListener) activity;
        }

        if (activity instanceof StartIntentForResult) {
            intentForResult = (StartIntentForResult) activity;
        }

        if (activity instanceof PhotoDifferentResultsListener) {
            differentResultsListener = (PhotoDifferentResultsListener) activity;
        }
    }

    public Photos(Fragment fragment) {
        setContext(fragment.getContext());

        if (fragment instanceof PhotoResultListener) {
            resultListener = (PhotoResultListener) fragment;
        }

        if (fragment instanceof PhotoErrorListener) {
            errorsListener = (PhotoErrorListener) fragment;
        }

        if (fragment instanceof PhotoWaitListener) {
            waitListener = (PhotoWaitListener) fragment;
        }

        if (fragment instanceof StartIntentForResult) {
            intentForResult = (StartIntentForResult) fragment;
        }

        if (fragment instanceof PhotoDifferentResultsListener) {
            differentResultsListener = (PhotoDifferentResultsListener) fragment;
        }
    }

    private void setContext(Context context) {
        weakReferenceContext = new WeakReference<>(context);
    }

    private Context getContext() {
        if (weakReferenceContext == null) {
            return null;
        }

        return weakReferenceContext.get();
    }

    /**
     * Выбор фотографий из галереи
     */
    public void selectedPhotoFromGallery() {
        lastEvent = GALLERY_REQUEST;

        Context context = getContext();
        if (context != null) {
            PermissionManager.permissionForGallery(context, getPermissionListener());
        }
    }

    /**
     * Выбор фотографий из галереи
     */
    private void openGallery() {
        if (intentForResult == null) {
            showError(INTENT_NOT_SET);
            return;
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intentForResult.startIntentForPhoto(
                Intent.createChooser(intent, getText(R.string.add_photo)),
                GALLERY_REQUEST
        );
    }

    /**
     * Сделать фото с камеры
     */
    public void takePhotoFromCamera() {
        lastEvent = TAKE_PHOTO_REQUEST;
        Context context = getContext();
        if (context != null) {
            PermissionManager.permissionForCamera(context, getPermissionListener());
        }
    }

    /**
     * Сделать фото с камеры
     */
    private void openCamera() {
        try {
            Context context = getContext();
            if (context == null) {
                return;
            }

            if (intentForResult == null) {
                showError(INTENT_NOT_SET);
                return;
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) == null) {
                showError(NO_CAMERA_ON_THE_DEVICE);
                return;
            }

            File image = FilesUtils.createJPGFileInOpenDirectory(context);

            if (image == null) {
                showError(CAN_NOT_CREATE_FILE);
                return;
            }

            filePath = image.getAbsolutePath();
            // Сохраняем, чтобы восстановить, если экран потеряется
            Repository.savePath(context, filePath);

            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    UriUtils.getFileUri(context, image));
            intentForResult.startIntentForPhoto(takePictureIntent, TAKE_PHOTO_REQUEST);

        } catch (IOException exc) {
            showError(EXCEPTION);
        }
    }

    /**
     * Обработка результата добавления фотографии
     *
     * @param data - результат
     */
    public void onResult(int requestCode, int resultCode, Intent data) {
        // получили данные о дейсвиях
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case GALLERY_REQUEST:
                photoFromGallery(data);
                break;
            case TAKE_PHOTO_REQUEST:
                photoFromCamera();
                break;
        }
    }

    /**
     * Достать путь к уже сделанной фотографии
     */
    private void photoFromCamera() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        // возможно, экран пересоздался
        if (filePath == null) {
            filePath = Repository.getPath(context);
            // подчищаем все, что хранится во временном хранилище
            Repository.removePath(context);
        }

        if (filePath == null) {
            showError(FILE_PATH_NOT_FOUND);
            return;
        }

        // если файл существует и его размер больше 0
        if (FilesUtils.checkedFile(filePath)) {
            returnResult(filePath);
            returnResultFromCamera(filePath);
            return;
        }

        setWaitState(true);
        getPhotoPathAsync(0);
    }

    /**
     * Достать путь к выбранной фотографии (фотографиям)
     *
     * @param data - хранится информация
     */
    private void photoFromGallery(Intent data) {
        Uri uri = UriUtils.getUrisFromData(data);
        if (uri == null) {
            showError(NO_FILE_IN_THE_SPECIFIED_PATH);
            return;
        }
        returnResultFromGallery(uri);
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
                if (FilesUtils.checkedFile(filePath)) {
                    setWaitState(false);
                    returnResult(filePath);
                    returnResultFromCamera(filePath);
                    return;
                }

                if (tryCount >= COUNT_REPEAT) {
                    setWaitState(false);
                    getPathLastPhoto();
                    return;
                }

                getPhotoPathAsync(tryCount + 1);
            }
        }, REPEAT_DELAY);

    }

    /**
     * Попытка достать последнюю
     * фотографию из галлереи
     */
    private void getPathLastPhoto() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        filePath = CursorUtils.getLastImageFile(context);

        if (filePath == null) {
            showError(FILE_PATH_NOT_FOUND);
            return;
        }

        returnResult(filePath);
        returnResultFromCamera(filePath);
    }

    /**
     * Возвращение результата из галереи
     *
     * @param uri - URI выбранного файла
     */
    private void returnResultFromGallery(Uri uri) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        setWaitState(true);
        UriUtils.getPath(context, uri, new GetPathCallback() {
            @Override
            public void onSuccess(String path) {
                returnResult(path);
                returnResultFromGallery(path);
                setWaitState(false);
            }

            @Override
            public void onError(int errorCode) {
                showError(errorCode);
                setWaitState(false);
            }
        });
    }

    private void returnResultFromCamera(String path) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (differentResultsListener != null) {
            differentResultsListener.getPathFromCamera(path);
        }
    }

    private void returnResultFromGallery(String path) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (differentResultsListener != null) {
            differentResultsListener.getPathFromGallery(path);
        }
    }

    private void returnResult(String path) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (resultListener != null) {
            resultListener.getPath(path);
        }
    }

    private void showError(int errorCode) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (errorsListener != null) {
            errorsListener.onError(errorCode);
        }
    }

    private void setWaitState(boolean state) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (waitListener != null) {
            waitListener.visibilityWait(state);
        }
    }

    private String getText(int res) {
        Context context = getContext();
        if (context == null) {
            return null;
        }

        return context.getString(res);
    }

    private PermissionListener getPermissionListener() {
        return new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // выдали разрешение, повторяем попытку запуска
                switch (lastEvent) {
                    case GALLERY_REQUEST:
                        openGallery();
                        break;
                    case TAKE_PHOTO_REQUEST:
                        openCamera();
                        break;
                }
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                showError(PERMISSION_DENIED);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                showError(PERMISSION_DENIED);
            }
        };
    }
}
