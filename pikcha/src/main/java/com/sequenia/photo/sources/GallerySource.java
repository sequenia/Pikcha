package com.sequenia.photo.sources;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sequenia.file.UriUtils;
import com.sequenia.photo.R;
import com.sequenia.photo.sources.permissions.PhotoPermissionChecker;

import static com.sequenia.ErrorCode.FILE_PATH_NOT_FOUND;

/**
 * Изображения из галереи
 */
public class GallerySource extends Source {

    private static final int GALLERY_REQUEST = 10101;

    public GallerySource(Context context) {
        super(context);
    }

    @Override
    protected void checkPermission(PhotoPermissionChecker permissionChecker) {
        permissionChecker.permissionForGallery();
    }

    @Override
    protected void parseResultData(Intent data) {
        Uri uri = UriUtils.getUriFromData(data);

        if (uri == null) {
            showError(FILE_PATH_NOT_FOUND);
            return;
        }

        returnResult(uri);
        returnDifferentResult(uri);
    }

    @Override
    protected int getRequestCode() {
        return GALLERY_REQUEST;
    }

    @Override
    protected Intent getIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return Intent.createChooser(intent, getText(R.string.add_photo));
    }

    protected void returnDifferentResult(Uri uri) {
        if (getContext() != null && differentResultsListener != null) {
            differentResultsListener.getPathFileFromGallery(uri);
        }
    }

    private String getText(int res) {
        Context context = getContext();
        return context != null ? context.getString(res) : null;
    }
}
