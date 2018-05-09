package com.example.akilino.player;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MusicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, SensorEventListener{

    final String songURLList[] = {
            "http://project-tango.org/Projects/TangoBand/Songs/files/01%20La%20Cumparsita.mp3",
            "http://ccmixter.org/content/VJ_Memes/VJ_Memes_-_Amanita_Muscaria.mp3",
            "http://ccmixter.org/content/Sassygal/Sassygal_-_What_do_you_Say_.mp3",
            "http://ccmixter.org/content/fluffy/fluffy_-_No_Brakes.mp3"
    };

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageButton imageButton_start;
    private ImageButton likeButton;
    private SensorManager sensorManager;
    private Sensor proximity;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private TextView songTotalDurationTextView, songCurrentDurationTextView, songName, authorName;
    private Utilities utilities;
    private SeekBar seekBarProgress, seekBarVolume;
    private AudioManager audioManager;
    private int volumeValue;
    private boolean emulatorIsRunning = false;
    private ArrayList<String> song, author;
    private int status;
    int[] indexListSong = {0,0,0,0};

    private int counter = 0;

    private String state = "initial";
    private String viewActivity = "music";

    private int mSelectedId;
    private boolean userSawDrawer = false;

    public MusicActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        loadVariables();

        if(!didUserSeeDrawer()){
            showDrawer();
            markDrawerSeen();
        }else{
            hideDrawer();
        }

        mSelectedId = savedInstanceState == null ? R.id.item_music : savedInstanceState.getInt(SELECTED_ITEM_ID);
        navigate(mSelectedId);

        updateTextViews();

        seekBarProgress.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public void loadVariables(){

        //Toolbar variables
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.music);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(),R.color.textColorPrimaryWhite));
        setSupportActionBar(toolbar);

        //Drawer variables
        navigationView = (NavigationView) findViewById(R.id.main_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Instantiate
        utilities = new Utilities();
        mediaPlayer = new MediaPlayer();

        //TextViews used on progress bar - Time
        songCurrentDurationTextView = (TextView) findViewById(R.id.songCurrentDurationText);
        songTotalDurationTextView = (TextView) findViewById(R.id.songTotalDurationText);

        //Variables of both progress and volume Seekbar
        seekBarProgress = (SeekBar)findViewById(R.id.seekBarProgress);
        seekBarVolume = (SeekBar) findViewById(R.id.seekBarVolume);

        //ImageButton Start
        imageButton_start = (ImageButton) findViewById(R.id.playPauseButton);

        //TextViews to show music and author title
        songName = (TextView) findViewById(R.id.songName);
        authorName = (TextView) findViewById(R.id.authorName);

        //Setting Up MediaPlayer and AudioManager
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //Sensor Variables
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        //ImageButton of the Like Button
        likeButton = (ImageButton) findViewById(R.id.likeButton);

        //ArrayList responsible to store data found on res/strings
        author = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.author)));
        song = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.song)));

        //Volume Control
        controlVolume();
        status = 0;
    }


    //Returns a boolean saying if the the user saw drawer
    private boolean didUserSeeDrawer(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userSawDrawer = sharedPreferences.getBoolean(FIRST_TIME,false);
        return userSawDrawer;
    }

    //Marks the Drawer as Seen
    private void markDrawerSeen(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, userSawDrawer).apply();
    }

    //Shows the Drawer
    private void showDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    //Hides the Drawer
    private void hideDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    //To allow navigation through the Drawer
    private void navigate(int mSelectedId){
        Intent intent;

        if(mSelectedId == R.id.item_music){
            if(this.viewActivity != "music"){
                drawerLayout.closeDrawer(GravityCompat.START);
                intent = new Intent(this, MusicActivity.class);
                this.viewActivity = "music";
                startActivity(intent);
            }else{
                drawerLayout.closeDrawer(GravityCompat.START);
                this.viewActivity = "music";
            }
        }

        if(mSelectedId == R.id.item_radio){
            if(this.viewActivity != "radio"){
                drawerLayout.closeDrawer(GravityCompat.START);
                intent = new Intent(this, RadioActivity.class);
                this.viewActivity = "radio";
                startActivity(intent);
            }else{
                drawerLayout.closeDrawer(GravityCompat.START);
                this.viewActivity = "radio";
            }
        }

        if(mSelectedId == R.id.item_video){
            if(this.viewActivity != "video"){
                drawerLayout.closeDrawer(GravityCompat.START);
                intent = new Intent(this, VideoActivity.class);
                this.viewActivity = "video";
                startActivity(intent);
            }else{
                drawerLayout.closeDrawer(GravityCompat.START);
                this.viewActivity = "video";
            }
        }
    }

    //Creates the Option Menu found on R.menu.menu_main
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Returns true if action_settings is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    //Sets item id to variable, when item on drawer is Clicked
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(true);
        mSelectedId = item.getItemId();

        navigate(mSelectedId);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, mSelectedId);
    }

    //If drawer is open, it is closed it back button is pressed.
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    //Responsbiel to update the progress bar
    public void updateProgressBar(){
        handler.postDelayed(updateTimeTask, 100);
    }

    //Responsible to update time on seekbarProgress
    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            songTotalDurationTextView.setText("" + utilities.millisecondsToTimer(totalDuration));
            songCurrentDurationTextView.setText("" + utilities.millisecondsToTimer(currentDuration));

            int progress = utilities.getProgressPercentage(currentDuration,totalDuration);
            seekBarProgress.setProgress(progress);

            handler.postDelayed(this, 100);
        }
    };


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utilities.progressToTimer(seekBar.getProgress(),totalDuration);

        mediaPlayer.seekTo(currentPosition);
        updateProgressBar();
    }

    public void updateTextViews(){
        authorName.setText(author.get(counter));
        songName.setText(song.get(counter));
    }

    //changes imageButton src
    public void changeToPlayButton(){
        imageButton_start.setImageResource(R.drawable.play_button);
    }

    //changes imageButton src
    public void changeToPauseButton(){
        imageButton_start.setImageResource(R.drawable.pause_button);
    }

    //Play or Pauses song, depending on state
    public void PlayPauseSong(View view) throws IOException {

        if(state == "initial"){
            changeToPauseButton();
            mediaPlayer.setDataSource(String.valueOf(songURLList[counter]));
            mediaPlayer.prepare();
            mediaPlayer.start();

            seekBarProgress.setProgress(0);
            seekBarProgress.setMax(100);
            updateProgressBar();

            state = "started";
        } else if (mediaPlayer.isPlaying()) {
            changeToPlayButton();
            mediaPlayer.pause();
        } else if(!mediaPlayer.isPlaying()){
            changeToPauseButton();
            mediaPlayer.start();
        }

    }

    //Increments counter, and takes action depending on whether the mediaPlayer is playing or not
    public void nextSong(View view) throws IOException {

        if (counter < songURLList.length - 1) {
            counter++;
        } else {
            counter = 0;
        }

        //Allows to change the color of the like button, case was clicked, while skipping up songs
        setUpLike();

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(String.valueOf(songURLList[counter]));
            mediaPlayer.prepare();
            updateTextViews();
            mediaPlayer.start();
        } else if(!mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(String.valueOf(songURLList[counter]));
            updateTextViews();
            mediaPlayer.prepare();
        }
    }

    //Decrements counter, and takes action depending on whether the mediaPlayer is playing or not
    public void previousSong(View view) throws IOException {

        if(counter <= 0){
            counter  = songURLList.length - 1;
        }else{
            counter = counter - 1;
        }

        //Allows to change the color of the like button, case was clicked, while skipping up songs
        setUpLike();

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(String.valueOf(songURLList[counter]));
            mediaPlayer.prepare();
            updateTextViews();
            mediaPlayer.start();
        } else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(String.valueOf(songURLList[counter]));
            updateTextViews();
            mediaPlayer.prepare();;

        }
    }

    //Verifies the index of the array, if the like button was clicked or not
    public void setUpLike(){
        if(indexListSong[counter] == 1){
            likeButton.setImageResource(R.drawable.like_pressed_button);
        }else if(indexListSong[counter] == 0){
            likeButton.setImageResource(R.drawable.like_unpressed_button);
        }
    }

    //Sets up the Like button
    public void addLike(View view){

        if(indexListSong[counter] == 0){
            likeButton.setImageResource(R.drawable.like_pressed_button);
            indexListSong[counter] = 1;
        }else if(indexListSong[counter] == 1){
            likeButton.setImageResource(R.drawable.like_unpressed_button);
            indexListSong[counter] = 0;
        }
    }

    //Goes to next song, when music reaches its end
    @Override
    public void onCompletion(MediaPlayer mp) {

        if(counter >= songURLList.length - 1){
            counter = 0;

        }else{
            counter++;
        }

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(String.valueOf(songURLList[counter]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateTextViews();
        mediaPlayer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    //Responsible for taking actions according to the sensor proximity
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!emulatorIsRunning){
            if(status == 0){
                volumeValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                status = 1;
            }

            if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                if(event.values[0] == 0){
                    Log.d("Progress", "onProgressChanged: " + volumeValue);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,2,0);
                    seekBarVolume.setProgress(2);
                    Toast.makeText(getApplicationContext(),"Volume Down",Toast.LENGTH_SHORT).show();
                }else{
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volumeValue,0);
                    seekBarVolume.setProgress(volumeValue);
                    Toast.makeText(getApplicationContext(),"Software Structures for User Interface",Toast.LENGTH_SHORT).show();
                    status = 0;
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    //Controls and Sets up the volume
    private void controlVolume(){
        seekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
                Log.d("Progress", "onProgressChanged: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //It's a toast. Notifies the user through GUI
    public void onPlayListClicked(View view){
        Toast.makeText(this,"Feature not available", Toast.LENGTH_SHORT).show();
    }
}
