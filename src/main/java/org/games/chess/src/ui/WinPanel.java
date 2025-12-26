package org.games.chess.src.ui;

import GDK.engine.Config;
import GDK.engine.GameObject;
import GDK.engine.ScriptControl;
import GDK.engine.Vector2;
import GDK.engine.components.CanvasRenderer;
import GDK.engine.samples.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.games.chess.src.GameConfig;
import org.games.chess.src.GameManager;

/**
 * Панель победы, отображающаяся при завершении игры.
 * Содержит информацию о победителе и кнопку рестарта.
 */
public class WinPanel extends ScriptControl {

    /**
     * Порядок отображения панели (более низкое значение означает более глубокий слой)
     */
    private int viewOrder = -99;

    /**
     * Canvas для отрисовки графики панели
     */
    private CanvasRenderer canvas;

    /**
     * Текст, отображающий информацию о победителе
     */
    private String winnerText = "Победа!";

    /**
     * Цвет фона панели
     */
    private Color backgroundColor = Color.rgb(0, 0, 0, 0.85);

    /**
     * Цвет текста на панели
     */
    private Color textColor = Color.GOLD;

    /**
     * Акцентный цвет для декоративных элементов
     */
    private Color accentColor = Color.GOLD;

    /**
     * Кнопка для перезапуска игры
     */
    private Button restartButton;

    /**
     * Метод инициализации, вызываемый при создании панели.
     * Устанавливает размеры, позиционирование и создает элементы UI.
     */
    @Override
    public void awake() {
        transform.setSize(new Vector2(GameConfig.WIDTH - 200, GameConfig.HEIGHT - 200));
        transform.setCenterPosition(GameConfig.CENTER_POS);
        canvas = CanvasRenderer.createCanvasObject("winpanel_canvas", transform.position, viewOrder);
        canvas.gameObject.setParent(gameObject);
        // Создаем кнопку перезапуска
        initializeRestartButton();

        // Отрисовываем всю панель
        render();
    }

    /**
     * Инициализирует панель с заданными параметрами победителя и цветами.
     *
     * @param winnerTeam название команды-победителя
     * @param bgColor цвет фона панели
     * @param txtColor цвет текста
     * @param accentColor акцентный цвет для декоративных элементов
     */
    public void initialize(String winnerTeam, Color bgColor, Color txtColor, Color accentColor) {
        this.winnerText = "Победили " + winnerTeam;
        this.backgroundColor = bgColor;
        this.textColor = txtColor;
        this.accentColor = accentColor;
        render(); // Перерисовываем с новыми данными
    }

    /**
     * Инициализирует панель с цветами по умолчанию.
     *
     * @param winnerTeam название команды-победителя
     */
    public void initialize(String winnerTeam) {
        initialize(winnerTeam,
                Color.rgb(0, 0, 0, 0.85),
                Color.GOLD,
                Color.GOLD);
    }

    /**
     * Создает и настраивает кнопку перезапуска игры.
     * Загружает префаб кнопки и устанавливает обработчик клика.
     */
    private void initializeRestartButton() {
        // Создаем кнопку перезапуска через instantiate
        GameObject buttonObj = instantiate(Config.PATH_PROJECT_LOCAL+"prefabs\\restart_button.prefab");
        if (buttonObj != null) {
            restartButton = buttonObj.getScript(Button.class);
            if (restartButton != null) {
                // Позиционируем кнопку в нижней части панели
                Vector2 buttonPosition = new Vector2(
                        transform.position.x + transform.size.x / 2,
                        transform.position.y + transform.size.y * 0.7
                );
                buttonObj.transform.setCenterPosition(buttonPosition);
                buttonObj.setParent(gameObject);

                // Устанавливаем обработчик клика
                restartButton.setOnClick(this::onRestartClicked);
            }
        }
    }

    /**
     * Обработчик клика по кнопке рестарта.
     * Перезапускает игру и уничтожает панель победы.
     */
    private void onRestartClicked() {
        System.out.println("Рестарт игры!");

        GameManager.getInstance().restartGame();
        restartButton.destroy();
        gameObject.destroy();
    }

    /**
     * Отрисовывает всю панель победы с текущими настройками.
     * Выполняет очистку canvas и последовательно рисует все элементы.
     */
    private void render() {
        if (canvas == null) return;

        canvas.clear();
        canvas.setTextAlignment(CanvasRenderer.TextAlignment.CENTER);

        // Рисуем основной фон панели
        drawPanelBackground();

        // Рисуем заголовок
        drawTitle();

        // Рисуем текст победителя
        drawWinnerText();

        // Рисуем декоративные элементы
        drawDecoration();
    }

    /**
     * Отрисовывает фоновый слой панели с рамкой.
     */
    private void drawPanelBackground() {
        // Основной фон
        canvas.setColor(backgroundColor);
        canvas.drawFilledRect(Vector2.zero(), transform.size);

        // Рамка
        canvas.setColor(accentColor);
        canvas.setLineWidth(4);
        canvas.drawRect(Vector2.zero(), transform.size);

        // Внутренняя подсветка
        canvas.setColor(accentColor.deriveColor(0, 1, 1, 0.3));
        canvas.setLineWidth(2);
        canvas.drawRect(new Vector2(2, 2), transform.size.subtractVector(new Vector2(4, 4)));
    }

