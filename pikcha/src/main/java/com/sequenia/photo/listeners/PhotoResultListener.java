package com.sequenia.photo.listeners;

import android.net.Uri;

/**
 * Слушатель на получение результата (абсолютного пути к файлу)
 */
public interface PhotoResultListener extends StartIntentForResult {

    /**
     * Получить путь к файлу
     *
     * @param uri пути к фотография
     */
    void getFilePath(Uri uri);
}
