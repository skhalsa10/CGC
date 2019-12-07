package cgc.tokenmanager;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;

import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *  This Class will manage the communication with all Token devices this includes Guests and
 *  Employees. It is another pretty simple class it keeps track of all active tokens and forwards messages to them... thats it.
 *
 *
 * Skeleton were written by Anas and Siri
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
