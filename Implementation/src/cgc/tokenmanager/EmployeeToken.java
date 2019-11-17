package cgc.tokenmanager;

import cgc.messages.Message;

/**
 * this token encapsulates the behavior os the Employee. in the real world it would just report data to the token
 * manager. but in this simulation it also simulates behavior over time..
 *
 * To simulate the behavior over time it will use a Timer and Timer task to generate location movement over
 * time.
 *
 *
 *
 */
public class EmployeeToken extends Token {

    public EmployeeToken(int ID) {
        super(ID);
    }

    @Override
    public void sendMessage(Message m) {

    }

    @Override
    public void getLocation() {

    }

    @Override
    public void checkHealth() {

    }

    @Override
    public void run() {

    }
}
