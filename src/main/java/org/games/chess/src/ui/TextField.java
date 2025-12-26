package org.games.chess.src.ui;

import GDK.engine.*;
import GDK.engine.components.CanvasRenderer;
import GDK.engine.components.Text;
import javafx.scene.paint.Color;
import org.games.chess.src.GameConfig;

/**
 * Текстовое поле с фоном для отображения информации в UI.
 * Используется для таймера и имени игрока.
 */
public class TextField extends ScriptControl {

    /**
     * Внутренние отступы поля от краев
     */
    public final Vector2 padding = new Vector2(10,10);

    /**
     * Компонент текста для отображения строк
     */
    public Text textComponent;

    /**
     * Canvas для отрисовки фона поля
     */
    private CanvasRenderer renderer;

    /**
     * Метод инициализации, вызываемый при создании текстового поля.
     * Настраивает внешний вид и размеры поля.
     */
    @Override
    public void awake() {
        textComponent = getComponent(Text.class);
        textComponent.setFontSize(30);
        textComponent.setAlignment(Text.TextAlignment.CENTER);
        textComponent.setColor(Color.BLACK);

        transform.setSize(new Vector2(100, GameConfig.PADDING_UP-GameConfig.PADDING_BOTTOM - padding.y*2));

        renderer = CanvasRenderer.createCanvasObject("TimerCanvas", transform.position, 1);
        renderer.setColor(GameConfig.DARK_WHITE);
        renderer.drawFilledRect(Vector2.zero(), transform.size);
        renderer.setColor(GameConfig.DARK_WHITE_2);
        renderer.setLineWidth(2);
        renderer.drawRect(Vector2.zero(), transform.size);
    }

    /**
     * Устанавливает текст для отображения в поле.
     *
     * @param text текст для отображения
     */
    public void setText(String text) {
        textComponent.text = text;
    }
}