package CGC.TRex;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;


import CGC.CGC;
import CGC.Communicator;
import CGC.Locatable;
import CGC.Maintainable;
import CGC.Messages.Message;

/**
 * the T-Rex Monitor class simulates how the real T-Rex monitor would be. This class
 * Will simulate the health it will nto read the biometrics exactly but will keep track of that as a property.
 * It will also simulate the movement of the T-Rex. It is up to the implementor to decide how the T-rex will wonder
 * around the enclosure. The T-REX will NOT leave the enclosure! this can be a feature added AFTER the fact if there is
 * time. There is a Timer and TimerTask to be used for changing data over time, like the x and y coordinates.
 * The timer and timertask might place a message in the blocking queue to perform an action. the main threads run will loop using the
 * blocking queue this will make the thread wait efficiently without using a busy wait.
 */
public class TRexMonitor extends Thread implements Maintainable, Locatable, Communicator {
    // Maybe add other coordinate space (square space? ... or circle if someone wants to do
    // circle math) to make sure
    // that TRex doesn't go outside.
    private Point GPS;
    private CGC cgc;
    private boolean isTranquilized;
    private boolean healthStatus;
    private long lastUpdate = 0;
    private LinkedBlockingQueue<Message> messages;


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
     * change x,y coordinates, and anything else that needs to happen over time
     */
    private void startTRexTimer() {

    }

    /**
     *  This will inject the T-Rex with the tranq if there is one  available.
     */
    private void inject() {

    }

    @Override
    public synchronized void checkHealth() {
        //TODO place a Message inside of the Trex blocking queue that tells it to update the CGC with
        // Health info
    }

    /**
     * send message to CGC.
     */
    private void reportHealth(boolean healthStatus) {
        //TODO Send a message to the CGC with health Status
    }

    /**
     * place message inside Trex queue qich triggers an update to the CGC when processed
     */
    @Override
    public synchronized void getLocation() {
        //TODO place a message in the T-Rex message queue to trigger a a location sync to the CGC
    }

    /**
     * send message to CGC.
     */
    private void updateLocation(Point loc) {
        //TODO send a message to the CGC with updated location
    }

    /**
     * this will take a message and store it in the blocking queue to be processed later.
     * @param m
     */
    @Override
    public synchronized void sendMessage(Message m) {
        //TODO Store this message in the queue for processing later
    }
}
