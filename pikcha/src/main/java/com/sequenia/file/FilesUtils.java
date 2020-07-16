package com.sequenia.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Работа с файлами
 * - создает файл в нужном месте
 * - проверяет файл на существование
 */
public class FilesUtils {

    private final static String EXP_PNG = "png";
    private final static String EXP_JPEG = "jpeg";

    /**
     * Создание файла JPEG с уникальным именем
     *
     * @return созданный файл
     */
    public static File createJPGFile(Context context) throws IOException {
        return createFileInAppSpecificDirectory(context, EXP_JPEG);
    }

    /**
     * Создание файла JPEG
     *
     * @param fileName имя файла
     * @return созданный файл
     */
    public static File createJPGFile(Context context, String fileName) throws IOException {
        return createFile(getInAppDirectory(context), fileName, EXP_JPEG);
    }

    /**
     * Создание файла JPEG с уникальным именем
     *
     * @return созданный файл
     */
    public static File createPNGFile(Context context) throws IOException {
        return createFileInAppSpecificDirectory(context, EXP_PNG);
    }

    /**
     * Создание файла JPEG
     *
     * @param fileName имя файла
     * @return созданный файл
     */
    public static File createPNGFile(Context context, String fileName) throws IOException {
        return createFile(getInAppDirectory(context), fileName, EXP_PNG);
    }

    /**
     * Запись @{@link Bitmap} в файл
     *
     * @param bitmap   @{@link Bitmap}
     * @param compress процент сжатия
     * @return файл, в который быз записан @{@link Bitmap}
     */
    public static File saveBitmapToPNGFile(Context context, Bitmap bitmap, int compress)
            throws IOException {
        return saveBitmapToPNGFile(context, bitmap, getDefaultFileName(EXP_PNG), compress);
    }

    /**
     * Запись @{@link Bitmap} в файл
     *
     * @param bitmap   @{@link Bitmap}
     * @param fileName имя файла
     * @param compress процент сжатия
     * @return файл, в который быз записан @{@link Bitmap}
     */
    public static File saveBitmapToPNGFile(Context context, Bitmap bitmap, String fileName,
                                           int compress) throws IOException {
        return saveBitmap(context, bitmap, fileName, EXP_PNG, compress);
    }

    /**
     * Запись @{@link Bitmap} в файл
     *
     * @param bitmap   @{@link Bitmap}
     * @param compress процент сжатия
     * @return файл, в который быз записан @{@link Bitmap}
     */
    public static File saveBitmapToJPGFile(Context context, Bitmap bitmap, int compress)
            throws IOException {
        return saveBitmapToJPGFile(context, bitmap, getDefaultFileName(EXP_JPEG), compress);
    }

    /**
     * Запись @{@link Bitmap} в файл
     *
     * @param bitmap   @{@link Bitmap}
     * @param fileName имя файла
     * @param compress процент сжатия
     * @return файл, в который быз записан @{@link Bitmap}
     */
    public static File saveBitmapToJPGFile(Context context, Bitmap bitmap, String fileName,
                                           int compress) throws IOException {
        return saveBitmap(context, bitmap, fileName, EXP_JPEG, compress);
    }

    /**
     * Копирование файла в директорию приложения
     *
     * @param context  контекст
     * @param uri      путь к файлу
     * @param listener слушатель для получения состояния копирования файла
     */
    public static void copyFileToInAppDirectory(Context context, Uri uri, CopyFileListener listener)
            throws IOException, OutOfMemoryError {

        File fileDirectory = getInAppDirectory(context);
        copyFile(context, fileDirectory, uri, listener);
    }

    /**
     * Копирование файла в кэш директорию приложения
     *
     * @param context  контекст
     * @param uri      путь к файлу
     * @param listener слушатель для получения состояния копирования файла
     */
    public static void copyFileToCashDirectory(Context context, Uri uri, CopyFileListener listener)
            throws IOException, OutOfMemoryError {

        File outputFile = getCashDirectory(context);
        copyFile(context, outputFile, uri, listener);
    }

