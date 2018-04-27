package com.sequenia.photo.listeners;

/**
 * Created by Ringo on 12.12.2016.
 * Интерфейс для приема результата
 */
public interface ResultFromGallery extends StartIntentForResult {

    /**
     * @param path - путь к файлу
     */
    void getPathFromGallery(String path);
}
