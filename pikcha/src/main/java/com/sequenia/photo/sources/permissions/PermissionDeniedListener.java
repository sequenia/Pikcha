package com.sequenia.photo.sources.permissions;

import java.util.List;

public interface PermissionDeniedListener {
    void onPermissionDenied(List<String> deniedPermissions);
}
