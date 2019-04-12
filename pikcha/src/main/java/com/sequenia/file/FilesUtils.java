package com.sequenia.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * Создание файла JPEG в открытой директории с дефолтовым именем
     *
     * @return - файл с дефолтовым именем
     */
    public static File createJPGFileInOpenDirectory(Context context) throws IOException {
        return createFile(createOpenDirectory(context), getDefaultFileName(EXP_JPEG), EXP_JPEG);
    }

    /**
     * Создание файла JPEG в закрытой директории с дефолтовым именем
     *
     * @return - файл с дефолтовым именем
     */
    public static File createJPGFileInCloseDirectory(Context context) throws IOException {
        return createFile(createCloseDirectory(context), getDefaultFileName(EXP_JPEG), EXP_JPEG);
    }

    /**
     * Создание файла JPEG в открытой директории с заданным именем
     *
     * @param name - имя файла
     * @return - файл с дефолтовым именем
     */
    public static File createJPGFileInOpenDirectory(Context context, String name)
            throws IOException {
        return createFile(createOpenDirectory(context), name, EXP_JPEG);
    }

    /**
     * Создание файла JPEG в закрытой директории с заданным именем
     *
     * @param name - имя файла
     * @return - файл с дефолтовым именем
     */
    public static File createJPGFileInCloseDirectory(Context context, String name)
            throws IOException {
        return createFile(createCloseDirectory(context), name, EXP_JPEG);
    }

    /**
     * Создание файла JPEG в открытой директории с дефолтовым именем
     *
     * @return - файл с дефолтовым именем
     */
    public static File createPNGFileInOpenDirectory(Context context) throws IOException {
        return createFile(createOpenDirectory(context), getDefaultFileName(EXP_PNG), EXP_PNG);
    }

    /**
     * Создание файла JPEG в закрытой директории с дефолтовым именем
     *
     * @return - файл с дефолтовым именем
     */
    public static File createPNGFileInCloseDirectory(Context context) throws IOException {
        return createFile(createCloseDirectory(context), getDefaultFileName(EXP_PNG), EXP_PNG);
    }

    /**
     * Создание файла JPEG в открытой директории с заданным именем
     *
     * @param name - имя файла
     * @return - файл с дефолтовым именем
     */
    public static File createPNGFileInOpenDirectory(Context context, String name)
            throws IOException {
        return createFile(createOpenDirectory(context), name, EXP_PNG);
    }

    /**
     * Создание файла JPEG в закрытой директории с заданным именем
     *
     * @param name - имя файла
     * @return - файл с дефолтовым именем
     */
    public static File createPNGFileInCloseDirectory(Context context, String name)
            throws IOException {
        return createFile(createCloseDirectory(context), name, EXP_PNG);
    }

    /**
     * Запись Bitmap в файл
     *
     * @param base     - Bitmap, который нужно сохранить в файле
     * @param compress - процент сжатия
     * @return путь к файлу, в который записан Bitmap
     */
    public static String saveBitmapToPNGFile(Context context, Bitmap base, int compress)
            throws IOException {
        return saveBitmap(context, base, getDefaultFileName(EXP_PNG), EXP_PNG, compress);
    }

    /**
     * Запись Bitmap в файл
     *
     * @param base     - Bitmap, который нужно сохранить в файле
     * @param name     - имя файла
     * @param compress - процент сжатия
     * @return путь к файлу, в который записан Bitmap
     */
    public static String saveBitmapToPNGFile(Context context, Bitmap base, String name,
                                             int compress) throws IOException {
        return saveBitmap(context, base, name, EXP_PNG, compress);
    }

    /**
     * Запись Bitmap в файл
     *
     * @param base     - Bitmap, который нужно сохранить в файле
     * @param compress - процент сжатия
     * @return путь к файлу, в который записан Bitmap
     */
    public static String saveBitmapToJPGFile(Context context, Bitmap base, int compress)
            throws IOException {
        return saveBitmap(context, base, getDefaultFileName(EXP_JPEG), EXP_JPEG, compress);
    }

    /**
     * Запись Bitmap в файл
     *
     * @param base     - Bitmap, который нужно сохранить в файле
     * @param name     - имя файла
     * @param compress - процент сжатия
     * @return путь к файлу, в который записан Bitmap
     */
    public static String saveBitmapToJPGFile(Context context, Bitmap base, String name,
                                             int compress) throws IOException {
        return saveBitmap(context, base, name, EXP_JPEG, compress);
    }

    /**
     * Удаляем файла
     *
     * @param path - путь к файлу, который удаляется
     */
    public static void deleteFile(String path) {
        if (path == null) {
            return;
        }

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Создание закрытой директории (доступ только для приложения)
     *
     * @return - директория
     */
    private static File createCloseDirectory(Context context) {
        return isExternalStorageWritable()
                ? context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                : context.getFilesDir();
    }

    /**
     * Создание открытой директории(файлы отображаются в проводниках)
     *
     * @return - директория
     */
    private static File createOpenDirectory(Context context) {
        return isExternalStorageWritable()
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                : context.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE);
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

        File image = File.createTempFile(name, "." + exp, storageDir);
        // Доступность файла для внешних приложений
        image.setReadable(true, false);
        return image;
    }

    private static String getDefaultFileName(String exp) {
        // Создание имени файла
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        return exp + "_" + timeStamp + "_";
    }

    /**
     * Сохранение Bitmap в файл
     *
     * @param context  - контектс
     * @param base     - Bitmap, который сохраняется в файл
     * @param name     - имя файла
     * @param exp      - расширение файла
     * @param compress - процент сжатия
     * @return - путь к файлу, в который записан Bitmap
     */
    private static String saveBitmap(@NonNull Context context, @NonNull Bitmap base,
                                     @NonNull String name, @NonNull String exp,
                                     int compress) throws IOException {
        File file = createFile(createOpenDirectory(context), name, exp);

        if (file == null) {
            return null;
        }

        FileOutputStream fOut = new FileOutputStream(file);
        base.compress(getCompressFormat(exp), compress, fOut);
        fOut.flush();
        fOut.close();
        return file.getAbsolutePath();
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

    /**
     * @return true, если внешнее хранилище доступно для записи
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Проверка файла на существование и
     * корректность информации
     *
     * @param path - путь к файлу
     * @return - true - файл существует и информация в нем корректна
     */
    public static boolean checkedFile(String path) {
        if (path == null) {
            return false;
        }

        File file = new File(path);
        return file.exists() && file.length() > 0;
    }

    /**
     * Получение поворота файла
     *
     * @param path - путь к файлу
     * @return - поворот файла
     */
    public int getFileOrientation(String path) throws IOException {
        ExifInterface exif = new ExifInterface(path);
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
    }

    /**
     * Копирует файл в новой директории
     *
     * @param context - контекст
     * @param uri     - ури на файл
     * @return - путь к файлу
     */
    public static String copyFile(Context context, Uri uri) throws IOException, OutOfMemoryError {
        ParcelFileDescriptor file = context.getContentResolver().openFileDescriptor(uri, "r");
        File dbFile = createJPGFileInOpenDirectory(context);

        if (file == null) {
            return null;
        }

        InputStream fileStream = new FileInputStream(file.getFileDescriptor());
        OutputStream newDatabase = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        int length;

        while ((length = fileStream.read(buffer)) > 0) {
            newDatabase.write(buffer, 0, length);
        }

        newDatabase.flush();
        fileStream.close();
        newDatabase.close();
        return dbFile.getAbsolutePath();
    }

}