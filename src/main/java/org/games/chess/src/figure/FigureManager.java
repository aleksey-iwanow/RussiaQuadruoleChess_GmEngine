package org.games.chess.src.figure;

import GDK.engine.Config;
import GDK.engine.GameObject;
import GDK.engine.Vector2;
import org.games.chess.src.GameConfig;
import org.games.chess.src.GameManager;
import org.games.chess.src.board.Board;
import org.games.chess.src.board.Cell;
import org.games.chess.src.board.Matrix;
import org.games.chess.src.figure.controllers.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Менеджер фигур, управляющий всеми фигурами на доске.
 * Отвечает за создание, перемещение, захват фигур и определение текущего хода.
 */
public class FigureManager {

    /**
     * Статический экземпляр менеджера для реализации Singleton
     */
    private static FigureManager instance;

    /**
     * Текущая выбранная фигура (на которую кликнул пользователь)
     */
    private Figure selectedFigure;

    /**
     * Номер команды, начинающей игру
     */
    private final int defaultTeamNumber = 1;

    /**
     * Номер команды, чей сейчас ход
     */
    private int currentTeam;

    /**
     * Список команд, проигравших в игре
     */
    private List<Integer> losingTeams = new ArrayList<>();

    /**
     * Список команд, все еще участвующих в игре
     */
    private List<Integer> liveTeams = new ArrayList<>();

    /**
     * Возвращает номер команды, чей сейчас ход.
     *
     * @return номер текущей команды (1-4)
     */
    public int getCurrentTeam() {
        return currentTeam;
    }

    /**
     * Устанавливает номер команды, чей сейчас ход.
     *
     * @param currentTeam номер текущей команды (1-4)
     */
    public void setCurrentTeam(int currentTeam) {
        this.currentTeam = currentTeam;
    }

    /**
     * Возвращает единственный экземпляр FigureManager (Singleton паттерн).
     *
     * @return экземпляр FigureManager
     */
    public static FigureManager getInstance() {
        return instance;
    }

    /**
     * Конструктор менеджера фигур. Создает экземпляр Singleton и инициализирует доску.
     */
    public FigureManager() {
        instance = this;
        initDefaultBoard();
    }

    /**
     * Инициализирует игровую доску стандартной расстановкой фигур.
     * Загружает расстановку из файла и сбрасывает все игровые состояния.
     */
    public void initDefaultBoard(){
        currentTeam = defaultTeamNumber;
        liveTeams = new ArrayList<>(Arrays.asList(1,2,3,4));
        loadBoardFromFile(getBoardFilePath());
    }

    /**
     * Возвращает путь к файлу с конфигурацией доски.
     *
     * @return абсолютный путь к файлу chess_board.txt
     */
    private String getBoardFilePath() {
        return System.getProperty("user.dir") + "\\" + Config.PATH_PROJECT + "res\\chess_board.txt";
    }

