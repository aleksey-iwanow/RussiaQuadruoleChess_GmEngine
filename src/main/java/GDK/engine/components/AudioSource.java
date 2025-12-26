package GDK.engine.components;

import GDK.engine.Config;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class AudioSource {
    public Media clip;
    private MediaPlayer mediaPlayer;
    private String clipPath;

    public AudioSource(String clipPath){
        setMedia(clipPath);
    }

    public void Play(String clipPath, boolean loop){
        setMedia(clipPath);
        Play(loop);
    }

    private void setMedia(String clipPath) {
        this.clipPath = clipPath;
        clip = new Media(new File(Config.PATH_PROJECT + clipPath).toURI().toString());
        mediaPlayer = new MediaPlayer(clip);
    }

    public void Play(boolean loop){
        mediaPlayer.stop();
        if (loop)
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public void Play(){
        Play(false);
    }
    public void Stop(){
        mediaPlayer.setOnReady(() -> {});
        mediaPlayer.stop();
    }

}