    /**
     * Удалить файл
     *
     * @param context - контекст
     * @param uri     - путь к файлу
     */
    public static void deleteFile(Context context, Uri uri) {
        new FileExtension(context, uri).delete();
    }

    /**
     * Проверка существования файла
     *
     * @param context контекст
     * @param uri     пусть к файлу
     * @return true файл существует и не пустой
     */
    public static boolean fileExists(Context context, Uri uri) throws IOException {
        return new FileExtension(context, uri).exist();
    }

    /**
     * Получить директорию приложения
     * <p>
     * С 29 и выше апи другие приложения не смогут считать эти файлы
     * При удаление приложения, удаляются и файлы
     * <p>
     * Не нужены разрешения для записи и чтения
     * <p>
     * Проверка на запись во внешнее хранилище есть, так как на некоторых устройствах
     * внешнее хранилище представлено только sd-картой
     *
     * @return директория приложения
     */
    private static File getInAppDirectory(Context context) {
        return isExternalStorageWritable() ?
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) : context.getFilesDir();
    }

    /**
     * Проверка разрешения для записи во внешнем хранилище
     *
     * @return true, если внешнее хранилище доступно для записи
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Получить директорию приложения для кэша
     * <p>
     * При удаление приложения, удаляются и файлы
     * <p>
     * Не нужены разрешения для записи и чтения
     * <p>
     * Проверка на запись во внешнее хранилище есть, так как на некоторых устройствах
     * внешнее хранилище представлено только sd-картой
     *
     * @return директория приложения для кэша
     */
    private static File getCashDirectory(Context context) {
        return isExternalStorageWritable() ? context.getExternalCacheDir() : context.getCacheDir();
    }

    /**
     * Создание файла в указанной директории и с указанным расширением
     *
     * @param storageDir директория
     * @param exp        расширение
     * @return созданный файл
     */
    private static File createFile(File storageDir, String name, String exp) throws IOException {
        // не нашлось директории и не удалось создать
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            return null;
        }

        File file = File.createTempFile(name, "." + exp, storageDir);
        // Доступность файла для внешних приложений
        file.setReadable(true, false);
        return file;
    }

    /**
     * Получить имя файла по умолчание
     *
     * @param exp расширение файла
     * @return имя файла
     */
    private static String getDefaultFileName(String exp) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        return String.format("%s_%s_", exp, timeStamp);
    }

    /**
     * Сохранение @{@link Bitmap} в файл
     *
     * @param context  контектс
     * @param bitmap   @{@link Bitmap}
     * @param fileName имя файла
     * @param exp      расширение файла
     * @param compress процент сжатия
     * @return файл, в который быз записан @{@link Bitmap}
     */
    private static File saveBitmap(@NonNull Context context, @NonNull Bitmap bitmap,
                                   @NonNull String fileName, @NonNull String exp,
                                   int compress) throws IOException {

        File outputFile = createFile(getInAppDirectory(context), fileName, exp);

        if (outputFile == null) {
            return null;
        }

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        bitmap.compress(getCompressFormat(exp), compress, outputStream);
        outputStream.flush();
        outputStream.close();
        return outputFile;
    }

    /**
     * Получение типа компрессии в зависимости от типа файла
     *
     * @param exp тип файла
     * @return тип компрессии
     */
    private static Bitmap.CompressFormat getCompressFormat(String exp) {
        return exp.equals(EXP_PNG) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
    }

    private static File createFileInAppSpecificDirectory(Context context, String exp)
            throws IOException {
        return createFile(getInAppDirectory(context), getDefaultFileName(exp), exp);
    }

    private static void copyFile(Context context, File fileDirectoryToCopy, Uri inputUri,
                                 CopyFileListener listener) throws IOException {

        FileExtension fileExtension = new FileExtension(context, inputUri);

        String fileExp = fileExtension.getExp();
        String fileName = getDefaultFileName(fileExp);
        File outputFile = createFile(fileDirectoryToCopy, fileName, fileExp);

        fileExtension.copyTo(outputFile, listener);
    }
}