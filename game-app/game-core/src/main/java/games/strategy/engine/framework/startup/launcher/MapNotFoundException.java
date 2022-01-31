package games.strategy.engine.framework.startup.launcher;

import games.strategy.engine.framework.I18nEngineFramework;

/**
 * An unchecked exception indicating the map associated with a save game was not found and must be
 * installed before game play can begin.
 */
public class MapNotFoundException extends IllegalStateException {
  private static final long serialVersionUID = -1027460394367073991L;

  public MapNotFoundException(final String mapName) {
    super(I18nEngineFramework.get().getString("startup.MapNotFoundException.err.FindMap", mapName));
  }
}
