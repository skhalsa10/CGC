package cgc.utils.messages;

/**
 * produced by the elctric fence when it shuts down. The cgc turns this into an emergency mode message
 * for the park to respond to
 *
 */
public class ElectricFenceDown implements Message {

    public ElectricFenceDown() {

    }

    /**
     *
     * @return This always has 1 to make it second highest priority after EnterEmergencyMode message.
     */
    @Override
    public long getTimeStamp() {
        return 1;
    }


}
