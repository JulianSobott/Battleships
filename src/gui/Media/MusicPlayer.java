package gui.Media;

import core.utils.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class MusicPlayer {

    private static MusicPlayer instance = new MusicPlayer();
    private MediaPlayer mediaPlayer;

    private MusicPlayer() {
        initNewSong();
    }

    private void initNewSong() {
        Media song = new Media(getRandomSongFile().toURI().toString());
        mediaPlayer = new MediaPlayer(song);
        mediaPlayer.setOnEndOfMedia(this::startNewSong);
    }

    private void startNewSong() {
        initNewSong();
        mediaPlayer.play();
    }

    public static void startMusic() {
        instance.startNewSong();
    }

    public static void stopMusic() {
        instance.mediaPlayer.stop();
    }

    public static void setVolume(double volume) {
        instance.mediaPlayer.setVolume(volume);
    }

    public static double getVolume() {
        return instance.mediaPlayer.getVolume();
    }

    private File getRandomSongFile() {
        File musicFolder = new File("assets/music/");
        File[] musicFiles = musicFolder.listFiles();
        assert musicFiles != null;
        return musicFiles[Random.random.nextInt(musicFiles.length)];
    }


}
