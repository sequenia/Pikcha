package com.sequenia.photo.listeners;

import com.sequenia.ErrorCode;

/**
 * Слушатель на получение сообщений о возникших ошибках
 */
public interface PhotoErrorListener {

    /**
     * Возможные коды ошибок указаны в @{@link ErrorCode}
     *
     * @param errorCode - код обработанной ошибки.
     */
    void onError(ErrorCode errorCode);
}
