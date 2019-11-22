package cgc.surveillancesystem;

import cgc.utils.Communicator;
import cgc.utils.Maintainable;
import cgc.utils.messages.Message;

import java.util.Timer;
import java.util.concurrent.PriorityBlockingQueue;

public class ElectricFence extends Thread implements Maintainable, Communicator {

    private boolean isDown;
    private SurveillanceSystem surveillanceSystem;
    private PriorityBlockingQueue<Message> messages;
    private Timer timer;

    public ElectricFence(SurveillanceSystem surveillanceSystem) {

    }


    @Override
    public void sendMessage(Message m) {

    }

    @Override
    public void checkHealth() {

    }

    private void startElectricFenceTimer() {
        // TODO: use timer task with timer to electric fence outage (maybe after a min?)
    }
}
