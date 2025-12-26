package org.games.chess.src.figure;

import GDK.engine.*;
import GDK.engine.components.CanvasRenderer;
import GDK.engine.components.ParticleSystem;
import javafx.scene.paint.Color;
import org.games.chess.src.GameConfig;
import org.games.chess.src.board.Cell;
import org.games.chess.src.board.Matrix;
import org.games.chess.src.board.WallController;
import org.games.chess.src.utils.MoveEventHandler;

import java.util.List;

/**
 * Базовый класс для всех шахматных фигур.
 * Определяет общую логику перемещения, взаимодействия и отображения фигур.
 */
public class Figure extends ScriptControl {

    /**
     * Прозрачность отладочной графики выделения фигур
     */
    private static final float DEBUG_OPACITY = 0.4f;

    /**
     * Смещение порядка отображения для отладочного canvas
     */
    private static final int DEBUG_VIEW_ORDER_OFFSET = 1;

    /**
     * Canvas для отрисовки отладочной информации (выделение, возможные ходы)
     */
    private CanvasRenderer debugCanvas;

    /**
     * Тип фигуры (символьный код: K, Q, R, B, N, P)
     */
    private char type;

    /**
     * Номер команды фигуры (1-4)
     */
    private int team;

    /**
     * Массив клеток, на которые фигура может переместиться
     */
    private Vector2[] possibleTargets = new Vector2[0];

    /**
     * Флаг, указывающий находится ли фигура в процессе перемещения
     */
    private boolean isMoving;

    /**
     * Статический флаг блокировки всех фигур (используется при паузе/окончании игры)
     */
    public static boolean block;

    /**
     * Флаг, указывающий совершает ли фигура первый ход
     */
    private boolean firstStep = true;

    /**
     * Возвращает значение флага первого хода.
     *
     * @return true если фигура еще не совершала ходов, false в противном случае
     */
    public boolean isFirstStep() {return firstStep;}

    /**
     * Обработчик начала перемещения фигуры
     */
    private MoveEventHandler onStartMove;

    /**
     * Обработчик завершения перемещения фигуры
     */
    private MoveEventHandler onEndMove;

    /**
     * Клетка, на которой находится фигура
     */
    protected Cell<Figure> cell;

    /**
     * Система частиц для визуализации уничтожения фигуры
     */
    private ParticleSystem destructionParticles;

    /**
     * Устанавливает обработчик начала перемещения.
     *
     * @param handler функция-обработчик начала перемещения
     */
    public void setStartMoveHandler(MoveEventHandler handler) {
        onStartMove = handler;
    }

    /**
     * Устанавливает обработчик завершения перемещения.
     *
     * @param handler функция-обработчик завершения перемещения
     */
    public void setEndMoveHandler(MoveEventHandler handler) {
        onEndMove = handler;
    }

    /**
     * Возвращает тип фигуры.
     *
     * @return символьный код типа фигуры
     */
    public char getType() {
        return type;
    }

    /**
     * Возвращает номер команды фигуры.
     *
     * @return номер команды (1-4)
     */
    public int getTeam() {
        return team;
    }

    /**
     * Возвращает текущую клетку фигуры.
     *
     * @return клетка, на которой находится фигура
     */
    public Cell<Figure> getCurrentCell() {
        return cell;
    }

    /**
     * Инициализирует фигуру с заданными параметрами.
     * Настраивает внешний вид, позицию и вспомогательные системы.
     *
     * @param type тип фигуры
     * @param team номер команды
     * @param cellPos позиция на доске в клетках
     * @param cell объект клетки, на которой находится фигура
     */
    public void initFigure(char type, int team, Vector2 cellPos, Cell<Figure> cell) {
        this.type = type;
        this.team = team;
        this.cell = cell;
        cell.setValue(this);

        initializeTransform(cellPos);
        initializeDebugCanvas();
        initializeImage();
        initializeDestructionParticles();
    }

