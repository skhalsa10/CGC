package cgc.cgcstation;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.messages.*;
import javafx.geometry.Point2D;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * THE CGCSTATION will maintain its own health and it must exist.
 * but it will never need to report outside the station
 *
 *
 * the CGCStation will receive message from CGCGui to ShutdownCGC
 *  1. The CGCStation will sendMessage to CGC to ShutdownCGC
 *  DONE
 *
 * The CGCStation will receive Shutdown Message from CGC
 *  1. shutting down GUI
 *  2. Shutting down self
 *  DONE
 *
 * the CGCStation will receive message from CGC EnterEmergencyMode
 *  1. send EnterEmergencyMode to GUI and PASystem
 *  2. place itself in emergency mode
 *  DONE
 *
 * the CGCStation will receive message from GUI ExitEmergencyMode
 *  1. send ExitEmergencyMode to CGC and PASystem
 *  2. itself will exit emergency mode
 *  DONE
 *
 * The CGCStation will receive message from CGC with updated Finance Info
 *  1. Forward message to GUI
 *  DONE
 *
 * CGCStaion will receive message from CGC with Location w/ tokenID
 *  1. Forward message to GUI
 *  DONE
 *
 * CGCStation will send message to CGC for updated Health info
 * DONE
 *
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
            cgc.sendMessage(m);
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
        else{
            System.out.println("cant candle message: " + m);
        }

    }
}
