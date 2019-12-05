package cgc.cgcstation;

import cgc.utils.Communicator;
import cgc.utils.Entity;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;


import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class CGCGUI extends AnimationTimer implements Runnable, Communicator {

    private long lastUpdate = 0;
    private PriorityBlockingQueue<Message> messages;
    private Thread messageThread;
    private boolean isRunning;
    private boolean isInEmergency;
    private Screen currentScreen;
    private boolean healthOverlayIsOn;
    private int stateCounter=0;
    private boolean emergencyBorderOn=true;
    private boolean emergencyByGUI = false;
    private double emergencyFenceWidth = 0;
    private boolean isBasicRender;

    //GUI stuff
    private Stage stage;

    //main screen
    private Scene mainScene;
    private HBox mainRoot;

    //finance screen
    private Scene financeScene;
    private VBox financeRoot;
    private HBox finBtnBox;
    private HBox titleBox;
    private HBox columnTitles;
    private Text financeTitle;
    private ScrollPane financeScrollPane;
    private VBox logBox;
    private HBox totalBox;
    private int childTokensSold;
    private int adultTokensSold;
    private int seniorTokensSold;
    private double totalSales;


    //animated map
    private Canvas canvas;
    private GraphicsContext gc;
    private StackPane canvasContainer;


    //button stuff
    private VBox leftBPane;
    private VBox rightBPane;
    private Button exitEmergency;
    private Button enterEmergency;
    private Button viewHealth;
    private Button viewFinances;
    private Button viewMain;


    //State stuff
    private Point2D  TRexLoc;
    private boolean  TRexHealth = true;
    private ConcurrentHashMap<Integer,Point2D> tourLocations;
    private ConcurrentHashMap<Integer,Point2D> patrolLocations;
    private ConcurrentHashMap<Integer,Point2D> employeeLocations;
    private ConcurrentHashMap<Integer,Point2D> guestLocations;
    private ConcurrentHashMap<Integer,Point2D> kioskLocations;

    private ConcurrentHashMap<Integer,Boolean> tourHealth;
    private ConcurrentHashMap<Integer,Boolean> patrolHealth;
    private ConcurrentHashMap<Integer,Boolean> employeeHealth;
    private ConcurrentHashMap<Integer,Boolean> guestHealth;
    private ConcurrentHashMap<Integer,Boolean> kioskHealth;
    private LinkedBlockingQueue<SaleLog> financeState;

    //Images to render
    private Image trex;
    private Image kiosk;
    private Image patrol;
    private Image tour;



    public CGCGUI(Stage primaryStage, CGCStation cgcStation) {

        //initialize non GUI stuff
        isBasicRender = false;
        trex = new Image("file:./src/resources/trex2.png", MapInfo.TREX_PIT_WIDTH/6,0,true,true);
        kiosk = new Image("file:./src/resources/kiosk1.png", 30,0,true,true);
        patrol = new Image("file:./src/resources/patrol4.png", 20,0,true,true);
        tour = new Image("file:./src/resources/tour1.png", 20,0,true,true);

        healthOverlayIsOn = false;
        isRunning = true;
        isInEmergency = false;
        messageThread = new Thread(this);
        messages = new PriorityBlockingQueue<>();
        financeState = new LinkedBlockingQueue<>();
        currentScreen = Screen.MAIN;

        //state related stuff
        tourLocations = new ConcurrentHashMap<>();
        patrolLocations = new ConcurrentHashMap<>();
        employeeLocations = new ConcurrentHashMap<>();
        guestLocations = new ConcurrentHashMap<>();
        kioskLocations = new ConcurrentHashMap<>();

        tourHealth = new ConcurrentHashMap<>();
        patrolHealth = new ConcurrentHashMap<>();
        employeeHealth = new ConcurrentHashMap<>();
        guestHealth = new ConcurrentHashMap<>();
        kioskHealth = new ConcurrentHashMap<>();

        //GUI stuff
        this.stage = primaryStage;
        stage.setTitle("Cretaceous Gardens Controller");


        //init main stuff
        mainRoot = new HBox();
        mainRoot.setAlignment(Pos.CENTER);
        canvasContainer = new StackPane();
        canvas = new Canvas(MapInfo.MAP_WIDTH,MapInfo.MAP_HEIGHT);
        canvas.maxWidth(MapInfo.MAP_WIDTH);
        canvas.maxHeight(MapInfo.MAP_HEIGHT);
        gc = canvas.getGraphicsContext2D();



        //button stuff
        leftBPane = new VBox();
        leftBPane.setAlignment(Pos.CENTER);
        rightBPane = new VBox();
        rightBPane.setAlignment(Pos.CENTER);
        //buttons
        enterEmergency = new Button("Enter\nEmergency");
        enterEmergency.getStyleClass().add("enterEmergency-button");
        enterEmergency.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!isInEmergency) {
                    emergencyByGUI = true;
                    cgcStation.sendMessage(new ElectricFenceDown());
                }
            }
        });

        exitEmergency = new Button("Exit\nEmergency");
        exitEmergency.getStyleClass().add("exitEmergency-button");
        exitEmergency.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(isInEmergency) {
                    cgcStation.sendMessage(new ExitEmergencyMode());
                    emergencyByGUI = false;
                    isInEmergency = false;
                    emergencyFenceWidth=0;
                }
            }
        });
        viewHealth = new Button("View\nHealth");
        viewHealth.getStyleClass().add("viewHealth-button");
        viewHealth.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cgcStation.sendMessage(new CGCRequestHealth());
                healthOverlayIsOn = !healthOverlayIsOn;
            }
        });
        viewFinances = new Button("View\nFinances");
        viewFinances.getStyleClass().add("viewFinances-button");
        viewFinances.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //cgcStation.sendMessage(new RequestFinanceInfo());
                currentScreen = Screen.FINANCES;
            }
        });


        viewHealth.setMinWidth(200);
        viewFinances.setMinWidth(200);
        enterEmergency.setMinWidth(200);
        exitEmergency.setMinWidth(200);


        //populate stuff
        leftBPane.getChildren().addAll(enterEmergency,exitEmergency);
        rightBPane.getChildren().addAll(viewHealth,viewFinances);
        canvasContainer.getChildren().addAll(canvas);
        canvasContainer.getStyleClass().add("canvasContainer");
        mainRoot.getChildren().addAll(leftBPane,canvasContainer,rightBPane);

        //initializethe Finance stuff
        //layout stuff
        financeRoot = new VBox();
        titleBox = new HBox();
        logBox = new VBox();
        columnTitles = new HBox();
        //init scroll pan and add the log box
        financeScrollPane = new ScrollPane();
        financeScrollPane.getStyleClass().add("financeScrollPane");
        financeScrollPane.setContent(logBox);
        financeScrollPane.setMinWidth(MapInfo.MAP_WIDTH+400);
        logBox.minWidthProperty().bind(financeScrollPane.widthProperty());

        //non gui related state needed for finance screen
        childTokensSold = 0;
        adultTokensSold = 0;
        seniorTokensSold = 0;
        totalSales = 0;

        //totals box set up
        totalBox = new HBox();

        viewMain = new Button("<");
        viewMain.getStyleClass().add("viewMain-button");
        viewMain.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cgcStation.sendMessage(new CGCRequestLocation());
                currentScreen = Screen.MAIN;
            }
        });
        finBtnBox = new HBox();
        //Pane s3 = new Pane();
        finBtnBox.getChildren().addAll(viewMain);
        //finBtnBox.setHgrow(s3, Priority.ALWAYS);

        financeTitle = new Text("Finances");
        financeTitle.getStyleClass().add("financeTitle");
        Pane s1 = new Pane();
        Pane s2 = new Pane();
        Pane s3 = new Pane();
        Pane s4 = new Pane();
        Text date=new Text("Date Purchased");
        date.getStyleClass().add("log-box");
        Text ticketType = new Text("Ticket Type");
        ticketType.getStyleClass().add("log-box");
        Text price = new Text("Ticket Price");
        price.getStyleClass().add("log-box");

        columnTitles.getChildren().addAll(s4,date,s1,ticketType,s2,price,s3);
        columnTitles.setHgrow(s1, Priority.ALWAYS);
        columnTitles.setHgrow(s2, Priority.ALWAYS);
        columnTitles.setHgrow(s3, Priority.ALWAYS);
        columnTitles.setHgrow(s4, Priority.ALWAYS);
        columnTitles.setAlignment(Pos.BASELINE_CENTER);

        Pane space1 = new Pane();
        Pane space2 = new Pane();
        titleBox.getChildren().addAll(space1, financeTitle,space2);
        titleBox.setAlignment(Pos.TOP_CENTER);
        titleBox.setHgrow(space1, Priority.ALWAYS);
        titleBox.setHgrow(space2, Priority.ALWAYS);
        financeRoot.getChildren().addAll(finBtnBox,titleBox, columnTitles, financeScrollPane,totalBox);
        financeRoot.setVgrow(financeScrollPane,Priority.ALWAYS);


        //TESTING TODO REMOVE GOR GOLIVE
        TRexLoc = new Point2D(MapInfo.CENTER_TREX_PIT.getX(), MapInfo.CENTER_TREX_PIT.getY());



        //create scene and set style sheet
        mainScene = new Scene(mainRoot, mainRoot.getMaxWidth(), MapInfo.MAP_HEIGHT-50);
        financeScene = new Scene(financeRoot, mainRoot.getMaxWidth(), MapInfo.MAP_HEIGHT-50);
        mainScene.getStylesheets().add("cgc/cgcstation/GUI.css");
        financeScene.getStylesheets().add("cgc/cgcstation/GUI.css");

        stage.setMinWidth(MapInfo.MAP_WIDTH+400);
        stage.setMinHeight(MapInfo.MAP_HEIGHT-50);

        //display the stage
        stage.setScene(mainScene);
        stage.show();

        messageThread.start();
        this.start();
    }




    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }

    @Override
    public void run() {
        while(isRunning){
            try {
                Message m = messages.take();
                //System.out.println("receiving messages");
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void processMessage(Message m){
        if(m instanceof UpdatedLocation){

            UpdatedLocation m2 = (UpdatedLocation) m;

            switch (m2.getEntityName()){
                case TREX:{
                    //System.out.println("X: " + m2.getLoc().getX() + " Y: " + m2.getLoc().getY());
                    TRexLoc = m2.getLoc();
                    break;
                }
                case GUEST_TOKEN:{
                    //this should replace location if it exists or add it if it doesnt
                    guestLocations.put(m2.getEntityID(), m2.getLoc());
                    break;
                }
                case TOUR_VEHICLE:{
                    tourLocations.put(m2.getEntityID(), m2.getLoc());
                    break;
                }
                case EMPLOYEE_TOKEN:{
                    //System.out.println("employee token location registered");
                    employeeLocations.put(m2.getEntityID(), m2.getLoc());
                    break;
                }
                case PATROL_VEHICLE:{
                    patrolLocations.put(m2.getEntityID(), m2.getLoc());
                    break;

                }
                case KIOSK:{
                    kioskLocations.put(m2.getEntityID(), m2.getLoc());
                    break;
                }
            }
        }
        else if(m instanceof UpdatedHealth){
            UpdatedHealth m2 = (UpdatedHealth) m;
            switch (m2.getEntityName()){
                case TREX:{
                    TRexHealth = m2.isHealthStatus();
                    break;
                }
                case GUEST_TOKEN:{
                    //this should replace location if it exists or add it if it doesnt
                    guestHealth.put(m2.getEntityID(), m2.isHealthStatus());
                    break;
                }
                case TOUR_VEHICLE:{
                    tourHealth.put(m2.getEntityID(), m2.isHealthStatus());
                    break;
                }
                case EMPLOYEE_TOKEN:{
                    employeeHealth.put(m2.getEntityID(), m2.isHealthStatus());
                    break;
                }
                case PATROL_VEHICLE:{
                    //System.out.println("UPDATED PATROL HEALTH");
                    patrolHealth.put(m2.getEntityID(), m2.isHealthStatus());
                    break;

                }
                case KIOSK:{
                    kioskHealth.put(m2.getEntityID(), m2.isHealthStatus());
                    break;
                }
            }
        }else if (m instanceof UpdatedDrivingLocation){
            UpdatedDrivingLocation m2 = (UpdatedDrivingLocation) m;

            //System.out.println("the UpdatedDriving Location in GUI is " + m2.getCurrentCarLocation()+ "for car id " + m2.getCarId());
            //System.out.println("...oh by the way the token list is " + m2.getTokenIds());
            //need to update the car location as well as all the token in the list
            tourLocations.put(m2.getCarId(),m2.getCurrentCarLocation());

            for(Integer tokenId:m2.getTokenIds()){
                if(guestLocations.get(tokenId) != null){
                    guestLocations.put(tokenId,m2.getCurrentCarLocation());
                }else if(employeeLocations.get(tokenId)!=null){
                    employeeLocations.put(tokenId,m2.getCurrentCarLocation());
                }else{
                    System.out.println("ERROR CGCGUI processing UpdatedDrivingLocation");
                }
            }

        }
        else if(m instanceof EnterEmergencyMode){
            EnterEmergencyMode m2 = (EnterEmergencyMode) m;
            //System.out.println("received enteremergencymode");
            isInEmergency = true;
        }
        else if(m instanceof SaleLog){
            try {
                financeState.put((SaleLog)m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(m instanceof  DeactivateToken){
            DeactivateToken m2 = (DeactivateToken)m;
            switch (m2.getEntity()){
                case GUEST_TOKEN:{
                    guestLocations.remove(m2.getID());
                    guestHealth.remove(m2.getID());
                    break;
                }
                case EMPLOYEE_TOKEN:{
                    employeeLocations.remove(m2.getID());
                    employeeHealth.remove(m2.getID());
                    break;
                }
            }
        }
        else if(m instanceof ShutDown){
            System.out.println("GUI is Shutting down");
            isRunning = false;
            this.stop();
        }
        else{
            System.out.println("Cant process this message sorry");
        }
    }



    /**
     * this is used to paint the gui on the screen. it is needed to draw the animation.
     * @param now
     */
    @Override
    public void handle(long now) {
        //there are 1000 miliseconds in a second. if we divide this by 60 there
        // are 16.666667 ms between frame draws
        if (now - lastUpdate >= 16_667_000) {

            if(currentScreen == Screen.FINANCES){
                renderFinanceScreen();

            }else if(currentScreen == Screen.HEALTH) {

            }
            else {
                renderMainScreen();
            }

            // helped to stabalize the rendor time
            lastUpdate = now;
        }
    }

    private void renderFinanceScreen() {
        stage.setScene(financeScene);
        SaleLog m;
        if((m = financeState.poll())!=null){
            buildItemizedList(m);
            buildTotalBox();
        }
    }

    /**
     * when a Sale Log message comes in it will get parsed and a new row will be added to the scroll list
     * total numbers also get incremented so the total box can be built
     * @param loginfo
     */
    private void buildItemizedList(SaleLog loginfo) {
        DecimalFormat format = new DecimalFormat("#,###.00");
        HBox row = new HBox();
        totalSales += loginfo.getAmount();
        Text type = new Text();
        //increment ticket counter and update type text
        switch (loginfo.getTicketType()){
            case CHILDREN:{
                type.setText("Child");
                childTokensSold++;
                break;
            }
            case ADULT:{
                type.setText(" Adult ");
                adultTokensSold++;
                break;
            }
            case SENIOR:{
                type.setText("Senior");
                seniorTokensSold++;
                break;
            }

        }
        type.getStyleClass().add("log-box");
        Text date = new Text(loginfo.getPurchasedDate().toString());
        date.getStyleClass().add("log-box");
        Text amount = new Text("$"+format.format(loginfo.getAmount()));
        amount.getStyleClass().add("log-box");

        Pane s2 = new Pane();
        Pane s6 = new Pane();
        Pane s7 = new Pane();

        row.getChildren().addAll(date,s2, type,s6, amount, s7);
        row.setHgrow(s2,Priority.ALWAYS);
        row.setHgrow(s6,Priority.ALWAYS);
        row.setHgrow(s7,Priority.ALWAYS);
        //row.setHgrow(amount,Priority.ALWAYS);
        logBox.getChildren().add(row);

    }

    /**
     * this build the horizontal box at the bottom of the screen
     */
    private void buildTotalBox() {
        Text c = new Text("Total Tokens: Child - "+ childTokensSold+" , ");
        Text a = new Text("Adult - "+adultTokensSold+" , ");
        Text s = new Text("Senior - "+seniorTokensSold);
        DecimalFormat format = new DecimalFormat("#,###,###.00");
        Text t = new Text("Total Sales: $" + format.format(totalSales));
        c.getStyleClass().add("Finance-Text");
        a.getStyleClass().add("Finance-Text");
        s.getStyleClass().add("Finance-Text");
        t.getStyleClass().add("Finance-Text");
        Pane spacer = new Pane();
        //clear the box
        totalBox.getChildren().clear();
        //read with updated info
        totalBox.getChildren().addAll(c,a,s, spacer,t);
        totalBox.setHgrow(spacer,Priority.ALWAYS);
    }


    private synchronized void renderMainScreen() {
        stage.setScene(mainScene);
        //first thing we need to do is paint the background of the map
        gc.setFill(MapInfo.CANVASBACKGROUND);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //Draw road
        gc.setStroke(MapInfo.ROADCOLOR);
        gc.setLineWidth(16);
        gc.strokeLine(MapInfo.ROAD_SOUTH.getX(), MapInfo.ROAD_SOUTH.getY(), MapInfo.ROAD_NORTH.getX(), MapInfo.ROAD_NORTH.getY());
        //for south to north
        //gc.strokeLine(MapInfo.ROAD_SOUTH_FOR_SOUTH_TO_NORTH.getX(), MapInfo.ROAD_SOUTH_FOR_SOUTH_TO_NORTH.getY(), MapInfo.ROAD_NORTH_FOR_SOUTH_TO_NORTH.getX(), MapInfo.ROAD_NORTH_FOR_SOUTH_TO_NORTH.getY());
        //for North to south
        //gc.strokeLine(MapInfo.ROAD_SOUTH_FOR_NORTH_TO_SOUTH.getX(), MapInfo.ROAD_SOUTH_FOR_NORTH_TO_SOUTH.getY(), MapInfo.ROAD_NORTH_FOR_NORTH_TO_SOUTH.getX(), MapInfo.ROAD_NORTH_FOR_NORTH_TO_SOUTH.getY());


        //draw the trex pit
        gc.setStroke(MapInfo.TREXPITSTROKE);
        gc.setLineWidth(4);
        if(isInEmergency && !emergencyByGUI){
            gc.setLineWidth(emergencyFenceWidth);
            if(stateCounter%3 == 0){
                emergencyFenceWidth++;
           }
            if(emergencyFenceWidth>5) {
                emergencyFenceWidth = 0;
            }
        }
        gc.setFill(MapInfo.TREXPITFILL);
        gc.fillRect(MapInfo.UPPER_LEFT_TREX_PIT.getX(), MapInfo.UPPER_LEFT_TREX_PIT.getY(),MapInfo.TREX_PIT_WIDTH, MapInfo.TREX_PIT_HEIGHT);
        gc.strokeRect(MapInfo.UPPER_LEFT_TREX_PIT.getX(), MapInfo.UPPER_LEFT_TREX_PIT.getY(),MapInfo.TREX_PIT_WIDTH, MapInfo.TREX_PIT_HEIGHT);

        //draw the South building
        gc.setStroke(MapInfo.SOUTHSTROKE);
        gc.setLineWidth(4);
        gc.setFill(MapInfo.SOUTHFILL);
        gc.fillRect(MapInfo.UPPER_LEFT_SOUTH_BULDING.getX(), MapInfo.UPPER_LEFT_SOUTH_BULDING.getY(),MapInfo.SOUTHBUILDING_WIDTH, MapInfo.SOUTHBUILDING_HEIGHT);
        gc.strokeRect(MapInfo.UPPER_LEFT_SOUTH_BULDING.getX(), MapInfo.UPPER_LEFT_SOUTH_BULDING.getY(),MapInfo.SOUTHBUILDING_WIDTH, MapInfo.SOUTHBUILDING_HEIGHT);

        //draw both Garages
        gc.setFill(MapInfo.GARAGEFILL);
        //here we do south
        gc.fillRect(MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getX(),MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getY(), MapInfo.GARAGE_WIDTH,MapInfo.GARAGE_HEIGHT);
        //here we do north
        gc.fillRect(MapInfo.UPPER_LEFT_TOURVEHICLE_NORTH_GARAGE.getX(),MapInfo.UPPER_LEFT_TOURVEHICLE_NORTH_GARAGE.getY(), MapInfo.GARAGE_WIDTH,MapInfo.GARAGE_HEIGHT);

        //DRAW TREX
        gc.setFill(MapInfo.TREX);
        if(isBasicRender){
            gc.fillOval(TRexLoc.getX(),TRexLoc.getY(),8,8);
        }
        else{
            gc.drawImage(trex,TRexLoc.getX()-trex.getWidth()/2,TRexLoc.getY()-trex.getHeight()/2);
        }

        if(healthOverlayIsOn){
            if(TRexHealth) {
                gc.setFill(Color.LIME);
                gc.fillText("Healthy",TRexLoc.getX(),TRexLoc.getY());
            }else{
                gc.setFill(Color.LIGHTSALMON);
                gc.fillText("Not Healthy",TRexLoc.getX(),TRexLoc.getY());
            }
        }

        //DRAW KIOSKS
        for(Integer i:kioskLocations.keySet()){
            gc.setFill(MapInfo.KIOSK);
            Point2D p = kioskLocations.get(i);
            if(isBasicRender){
                gc.fillRect(p.getX(),p.getY(),12,8);
            }else {

                gc.drawImage(kiosk, p.getX() - kiosk.getWidth() / 2, p.getY() - (kiosk.getHeight() - 8));
            }
            if(kioskHealth.get(i)!= null) {
                renderHealth(kioskHealth.get(i), p.getX(), p.getY());
            }else{
                //System.out.println("cant render health for this node...");
            }
        }


        //Draw guest tokens
        for(Integer i:guestLocations.keySet()){
            gc.setFill(MapInfo.GUEST);
            renderNodeAndHealth(i, guestLocations, guestHealth, Entity.GUEST_TOKEN);
        }


        //DRAW employee tokens
        for(Integer i:employeeLocations.keySet()){
            gc.setFill(MapInfo.EMPLOYEE);
            renderNodeAndHealth(i, employeeLocations, employeeHealth, Entity.EMPLOYEE_TOKEN);
        }


        //DRAW TOUR VEHICLES
        //System.out.println("The Tour Vehicle Location: " + tourLocations.size());
        for(Integer i:tourLocations.keySet()){
            gc.setFill(MapInfo.TOURVEHICLE);
            renderNodeAndHealth(i, tourLocations, tourHealth, Entity.TOUR_VEHICLE);
        }


//
        for(Integer i:patrolLocations.keySet()){
            gc.setFill(MapInfo.PATROLVEHICLE);
            renderNodeAndHealth(i, patrolLocations, patrolHealth, Entity.PATROL_VEHICLE);
        }

        if(isInEmergency){
            if(emergencyBorderOn) {
                gc.setStroke(Color.web("#c92d39", .2));
                gc.setLineWidth(40);
                gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
            stateCounter++;
            if(stateCounter%50==0){
                emergencyBorderOn = !emergencyBorderOn;
            }

        }


    }

    /**
     * Helper function for renderMainScreen
     * @param i
     * @param locations
     * @param health
     * @param entity
     */
    private void renderNodeAndHealth(Integer i, ConcurrentHashMap<Integer, Point2D> locations, ConcurrentHashMap<Integer, Boolean> health, Entity entity) {
        Point2D p = locations.get(i);
        if(isBasicRender) {
            gc.fillOval(p.getX(), p.getY(), 6, 6);
        }else{
            switch (entity){
                case PATROL_VEHICLE:{
                    gc.drawImage(patrol,p.getX()-patrol.getWidth()/2,p.getY()-patrol.getHeight()/2);
                    break;
                }
                case EMPLOYEE_TOKEN:{
                    gc.fillOval(p.getX(), p.getY(), 6, 6);
                    break;
                }
                case GUEST_TOKEN:{
                    gc.fillOval(p.getX(), p.getY(), 6, 6);
                    break;
                }
                case TOUR_VEHICLE:{
                    gc.drawImage(tour,p.getX()-patrol.getWidth()/2,p.getY()-patrol.getHeight()/2);

                }
            }
        }
        if(health.get(i)!= null) {
            renderHealth(health.get(i), p.getX(), p.getY());
        }else{
            //System.out.println("cant render health for this node...");
        }
    }

    /**
     * helper function for renderMainScreen
     * @param healthStatus
     * @param x
     * @param y
     */
    private void renderHealth(Boolean healthStatus, double x, double y) {
        if(healthOverlayIsOn){

            if(healthStatus) {
                gc.setFill(Color.LIME);
                gc.fillText("Healthy",x,y);
            }else{
                gc.setFill(Color.LIGHTSALMON);
                gc.fillText("Not Healthy",x,y);
            }
        }
    }

}