    /**
     * Инициализирует систему частиц для визуализации уничтожения фигуры.
     * Настраивает параметры взрыва частиц при захвате фигуры.
     */
    private void initializeDestructionParticles() {
        destructionParticles = new ParticleSystem(gameObject);

        destructionParticles.isPlaying = false;
        destructionParticles.destroyOnEnd = true;
        destructionParticles.loop = false;
        destructionParticles.duration = 0.3;

        destructionParticles.emitterPosition = new Vector2(
                GameConfig.CELL_SIZE / 2,
                GameConfig.CELL_SIZE / 2
        );
        destructionParticles.emissionRate = 10000.0;
        destructionParticles.maxParticles = 20;


        destructionParticles.particleType = ParticleSystem.ParticleType.CIRCLE;
        destructionParticles.movementType = ParticleSystem.MovementType.EXPLOSION;
        destructionParticles.startSpeed = 100.0;
        destructionParticles.startSpeedVariation = 20.0;
        destructionParticles.startSize = 10.0;
        destructionParticles.startSizeVariation = 2.0;
        destructionParticles.lifeTime = 0.3;
        destructionParticles.lifeTimeVariation = 0;

        destructionParticles.startColor = Color.RED;

        destructionParticles.fadeOut = true;
        destructionParticles.useGravity = false;

        gameObject.addComponent(destructionParticles);
    }

    /**
     * Инициализирует трансформ фигуры, устанавливая позицию и размер.
     *
     * @param cellPos позиция на доске в клетках
     */
    private void initializeTransform(Vector2 cellPos) {
        Vector2 position = calculateGlobalPosition(cellPos);
        transform.setPos(position);
        transform.setSize(GameConfig.CELL_SIZE);
    }

    /**
     * Создает canvas для отрисовки отладочной информации.
     * Используется для выделения фигуры и отображения возможных ходов.
     */
    private void initializeDebugCanvas() {
        debugCanvas = CanvasRenderer.createCanvasObject(
                "figureDebugCanvas_" + type + team,
                Vector2.zero(),
                gameObject.getViewOrder() + DEBUG_VIEW_ORDER_OFFSET);
    }

    /**
     * Инициализирует изображение фигуры в зависимости от ее типа и команды.
     * Загружает соответствующую текстуру из папки ресурсов.
     */
    private void initializeImage() {
        String imagePath = buildImagePath();
        gameObject.image.setImageView(imagePath);
        gameObject.image.setPadding(2, 2);
        gameObject.image.setPreserveAspectRatio(true);
    }


    /**
     * Метод обновления, вызываемый каждый кадр.
     * Отвечает за отрисовку отладочной графики.
     */
    @Override
    public void update() {
        if (debugCanvas == null) return;

        debugCanvas.clear();
        renderSelectionAndTargets();
    }

    /**
     * Метод позднего обновления, вызываемый после всех update().
     * Обрабатывает выбор фигуры и завершение перемещения.
     */
    @Override
    public void lateUpdate() {
        if (block) return;

        handleFigureSelection();
        handleMoveCompletion();
    }

    /**
     * Обработчик наведения курсора на фигуру.
     * Выбирает фигуру при нажатии левой кнопки мыши.
     */
    @Override
    public void onHover() {
        if (block) return;

        if (shouldSelectFigure()) {
            selectFigure();
        }
    }

    /**
     * Строит путь к изображению фигуры на основе ее типа и команды.
     *
     * @return путь к файлу изображения
     */
    private String buildImagePath() {
        int imageTeam = team > 2 ? team - 2 : team;
        return "img/" + imageTeam + "_" + type + ".png";
    }

    /**
     * Отрисовывает выделение фигуры и возможные цели для хода.
     */
    private void renderSelectionAndTargets() {
        if (!isMoving && FigureManager.getInstance().isSelected(this)) {
            renderSelectionHighlight();
            renderPossibleTargets();
        }
    }

    /**
     * Отрисовывает подсветку выбранной фигуры.
     */
    private void renderSelectionHighlight() {
        debugCanvas.setColor(GameConfig.SELECTED_COLOR);
        debugCanvas.setOpacity(DEBUG_OPACITY);
        debugCanvas.drawFilledRect(transform.position, transform.size);
    }

    /**
     * Отрисовывает возможные клетки для хода.
     */
    private void renderPossibleTargets() {
        if (possibleTargets == null) return;

        debugCanvas.setColor(GameConfig.SELECTED_COLOR_2);
        debugCanvas.setOpacity(DEBUG_OPACITY);

        for (Vector2 target : possibleTargets) {
            Vector2 globalPos = toGlobal(target);
            debugCanvas.drawFilledRect(globalPos, transform.size);
        }
    }

