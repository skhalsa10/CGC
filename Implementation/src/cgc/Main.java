package cgc;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    CGC cgc;

    @Override
    public void start(Stage primaryStage) throws Exception{
        cgc = new CGC(primaryStage);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
