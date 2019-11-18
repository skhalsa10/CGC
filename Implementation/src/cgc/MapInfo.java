package cgc;

import java.awt.*;

/**
 * the following constants can be used to calculate  math related stuff with the map is needed
 *
 * for example the TRexMonitor implementor will need to know the x and y coordinates of the TREX PIT
 * this can be extrapulated from the data below. the behavior logic of the location movement in the t-rex
 * will need this to make sure it never leaves the pit.
 *
 *
 *
 */
public final class MapInfo {

    public final static int MAP_WIDTH = 600;
    public final static int MAP_HEIGHT = 1200;
    public final static Point GUEST_SPAWN_LOCATION = new Point(MAP_WIDTH/2,MAP_HEIGHT);
    public final static Point SOUTH_PICKUP_LOCATION = new Point();
    public final static Point North_Pickup_Location = new Point();
    //point in the upper left corner of square t-rex pit
    public final static Point UPPER_LEFT_TREX_PIT = new Point(MAP_WIDTH/4,0);
    public final static int TREX_PIT_WIDTH = MAP_WIDTH/2;
    public final static int TREX_PIT_HEIGHT = TREX_PIT_WIDTH;

    //TODO add linear coordinate for the path of te highway that goes from south to north
    //two points needed

    //TODO if there is a different high way being used from northside to south side those points should be identified below
}
