package GDK.engine.components;

import GDK.engine.*;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Система рисования базовых фигур с управлением z-order и привязкой к GameObject
 */
public class CanvasRenderer extends Component {
    public enum TextAlignment {
        LEFT_UP, LEFT, LEFT_DOWN,
        CENTER_UP, CENTER, CENTER_DOWN,
        RIGHT_UP, RIGHT, RIGHT_DOWN
    }

    private Color currentColor = Color.BLACK;
    private double currentLineWidth = 1.0;
    private double currentOpacity = 1.0;
    private TextAlignment textAlignment = TextAlignment.CENTER;

    // Маппинг для выравнивания текста
    private static final Map<TextAlignment, javafx.scene.text.TextAlignment> HORIZONTAL_ALIGNMENT_MAP = createHorizontalAlignmentMap();
    private static final Map<TextAlignment, VPos> VERTICAL_ALIGNMENT_MAP = createVerticalAlignmentMap();
    private Font currentFont = Font.font("Arial", 12);

    /**
     * Устанавливает шрифт для текста
     */
    public void setFont(Font font) {
        this.currentFont = font;
    }

    /**
     * Устанавливает шрифт по имени и размеру
     */
    public void setFont(String family, double size) {
        this.currentFont = Font.font(family, size);
    }

    /**
     * Устанавливает размер шрифта
     */
    public void setFontSize(double size) {
        if (currentFont != null) {
            this.currentFont = Font.font(currentFont.getFamily(), size);
        }
    }

    private static Map<TextAlignment, javafx.scene.text.TextAlignment> createHorizontalAlignmentMap() {
        Map<TextAlignment, javafx.scene.text.TextAlignment> map = new HashMap<>();
        map.put(TextAlignment.LEFT_UP, javafx.scene.text.TextAlignment.LEFT);
        map.put(TextAlignment.LEFT, javafx.scene.text.TextAlignment.LEFT);
        map.put(TextAlignment.LEFT_DOWN, javafx.scene.text.TextAlignment.LEFT);
        map.put(TextAlignment.CENTER_UP, javafx.scene.text.TextAlignment.CENTER);
        map.put(TextAlignment.CENTER, javafx.scene.text.TextAlignment.CENTER);
        map.put(TextAlignment.CENTER_DOWN, javafx.scene.text.TextAlignment.CENTER);
        map.put(TextAlignment.RIGHT_UP, javafx.scene.text.TextAlignment.RIGHT);
        map.put(TextAlignment.RIGHT, javafx.scene.text.TextAlignment.RIGHT);
        map.put(TextAlignment.RIGHT_DOWN, javafx.scene.text.TextAlignment.RIGHT);
        return map;
    }

    private static Map<TextAlignment, VPos> createVerticalAlignmentMap() {
        Map<TextAlignment, VPos> map = new HashMap<>();
        map.put(TextAlignment.LEFT_UP, VPos.TOP);
        map.put(TextAlignment.CENTER_UP, VPos.TOP);
        map.put(TextAlignment.RIGHT_UP, VPos.TOP);
        map.put(TextAlignment.LEFT, VPos.CENTER);
        map.put(TextAlignment.CENTER, VPos.CENTER);
        map.put(TextAlignment.RIGHT, VPos.CENTER);
        map.put(TextAlignment.LEFT_DOWN, VPos.BOTTOM);
        map.put(TextAlignment.CENTER_DOWN, VPos.BOTTOM);
        map.put(TextAlignment.RIGHT_DOWN, VPos.BOTTOM);
        return map;
    }
    public CanvasRenderer(GameObject gameObject) {
        super(gameObject);
    }

    /**
     * Устанавливает выравнивание текста
     */
    public void setTextAlignment(TextAlignment alignment) {
        this.textAlignment = alignment;
    }

    /**
     * Устанавливает выравнивание текста из строки
     */
    public void setTextAlignment(String alignment) {
        try {
            setTextAlignment(TextAlignment.valueOf(alignment.toUpperCase()));
        } catch (IllegalArgumentException e) {
            System.err.println("Неизвестное выравнивание: " + alignment + ". Используется CENTER");
            setTextAlignment(TextAlignment.CENTER);
        }
    }

    /**
     * Очищает все нарисованные фигуры
     */
    public void clear() {
        for (Node shape : gameObject.nodes) {
            Main.removeWidget(shape);
        }
        gameObject.nodes.clear();
    }

