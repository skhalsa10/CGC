package cgc.SurveillanceSystem;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.messages.Message;

import java.util.concurrent.PriorityBlockingQueue;

/**
 *  This Class will manage the communication with TRex Monitor, Electric Fence, and DVR.
 *
 * The SurveillanceSystem may receive a message from the CGC to report the Health of electric fence, TRexMonitor, and DVR
 *     1. SendMessage to all children's requesting health. Keep track of the updates.
 *     2. It will respond with a message back to cgc with the current health of all children's.
 *
 * The SurveillanceSystem may receive a message from a cgc requesting location of T-Rex.
 *     1. SendMessage to TRexMonitor to get updated location.
 *     2. SendMessage back to cgc with TRex location.
 *
 * The SurveillanceSystem may receive EmergencyMode message from cgc
 *    1. it needs to sendMessage to all children's.
 *    2. Put itself in emergency mode.
 *
 * The SurveillanceSystem may receive exit EmergencyMode message from cgc
 *    1. it needs to sendMessage to all children's.
 *    2. Put itself out of emergency mode.
 *
 * The SurveillanceSystem may receive message from electric fence of reporting down
 *    1. the SurveillanceSystem will sendMessage to cgc of reporting electric fence down.
 *    2. Then CGC will do appropriate stuff to go in emergency mode (not important for this class, cgc will take care of that).
 *
 */
public class SurveillanceSystem extends Thread implements Communicator {

    private PriorityBlockingQueue<Message> messages;
    private CGC cgc;
    private TRexMonitor tRexMonitor;
    private ElectricFence electricFence;

    public SurveillanceSystem(CGC cgc) {
    }

    @Override
    public void run() {

    }

    @Override
    public void sendMessage(Message m) {

    }

    private void processMessage(Message m) {

    }
}
