package com.sequenia.photo.listeners;

/**
 * Created by Ringo on 12.12.2016.
 * Интерфейс для приема результата
 */
public interface ResultFromCamera extends StartIntentForResult{
    /**
     * Возвращение результата
     * @param path - пути к фотография
     */
    void getPathFromCamera(String path);
}
