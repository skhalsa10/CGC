package cgc.old.Token;

import cgc.old.CGC;
import cgc.messages.Message;

import java.awt.*;


public class Employee extends Token{

    public Employee(int tokenID, CGC cgc) {
        super(tokenID, cgc);
    }

    @Override
    public void run() {
        //TODO this should love over the blocking queue processing messages until
        // it receives the "Shutdown" Message that tells it to gracefully shutdown
    }

    @Override
    protected void startTokenTimer() {

    }

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

    @Override
    public void sendMessage(Message m) {

    }
}
