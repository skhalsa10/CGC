package cgc.surveillancesystem;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.messages.*;

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
    private boolean emergencyMode;
    private boolean run;

    public SurveillanceSystem(CGC cgc) {
        this.emergencyMode = false;
        this.run = true;
        this.cgc = cgc;
        this.messages = new PriorityBlockingQueue<>();
        this.tRexMonitor = new TRexMonitor(this);
        this.electricFence = new ElectricFence(this);
        start();
    }

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

    @Override
    public synchronized void sendMessage(Message m) {
        this.messages.put(m);
    }

    private synchronized void processMessage(Message m) {
        if (m instanceof ShutDown) {
            this.electricFence.sendMessage(m);
            this.tRexMonitor.sendMessage(m);
            this.run = false;
            System.out.println("SurveillanceSystem is shutting down.");
        }
        else if (m instanceof EnterEmergencyMode) {
            if (!emergencyMode) {
                this.emergencyMode = true;

                this.electricFence.sendMessage(m);
                this.tRexMonitor.sendMessage(m);
            }
        }
        // electric fence may report of being down, so send to cgc so it can declare emergency.
        else if (m instanceof ElectricFenceDown) {
            this.cgc.sendMessage(m);
        }
        else if (m instanceof ExitEmergencyMode) {
            this.emergencyMode = false;

            this.electricFence.sendMessage(m);
            this.tRexMonitor.sendMessage(m);
        }
        else if (m instanceof CGCRequestHealth) {
            this.electricFence.sendMessage(m);
            this.tRexMonitor.sendMessage(m);
        }
        else if (m instanceof UpdatedHealth) {
            this.cgc.sendMessage(m);
        }
        // cgc asking for TRex's location.
        else if (m instanceof CGCRequestLocation) {
            this.tRexMonitor.sendMessage(m);
        }
        // TRex giving its updated location.
        else if (m instanceof UpdatedLocation) {
            this.cgc.sendMessage(m);
        }
    }
}
