package com.sequenia.photo.listeners;

import android.content.Intent;

/**
 * Для вызора старта intent с нужным
 * источником возврата результата
 */
public interface StartIntentForResult {

    /**
     * Необходимо вызывать внутри этого метода
     * startActivityForResult для activity или fragment
     *
     * @param intent      - который необходимо открыть
     * @param requestCode - реквеск код для приема ответа
     */
    void startIntentForPhoto(Intent intent, int requestCode);
}
