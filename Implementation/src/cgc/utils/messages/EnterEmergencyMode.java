package cgc.utils.messages;

public class EnterEmergencyMode implements Message {

    public EnterEmergencyMode() {

    }

    /**
     * This always has -2 to make it highest priority no matter what (this message is always
     * less to any other message it gets compared to).
     * @return
     */
    @Override
    public long getTimeStamp() {
        return 0;
    }


}
