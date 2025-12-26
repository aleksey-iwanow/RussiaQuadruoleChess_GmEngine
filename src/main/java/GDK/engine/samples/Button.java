package GDK.engine.samples;

import GDK.engine.Input;
import GDK.engine.ScriptControl;
import GDK.engine.Vector2;
import GDK.engine.components.CanvasRenderer;
import GDK.engine.components.Text;
import javafx.scene.paint.Color;

public class Button extends ScriptControl {

    private CanvasRenderer canvas;
    private Text textComponent;
    private Runnable onClick;

    private Color normalColor = Color.valueOf("#80d180");
    private Color hoverColor = Color.valueOf("#59ab59");
    private Color pressedColor = Color.valueOf("#59ab59");
    @Override
    public void awake() {
        gameObject.setViewOrder(-100);
        canvas = CanvasRenderer.createCanvasObject("button_canvas", transform.position, gameObject.getViewOrder()+1);
        canvas.gameObject.setParent(gameObject);
        textComponent = getComponent(Text.class);

        if (textComponent != null) {
            textComponent.setAlignment(Text.TextAlignment.CENTER);
            textComponent.setColor(Color.BLACK);
            textComponent.setFontWeight(Text.FontWeight.BOLD);
        }

        renderNormal();
    }

    public void setText(String text) {
        if (textComponent != null) {
            textComponent.text = text;
        }
    }

    public void setOnClick(Runnable action) {
        this.onClick = action;
    }

    @Override
    public void onHover() {
        if (Input.getMouseButton(0) && onClick != null) {
            renderPressed();
            onClick.run();
        }
    }

    @Override
    public void onHoverExit() {
        renderNormal();
    }

    @Override
    public void onHoverOnce() {
        renderHover();
    }

    private void renderNormal() {
        renderButton(normalColor);
    }

    private void renderHover() {
        renderButton(hoverColor);
    }

    private void renderPressed() {
        renderButton(pressedColor);
    }

    private void renderButton(Color color) {
        if (canvas == null) return;

        canvas.clear();
        canvas.setColor(color);
        canvas.drawFilledRect(Vector2.zero(), transform.size);

        canvas.setColor(Color.BLACK);
        canvas.setLineWidth(2);
        canvas.drawRect(Vector2.zero(), transform.size);
    }
}