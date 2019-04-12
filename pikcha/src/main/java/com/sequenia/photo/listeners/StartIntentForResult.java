package com.sequenia.photo.listeners;

import android.content.Intent;

/**
 * Created by Ringo on 17.04.2018.
 * Для вызора старта intent с нужным
 * источником возврата результата
 */

public interface StartIntentForResult {
    /**
     * Необходимо вызывать внутри этого метода
     * startActivityForResult для activity или fragment
     * @param intent - который необходимо открыть
     * @param requestCode - реквеск код для приема ответа
     */
    void startIntentForPhoto(Intent intent, int requestCode);
}
