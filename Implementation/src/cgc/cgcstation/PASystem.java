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
    AudioInputStream audioIn;
    Clip clip;

    public PASystem(CGCStation cgcStation) {


        this.cgcStation = cgcStation;

        theme = Paths.get("./src/resources/emergency.wav").toFile();
        try {
            audioIn = AudioSystem.getAudioInputStream(theme);
            clip = AudioSystem.getClip();
            clip.open(audioIn);

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

//    @Override
//    public void sendMessage(Message m) {
//
//    }
//
//    @Override
//    public void checkHealth() {
//
//    }
//
//    @Override
//    public void run() {
//
//    }
}
