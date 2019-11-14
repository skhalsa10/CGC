package CGC.TRex;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import CGC.CGC;
import CGC.Locatable;
import CGC.Maintainable;
import CGC.Messages.Message;
import javafx.animation.AnimationTimer;

public class TRexMonitor extends Thread implements Maintainable, Locatable {
    // Maybe add other coordinate space (square space?) to make sure
    // that TRex doesn't go outside.
    private Point GPS;
    private CGC cgc;
    private boolean isTranquilized;
    private boolean healthStatus;
    private long lastUpdate = 0;
    private LinkedBlockingQueue<Message> messages;

    // We need to make sure that we only communicate with the BlockingQueue of CGC or
    // we will have thread issues.
    public TRexMonitor(CGC cgc) {

        startTRexTimer();
        start();
    }

    /**
     * Be in a loop and check messages, it will block
     * and wait for messages. That way, the thread is not in a busy wait.
     *
     */
    @Override
    public void run() {

    }

    /**
     * Instantiates timer and schedules timer tasks to
     * change x,y coordinates.
     */
    private void startTRexTimer() {

    }

    public void inject() {

    }

    @Override
    public synchronized boolean checkHealth() {
        return false;
    }

    /**
     * send message to CGC.
     */
    private void reportHealth(boolean healthStatus) {

    }

    @Override
    public synchronized Point getLocation() {
        return null;
    }

    private void updateLocation(Point loc) {

    }
}
