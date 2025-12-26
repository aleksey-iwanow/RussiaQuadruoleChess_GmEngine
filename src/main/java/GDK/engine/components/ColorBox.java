package GDK.engine.components;

import GDK.engine.Component;
import GDK.engine.GameObject;
import GDK.engine.Screen;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ColorBox extends Component {
    public String color;
    public Rectangle square;
    public ColorBox(GameObject gameObject, String[] args){
        super(gameObject);
        color = args[0];
        square = new Rectangle(100,100,100,100);
        square.setFill(Color.web(color));
        //square.setStroke(Color.BLACK);
        //square.setStrokeWidth(2);
        gameObject.nodes.add(square);
    }

    @Override
    public void update(){
        square.setX((gameObject.transform.position.x)* Screen.scale.x);
        square.setY((gameObject.transform.position.y)*Screen.scale.y);
        square.setRotate(gameObject.transform.getGlobalAngle());
        square.setWidth((gameObject.transform.size.x)*Screen.scale.x);
        square.setHeight((gameObject.transform.size.y)*Screen.scale.y);
    }

}
