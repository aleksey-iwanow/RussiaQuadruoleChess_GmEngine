package org.games.chess.src;

import GDK.engine.*;
import GDK.engine.components.CanvasRenderer;

import javafx.scene.paint.Color;
import org.games.chess.src.board.Board;
import org.games.chess.src.figure.Figure;
import org.games.chess.src.figure.FigureManager;
import org.games.chess.src.ui.UIPanel;
import org.games.chess.src.ui.WinPanel;

/**
 * Главный менеджер игры, управляющий основным игровым процессом.
 * Обрабатывает инициализацию, завершение и рестарт игры.
 */
public class GameManager extends ScriptControl {

    /**
     * Менеджер фигур, управляющий всеми фигурами на доске
     */
    private FigureManager figureManager;

    /**
     * Статический экземпляр GameManager для реализации шаблона Singleton
     */
    private static GameManager instance;

    /**
     * Флаг, указывающий находится ли игра на паузе
     */
    public static boolean gameOnPause;

    /**
     * Возвращает единственный экземпляр GameManager (Singleton паттерн)
     * @return экземпляр GameManager
     */
    public static GameManager getInstance() {
        return instance;
    }

    /**
     * Метод инициализации, вызываемый при создании объекта.
     * Устанавливает размер экрана, создает доску и менеджер фигур.
     */
    @Override
    public void awake(){
        instance = this;
        Screen.setWidth(GameConfig.WIDTH);
        Screen.setHeight(GameConfig.HEIGHT);
        Board board = new Board();
        figureManager = new FigureManager();

        board.render();
    }

    /**
     * Завершает игру с указанием победившей команды.
     * Блокирует фигуры, ставит игру на паузу и показывает панель победы.
     *
     * @param winTeam номер команды-победителя (1-4)
     */
    public void endGame(int winTeam) {
        Figure.block = true;
        gameOnPause= true;
        UIPanel.activeTimer(false);
        Board.getInstance().disableMark();

        GameObject winPanelObj = instantiate(Config.PATH_PROJECT_LOCAL+"prefabs\\winPanel.prefab");
        WinPanel winPanel = winPanelObj.getScript(WinPanel.class);

        winPanel.initialize(winTeam % 2 == 1 ? "БЕЛЫЕ" : "ЧЕРНЫЕ",
                Color.rgb(30, 30, 40, 0.9),
                Color.SILVER,
                Color.LIGHTBLUE);

        winPanel.show();
    }

    /**
     * Перезапускает игру, сбрасывая все состояния.
     * Уничтожает все фигуры, очищает таймер и снимает игру с паузы.
     */
    public void restartGame(){
        Figure.block = false;
        figureManager.DestroyAllFiguresAndClearList();
        figureManager.initDefaultBoard();

        UIPanel.clearTimer();
        UIPanel.activeTimer(true);

        gameOnPause = false;
    }
}