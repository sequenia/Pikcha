package com.gun0912.tedpermission;

import android.content.Context;

public class TedPermission {

    public static Builder with(Context context) {
        throw new RuntimeException("stub!");
    }

    public static class Builder extends PermissionBuilder<Builder> {

        private Builder(Context context) {
            super(context);
        }

        public void check() {
            throw new RuntimeException("stub!");
        }

    }
}
