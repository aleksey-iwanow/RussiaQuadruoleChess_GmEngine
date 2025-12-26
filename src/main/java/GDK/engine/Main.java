package GDK.engine;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Main extends Application {
    private static final Duration UPDATE_INTERVAL = Duration.millis(1);
    public static Group groupWidgets;
    public static ArrayList<Runnable> listRunnable = new ArrayList<>();
    public static ArrayList<Runnable> listUpdateScripts = new ArrayList<>();

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        launch(args);
    }

    public void initEngine(){
        ReadGameData rgData = new ReadGameData(Config.NAME_SCENE);
        try {
            new Engine(rgData.readFile());
        }
        catch (IOException ex){
            System.out.print(ex.getMessage());
        }
    }

    public static void addWidget(Node widget) {
        Runnable r = () -> {
            if (widget != null && !groupWidgets.getChildren().contains(widget)) {
                groupWidgets.getChildren().add(widget);
            }
        };
        Platform.runLater(r);
    }

    public static void removeWidget(Node widget) {
        Runnable r = () -> {
            if (widget != null)
                groupWidgets.getChildren().remove(widget);
        };
        Platform.runLater(r);
    }

    public static Runnable startCoroutine(CoroutineObject coroutineObject){
        Runnable runnable = () -> {
            if (Time.current() >= coroutineObject.delay){
                coroutineObject.method.run();
                coroutineObject.delay = coroutineObject.timeMs + Time.current();
                if (coroutineObject.isOneShot){
                    stopCoroutine(coroutineObject.runnable);
                }
            }
        };
        coroutineObject.runnable = runnable;
        listRunnable.add(runnable);
        return runnable;
    }

    public static void stopCoroutine(Runnable coroutineRunnable){
        listRunnable.remove(coroutineRunnable);
    }

    @Override
    public void stop(){
        // закрытие
    }

    public void updateElements(Stage stage){
        Time.setNewForDeltaTime();
        if (Time.current() >= Time.mainDelay){
            Input.updateMousePosition(new double[] {stage.getX(), stage.getY()});

            for (int i = 0; i< listRunnable.size(); i++) {
                var runnable = listRunnable.get(i);
                if (runnable != null) runnable.run();
            }
            for (int i = 0; i< listUpdateScripts.size(); i++) {
                var runnable = listUpdateScripts.get(i);
                if(runnable != null) runnable.run();
            }
            Time.setOldForDeltaTime();
            Time.setDeltaTime();
            Time.setMainDelay();
            Time.addFrame();
        }

    }

    @Override
    public void start(Stage stage) {

        groupWidgets = new Group();
        Scene scene = new Scene(groupWidgets);
        scene.setFill(new Color(0.1f, 0.1f, 0.1f, 1.0f));
        if (Config.FULLSCREEN) {
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
        }
        else{
            stage.setWidth(Config.WIDTH);
            stage.setHeight(Config.HEIGHT);
        }
        stage.setResizable(Config.RESIZABLE);
        stage.setTitle(Config.TITLE);

        new Screen(stage, scene);
        initEngine(); //важно, чтобы после инициализации "groupWidgets" !!!
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> updateElements(stage)), new KeyFrame(UPDATE_INTERVAL));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        scene.setOnKeyPressed(event -> {
            String code = event.getCode().getName();
            Input.currentKey = code;
            boolean[] values = Input.pressedKeys.getOrDefault(code, new boolean[]{false, false, false});
            Input.pressedKeys.put(code, new boolean[]{true, values[1], false});
        });

        scene.setOnMousePressed(event -> {
            String buttonName = event.getButton().name();
            boolean[] values = Input.pressedMouses.getOrDefault(buttonName, new boolean[]{false, false, false});
            Input.pressedMouses.put(buttonName, new boolean[]{true, values[1], false});
        });

        scene.setOnMouseReleased(event -> {
            String buttonName = event.getButton().name();
            boolean[] values = Input.pressedMouses.getOrDefault(buttonName, new boolean[]{false, false, false});
            Input.pressedMouses.put(buttonName, new boolean[]{false, false, true});
        });

        scene.setOnKeyReleased(event -> {
            Input.isClear = false;
            Input.pressedKeys.put(event.getCode().getName(), new boolean[]{false, false, true});
        });

        stage.setScene(scene);
        stage.show();

        if (Config.AUTOSCALE)
            Screen.setScale(new Vector2(stage.getWidth() / Config.WIDTH, stage.getWidth() / Config.WIDTH));
        else
            Screen.setScale(new Vector2(Config.SCALEFACTOR, Config.SCALEFACTOR));
    }

}