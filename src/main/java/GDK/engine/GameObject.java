package GDK.engine;

import GDK.engine.components.*;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;

public class GameObject {
    public Transform transform;
    public ImageComponent image;
    public Animator animator;
    public GameObject parent;
    public ArrayList<GameObject> childs = new ArrayList<>();
    public Collider collider;
    public String name;
    public ArrayList<Node> nodes = new ArrayList<>();
    public boolean collision;
    public Runnable updaterScript;
    private String parentName;
    public boolean isDynamic;
    public boolean isStart;
    public boolean isAwake;
    public boolean isDestroy;
    public Runnable updater;
    public ArrayList<Component> components;
    private double viewOrder;

    public GameObject(ArrayList<String> args, int index){
        name = args.get(0);
        collision = Boolean.parseBoolean(args.get(6));
        isDynamic = Boolean.parseBoolean(args.get(7));
        parentName = args.get(args.size()-1);
        transform = new Transform(
                this,
                args.get(2).split(","),
                args.get(3).split(","),
                index,
                args.get(4)
        );
        image = new ImageComponent(
                this,
                args.get(1),
                args.get(5)
        );
        animator = new Animator(this);
        if (args.size() < 10)
            components = getComponents("None");
        else
            components = getComponents(args.get(8));
        if (collision && collider == null) collider = new Collider(this);
        if (getComponent(Function.class) == null){
            Engine.startThreadForStatic(this);
        }
    }

    public static GameObject instantiate(String path) {
        try {
            GameObject inst = ReadGameData.readPrefab(path);
            Engine.addNewGameObjectInList(inst);
            return inst;
        }
        catch (IOException ex){
            System.out.print(ex.getMessage());
            return null;
        }
    }

    public void setParent(GameObject inst){
        parentName = "isSetting";
        parent = inst;
        transform.localPosition = transform.position.subtractVector(parent.transform.position);
        parent.childs.add(this);
    }

    public void loadComplete() {
        if (!parentName.equals("isSetting")) {
            if (parentName.equals("None"))
                parent = null;
            else {
                setParent(Engine.find(parentName));
            }
        }

        // Устанавливаем порядок отрисовки для всех узлов
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            n.setViewOrder(this.viewOrder);
            Main.addWidget(n);
        }
    }

    public void setViewOrder(double viewOrder){
        this.viewOrder = viewOrder;
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            n.setViewOrder(this.viewOrder);
        }
    }

    public double getViewOrder(){return viewOrder;}

    public void addComponent(Component comp){
        components.add(comp);
    }

    public void destroy(){
        destroyChild();
        Engine.destroy(this);
    }

    public void destroyChild(){
        for (int i = 0; i < childs.size(); i++) {
            GameObject child = childs.get(i);
            child.destroyChild();
            Engine.destroy(child);
        }
        childs.clear();
    }

    public void removeAllNode(){
        for (int i = 0; i < nodes.size(); i++){
            Node n = nodes.get(i);
            n.setVisible(false);
            Main.removeWidget(n);}
        nodes.clear();
    }

    public void updateComponents(){
        image.update();
        animator.updateAnimation();
        for (Component comp: components) {
            comp.update();
        }
    }

    public <T> T getComponent(Class<T> type){
        for (Component c: components) {
            if (c.getClass() == type){
                return (T)c;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getScript(Class<T> type){
        var comps = getComponents(Function.class);

        for (int i = 0; i < comps.size(); i++) {
            Function f = (Function)comps.get(i);
            if (type.isInstance(f.scControl)){
                return (T)f.scControl;
            }
        }
        return null;
    }

    public <T> ArrayList<Component> getComponents(Class<T> type){
        ArrayList<Component> comps = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (c.getClass() == type){
                comps.add(c);
            }
        }
        return comps;
    }


    public ArrayList<Component> getComponents (String value){
        if (value.startsWith("None")) return new ArrayList<Component>();
        ArrayList<Component> comps = new ArrayList<>();
        for (String c: value.split("\\$")) {
            String[] splComp = c.split("\\^,\\^");
            String[] compArgs = splComp[2].split("\\^;\\^");
            switch (splComp[0]) {
                case "Function" -> comps.add(new Function(this, compArgs));
                case "Color box" -> comps.add(new ColorBox(this, compArgs));
                case "Text" -> comps.add(new Text(this, compArgs));
                case "Collider" -> collider = new Collider(this, compArgs);
                case "Animation" -> animator.animations.add(new Animation(this, compArgs));
                case "OrderView" -> setViewOrder(Double.parseDouble(compArgs[0]));
            }
        }
        return comps;
    }


    /**
     * Устанавливает порядок отрисовки для дочерних объектов
     */
    public void setChildsViewOrder(double baseViewOrder) {
        for (int i = 0; i < childs.size(); i++) {
            GameObject child = childs.get(i);
            child.setViewOrder(baseViewOrder - (i + 1) * 0.1); // Дочерние объекты с небольшим смещением
        }
    }

    public boolean isCollision() {
        return collider != null && collision;
    }
}
