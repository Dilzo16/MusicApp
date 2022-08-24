package chathu.learn.music;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay,previous,next,middleRotate,addToFav;

    ArrayList<AudioModel>favList;

    ArrayList<AudioModel>songsList;
    AudioModel currentSong,currentFav;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();
    int x=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv=findViewById(R.id.songTitle);
        currentTimeTv=findViewById(R.id.currentTime);
        totalTimeTv=findViewById(R.id.totalTime);

        seekBar=findViewById(R.id.seekBar);

        previous=findViewById(R.id.previous);
        next=findViewById(R.id.next);
        pausePlay=findViewById(R.id.pausePlay);
        middleRotate=findViewById(R.id.middle_rotate);
        addToFav=findViewById(R.id.addToFav);

        titleTv.setSelected(true);//marquee text

        songsList= (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());//change seek bar possition with the song goes on
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));//update the current time with song goes on

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_round_pause);
                        middleRotate.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_round_play);
                        middleRotate.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);//upper updates will hapen in each 100ms
            }
        });

        //change the song possition with manual seek bar change
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void setResourcesWithMusic(){
        currentSong=songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v -> pausePlay());
        next.setOnClickListener(v -> playNext());
        previous.setOnClickListener(v -> playPrevious());

        //
        //addToFav.setOnClickListener(v -> addToFavList());

        playMusic();

    }


    //method to covert milliseconds
    public static String convertToMMSS(String dur){
        Long millis=Long.parseLong(dur);

        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis)%TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis)%TimeUnit.MINUTES.toSeconds(1));

    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            //
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNext();
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void playNext(){

        if(MyMediaPlayer.currentIndex==songsList.size()-1){// check if the current song is last one, then return
            return;
        }
        MyMediaPlayer.currentIndex+=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private void playPrevious(){

        if(MyMediaPlayer.currentIndex==0){
            return;
        }
        MyMediaPlayer.currentIndex-=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){

        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }

    }

    private void addToFavList(){


    }





}