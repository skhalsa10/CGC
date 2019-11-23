package cgc.utils;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

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
    //colors for GUI
    private final Color CANVASBACKGROUND = Color.web("#313335");
    private final Color TREXPITSTROKE = Color.web("#c92d39");
    private final Color TREXPITFILL = Color.web("#666666");
    private final Color TREX = Color.web("#c92d39");
    private final Color EMPLOYEE = Color.web("#834187");
    private final Color GUEST = Color.web("#7ab648");
    private final Color TOURVEHICLE = Color.WHITE;
    private final Color PATROLVEHICLE = Color.web("#3aa6dd");
    private final Color KIOSK = Color.web("#ffc374");
    private final Color SOUTHSTROKE = Color.web("#834187");
    private final Color SOUTHFILL = Color.web("#666666");


    public final static double MAP_WIDTH = 600;
    public final static double MAP_HEIGHT = 1000;
    public final static double SOUTHBUILDING_WIDTH = MAP_WIDTH-(MAP_WIDTH/5);
    public final static double SOUTHBUILDING_HEIGHT = MAP_HEIGHT/8;
    public final static double TREX_PIT_WIDTH = MAP_WIDTH/2;
    public final static double TREX_PIT_HEIGHT = TREX_PIT_WIDTH;
    public final static Point2D GUEST_SPAWN_LOCATION = new Point2D(MAP_WIDTH/2,MAP_HEIGHT);
    public final static Point2D SOUTH_PICKUP_LOCATION = new Point2D(MAP_WIDTH/2,MAP_HEIGHT-SOUTHBUILDING_HEIGHT);
    public final static Point2D North_Pickup_Location = new Point2D(0,TREX_PIT_HEIGHT);
    //point in the upper left corner of square t-rex pit
    public final static Point2D UPPER_LEFT_TREX_PIT = new Point2D(MAP_WIDTH/4,0);


    //TODO add linear coordinate for the path of te highway that goes from south to north
    //two points needed

    //TODO if there is a different high way being used from northside to south side those points should be identified below
}
