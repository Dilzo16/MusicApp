package chathu.learn.music;

import android.media.MediaPlayer;

/**
 * singleton helper class to access mediaplayer from  any activity
 */
public class MyMediaPlayer {
    static MediaPlayer instance;

    public static MediaPlayer getInstance(){
        if(instance==null){
            instance=new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex=-1;//song is not clicked
}
