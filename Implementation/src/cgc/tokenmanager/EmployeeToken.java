package cgc.tokenmanager;

import cgc.messages.Message;

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
public class EmployeeToken extends Token {

    public EmployeeToken(int ID, TokenManager tokenManager, Point GPSLocation) {

        super(ID, tokenManager);
        this.GPSLocation = GPSLocation;
    }

    @Override
    public synchronized void sendMessage(Message m) {
        //TODO place this message in messages queue
    }

    @Override
    public synchronized void getLocation() {
        //TODO Place a message in messages queue to send a location update to Token Manager
    }

    @Override
    public synchronized void checkHealth() {
        //TODO Place a message in messages queue to send a health update to Token Manager
    }

    @Override
    public void run() {
        //TODO This should loop and wait on the message queue and shut down only if shutdown is received
        //TODO this will call processMessage(m) to respond accordingly
    }


    @Override
    protected void startTokenTimer() {
        //TODO start token timer here and use a timer task with it.
    }

    @Override
    protected void processMessage(Message m) {
        //TODO process m using instanceof
    }
}
