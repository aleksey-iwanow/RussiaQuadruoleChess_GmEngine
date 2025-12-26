package org.games.chess.src.figure;

/**
 * Data-класс для хранения информации об убитой фигуре.
 * Содержит ссылку на фигуру, информацию о захвате и временную метку.
 */
public class CapturedFigureData {

    /**
     * Ссылка на захваченную фигуру
     */
    private final Figure figure;

    /**
     * Номер команды, захватившей фигуру
     */
    private final int capturedByTeam;

    /**
     * Время захвата фигуры (миллисекунды с эпохи Unix)
     */
    private final long captureTime;

    /**
     * Тип фигуры в строковом представлении
     */
    private final String figureType;

    /**
     * Номер команды, которой принадлежала фигура
     */
    private final int figureTeam;

    /**
     * Создает новую запись о захваченной фигуре.
     *
     * @param figure захваченная фигура
     * @param capturedByTeam номер команды, захватившей фигуру
     */
    public CapturedFigureData(Figure figure, int capturedByTeam) {
        this.figure = figure;
        this.capturedByTeam = capturedByTeam;
        this.captureTime = System.currentTimeMillis();
        this.figureType = figure.getClass().getSimpleName();
        this.figureTeam = figure.getTeam();
    }

    // Getters

    /**
     * Возвращает захваченную фигуру.
     *
     * @return объект фигуры
     */
    public Figure getFigure() { return figure; }

    /**
     * Возвращает номер команды, захватившей фигуру.
     *
     * @return номер команды захватчика
     */
    public int getCapturedByTeam() { return capturedByTeam; }

    /**
     * Возвращает время захвата фигуры.
     *
     * @return время захвата в миллисекундах
     */
    public long getCaptureTime() { return captureTime; }

    /**
     * Возвращает тип фигуры в строковом представлении.
     *
     * @return название класса фигуры
     */
    public String getFigureType() { return figureType; }

    /**
     * Возвращает номер команды, которой принадлежала фигура.
     *
     * @return номер команды фигуры
     */
    public int getFigureTeam() { return figureTeam; }

    /**
     * Возвращает строковое представление времени, прошедшего с момента захвата.
     * Формат зависит от прошедшего времени: HH:MM:SS или MM:SS.
     *
     * @return отформатированная строка времени
     */
    public String getFormattedCaptureTime() {
        long seconds = (System.currentTimeMillis() - captureTime) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else {
            return String.format("%02d:%02d", minutes, seconds % 60);
        }
    }

    /**
     * Возвращает информацию о фигуре в читаемом формате.
     * Включает тип фигуры, команду фигуры и команду захватчика.
     *
     * @return строка с описанием захваченной фигуры
     */
    public String getDisplayInfo() {
        return String.format("%s команды %d (убита командой %d)",
                figureType, figureTeam, capturedByTeam);
    }

    /**
     * Возвращает строковое представление объекта.
     *
     * @return результат вызова getDisplayInfo()
     */
    @Override
    public String toString() {
        return getDisplayInfo();
    }
}