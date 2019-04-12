package com.sequenia.photo.listeners;

/**
 * Слушатель на получение сообщений о возникших ошибках
 */
public interface PhotoErrorListener {

    /**
     * Возможные коды ошибок указаны в @{@link com.sequenia.ErrorCodes}
     *
     * @param errorCode - код обработанной ошибки.
     */
    void onError(int errorCode);
}
