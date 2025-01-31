package cgc;

import cgc.cgcstation.CGCStation;
import cgc.kioskmanager.KioskManager;

import cgc.surveillancesystem.SurveillanceSystem;
import cgc.utils.*;
import cgc.tokenmanager.TokenManager;
import cgc.utils.messages.*;
import cgc.vehiclemanager.VehicleManager;
import javafx.stage.Stage;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * The CGC class will act as a Message  logic switch It will receive messages
 * from each of the classes that compose it. In this case it will receive a messages from
 * the CGCStation, KioskManager, TokenManager,and VehicleManager. It will also be able to send
 * messages to these classes ONLY.
 *
 * @author Anas
 * @version 1
 *
 * Skeleton were written by Anas and Siri
 */
public class CGC extends Thread implements Communicator {

    private PriorityBlockingQueue<Message> messages;
    private CGCStation station;
    private KioskManager kioskManager;
    private VehicleManager vehicleManager;
    private TokenManager tokenManager;
    private SurveillanceSystem surveillanceSystem;
    private boolean run;
    private boolean emergencyMode;

    public CGC(Stage primaryStage){
        this.station = new CGCStation(primaryStage,this);
        this.kioskManager = new KioskManager(this);
        this.vehicleManager = new VehicleManager(this);
        this.tokenManager = new TokenManager(this);
        this.messages = new PriorityBlockingQueue<>();
        this.surveillanceSystem = new SurveillanceSystem(this);
        this.run = true;
        this.emergencyMode = false;
        start();
    }


    @Override
    public void sendMessage(Message m) {
        this.messages.put(m);
    }

    /**
     * This overrides the run method from extending thread. This should loop over the Message Queue
     * It will wait on the Messages (put and take block and wait until the thread is free) and
     * not be stuck in a busy wait which is not good.
     * It should never exit this loop unless it received the ShutDown Message in which case the boolean value run = false.
     */
    @Override
    public void run() {
        while (run) {
            try {
                Message m = this.messages.take();
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void processMessage(Message m){
        if (m instanceof ShutDown) {
            this.vehicleManager.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
            this.surveillanceSystem.sendMessage(m);
            this.station.sendMessage(m);
            this.run = false;
            System.out.println("CGC is shutting down.");
        }
        else if (m instanceof ElectricFenceDown) {
            if (!emergencyMode) {
                Message emergencyModeTriggered = new EnterEmergencyMode();

                this.vehicleManager.sendMessage(emergencyModeTriggered);
                this.kioskManager.sendMessage(emergencyModeTriggered);
                this.tokenManager.sendMessage(emergencyModeTriggered);
                this.surveillanceSystem.sendMessage(emergencyModeTriggered);
                this.station.sendMessage(emergencyModeTriggered);
                this.emergencyMode = true;
            }
        }
        else if (m instanceof ExitEmergencyMode) {
            this.emergencyMode = false;

            this.vehicleManager.sendMessage(m);
            this.surveillanceSystem.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
        }
        // when cgcstation request finance info, cgc will ask from kioskManager.
        else if (m instanceof RequestFinanceInfo) {
            this.kioskManager.sendMessage(m);
        }
        // kiosk manager then returns back the updated finance info.
        else if (m instanceof UpdatedFinanceInfo) {
            // forward it to cgcstation.
            this.station.sendMessage(m);
        }
        else if (m instanceof RequestToken) {
            // guest token is requested, forward it to tokenManager.
            this.tokenManager.sendMessage(m);
        }
        // When cgcstation request health or location.
        else if (m instanceof CGCRequestHealth) {
            // sendMessage to all the managers to report their entities health.
            this.surveillanceSystem.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
            this.vehicleManager.sendMessage(m);
        }
        else if (m instanceof CGCRequestLocation) {
            this.surveillanceSystem.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
            this.vehicleManager.sendMessage(m);
        }
        // no matter whether cgcstation requests health or location, the cgc will forward all
        // the health and location info either way.
        else if (m instanceof UpdatedHealth) {
            this.station.sendMessage(m);
        }
        else if (m instanceof UpdatedLocation) {
            this.station.sendMessage(m);
        }
        else if (m instanceof UpdatedDrivingLocation) {
            this.tokenManager.sendMessage(m);
            this.station.sendMessage(m);
        }
        else if (m instanceof TourCarArrivedAtDropOff) {
            this.tokenManager.sendMessage(m);
        }
        else if (m instanceof DeactivateToken) {
            this.station.sendMessage(m);
        }
        else if(m instanceof SaleLog){
            station.sendMessage(m);
        }
        else if (m instanceof TokenReadyToLeave) {
            this.vehicleManager.sendMessage(m);
        }
        else{
            System.out.println("CGC cannot process Message: " + m);
        }
    }
}
