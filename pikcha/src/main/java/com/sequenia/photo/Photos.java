package com.sequenia.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import com.sequenia.ErrorCode;
import com.sequenia.file.FilesUtils;
import com.sequenia.file.UriUtils;
import com.sequenia.photo.PermissionChecker.PermissionDeniedListener;
import com.sequenia.photo.PermissionChecker.PermissionGrantedListener;
import com.sequenia.photo.listeners.PhotoDifferentResultsListener;
import com.sequenia.photo.listeners.PhotoErrorListener;
import com.sequenia.photo.listeners.PhotoResultListener;
import com.sequenia.photo.listeners.PhotoWaitListener;
import com.sequenia.photo.listeners.StartIntentForResult;
import com.sequenia.photo.repository.Repository;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import static com.sequenia.ErrorCode.CAN_NOT_CREATE_FILE;
import static com.sequenia.ErrorCode.CONTEXT_NOT_FOUND;
import static com.sequenia.ErrorCode.EXCEPTION;
import static com.sequenia.ErrorCode.FILE_NOT_FOUND;
import static com.sequenia.ErrorCode.FILE_PATH_NOT_FOUND;
import static com.sequenia.ErrorCode.INTENT_NOT_SET;
import static com.sequenia.ErrorCode.NO_CAMERA_ON_THE_DEVICE;
import static com.sequenia.ErrorCode.PERMISSION_DENIED;

/**
 * Класс, осуществляющий всю работу с изображениями
 * <p>
 * - добавить фотографию из галереи
 * - сделать фотографию с камеры
 */
public class Photos {

    private static final int GALLERY_REQUEST = 10101;
    private static final int TAKE_PHOTO_REQUEST = 20202;
    private static final int SELECT_METHOD_OF_ADDING_PHOTO_REQUEST = 30303;

    private static final int COUNT_REPEAT = 10;
    private static final int REPEAT_DELAY = 1000;

    /**
     * Последнее действие (нужно для перезапуска, после выставления пермишенов)
     */
    private int lastEvent;

    /**
     * Абсолютный путь к файлу при добавление фотографии с камеры
     */
    private Uri filePathFromCamera;

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

    public Photos(Context context) {
        setContext(context);

        if (context instanceof PhotoResultListener) {
            resultListener = (PhotoResultListener) context;
        }

        if (context instanceof PhotoErrorListener) {
            errorsListener = (PhotoErrorListener) context;
        }

        if (context instanceof PhotoWaitListener) {
            waitListener = (PhotoWaitListener) context;
        }

        if (context instanceof StartIntentForResult) {
            intentForResult = (StartIntentForResult) context;
        }

        if (context instanceof PhotoDifferentResultsListener) {
            differentResultsListener = (PhotoDifferentResultsListener) context;
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

    /**
     * Задание контекста
     * метод открыт на случай, если потерялся контекст по какой-то причине,
     * не связанной с уходом с экрана
     *
     * @param context - контекст
     */
    public void setContext(Context context) {
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
        Context context = getContext();
        if (context == null) {
            showError(CONTEXT_NOT_FOUND);
            return;
        }

        lastEvent = GALLERY_REQUEST;

        PhotoPermissionChecker.permissionForGallery(
                context,
                getPermissionGrantedListener(),
                getPermissionDeniedListener()
        );
    }

    /**
     * Сделать фото с камеры
     */
    public void takePhotoFromCamera() {
        Context context = getContext();
        if (context == null) {
            showError(CONTEXT_NOT_FOUND);
            return;
        }

        lastEvent = TAKE_PHOTO_REQUEST;

        PhotoPermissionChecker.permissionForCamera(
                context,
                getPermissionGrantedListener(),
                getPermissionDeniedListener()
        );
    }

    /**
     * Выбрать способ добавления фото
     */
    public void selectMethodOfAddingPhoto() {
        Context context = getContext();
        if (context == null) {
            showError(CONTEXT_NOT_FOUND);
            return;
        }

        lastEvent = SELECT_METHOD_OF_ADDING_PHOTO_REQUEST;
        PhotoPermissionChecker.permissionForChooser(
                context,
                getPermissionGrantedListener(),
                getPermissionDeniedListener()
        );
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
            case SELECT_METHOD_OF_ADDING_PHOTO_REQUEST:
                addedPhotoFromUnknownSource(data);
                break;
        }
    }

    /**
     * Показать выбор для добавления фотографий
     */
    private void showChooserOfAddingPhoto() {
        if (intentForResult == null) {
            showError(INTENT_NOT_SET);
            return;
        }

        Intent cameraIntent = getCameraIntent();
        Intent galleryIntent = getGalleryIntent();

        if (cameraIntent == null) {
            return;
        }

        Intent chooserIntent = Intent.createChooser(galleryIntent, null);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{cameraIntent});
        intentForResult.startIntentForPhoto(chooserIntent, SELECT_METHOD_OF_ADDING_PHOTO_REQUEST);
    }

