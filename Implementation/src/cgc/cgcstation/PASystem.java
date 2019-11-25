package cgc.cgcstation;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static javax.sound.sampled.Clip.LOOP_CONTINUOUSLY;

/**
 * This will play sounds on the speaker.
 *
 * This class is extremeley simple when it is constructed it
 * begins playing the theme music for the park. It can enter Emergency mode.
 *
 * the effect here is to change the music to an emergency protocol
 * with instructions oh what the guests should do.
 *
 */
public class PASystem  {


    private boolean isInEmergencyMode;
    private File theme;
    private File emergency;
    private Clip clip;


    public PASystem() {

        this.isInEmergencyMode = false;
        //load the wav files used for regular and emergency mode
        theme = Paths.get("./src/resources/theme.wav").toFile();
        emergency = Paths.get("./src/resources/emergency.wav").toFile();
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(theme));
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

    /**
     * swap the mode to emergency
     */
    public void enterEmergency(){
        if(!isInEmergencyMode) {
            clip.stop();
            setAudio(emergency);
            clip.loop(LOOP_CONTINUOUSLY);
            clip.start();
            isInEmergencyMode = true;
        }
    }

    /**
     * lets put it back into normal mode dude
     */
    public void exitEmergency(){
        if(isInEmergencyMode) {
            clip.stop();
            setAudio(theme);
            clip.loop(LOOP_CONTINUOUSLY);
            clip.start();
            isInEmergencyMode = false;
        }

    }

    /**
     * this will load the input audio stream into the Clip that is being played
     * @param audio this is the audio stream to play :)
     */
    private void setAudio(File audio) {
        clip.close();
        try {
            clip.open(AudioSystem.getAudioInputStream(audio));
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    /**
     * we need to close the files that were opened d
     */
    public void shutDown(){
        clip.close();

    }
}
