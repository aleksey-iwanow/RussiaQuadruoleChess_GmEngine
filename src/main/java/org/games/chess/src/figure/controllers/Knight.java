package org.games.chess.src.figure.controllers;

import GDK.engine.Vector2;
import org.games.chess.src.GameConfig;
import org.games.chess.src.board.Cell;
import org.games.chess.src.board.Matrix;
import org.games.chess.src.figure.Figure;

import java.util.ArrayList;

/**
 * Класс фигуры Конь (Knight).
 * Перемещается буквой "Г" - на 2 клетки в одном направлении и на 1 в перпендикулярном.
 * Может перепрыгивать через другие фигуры.
 */
public class Knight extends Figure {

    /**
     * Возвращает массив клеток, на которые может переместиться конь.
     * Конь перемещается на клетки в форме буквы "Г" (8 возможных позиций).
     *
     * @param cell текущая клетка коня
     * @return массив возможных целевых клеток
     */
    @Override
    protected Vector2[] getTargetsCells(Cell<Figure> cell) {
        if (cell == null) return new Vector2[0];

        Vector2 currentPos = cell.getPos();
        int currentX = (int)currentPos.x;
        int currentY = (int)currentPos.y;

        ArrayList<Vector2> targets = new ArrayList<>();

        int[][] moves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : moves) {
            addTargetIfValid(targets, currentX + move[0], currentY + move[1]);
        }

        return targets.toArray(new Vector2[0]);
    }
}