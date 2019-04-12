package com.sequenia.photo.listeners;

/**
 * Слушатель на получение результата (абсолютного пути к файлу)
 * - с камеры
 * - из галереи
 */
public interface PhotoDifferentResultsListener extends StartIntentForResult {

    /**
     * Возвращение результата из галереи
     *
     * @param path - пути к фотография
     */
    void getPathFromGallery(String path);

    /**
     * Возвращение результата с камеры
     *
     * @param path - пути к фотография
     */
    void getPathFromCamera(String path);
}
