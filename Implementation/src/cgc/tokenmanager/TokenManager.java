package cgc.tokenmanager;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;

import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *  This Class will manage the communication with all Token devices this includes Guests and
 *  Employees.
 *
 * The TokenManager may receive a message fro the CGC to spawn a new GuestToken
 *     1. The Token Manager must create a new token.
 * DONE
 *
 * The Token Manager may receive a message from a token with updated location
 *     1. it will respond by forwarding that location with tokenID to cgc.
 * DONE
 *
 * The Token Manager may receive updated health information from the token
 *     1. it must update its list of token health information.
 * DONE
 *
 * The Token Manager may sendMessage to token for updated health information
 * DONE
 *
 * The Token Manager may sendMessage to token requesting its location.
 * DONE
 *
 * The Token Manager may receive EmergencyMode message
 *    1. it needs to sendMessage to all active tokens.
 *    2. Put itself in emergency mode.
 * DONE
 *
 * The Token Manager may receive exit EmergencyMode message
 *    1. it needs to sendMessage to all active tokens.
 *    2. Put itself out of emergency mode.
 * DONE
 *
 * The Token Manager May receive a Shutdown Message from the CGC
 *      1. it will need to send a Shutdown Message to all active tokens
 *      2. it will then shut down gracefully itself.
 * DONE
 *
 * The Token Manager may receive a message from a guest token that it is Deactivated
 *      1. It must delete all information that is being stored for this token
 *
 * The Token Manager may receive a message from the CGC to report the Health of all the Tokens
 *      1. It will respond with a message with the current health of all tokens
 *  DONE
 *
 */
public class TokenManager extends Thread implements Communicator
{

    //need reference to cgc and a messages queue
    private CGC cgc;
    private PriorityBlockingQueue<Message> messages;

    //variables I added
    private boolean isRunning = true;
    private boolean isInEmergency = false;
    private HashMap<Integer,GuestToken> guestTokens = new HashMap<>();
    private HashMap<Integer,EmployeeToken> employeeTokens = new HashMap<>();
    private int tokenID = 0;

    public TokenManager(CGC cgc){
        messages = new PriorityBlockingQueue<>();
        this.cgc = cgc;
        this.start();
    }


    @Override
    public void run() {
        //create employee tokens
        for (int i = 0; i < 30; i++)
        {
            employeeTokens.put(tokenID,new EmployeeToken(tokenID, this, MapInfo.ENTRANCE));
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

    /**
     * place m in queue to process later
     * @param m
     */
    @Override
    public void sendMessage(Message m)
    {
        messages.put(m);
    }

    private synchronized void processMessage(Message m)
    {

        if (m instanceof ShutDown) {
            //shut down all tokens then allow for shutdown of self.
            //loop through tokens shutting them all down
            forwardMessageToAll(m);
            isRunning = false;
        }
        else if(m instanceof EnterEmergencyMode) {
            if(!isInEmergency){
                forwardMessageToAll(m);
                isInEmergency = true;
            }
        }
        else if (m instanceof ExitEmergencyMode) {
            if(isInEmergency) {
                forwardMessageToAll(m);
                isInEmergency = false;
            }

        }
        else if (m instanceof RequestToken) {

            RequestToken m2 = (RequestToken) m;
            GuestToken tmp = new GuestToken(tokenID, this, m2.getLocation());
            guestTokens.put(tokenID,tmp);
            tokenID++;
        }
        else if (m instanceof CGCRequestLocation) {
            forwardMessageToAll(m);
        }
        else if (m instanceof UpdatedLocation){
            cgc.sendMessage(m);
        }
        else if (m instanceof CGCRequestHealth){
            forwardMessageToAll(m);
        }
        else if(m instanceof UpdatedHealth){
            cgc.sendMessage(m);
        }
        else if(m instanceof UpdatedDrivingLocation){
            //forward message to tokens that are in car:
            UpdatedDrivingLocation m2 = (UpdatedDrivingLocation) m;
            for(Integer id:m2.getTokenIds()){
                if(guestTokens.get(id) != null){
                    guestTokens.get(id).sendMessage(m);
                }else if(employeeTokens.get(id)!=null){
                    employeeTokens.get(id).sendMessage(m);
                }
            }
        }
        else if(m instanceof TourCarArrivedAtDropOff){
            // forward to all tokens
            TourCarArrivedAtDropOff m2 = (TourCarArrivedAtDropOff) m;

            //System.out.println("TokenManager Processed TourCarArrivedAtDropOff with tokens "+ m2.getTokensId());
            //System.out.println("at the location "+ m2.getDropOffLocation());

            for(Integer id:m2.getTokensId()){
                if(guestTokens.get(id) != null){
                    guestTokens.get(id).sendMessage(m);
                }else if(employeeTokens.get(id)!=null){
                    employeeTokens.get(id).sendMessage(m);
                }
            }
        }
        else if(m instanceof DeactivateToken){
            DeactivateToken m2 = (DeactivateToken)m;
            guestTokens.remove(m2.getID());
            cgc.sendMessage(m2);
        }
        else if(m instanceof TokenReadyToLeave){
            //System.out.println("Token Manager processing token ready for ID "+ ((TokenReadyToLeave) m).getTokenId());
            cgc.sendMessage(m);
        }
        else{
            System.out.println("Token Manager can not respond to message " + m);
        }
    }

    /**
     * will take a message and forward to Guest and Employee Tokens
     * @param m
     */
    private void forwardMessageToAll(Message m) {
        for (GuestToken tok: guestTokens.values())
        {
            tok.sendMessage(m);
        }
        for (EmployeeToken tok: employeeTokens.values())
        {
            tok.sendMessage(m);
        }
    }
}
