package cgc.cgcstation;

import cgc.Communicator;
import cgc.messages.Message;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

import java.util.concurrent.PriorityBlockingQueue;

public class CGCGUI extends AnimationTimer implements Runnable, Communicator {
    long lastUpdate = 0;
    PriorityBlockingQueue<Message> messages;

    public CGCGUI(Stage primaryStage, CGCStation cgcStation) {
        Thread messageThread = new Thread(this);
        messageThread.start();
    }

    @Override
    public void handle(long now) {
        //there are 1000 miliseconds in a second. if we divide this by 60 there
        // are 16.666667 ms between frame draws
        if (now - lastUpdate >= 16_667_000) {



            // helped to stabalize the rendor time
            lastUpdate = now;
        }
    }

    private void processMessage(Message m){
        //TODO
    }


    @Override
    public void sendMessage(Message m) {

    }

    @Override
    public void run() {

    }
}
