package com.sequenia.photo;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.sequenia.photo.listeners.PhotoDifferentResultsListener;
import com.sequenia.photo.listeners.PhotoErrorListener;
import com.sequenia.photo.listeners.PhotoResultListener;
import com.sequenia.photo.listeners.PhotoWaitListener;
import com.sequenia.photo.listeners.StartIntentForResult;
import com.sequenia.photo.repository.Repository;
import com.sequenia.photo.sources.CameraSource;
import com.sequenia.photo.sources.GallerySource;
import com.sequenia.photo.sources.SelectableSource;
import com.sequenia.photo.sources.Source;
import com.sequenia.photo.sources.SourceType;

/**
 * Класс, осуществляющий всю работу с изображениями
 * <p>
 * - добавить фотографию из галереи
 * - сделать фотографию с камеры
 */
public class Photos {

    /**
     * Источник фотографий
     */
    private Source sourcePhoto;

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

    public Photos(Context context) {

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

        restoreSource(context);
    }

    public Photos(Fragment fragment) {

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

        restoreSource(fragment.getContext());
    }

    /**
     * Выбор фото из галереи
     */
    public void selectedPhotoFromGallery(Context context) {
        initSource(context, SourceType.GALLERY);
        openSource();
    }

    /**
     * Сделать фото с камеры
     */
    public void takePhotoFromCamera(Context context) {
        initSource(context, SourceType.CAMERA);
        openSource();
    }

    /**
     * Выбрать способ добавления фото
     */
    public void selectMethodOfAddingPhoto(Context context) {
        initSource(context, SourceType.SELECTABLE);
        openSource();
    }

    /**
     * Обработка результата добавления фотографии
     *
     * @param data - результат
     */
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (sourcePhoto != null) {
            sourcePhoto.onResult(requestCode, resultCode, data);
        }
    }

    private void initSource(Context context, SourceType type) {
        Repository.saveSourceTypeName(context, type.name());

        switch (type) {
            case CAMERA:
                initCameraSource(context);
                break;
            case GALLERY:
                initGallerySource(context);
                break;
            case SELECTABLE:
                initSelectableSource(context);
                break;
        }
    }

    private void initGallerySource(Context context) {
        sourcePhoto = new GallerySource(context);
        sourcePhoto.setErrorsListener(errorsListener);
        sourcePhoto.setIntentForResult(intentForResult);
        sourcePhoto.setResultListener(resultListener);
        sourcePhoto.setDifferentResultsListener(differentResultsListener);
    }

    private void initCameraSource(Context context) {
        CameraSource source = new CameraSource(context);
        source.setErrorsListener(errorsListener);
        source.setIntentForResult(intentForResult);
        source.setResultListener(resultListener);
        source.setDifferentResultsListener(differentResultsListener);
        source.setWaitListener(waitListener);

        this.sourcePhoto = source;
    }

    private void initSelectableSource(Context context) {
        SelectableSource source = new SelectableSource(context);

        source.setErrorsListener(errorsListener);
        source.setIntentForResult(intentForResult);
        source.setResultListener(resultListener);
        source.setDifferentResultsListener(differentResultsListener);
        source.setWaitListener(waitListener);

        this.sourcePhoto = source;
    }

    private void restoreSource(Context context) {
        String typeName = Repository.getSourceTypeName(context);
        Repository.removeSourceTypeName(context);

        for (SourceType type : SourceType.values()) {
            if (type.name().equals(typeName)) {
                initSource(context, type);
                break;
            }
        }
    }

    private void openSource() {
        sourcePhoto.open();
    }
}
