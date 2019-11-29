package cgc.tokenmanager;

import cgc.utils.messages.EnterEmergencyMode;
import cgc.utils.messages.ExitEmergencyMode;
import cgc.utils.messages.Message;
import cgc.utils.messages.ShutDown;
import javafx.geometry.Point2D;

import java.awt.*;


/**
 * this token encapsulates the behavior os the Guest. in the real world it would just report data to the token
 * manager. but in this simulation it also simulates behavior over time..
 *
 * To simulate the behavior over time it will use a Timer and Timer task to generate location movement over
 * time.
 *
 * This Token Must sendMessage() to the Token Manager with its updated healthstatus Every time it changes
 *
 * This Token Must sendMessage() to the Token Manager with its updated Location EVERY time it changes
 *
 * The Token  may receive EmergencyMode message
 *    2. Put itself in emergency mode.
 *
 * The Token  may receive ExitEmergencyMode message
 *    2. it must exit emergency mode.
 *
 * The Token may receive a Shutdown Message from the Token Manager
 *
 *
 */
public class GuestToken extends Token
{

    //TODO there may also need to be a separate timer and timer task to trigger when a guest visitor is ready to leave exhibit
    private boolean isRunning = true;
    private boolean emergency = false;


    public GuestToken(int ID, TokenManager tokenManager, Point2D GPSLocation)
    {

        super(ID, tokenManager);
        this.GPSLocation = GPSLocation;
    }

    @Override
    public void sendMessage(Message m)
    {
        //TODO place this message in messages queue
    }


    @Override
    public void run()
    {
        //TODO This should loop and wait on the message queue and shut down only if shutdown is received
        //TODO this will call processMessage(m) to respond accordingly
        while (isRunning)
        {
            try
            {
                Message m = this.messages.take();
                processMessage(m);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("Guest Token #"+this.tokenID+" is shutting down.");
    }


    @Override
    protected void startTokenTimer()
    {
        //TODO start token timer here and use a timer task with it.
    }

    @Override
    protected synchronized void processMessage(Message m)
    {
        //TODO process m using instanceof
        if(m instanceof ShutDown)
        {
            isRunning = false;
        }
        else if (m instanceof EnterEmergencyMode)
        {
            emergency = true;
        }
        else if (m instanceof ExitEmergencyMode)
        {
            emergency = false;
        }
    }
}
