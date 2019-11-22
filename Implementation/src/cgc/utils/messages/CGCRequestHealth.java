package cgc.utils.messages;

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


}
