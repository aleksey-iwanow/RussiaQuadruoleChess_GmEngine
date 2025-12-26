package org.games.chess.src.figure.controllers;

import GDK.engine.Vector2;
import org.games.chess.src.board.Cell;
import org.games.chess.src.figure.Figure;

import java.util.ArrayList;

/**
 * Класс фигуры Ферзь (Queen).
 * Может перемещаться на любое количество клеток по горизонтали, вертикали или диагонали.
 * Объединяет возможности ладьи и слона.
 */
public class Queen extends Figure {

    /**
     * Возвращает массив клеток, на которые может переместиться ферзь.
     * Ферзь перемещается по прямым линиям во всех восьми направлениях.
     *
     * @param cell текущая клетка ферзя
     * @return массив возможных целевых клеток
     */
    @Override
    protected Vector2[] getTargetsCells(Cell<Figure> cell) {
        if (cell == null) return new Vector2[0];

        Vector2 currentPos = cell.getPos();
        int currentX = (int)currentPos.x;
        int currentY = (int)currentPos.y;

        ArrayList<Vector2> targets = new ArrayList<>();

        int[][] directions = {
                {1, 0}, {1, 1}, {0, 1}, {-1, 1},
                {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
        };

        for (int[] dir : directions) {
            traverseDirection(targets, currentX, currentY, dir[0], dir[1]);
        }

        return targets.toArray(new Vector2[0]);
    }
}