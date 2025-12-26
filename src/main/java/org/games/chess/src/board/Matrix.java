package org.games.chess.src.board;

import GDK.engine.Vector2;
import org.games.chess.src.GameConfig;
import org.games.chess.src.utils.CellEventHandler;

/**
 * Двумерная матрица клеток игрового поля.
 * Управляет созданием, хранением и доступом к клеткам доски.
 * Реализует паттерн Singleton.
 *
 * @param <T> тип значения, хранящегося в клетках (обычно Figure)
 */
public class Matrix<T> {

    /**
     * Двумерный массив клеток
     */
    private Cell<T>[][] elements;

    /**
     * Размер матрицы в клетках
     */
    private final Vector2 size;

    /**
     * Размер внешней границы неактивных клеток
     */
    private final int margin;

    /**
     * Размер крепостей в углах доски
     */
    private final Vector2 fortressSize;

    /**
     * Статический экземпляр матрицы для реализации Singleton
     */
    private static Matrix instance;

    /**
     * Возвращает единственный экземпляр Matrix.
     *
     * @return экземпляр Matrix
     */
    public static Matrix getInstance() {return instance;}

    /**
     * Конструктор матрицы.
     * Инициализирует все клетки доски с учетом крепостей и границ.
     *
     * @param baseEventHandler базовый обработчик событий для клеток
     */
    public Matrix(CellEventHandler<T> baseEventHandler) {
        instance = this;

        this.size = Vector2.toVec(GameConfig.BOARD_SIZE_IN_CELL);
        this.margin = GameConfig.BOARD_MARGIN;
        this.fortressSize = Vector2.toVec(GameConfig.FORTRESS_SIZE_IN_CELL);

        initializeMatrix(baseEventHandler);
    }

    /**
     * Инициализирует матрицу клеток.
     * Создает все клетки и устанавливает их активность в зависимости от позиции.
     *
     * @param baseEventHandler обработчик событий для клеток
     */
    @SuppressWarnings("unchecked")
    private void initializeMatrix(CellEventHandler<T> baseEventHandler) {
        elements = (Cell<T>[][]) new Cell[size.xInt()][size.yInt()];

        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                boolean isActive = isPositionActiveFortress(x, y) || isPositionActiveBoard(x, y);
                elements[x][y] = new Cell<>(isActive, new Vector2(x, y));
                elements[x][y].setOnUpdateHandler(baseEventHandler);
            }
        }
    }

    /**
     * Проверяет, является ли позиция активной частью основной доски.
     * Основная доска - это область внутри границ (margin).
     *
     * @param x координата X
     * @param y координата Y
     * @return true если позиция активна на основной доске, false в противном случае
     */
    private boolean isPositionActiveBoard(int x, int y) {
        return x > margin - 1 && x < size.xInt() - margin &&
                y > margin - 1 && y < size.yInt() - margin;
    }

    /**
     * Проверяет, является ли позиция активной частью крепости.
     * Крепости - это квадратные области 4x4 в углах доски.
     *
     * @param x координата X
     * @param y координата Y
     * @return true если позиция находится в крепости, false в противном случае
     */
    private boolean isPositionActiveFortress(int x, int y) {
        // Проверяем, не находимся ли мы в левой верхней крепости
        if (x < fortressSize.x && y < fortressSize.y) {
            return true;
        }

        // Проверяем, не находимся ли мы в правой верхней крепости
        if (x >= size.x - fortressSize.x && y < fortressSize.y) {
            return true;
        }

        // Проверяем, не находимся ли мы в левой нижней крепости
        if (x < fortressSize.x && y >= size.y - fortressSize.y) {
            return true;
        }

        // Проверяем, не находимся ли мы в правой нижней крепости
        return x >= size.x - fortressSize.x && y >= size.y - fortressSize.y;
    }

    /**
     * Обновляет все клетки матрицы.
     * Вызывает метод update() для каждой клетки, что приводит к их перерисовке.
     */
    public void updateCells() {
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < elements[i].length; j++) {
                if (elements[i][j] != null) {
                    elements[i][j].update();
                }
            }
        }
    }


    /**
     * Возвращает клетку по указанным координатам.
     *
     * @param x координата X
     * @param y координата Y
     * @return объект клетки или null если координаты вне диапазона
     */
    public Cell<T> getCell(int x, int y) {
        if (x >= 0 && x < size.x && y >= 0 && y < size.y) {
            return elements[x][y];
        }
        return null;
    }

    /**
     * Возвращает клетку по вектору позиции.
     *
     * @param pos вектор позиции
     * @return объект клетки или null если позиция вне диапазона
     */
    public Cell<T> getCell(Vector2 pos) {
        return getCell(pos.xInt(), pos.yInt());
    }

    /**
     * Устанавливает значение в указанную клетку.
     * Устанавливает значение только если клетка активна.
     *
     * @param x координата X
     * @param y координата Y
     * @param value значение для установки
     */
    public void setValue(int x, int y, T value) {
        Cell<T> cell = getCell(x, y);
        if (cell != null && cell.isActive()) {
            cell.setValue(value);
        }
    }

    /**
     * Устанавливает значение в клетку по вектору позиции.
     *
     * @param pos вектор позиции
     * @param value значение для установки
     */
    public void setValue(Vector2 pos, T value) {
        setValue(pos.xInt(), pos.yInt(), value);
    }

    /**
     * Возвращает значение из указанной клетки.
     *
     * @param x координата X
     * @param y координата Y
     * @return значение клетки или null если клетка неактивна или не существует
     */
    public T getValue(int x, int y) {
        Cell<T> cell = getCell(x, y);
        return (cell != null && cell.isActive()) ? cell.getValue() : null;
    }

    /**
     * Возвращает значение из клетки по вектору позиции.
     *
     * @param pos вектор позиции
     * @return значение клетки или null если клетка неактивна или не существует
     */
    public T getValue(Vector2 pos) {
        return getValue(pos.xInt(), pos.yInt());
    }

    /**
     * Проверяет, активна ли указанная клетка.
     *
     * @param x координата X
     * @param y координата Y
     * @return true если клетка существует и активна, false в противном случае
     */
    public boolean isCellActive(int x, int y) {
        Cell<T> cell = getCell(x, y);
        return cell != null && cell.isActive();
    }

    /**
     * Проверяет, активна ли клетка по вектору позиции.
     *
     * @param pos вектор позиции
     * @return true если клетка существует и активна, false в противном случае
     */
    public boolean isCellActive(Vector2 pos) {
        return isCellActive(pos.xInt(), pos.yInt());
    }

    /**
     * Возвращает размер матрицы в клетках.
     *
     * @return вектор размера
     */
    public Vector2 getSize() {
        return size;
    }

    /**
     * Возвращает размер границы неактивных клеток.
     *
     * @return размер границы
     */
    public int getMargin() {
        return margin;
    }

    /**
     * Возвращает размер крепостей в клетках.
     *
     * @return размер крепостей
     */
    public Vector2 getFortressSize() {
        return fortressSize;
    }
}