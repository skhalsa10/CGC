package cgc.tokenmanager;

import cgc.utils.Communicator;
import cgc.utils.Locatable;
import cgc.utils.Maintainable;
import cgc.utils.messages.Message;
import javafx.geometry.Point2D;

import java.util.Timer;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class Token extends Thread implements Locatable, Maintainable, Communicator
{
    protected int tokenID;
    protected Point2D location;
    protected boolean healthStatus;
    protected PriorityBlockingQueue<Message> messages;
    protected TokenManager tokenManager;
    protected Timer timer;


    public Token(int tokenID, TokenManager tokenManager)
    {
        this.tokenID = tokenID;
        this.tokenManager = tokenManager;
        this.healthStatus = true;
        this.timer = new Timer();
        this.messages = new PriorityBlockingQueue<>();
    }

    protected abstract void startTokenTimer();

    protected abstract void processMessage(Message m);
}
