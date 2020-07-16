package com.sequenia.photo.listeners;

import android.net.Uri;

/**
 * Слушатель на получение результата (абсолютного пути к файлу)
 * - с камеры
 * - из галереи
 */
public interface PhotoDifferentResultsListener extends StartIntentForResult {

    /**
     * Получить путь к файлу из галереи
     *
     * @param uri пути к фотография
     */
    void getPathFileFromGallery(Uri uri);

    /**
     * Получить путь к файлу с камеры
     *
     * @param uri пути к фотография
     */
    void getPathFromCamera(Uri uri);
}
