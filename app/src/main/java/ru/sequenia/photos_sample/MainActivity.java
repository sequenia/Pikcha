package ru.sequenia.photos_sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sequenia.navigation_router.NavigationRouterActivity;

public class MainActivity extends NavigationRouterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
    }

    @Override
    protected int getContainerId() {
        return R.id.content;
    }

    @Override
    protected Fragment openFirstScreen() {
        return new AddPhotoFragment();
    }
}