    /**
     * Отрисовывает заголовок "ПОБЕДА!".
     */
    private void drawTitle() {
        canvas.setColor(textColor);

        String title = "ПОБЕДА!";
        double titleY = transform.size.y * 0.2;

        // Фон для заголовка
        canvas.setColor(accentColor.deriveColor(0, 1, 1, 0.2));
        canvas.drawFilledRect(transform.size.x * 0.25, titleY - 25, transform.size.x * 0.5, 60);

        // Текст заголовка - центр текста будет в (transform.size.x / 2, titleY)
        canvas.setColor(textColor);
        canvas.setFont(new Font(35));
        canvas.drawText(title, transform.size.x / 2, titleY-10);

        // Подчеркивание заголовка
        canvas.setColor(accentColor);
        canvas.setLineWidth(3);
        canvas.drawLine(transform.size.x * 0.3, titleY + 25, transform.size.x * 0.7, titleY + 25);
    }

    /**
     * Отрисовывает текст с информацией о победителе.
     */
    private void drawWinnerText() {
        canvas.setColor(textColor);

        // Основной текст победителя
        double textY = transform.size.y * 0.45;
        canvas.setFont(new Font(22));
        canvas.drawText(winnerText, transform.size.x / 2, textY);

        // Дополнительный декоративный текст
        canvas.setColor(accentColor.deriveColor(0, 1, 1, 0.7));
        String congrats = "Поздравляем с победой!";
        canvas.setFont(new Font(17));
        canvas.drawText(congrats, transform.size.x / 2, textY + 40);
    }

    /**
     * Отрисовывает декоративные элементы панели (уголки и боковые украшения).
     */
    private void drawDecoration() {
        canvas.setColor(accentColor);
        canvas.setLineWidth(2);

        // Декоративные уголки
        double cornerSize = 25;

        // Левый верхний угол
        canvas.drawLine(0, 0, cornerSize, 0);
        canvas.drawLine(0, 0, 0, cornerSize);

        // Правый верхний угол
        canvas.drawLine(transform.size.x, 0, transform.size.x - cornerSize, 0);
        canvas.drawLine(transform.size.x, 0, transform.size.x, cornerSize);

        // Левый нижний угол
        canvas.drawLine(0, transform.size.y, cornerSize, transform.size.y);
        canvas.drawLine(0, transform.size.y, 0, transform.size.y - cornerSize);

        // Правый нижний угол
        canvas.drawLine(transform.size.x, transform.size.y, transform.size.x - cornerSize, transform.size.y);
        canvas.drawLine(transform.size.x, transform.size.y, transform.size.x, transform.size.y - cornerSize);

        // Декоративные элементы по бокам
        drawSideDecorations();
    }

    /**
     * Отрисовывает боковые декоративные элементы (полоски по краям панели).
     */
    private void drawSideDecorations() {
        canvas.setColor(accentColor.deriveColor(0, 1, 1, 0.4));
        canvas.setLineWidth(2);

        double centerY = transform.size.y / 2;
        double decorationWidth = 15;

        // Левый боковой декор
        for (int i = 0; i < 5; i++) {
            double y = centerY - 40 + i * 20;
            canvas.drawLine(0, y, decorationWidth, y);
        }

        // Правый боковой декор
        for (int i = 0; i < 5; i++) {
            double y = centerY - 40 + i * 20;
            canvas.drawLine(transform.size.x, y, transform.size.x - decorationWidth, y);
        }

        // Верхний и нижний декор
        double centerX = transform.size.x / 2;
        for (int i = 0; i < 3; i++) {
            double x = centerX - 30 + i * 30;
            canvas.drawLine(x, 0, x, 10);
            canvas.drawLine(x, transform.size.y, x, transform.size.y - 10);
        }
    }

    /**
     * Обновляет текст победителя и перерисовывает панель.
     *
     * @param teamName название команды-победителя
     */
    public void setWinnerText(String teamName) {
        this.winnerText = "Победила команда: " + teamName;
        render();
    }

    /**
     * Показывает панель победы, устанавливая соответствующий порядок отображения.
     */
    public void show() {
        gameObject.setViewOrder(viewOrder);
        if (restartButton != null) {
            restartButton.gameObject.setViewOrder(viewOrder + 1);
        }
    }

    /**
     * Скрывает панель победы, перемещая ее на задний план.
     */
    public void hide() {
        gameObject.setViewOrder(999); // Убираем с экрана
    }

    /**
     * Метод обновления, вызываемый каждый кадр.
     * Синхронизирует позицию canvas с transform панели.
     */
    @Override
    public void update() {
        // Обновляем позицию canvas если transform изменился
        if (canvas != null) {
            canvas.gameObject.transform.position = transform.position;
        }
    }

    /**
     * Устанавливает кастомные цвета для панели.
     *
     * @param background цвет фона
     * @param text цвет текста
     * @param accent акцентный цвет
     */
    public void setColors(Color background, Color text, Color accent) {
        this.backgroundColor = background;
        this.textColor = text;
        this.accentColor = accent;
        render();
    }
}