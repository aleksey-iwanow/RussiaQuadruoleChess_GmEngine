package org.games.chess.src.figure.controllers;

import GDK.engine.Vector2;
import org.games.chess.src.board.Cell;
import org.games.chess.src.figure.Figure;

import java.util.ArrayList;
import java.util.Map;

/**
 * Класс фигуры Пешка (Pawn).
 * Имеет ограниченное движение вперед и особые правила для взятия фигур по диагонали.
 * У каждой команды пешки двигаются в своем направлении.
 */
public class Pawn extends Figure {

    /**
     * Маппинг направлений движения для каждой команды.
     * Ключ - номер команды, значение - вектор направления движения.
     */
    private final Map<Integer, Vector2> directionTeams = Map.of(
            1, new Vector2(0, 1),   // Команда 1: вниз
            2, new Vector2(-1, 0),  // Команда 2: влево
            3, new Vector2(0, -1),  // Команда 3: вверх
            4, new Vector2(1, 0)    // Команда 4: вправо
    );

    /**
     * Возвращает массив клеток, на которые может переместиться пешка.
     * Пешка может двигаться вперед на одну клетку, на две клетки при первом ходе,
     * и брать фигуры по диагонали.
     *
     * @param cell текущая клетка пешки
     * @return массив возможных целевых клеток
     */
    @Override
    protected Vector2[] getTargetsCells(Cell<Figure> cell) {
        if (cell == null) return new Vector2[0];

        Vector2 currentPos = cell.getPos();
        int x = (int)currentPos.x;
        int y = (int)currentPos.y;

        ArrayList<Vector2> targets = new ArrayList<>();

        var vec = directionTeams.get(getTeam());
        int dx = vec.xInt(), dy = vec.yInt();

        // Ход вперед
        var fx = x + dx;
        var fy = y + dy;
        if (checkForwardMove(getCellAt(fx, fy))) {
            targets.add(new Vector2(fx, fy));
            if (isFirstStep() && checkForwardMove(getCellAt(fx+dx, fy+dy))){
                targets.add(new Vector2(fx+dx, fy+dy));
            }
        }

        // Взятия по диагонали (перпендикулярно направлению движения)
        if (dx == 0) { // Движение по вертикали
            addCaptureIfEnemy(targets, x - 1, y + dy); // левая диагональ
            addCaptureIfEnemy(targets, x + 1, y + dy); // правая диагональ
        } else { // Движение по горизонтали
            addCaptureIfEnemy(targets, x + dx, y - 1); // верхняя диагональ
            addCaptureIfEnemy(targets, x + dx, y + 1); // нижняя диагональ
        }

        return targets.toArray(new Vector2[0]);
    }

    /**
     * Проверяет, может ли пешка сделать ход вперед на указанную клетку.
     *
     * @param forwardCell клетка для проверки
     * @return true если клетка активна и пуста, false в противном случае
     */
    private boolean checkForwardMove(Cell<Figure> forwardCell) {
        return forwardCell != null && forwardCell.isActive() && forwardCell.getValue() == null;
    }
}