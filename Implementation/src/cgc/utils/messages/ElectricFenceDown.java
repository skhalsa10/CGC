package cgc.utils.messages;

public class ElectricFenceDown implements Message {

    public ElectricFenceDown() {

    }

    /**
     *
     * @return This always has -1 to make it second highest priority after EnterEmergencyMode message.
     */
    @Override
    public long getTimeStamp() {
        return -1;
    }


}
