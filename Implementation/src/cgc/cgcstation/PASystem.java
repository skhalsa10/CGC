package cgc.cgcstation;

import cgc.Communicator;
import cgc.Maintainable;
import cgc.messages.Message;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * This will play sounds on the speaker
 */
public class PASystem extends Thread implements Maintainable, Communicator {

    private PriorityBlockingQueue<Message> messages;
    private CGCStation cgcStation;
    //TODO Mode enum

    public PASystem(CGCStation cgcStation) {

    }

    @Override
    public void sendMessage(Message m) {

    }

    @Override
    public void checkHealth() {

    }

    @Override
    public void run() {

    }
}
