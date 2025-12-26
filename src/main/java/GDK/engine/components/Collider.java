package GDK.engine.components;

import GDK.engine.*;
import javafx.scene.shape.Rectangle;

public class Collider extends Component {
    public Rectangle bounds;
    //private Line left, right, up, down;


    public Collider(GameObject gameObject, String[] args) {
        super(gameObject);
        /*
        left = Draw.line(Vector2.zero(), Vector2.zero(), 4, Color.RED);
        right = Draw.line(Vector2.zero(), Vector2.zero(), 4, Color.RED);
        up = Draw.line(Vector2.zero(), Vector2.zero(), 4, Color.RED);
        down = Draw.line(Vector2.zero(), Vector2.zero(), 4, Color.RED);

         */
        bounds = new Rectangle(
                Double.parseDouble(args[0]),
                Double.parseDouble(args[1]),
                Double.parseDouble(args[2]),
                Double.parseDouble(args[3])
                );
        System.out.println(bounds);
    }
    public Collider(GameObject gameObject) {
        super(gameObject);
        Transform tr = gameObject.transform;
        bounds = new Rectangle(0, 0, tr.size.x, tr.size.y);
    }

    public Rectangle getBounds(){
        return new Rectangle(
                gameObject.transform.position.x + bounds.getX(),
                gameObject.transform.position.y + bounds.getY(),
                bounds.getWidth(),
                bounds.getHeight()
                );
    }
    /*
    @Override
    public void update(){
        Rectangle bounds = getBounds();
        if (left == null) return;
        left.setStartX(bounds.getX()* Screen.scale.x);
        left.setEndX(bounds.getX()*Screen.scale.x);
        left.setStartY(bounds.getY()*Screen.scale.y);
        left.setEndY((bounds.getY()+bounds.getHeight())*Screen.scale.y);
        right.setStartX((bounds.getX()+bounds.getWidth())* Screen.scale.x);
        right.setEndX((bounds.getX()+bounds.getWidth())*Screen.scale.x);
        right.setStartY(bounds.getY()*Screen.scale.y);
        right.setEndY((bounds.getY()+bounds.getHeight())*Screen.scale.y);
        up.setStartX(bounds.getX()* Screen.scale.x);
        up.setEndX((bounds.getX()+bounds.getWidth())*Screen.scale.x);
        up.setStartY(bounds.getY()*Screen.scale.y);
        up.setEndY(bounds.getY()*Screen.scale.y);
        down.setStartX(bounds.getX()* Screen.scale.x);
        down.setEndX((bounds.getX()+bounds.getWidth())*Screen.scale.x);
        down.setStartY((bounds.getY()+bounds.getHeight())*Screen.scale.y);
        down.setEndY((bounds.getY()+bounds.getHeight())*Screen.scale.y);
    }*/

    public boolean intersects(Collider collider) {
        return collider.getBounds().intersects(gameObject.collider.getBounds().getLayoutBounds());
    }
}
