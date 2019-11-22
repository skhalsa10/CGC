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
 * The CGC may Receive a message from the CGCStation to shutdown application
 *  1. Send shutdown Message ServeillanceSystem, CGCStation, KioskManager, VehicleManager, tokenManager DONE!
 *
 * The CGC may receive Message from ServeillanceSystem that electrical fence is down
 *  1. Will blast out EmergencyMode triggered to ServeillanceSystem, CGCStation, KioskManager, VehicleManager, tokenManager DONE!
 *
 * the CGCStation may request for finance info, then the CGC may send a message to KioskManager requesting Finacne info DONE!
 *
 * The CGC may receive message from Kiosk Manager with Updated Finance info DONE!
 *  1. this message will be forwarded to the CGCStation
 *
 * The CGC May receive a message from KioskManager requesting a new GuestToken to be Generated DONE!
 *  1. this message will be forwarded to the TokenManager
 *
 * The CGC may receive a message from tokenManager a Location with the TokenID DONE!
 *  1. will forward information to the CGCStation
 *
 * The CGC may send message to ServeillanceSystem, CGCStation, KioskManager, VehicleManager, tokenManager to EnterEmergencyMode DONE!
 *
 * The CGC may send message to ServeillanceSystem, KioskManager, VehicleManager, tokenManager to ExitEmergencyMode DONE!
 *
 * The CGC may send message to ServeillanceSystem, KioskManager, VehicleManager, tokenManager to report health. DONE!
 *
 * The CGC May send message to ServeillanceSystem TokenManager, and VehicleManager to get updated locations DONE!
 *
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

    private void processMessage(Message m){
        if (m instanceof ShutDown) {
            this.vehicleManager.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
            this.surveillanceSystem.sendMessage(m);
            this.station.sendMessage(m);
            this.run = false;
        }
        if (m instanceof ElectricFenceDown) {
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
        if (m instanceof ExitEmergencyMode) {
            this.emergencyMode = false;

            this.vehicleManager.sendMessage(m);
            this.surveillanceSystem.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
        }
        // when cgcstation request finance info, cgc will ask from kioskManager.
        if (m instanceof RequestFinanceInfo) {
            this.kioskManager.sendMessage(m);
        }
        // kiosk manager then returns back the updated finance info.
        if (m instanceof UpdatedFinanceInfo) {
            // forward it to cgcstation.
            this.station.sendMessage(m);
        }
        if (m instanceof RequestToken) {
            // guest token is requested, forward it to tokenManager.
            this.tokenManager.sendMessage(m);
        }
        if (m instanceof TokenInfo) {
            // forward token location, id to cgcstation.
            this.station.sendMessage(m);
        }
        // When cgcstation request health or location.
        if (m instanceof CGCRequestHealth) {
            // sendMessage to all the managers to report their entities health.
            this.surveillanceSystem.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
            this.vehicleManager.sendMessage(m);
        }
        if (m instanceof CGCRequestLocation) {
            this.surveillanceSystem.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
            this.vehicleManager.sendMessage(m);
        }
        // no matter whether cgcstation requests health or location, the cgc will forward all
        // the health and location info either way.
        if (m instanceof UpdatedHealth) {
            this.station.sendMessage(m);
        }
        if (m instanceof UpdatedLocation) {
            this.station.sendMessage(m);
        }
    }
}
