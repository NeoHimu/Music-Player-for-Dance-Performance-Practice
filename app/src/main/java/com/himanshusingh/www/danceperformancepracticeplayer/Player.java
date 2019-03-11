package com.himanshusingh.www.danceperformancepracticeplayer;

import android.content.DialogInterface;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by himanshu on 11/3/19.
 */

public class Player extends AppCompatActivity {
    Timer timer;
    Handler handler;
    Handler handler1;
    Runnable runnable1;
    ImageView btPlayPause;
    ImageView getBtPlayPauseTest;
    EditText etStartMin, etEndMin, etStartSec, etEndSec, etCount;
    MediaPlayer mp=new MediaPlayer();
    MediaPlayer mpTest = new MediaPlayer();
    boolean isPlaying = false;
    TextView currentSong;
    int startTime = 0;
    SeekBar seekBar;
    TextView tvProgress;
    int endTime = 0;
    int count = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        seekBar = findViewById(R.id.idSeekbar);
        getBtPlayPauseTest = findViewById(R.id.idPlayPauseTest);
        tvProgress = findViewById(R.id.idTime);
        btPlayPause = findViewById(R.id.idPlayPause);
        currentSong = findViewById(R.id.idCurrentSong);
        etStartMin = findViewById(R.id.idStartTimeMin);
        etStartSec = findViewById(R.id.idStartTimeSec);
        etEndMin = findViewById(R.id.idEndTimeMin);
        etEndSec = findViewById(R.id.idEndTimeSec);
        etCount = findViewById(R.id.idEnoofTimes);
        handler = new Handler();
        handler1 = new Handler();

        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("song_url");
        String song_name = bundle.getString("song_name");
        currentSong.setText(song_name);





        try {
            mp.setDataSource(url);//Write your location here
            mp.prepare();

            mpTest.setDataSource(url);//Write your location here
            mpTest.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        seekBar.setMax(mpTest.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    mpTest.seekTo(progress);
                }
                tvProgress.setText((progress/1000)/60+":" +(progress/1000)%60);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        getBtPlayPauseTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying==false)
                {
                    isPlaying = true;
                    getBtPlayPauseTest.setBackgroundResource(R.drawable.icon_pause);
                    playCycle();
//                    mpTest.start();
                    mpTest.start();
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isPlaying) {
                                        tvProgress.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvProgress.setText((mpTest.getCurrentPosition()/1000)/60+":" +(mpTest.getCurrentPosition()/1000)%60 );
                                            }
                                        });
                                    } else {
                                        timer.cancel();
                                        timer.purge();
                                    }
                                    }
                                });
                            }
                        }, 10, 1000);
                }
                else {
                    isPlaying = false;
                    mpTest.pause();
                    getBtPlayPauseTest.setBackgroundResource(R.drawable.icon_play);
                }
            }
        });




        btPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean fieldsOK = validate(new EditText[] { etStartMin, etStartSec, etEndMin, etEndSec, etCount });
                if(fieldsOK==false)
                {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle(":(")
                            .setMessage("Sare entry bhi fill nhi kar sakte?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }



                startTime = Integer.parseInt(etStartMin.getText().toString())*60 + Integer.parseInt(etStartSec.getText().toString());
                endTime = Integer.parseInt(etEndMin.getText().toString())*60 + Integer.parseInt(etEndSec.getText().toString());

                if(startTime>endTime)
                {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle(":(")
                            .setMessage("Gaana reverse mein play karna hai kya?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                count = Integer.parseInt(etCount.getText().toString());
                new PlaySong().execute(startTime+"", endTime+"", count+"");


            }
        });
    }

    private void playCycle() {

        seekBar.setProgress(mpTest.getCurrentPosition());
        if(isPlaying)
        {
            runnable1 = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler1.postDelayed(runnable1, 1000);
        }

    }

    public String convertDuration(long duration) {
        String out = null;
        long hours=0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;

    }

    private boolean validate(EditText[] fields){
        for(int i = 0; i < fields.length; i++){
            EditText currentField = fields[i];
            if(currentField.getText().toString().length() <= 0){
                return false;
            }
        }
        return true;
    }

    private class PlaySong extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            int start = Integer.parseInt(params[0])*1000;
            Log.d("start ", start+"");
            int end = Integer.parseInt(params[1])*1000;
            int count = Integer.parseInt(params[2]);

            for (int i = 0; i < count; i++) {
                mp.seekTo(start);
                mp.start();
                try {
                    Thread.sleep((end-start)+2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mp.pause();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            btPlayPause.setClickable(true);
        }

        @Override
        protected void onPreExecute() {
            btPlayPause.setClickable(false);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
}
