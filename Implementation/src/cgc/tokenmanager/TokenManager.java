package cgc.tokenmanager;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.messages.Message;

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
public class TokenManager extends Thread implements Communicator {

    private CGC cgc;
    private PriorityBlockingQueue<Message> messages;

    public  TokenManager(CGC cgc){
        messages = new PriorityBlockingQueue<>();
        this.cgc = cgc;

    }


    @Override
    public void run() {
        //TODO this will loop and wait on the messages queue and
        // call processMessage(m) when a message arrives
    }

    @Override
    public synchronized void sendMessage(Message m) {
        //TODO place Message inside of messages Queue
    }

    private void processMessage(Message m){
        //TODO  respond accordingly to m using instanceof
    }
}
