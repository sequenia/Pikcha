package com.sequenia.photo.sources.permissions;

import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.sequenia.photo.R;

import java.util.List;

/**
 * Проверка разрешений
 * <p>
 * Обертка над библиотекой TedPermission
 */
class PermissionChecker {

    private Context context;
    private PermissionGrantedListener permissionGrantedListener;
    private PermissionDeniedListener permissionDeniedListener;

    static PermissionChecker init(Context context) {
        return new PermissionChecker(context);
    }

    private PermissionChecker(Context context) {
        this.context = context;
    }

    /**
     * Задачать слушатель на получение результата
     *
     * @param listener - слушатель для получения результата
     */
    PermissionChecker setPermissionGrantedListener(PermissionGrantedListener listener) {
        this.permissionGrantedListener = listener;
        return this;
    }

    /**
     * Задачать слушатель на получение результата
     *
     * @param listener - слушатель для получения результата
     */
    PermissionChecker setPermissionDeniedListener(PermissionDeniedListener listener) {
        this.permissionDeniedListener = listener;
        return this;
    }

    /**
     * Проверка разрешений
     *
     * @param permissions - запрашиваемые разрешения
     */
    void checkPermissions(String... permissions) {
        if (context == null) {
            return;
        }

        TedPermission.create()
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (permissionGrantedListener != null) {
                            permissionGrantedListener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        if (permissionDeniedListener != null) {
                            permissionDeniedListener.onPermissionDenied(deniedPermissions);
                        }
                    }
                })
                .setDeniedMessage(R.string.permissions_string_help_text)
                .setDeniedCloseButtonText(R.string.permissions_quit)
                .setGotoSettingButtonText(R.string.permissions_settings)
                .setPermissions(permissions)
                .check();
    }

}