    /**
     * Загружает расстановку фигур из текстового файла.
     * Формат файла: каждая строка содержит коды фигур, разделенные пробелами.
     *
     * @param filename путь к файлу с расстановкой фигур
     */
    public void loadBoardFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            processBoardFile(reader);
        } catch (IOException e) {
            handleLoadError(e);
        }
    }

    /**
     * Обрабатывает файл с доской, построчно читая расстановку фигур.
     *
     * @param reader BufferedReader для чтения файла
     * @throws IOException если произошла ошибка чтения файла
     */
    private void processBoardFile(BufferedReader reader) throws IOException {
        String line;
        int row = 0;

        while ((line = reader.readLine()) != null && row < GameConfig.BOARD_SIZE_IN_CELL) {
            processBoardRow(line, row);
            row++;
        }
    }

    /**
     * Обрабатывает одну строку файла с доской.
     * Создает фигуры в соответствии с кодами в строке.
     *
     * @param line строка с кодами фигур
     * @param row номер строки на доске (координата Y)
     */
    private void processBoardRow(String line, int row) {
        String[] cells = line.trim().split("\\s+");

        for (int col = 0; col < Math.min(cells.length, GameConfig.BOARD_SIZE_IN_CELL); col++) {
            String cellData = cells[col];
            if (!isCellEmpty(cellData)) {
                createFigureFromCellData(cellData, col, row);
            }
        }
    }

    /**
     * Проверяет, является ли ячейка пустой по ее коду.
     * Код "00" означает пустую ячейку.
     *
     * @param cellData код ячейки (например, "K1" для короля команды 1)
     * @return true если ячейка пустая, false если содержит фигуру
     */
    private boolean isCellEmpty(String cellData) {
        return "00".equals(cellData);
    }

    /**
     * Создает фигуру на основе кода ячейки.
     * Код состоит из типа фигуры (K, Q, R, B, N, P) и номера команды (1-4).
     *
     * @param cellData код фигуры (например, "K1")
     * @param col координата X на доске
     * @param row координата Y на доске
     */
    private void createFigureFromCellData(String cellData, int col, int row) {
        char type = cellData.charAt(0);
        int team = Character.getNumericValue(cellData.charAt(1));

        GameObject figureObj = createFigureGameObject(type);
        if (figureObj == null) return;

        Figure figure = initializeFigure(figureObj, type, team, col, row);
        if (figure != null) {
            registerFigure(figure);
        }
    }

    /**
     * Создает игровой объект фигуры по ее типу.
     *
     * @param type символьный код типа фигуры (K, Q, R, B, N, P)
     * @return созданный GameObject фигуры или null если тип неизвестен
     */
    private GameObject createFigureGameObject(char type) {
        return GameObject.instantiate(Config.PATH_PROJECT_LOCAL + "prefabs\\figure" + type + ".prefab");
    }

    /**
     * Инициализирует фигуру, устанавливая все необходимые параметры.
     *
     * @param figureObj игровой объект фигуры
     * @param type тип фигуры
     * @param team номер команды
     * @param col координата X
     * @param row координата Y
     * @return инициализированная фигура или null если инициализация не удалась
     */
    private Figure initializeFigure(GameObject figureObj, char type, int team, int col, int row) {
        figureObj.setViewOrder(0);

        Figure figure = figureObj.getScript(Figure.class);
        if (figure == null) return null;

        Cell<Figure> cell = (Cell<Figure>) Matrix.getInstance().getCell(col, row);
        figure.initFigure(type, team, new Vector2(col, row), cell);
        figure.setEndMoveHandler(this::handleEndMove);

        return figure;
    }

    /**
     * Регистрирует фигуру в контроллере списка фигур.
     *
     * @param figure фигура для регистрации
     */
    private void registerFigure(Figure figure) {
        FigureListController.getInstance().addFigure(figure);
    }

    /**
     * Обрабатывает ошибку загрузки доски.
     *
     * @param e исключение, возникшее при загрузке
     */
    private void handleLoadError(IOException e) {
        System.err.println("Ошибка загрузки доски: " + e.getMessage());
    }

    /**
     * Обработчик завершения хода фигуры.
     * Выполняет захват фигур, обновление клеток и передачу хода следующей команде.
     *
     * @param figure фигура, завершившая ход
     */
    private void handleEndMove(Figure figure) {
        captureFigureIfPresent(figure);
        updateFigureCell(figure);
        if (figure.isFirstStep() && figure instanceof King && ((King)figure).checkRook()){
            System.out.println("rooooook");
            ((King) figure).moveRook();
        }
        else{
            figure.setFirstStep(false);
            switchToNextTeam();
        }
    }

    /**
     * Захватывает фигуру на целевой клетке, если она существует.
     *
     * @param figure фигура, совершающая ход
     */
    private void captureFigureIfPresent(Figure figure) {
        Figure targetFigure = figure.getCurrentCell().getValue();
        if (targetFigure != null) {
            captureFigure(targetFigure, figure.getTeam());
        }
    }

    /**
     * Захватывает фигуру и регистрирует этот факт.
     *
     * @param figure фигура, которая была захвачена
     * @param capturingTeam номер команды, захватившей фигуру
     */
    private void captureFigure(Figure figure, int capturingTeam) {
        FigureListController.getInstance().addCapturedFigure(figure, capturingTeam);
        figure.captured();
    }

    /**
     * Обновляет клетку после перемещения фигуры.
     *
     * @param figure фигура, которая переместилась
     */
    private void updateFigureCell(Figure figure) {
        figure.getCurrentCell().setValue(figure);
    }

    /**
     * Передает ход следующей команде в порядке 1→2→3→4→1.
     * Пропускает команды, уже проигравшие в игре.
     */
    private void switchToNextTeam() {
        int newTeam = calculateNextTeam();
        setCurrentTeam(newTeam);
        if (!GameManager.gameOnPause)
            Board.getInstance().renderCurrentTeamMark(newTeam);
    }

    /**
     * Вычисляет номер следующей команды для хода.
     *
     * @return номер следующей команды (1-4)
     */
    private int calculateNextTeam() {
        int newTeam = getCurrentTeam() + 1;
        if (newTeam > 4) {
            newTeam = 1;
        }

        return findNextActiveTeam(newTeam);
    }

    /**
     * Находит следующую активную команду (не проигравшую).
     *
     * @param startTeam номер команды, с которой начинается поиск
     * @return номер следующей активной команды
     */
    private int findNextActiveTeam(int startTeam) {
        int team = startTeam;
        int attempts = 0;
        final int maxAttempts = 4;

        while (losingTeams.contains(team) && attempts < maxAttempts) {
            team = (team % 4) + 1;
            attempts++;
        }

        return team;
    }

    /**
     * Устанавливает выбранную фигуру.
     * Изменяет порядок отображения предыдущей и новой выбранной фигуры.
     *
     * @param figure фигура, которую нужно выбрать
     */
    public void setSelectedFigure(Figure figure) {
        updateSelectedFigureViewOrder(selectedFigure, 0);
        selectedFigure = figure;
        updateSelectedFigureViewOrder(selectedFigure, -1);
    }

    /**
     * Обновляет порядок отображения выбранной фигуры.
     *
     * @param figure фигура для обновления
     * @param viewOrder новый порядок отображения
     */
    private void updateSelectedFigureViewOrder(Figure figure, int viewOrder) {
        if (figure != null) {
            figure.gameObject.setViewOrder(viewOrder);
        }
    }

    /**
     * Возвращает текущую выбранную фигуру.
     *
     * @return выбранная фигура или null если фигура не выбрана
     */
    public Figure getSelectedFigure() {
        return selectedFigure;
    }

    /**
     * Проверяет, является ли указанная фигура выбранной.
     *
     * @param figure фигура для проверки
     * @return true если фигура выбрана, false в противном случае
     */
    public boolean isSelected(Figure figure) {
        return figure == selectedFigure;
    }

    /**
     * Проверяет, находится ли выбранная фигура в процессе перемещения.
     *
     * @return true если выбранная фигура перемещается, false в противном случае
     */
    public boolean selectedIsMove() {
        return selectedFigure != null && selectedFigure.transform.isTranslate;
    }

    /**
     * Помечает команду как проигравшую.
     * Удаляет все фигуры команды и проверяет условия окончания игры.
     *
     * @param team номер проигравшей команды
     */
    public void setLosingTeam(int team) {
        removeTeamFigures(team);
        losingTeams.add(team);
        liveTeams.remove((Object)team);
        checkGameEndCondition();
    }

    /**
     * Удаляет все фигуры указанной команды.
     *
     * @param team номер команды для удаления
     */
    private void removeTeamFigures(int team) {
        for (Figure figure : FigureListController.getInstance().getFiguresByTeam(team)) {
            figure.captured();
        }
        FigureListController.getInstance().clearForTeam(team);
    }

    /**
     * Проверяет условия окончания игры.
     * Игра заканчивается, когда все оставшиеся команды одного цвета (все четные или все нечетные).
     */
    private void checkGameEndCondition() {
        var check = liveTeams.stream().allMatch(n -> n % 2 == 0);
        var check2 = liveTeams.stream().allMatch(n -> n % 2 == 1);

        if ((check || check2) && losingTeams.size() >= 2) {
            GameManager.getInstance().endGame(currentTeam);
        }
    }

    /**
     * Уничтожает все фигуры и очищает все списки.
     * Используется при рестарте игры.
     */
    public void DestroyAllFiguresAndClearList(){
        losingTeams.clear();
        liveTeams.clear();
        for (Figure figure : FigureListController.getInstance().getAllFigures()){
            figure.captured();
        }

        FigureListController.getInstance().clearAll();
    }
}