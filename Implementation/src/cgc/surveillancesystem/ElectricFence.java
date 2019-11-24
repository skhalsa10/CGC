package cgc.surveillancesystem;

import cgc.utils.Communicator;
import cgc.utils.Entity;
import cgc.utils.Maintainable;
import cgc.utils.messages.Message;
import cgc.utils.messages.UpdatedHealth;

import java.util.Timer;
import java.util.concurrent.PriorityBlockingQueue;

public class ElectricFence extends Thread implements Maintainable, Communicator {

    private boolean isDown;
    private SurveillanceSystem surveillanceSystem;
    private PriorityBlockingQueue<Message> messages;
    private boolean run;
    private boolean healthStatus;
    private Timer timer;

    public ElectricFence(SurveillanceSystem surveillanceSystem) {
        this.run = true;
        this.surveillanceSystem = surveillanceSystem;
        this.messages = new PriorityBlockingQueue<>();

        startElectricFenceTimer();
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

    /**
     * send message to surveillance system.
     */
    private void reportHealth(boolean healthStatus) {
        //TODO Send a message to the surveillanceSystem with health Status
        UpdatedHealth updatedHealth = new UpdatedHealth(Entity.ELECTRIC_FENCE, 1, healthStatus);
        this.surveillanceSystem.sendMessage(updatedHealth);
    }


    private void startElectricFenceTimer() {
        // TODO: use timer task with timer to electric fence outage (maybe after a min?)
    }

    private synchronized void processMessage(Message message) {

    }
}
