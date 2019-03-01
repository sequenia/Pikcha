package com.sequenia;

/**
 * Коды обработанных ошибок
 */
public interface ErrorCodes {

    /**
     * Не хватает памяти для выполнения операции, отведенной на приложение
     */
    int OUT_OF_MEMORY = 0;

    /**
     * Не указан путь к файлу
     */
    int FILE_PATH_NOT_FOUND = 1;

    /**
     * По указанному пути не нашлось файла
     */
    int NO_FILE_IN_THE_SPECIFIED_PATH = 2;

    /**
     * На устройстве не удалось найти камеру
     */
    int NO_CAMERA_ON_THE_DEVICE = 3;

    /**
     * Не удалось создать файл
     */
    int CAN_NOT_CREATE_FILE = 4;

    /**
     * Не задан intent для обработки результата
     */
    int INTENT_NOT_SET = 5;

    /**
     * Отказано в выдаче разрешений
     */
    int PERMISSION_DENIED = 6;

    /**
     * Потерян контекст
     */
    int CONTEXT_NOT_FOUND = 7;

    /**
     * Обработанное исключение
     * <p>
     * Что-то пошло не так!
     */
    int EXCEPTION = 8;

}
