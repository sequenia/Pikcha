package com.sequenia.photo.listeners;

/**
 * Created by Ringo on 12.12.2016.
 * Интерфейс для отображения ожидания
 * (длительные операции добавления)
 */
public interface PhotoWait {
    /**
     * Отображение состояния ожидания
     * @param state - true - ожидание
     *              false - окончание ожидания
     */
    void visibilityWait(boolean state);
}
