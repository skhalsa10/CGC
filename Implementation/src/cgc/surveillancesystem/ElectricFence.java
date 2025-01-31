package cgc.surveillancesystem;

import cgc.utils.Communicator;
import cgc.utils.Entity;
import cgc.utils.Maintainable;
import cgc.utils.messages.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class will simulate the electric fence. It will randomly go
 * offline after time hs expired and send the message to its manager above it.
 *
 * @author Anas
 * @version 1
 *
 * Skeleton were written by Anas and Siri
 */
public class ElectricFence extends Thread implements Maintainable, Communicator {

    private SurveillanceSystem surveillanceSystem;
    private PriorityBlockingQueue<Message> messages;
    private boolean run;
    private boolean healthStatus;
    private boolean emergencyMode;
    private Timer timer;

    public ElectricFence(SurveillanceSystem surveillanceSystem) {
        this.run = true;
        this.healthStatus = true;
        this.emergencyMode = false;
        this.surveillanceSystem = surveillanceSystem;
        this.messages = new PriorityBlockingQueue<>();

        this.timer = new Timer();
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
    public void sendMessage(Message m) {
        this.messages.put(m);
    }

    /**
     * send message to surveillance system.
     */
    private void reportHealth(boolean healthStatus) {
        UpdatedHealth updatedHealth = new UpdatedHealth(Entity.ELECTRIC_FENCE, 1, healthStatus);
        this.surveillanceSystem.sendMessage(updatedHealth);
    }

    private void restartTimer() {
        this.timer = new Timer();
        startElectricFenceTimer();
    }

    private void startElectricFenceTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ScheduleElectricFenceOutage outage = new ScheduleElectricFenceOutage();
                messages.put(outage);
            }
        };
        // schedules electric fence outage after 7.5 mins (we have demo for 15 mins, so keep it half for presentation.)
        // , start the initial outage after a minute.
        this.timer.schedule(task, 450000, 450000);
    }

    private synchronized void processMessage(Message message) {
        if (message instanceof ShutDown) {
            this.run = false;
            this.timer.cancel();
            System.out.println("Electric Fence shutting down.");
        }
        else if (message instanceof EnterEmergencyMode) {
            if (!emergencyMode) {
                this.emergencyMode = true;
                this.healthStatus = false;
                // terminates the timer, resume when we exit.
                this.timer.cancel();
                reportHealth(this.healthStatus);
            }
        }
        else if (message instanceof ExitEmergencyMode) {
            this.emergencyMode = false;
            this.healthStatus = true;
            reportHealth(this.healthStatus);
            // resume timer for a possible natural electric fence outage.
            restartTimer();
        }
        else if (message instanceof CGCRequestHealth) {
            reportHealth(this.healthStatus);
        }
        else if (message instanceof ScheduleElectricFenceOutage) {
            ElectricFenceDown electricFenceDown = new ElectricFenceDown();
            // declare emergency terminate the timer, resume when we exit.
            this.timer.cancel();
            this.emergencyMode = true;
            this.surveillanceSystem.sendMessage(electricFenceDown);
        }
    }
}
