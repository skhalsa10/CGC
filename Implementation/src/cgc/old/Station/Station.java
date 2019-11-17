package cgc.old.Station;

import cgc.Communicator;
import cgc.messages.Message;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * This station will be the GUI for this application
 * This class will actually use multiple threads.
 * 1. will be the animation timer thread this will refresh the GUI at 60 frames per second
 *    Due to the need to animate figures on the screen over time we need to have this.
 * 2. the second thread  needs to be dedicated to processing messages. it will wait on the blocking
 */
public class Station extends AnimationTimer implements Runnable, Communicator {
    long lastUpdate = 0;
    PriorityBlockingQueue<Message> messages;

    public Station(Stage primaryStage) {

        //TODO since this is a GUI the constructor will be pretty big while it pieces togethor  all
        // the components.

        Thread messageThread = new Thread(this);
        messageThread.start();

    }

    private void processMessage(){
        //TODO
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

    @Override
    public void run() {

    }

    @Override
    public void sendMessage(Message m) {

    }
}
