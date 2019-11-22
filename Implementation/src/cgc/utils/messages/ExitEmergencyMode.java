package cgc.utils.messages;

public class ExitEmergencyMode implements Message {

    public ExitEmergencyMode() {

    }

    @Override
    public String readMessage() {
        return "Exiting Emergency Mode.";
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
