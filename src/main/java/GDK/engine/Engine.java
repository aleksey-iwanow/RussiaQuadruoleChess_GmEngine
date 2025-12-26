package GDK.engine;

import GDK.engine.components.Function;
import javafx.application.Platform;

import java.util.ArrayList;

public class Engine {
    public static ArrayList<GameObject> gameObjects;
    public static boolean run=true;
    public static void startThreadForObject(ScriptControl scriptControl){

        Runnable updater = () -> {
            if (!scriptControl.gameObject.isAwake){
                scriptControl.gameObject.loadComplete();
                scriptControl.awake();
                scriptControl.gameObject.isAwake = true;
            }
            scriptControl.gameObject.transform.update();
            if (scriptControl.gameObject.transform.isHover(Input.mousePosition)){
                if (!scriptControl.isHover)
                    scriptControl.onHoverOnce();
                scriptControl.isHover = true;
                scriptControl.onHover();
            }
            else {
                if (scriptControl.isHover) {
                    scriptControl.onHoverExit();
                }
                scriptControl.isHover = false;
            }

            if (scriptControl.gameObject.isCollision()) {
                Thread thread = new Thread(() -> {
                    ArrayList<GameObject> gmObjs = new ArrayList<>();
                    for (int i = 0; i < gameObjects.size(); i++) {
                        GameObject gm = gameObjects.get(i);

                        if (gm != null
                                && gm != scriptControl.gameObject
                                && gm.isCollision()
                                && scriptControl.gameObject.collider.intersects(gm.collider)) {
                            gmObjs.add(gm);
                        }
                    }
                    scriptControl.collisionGameObjects = gmObjs;
                    scriptControl.onCollisionStay(gmObjs);
                    if (gmObjs.isEmpty()){
                        scriptControl.onCollisionExit(gmObjs);
                    }
                    else{
                        scriptControl.onCollisionEnter(gmObjs);
                    }
                });
                Platform.runLater(thread);
            }
            scriptControl.gameObject.updateComponents();
            Input.clearReleasedKeys();
        };
        Runnable updaterScript = () -> {
            if (!scriptControl.gameObject.isStart){
                scriptControl.start();
                scriptControl.gameObject.isStart = true;
            }
            scriptControl.update();
            scriptControl.lateUpdate();
        };

        scriptControl.gameObject.updater = updater;
        scriptControl.gameObject.updaterScript = updaterScript;
        Main.listRunnable.add(updater);
        Main.listUpdateScripts.add(updaterScript);
    }

    public static void startThreadForStatic(GameObject gameObject){

        Runnable updater = () -> {
            if (!gameObject.isStart) {
                gameObject.isStart = true;
                gameObject.loadComplete();
            }
            gameObject.transform.update();
            gameObject.updateComponents();
        };
        gameObject.updater = updater;
        Main.listRunnable.add(updater);

    }

    public Engine(ArrayList<GameObject> gameObjects){
        Engine.gameObjects = gameObjects;
        activeFuncComponent();
    }

    public static void destroy(GameObject gameObject){
        if (gameObject.isDestroy) return;
        gameObject.removeAllNode();
        Main.listRunnable.remove(gameObject.updater);
        if (gameObject.updaterScript != null)
            Main.listUpdateScripts.remove(gameObject.updaterScript);
        gameObjects.remove(gameObject);
    }

    public static GameObject find(String name){
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gm = gameObjects.get(i);
            if (gm.name.equals(name)) return gm;
        }
        return null;
    }

    public static ArrayList<GameObject> findAll(String name){
        ArrayList<GameObject> result = new ArrayList<>();
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gm = gameObjects.get(i);
            if (gm.name.equals(name))
                result.add(gm);
        }
        return result;
    }

    public static <T> ArrayList<T> findAllWithScript(Class<T> type_){
        ArrayList<T> result = new ArrayList<>();
        for (int i = 0; i < gameObjects.size(); i++) {
            T script = gameObjects.get(i).getScript(type_);
            if (script != null)
                result.add(script);
        }
        return result;
    }

    public static <T> T findWithScript(Class<T> type_){
        var res = findAllWithScript(type_);
        return res.isEmpty() ? null : res.getFirst();
    }


    private void activeFuncComponent(){
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            activeFuncInGameObject(gameObject);
        }
    }

    private static void activeFuncInGameObject(GameObject gameObject){
        ArrayList<Component> components = gameObject.getComponents(Function.class);
        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);
            if (comp != null) ((Function)comp).active();
        }
    }

    public static void addNewGameObjectInList(GameObject gameObject){
        gameObjects.add(gameObject);
        activeFuncInGameObject(gameObject);
    }
}