    /**
     * Обрабатывает выбор фигуры для перемещения.
     */
    private void handleFigureSelection() {
        if (shouldStartMove()) {
            Vector2 targetCellPos = getTargetForMoveAtPos(Input.mousePosition);
            if (targetCellPos != null) {
                startMoveToCell(targetCellPos);
            }
        }
    }

    /**
     * Проверяет, можно ли начать перемещение фигуры.
     *
     * @return true если фигура выбрана и нажата левая кнопка мыши
     */
    private boolean shouldStartMove() {
        return FigureManager.getInstance().isSelected(this) &&
                !transform.isTranslate &&
                Input.getMouseButton(0);
    }

    /**
     * Начинает перемещение фигуры на указанную клетку.
     * Освобождает текущую клетку и запускает анимацию перемещения.
     *
     * @param targetCellPos позиция целевой клетки в координатах доски
     */
    protected void startMoveToCell(Vector2 targetCellPos) {
        isMoving = true;

        if (cell != null) {
            cell.clear();
        }

        cell = (Cell<Figure>) Matrix.getInstance().getCell(targetCellPos);
        Vector2 targetPosition = toGlobal(targetCellPos);

        notifyMoveStart();
        transform.translateToTarget(targetPosition, 10, 400);
    }

    /**
     * Уведомляет обработчики о начале перемещения.
     */
    private void notifyMoveStart() {
        if (onStartMove != null) {
            onStartMove.handle(this);
        }
    }

    /**
     * Обрабатывает завершение перемещения фигуры.
     */
    private void handleMoveCompletion() {
        if (isMoving && !transform.isTranslate) {
            completeMove();
        }
    }

    /**
     * Завершает перемещение фигуры.
     * Уведомляет обработчики и сбрасывает выбор фигуры.
     */
    private void completeMove() {
        isMoving = false;
        notifyMoveEnd();
        FigureManager.getInstance().setSelectedFigure(null);
    }

    /**
     * Уведомляет обработчики о завершении перемещения.
     */
    private void notifyMoveEnd() {
        if (onEndMove != null) {
            onEndMove.handle(this);
        }
    }

    /**
     * Обработчик захвата фигуры.
     * Может быть переопределен в подклассах для добавления специфичного поведения.
     */
    protected void onCaptured() {
        // Override in subclasses for specific capture behavior
    }

    /**
     * Преобразует координаты клетки в глобальные координаты экрана.
     *
     * @param cellPos позиция клетки в координатах доски
     * @return глобальные координаты на экране
     */
    private Vector2 toGlobal(Vector2 cellPos) {
        return calculateGlobalPosition(cellPos);
    }

    /**
     * Вычисляет глобальную позицию на основе координат клетки.
     *
     * @param cellPos позиция клетки в координатах доски
     * @return глобальные координаты на экране
     */
    private Vector2 calculateGlobalPosition(Vector2 cellPos) {
        return cellPos.increaseVector(GameConfig.CELL_SIZE).addVector(GameConfig.PADDING);
    }

    /**
     * Возвращает массив клеток, на которые может переместиться фигура.
     * Абстрактный метод, должен быть реализован в подклассах.
     *
     * @param cell текущая клетка фигуры
     * @return массив возможных целевых клеток
     */
    protected Vector2[] getTargetsCells(Cell<Figure> cell) {
        return new Vector2[0];
    }

    /**
     * Определяет целевую клетку для перемещения на основе позиции мыши.
     * Проверяет, находится ли позиция мыши на одной из возможных целевых клеток.
     *
     * @param mousePosition позиция мыши в глобальных координатах
     * @return координаты целевой клетки или null если ход невозможен
     */
    protected Vector2 getTargetForMoveAtPos(Vector2 mousePosition) {
        Vector2 gridPos = convertToGridPosition(mousePosition);
        return findValidTarget(gridPos);
    }

    /**
     * Преобразует глобальные координаты в координаты клетки на доске.
     *
     * @param mousePosition позиция мыши в глобальных координатах
     * @return координаты клетки на доске
     */
    private Vector2 convertToGridPosition(Vector2 mousePosition) {
        Vector2 adjustedPos = mousePosition.subtractVector(GameConfig.PADDING);
        return adjustedPos.divideVector(GameConfig.CELL_SIZE).vecInt();
    }

