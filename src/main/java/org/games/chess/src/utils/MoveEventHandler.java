package org.games.chess.src.utils;

import org.games.chess.src.figure.Figure;

@FunctionalInterface
public interface MoveEventHandler {
    void handle(Figure figure);
}