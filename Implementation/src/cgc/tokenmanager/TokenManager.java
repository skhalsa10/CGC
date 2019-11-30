package cgc.tokenmanager;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;

import javafx.geometry.Point2D;

import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *  This Class will manage the communication with all Token devices this includes Guests and
 *  Employees.
 *
 * The TokenManager may receive a message fro the CGC to spawn a new GuestToken
 *     1. The Token Manager must create a new token.
 *     2. The Token Manager must send Message to CGC with Token information including health and location  and stuff
 *
 * The Token Manager may receive a message from a token with updated location
 *     1. it will respond by forwarding that location with tokenID to cgc.
 *
 * The Token Manager may receive updated health information from the token
 *     1. it must update its list of token health information.
 *
 * The Token Manager may sendMessage to token for updated health information
 *
 * The Token Manager may sendMessage to token requesting its location.
 *
 * The Token Manager may receive EmergencyMode message
 *    1. it needs to sendMessage to all active tokens.
 *    2. Put itself in emergency mode.
 *
 * The Token Manager may receive exit EmergencyMode message
 *    1. it needs to sendMessage to all active tokens.
 *    2. Put itself out of emergency mode.
 *
 * The Token Manager May receive a Shutdown Message from the CGC
 *      1. it will need to send a Shutdown Message to all active tokens
 *      2. it will then shut down gracefully itself.
 *
 * The Token Manager may receive a message from a guest token that it is Deactivated
 *      1. It must delete all information that is being stored for this token
 *
 * The Token Manager may receive a message from the CGC to report the Health of all the Tokens
 *      1. It will respond with a message with the current health of all tokens
 *
 */
public class TokenManager extends Thread implements Communicator
{

    private CGC cgc;
    private PriorityBlockingQueue<Message> messages;

    //variables I added
    private boolean isRunning = true;
    private boolean emergency = false;
    private LinkedList<GuestToken> guestTokens = new LinkedList<>();
    private LinkedList<EmployeeToken> employeeTokens = new LinkedList<>();
    private int tokenID = 0;

    public TokenManager(CGC cgc){
        messages = new PriorityBlockingQueue<>();
        this.cgc = cgc;

    }


    @Override
    public void run()
    {
        //TODO this will loop and wait on the messages queue and
        // call processMessage(m) when a message arrives
        //create employee tokens
        for (int i = 0; i < 5; i++)
        {
            //TODO -EmployeeToken tmp = new EmployeeToken(tokenID, this);
            EmployeeToken tmp = new EmployeeToken(tokenID, this, MapInfo.ENTRANCE);
            tokenID++;
        }
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
        System.out.println("TokenManager shutting down.");
    }

    @Override
    public void sendMessage(Message m)
    {
        //TODO place Message inside of messages Queue
        messages.put(m);
    }

    private synchronized void processMessage(Message m)
    {
        //TODO  respond accordingly to m using instanceof
        if (m instanceof ShutDown)
        {
            //shut down all tokens then allow for shutdown of self.
            //loop through tokens shutting them all down
            for (GuestToken tok: guestTokens)
            {
                sendMessage(m);
            }
            for (EmployeeToken tok: employeeTokens)
            {
                sendMessage(m);
            }
            isRunning = false;
        }
        else if(m instanceof EnterEmergencyMode)
        {
            emergency = true;
            for (GuestToken tok: guestTokens)
            {
                sendMessage(m);
            }
            for (EmployeeToken tok: employeeTokens)
            {
                sendMessage(m);
            }
            //loop through tokens and tell them emergency mode
        }
        else if (m instanceof ExitEmergencyMode)
        {
            emergency = false;
            for (GuestToken tok: guestTokens)
            {
                sendMessage(m);
            }
            for (EmployeeToken tok: employeeTokens)
            {
                sendMessage(m);
            }
            //loop through tokens and free them from emergency mode
        }
        else if (m instanceof RequestToken)
        {
            if (emergency)
            {
                System.out.println("Cannot create a token in emergency mode.");
            }
            else
            {
                GuestToken tmp = new GuestToken(tokenID, this, MapInfo.GUEST_SPAWN_LOCATION);
                guestTokens.add(tmp);
                TokenInfo passInfo = new TokenInfo();
                passInfo.tokenID = tmp.tokenID;
                passInfo.GPSLocation = tmp.GPSLocation;
                passInfo.healthStatus = true;
                sendMessage(passInfo);
                tokenID++;
            }
        }
        else if (m instanceof CGCRequestLocation)
        {
            for (GuestToken tok:guestTokens)
            {
                for (GuestToken toke: guestTokens)
                {
                    sendMessage(m);
                }
                for (EmployeeToken toke: employeeTokens)
                {
                    sendMessage(m);
                }
            }
        }
        else if (m instanceof CGCRequestHealth)
        {
            //loop though all components?
            for (GuestToken tok: guestTokens)
            {
                sendMessage(m);
            }
            for (EmployeeToken tok: employeeTokens)
            {
                sendMessage(m);
            }
        }
    }
}
