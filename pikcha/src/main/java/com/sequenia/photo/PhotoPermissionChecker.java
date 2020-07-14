package com.sequenia.photo;

import android.Manifest;
import android.content.Context;

import com.sequenia.photo.PermissionChecker.PermissionDeniedListener;
import com.sequenia.photo.PermissionChecker.PermissionGrantedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Проверка разрешений для работы библиотеки
 */
class PhotoPermissionChecker {

    /**
     * Запрос разрешений для камеры
     */
    static void permissionForCamera(Context context,
                                    PermissionGrantedListener permissionGrantedListener,
                                    PermissionDeniedListener permissionDeniedListener) {

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        String[] permissionConverted = permissions.toArray(new String[0]);

        PermissionChecker.init(context)
                .setPermissionGrantedListener(permissionGrantedListener)
                .setPermissionDeniedListener(permissionDeniedListener)
                .checkPermissions(permissionConverted);
    }

    /**
     * Запрос разрешений для галереи
     */
    static void permissionForGallery(Context context,
                                     PermissionGrantedListener permissionGrantedListener,
                                     PermissionDeniedListener permissionDeniedListener) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            permissionGrantedListener.onPermissionGranted();
            return;
        }

        PermissionChecker.init(context)
                .setPermissionGrantedListener(permissionGrantedListener)
                .setPermissionDeniedListener(permissionDeniedListener)
                .checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    static void permissionForChooser(Context context,
                                     PermissionGrantedListener permissionGrantedListener,
                                     PermissionDeniedListener permissionDeniedListener) {

        permissionForCamera(context, permissionGrantedListener, permissionDeniedListener);
    }
}
