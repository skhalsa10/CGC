package cgc.cgcstation;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.messages.*;
import javafx.stage.Stage;


import java.util.concurrent.PriorityBlockingQueue;

/**
 * The CGCStation will act as a logic switch for the Station related classes. this includes the
 * GUI and the PASystem
 *
 * @author siri
 * @version 1
 *
 */
public class CGCStation extends Thread implements Communicator {
    private CGC cgc;
    private PriorityBlockingQueue<Message> messages;
    private CGCGUI cgcgui;
    private PASystem paSystem;
    private boolean isRunning;
    private boolean isInEmergencyMode;

    public CGCStation(Stage primaryStage, CGC cgc) {
        this.cgc = cgc;
        paSystem = new PASystem();
        cgcgui = new CGCGUI(primaryStage,this);
        isRunning = true;
        isInEmergencyMode = false;
        messages = new PriorityBlockingQueue<>();
        this.start();
    }

    /**
     * This will just pass the message to the messaging queue
     * @param m
     */
    @Override
    public synchronized void sendMessage(Message m) {
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

    private synchronized void processMessage(Message m){
        //this handles the shutdown message
        if(m instanceof ShutDown){
            paSystem.shutDown();
            cgcgui.sendMessage(m);
            isRunning = false;
            System.out.println("CGCSTATION is shutting down");
        }
        else if(m instanceof UpdatedFinanceInfo){
            cgcgui.sendMessage(m);
        }
        else if(m instanceof UpdatedHealth){
            cgcgui.sendMessage(m);
        }
        else if(m instanceof UpdatedLocation){
            cgcgui.sendMessage(m);
        }
        else if(m instanceof CGCRequestHealth){
            System.out.println("CGC STATION WILL BE FORWARDING request for health");
            cgc.sendMessage(new CGCRequestHealth());
        }
        else if(m instanceof CGCRequestLocation){
            cgc.sendMessage(m);
        }
        else if(m instanceof ElectricFenceDown){
            cgc.sendMessage(m);
        }
        else if(m instanceof EnterEmergencyMode){
            if(!isInEmergencyMode) {
                paSystem.enterEmergency();
                cgcgui.sendMessage(m);
                isInEmergencyMode = true;
            }

        }
        else if(m instanceof ExitEmergencyMode){
            if(isInEmergencyMode) {
                paSystem.exitEmergency();
                cgc.sendMessage(m);
                isInEmergencyMode = false;
            }

        }
        else if(m instanceof RequestFinanceInfo){
            cgc.sendMessage(m);
        }
        else if(m instanceof SaleLog){
            cgcgui.sendMessage(m);
        }
        else if(m instanceof UpdatedDrivingLocation){
            cgcgui.sendMessage(m);
        }
        else if(m instanceof DeactivateToken){
            cgcgui.sendMessage(m);
        }
        else{
            System.out.println("Station cant candle message: " + m);
        }

    }
}
