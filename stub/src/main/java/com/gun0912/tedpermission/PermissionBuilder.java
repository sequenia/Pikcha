package com.gun0912.tedpermission;

public abstract class PermissionBuilder<T extends PermissionBuilder> {

    public T setPermissionListener(PermissionListener listener) {
        throw new RuntimeException("stub!");
    }

    public T setPermissions(String... permissions) {
        throw new RuntimeException("stub!");
    }

    public T setDeniedMessage(int stringRes) {
        throw new RuntimeException("stub!");
    }

    public T setGotoSettingButtonText(int stringRes) {
        throw new RuntimeException("stub!");
    }

    public T setDeniedCloseButtonText(int stringRes) {
        throw new RuntimeException("stub!");
    }

}
