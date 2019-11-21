package cgc.messages;

public class EnterEmergencyMode implements Message {

    public EnterEmergencyMode() {

    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    /**
     * This always has -2 to make it highest priority no matter what (this message is always
     * less to any other message it gets compared to).
     * @param o other message
     * @return -2 for highest priority.
     */
    @Override
    public int compareTo(Message o) {
        return -2;
    }
}
