package cgc.Token;

import cgc.CGC;
import cgc.Locatable;
import cgc.Maintainable;
import cgc.Messages.Message;
import cgc.Communicator;

import java.awt.*;
import java.util.Timer;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class Token extends Thread implements Maintainable, Locatable, Communicator {
    private int tokenID;
    private PriorityBlockingQueue<Message> messages;
    private Point GPS;
    private Timer timer;

    public Token(int tokenID, CGC cgc) {

        start();
    }


    /**
     * The timer task will be used to place messages in the blocking queue
     * to change x,y coordinates and perform any other potential behavior overtime.
     * Will need to use a TimerTask with the timer.
     */
    protected abstract void startTokenTimer();

    /**
     * Might want to stop x,y coordinates, for example, when visitors get in the car.
     */
    protected abstract void stopTokenTimer();
}
