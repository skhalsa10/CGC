package cgc.messages;

/**
 * TODO: Whoever is implementing KioskManager will need some sort of way to include all finance data in this message.
 */
public class UpdatedFinanceInfo implements Message {
    // TODO: Add appropriate properties.

    public UpdatedFinanceInfo() {

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
