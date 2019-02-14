package com.sequenia.photo.listeners;

/**
 * Интерфейс для отображения ожидания
 * (длительные операции добавления)
 */
public interface PhotoWaitListener {
    /**
     * Отображение состояния ожидания
     * @param state - true - ожидание
     *              false - окончание ожидания
     */
    void visibilityWait(boolean state);
}
