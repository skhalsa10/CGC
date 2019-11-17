package cgc.Token;

import cgc.CGC;
import cgc.Messages.Message;
import java.awt.*;

public class Visitor extends Token {

    @Override
    public void run() {
        super.run();
    }

    public Visitor(int tokenID, CGC cgc) {
        super(tokenID, cgc);
    }

    /**
     * please use the Timer Task in conjunction with Timer to schedule actions over time
     */
    @Override
    protected void startTokenTimer() {

    }

    /**
     * in case you want to stop the timer and the tasks it generates
     */
    @Override
    protected void stopTokenTimer() {

    }

    @Override
    public synchronized void checkHealth() {
        //TODO place a Message inside of the this Class' blocking queue that tells it to update the cgc with
        // Health info
    }

    /**
     * send message to cgc.
     */
    private void reportHealth(boolean healthStatus) {
        //TODO Send a message to the cgc with health Status
    }

    /**
     * place message inside  queue which triggers an update to the cgc when processed
     */
    @Override
    public synchronized void getLocation() {
        //TODO place a message in this class' message queue to trigger a a location sync to the cgc
    }

    /**
     * send message to cgc.
     */
    private void updateLocation(Point loc) {
        //TODO send a message to the cgc with updated location
    }

    /**
     * this is a public message to the outside world to use if the "cgc" needs to send a message to this class it will do
     * so by calling this message. this class may be processing messages already. so this should just move the message into
     * its blocking queue to process when it can.
     * @param m is the message to be placed in this class' Message queue
     */
    @Override
    public void sendMessage(Message m) {

    }
}
