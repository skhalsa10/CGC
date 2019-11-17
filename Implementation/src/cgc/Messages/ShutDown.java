package cgc.Messages;

/**
 * this is a Completed Message it should be sent to a class to tell it to shut down gracefully
 * this particular class is basic and does not contain much other info but these message can contain other data.
 */
public class ShutDown implements Message {
    private final long timeStamp;

    public ShutDown() {
        this.timeStamp = System.nanoTime();
    }

    @Override
    public String readMessage() {
        return "Shutting Down";
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }
}
