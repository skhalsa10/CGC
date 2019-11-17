package cgc.kioskmanager;

import cgc.Communicator;
import cgc.Maintainable;
import cgc.messages.Message;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * The purpose of this class is that it will simulate the behavior of a pay kiosk.
 * it will also simulate the purchasing of tickets. this will happen randomly.
 * It will also keep track of it's health.
 *
 *
 * this class will use a Timer and Timer task to generate token sales
 *
 *
 * Probable message to process
 *
 * The Pay Kiosk may receive a request to check health from the Kiosk Manager
 *      1. The kiosk will send message to Kiosk manager with the  health status
 *
 * The Pay Kiosk may receive a message to shut down.
 *      1. it must shut down gracefully
 */
public class PayKiosk extends Thread implements Communicator, Maintainable {
    private KioskManager kioskManager;
    private PriorityBlockingQueue<Message> messages;
    private int ID;
    private Timer timer;
    private TimerTask timerTask;
    private boolean healthStatus;

    public PayKiosk(KioskManager kioskManager, int ID){
        this.kioskManager = kioskManager;
        this.ID = ID;
    }

    @Override
    public void sendMessage(Message m) {
        //TODO Put Message m into the messages queue
    }

    @Override
    public void checkHealth() {
        //TODO Place a message into the Message Queue to update the Kiosk Manager with the current health
    }

    @Override
    public void run() {
        //TODO loop and wait on blocking queue until Shutdown message received.
        //TODO when a message is receive it will call processMessage(m)
    }

    private synchronized void processMessage(Message m){
        //TODO check what instance m is and take appropriate action
    }

    /**
     * use the timer and timertask to trigger purchasing the sales of tokens overtime
     */
    private void startTimer(){
        //TODO use the timer and timer task to purchase a token.
        // e
    }
}
