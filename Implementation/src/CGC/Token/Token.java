package CGC.Token;

import CGC.CGC;
import CGC.Locatable;
import CGC.Maintainable;
import CGC.Messages.Message;

import java.awt.*;
import java.util.Timer;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class Token extends Thread implements Maintainable, Locatable {
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
     */
    protected abstract void startTokenTimer();

    /**
     * Might want to stop x,y coordinates, for example, when visitors get in the car.
     */
    protected abstract void stopTokenTimer();
}
