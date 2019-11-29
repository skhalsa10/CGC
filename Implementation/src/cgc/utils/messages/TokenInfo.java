package cgc.utils.messages;

import javafx.geometry.Point2D;

import java.awt.*;

/**
 * Once the token is created then, The tokenManager will pass on token info such as location and tokenID to CGC.
 */
public class TokenInfo implements Message
{

    // TODO: Add tokenID, location and other appropriate properties that needs to be send to CGC.
    public int tokenID;
    public Point2D GPSLocation;
    public boolean healthStatus;

    public TokenInfo()
    {
    }

    //getTokenID()


}
