package cgc.tokenmanager;

import cgc.utils.messages.*;
import javafx.geometry.Point2D;

import java.awt.*;

/**
 * this token encapsulates the behavior os the Employee. in the real world it would just report data to the token
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
public class EmployeeToken extends Token
{

    public EmployeeToken(int ID, TokenManager tokenManager, Point2D GPSLocation)
    {

        super(ID, tokenManager);
        this.GPSLocation = GPSLocation;
    }
    private boolean isRunning = true;
    private boolean emergency = false;

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
        System.out.println("Employee Token #"+this.tokenID+" is shutting down.");
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
        else if (m instanceof CGCRequestHealth)
        {
            //for now I'm putting true in because I don't forsee needing a health status during the simulation, at least for v1
            //also may need to outright commentout this line to make this all work for v1
            sendMessage(new UpdatedHealth(this.getName(),this.tokenID,this.healthStatus));
        }
    }
}
