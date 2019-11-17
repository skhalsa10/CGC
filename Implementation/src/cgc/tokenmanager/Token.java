package cgc.tokenmanager;

import cgc.Communicator;
import cgc.Locatable;
import cgc.Maintainable;

public abstract class Token extends Thread implements Locatable, Maintainable, Communicator {
    private int tokenID;


    public Token(int tokenID) {
        this.tokenID = tokenID;
    }
}
