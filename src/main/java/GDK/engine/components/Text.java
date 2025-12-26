package GDK.engine.components;

import GDK.engine.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.geometry.Pos;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Text extends Component {
    public String text;
    public double sizeFont;
    public Color color;
    public Label textLabel;
    public Font currentFont;
    public TextAlignment alignment = TextAlignment.LEFT;
    public FontPosture fontPosture = FontPosture.REGULAR;
    public FontWeight fontWeight = FontWeight.NORMAL;

    public enum FontWeight {
        NORMAL, BOLD
    }

    public enum TextAlignment {
        LEFT_UP, LEFT, LEFT_DOWN,
        CENTER_UP, CENTER, CENTER_DOWN,
        RIGHT_UP, RIGHT, RIGHT_DOWN
    }

    // Алфавит для выравнивания
    private static final Map<TextAlignment, Pos> ALIGNMENT_MAP = createAlignmentMap();

    private static Map<TextAlignment, Pos> createAlignmentMap() {
        Map<TextAlignment, Pos> map = new HashMap<>();
        map.put(TextAlignment.LEFT_UP, Pos.TOP_LEFT);
        map.put(TextAlignment.LEFT, Pos.CENTER_LEFT);
        map.put(TextAlignment.LEFT_DOWN, Pos.BOTTOM_LEFT);
        map.put(TextAlignment.CENTER_UP, Pos.TOP_CENTER);
        map.put(TextAlignment.CENTER, Pos.CENTER);
        map.put(TextAlignment.CENTER_DOWN, Pos.BOTTOM_CENTER);
        map.put(TextAlignment.RIGHT_UP, Pos.TOP_RIGHT);
        map.put(TextAlignment.RIGHT, Pos.CENTER_RIGHT);
        map.put(TextAlignment.RIGHT_DOWN, Pos.BOTTOM_RIGHT);
        return map;
    }

    public Text(GameObject gameObject, String[] args) {
        super(gameObject);
        text = args[0];
        sizeFont = Double.parseDouble(args[1]);
        color = Color.web(args[2]);

        textLabel = new Label();
        textLabel.setViewOrder(gameObject.getViewOrder());
        textLabel.setTextFill(color);

        textLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        textLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);

        setFont("Arial", FontPosture.REGULAR, sizeFont);

        if (args.length > 3) {
            setAlignment(TextAlignment.valueOf(args[3].toUpperCase()));
        } else {
            setAlignment(TextAlignment.LEFT);
        }

        gameObject.nodes.add(textLabel);
    }

    public void setColor(Color color) {
        this.color = color;
        textLabel.setTextFill(color);
    }

    public void setColor(String hexColor) {
        try {
            this.color = Color.web(hexColor);
            textLabel.setTextFill(this.color);
        } catch (IllegalArgumentException e) {
            System.err.println("Неверный формат цвета: " + hexColor);
        }
    }

    public void setColor(int red, int green, int blue) {
        this.color = Color.rgb(red, green, blue);
        textLabel.setTextFill(this.color);
    }

    public void setColor(double red, double green, double blue) {
        this.color = new Color(red, green, blue, 1.0);
        textLabel.setTextFill(this.color);
    }

    public void setColor(double red, double green, double blue, double opacity) {
        this.color = new Color(red, green, blue, opacity);
        textLabel.setTextFill(this.color);
    }

    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
        applyAlignment();
    }

    public void setAlignment(String alignment) {
        try {
            setAlignment(TextAlignment.valueOf(alignment.toUpperCase()));
        } catch (IllegalArgumentException e) {
            System.err.println("Неизвестное выравнивание: " + alignment + ". Используется LEFT");
            setAlignment(TextAlignment.LEFT);
        }
    }

    private void applyAlignment() {
        Pos javafxPos = ALIGNMENT_MAP.get(alignment);

        textLabel.setAlignment(Objects.requireNonNullElse(javafxPos, Pos.CENTER_LEFT));
    }

    public void setFontSize(double sizeFont){
        setFont(currentFont.getName(), fontPosture, fontWeight, sizeFont);
    }

    public void setFont(String fontName, FontPosture fontPosture, FontWeight fontWeight, double sizeFont){
        this.sizeFont = sizeFont;
        this.fontPosture = fontPosture;
        this.fontWeight = fontWeight;
        currentFont = Font.font(fontName, fontWeight == FontWeight.BOLD ? javafx.scene.text.FontWeight.BOLD : javafx.scene.text.FontWeight.NORMAL, fontPosture, sizeFont);
        textLabel.setFont(currentFont);
    }

    public void setFont(String fontName, FontPosture fontPosture, double sizeFont){
        setFont(fontName, fontPosture, FontWeight.NORMAL, sizeFont);
    }

    public void setBold(boolean bold) {
        setFont(currentFont.getName(), fontPosture, bold ? FontWeight.BOLD : FontWeight.NORMAL, sizeFont);
    }

    public void setFontWeight(FontWeight weight) {
        setFont(currentFont.getName(), fontPosture, weight, sizeFont);
    }

    public boolean isBold() {
        return fontWeight == FontWeight.BOLD;
    }

    public FontWeight getFontWeight() {
        return fontWeight;
    }

    @Override
    public void update() {
        textLabel.setText(text);

        updatePosition();
        textLabel.setRotate(gameObject.transform.getGlobalAngle());
    }

    private void updatePosition() {
        double x = gameObject.transform.position.x * Screen.scale.x;
        double y = gameObject.transform.position.y * Screen.scale.y;

        // Для правильного позиционирования нужно учитывать размеры текста
        textLabel.setLayoutX(x);
        textLabel.setLayoutY(y);

        // Устанавливаем размеры трансформа для вычисления смещения
        if (gameObject.transform.size != null) {
            textLabel.setPrefWidth(gameObject.transform.size.x * Screen.scale.x);
            textLabel.setPrefHeight(gameObject.transform.size.y * Screen.scale.y);
        }
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

    public String getAlignmentAsString() {
        return alignment.name().toLowerCase();
    }

    public Color getColor() {
        return color;
    }
}