    /**
     * Проверяет, является ли позиция мыши валидной целевой клеткой.
     *
     * @param gridPos координаты клетки для проверки
     * @return координаты клетки если она валидна, null в противном случае
     */
    private Vector2 findValidTarget(Vector2 gridPos) {
        if (possibleTargets == null) return null;

        for (Vector2 target : possibleTargets) {
            if (target.equal(gridPos)) {
                return gridPos;
            }
        }
        return null;
    }

    /**
     * Проверяет, являются ли координаты валидными (в пределах доски).
     *
     * @param x координата X клетки
     * @param y координата Y клетки
     * @return true если клетка находится в пределах доски, false в противном случае
     */
    protected boolean isValidCell(int x, int y) {
        return x >= 0 && x < GameConfig.BOARD_SIZE_IN_CELL &&
                y >= 0 && y < GameConfig.BOARD_SIZE_IN_CELL;
    }

    /**
     * Возвращает клетку по заданным координатам.
     *
     * @param x координата X клетки
     * @param y координата Y клетки
     * @return объект клетки или null если координаты невалидны
     */
    protected Cell<Figure> getCellAt(int x, int y) {
        if (!isValidCell(x, y)) return null;
        return (Cell<Figure>) Matrix.getInstance().getCell(x, y);
    }

    /**
     * Проверяет, может ли фигура переместиться на указанную клетку.
     * Проверяет активность клетки, наличие стен и наличие других фигур.
     *
     * @param targetCell целевая клетка для проверки
     * @return true если фигура может переместиться на клетку, false в противном случае
     */
    protected boolean canMoveToCell(Cell<Figure> targetCell) {
        if (targetCell == null || !targetCell.isActive() || doesPathCrossWall(targetCell.getPos().xInt(), targetCell.getPos().yInt())) return false;

        Figure figureOnCell = targetCell.getValue();
        return figureOnCell == null || isEnemyFigure(figureOnCell);
    }

    /**
     * Добавляет клетку в список возможных целей если она валидна.
     *
     * @param targets список целевых клеток
     * @param x координата X проверяемой клетки
     * @param y координата Y проверяемой клетки
     */
    protected void addTargetIfValid(List<Vector2> targets, int x, int y) {
        Cell<Figure> targetCell = getCellAt(x, y);
        if (canMoveToCell(targetCell)) {
            targets.add(new Vector2(x, y));
        }
    }

    /**
     * Проверяет клетку в заданном направлении на возможность перемещения.
     *
     * @param targets список целевых клеток
     * @param x координата X проверяемой клетки
     * @param y координата Y проверяемой клетки
     * @param stopAtEnemy флаг, указывающий нужно ли останавливаться при встрече вражеской фигуры
     * @return true если проверка продолжается, false если нужно остановиться
     */
    protected boolean checkDirection(List<Vector2> targets, int x, int y, boolean stopAtEnemy) {
        Cell<Figure> targetCell = getCellAt(x, y);
        if (targetCell == null || !targetCell.isActive()) return false;

        Figure figureOnCell = targetCell.getValue();

        if (isEmptyCell(targetCell)) {
            targets.add(new Vector2(x, y));
            return true;
        } else if (isEnemyFigure(figureOnCell)) {
            targets.add(new Vector2(x, y));
            return !stopAtEnemy;
        }

        return false;
    }

    /**
     * Обходит все клетки в заданном направлении до границы доски или препятствия.
     *
     * @param targets список целевых клеток
     * @param startX начальная координата X
     * @param startY начальная координата Y
     * @param dx направление по X (1, 0 или -1)
     * @param dy направление по Y (1, 0 или -1)
     */
    protected void traverseDirection(List<Vector2> targets, int startX, int startY,
                                     int dx, int dy) {
        for (int step = 1; step < GameConfig.BOARD_SIZE_IN_CELL; step++) {
            int newX = startX + dx * step;
            int newY = startY + dy * step;
            if (doesPathCrossWall(startX, startY, newX, newY)) break;
            if (!checkDirection(targets, newX, newY, true)) {
                break;
            }
        }
    }

