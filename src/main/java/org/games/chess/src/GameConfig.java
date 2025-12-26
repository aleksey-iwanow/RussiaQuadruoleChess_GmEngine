package org.games.chess.src;

import GDK.engine.Vector2;
import javafx.scene.paint.Color;

/**
 * Класс конфигурации игры, содержащий все основные параметры и настройки.
 * Определяет размеры игрового поля, цветовые схемы и другие константы.
 */
public class GameConfig {
    /**
     * Размер игрового поля в клетках (квадратное поле 16x16)
     */
    public static final int BOARD_SIZE_IN_CELL = 16;

    /**
     * Размер одной клетки в пикселях
     */
    public static final int CELL_SIZE = 32;

    /**
     * Отступы от краев экрана для позиционирования игрового поля
     */
    public static final Vector2 PADDING = new Vector2(20, 80);

    /**
     * Нижний отступ в пикселях
     */
    public static final int PADDING_BOTTOM = PADDING.xInt();

    /**
     * Верхний отступ в пикселях
     */
    public static final int PADDING_UP = PADDING.yInt();

    /**
     * Левый отступ в пикселях
     */
    public static final int PADDING_LEFT = PADDING.xInt();

    /**
     * Правый отступ в пикселях
     */
    public static final int PADDING_RIGHT = PADDING.xInt();

    /**
     * Внешний отступ игрового поля
     */
    public static final int BOARD_MARGIN = 2;

    /**
     * Ширина игрового окна в пикселях
     */
    public static final int WIDTH = BOARD_SIZE_IN_CELL * CELL_SIZE + PADDING_LEFT + PADDING_RIGHT;

    /**
     * Высота игрового окна в пикселях
     */
    public static final int HEIGHT = BOARD_SIZE_IN_CELL * CELL_SIZE + PADDING_UP + PADDING_BOTTOM;

    /**
     * Размер крепости в клетках (квадратная область 4x4 в углах поля)
     */
    public static final int FORTRESS_SIZE_IN_CELL = 4;

    /**
     * Центральная позиция игрового поля
     */
    public static final Vector2 CENTER_POS = new Vector2(WIDTH/2, HEIGHT/2);

    // ЦВЕТА

    /**
     * Цвет четных клеток игрового поля
     */
    public static final Color OVEN_CELL_COLOR = new Color(0.878, 0.882, 0.902, 1);

    /**
     * Цвет нечетных клеток игрового поля
     */
    public static final Color ODD_CELL_COLOR = new Color(0.512, 0.527, 0.567, 1);

    /**
     * Цвет границ клеток
     */
    public static final Color BORDER_COLOR = new Color(0.288, 0.324, 0.414, 1);

    /**
     * Цвет стен на игровом поле
     */
    public static final Color WALL_COLOR = new Color(0.141, 0.157, 0.196, 1);

    /**
     * Основной цвет выделения фигур
     */
    public static final Color SELECTED_COLOR = new Color(0.200, 0.612, 0.625, 1);

    /**
     * Дополнительный цвет выделения (для возможных ходов)
     */
    public static final Color SELECTED_COLOR_2 = new Color(0.100, 0.612, 0.25, 1);

    /**
     * Темно-белый цвет для UI элементов
     */
    public static final Color DARK_WHITE = new Color(0.7,0.7,0.7,1);

    /**
     * Более темный белый цвет для UI элементов
     */
    public static final Color DARK_WHITE_2 = new Color(0.5,0.5,0.5,1);
}