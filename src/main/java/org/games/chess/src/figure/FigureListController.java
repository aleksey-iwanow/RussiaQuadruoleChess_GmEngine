package org.games.chess.src.figure;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер для хранения всех фигур (и живых, и убитых) всех команд.
 * Реализует паттерн Singleton и предоставляет методы для управления коллекциями фигур.
 */
public class FigureListController {

    /**
     * Статический экземпляр контроллера для реализации Singleton
     */
    private static FigureListController instance;

    /**
     * Список всех живых фигур на доске
     */
    private final List<Figure> allFigures; // Все фигуры на доске

    /**
     * Список всех убитых фигур с дополнительной информацией о захвате
     */
    private final List<CapturedFigureData> capturedFigures; // Убитые фигуры

    /**
     * Маппинг команд к их живым фигурам
     */
    private final Map<Integer, List<Figure>> figuresByTeam; // Фигуры по командам (живые)

    /**
     * Маппинг команд к захваченным ими фигурам
     */
    private final Map<Integer, List<CapturedFigureData>> capturedByTeam; // Убитые по командам

    /**
     * Возвращает единственный экземпляр FigureListController.
     * Создает новый экземпляр, если он еще не существует.
     *
     * @return экземпляр FigureListController
     */
    public static FigureListController getInstance() {
        if (instance == null) {
            instance = new FigureListController();
        }
        return instance;
    }

    /**
     * Приватный конструктор контроллера.
     * Инициализирует все коллекции для 4 команд.
     */
    private FigureListController() {
        allFigures = new ArrayList<>();
        capturedFigures = new ArrayList<>();
        figuresByTeam = new HashMap<>();
        capturedByTeam = new HashMap<>();

        for (int i = 1; i <= 4; i++) {
            figuresByTeam.put(i, new ArrayList<>());
            capturedByTeam.put(i, new ArrayList<>());
        }
    }

    // === МЕТОДЫ ДЛЯ ВСЕХ ФИГУР ===

    /**
     * Добавляет фигуру в общий список живых фигур.
     * Также добавляет фигуру в список соответствующей команды.
     *
     * @param figure фигура для добавления
     */
    public void addFigure(Figure figure) {
        if (figure == null) return;

        allFigures.add(figure);
        int team = figure.getTeam();

        // Добавляем в список команды
        List<Figure> teamFigures = figuresByTeam.get(team);
        if (teamFigures != null) {
            teamFigures.add(figure);
        }

        System.out.println("Фигура " + figure.getClass().getSimpleName() + " команды " + team + " добавлена");
    }

    /**
     * Удаляет фигуру из общего списка живых фигур.
     * Также удаляет фигуру из списка соответствующей команды.
     *
     * @param figure фигура для удаления
     */
    public void removeFigure(Figure figure) {
        if (figure == null) return;

        allFigures.remove(figure);
        int team = figure.getTeam();

        // Удаляем из списка команды
        List<Figure> teamFigures = figuresByTeam.get(team);
        if (teamFigures != null) {
            teamFigures.remove(figure);
        }

        System.out.println("Фигура " + figure.getClass().getSimpleName() + " команды " + team + " удалена");
    }

    /**
     * Возвращает копию списка всех живых фигур.
     *
     * @return список всех живых фигур
     */
    public List<Figure> getAllFigures() {
        return new ArrayList<>(allFigures);
    }

