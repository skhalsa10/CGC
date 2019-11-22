package cgc.cgcstation;

import cgc.Communicator;
import cgc.Maintainable;
import cgc.messages.Message;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.PriorityBlockingQueue;

import static javax.sound.sampled.Clip.LOOP_CONTINUOUSLY;

/**
 * This will play sounds on the speaker
 */
public class PASystem  {

    private PriorityBlockingQueue<Message> messages;
    private CGCStation cgcStation;
    //TODO Mode enum
    File theme;
    File emergency;
    AudioInputStream themeAudio;
    AudioInputStream emergencyAudio;
    Clip clip;

    public PASystem(CGCStation cgcStation) {


        this.cgcStation = cgcStation;

        theme = Paths.get("./src/resources/theme.wav").toFile();
        emergency = Paths.get("./src/resources/emergency.wav").toFile();
        try {
            themeAudio = AudioSystem.getAudioInputStream(theme);
            emergencyAudio = AudioSystem.getAudioInputStream(emergency);
            clip = AudioSystem.getClip();
            clip.open(themeAudio);



        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        clip.loop(LOOP_CONTINUOUSLY);
        clip.start();
    }

    public void enterEmergency(){
        clip.stop();
        setAudio(emergencyAudio);
        clip.start();
    }

    public void exitEmergency(){
        clip.stop();
        setAudio(themeAudio);
        clip.start();

    }

    private void setAudio(AudioInputStream themeAudio) {
        clip.close();
        try {
            clip.open(themeAudio);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutDown(){
        clip.close();
        try {
            emergencyAudio.close();
            themeAudio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
