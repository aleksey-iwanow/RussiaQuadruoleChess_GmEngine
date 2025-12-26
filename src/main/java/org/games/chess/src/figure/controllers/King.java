package org.games.chess.src.figure.controllers;

import GDK.engine.Vector2;
import org.games.chess.src.GameConfig;
import org.games.chess.src.GameManager;
import org.games.chess.src.board.Cell;
import org.games.chess.src.board.Matrix;
import org.games.chess.src.figure.Figure;
import org.games.chess.src.figure.FigureManager;

import java.util.ArrayList;
import java.util.Map;

/**
 * Класс фигуры Король (King).
 * Может перемещаться на одну клетку в любом направлении.
 * При потере короля команда проигрывает.
 * Поддерживает специальный ход - рокировку.
 */
public class King extends Figure {

    /**
     * Маппинг направлений для поиска ладьи при рокировке.
     * Указывает в каком направлении от короля находится ладья для каждой команды.
     */
    public final Map<Integer, Vector2> hideDirectionTeams = Map.of(
            4, new Vector2(0, 1),   // Команда 4: ладья находится справа (вниз по координатам)
            1, new Vector2(-1, 0),  // Команда 1: ладья находится слева
            2, new Vector2(0, -1),  // Команда 2: ладья находится сверху
            3, new Vector2(1, 0)    // Команда 3: ладья находится справа
    );

    /**
     * Ладья, участвующая в рокировке
     */
    private Rook hideRook;

    /**
     * Позиция ладьи для рокировки
     */
    private Vector2 hideRookPos;

    /**
     * Возвращает массив клеток, на которые может переместиться король.
     * Король может перемещаться на одну клетку в любом направлении.
     * Также включает клетку для рокировки при выполнении условий.
     *
     * @param cell текущая клетка короля
     * @return массив возможных целевых клеток
     */
    @Override
    protected Vector2[] getTargetsCells(Cell<Figure> cell) {
        if (cell == null) return new Vector2[0];

        Vector2 currentPos = cell.getPos();
        int currentX = (int)currentPos.x;
        int currentY = (int)currentPos.y;

        ArrayList<Vector2> targets = new ArrayList<>();

        int[][] directions = {
                {1, 0}, {1, 1}, {0, 1}, {-1, 1},
                {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
        };

        for (int[] dir : directions) {
            addTargetIfValid(targets, currentX + dir[0], currentY + dir[1]);
        }

        var hideVec = hideDirectionTeams.get(getTeam());
        var targetPoint = hideVec.increaseVector(2).addVector(currentPos);
        if (isFirstStep()
                && checkMove(hideVec.addVector(currentPos))
                && checkMove(targetPoint)
                && checkRook(hideVec.increaseVector(3).addVector(currentPos))
        ){
            targets.add(targetPoint);
        }

        return targets.toArray(new Vector2[0]);
    }

    /**
     * Обработчик захвата короля.
     * При захвате короля его команда проигрывает.
     */
    @Override
    protected void onCaptured(){
        if (GameManager.gameOnPause) return;

        FigureManager.getInstance().setLosingTeam(getTeam());
    }

    /**
     * Проверяет, может ли король переместиться на указанную позицию.
     * Проверяет только доступность клетки (не проверяет шах).
     *
     * @param vec позиция для проверки
     * @return true если клетка доступна, false в противном случае
     */
    private boolean checkMove(Vector2 vec) {
        Cell<Figure> forwardCell = getCellAt(vec.xInt(), vec.yInt());
        return forwardCell != null && forwardCell.isActive() && forwardCell.isEmpty();
    }

    /**
     * Проверяет, находится ли на указанной позиции ладья той же команды.
     *
     * @param vec позиция для проверки
     * @return true если на позиции находится союзная ладья, false в противном случае
     */
    private boolean checkRook(Vector2 vec){
        Cell<Figure> rook = getCellAt(vec.xInt(), vec.yInt());
        return rook != null
                && rook.isActive()
                && rook.getValue() != null
                && rook.getValue().getTeam() == getTeam()
                && rook.getValue().getType() == 'R';
    }

    /**
     * Проверяет возможность рокировки.
     * Ищет ладью в направлении, указанном для команды короля.
     *
     * @return true если рокировка возможна, false в противном случае
     */
    public boolean checkRook() {
        Vector2 currentPos = cell.getPos();
        hideRookPos = currentPos.addVector(hideDirectionTeams.get(getTeam()));
        var rook = getCellAt(hideRookPos.xInt(), hideRookPos.yInt()).getValue();
        if (rook instanceof Rook && rook.isFirstStep()){
            hideRook = (Rook) rook;
            return true;
        }
        return false;
    }

    /**
     * Выполняет перемещение ладьи при рокировке.
     * Перемещает ладью на две клетки по направлению к королю.
     */
    public void moveRook() {
        hideRook.moveAtPoint(hideRookPos.addVector(hideDirectionTeams.get(getTeam()).negative().increaseVector(2)));
    }
}