    /**
     * Устанавливает цвет рисования
     */
    public void setColor(Color color) {
        this.currentColor = color;
    }

    /**
     * Устанавливает ширину линии с учетом scale factor
     */
    public void setLineWidth(double width) {
        this.currentLineWidth = width * Config.SCALEFACTOR;
    }

    /**
     * Устанавливает прозрачность
     */
    public void setOpacity(double opacity) {
        this.currentOpacity = opacity;
    }

    /**
     * Рисует линию с учетом scale factor и позиции канваса
     */
    public Line drawLine(double startX, double startY, double endX, double endY) {
        return drawLine(new Vector2(startX, startY), new Vector2(endX, endY));
    }

    public Line drawLine(Vector2 start, Vector2 end) {
        Vector2 canvasPos = gameObject.transform.position;
        Line line = new Line(
                (canvasPos.x + start.x) * Config.SCALEFACTOR,
                (canvasPos.y + start.y) * Config.SCALEFACTOR,
                (canvasPos.x + end.x) * Config.SCALEFACTOR,
                (canvasPos.y + end.y) * Config.SCALEFACTOR
        );
        applyStyle(line);
        addShape(line);
        return line;
    }

    /**
     * Рисует прямоугольник с учетом scale factor и позиции канваса
     */
    public Rectangle drawRect(double x, double y, double width, double height) {
        return drawRect(new Vector2(x, y), new Vector2(width, height));
    }

    public Rectangle drawRect(Vector2 position, Vector2 size) {
        Vector2 canvasPos = gameObject.transform.position;
        Rectangle rect = new Rectangle(
                (canvasPos.x + position.x) * Config.SCALEFACTOR,
                (canvasPos.y + position.y) * Config.SCALEFACTOR,
                size.x * Config.SCALEFACTOR,
                size.y * Config.SCALEFACTOR
        );
        applyStyle(rect);
        addShape(rect);
        return rect;
    }

    /**
     * Рисует закрашенный прямоугольник с учетом scale factor и позиции канваса
     */
    public Rectangle drawFilledRect(double x, double y, double width, double height) {
        return drawFilledRect(new Vector2(x, y), new Vector2(width, height));
    }

    public Rectangle drawFilledRect(Vector2 position, Vector2 size) {
        Vector2 canvasPos = gameObject.transform.position;
        Rectangle rect = new Rectangle(
                (canvasPos.x + position.x) * Config.SCALEFACTOR,
                (canvasPos.y + position.y) * Config.SCALEFACTOR,
                size.x * Config.SCALEFACTOR,
                size.y * Config.SCALEFACTOR
        );
        rect.setFill(currentColor);
        rect.setStroke(null);
        rect.setOpacity(currentOpacity);
        addShape(rect);
        return rect;
    }

    /**
     * Рисует круг с учетом scale factor и позиции канваса
     */
    public Circle drawCircle(double centerX, double centerY, double radius) {
        return drawCircle(new Vector2(centerX, centerY), radius);
    }

    public Circle drawCircle(Vector2 center, double radius) {
        Vector2 canvasPos = gameObject.transform.position;
        Circle circle = new Circle(
                (canvasPos.x + center.x) * Config.SCALEFACTOR,
                (canvasPos.y + center.y) * Config.SCALEFACTOR,
                radius * Config.SCALEFACTOR
        );
        applyStyle(circle);
        addShape(circle);
        return circle;
    }

    /**
     * Рисует закрашенный круг с учетом scale factor и позиции канваса
     */
    public Circle drawFilledCircle(double centerX, double centerY, double radius) {
        return drawFilledCircle(new Vector2(centerX, centerY), radius);
    }

    public Circle drawFilledCircle(Vector2 center, double radius) {
        Vector2 canvasPos = gameObject.transform.position;
        Circle circle = new Circle(
                (canvasPos.x + center.x) * Config.SCALEFACTOR,
                (canvasPos.y + center.y) * Config.SCALEFACTOR,
                radius * Config.SCALEFACTOR
        );
        circle.setFill(currentColor);
        circle.setStroke(null);
        circle.setOpacity(currentOpacity);
        addShape(circle);
        return circle;
    }

    /**
     * Рисует эллипс с учетом scale factor и позиции канваса
     */
    public Ellipse drawEllipse(double centerX, double centerY, double radiusX, double radiusY) {
        return drawEllipse(new Vector2(centerX, centerY), radiusX, radiusY);
    }

