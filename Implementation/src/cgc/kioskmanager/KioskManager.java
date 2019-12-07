package cgc.kioskmanager;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;

import javafx.geometry.Point2D;

import java.util.Date;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This Class will manage the communication with all Kiosks. It will also manage the finance logic.
 *
 * It will  route messages to the appropriate kiosks or write to the transaction logger.
 *
 * @author Santi
 * @version 1
 */
public class KioskManager extends Thread implements Communicator {
    //this is needed to send messages back up to the CGC
    private CGC cgc;
    private TransactionLogger transactionLogger;
    private PriorityBlockingQueue<Message>  messages;
    private boolean isRunning;
    private boolean isInEmergencyMode;
    private ArrayList<PayKiosk> kiosks;
    private ArrayList<Boolean> healthKiosks;


    //This function will intialize the pay kiosks and set them their position Point2D.
    private void initializePayKiosks(){
        kiosks = new ArrayList<PayKiosk>(4);

        //Method that calculates the positon of the Kiosks.
        double x = MapInfo.UPPER_LEFT_SOUTH_BULDING.getX() + MapInfo.SOUTHBUILDING_WIDTH/8;
        double y = MapInfo.MAP_HEIGHT - MapInfo.SOUTHBUILDING_HEIGHT/2;

        Point2D point = new Point2D(x,y);

        for(int i=0; i < 4; i++) {
            kiosks.add(new PayKiosk(this, i, point));
            point = point.add(MapInfo.SOUTHBUILDING_WIDTH/4, 0);
        }

    }

    public KioskManager(CGC cgc){
        this.cgc = cgc;
        transactionLogger = new TransactionLogger();
        messages = new PriorityBlockingQueue<>();
        healthKiosks = new ArrayList<Boolean>(4);
        isRunning = true;
        isInEmergencyMode = false;
        this.start();
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
        //Need to be here because we need first to start KioksManager before the PayKiosk send the message.
        this.initializePayKiosks();
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
        
        if (m instanceof ShutDown) {
            //Shutdown itself.
            isRunning = false;

            //Send message to all the threads (Pay Kiosk).
            for(int i=0; i <4; i++)
                kiosks.get(i).sendMessage(m);
        }
        else if (m instanceof TokenPurchasedInfo){
            //Pass to the Transaction Log.
            TokenPurchasedInfo m2 = (TokenPurchasedInfo)m;
            double amount = m2.getAmount();
            Date purchasedDate =m2.getPurchasedDate();
            TicketPrice ticketType = m2.getTypeTicket();
            transactionLogger.registerSale(amount, purchasedDate, ticketType);

            //Message to the GCG generate new Token
            Message generateNewToken = new RequestToken(((TokenPurchasedInfo) m).getLocation());
            //System.out.println("KIOSK MANAGER REQUEST LOCATION: "+((TokenPurchasedInfo) m).getLocation());
            cgc.sendMessage(generateNewToken);
            // this message is needed for the GUI log
            cgc.sendMessage(new SaleLog(m2.getAmount(),m2.getPurchasedDate(),m2.getTypeTicket()));
        }
        //NEED the Benefits like per month or total or a particular month. ???
        else if (m instanceof RequestFinanceInfo){
            //Get the Financial Information
            double total_benefits = transactionLogger.getTotalBenefits();
            ArrayList<Double> mensual_benefits = transactionLogger.getMonthsBenefits();
            ArrayList<Integer> typeTicketsSold = transactionLogger.getTypeTicketsSold();

            //Message about Finances
            Message financeInfo = new UpdatedFinanceInfo(total_benefits, mensual_benefits, typeTicketsSold);
            cgc.sendMessage(financeInfo);
        }
        else if (m instanceof CGCRequestHealth){
            //Request the Pay Kiosks their health
            for(int i=0; i <4; i++) {
                kiosks.get(i).sendMessage(m);
            }
        }
        else if (m instanceof UpdatedHealth){
            //forward to CGC
            cgc.sendMessage(m);
        }
        else if( m instanceof EnterEmergencyMode){
            //Enter emergency mode if needed
            if(!isInEmergencyMode) {
                isInEmergencyMode = true;

                //Notify the pay kiosks
                for (int i = 0; i < 4; i++) {
                    kiosks.get(i).sendMessage(m);
                }
            }
        }
        else if( m instanceof ExitEmergencyMode){
            //Exit emergency mode if we are in emergency mode
            if(isInEmergencyMode) {
                isInEmergencyMode = false;

                //Notify the pay kiosks
                for (int i = 0; i < 4; i++) {
                    kiosks.get(i).sendMessage(m);
                }
            }
        }
        else if(m instanceof CGCRequestLocation){
            //Notify the pay kiosks to send their location
            for(int i=0; i <4; i++) {
                kiosks.get(i).sendMessage(m);
            }
        }
        else if(m instanceof UpdatedLocation){
            cgc.sendMessage(m);
        }
        else{
            System.out.println("Kiosk Manager cannot handle message: " + m);
        }
    }
}
