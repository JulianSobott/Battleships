package gui.Media;

import core.utils.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;

public class MusicPlayer {

    private static MusicPlayer instance = new MusicPlayer();
    private MediaPlayer mediaPlayer;
    private boolean playSoundEffects = false;
    private HashMap<Sound, MediaPlayer> soundEffects = new HashMap<>();

    private MusicPlayer() {
        initNewSong();
    }

    public static void loadSounds() {
        new Thread(() -> {
            for(Sound sound : Sound.values()) {
                Media media = new Media(new File("assets/sound_effects/", sound.fileName).toURI().toString() + ".mp3");
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                instance.soundEffects.put(sound, mediaPlayer);
            }
        }).start();
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

    public static void playSound(Sound sound) {
        if(instance.playSoundEffects) {
            double volume = instance.mediaPlayer.getVolume();
            instance.mediaPlayer.setVolume(0.2);
            instance.soundEffects.get(sound).seek(new Duration(0));
            instance.soundEffects.get(sound).play();
            instance.soundEffects.get(sound).setOnEndOfMedia(()->instance.mediaPlayer.setVolume(volume));
        }
    }

    private File getRandomSongFile() {
        File musicFolder = new File("assets/music/");
        File[] musicFiles = musicFolder.listFiles();
        assert musicFiles != null;
        return musicFiles[Random.random.nextInt(musicFiles.length)];
    }


    public enum Sound {
        WIN("win"), LOSE("lose"), HIT_SHIP("hit_ship"), HIT_WATER("hit_water");

        private String fileName;

        private Sound(String fileName) {
            this.fileName = fileName;
        }
    }
}