    /**
     * Добавляет клетку в список целей если на ней находится вражеская фигура.
     *
     * @param targets список целевых клеток
     * @param targetX координата X проверяемой клетки
     * @param targetY координата Y проверяемой клетки
     */
    protected void addCaptureIfEnemy(List<Vector2> targets, int targetX, int targetY) {
        Cell<Figure> targetCell = getCellAt(targetX, targetY);
        if (targetCell != null && targetCell.isActive()) {
            Figure figureOnCell = targetCell.getValue();
            if (isEnemyFigure(figureOnCell) && !doesPathCrossWall(targetX, targetY)) {
                targets.add(new Vector2(targetX, targetY));
            }
        }
    }

    /**
     * Проверяет, можно ли выбрать данную фигуру.
     *
     * @return true если фигура принадлежит текущей команде и не происходит другое перемещение
     */
    private boolean shouldSelectFigure() {
        return team == FigureManager.getInstance().getCurrentTeam() &&
                Input.getMouseButton(0) &&
                !FigureManager.getInstance().selectedIsMove();
    }

    /**
     * Выбирает данную фигуру.
     * Устанавливает фигуру как выбранную и вычисляет возможные ходы.
     */
    private void selectFigure() {
        FigureManager.getInstance().setSelectedFigure(this);
        possibleTargets = getTargetsCells(cell);
    }

    /**
     * Обработчик захвата фигуры.
     * Запускает визуальные эффекты уничтожения и удаляет фигуру из игры.
     */
    public void captured() {captured(false);}

    /**
     * Обработчик захвата фигуры с опцией отключения визуальных эффектов.
     *
     * @param withoutParticles если true, то частицы не будут воспроизведены
     */
    public void captured(boolean withoutParticles) {
        onCaptured();

        if (destructionParticles != null && !withoutParticles) {
            gameObject.image.setVisible(false);
            destructionParticles.play();

            invoke(gameObject::destroy, (int)(destructionParticles.duration*1000)+10);
        } else {
            gameObject.destroy();
        }

        if (cell != null) {
            cell.clear();
        }
    }

    /**
     * Проверяет, является ли указанная фигура вражеской.
     * Вражескими считаются фигуры команд противоположного цвета (1-3 против 2-4).
     *
     * @param figure фигура для проверки
     * @return true если фигура вражеская, false в противном случае
     */
    protected boolean isEnemyFigure(Figure figure) {
        return figure != null && figure.getTeam() % 2 != this.getTeam() % 2;
    }

    /**
     * Проверяет, является ли указанная фигура союзной.
     *
     * @param figure фигура для проверки
     * @return true если фигура союзная, false в противном случае
     */
    protected boolean isAllyFigure(Figure figure) {
        return figure != null && figure.getTeam() == this.getTeam();
    }

    /**
     * Проверяет, является ли указанная клетка пустой.
     *
     * @param cell клетка для проверки
     * @return true если клетка пустая, false в противном случае
     */
    protected boolean isEmptyCell(Cell<Figure> cell) {
        return cell != null && cell.getValue() == null;
    }

    /**
     * Проверяет, пересекает ли путь от текущей позиции к целевой клетке стену.
     *
     * @param newX координата X целевой клетки
     * @param newY координата Y целевой клетки
     * @return true если путь пересекает стену, false в противном случае
     */
    protected boolean doesPathCrossWall(int newX, int newY) {
        var pos = this.cell.getPos();
        return doesPathCrossWall(pos.xInt(), pos.yInt(), newX, newY);
    }

    /**
     * Проверяет, пересекает ли отрезок между двумя точками на доске стену.
     *
     * @param startX координата X начальной точки
     * @param startY координата Y начальной точки
     * @param newX координата X конечной точки
     * @param newY координата Y конечной точки
     * @return true если отрезок пересекает стену, false в противном случае
     */
    protected boolean doesPathCrossWall(int startX, int startY, int newX, int newY) {
        return WallController.getInstance().doesChessPathCrossWall(new Vector2(startX, startY), new Vector2(newX, newY));
    }

    /**
     * Устанавливает флаг первого хода фигуры.
     *
     * @param b значение флага первого хода
     */
    public void setFirstStep(boolean b){
        firstStep = b;
    }
}