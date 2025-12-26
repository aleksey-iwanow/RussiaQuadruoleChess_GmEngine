package GDK.engine.components;

import GDK.engine.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Animation extends Component{
    public boolean looping;
    public boolean active;
    public boolean deleted;
    public String tag;
    public int speedMs;
    public ArrayList<Image> images = new ArrayList<>();
    private int indexImage=0;
    public Thread thread;

    private long delayTime;

    public ImageView view;

    public Animation(GameObject gameObject,
                     String[] paths,
                     boolean looping,
                     boolean active,
                     boolean deleted,
                     int speedMs,
                     String tag
                     ){

        super(gameObject);
        for (String path: paths) {
            images.add(ImageComponent.createImage(Config.PATH_PROJECT + path));
        }
        view = gameObject.image.imageView;
        this.looping = looping;
        this.active = active;
        this.deleted = deleted;
        this.speedMs = speedMs;
        this.tag = tag;
        if (active) gameObject.animator.startAnimation(this);
    }

    public Animation(GameObject gameObject, String[] args){
        this(gameObject,
                args[1].split("\\*&\\*"),
                Boolean.parseBoolean(args[2]),
                Boolean.parseBoolean(args[4]),
                Boolean.parseBoolean(args[5]),
                Integer.parseInt(args[3]),
                args[6]);
    }

    public void start(){
        delayTime = Time.current() + speedMs;
    }

    public void stop(){
        gameObject.image.setToDefault();
    }

    public boolean updateAnim(){
        long time = Time.current();
        if (time < delayTime) return true;

        delayTime = time + speedMs;
        view.setImage(images.get(indexImage));
        indexImage++;
        if (indexImage == images.size()){
            indexImage=0;
            if (!looping){
                if (deleted){
                    gameObject.destroy();
                    return true;
                }
                return false;
            }
        }
        return true;
    }
}
