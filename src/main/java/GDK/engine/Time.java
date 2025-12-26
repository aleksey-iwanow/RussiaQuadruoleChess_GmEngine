package GDK.engine;

import java.time.LocalTime;

public class Time {
    public static long oldTime;
    public static long mainDelay;
    public static long fpsDelay = current() + 1000;
    public static long newDeltaTime;
    public static long oldDeltaTime;
    public static boolean dontSet = false;
    private static double deltaTime;
    public static double currentFps;
    private static double frames;

    public static long getFullTimeNumber() {return LocalTime.now().toNanoOfDay() / 1_000_000;}

    public static void setMainDelay(){
        mainDelay = current() + 1000/Config.FPS;
    }

    public static void addFrame(){
        frames++;
        if (current() >= fpsDelay){
            currentFps = frames;
            frames=0;
            fpsDelay = current()+1000;
        }
    }

    public static long current() {
        long time = getFullTimeNumber();
        if (oldTime > time)
            time = oldTime + getFullTimeNumber();
        oldTime = time;
        return time;

    }

    public static double deltaTime(){
        return deltaTime;
    }

    public static void setNewForDeltaTime(){
        if (dontSet) return;
        dontSet = true;
        newDeltaTime = current();
    }

    public static void setOldForDeltaTime(){
        dontSet = false;
        oldDeltaTime = current();
    }

    public static double getFps(){
        return currentFps;
    }

    public static void setDeltaTime() {
        deltaTime = ((double) (oldDeltaTime-newDeltaTime))/1000;
    }
}
