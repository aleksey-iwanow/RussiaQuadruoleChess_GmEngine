package GDK.engine;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Input {
    public static final Map<String, boolean[]> pressedKeys = new HashMap<>();
    public static final Map<String, boolean[]> pressedMouses = new HashMap<>(); // Теперь такой же формат как у клавиш
    public static final String[] MOUSE_NAMES = new String[]{"PRIMARY", "MIDDLE", "SECONDARY"};
    public static boolean isClear;
    public static Vector2 mousePosition;
    public static Vector2 mousePositionInt;
    public static String currentKey="Null";
    public static boolean block;

    public static boolean getKeyDown(String keyCode) {
        if (Input.block) return false;
        boolean result = pressedKeys.getOrDefault(keyCode, new boolean[]{false, false, false})[0];
        return result;
    }

    public static boolean getKey(String keyCode) {
        if (Input.block) return false;
        boolean[] values = pressedKeys.getOrDefault(keyCode, new boolean[]{false, false, false});
        boolean result = false;
        if (values[0] && !values[1]){
            pressedKeys.put(keyCode, new boolean[]{true, true, values[2]});
            result = true;
        }
        return result;
    }

    public static boolean getKeyUp(String keyCode) {
        if (Input.block) return false;
        boolean[] values = pressedKeys.getOrDefault(keyCode, new boolean[]{false, false, false});
        return values[2];
    }

    // Методы для мыши (аналогично клавишам)
    public static boolean getMouseButtonDown(int index) {
        if (Input.block) return false;
        String mouseName = MOUSE_NAMES[index];
        boolean result = pressedMouses.getOrDefault(mouseName, new boolean[]{false, false, false})[0];
        return result;
    }

    public static boolean getMouseButton(int index) {
        if (Input.block) return false;
        String mouseName = MOUSE_NAMES[index];
        boolean[] values = pressedMouses.getOrDefault(mouseName, new boolean[]{false, false, false});
        boolean result = false;
        if (values[0] && !values[1]){
            pressedMouses.put(mouseName, new boolean[]{true, true, values[2]});
            result = true;
        }
        return result;
    }

    public static boolean getMouseButtonUp(int index) {
        if (Input.block) return false;
        String mouseName = MOUSE_NAMES[index];
        boolean[] values = pressedMouses.getOrDefault(mouseName, new boolean[]{false, false, false});
        return values[2];
    }

    public static void updateMousePosition(double[] posStage){
        var mousePosPointer = MouseInfo.getPointerInfo().getLocation();
        Input.mousePosition = new Vector2((mousePosPointer.getX() - posStage[0]) / Screen.scale.x - Config.SPACEX,
                (mousePosPointer.getY() - posStage[1])  / Screen.scale.y - Config.SPACEY);
        Input.mousePositionInt = mousePosition.roundVector();
    }

    public static void clearReleasedKeys(){
        if (isClear) return;
        isClear = true;

        // Очищаем клавиши
        for (var code: pressedKeys.keySet()) {
            boolean[] values = Input.pressedKeys.getOrDefault(code, new boolean[]{false, false, false});
            Input.pressedKeys.put(code, new boolean[]{values[0], values[1], false});
        }

        // Очищаем мышь
        for (String mouseName : MOUSE_NAMES) {
            boolean[] values = Input.pressedMouses.getOrDefault(mouseName, new boolean[]{false, false, false});
            Input.pressedMouses.put(mouseName, new boolean[]{values[0], values[1], false});
        }
    }
}