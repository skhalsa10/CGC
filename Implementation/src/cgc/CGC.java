package cgc;

import cgc.cgcstation.CGCStation;
import cgc.kioskmanager.KioskManager;
import cgc.messages.Message;
import cgc.tokenmanager.TokenManager;
import cgc.vehiclemanager.VehicleManager;
import javafx.stage.Stage;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * The CGC class will act as a Message  logic switch It will receive messages
 * from each of the classes that compose it. In this case it will receive a messages from
 * the CGCStation, KioskManager, TokenManager,and VehicleManager. It will also be able to send
 * messages to these classes ONLY.
 */
public class CGC extends Thread implements Communicator {

    private PriorityBlockingQueue<Message> messages;
    private CGCStation station;
    private KioskManager kioskManager;
    private VehicleManager vehicleManager;
    private TokenManager tokenManager;

    public CGC(Stage primaryStage){

    }


    @Override
    public void sendMessage(Message m) {
        //Todo this should place a message in the CGC Message queue to be processed later
    }

    /**
     * This overrides the run method from extending thread. This should loop over the Message Queue
     * It will wait on the Messages and not be stuck in a busy wait which is not good.
     * It should never exit this loop unless it received the ShutDown Message.
     */
    @Override
    public void run() {
        //TODO loop on the blocking queue until Shutdown message
    }

    private void processMessage(Message m){
        //TODO check what instance m is and take appropriate action
    }
}
