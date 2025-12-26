package GDK.engine;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Screen {
    public static Stage stage;
    public static Scene scene;
    public static Vector2 scale=Vector2.one();
    public Screen(Stage stage, Scene scene){
        Screen.stage = stage;
        Screen.scene = scene;
    }

    public static int width() { return (int)stage.getWidth(); }
    public static int height() { return (int)stage.getHeight(); }

    public static void setWidth(int width) { stage.setWidth(width * Config.SCALEFACTOR + Config.SPACEX);}
    public static void setHeight(int height){ stage.setHeight(height * Config.SCALEFACTOR + Config.SPACEY);}

    public static Vector2 size() { return new Vector2 ((int)stage.getWidth(), (int)stage.getHeight()); }

    public static void setBackground(Color color){
        scene.setFill(color);
    }
    public static void setScale(Vector2 scale) { Screen.scale = scale; }
}
