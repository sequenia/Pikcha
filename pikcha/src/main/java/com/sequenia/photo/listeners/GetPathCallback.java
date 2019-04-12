package com.sequenia.photo.listeners;

public interface GetPathCallback {

    /**
     * Успешно получен абсолютный путь к файлу
     *
     * @param path - абсолютный путь к файлу
     */
    void onSuccess(String path);

    /**
     * Возможные коды ошибок указаны в @{@link com.sequenia.ErrorCodes}
     *
     * @param errorCode - код обработанной ошибки.
     */
    void onError(int errorCode);
}
