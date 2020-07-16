package com.sequenia;

/**
 * Коды обработанных ошибок
 */
public enum ErrorCode {

    /**
     * Не указан путь к файлу
     */
    FILE_PATH_NOT_FOUND,

    /**
     * На устройстве не удалось найти камеру
     */
    NO_CAMERA_ON_THE_DEVICE,

    /**
     * Не удалось создать файл
     */
    CAN_NOT_CREATE_FILE,

    /**
     * Не задан intent для обработки результата
     */
    INTENT_NOT_SET,

    /**
     * Отказано в выдаче разрешений
     */
    PERMISSION_DENIED,

    /**
     * Потерян контекст
     */
    CONTEXT_NOT_FOUND,

    /**
     * По указаному пути не найден файл
     */
    FILE_NOT_FOUND,

    /**
     * Обработанное исключение
     * <p>
     * Что-то пошло не так!
     */
    EXCEPTION

}
