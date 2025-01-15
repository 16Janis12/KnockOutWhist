package de.knockoutwhist.persistence;

public interface PersistanceManager {

    def saveCurrentGame(game: Game): Unit

}
