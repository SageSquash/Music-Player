package com.example.musicplayer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class PlayerActivity extends AppCompatActivity {

    Button btn_next,btn_previous,btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;
    String sname;

    static MediaPlayer mymediaPlayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateseekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btn_next=(Button)findViewById(R.id.next);
        btn_previous=(Button)findViewById(R.id.previous);
        btn_pause=(Button)findViewById(R.id.pause);
        songTextLabel=(TextView) findViewById(R.id.songLabel);
        songSeekbar = (SeekBar) findViewById(R.id.seekbar);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




        updateseekBar=new Thread(){

            @Override
            public void run() {

                int totalDuration = mymediaPlayer.getCurrentPosition();
                int currentPosition = 0;

                while(currentPosition<totalDuration){
                    try{
                        sleep(500);
                        currentPosition=mymediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        };


        if(mymediaPlayer!=null){
            mymediaPlayer.stop();
            mymediaPlayer.release();
        }

        Intent i = getIntent();
         Bundle bundle = i.getExtras();

         mySongs=(ArrayList) bundle.getParcelableArrayList("songs");
         sname = mySongs.get(position).getName().toString();

         String songName = i.getStringExtra("songname");

         songTextLabel.setText(songName);
         songTextLabel.setSelected(true);

         position = bundle.getInt("pos",0);
         Uri u = Uri.parse(mySongs.get(position).toString());

         mymediaPlayer = MediaPlayer.create(getApplicationContext(),u);

         mymediaPlayer.start();
         songSeekbar.setMax(mymediaPlayer.getDuration());

         updateseekBar.start();

         songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
         songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

         songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

                 mymediaPlayer.seekTo(seekBar.getProgress());

             }
         });

         btn_pause.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 songSeekbar.setMax(mymediaPlayer.getDuration());

                 if(mymediaPlayer.isPlaying()) {

                     btn_pause.setBackgroundResource(R.drawable.icon_play);
                     mymediaPlayer.pause();
                 }
                 else {
                     btn_pause.setBackgroundResource(R.drawable.icon_pause);
                     mymediaPlayer.start();
                 }
             }
         });
         btn_next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 mymediaPlayer.stop();
                 mymediaPlayer.release();
                 position = ((position+1)%mySongs.size());

                 Uri u = Uri.parse(mySongs.get(position).toString());
                 mymediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                 sname = mySongs.get(position).getName().toString();
                 songTextLabel.setText(sname);

                 mymediaPlayer.start();
             }
         });

         btn_previous.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 mymediaPlayer.stop();
                 mymediaPlayer.release();

                 position = ((position-1)<0)?(mySongs.size()-1):(position-1);
                 Uri u = Uri.parse(mySongs.get(position).toString());
                 mymediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                 sname = mySongs.get(position).getName().toString();
                 songTextLabel.setText(sname);

                 mymediaPlayer.start();
             }
         });
         }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
