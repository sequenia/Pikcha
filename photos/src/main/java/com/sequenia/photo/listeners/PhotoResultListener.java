package com.sequenia.photo.listeners;

/**
 * Слушатель на получение результата (абсолютного пути к файлу)
 */
public interface PhotoResultListener extends StartIntentForResult {
    /**
     * Возвращение результата
     *
     * @param path - пути к фотография
     */
    void getPath(String path);
}
