package org.games.chess.src.board;

import GDK.engine.Vector2;
import org.games.chess.src.GameConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для управления стенами на игровом поле.
 * Обеспечивает создание, хранение и проверку пересечения с стенами.
 */
public class WallController {

    /**
     * Статический экземпляр контроллера для реализации Singleton
     */
    private static WallController instance;

    /**
     * Список всех стен на игровом поле
     */
    private final List<Wall> walls = new ArrayList<>();

    /**
     * Возвращает единственный экземпляр WallController.
     *
     * @return экземпляр WallController
     */
    public static WallController getInstance() {
        return instance;
    }

    /**
     * Конструктор контроллера стен.
     * Создает экземпляр Singleton и инициализирует стандартные стены.
     */
    public WallController() {
        instance = this;
        initializeWalls();
    }

    /**
     * Инициализирует стандартные стены на игровом поле.
     * Создает 4 стены, образующие крепости в углах доски.
     */
    private void initializeWalls() {
        walls.add(new Wall(new Vector2(0, 4), new Vector2(4, 4)));
        walls.add(new Wall(new Vector2(4, GameConfig.BOARD_SIZE_IN_CELL - 4),
                new Vector2(4, GameConfig.BOARD_SIZE_IN_CELL)));
        walls.add(new Wall(new Vector2(GameConfig.BOARD_SIZE_IN_CELL - 4, GameConfig.BOARD_SIZE_IN_CELL - 4),
                new Vector2(GameConfig.BOARD_SIZE_IN_CELL, GameConfig.BOARD_SIZE_IN_CELL - 4)));
        walls.add(new Wall(new Vector2(GameConfig.BOARD_SIZE_IN_CELL - 4, 0),
                new Vector2(GameConfig.BOARD_SIZE_IN_CELL - 4, 4)));
    }

    /**
     * Преобразует координаты клетки в координаты ее центра на экране.
     *
     * @param cellPos позиция клетки в координатах доски
     * @return координаты центра клетки на экране
     */
    private Vector2 getCellCenter(Vector2 cellPos) {
        return cellPos.increaseVector(GameConfig.CELL_SIZE)
                .addVector(GameConfig.PADDING)
                .addVector(new Vector2(GameConfig.CELL_SIZE / 2, GameConfig.CELL_SIZE / 2));
    }


    /**
     * Проверяет, пересекает ли путь между двумя клетками на доске какую-либо стену.
     * Используется для проверки возможности хода фигур.
     *
     * @param startCell начальная клетка пути
     * @param endCell конечная клетка пути
     * @return true если путь пересекает стену, false в противном случае
     */
    public boolean doesChessPathCrossWall(Vector2 startCell, Vector2 endCell) {
        Vector2 startCenter = getCellCenter(startCell);
        Vector2 endCenter = getCellCenter(endCell);

        for (Wall wall : walls) {
            if (segmentsIntersect(wall.startPos.addVector(GameConfig.PADDING), wall.endPos.addVector(GameConfig.PADDING), startCenter, endCenter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет пересечение двух отрезков на плоскости.
     * Использует алгоритм определения взаимного расположения отрезков.
     *
     * @param a начальная точка первого отрезка
     * @param b конечная точка первого отрезка
     * @param c начальная точка второго отрезка
     * @param d конечная точка второго отрезка
     * @return true если отрезки пересекаются, false в противном случае
     */
    public static boolean segmentsIntersect(Vector2 a, Vector2 b, Vector2 c, Vector2 d) {
        double cross1 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        double cross2 = (b.x - a.x) * (d.y - a.y) - (b.y - a.y) * (d.x - a.x);
        double cross3 = (d.x - c.x) * (a.y - c.y) - (d.y - c.y) * (a.x - c.x);
        double cross4 = (d.x - c.x) * (b.y - c.y) - (d.y - c.y) * (b.x - c.x);

        if (cross1 * cross2 < 0 && cross3 * cross4 < 0) return true;

        // Проверка особых случаев (концы отрезков совпадают)
        return cross1 == 0 && onSegment(a, b, c) || cross2 == 0 && onSegment(a, b, d) ||
                cross3 == 0 && onSegment(c, d, a) || cross4 == 0 && onSegment(c, d, b);
    }

    /**
     * Проверяет, лежит ли точка C на отрезке AB.
     *
     * @param a начальная точка отрезка
     * @param b конечная точка отрезка
     * @param c точка для проверки
     * @return true если точка лежит на отрезке, false в противном случае
     */
    private static boolean onSegment(Vector2 a, Vector2 b, Vector2 c) {
        return c.x >= Math.min(a.x, b.x) && c.x <= Math.max(a.x, b.x) &&
                c.y >= Math.min(a.y, b.y) && c.y <= Math.max(a.y, b.y);
    }

    /**
     * Возвращает копию списка всех стен.
     *
     * @return список всех стен на поле
     */
    public List<Wall> getWalls() {
        return new ArrayList<>(walls);
    }

    /**
     * Добавляет новую стену программно.
     *
     * @param wall стена для добавления
     */
    public void addWall(Wall wall) {
        walls.add(wall);
    }

    /**
     * Очищает все стены на поле.
     */
    public void clearWalls() {
        walls.clear();
    }
}