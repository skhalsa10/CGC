package cgc.messages;

public class EnterEmergencyMode implements Message {

    public EnterEmergencyMode() {

    }

    @Override
    public String readMessage() {
        return "Entering Emergency Mode.";
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    /**
     * This always has -1 to make it highest priority no matter what (this message is always
     * less to any other message it gets compared to).
     * @param o other message
     * @return -1 for highest priority.
     */
    @Override
    public int compareTo(Message o) {
        return -1;
    }
}
