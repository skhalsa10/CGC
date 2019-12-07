package cgc;

import cgc.utils.messages.ShutDown;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class is an entry point for javafx
 * it initialized the CGC as well.
 *
 * @version 1
 * @author siri
 *
 * Skeleton were written by Anas and Siri
 */
public class Main extends Application {
    private CGC cgc;

    @Override
    public void start(Stage primaryStage) throws Exception{
        cgc = new CGC(primaryStage);


    }

    @Override
    public void stop(){
        System.out.println("Main shutting down");
        cgc.sendMessage(new ShutDown());
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
