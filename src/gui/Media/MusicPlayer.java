package gui.Media;

import core.utils.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

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
            for (Sound sound : Sound.values()) {
                Media media =
                        new Media(MusicPlayer.class.getResource("/sound_effects/" + sound.fileName + ".mp3").toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                instance.soundEffects.put(sound, mediaPlayer);
            }
        }).start();
    }

    public static void playSound(Sound sound) {
        if (instance.playSoundEffects) {
            double volume = instance.mediaPlayer.getVolume();
            instance.mediaPlayer.setVolume(0.2);
            instance.soundEffects.get(sound).seek(new Duration(0));
            instance.soundEffects.get(sound).play();
            instance.soundEffects.get(sound).setOnEndOfMedia(() -> instance.mediaPlayer.setVolume(volume));
        }
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

    private void initNewSong() {
        Media song = new Media(getRandomSongFile());
        mediaPlayer = new MediaPlayer(song);
        mediaPlayer.setOnEndOfMedia(this::startNewSong);
    }

    private String getRandomSongFile() {
        String[] songNames = new String[]{"bensound-epic"};
        String randomSong = songNames[Random.random.nextInt(songNames.length)];
        return getClass().getResource("/music/" + randomSong + ".mp3").toString();
    }


    public enum Sound {
        WIN("win"), LOSE("lose"), HIT_SHIP("hit_ship"), HIT_WATER("hit_water");

        private String fileName;

        Sound(String fileName) {
            this.fileName = fileName;
        }
    }
}
