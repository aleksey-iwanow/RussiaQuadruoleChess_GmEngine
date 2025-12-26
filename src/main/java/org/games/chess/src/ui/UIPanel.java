package org.games.chess.src.ui;

import GDK.engine.ScriptControl;
import GDK.engine.Vector2;
import GDK.engine.components.CanvasRenderer;
import org.games.chess.src.GameConfig;
import org.games.chess.src.figure.FigureManager;

/**
 * Панель пользовательского интерфейса, отображающая информацию во время игры.
 * Содержит таймер и имя текущего игрока.
 */
public class UIPanel extends ScriptControl {

    /**
     * Canvas для отрисовки UI элементов
     */
    private CanvasRenderer canvasRenderer;

    /**
     * Поле для отображения времени игры
     */
    private TextField timerField;

    /**
     * Поле для отображения имени текущего игрока/команды
     */
    private TextField playerNameField;

    /**
     * Время начала игры (в миллисекундах с эпохи Unix)
     */
    private static long startTime;

    /**
     * Флаг активности таймера
     */
    private static boolean timerActive=true;

    /**
     * Метод инициализации, вызываемый при создании панели.
     * Устанавливает начальные параметры и создает UI элементы.
     */
    @Override
    public void awake(){
        startTime = System.currentTimeMillis();
        transform.setPos(150, 0);
        canvasRenderer = CanvasRenderer.createCanvasObject("canvasUIPanel", transform.position, 0);
        transform.setSize(GameConfig.WIDTH, GameConfig.PADDING_UP-GameConfig.PADDING_BOTTOM);

        timerField = find("timer").getScript(TextField.class);
        timerField.transform.setPos(timerField.padding);
        playerNameField = find("playerName").getScript(TextField.class);
        playerNameField.transform.setPos(GameConfig.WIDTH - playerNameField.transform.size.x - playerNameField.padding.x*2, playerNameField.padding.y );
    }

    /**
     * Метод начальной настройки, вызываемый после awake().
     * Устанавливает размер шрифта для поля имени игрока.
     */
    @Override
    public void start(){
        playerNameField.textComponent.setFontSize(20);
    }

    /**
     * Активирует или деактивирует таймер игры.
     *
     * @param value true для активации таймера, false для деактивации
     */
    public static void activeTimer(boolean value){
        timerActive = value;
    }

    /**
     * Сбрасывает таймер игры, устанавливая текущее время как время начала.
     */
    public static void clearTimer(){
        startTime = System.currentTimeMillis();
    }

    /**
     * Метод обновления, вызываемый каждый кадр.
     * Обновляет отображаемое время и имя текущего игрока.
     */
    @Override
    public void update() {
        if (timerActive)
            timerField.setText(getCurrentTime());
        playerNameField.setText("Игрок " + FigureManager.getInstance().getCurrentTeam());
    }

    /**
     * Возвращает текущее время игры в формате MM:SS.
     *
     * @return строка с отформатированным временем игры
     */
    private String getCurrentTime(){
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - startTime) / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}