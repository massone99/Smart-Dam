package application;

import controller.HttpClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

    public static String STATUS;

    private final HttpClient client = new HttpClient();

    @Override
    public void start(Stage primaryStage) throws Exception {
        GUI gui = new GUI(primaryStage);

        gui.updateLineChartData();

        gui.show();

        new Thread(() -> {
            while (true) {
                try {
                    client.getData();
                    client.getStatus();
                    Platform.runLater(gui::updateStatus);
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

