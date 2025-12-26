package GDK.engine;

public class CoroutineObject{
    public long delay;
    public int timeMs;
    public Runnable method;
    public Runnable runnable;
    public boolean isOneShot;

    public CoroutineObject(Runnable method, int timeMs, boolean playOnStart, boolean isOneShot){
        this.method = method;
        this.timeMs = timeMs;
        this.isOneShot = isOneShot;
        if (!playOnStart)
            delay = Time.current() + timeMs;
    }

    public CoroutineObject(Runnable method, int timeMs, boolean playOnStart){
        this(method, timeMs, playOnStart, false);
    }
}