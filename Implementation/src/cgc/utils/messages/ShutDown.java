package cgc.utils.messages;

/**
 * this is a Completed Message it should be sent to a class to tell it to shut down gracefully
 * this particular class is basic and does not contain much other info but these message can contain other data.
 */
public class ShutDown implements Message {

    public ShutDown() {

    }

    @Override
    public String readMessage() {
        return "Shutting Down";
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
