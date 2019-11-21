package cgc.messages;

public class ElectricFenceDown implements Message {

    public ElectricFenceDown() {

    }

    @Override
    public long getTimeStamp() {
        return 1;
    }

    /**
     * This always has -1 to make it second highest priority after EnterEmergencyMode message.
     * @param o other message
     * @return -1 for second highest priority.
     */
    @Override
    public int compareTo(Message o) {
        return -1;
    }
}
