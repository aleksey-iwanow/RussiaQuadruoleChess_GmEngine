package GDK.engine;

public abstract class Component {
    public GameObject gameObject;

    public void update(){

    }

    public Component(GameObject gameObject){
        this.gameObject = gameObject;
    }
}
