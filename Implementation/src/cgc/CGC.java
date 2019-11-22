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
 *  1. Send shutdown Message ServeillanceSystem, CGCStation, KioskManager, VehicleManager, tokenManager
 *
 * The CGC may receive Message from ServeillanceSystem that electrical fence is down
 *  1. Will blast out EmergencyMode triggered to ServeillanceSystem, CGCStation, KioskManager, VehicleManager, tokenManager
 *
 * the CGC may send a message to KioskManager requesting updated Finacne info
 *
 * The CGC may receive message from Kiosk Manager with Updated Finance info
 *  1. this message will be forwarded to the CGCStation
 *
 * The CGC May receive a message from KioskManager requesting a new GuestToken to be Generated
 *  1. this message will be forwarded to the TokenManager
 *
 * The CGC may receive a message from tokenManager a Location with the TokenID
 *  1. will forward information to the CGCStation
 *
 * The CGC may send message to ServeillanceSystem, CGCStation, KioskManager, VehicleManager, tokenManager to EnterEmergencyMode
 *
 * The CGC may send message to ServeillanceSystem, CGCStation, KioskManager, VehicleManager, tokenManager to ExitEmergencyMode
 *
 * The CGC may send message to ServeillanceSystem, KioskManager, VehicleManager, tokenManager to report health.
 *
 * The CGC May send message to ServeillanceSystem TokenManager, and VehicleManager to get updated locations
 *
 */
public class CGC extends Thread implements Communicator {

    private PriorityBlockingQueue<Message> messages;
    private CGCStation station;
    private KioskManager kioskManager;
    private VehicleManager vehicleManager;
    private TokenManager tokenManager;
    private SurveillanceSystem surveillanceSystem;

    public CGC(Stage primaryStage){
        this.station = new CGCStation(primaryStage,this);
        this.kioskManager = new KioskManager(this);
        this.vehicleManager = new VehicleManager(this);
        this.tokenManager = new TokenManager(this);
        this.messages = new PriorityBlockingQueue<>();
        this.surveillanceSystem = new SurveillanceSystem(this);
        start();
    }


    @Override
    public void sendMessage(Message m) {
        //Todo this should place a message in the CGC Message queue to be processed later
        this.messages.add(m);
    }

    /**
     * This overrides the run method from extending thread. This should loop over the Message Queue
     * It will wait on the Messages and not be stuck in a busy wait which is not good.
     * It should never exit this loop unless it received the ShutDown Message.
     */
    @Override
    public void run() {
        //TODO loop on the blocking queue until Shutdown message.
    }

    private void processMessage(Message m){
        //TODO check what instance m is and take appropriate action
        if (m instanceof ShutDown) {
            this.vehicleManager.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
            this.surveillanceSystem.sendMessage(m);
            this.station.sendMessage(m);
        }
        if (m instanceof ElectricFenceDown) {
            Message emergencyModeTriggered = new EnterEmergencyMode();

            this.vehicleManager.sendMessage(emergencyModeTriggered);
            this.kioskManager.sendMessage(emergencyModeTriggered);
            this.tokenManager.sendMessage(emergencyModeTriggered);
            this.surveillanceSystem.sendMessage(emergencyModeTriggered);
            this.station.sendMessage(emergencyModeTriggered);
        }
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
        if (m instanceof CGCRequestHealth) {
            // the view health button was pressed on CGCStationGUI.
            // sendMessage to all the managers to report their health.
            this.surveillanceSystem.sendMessage(m);
            this.kioskManager.sendMessage(m);
            this.tokenManager.sendMessage(m);
            this.vehicleManager.sendMessage(m);
        }
    }
}
