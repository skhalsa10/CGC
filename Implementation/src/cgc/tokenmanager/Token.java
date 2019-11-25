package cgc.tokenmanager;

import cgc.utils.Communicator;
import cgc.utils.Locatable;
import cgc.utils.Maintainable;
import cgc.utils.messages.Message;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class Token extends Thread implements Locatable, Maintainable, Communicator
{
    protected int tokenID;
    protected Point2D GPSLocation;
    protected boolean healthStatus;
    protected PriorityBlockingQueue<Message> messages;
    protected TokenManager tokenManager;
    //TODO should there be a property here to determine if it is ready for pickup?
    //TODO what about one to determine if it is currently in  a car?
    //TODO what about one if it is on the southlot or north lot?


    public Token(int tokenID, TokenManager tokenManager)
    {
        this.tokenID = tokenID;
        this.tokenManager = tokenManager;
        this.healthStatus = true;
        this.messages = new PriorityBlockingQueue<>();
    }

    protected abstract void startTokenTimer();

    protected abstract void processMessage(Message m);
}
