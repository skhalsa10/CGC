package cgc.cgcstation;

import cgc.utils.Communicator;
import cgc.utils.MapInfo;
import cgc.utils.messages.Message;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.PriorityBlockingQueue;

public class CGCGUI extends AnimationTimer implements Runnable, Communicator {

    private long lastUpdate = 0;
    private PriorityBlockingQueue<Message> messages;
    private Thread messageThread;
    private boolean isRunning;

    //GUI stuff
    private Stage stage;
    private Scene scene;
    private HBox root;

    //animated map
    private Canvas canvas;
    private GraphicsContext gc;
    private StackPane canvasContainer;
    private Color CANVASBACKGROUND = Color.web("#313335");

    //button stuff
    private VBox leftBPane;
    private VBox rightBPane;
    private Button exitEmergency;
    private Button enterEmergency;
    private Button viewHealth;
    private Button viewFinances;



    public CGCGUI(Stage primaryStage, CGCStation cgcStation) {

        isRunning = true;
        messageThread = new Thread(this);
        messages = new PriorityBlockingQueue<>();

        //GUI
        this.stage = primaryStage;
        stage.setTitle("Cretaceous Gardens Controller");

        //init main stuff
        root = new HBox();
        root.setAlignment(Pos.CENTER);
        canvasContainer = new StackPane();
        canvas = new Canvas(MapInfo.MAP_WIDTH,MapInfo.MAP_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        //button stuff
        leftBPane = new VBox();
        leftBPane.setAlignment(Pos.CENTER);
        //leftBPane.setSpacing(5);
        //leftBPane.setPadding(new Insets(5, 5, 5, 5));
        rightBPane = new VBox();
        rightBPane.setAlignment(Pos.CENTER);
        //rightBPane.setSpacing(5);
        //rightBPane.setPadding(new Insets(5, 5, 5, 5));
        //buttons
        enterEmergency = new Button("Enter\nEmergency");
        enterEmergency.getStyleClass().add("enterEmergency-button");
        exitEmergency = new Button("Exit\nEmergency");
        exitEmergency.getStyleClass().add("exitEmergency-button");
        viewHealth = new Button("View\nHealth");
        viewHealth.getStyleClass().add("viewHealth-button");
        viewFinances = new Button("View\nFinances");
        viewFinances.getStyleClass().add("viewFinances-button");

        //populate stuff
        leftBPane.getChildren().addAll(enterEmergency,exitEmergency);
        rightBPane.getChildren().addAll(viewHealth,viewFinances);
        canvasContainer.getChildren().addAll(canvas);
        canvasContainer.getStyleClass().add("canvasContainer");
        root.getChildren().addAll(leftBPane,canvasContainer,rightBPane);




        //create scene and set style sheet
        scene = new Scene(root, 1000, 1000);
        scene.getStylesheets().add("cgc/cgcstation/GUI.css");

        //display the stage
        stage.setScene(scene);
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
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(Message m){

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

            //first thing we need to do is paint the background of the map
            gc.setFill(CANVASBACKGROUND);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            //draw the trex pit
            gc.setStroke();

            // helped to stabalize the rendor time
            lastUpdate = now;
        }
    }
}
