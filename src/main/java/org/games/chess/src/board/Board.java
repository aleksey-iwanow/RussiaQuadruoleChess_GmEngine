package org.games.chess.src.board;

import GDK.engine.Config;
import GDK.engine.Vector2;
import GDK.engine.components.CanvasRenderer;
import javafx.scene.paint.Color;
import org.games.chess.src.GameConfig;
import org.games.chess.src.figure.Figure;
import org.games.chess.src.figure.FigureManager;

/**
 * Класс игровой доски.
 * Отвечает за отрисовку игрового поля, клеток, стен и маркеров текущей команды.
 */
public class Board {

    /**
     * Canvas для отрисовки основного поля (клетки и стены)
     */
    private final CanvasRenderer canvas;

    /**
     * Canvas для отрисовки маркера текущей команды
     */
    private final CanvasRenderer canvasCurrentTeamMark;

    /**
     * Матрица клеток игрового поля
     */
    private final Matrix<Figure> matrix;

    /**
     * Контроллер стен на поле
     */
    private final WallController wallController;

    /**
     * Статический экземпляр доски для реализации Singleton
     */
    private static Board instance;

    /**
     * Возвращает единственный экземпляр Board.
     *
     * @return экземпляр Board
     */
    public static Board getInstance() {
        return instance;
    }

    /**
     * Конструктор доски.
     * Инициализирует canvas, матрицу клеток и контроллер стен.
     */
    public Board(){
        instance = this;
        canvas = CanvasRenderer.createCanvasObject("canvas", Vector2.toVec(GameConfig.PADDING), 99);
        canvasCurrentTeamMark = CanvasRenderer.createCanvasObject("canvasCurrentTeamMark", Vector2.toVec(GameConfig.PADDING), 88);
        matrix = new Matrix<>(this::onUpdateCell);

        // Инициализируем контроллер стен вместо прямого создания стен
        wallController = new WallController();
    }

    /**
     * Обработчик обновления клетки.
     * Вызывается при каждой перерисовке клетки, отрисовывает ее внешний вид.
     *
     * @param <T> тип значения в клетке
     * @param cell клетка для отрисовки
     */
    private <T> void onUpdateCell(Cell<T> cell) {
        if (!cell.isActive()) return;

        Vector2 size = cell.getSize();
        Vector2 pos = cell.getPos().increaseVector(size);

        //непосредственно ячейка
        Color color = GameConfig.ODD_CELL_COLOR;
        if (cell.getPos().sum() % 2 == 0)
            color = GameConfig.OVEN_CELL_COLOR;

        canvas.setColor(color);
        canvas.drawFilledRect(pos, size);

        //обводка
        canvas.setColor(GameConfig.BORDER_COLOR);
        double thickness = 2.0, ext = 1.0;

        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                double x = (i == 0) ? pos.x - thickness/2 : pos.x + size.x - thickness/2;
                canvas.drawFilledRect(
                        new Vector2(x, pos.y - ext),
                        new Vector2(thickness, size.y + 2 * ext)
                );
            } else {
                double y = (i == 1) ? pos.y - thickness/2 : pos.y + size.y - thickness/2;
                canvas.drawFilledRect(
                        new Vector2(pos.x - ext, y),
                        new Vector2(size.x + 2 * ext, thickness)
                );
            }
        }
    }

    /**
     * Отключает отображение маркера текущей команды.
     * Используется при окончании игры или паузе.
     */
    public void disableMark(){
        canvasCurrentTeamMark.clear();
    }

    /**
     * Отрисовывает маркер текущей команды в виде треугольника на соответствующей стороне поля.
     *
     * @param team номер команды для отрисовки маркера (1-4)
     */
    public void renderCurrentTeamMark(int team){
        if (canvasCurrentTeamMark == null) return;
        canvasCurrentTeamMark.clear();
        canvasCurrentTeamMark.setColor(GameConfig.SELECTED_COLOR_2);

        Vector2[] trianglePoints;
        double size = GameConfig.CELL_SIZE;
        double centerX = ((double) GameConfig.BOARD_SIZE_IN_CELL /2)*GameConfig.CELL_SIZE;
        double centerY = ((double) GameConfig.BOARD_SIZE_IN_CELL /2)*GameConfig.CELL_SIZE;
        double offset = GameConfig.CELL_SIZE * 8;

        switch (team) {
            case 1: // Верх - треугольник направлен ВНИЗ к центру
                trianglePoints = new Vector2[]{
                        new Vector2(centerX, centerY - offset + size), // Нижняя точка (к центру)
                        new Vector2(centerX - size/2, centerY - offset), // Левая верхняя
                        new Vector2(centerX + size/2, centerY - offset)  // Правая верхняя
                };
                break;
            case 2: // Право - треугольник направлен ВЛЕВО к центру
                trianglePoints = new Vector2[]{
                        new Vector2(centerX + offset - size, centerY), // Левая точка (к центру)
                        new Vector2(centerX + offset, centerY - size/2), // Верхняя правая
                        new Vector2(centerX + offset, centerY + size/2)  // Нижняя правая
                };
                break;
            case 3: // Низ - треугольник направлен ВВЕРХ к центру
                trianglePoints = new Vector2[]{
                        new Vector2(centerX, centerY + offset - size), // Верхняя точка (к центру)
                        new Vector2(centerX - size/2, centerY + offset), // Левая нижняя
                        new Vector2(centerX + size/2, centerY + offset)  // Правая нижняя
                };
                break;
            case 4: // Лево - треугольник направлен ВПРАВО к центру
                trianglePoints = new Vector2[]{
                        new Vector2(centerX - offset + size, centerY), // Правая точка (к центру)
                        new Vector2(centerX - offset, centerY - size/2), // Верхняя левая
                        new Vector2(centerX - offset, centerY + size/2)  // Нижняя левая
                };
                break;
            default:
                return;
        }

        double[] points = new double[trianglePoints.length * 2];
        for (int i = 0; i < trianglePoints.length; i++) {
            points[i * 2] = trianglePoints[i].x;
            points[i * 2 + 1] = trianglePoints[i].y;
        }

        canvasCurrentTeamMark.drawFilledPolygon(points);
    }

    /**
     * Полностью перерисовывает игровое поле.
     * Очищает canvas, обновляет все клетки, рисует стены и маркер текущей команды.
     */
    public void render(){
        canvas.clear();
        matrix.updateCells();
        renderWalls();
        renderCurrentTeamMark(FigureManager.getInstance().getCurrentTeam());
    }

    /**
     * Отрисовывает все стены на игровом поле.
     * Получает список стен из контроллера и рисует их линиями.
     */
    private void renderWalls() {
        canvas.setLineWidth(5.0);
        canvas.setColor(GameConfig.WALL_COLOR);

        for (Wall wall : wallController.getWalls()) {
            canvas.drawLine(wall.startPos.x, wall.startPos.y, wall.endPos.x, wall.endPos.y);
        }
    }
}