    /**
     * Сделать фото с камеры
     */
    private void openCamera() {
        if (intentForResult == null) {
            showError(INTENT_NOT_SET);
            return;
        }

        Intent cameraIntent = getCameraIntent();

        if (cameraIntent == null) {
            return;
        }

        intentForResult.startIntentForPhoto(cameraIntent, TAKE_PHOTO_REQUEST);
    }

    /**
     * Выбор фотографий из галереи
     */
    private void openGallery() {
        if (intentForResult == null) {
            showError(INTENT_NOT_SET);
            return;
        }

        Intent intent = getGalleryIntent();
        intentForResult.startIntentForPhoto(
                Intent.createChooser(intent, getText(R.string.add_photo)),
                GALLERY_REQUEST
        );
    }

    private Intent getCameraIntent() {
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

    private Intent getGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    private void addedPhotoFromUnknownSource(Intent data) {
        if (data == null) {
            photoFromCamera();
            return;
        }

        photoFromGallery(data);
    }

    /**
     * Достать путь к уже сделанной фотографии
     */
    private void photoFromCamera() {
        boolean waitMore = returnResultIfPhotoAvailable();

        if (!waitMore) {
            return;
        }

        setWaitState(true);
        getPhotoPathAsync(0);
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

    /**
     * Достать путь к выбранной фотографии
     *
     * @param data - хранится информация
     */
    private void photoFromGallery(Intent data) {
        Uri uri = UriUtils.getUriFromData(data);

        if (uri == null) {
            showError(FILE_PATH_NOT_FOUND);
            return;
        }

        returnResult(uri);
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
                returnResultFromCamera(filePath);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();

            showError(FILE_NOT_FOUND);

            return false;
        }

        return true;
    }

    private void returnResultFromCamera(Uri uri) {
        if (differentResultsListener != null) {
            differentResultsListener.getPathFromCamera(uri);
        }
    }

    private void returnResultFromGallery(Uri uri) {
        if (differentResultsListener != null) {
            differentResultsListener.getPathFileFromGallery(uri);
        }
    }

    private void returnResult(Uri uri) {
        if (resultListener != null) {
            resultListener.getFilePath(uri);
        }
    }

    private void showError(ErrorCode errorCode) {
        if (errorsListener != null) {
            errorsListener.onError(errorCode);
        }
    }

    private void setWaitState(boolean state) {
        if (waitListener != null) {
            waitListener.visibilityWait(state);
        }
    }

    private String getText(int res) {
        Context context = getContext();
        return context != null ? context.getString(res) : null;
    }

    private PermissionDeniedListener getPermissionDeniedListener() {
        return new PermissionDeniedListener() {
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                showError(PERMISSION_DENIED);
            }
        };
    }

    private PermissionGrantedListener getPermissionGrantedListener() {
        return new PermissionGrantedListener() {
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
                    case SELECT_METHOD_OF_ADDING_PHOTO_REQUEST:
                        showChooserOfAddingPhoto();
                        break;
                }
            }
        };
    }
}