    /**
     * Возвращает список фигур определенного типа.
     *
     * @param figureType класс типа фигур для фильтрации
     * @return список фигур указанного типа
     */
    public List<Figure> getFiguresByType(Class<? extends Figure> figureType) {
        return allFigures.stream()
                .filter(figureType::isInstance)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список живых фигур определенной команды.
     *
     * @param team номер команды (1-4)
     * @return список фигур указанной команды
     */
    public List<Figure> getFiguresByTeam(int team) {
        return new ArrayList<>(figuresByTeam.getOrDefault(team, new ArrayList<>()));
    }

    /**
     * Возвращает список фигур определенной команды и типа.
     *
     * @param team номер команды (1-4)
     * @param figureType класс типа фигур для фильтрации
     * @return список фигур, удовлетворяющих условиям
     */
    public List<Figure> getFiguresByTeamAndType(int team, Class<? extends Figure> figureType) {
        return getFiguresByTeam(team).stream()
                .filter(figureType::isInstance)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает количество живых фигур определенной команды.
     *
     * @param team номер команды (1-4)
     * @return количество фигур команды
     */
    public int getFigureCountByTeam(int team) {
        return figuresByTeam.getOrDefault(team, new ArrayList<>()).size();
    }

    /**
     * Возвращает количество фигур определенной команды и типа.
     *
     * @param team номер команды (1-4)
     * @param figureType класс типа фигур для подсчета
     * @return количество фигур, удовлетворяющих условиям
     */
    public int getFigureCountByTeamAndType(int team, Class<? extends Figure> figureType) {
        return (int) getFiguresByTeam(team).stream()
                .filter(figureType::isInstance)
                .count();
    }

    // === МЕТОДЫ ДЛЯ УБИТЫХ ФИГУР ===

    /**
     * Добавляет фигуру в список убитых.
     * Удаляет фигуру из живых и добавляет запись о захвате.
     *
     * @param figure фигура, которая была убита
     * @param capturedTeam номер команды, убившей фигуру
     */
    public void addCapturedFigure(Figure figure, int capturedTeam) {
        if (figure == null) return;

        // Удаляем из живых
        removeFigure(figure);

        // Добавляем в убитые
        CapturedFigureData data = new CapturedFigureData(figure, capturedTeam);
        capturedFigures.add(data);

        // Добавляем в список команды, которая убила
        List<CapturedFigureData> teamCaptured = capturedByTeam.get(capturedTeam);
        if (teamCaptured != null) {
            teamCaptured.add(data);
        }

        System.out.println(data.getDisplayInfo() + " добавлена в список убитых");
    }

    /**
     * Возвращает копию списка всех убитых фигур.
     *
     * @return список всех убитых фигур
     */
    public List<CapturedFigureData> getAllCapturedFigures() {
        return new ArrayList<>(capturedFigures);
    }

    /**
     * Возвращает список фигур, убитых определенной командой.
     *
     * @param team номер команды, убившей фигуры (1-4)
     * @return список убитых фигур указанной командой
     */
    public List<CapturedFigureData> getCapturedFiguresByTeam(int team) {
        return new ArrayList<>(capturedByTeam.getOrDefault(team, new ArrayList<>()));
    }

    /**
     * Возвращает список фигур определенного типа, убитых определенной командой.
     *
     * @param team номер команды, убившей фигуры (1-4)
     * @param figureType класс типа фигур для фильтрации
     * @return список убитых фигур, удовлетворяющих условиям
     */
    public List<CapturedFigureData> getCapturedFiguresByTeamAndType(int team, Class<? extends Figure> figureType) {
        return getCapturedFiguresByTeam(team).stream()
                .filter(data -> figureType.isInstance(data.getFigure()))
                .collect(Collectors.toList());
    }

    /**
     * Удаляет запись об убитой фигуре.
     *
     * @param capturedFigure запись об убитой фигуре для удаления
     */
    public void removeCapturedFigure(CapturedFigureData capturedFigure) {
        if (capturedFigure == null) return;

        capturedFigures.remove(capturedFigure);
        int team = capturedFigure.getCapturedByTeam();

        List<CapturedFigureData> teamCaptured = capturedByTeam.get(team);
        if (teamCaptured != null) {
            teamCaptured.remove(capturedFigure);
        }
    }

    /**
     * Возвращает последнюю фигуру, убитую определенной командой.
     *
     * @param team номер команды (1-4)
     * @return последняя убитая фигура или null если таких нет
     */
    public CapturedFigureData getLastCapturedFigure(int team) {
        List<CapturedFigureData> figures = capturedByTeam.get(team);
        if (figures == null || figures.isEmpty()) return null;
        return figures.get(figures.size() - 1);
    }


    /**
     * Очищает все коллекции фигур.
     * Удаляет все живые и убитые фигуры всех команд.
     */
    public void clearAll() {
        allFigures.clear();
        capturedFigures.clear();
        for (List<Figure> figures : figuresByTeam.values()) {
            figures.clear();
        }
        for (List<CapturedFigureData> captured : capturedByTeam.values()) {
            captured.clear();
        }
    }

    /**
     * Очищает все фигуры определенной команды.
     * Удаляет как живые, так и убитые фигуры команды.
     *
     * @param team номер команды для очистки (1-4)
     */
    public void clearForTeam(int team) {
        // Удаляем живые фигуры команды
        List<Figure> teamFigures = figuresByTeam.get(team);
        if (teamFigures != null) {
            allFigures.removeAll(teamFigures);
            teamFigures.clear();
        }

        // Удаляем убитые фигуры команды
        List<CapturedFigureData> teamCaptured = capturedByTeam.get(team);
        if (teamCaptured != null) {
            capturedFigures.removeAll(teamCaptured);
            teamCaptured.clear();
        }
    }


    /**
     * Возвращает общее количество всех фигур (живых и убитых).
     *
     * @return суммарное количество фигур
     */
    public int getTotalFigureCount() {
        return allFigures.size() + capturedFigures.size();
    }

    /**
     * Находит фигуру по идентификатору.
     * Временная реализация - ищет по строковому представлению фигуры.
     *
     * @param id идентификатор для поиска
     * @return найденная фигура или null если не найдена
     */
    public Figure findFigureById(String id) {
        // Предполагая, что в Figure есть метод getId()
        return allFigures.stream()
                .filter(figure -> figure.toString().contains(id)) // временная реализация
                .findFirst()
                .orElse(null);
    }

    /**
     * Восстанавливает фигуру из списка убитых.
     * Переносит фигуру обратно в список живых.
     *
     * @param capturedFigure запись об убитой фигуре для восстановления
     */
    public void restoreFigure(CapturedFigureData capturedFigure) {
        if (capturedFigure == null) return;

        Figure figure = capturedFigure.getFigure();
        removeCapturedFigure(capturedFigure);
        addFigure(figure);

        System.out.println("Фигура " + figure.getClass().getSimpleName() + " восстановлена");
    }
}