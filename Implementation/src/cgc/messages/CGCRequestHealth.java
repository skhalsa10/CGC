package cgc.messages;

/**
 *
 * this message will be sent to a node when the cgc needs it to inform it of its current health
 * status at the time that this message is processed.  The receiving class should respond to this
 * Message by sending its current health status to the cgc in a message.
 *
 */
public class CGCRequestHealth implements Message {

    public CGCRequestHealth() {

    }

    @Override
    public String readMessage() {
        return "The cgc is requesting the current Health Status.";
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public int compareTo(Message o) {
        long result = this.timeStamp - o.getTimeStamp();
        if (result > 0 ) { return 1; }
        else if (result == 0) { return 0; }
        else { return 0; }
    }
}
