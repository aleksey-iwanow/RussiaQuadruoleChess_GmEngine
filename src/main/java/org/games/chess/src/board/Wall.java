package org.games.chess.src.board;

import GDK.engine.Vector2;
import org.games.chess.src.GameConfig;

/**
 * Класс, представляющий стену на игровом поле.
 * Содержит информацию о положении и ориентации стены.
 */
public class Wall {

    /**
     * Начальная позиция стены в пикселях (глобальные координаты)
     */
    public Vector2 startPos;

    /**
     * Конечная позиция стены в пикселях (глобальные координаты)
     */
    public Vector2 endPos;

    /**
     * Начальная позиция стены в координатах клеток
     */
    public Vector2 startCell;

    /**
     * Конечная позиция стены в координатах клеток
     */
    public Vector2 endCell;

    /**
     * Создает новую стену на основе координат клеток.
     * Автоматически преобразует координаты клеток в пиксельные координаты.
     *
     * @param startPosCell начальная позиция стены в координатах клеток
     * @param endPosCell конечная позиция стены в координатах клеток
     */
    public Wall(Vector2 startPosCell, Vector2 endPosCell){
        this.startCell = startPosCell;
        this.endCell = endPosCell;
        this.startPos = startPosCell.increaseVector(GameConfig.CELL_SIZE);
        this.endPos = endPosCell.increaseVector(GameConfig.CELL_SIZE);
    }

    /**
     * Проверяет, является ли стена вертикальной.
     *
     * @return true если стена вертикальная, false в противном случае
     */
    public boolean isVertical() {
        return startCell.x == endCell.x;
    }

    /**
     * Проверяет, является ли стена горизонтальной.
     *
     * @return true если стена горизонтальная, false в противном случае
     */
    public boolean isHorizontal() {
        return startCell.y == endCell.y;
    }
}