package com.example.akilino.player;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by akili on 08/05/2016.
 */
public class VideoActivity extends AppCompatActivity{

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mToolbar = (Toolbar)findViewById(R.id.toolbar_video);
        mToolbar.setTitle(R.string.video);
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColorPrimaryWhite));
        setSupportActionBar(mToolbar);

    }
}
