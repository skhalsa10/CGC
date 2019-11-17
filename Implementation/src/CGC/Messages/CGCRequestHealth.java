package CGC.Messages;

/**
 *
 * this message will be sent to a node when the CGC needs it to inform it of its current health
 * status at the time that this message is processed.  The receiving class should respond to this
 * Message by sending its current health status to the CGC in a message.
 *
 */
public class CGCRequestHealth implements Message {
    private final long timeStamp;

    public CGCRequestHealth() {
        this.timeStamp = System.nanoTime();
    }

    @Override
    public String readMessage() {
        return "The CGC is requesting the current Health Status.";
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }
}
