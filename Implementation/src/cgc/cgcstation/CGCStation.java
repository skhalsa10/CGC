package cgc.cgcstation;

import cgc.CGC;
import cgc.Communicator;
import cgc.messages.Message;
import javafx.stage.Stage;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * THE CGCSTATION will maintain its own health and it must exist.
 * but it will never need to report outside the station
 *
 *
 * the CGCStation will receive message from CGCGui to ShutdownCGC
 *  1. The CGCStation will sendMessage to CGC to ShutdownCGC
 *
 * The CGCStation will receive Shutdown Message from CGC
 *  1. shutting down GUI
 *  2. Shutting down self
 *
 * the CGCStation will receive message from CGC EnterEmergencyMode
 *  1. send EnterEmergencyMode to GUI and PASystem
 *  2. place itself in emergency mode
 *
 * the CGCStation will receive message from CGC ExitEmergencyMode
 *  1. send ExitEmergencyMode to GUI and PASystem
 *  2. itself will exit emergency mode
 *
 * The CGCStation will receive message from CGC with updated Finance Info
 *  1. Forward message to GUI
 *
 * CGCStaion will receive message from CGC with Location w/ tokenID
 *  1. Forward message to GUI
 *
 * CGCStation will send message to CGC for updated Health info
 *
 * CGCStation will receive message from CGC with updated health of all devices
 *  1. forward message to gui to display.
 *
 */
public class CGCStation extends Thread implements Communicator {
    private CGC cgc;
    private PriorityBlockingQueue<Message> messages;
    private CGCGUI cgcgui;
    private PASystem paSystem;

    public CGCStation(Stage primaryStage, CGC cgc) {
        paSystem = new PASystem(this);
    }

    @Override
    public void sendMessage(Message m) {

    }

    @Override
    public void run() {

    }

    private void processMessage(Message m){

    }
}
