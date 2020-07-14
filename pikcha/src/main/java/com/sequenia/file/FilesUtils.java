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
 * Created by Ringo on 30.06.2016.
 * <p>
 * Работа с файлами
 * - создает файл в нужном месте
 * - проверяет файл на существование
 */
public class FilesUtils {

    private final static String EXP_PNG = "png";
    private final static String EXP_JPEG = "jpeg";

    /**
     * Создание файла JPEG в закрытой директории с дефолтовым именем
     *
     * @return - файл с дефолтовым именем
     */
    public static File createJPGFileInAppSpecificDirectory(Context context) throws IOException {
        return createFileInAppSpecificDirectory(context, EXP_JPEG);
    }

    /**
     * Создание файла JPEG в закрытой директории с заданным именем
     *
     * @param fileName - имя файла
     * @return - файл с дефолтовым именем
     */
    public static File createJPGFileInAppSpecificDirectory(Context context, String fileName)
            throws IOException {
        return createFile(getAppSpecificDirectory(context), fileName, EXP_JPEG);
    }

    /**
     * Создание файла JPEG в закрытой директории с дефолтовым именем
     *
     * @return - файл с дефолтовым именем
     */
    public static File createPNGFileInAppSpecificDirectory(Context context) throws IOException {
        return createFileInAppSpecificDirectory(context, EXP_PNG);
    }

    /**
     * Создание файла JPEG в закрытой директории с заданным именем
     *
     * @param fileName - имя файла
     * @return - файл с дефолтовым именем
     */
    public static File createPNGFileInAppSpecificDirectory(Context context, String fileName)
            throws IOException {
        return createFile(getAppSpecificDirectory(context), fileName, EXP_PNG);
    }

    /**
     * Запись Bitmap в файл
     *
     * @param bitmap   - Bitmap, который нужно сохранить в файле
     * @param compress - процент сжатия
     * @return файл сохраненного изображения
     */
    public static File saveBitmapToPNGFile(Context context, Bitmap bitmap, int compress)
            throws IOException {
        return saveBitmap(context, bitmap, getDefaultFileName(EXP_PNG), EXP_PNG, compress);
    }

    /**
     * Запись Bitmap в файл
     *
     * @param bitmap   - Bitmap, который нужно сохранить в файле
     * @param fileName - имя файла
     * @param compress - процент сжатия
     * @return файл сохраненного изображения
     */
    public static File saveBitmapToPNGFile(Context context, Bitmap bitmap, String fileName,
                                           int compress) throws IOException {
        return saveBitmap(context, bitmap, fileName, EXP_PNG, compress);
    }

    /**
     * Запись Bitmap в файл
     *
     * @param bitmap   - Bitmap, который нужно сохранить в файле
     * @param compress - процент сжатия
     * @return файл сохраненного изображения
     */
    public static File saveBitmapToJPGFile(Context context, Bitmap bitmap, int compress)
            throws IOException {
        return saveBitmap(context, bitmap, getDefaultFileName(EXP_JPEG), EXP_JPEG, compress);
    }

    /**
     * Запись Bitmap в файл
     *
     * @param bitmap   - Bitmap, который нужно сохранить в файле
     * @param fileName - имя файла
     * @param compress - процент сжатия
     * @return файл сохраненного изображения
     */
    public static File saveBitmapToJPGFile(Context context, Bitmap bitmap, String fileName,
                                           int compress) throws IOException {
        return saveBitmap(context, bitmap, fileName, EXP_JPEG, compress);
    }

    /**
     * Копирование файла в AppSpecificDirectory
     *
     * @param context  контекст
     * @param uri      путь к файлу
     * @param listener слушатель для получения состояния копирования файла
     */
    public static void copyFileToAppSpecificDirectory(Context context, Uri uri,
                                                      CopyFileListener listener)
            throws IOException, OutOfMemoryError {

        File fileDirectory = getAppSpecificDirectory(context);
        copyFile(context, fileDirectory, uri, listener);
    }

    /**
     * Копирование файла в кэш AppSpecificDirectory
     *
     * @param context  контекст
     * @param uri      путь к файлу
     * @param listener слушатель для получения состояния копирования файла
     */
    public static void copyFileToAppSpecificCashDirectory(Context context, Uri uri,
                                                          CopyFileListener listener)
            throws IOException, OutOfMemoryError {

        File outputFile = getAppSpecificCashDirectory(context);
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
     * Получить директорию AppSpecific
     * <p>
     * С 29 и выше апи другие приложения не смогут считать эти файлы
     * При удаление приложения, удаляются и файлы
     * <p>
     * Не нужены разрешения для записи и чтения
     *
     * @return директория AppSpecific
     */
    private static File getAppSpecificDirectory(Context context) {
        return isExternalStorageWritable()
                ? context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                : context.getFilesDir();
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
     * Получить директорию AppSpecific для кэша
     *
     * @return директория AppSpecific для кэша
     */
    private static File getAppSpecificCashDirectory(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * Создание файла в указанной директории и с указанным расширением
     *
     * @param storageDir - директория
     * @param exp        - расширение
     * @return - созданный файл
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
        return exp + "_" + timeStamp + "_";
    }

    /**
     * Сохранение Bitmap в файл
     *
     * @param context  - контектс
     * @param bitmap   - Bitmap, который сохраняется в файл
     * @param fileName - имя файла
     * @param exp      - расширение файла
     * @param compress - процент сжатия
     * @return - путь к файлу, в который записан Bitmap
     */
    private static File saveBitmap(@NonNull Context context, @NonNull Bitmap bitmap,
                                   @NonNull String fileName, @NonNull String exp,
                                   int compress) throws IOException {

        File outputFile = createFile(getAppSpecificDirectory(context), fileName, exp);

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
     * @param exp - тип файла
     * @return - тип компрессии
     */
    private static Bitmap.CompressFormat getCompressFormat(String exp) {
        return exp.equals(EXP_PNG) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
    }

    private static File createFileInAppSpecificDirectory(Context context, String exp)
            throws IOException {
        return createFile(getAppSpecificDirectory(context), getDefaultFileName(exp), exp);
    }

    private static void copyFile(Context context, File fileDirectoryToCopy, Uri inputUri,
                                 CopyFileListener listener)
            throws IOException {

        FileExtension fileExtension = new FileExtension(context, inputUri);

        String fileExp = fileExtension.getExp();
        String fileName = getDefaultFileName(fileExp);
        File outputFile = createFile(fileDirectoryToCopy, fileName, fileExp);

        fileExtension.copyTo(outputFile, listener);
    }
}