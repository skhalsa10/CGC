package cgc;

import cgc.utils.messages.ShutDown;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    CGC cgc;

    @Override
    public void start(Stage primaryStage) throws Exception{
        cgc = new CGC(primaryStage);


    }

    @Override
    public void stop(){
        System.out.println("shutting down");
        cgc.sendMessage(new ShutDown());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
