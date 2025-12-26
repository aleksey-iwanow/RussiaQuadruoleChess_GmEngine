package org.games.chess.src.board;

import GDK.engine.Vector2;
import org.games.chess.src.GameConfig;
import org.games.chess.src.utils.CellEventHandler;

/**
 * Класс, представляющий одну клетку игрового поля.
 * Может содержать значение (например, фигуру) и имеет состояние активности.
 *
 * @param <T> тип значения, хранящегося в клетке
 */
public class Cell<T> {

    /**
     * Значение, хранящееся в клетке (например, фигура)
     */
    private T value;

    /**
     * Флаг активности клетки (неактивные клетки недоступны для ходов)
     */
    private boolean active;

    /**
     * Позиция клетки на доске в координатах клеток
     */
    private Vector2 pos;

    /**
     * Обработчик событий обновления клетки
     */
    private CellEventHandler<T> onUpdate;

    /**
     * Создает новую клетку с указанной активностью.
     *
     * @param active флаг активности клетки
     */
    public Cell(boolean active) {
        this(active, null, null);
    }

    /**
     * Создает новую клетку с указанной активностью и позицией.
     *
     * @param active флаг активности клетки
     * @param pos позиция клетки на доске
     */
    public Cell(boolean active, Vector2 pos) {
        this(active, null, pos);
    }

    /**
     * Создает новую клетку с указанной активностью, значением и позицией.
     *
     * @param active флаг активности клетки
     * @param value начальное значение клетки
     * @param pos позиция клетки на доске
     */
    public Cell(boolean active, T value, Vector2 pos) {
        this.active = active;
        this.value = value;
        this.pos = pos;
    }

    /**
     * Устанавливает обработчик событий обновления клетки.
     * Обработчик вызывается при каждом обновлении клетки.
     *
     * @param handler обработчик событий клетки
     */
    public void setOnUpdateHandler(CellEventHandler<T> handler) {
        this.onUpdate = handler;
    }

    /**
     * Вызывает обработчик обновления клетки.
     * Используется для перерисовки клетки при изменениях.
     */
    public void update() {
        if (onUpdate != null) {
            onUpdate.handle(this);
        }
    }

    /**
     * Устанавливает позицию клетки на доске.
     *
     * @param pos новая позиция клетки
     */
    public void setPos(Vector2 pos) { this.pos = pos; }

    /**
     * Возвращает позицию клетки на доске.
     *
     * @return позиция клетки
     */
    public Vector2 getPos() { return pos; }


    /**
     * Возвращает значение, хранящееся в клетке.
     *
     * @return значение клетки
     */
    public T getValue() {
        return value;
    }

    /**
     * Устанавливает значение клетки.
     *
     * @param value новое значение для клетки
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Проверяет, активна ли клетка.
     *
     * @return true если клетка активна, false в противном случае
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Устанавливает активность клетки.
     *
     * @param active флаг активности
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Проверяет, пуста ли клетка (не содержит значения).
     *
     * @return true если клетка пуста, false в противном случае
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Очищает клетку, удаляя значение.
     */
    public void clear() {
        this.value = null;
    }

    /**
     * Возвращает размер клетки в пикселях.
     *
     * @return размер клетки
     */
    public Vector2 getSize() {
        return Vector2.toVec(GameConfig.CELL_SIZE);
    }
}