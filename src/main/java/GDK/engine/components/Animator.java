package GDK.engine.components;

import GDK.engine.Component;
import GDK.engine.GameObject;

import java.util.ArrayList;

public class Animator extends Component{
    public ArrayList<Animation> animations = new ArrayList<Animation>();
    public Animation currentAnimation;

    public Animator(GameObject gameObject){
        super(gameObject);
    }

    public void startAnimation(String tag){
        Animation animation = getAnimationByTag(tag);
        if (animation == null) return;
        currentAnimation = animation;
    }

    public void startAnimation(Animation animation){
        currentAnimation = animation;
        currentAnimation.start();
    }

    public void stopAnimation(){
        currentAnimation.stop();
        currentAnimation = null;
    }

    public void updateAnimation(){
        if (currentAnimation != null){
            if (!currentAnimation.updateAnim()){
                stopAnimation();
            }
        }

    }

    private Animation getAnimationByTag(String tag){
        for (int i = 0; i < animations.size(); i++) {
            Animation anim = animations.get(i);
            if (anim.tag.equals(tag)){
                return anim;
            }
        }
        return null;
    }
}
