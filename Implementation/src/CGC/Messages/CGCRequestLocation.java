package CGC.Messages;

/**
 *
 * this message will be sent to a node when the CGC needs it to inform it of its current Location
 * at the time that this message is processed.  The receiving class should respond to this
 * Message by sending its current location to the CGC in a message.
 *
 */
public class CGCRequestLocation implements Message {
    private final long timeStamp;

    public CGCRequestLocation() {
        this.timeStamp = System.nanoTime();
    }

    @Override
    public String readMessage() {
        return "The CGC is requesting the current Location.";
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }
}
