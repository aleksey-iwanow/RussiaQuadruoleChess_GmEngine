package org.games.chess.src.utils;

import org.games.chess.src.board.Cell;

@FunctionalInterface
public interface CellEventHandler<T> {
    void handle(Cell<T> cell);
}