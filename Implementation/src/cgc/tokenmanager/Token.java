package cgc.tokenmanager;

import cgc.Communicator;
import cgc.Locatable;
import cgc.Maintainable;

public abstract class Token extends Thread implements Locatable, Maintainable, Communicator {
    int ID;


    public Token(int ID) {
        this.ID = ID;
    }
}
