package ru.sequenia.pikcha_sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sequenia.photo.Photos;
import com.sequenia.photo.listeners.PhotoDifferentResultsListener;
import com.sequenia.photo.listeners.PhotoErrorListener;
import com.sequenia.photo.listeners.PhotoWaitListener;
import com.squareup.picasso.Picasso;

import java.io.File;

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
                photos.takePhotoFromCamera();
            }
        });

        findViewById(R.id.open_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photos.selectedPhotoFromGallery();
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

    private void showPhoto(String path) {
        // Абсолютны путь к файлу, можно отображать
        Picasso.with(this)
                .load(new File(path))
                .into(photo);
    }

    @Override
    public void onError(int errorCode) {
        showMessage("ERROR CODE " + errorCode);
    }

    @Override
    public void visibilityWait(boolean state) {
        showMessage(state ? "START" : "STOP");
    }

    /*@Override
    public void getPath(String path) {
        showMessage("Пришел результат с камеры или из галереи");
        showPhoto(path);
    }*/

    @Override
    public void getPathFromGallery(String path) {
        showMessage("Пришел результат из галереи");
        showPhoto(path);
    }

    @Override
    public void getPathFromCamera(String path) {
        showMessage("Пришел результат с камеры");
        showPhoto(path);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
