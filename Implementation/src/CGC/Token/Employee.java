package CGC.Token;

import CGC.CGC;

import java.awt.*;


public class Employee extends Token{

    public Employee(int tokenID, CGC cgc) {
        super(tokenID, cgc);
    }

    @Override
    public void run() {

    }

    @Override
    protected void startTokenTimer() {

    }

    @Override
    protected void stopTokenTimer() {

    }

    @Override
    public synchronized void checkHealth() {
        //TODO place a Message inside of the this Class' blocking queue that tells it to update the CGC with
        // Health info
    }

    /**
     * send message to CGC.
     */
    private void reportHealth(boolean healthStatus) {
        //TODO Send a message to the CGC with health Status
    }

    /**
     * place message inside  queue which triggers an update to the CGC when processed
     */
    @Override
    public synchronized void getLocation() {
        //TODO place a message in this class' message queue to trigger a a location sync to the CGC
    }

    /**
     * send message to CGC.
     */
    private void updateLocation(Point loc) {
        //TODO send a message to the CGC with updated location
    }
}
