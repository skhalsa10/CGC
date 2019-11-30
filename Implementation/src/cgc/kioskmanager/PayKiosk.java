package cgc.kioskmanager;

import cgc.utils.Communicator;
import cgc.utils.Locatable;
import cgc.utils.Maintainable;
import cgc.utils.Entity;
import cgc.utils.messages.*;
import javafx.geometry.Point2D;

import java.util.*;

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
    private boolean isRunning;
    private boolean healthStatus;
    private Point2D location;
    private boolean isInEmergencyMode;

    
    //Ticket price .v1
    private static double adult_price = 15.00;
    private static double children_price = 8.00;
    private static double senior_price = 12.00;
    
    public PayKiosk(KioskManager kioskManager, int ID, Point2D location){
        this.kioskManager = kioskManager;
        this.ID = ID;
        entity = Entity.KIOSK;
        messages = new PriorityBlockingQueue<>();
        isRunning = true;
        healthStatus = true;
        isInEmergencyMode = false;
        this.location = location;
        timer = new Timer();
        startTimer();
        this.start();

    }

    /**
     * dumps message m into the queue for processing when there is time.
     * @param m
     */
    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }

    /**
     * This will just loop on messaging queue responding to messages
     * @version 1
     * @author Santi
     */
    @Override
    public void run() {
        //This will update the Kiosk Manager with its location upon its initial creation
        this.updateLocation();
        //loop on blocking queue until shutdown
        while(isRunning){
            try {
                Message m = messages.take();
                processMessage(m);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * This method contains the specific logic for the messages the Pay kiosk responds to
     * @param m is the message to respond to
     */
    private synchronized void processMessage(Message m){
        //This handels the shutdown message
        if (m instanceof ShutDown){
            isRunning = false;
            timer.cancel();
        }
        else if (m instanceof CGCRequestHealth){
            //Make message to the CGC.
            Message updatedHealth = new UpdatedHealth(entity, ID, healthStatus);
            kioskManager.sendMessage(updatedHealth);
        }
        else if (m instanceof EnterEmergencyMode){
            if(!isInEmergencyMode) {
                isInEmergencyMode = true;
                timer.cancel();
            }
        }
        else if (m instanceof ExitEmergencyMode) {
            if (isInEmergencyMode){
                isInEmergencyMode = false;
                restartTimer();
            }
        }
        else if(m instanceof BuyTicket){
            buyTicket();
        }
        else if(m instanceof CGCRequestLocation){
            Message location = new UpdatedLocation(entity, ID, this.location);
            kioskManager.sendMessage(location);
        }
        else{
            System.out.println("Pay kiosk cannot handle Message: " + m);
        }

    }

    /**
     * this function will just randomize what tickets end up
     * getting sold whether it be a child or an adult or senior
     * it then sends a token purchase request to the kiosk Manager to handle the sale
     */
    public void buyTicket(){
        //Random buy (children, adult or senior)
        TicketPrice[] tickets = TicketPrice.values();
        Random random = new Random();
        TicketPrice price = tickets[random.nextInt(tickets.length)];

        double priceTicket = 0.0;

        if(price == TicketPrice.ADULT){
            priceTicket = adult_price;
        }else if(price == TicketPrice.CHILDREN){
            priceTicket = children_price;
        }else if (price == TicketPrice.SENIOR) {
            priceTicket = senior_price;
        }

        //Sends to the KioskManager that a ticket has been purchased.
        Date purchasedDate = new Date();
        Message tokenPurchased = new TokenPurchasedInfo(priceTicket, purchasedDate, location, price);
        this.kioskManager.sendMessage(tokenPurchased);
    }

    /**
     * this will just send the Kiosk manager the current location of this kiosk
     */
    private void updateLocation(){
        Message location = new UpdatedLocation(entity, ID, this.location);
        kioskManager.sendMessage(location);
    }

    private void restartTimer() {
        this.timer = new Timer();
        startTimer();
    }

    /**
     * use the timer and timertask to trigger purchasing the sales of tokens overtime
     */
    private void startTimer(){
        TimerTask task = new TimerTask() {
            public void run() {
                BuyTicket handleBuyTicket = new BuyTicket();
                messages.put(handleBuyTicket);
            }
        };
        // schedules the buy of a token after 1 minute.
        this.timer.schedule(task, 100, 30000);
    }
}
