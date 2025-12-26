package GDK.engine;

public class Config {
    public static final String PATH_STATIC = "src\\main\\java\\";
    public static final String PATH_PROJECT_LOCAL ="org\\games\\chess\\";
    public static final String PATH_PROJECT = PATH_STATIC + PATH_PROJECT_LOCAL;
    public static final String NAME_SCENE = PATH_PROJECT+"res\\main.scene";
    public static final String STATICASSETSPATH = "GDK\\engine\\assets\\";
    public static String PACKAGEGAMESNAME = "";
    public static final String TITLE = "GameEngine";
    public static final double SCALEFACTOR = 1;
    public static final double SPACEX = 8;
    public static final double SPACEY = 32;
    public static final double WIDTH = 800*SCALEFACTOR+SPACEX;
    public static final double HEIGHT = 800*SCALEFACTOR+SPACEY;
    public static final boolean FULLSCREEN = false;
    public static final boolean RESIZABLE = false;
    public static final boolean AUTOSCALE = false;
    public static final int FPS = 100;

}
