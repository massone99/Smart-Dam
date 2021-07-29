package application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import controller.HttpClient;
import controller.WaterRilevation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class GUI {

    private static final int WIDTH = 320;
    private static final int HEIGHT = 160;


    private final int NRIL = 20;
    private final int D1 = 100;
    private final int D2 = 40;


    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Stage primaryStage;
    private XYChart.Series<String, Double> series;
    private LineChart<String, Double> lc;
    private Text state;
    private Text damOpeningLabel;
    private Text damOpening;
    private Text damModeLabel;
    private Text damMode;

    private HBox lcBox;
    private VBox vbox;

    public GUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        init();
    }

    /**
     * Initializes the main elements of the GUI
     */
    public void init() {
        primaryStage.setTitle("Dam Dashboard");

        Text title = new Text();

        title.setText("Dam Dashboard");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 25));
        title.setTextAlignment(TextAlignment.CENTER);

        Text stateLabel = new Text();
        stateLabel.setText("Stato: ");
        stateLabel.setFont(Font.font("Tahoma", 20));

        state = new Text();
        state.setText("ND");
        state.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));

        this.series = new XYChart.Series<>();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        xAxis.setAnimated(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Water Level");

        lc = new LineChart(
                xAxis,
                yAxis
        );
        lc.getData().add(series);
        lc.setTitle("Rilevazioni livello idrometrico");
        lc.setLegendVisible(false);
        lc.setAnimated(false);

        lcBox = new HBox();
        showLineChart();
        hideLineChart();

        damOpeningLabel = new Text();
        damOpeningLabel.setText("Livello apertura diga: ");
        damOpeningLabel.setFont(Font.font("Tahoma", 20));

        damOpening = new Text();
        damOpening.setText("0");
        damOpening.setFont(Font.font("Tahoma", FontPosture.ITALIC, 20));

        damModeLabel = new Text();
        damModeLabel.setText("Dam Mode: ");
        damModeLabel.setFont(Font.font("Tahoma", 20));

        damMode = new Text();
        damMode.setText("AUTO");
        damMode.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));

        vbox = new VBox();
        vbox.getChildren().addAll(
                title,
                stateLabel,
                state,
                lcBox,
                damOpeningLabel,
                damOpening,
                damModeLabel,
                damMode);
        vbox.setAlignment(Pos.TOP_CENTER);
        hideDamLevel();

        Group content = new Group(vbox);

        VBox container = new VBox(content);
        VBox.setMargin(lcBox, new Insets(0, 0, 20, 0));
        container.setAlignment(Pos.TOP_CENTER);

        primaryStage.setScene(new Scene(container, 720, 600));
    }

    /**
     * Shows the GUI
     */
    public void show() {
        this.primaryStage.show();
    }

    /**
     * Hides the dam mode in the GUI
     */
    public void hideDamMode() {
        if (vbox.getChildren().contains(damMode) && vbox.getChildren().contains(damModeLabel)) {
            vbox.getChildren().remove(damMode);
            vbox.getChildren().remove(damModeLabel);
        }
    }

    /**
     * Shows the dam mode in the GUI
     */
    public void showDamMode() {
        if (!vbox.getChildren().contains(damMode) && !vbox.getChildren().contains(damModeLabel) ) {
            vbox.getChildren().addAll(damMode, damModeLabel);
        }
    }

    /**
     * Hides the dam level in the GUI
     */
    public void hideDamLevel() {
        if (vbox.getChildren().contains(damOpening) && vbox.getChildren().contains(damOpeningLabel)) {
            vbox.getChildren().remove(damOpeningLabel);
            vbox.getChildren().remove(damOpening);
        }
    }

    /**
     * Shows the dam level in the GUI
     */
    public void showDamLevel() {
        if (!vbox.getChildren().contains(damOpeningLabel) && !vbox.getChildren().contains(damOpening) ) {
            vbox.getChildren().addAll(damOpeningLabel, damOpening);
        }
    }

    /**
     * Updates the status label using the data received from the Back-end service
     */
    public void updateStatus() {
        try {
            switch (Main.STATUS) {
                case "normal":
                    state.setText("Normal");
                    state.setFill(Color.GREEN);
                    hideDamLevel();
                    hideLineChart();
                    hideDamMode();
                    break;
                case "pre-alarm":
                    state.setText("Pre-Alarm");
                    state.setFill(Color.ORANGE);
                    showLineChart();
                    updateLineChartData();
                    hideDamLevel();
                    hideDamMode();
                    break;
                case "alarm":
                    state.setText("Alarm");
                    state.setFill(Color.RED);
                    showLineChart();
                    updateLineChartData();
                    showDamLevel();
                    showDamMode();
                    //todo: update damLevel()
                    break;
            }
        } catch (NullPointerException ignored) { }
    }

    /**
     * Hides the line chart in the GUI
     */
    public void hideLineChart() {
        if (lcBox.getChildren().contains(lc)) {
            lcBox.getChildren().remove(0);
        }
    }

    /**
     * Shows the line chart in the GUI
     */
    public void showLineChart() {
        if (!lcBox.getChildren().contains(lc)) {
            lcBox.getChildren().addAll(lc);
            lcBox.setAlignment(Pos.CENTER);
        }
    }

    /**
     * Updates the line chart data using the JSON retrieved by the HTTPClient
     */
    public void updateLineChartData() {
        List<WaterRilevation> rilevations = new ArrayList<>();
        try {
            JsonReader reader = new JsonReader(new FileReader(HttpClient.DATA_PATH));
            rilevations = gson.fromJson(reader, new TypeToken<List<WaterRilevation>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (rilevations != null) {
            List<WaterRilevation> lastRilevations = new ArrayList<>();
            if (rilevations.size() <= NRIL) {
                lastRilevations = rilevations;
            } else {
                lastRilevations = rilevations.subList(rilevations.size() - NRIL, rilevations.size());
            }

            this.series.getData().clear();

            for (WaterRilevation ril : lastRilevations) {
                this.series.getData().add(ril.getXYChartData());
            }
        }
    }

}