    public Ellipse drawEllipse(Vector2 center, double radiusX, double radiusY) {
        Vector2 canvasPos = gameObject.transform.position;
        Ellipse ellipse = new Ellipse(
                (canvasPos.x + center.x) * Config.SCALEFACTOR,
                (canvasPos.y + center.y) * Config.SCALEFACTOR,
                radiusX * Config.SCALEFACTOR,
                radiusY * Config.SCALEFACTOR
        );
        applyStyle(ellipse);
        addShape(ellipse);
        return ellipse;
    }

    /**
     * Рисует многоугольник с учетом scale factor и позиции канваса
     */
    public Polygon drawPolygon(double[] points) {
        double[] scaledPoints = scalePoints(points);
        Polygon polygon = new Polygon(scaledPoints);
        applyStyle(polygon);
        addShape(polygon);
        return polygon;
    }

    /**
     * Рисует закрашенный многоугольник с учетом scale factor и позиции канваса
     */
    public Polygon drawFilledPolygon(double[] points) {
        double[] scaledPoints = scalePoints(points);
        Polygon polygon = new Polygon(scaledPoints);
        polygon.setFill(currentColor);
        polygon.setStroke(null);
        polygon.setOpacity(currentOpacity);
        addShape(polygon);
        return polygon;
    }

    /**
     * Масштабирует точки многоугольника с учетом scale factor и позиции канваса
     */
    private double[] scalePoints(double[] points) {
        Vector2 canvasPos = gameObject.transform.position;
        double[] scaledPoints = new double[points.length];
        for (int i = 0; i < points.length; i++) {
            // Четные индексы - X координаты, нечетные - Y координаты
            if (i % 2 == 0) {
                scaledPoints[i] = (canvasPos.x + points[i]) * Config.SCALEFACTOR;
            } else {
                scaledPoints[i] = (canvasPos.y + points[i]) * Config.SCALEFACTOR;
            }
        }
        return scaledPoints;
    }

    public Text drawText(String text, double x, double y) {
        return drawText(text, new Vector2(x, y));
    }

    public Text drawText(String text, Vector2 position) {
        Vector2 canvasPos = gameObject.transform.position;

        // Создаем текстовый узел
        Text textNode = new Text(text);

        // Сначала устанавливаем шрифт и получаем реальные размеры текста
        if (currentFont != null) {
            textNode.setFont(currentFont);
        }

        // Принудительно вычисляем размеры текста
        textNode.applyCss();

        // Получаем реальные размеры текста
        double textWidth = textNode.getLayoutBounds().getWidth();
        double textHeight = textNode.getLayoutBounds().getHeight();

        // Рассчитываем позицию так, чтобы центр текста был в указанной точке
        double centerX = (canvasPos.x + position.x) * Config.SCALEFACTOR;
        double centerY = (canvasPos.y + position.y) * Config.SCALEFACTOR;

        // Позиционируем так, чтобы центр текста был в (centerX, centerY)
        double textX = centerX - textWidth / 2;
        double textY = centerY + textHeight / 4; // + потому что в JavaFX Y идет сверху вниз

        textNode.setX(textX);
        textNode.setY(textY);

        // Применяем выравнивание
        applyTextAlignment(textNode);

        textNode.setFill(currentColor);
        textNode.setOpacity(currentOpacity);

        addShape(textNode);
        return textNode;
    }

    /**
     * Рисует текст с указанным шрифтом и учетом scale factor и позиции канваса
     * Текст центрируется относительно указанной позиции
     */
    public Text drawText(String text, double x, double y, Font font) {
        // Сохраняем текущий шрифт и устанавливаем новый
        Font previousFont = currentFont;
        setFont(font);

        Text textNode = drawText(text, x, y);

        // Восстанавливаем предыдущий шрифт
        currentFont = previousFont;

        return textNode;
    }
    /**
     * Рисует текст в указанной области с выравниванием
     */
    public Text drawTextInRect(String text, Vector2 rectPosition, Vector2 rectSize) {
        Vector2 canvasPos = gameObject.transform.position;

        Text textNode = new Text(text);

        // Рассчитываем позицию в зависимости от выравнивания
        double x = (canvasPos.x + rectPosition.x) * Config.SCALEFACTOR;
        double y = (canvasPos.y + rectPosition.y) * Config.SCALEFACTOR;
        double width = rectSize.x * Config.SCALEFACTOR;
        double height = rectSize.y * Config.SCALEFACTOR;

        // Применяем выравнивание для позиционирования в области
        applyTextAlignmentInRect(textNode, x, y, width, height);

        textNode.setFill(currentColor);
        textNode.setOpacity(currentOpacity);

        addShape(textNode);
        return textNode;
    }

