package cgc.kioskmanager;

import cgc.CGC;
import cgc.Communicator;
import cgc.messages.Message;

/**
 * This Class will manage the communication with all Kiosks. It will also manage the finance logic.
 */
public class KioskManager extends Thread implements Communicator {
    private CGC cgc;

    public KioskManager(CGC cgc){
        this.cgc = cgc;
    }

    /**
     * This is enforced by the Communicator interface it allows classes that have a reference to the Kiosk
     * Manager to be able to send a message to it.
     * @param m
     */
    @Override
    public void sendMessage(Message m) {
        //TODO place message into Priority blocking queue
    }

    @Override
    public void run() {
        //TODO loop and wait on blocking queue until Shutdown message received.
    }


    private void processMessage(Message m){
        //TODO check what instance m is and take appropriate action
    }
}
