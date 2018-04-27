package com.sequenia.photo.listeners;

import java.util.List;

/**
 * Created by Ringo on 11.04.2018.
 * Возвращения результат добавления фотографий из галереи
 */

public interface MultiResultFromGallery extends StartIntentForResult {

    /**
     * @param paths - список путей к выбранным файлам
     */
    void getPathsFromGallery(List<String> paths);
}