    /**
     * Применяет выравнивание к текстовому узлу
     */
    private void applyTextAlignment(Text textNode) {
        javafx.scene.text.TextAlignment horizontalAlign = HORIZONTAL_ALIGNMENT_MAP.get(textAlignment);
        VPos verticalAlign = VERTICAL_ALIGNMENT_MAP.get(textAlignment);

        textNode.setTextAlignment(horizontalAlign);
        textNode.setTextOrigin(verticalAlign);
    }

    /**
     * Применяет выравнивание текста в указанной области
     */
    private void applyTextAlignmentInRect(Text textNode, double x, double y, double width, double height) {
        javafx.scene.text.TextAlignment horizontalAlign = HORIZONTAL_ALIGNMENT_MAP.get(textAlignment);
        VPos verticalAlign = VERTICAL_ALIGNMENT_MAP.get(textAlignment);

        textNode.setTextAlignment(horizontalAlign);
        textNode.setTextOrigin(verticalAlign);

        // Позиционируем текст в зависимости от выравнивания
        double textX = x;
        double textY = y;

        // Горизонтальное выравнивание
        switch (horizontalAlign) {
            case CENTER:
                textX = x + width / 2;
                break;
            case RIGHT:
                textX = x + width;
                break;
            case LEFT:
            default:
                textX = x;
                break;
        }

        // Вертикальное выравнивание
        switch (verticalAlign) {
            case CENTER:
                textY = y + height / 2;
                break;
            case BOTTOM:
                textY = y + height;
                break;
            case TOP:
            default:
                textY = y;
                break;
        }

        textNode.setX(textX);
        textNode.setY(textY);
    }




    /**
     * Рисует дугу с учетом scale factor и позиции канваса
     */
    public Arc drawArc(double centerX, double centerY, double radiusX, double radiusY,
                       double startAngle, double length) {
        Vector2 canvasPos = gameObject.transform.position;
        Arc arc = new Arc(
                (canvasPos.x + centerX) * Config.SCALEFACTOR,
                (canvasPos.y + centerY) * Config.SCALEFACTOR,
                radiusX * Config.SCALEFACTOR,
                radiusY * Config.SCALEFACTOR,
                startAngle,
                length
        );
        applyStyle(arc);
        addShape(arc);
        return arc;
    }

    /**
     * Применяет текущие стили к фигуре (ширина линии уже масштабирована)
     */
    private void applyStyle(Shape shape) {
        shape.setStroke(currentColor);
        shape.setFill(null);
        shape.setStrokeWidth(currentLineWidth); // Уже содержит Config.SCALEFACTOR
        shape.setOpacity(currentOpacity);
    }

    /**
     * Добавляет фигуру в отрисовку
     */
    private void addShape(Node shape) {
        shape.setViewOrder(gameObject.getViewOrder());
        gameObject.nodes.add(shape);
        Main.addWidget(shape);
    }

    @Override
    public void update() {
        //...
    }

    /**
     * Получает количество нарисованных фигур
     */
    public int getShapeCount() {
        return gameObject.nodes.size();
    }

    /**
     * Получает текущее выравнивание текста
     */
    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    /**
     * Вспомогательный метод для получения масштабированного значения
     */
    public static double getScaled(double value) {
        return value * Config.SCALEFACTOR;
    }

    /**
     * Вспомогательный метод для получения масштабированного вектора
     */
    public static Vector2 getScaled(Vector2 vector) {
        return new Vector2(vector.x * Config.SCALEFACTOR, vector.y * Config.SCALEFACTOR);
    }

    public static CanvasRenderer createCanvasObject(String name, Vector2 position, double viewOrder) {
        GameObject canvasObj = GameObject.instantiate(Config.STATICASSETSPATH + "prefabs\\canvasrenderer.prefab");
        if (canvasObj == null){
            return null;
        }
        canvasObj.name = name;
        canvasObj.transform.position = position;
        canvasObj.setViewOrder(viewOrder);

        CanvasRenderer canvas = new CanvasRenderer(canvasObj);
        canvasObj.addComponent(canvas);

        Engine.addNewGameObjectInList(canvasObj);
        return canvas;
    }
}