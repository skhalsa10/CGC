package cgc.utils.messages;

import java.util.LinkedList;

/**
 * dispatcher sends this to tourVehicle to move towards the appropriate dropoff
 * @author Anas
 */
public class BeginDrivingToDropOff implements Message {

    private LinkedList<Integer> tokensInCarId;

    public BeginDrivingToDropOff(LinkedList<Integer> tokensInCarId) {

        this.tokensInCarId = cloneList(tokensInCarId);
    }

    /**
     * We clone this list here so we dont reference a
     * list that has been modified elsewhere and has data we do not expect
     * @param tokensId
     * @return
     */
    private LinkedList<Integer> cloneList(LinkedList<Integer> tokensId) {
        LinkedList<Integer> temp = new LinkedList<>();
        for(Integer id:tokensId){
            temp.add(id);
        }
        return temp;
    }

    public LinkedList<Integer> getTokensInCarId() {
        return tokensInCarId;
    }
}
