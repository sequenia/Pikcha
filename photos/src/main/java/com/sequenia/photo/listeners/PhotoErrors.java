package com.sequenia.photo.listeners;

/**
 * Created by Ringo on 12.12.2016.
 * Интерфейс для отлова ошибок
 */
public interface PhotoErrors {
    /**
     * Ошибки при добавлении фотографии с камеры
     */
    void errorTakePhotoFromCamera(String error);

    /**
     * Ошибки при добавлении фотографий из галлереи
     */
    void errorSelectedPhotoFromGallery(String error);
}
