package com.sequenia.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Работа с Bitmaps
 */
public class BitmapsUtils {

    /**
     * Поворот изображения
     *
     * @param bitmap      - изображение, которое необходимо повернуть
     * @param orientation - оринтация изображения
     * @return - повернутое изображение
     */
    public static Bitmap rotateBitmap(@NonNull Bitmap bitmap, int orientation)
            throws OutOfMemoryError {
        Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(),
                getRotationMatrix(orientation), true);

        if (bmRotated == null) {
            return bitmap;
        }

        if (bmRotated != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        return bmRotated;
    }

    /**
     * Создание матрицы для поворота изображения
     *
     * @param orientation - ориетация изображения
     * @return - матрица для поворота
     */
    public static Matrix getRotationMatrix(int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
        }
        return matrix;
    }

    /**
     * Обрезание изображения в квадрат
     *
     * @param bitmap - изображение для обрезки в квадрат
     * @return квадратный битмап в случае удачи и оригинал при неудаче
     */
    public static Bitmap cropToSquare(@NonNull Bitmap bitmap) {
        int min = Math.max(bitmap.getWidth(), bitmap.getHeight());
        Bitmap result = ThumbnailUtils.extractThumbnail(bitmap, min, min);

        if (result == null) {
            return bitmap;
        }

        if (result != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return result;
    }

    /**
     * Изменить размер изображения
     *
     * @param bitmap       - изображение, у которого изменяется размер
     * @param resizeWidth  - размер по ширине
     * @param resizeHeight - размер по высота
     * @return - изображение с измененным размером
     */
    public static Bitmap resizeBitmap(@NonNull Bitmap bitmap, int resizeWidth, int resizeHeight) {
        int min = Math.max(bitmap.getWidth(), bitmap.getHeight());
        int width = bitmap.getWidth() * resizeWidth / min;
        int height = bitmap.getHeight() * resizeHeight / min;
        Bitmap result = Bitmap.createScaledBitmap(bitmap, width, height, true);

        if (result == null) {
            return bitmap;
        }

        if (result != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return result;
    }

    /**
     * Загрузка файла в Bitmap, не превыщающий размеров reqWidth и reqHeight
     *
     * @param path      - путь к файлу
     * @param reqWidth  - граница по ширине (степень 2)
     * @param reqHeight - граница по высоте (степень 2)
     * @return - загруженный Bitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            FileInputStream in = new FileInputStream(path);
            BitmapFactory.decodeStream(in, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            in = new FileInputStream(path);
            return BitmapFactory.decodeStream(in, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
