package cgc.utils;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.net.PortUnreachableException;


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
    public final static Color CANVASBACKGROUND = Color.web("#313335");
    public final static Color TREXPITSTROKE = Color.web("#c92d39");
    public final static Color TREXPITFILL = Color.web("#666666");
    public final static Color TREX = Color.web("#c92d39");
    public final static Color EMPLOYEE = Color.web("#834187");
    public final static Color GUEST = Color.web("#7ab648");
    public final static Color TOURVEHICLE = Color.WHITE;
    public final static Color PATROLVEHICLE = Color.web("#3aa6dd");
    public final static Color KIOSK = Color.web("#ffc374");
    public final static Color SOUTHSTROKE = Color.web("#834187");
    public final static Color SOUTHFILL = Color.web("#666666");
    public final static Color ROADCOLOR = Color.BEIGE;


    //public final static double MAP_WIDTH  = Screen.getPrimary().getBounds().getWidth()/2;
    public final static double MAP_WIDTH = 600;
    public final static double MAP_HEIGHT = Screen.getPrimary().getBounds().getHeight()-50;
    public final static double SOUTHBUILDING_WIDTH = MAP_WIDTH-(MAP_WIDTH/5);
    public final static double SOUTHBUILDING_HEIGHT = MAP_HEIGHT/8;
    public final static double TREX_PIT_WIDTH = MAP_WIDTH/2;
    public final static double TREX_PIT_HEIGHT = MAP_HEIGHT/4;

    //PICKUP LOCATIONS
    public final static Point2D GUEST_SPAWN_LOCATION = new Point2D(MAP_WIDTH/2,MAP_HEIGHT);
    public final static Point2D SOUTH_PICKUP_LOCATION = new Point2D(MAP_WIDTH/2,MAP_HEIGHT-SOUTHBUILDING_HEIGHT);
    public final static Point2D NORTH_PICKUP_LOCATION = new Point2D(MAP_WIDTH/2,TREX_PIT_HEIGHT);

    //TREX STUFF
    public final static Point2D UPPER_LEFT_TREX_PIT = new Point2D(MAP_WIDTH/4,0);
    public final static Point2D UPPER_RIGHT_TREX_PIT = new Point2D(MAP_WIDTH/4 + TREX_PIT_WIDTH,0);
    public final static Point2D BOTTOM_LEFT_TREX_PIT = new Point2D(MAP_WIDTH/4, TREX_PIT_HEIGHT);
    public final static Point2D BOTTOM_RIGHT_TREX_PIT = new Point2D(MAP_WIDTH/4 + TREX_PIT_WIDTH, TREX_PIT_HEIGHT);
    public final static Point2D CENTER_TREX_PIT = new Point2D(MAP_WIDTH/4 + TREX_PIT_WIDTH/2, TREX_PIT_HEIGHT/2);

    //PATROL BOX
    public final static Point2D UPPER_LEFT_PATROL_BOX = new Point2D(0, TREX_PIT_HEIGHT+TREX_PIT_HEIGHT/4);
    public final static Point2D BOTTOM_RIGHT_PATROL_BOX = new Point2D(MAP_WIDTH, MAP_HEIGHT-SOUTHBUILDING_HEIGHT);

    // north and south garage dimensions.
    public final static double GARAGE_WIDTH = MAP_WIDTH/6;
    public final static double GARAGE_HEIGHT = MAP_WIDTH/6;

    //Tour Vehicle garage on north and south end, can calculate other 3 points from upper_left and garage dimensions.
    public final static Point2D UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE = new Point2D((MAP_WIDTH/3)*2.5, MAP_HEIGHT-(SOUTHBUILDING_HEIGHT*2));

    //South BUILDING
    public final static Point2D UPPER_LEFT_SOUTH_BULDING = new Point2D((MAP_WIDTH-SOUTHBUILDING_WIDTH)/2,MAP_HEIGHT-SOUTHBUILDING_HEIGHT);

    //Vehicle path
    public final static Point2D ROAD_SOUTH = new Point2D(MAP_WIDTH/2,MAP_HEIGHT-SOUTHBUILDING_HEIGHT);
    public final static Point2D ROAD_NORTH = new Point2D(MAP_WIDTH/2,TREX_PIT_HEIGHT+30);

    //TODO add linear coordinate for the path of te highway that goes from south to north
    //two points needed

    //TODO if there is a different high way being used from northside to south side those points should be identified below
}
