package cgc.utils.messages;

import java.util.LinkedList;

public class BeginDrivingToDropOff implements Message {

    private LinkedList<Integer> tokensInCarId;

    public BeginDrivingToDropOff(LinkedList<Integer> tokensInCarId) {
        this.tokensInCarId = tokensInCarId;
    }

    public LinkedList<Integer> getTokensInCarId() {
        return tokensInCarId;
    }
}
