package com.sequenia.photo;

import android.Manifest;
import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

/**
 * Работа с пермишинами
 */
public class PermissionManager {

    /**
     * запрос разрешения на телефон
     */
    public static void storagePermission(Context context, PermissionListener listener) {
        permissions(context, listener,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static void permissions(Context context, PermissionListener permissionlistener,
                                    String... permissions) {
        TedPermission.with(context)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(R.string.permissions_string_help_text)
                .setDeniedCloseButtonText(R.string.permissions_quit)
                .setGotoSettingButtonText(R.string.permissions_settings)
                .setPermissions(permissions)
                .check();
    }
}
