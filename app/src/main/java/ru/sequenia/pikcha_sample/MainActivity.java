package ru.sequenia.pikcha_sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.sequenia.ErrorCode;
import com.sequenia.photo.Photos;
import com.sequenia.photo.listeners.PhotoDifferentResultsListener;
import com.sequenia.photo.listeners.PhotoErrorListener;
import com.sequenia.photo.listeners.PhotoWaitListener;

/**
 * Пример использования
 */
public class MainActivity extends AppCompatActivity implements
        PhotoDifferentResultsListener, PhotoErrorListener, PhotoWaitListener {

    private Photos photos;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        photos = new Photos(this);

        photo = findViewById(R.id.photo);

        findViewById(R.id.open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photos.takePhotoFromCamera(MainActivity.this);
            }
        });

        findViewById(R.id.open_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photos.selectedPhotoFromGallery(MainActivity.this);
            }
        });

        findViewById(R.id.open_chooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photos.selectMethodOfAddingPhoto(MainActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photos.onResult(requestCode, resultCode, data);
    }

    @Override
    public void startIntentForPhoto(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    private void showPhoto(Uri uri) {
        // Абсолютны путь к файлу, можно отображать
        Glide.with(this)
                .load(uri)
                .into(photo);
    }

    @Override
    public void onError(ErrorCode errorCode) {
        showMessage("ERROR CODE " + errorCode.name());
    }

    @Override
    public void visibilityWait(boolean state) {
        showMessage(state ? "START" : "STOP");
    }

    @Override
    public void getPathFileFromGallery(Uri uri) {
        showMessage("Пришел результат из галереи");
        showPhoto(uri);
    }

    @Override
    public void getPathFromCamera(Uri uri) {
        showMessage("Пришел результат с камеры");
        showPhoto(uri);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
