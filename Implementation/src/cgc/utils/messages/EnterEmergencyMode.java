package cgc.utils.messages;

/**
 * the park responds to this by going into emergency mode this message is sent to everything!
 */
public class EnterEmergencyMode implements Message {

    public EnterEmergencyMode() {

    }

    /**
     * This always has 0 to make it highest priority no matter what (this message is always
     * less to any other message it gets compared to).
     * @return
     */
    @Override
    public long getTimeStamp() {
        return 0;
    }


}
