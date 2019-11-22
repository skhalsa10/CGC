package cgc.cgcstation;

import cgc.utils.Communicator;
import cgc.utils.messages.Message;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

import java.util.concurrent.PriorityBlockingQueue;

public class CGCGUI extends AnimationTimer implements Runnable, Communicator {

    private long lastUpdate = 0;
    private PriorityBlockingQueue<Message> messages;
    private Thread messageThread;
    private boolean isRunning;

    //GUI stuff
    private Stage stage;


    public CGCGUI(Stage primaryStage, CGCStation cgcStation) {

        isRunning = true;
        messageThread = new Thread(this);
        messageThread.start();

        //GUI
        this.stage = primaryStage;
        stage.setTitle("Cretaceous Gardens Controller");

        this.start();
    }


    private void processMessage(Message m){
        //TODO
    }


    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }

    @Override
    public void run() {
        while(isRunning){
            try {
                Message m = messages.take();
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * this is used to paint the gui on the screen. it is needed to draw the animation.
     * @param now
     */
    @Override
    public void handle(long now) {
        //there are 1000 miliseconds in a second. if we divide this by 60 there
        // are 16.666667 ms between frame draws
        if (now - lastUpdate >= 16_667_000) {

            // helped to stabalize the rendor time
            lastUpdate = now;
        }
    }
}
