package org.games.chess.src.figure.controllers;

import GDK.engine.Vector2;
import org.games.chess.src.board.Cell;
import org.games.chess.src.figure.Figure;

import java.util.ArrayList;

/**
 * Класс фигуры Слон (Bishop).
 * Может перемещаться на любое количество клеток по диагонали.
 */
public class Bishop extends Figure {

    /**
     * Возвращает массив клеток, на которые может переместиться слон.
     * Слон перемещается только по диагоналям (в четырех направлениях).
     *
     * @param cell текущая клетка слона
     * @return массив возможных целевых клеток
     */
    @Override
    protected Vector2[] getTargetsCells(Cell<Figure> cell) {
        if (cell == null) return new Vector2[0];

        Vector2 currentPos = cell.getPos();
        int currentX = (int)currentPos.x;
        int currentY = (int)currentPos.y;

        ArrayList<Vector2> targets = new ArrayList<>();

        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] dir : directions) {
            traverseDirection(targets, currentX, currentY, dir[0], dir[1]);
        }

        return targets.toArray(new Vector2[0]);
    }
}