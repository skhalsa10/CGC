package cgc.kioskmanager;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.messages.*;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This Class will manage the communication with all Kiosks. It will also manage the finance logic.
 *
 * The Kiosk Manager will receive a message from the Pay Kiosk that a token was purchased.
 *      1. It will then log the transaction with the transactionLogger.
 *      2. it will send a message to the CGC to generate a new Guest Token
 *
 * The Kiosk Manager May get a Message from the CGC to retrieve updated Finance information
 *      1. it should respond by sending updated FinanceInfo Message
 *
 * The Kiosk Manager May receive a Shutdown Message from the CGC
 *      1. it will need to send a Shutdown Message to all Pay Kiosk
 *      2.  after 1 is complete it will then shut down gracefully itself.
 *
 * The Kiosk Manager may receive a message from the CGC to report the Health of all the Pay Kiosks
 *      1. It will respond with a message with the current health of all Pay Kiosks
 *
 * The Kiosk Manager may receive a message that we are in Emergency Mode
 *      1. It must respond by sending all Pay Kiosks an EmergencyMode Message
 */
public class KioskManager extends Thread implements Communicator {
    //this is needed to send messages back up to the CGC
    private CGC cgc;
    private TransactionLogger transactionLogger;
    private PriorityBlockingQueue<Message>  messages;
    private boolean isRunning;
    private boolean isInEmergencyMode;
    //TODO need datastructure to keep track of active kiosks
    //TODO need a data structure that associated the Kiosk ID with its associated Health status
    private ArrayList<PayKiosk> kiosks;
    private ArrayList<Boolean> healthKiosks;


    //This function will intialize the pay kiosks and set them their position Point2D.
    private void initializePayKiosks(){

    }

    public KioskManager(CGC cgc){
        this.cgc = cgc;
        TransactionLogger = new TransactionLogger();
        messages = new PriorityBlockingQueue<>();
        this.initializePayKiosks();
        healthKiosks = new ArrayList<Boolean>(4);
        isRunning = true;
        isInEmergencyMode = false;
    }

    /**
     * This is enforced by the Communicator interface it allows classes that have a reference to the Kiosk
     * Manager to be able to send a message to it.
     * @param m
     */
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


    private void processMessage(Message m){
        
        if (m instanceof ShutDown) {
            //Send message to all the treads (Pay Kiosk).
        }
        else if (m instanceof TokenPurchasedInfo){
            //Pass to the Transaction Log.

            //Message to the GCG generate new Token
        }
        else if (m instanceof UpdatedFinanceInfo){

        }
        else if (m instanceof CGCRequestHealth){
            //This is ok?
        }
        else if( m instanceof EnterEmergencyMode){
            //Enter emergency mode.
        }
        else if( m instanceof ExitEmergencyMode){
            //Exit emergency mode.
        }
    }
}
