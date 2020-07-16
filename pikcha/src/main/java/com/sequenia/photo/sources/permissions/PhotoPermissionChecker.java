package com.sequenia.photo.sources.permissions;

import android.Manifest;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Проверка разрешений для работы библиотеки
 */
public class PhotoPermissionChecker {

    private Context context;

    private PermissionGrantedListener permissionGrantedListener;
    private PermissionDeniedListener permissionDeniedListener;

    public PhotoPermissionChecker(Context context) {
        this.context = context;
    }

    public void setPermissionGrantedListener(PermissionGrantedListener listener) {
        this.permissionGrantedListener = listener;
    }

    public void setPermissionDeniedListener(PermissionDeniedListener listener) {
        this.permissionDeniedListener = listener;
    }

    /**
     * Запрос разрешений для камеры
     */
    public void permissionForCamera() {
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
    public void permissionForGallery() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            permissionGrantedListener.onPermissionGranted();
            return;
        }

        PermissionChecker.init(context)
                .setPermissionGrantedListener(permissionGrantedListener)
                .setPermissionDeniedListener(permissionDeniedListener)
                .checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void permissionForChooser() {
        permissionForCamera();
    }
}
