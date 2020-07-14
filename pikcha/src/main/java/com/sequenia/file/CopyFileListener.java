package com.sequenia.file;

import java.io.File;

/**
 * Слушатель состояния файла при копирование
 */
public interface CopyFileListener {

    /**
     * Не удалось скопировать файл
     */
    void fileNotCopied();

    /**
     * Файл скопирован
     *
     * @param file копися файла
     */
    void copiedFile(File file);

    /**
     * Началось копирование файла
     */
    default void startCopyFile() {
    }

    /**
     * Закончилось копирование файла
     */
    default void endCopyFile() {
    }
}
