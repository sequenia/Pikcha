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
     * Не указан путь к фотографии
     */
    int FILE_PATH_NOT_FOUND = 1;

    /**
     * По указанному пути не нашлось фотографии
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
     * Обработанное исключение
     * <p>
     * Что-то пошло не так!
     */
    int EXCEPTION = 6;

}
