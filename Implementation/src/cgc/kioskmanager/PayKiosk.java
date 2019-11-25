package cgc.kioskmanager;

import cgc.utils.Communicator;
import cgc.utils.Locatable;
import cgc.utils.Maintainable;
import cgc.utils.Entity;
import cgc.utils.messages.*;

import java.util.Date;
import java.time.LocalTime;
import java.util.Calendar;

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
 *
 * The Pay Kiosk MUST send message to the Kiosk Manager that a token was purchased from this kiosk
 */
public class PayKiosk extends Thread implements Communicator, Maintainable, Locatable {
    private KioskManager kioskManager;
    private PriorityBlockingQueue<Message> messages;
    private int ID;
    private Entity entity;
    private Timer timer;
    private TimerTask timerTask;
    private boolean isRunning;
    private boolean healthStatus;
    private boolean isInEmergencyMode;
    
    //Ticket price .v1
    private static double price = 15.00;
    
    public PayKiosk(KioskManager kioskManager, int ID){
        this.kioskManager = kioskManager;
        this.ID = ID;
        entity = Entity.KIOSK;
        messages = new PriorityBlockingQueue<>();
        isRunning = true;
        healthStatus = true;
        isInEmergencyMode = false;
        this.start();
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
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private synchronized void processMessage(Message m){
        //This handels the shutdown message
        if (m instanceof ShutDown){
            isRunning = false;
        }
        else if (m instanceof CGCRequestHealth){
            //Make message to the CGC.
            Message updatedHealth = new UpdatedHealth(entity, ID, healthStatus);
            kioskManager.sendMessage(m);
        }
        else if (m instanceof EnterEmergencyMode){
            isInEmergencyMode = true;
        }
        else if (m instanceof ExitEmergencyMode){
            isInEmergencyMode = false;
        }
    }

    public void buyTicket(){
        //Sends to the KioskManager that a ticket has been purchased.
        Date purchasedDate = new Date();
        Message tokenPurchased = new TokenPurchasedInfo(price, purchasedDate);
        this.kioskManager.sendMessage(tokenPurchased);
    }

    /**
     * use the timer and timertask to trigger purchasing the sales of tokens overtime
     */
    private void startTimer(){
        //TODO use the timer and timer task to purchase a token.

        
    }
}
