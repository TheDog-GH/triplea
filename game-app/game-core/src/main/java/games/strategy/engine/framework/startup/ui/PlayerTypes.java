package games.strategy.engine.framework.startup.ui;

import games.strategy.engine.framework.I18nEngineFramework;
import games.strategy.engine.player.Player;
import games.strategy.triplea.TripleAPlayer;
import games.strategy.triplea.ai.AiProvider;
import games.strategy.triplea.ai.fast.FastAi;
import games.strategy.triplea.ai.pro.ProAi;
import games.strategy.triplea.ai.weak.WeakAi;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Value
public class PlayerTypes {

  public static final String DOES_NOTHING_PLAYER_LABEL = "Does Nothing (AI)";

  public static final Type WEAK_AI =
      new Type(I18nEngineFramework.get().getString("Player.WeakAI")) {
        @Override
        public Player newPlayerWithName(final String name) {
          return new WeakAi(name);
        }
      };
  public static final Type FAST_AI =
      new Type(I18nEngineFramework.get().getString("Player.FastAI")) {
        @Override
        public Player newPlayerWithName(final String name) {
          return new FastAi(name);
        }
      };
  public static final Type PRO_AI =
      new Type(I18nEngineFramework.get().getString("Player.HardAI")) {
        @Override
        public Player newPlayerWithName(final String name) {
          return new ProAi(name);
        }
      };
  /** A 'dummy' player type used for battle calc. */
  public static final Type BATTLE_CALC_DUMMY =
      new Type(I18nEngineFramework.get().getString("Player.NoneAI"), false) {
        @Override
        public Player newPlayerWithName(final String name) {
          throw new UnsupportedOperationException(
              I18nEngineFramework.get()
                  .getString("startup.PlayerTypes.err.InstantiateDummyPlayer"));
        }
      };

  /**
   * Converter function, each player type has a label, this method will convert from a given label
   * value to the corresponding enum.
   *
   * @throws IllegalStateException Thrown if the given label does not match any in the current enum.
   */
  public Type fromLabel(final String label) {
    return playerTypes.stream()
        .filter(playerType -> playerType.getLabel().equals(label))
        .findAny()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    I18nEngineFramework.get()
                        .getString("startup.PlayerTypes.err.FindPlayerType", label)));
  }  public static final Type HUMAN_PLAYER =
      new Type(I18nEngineFramework.get().getString("Player.Human")) {
        @Override
        public Player newPlayerWithName(final String name) {
          return new TripleAPlayer(name) {
            @Override
            public Type getPlayerType() {
              return HUMAN_PLAYER;
            }
          };
        }
      };



  /** A hidden player type to represent network connected players. */
  public static final Type CLIENT_PLAYER =
      new Type(I18nEngineFramework.get().getString("Player.Client"), false) {
        @Override
        public Player newPlayerWithName(final String name) {
          return new TripleAPlayer(name) {
            @Override
            public Type getPlayerType() {
              return CLIENT_PLAYER;
            }
          };
        }
      };

  Collection<Type> playerTypes;

  public PlayerTypes(final Collection<Type> playerTypes) {
    this.playerTypes = playerTypes;
  }

  public static Collection<Type> getBuiltInPlayerTypes() {
    return List.of(
        PlayerTypes.HUMAN_PLAYER,
        PlayerTypes.WEAK_AI,
        PlayerTypes.FAST_AI,
        PlayerTypes.PRO_AI,
        PlayerTypes.CLIENT_PLAYER,
        PlayerTypes.BATTLE_CALC_DUMMY);
  }

  /**
   * Returns the set of player types that users can select.
   *
   * @return Human readable player type labels.
   */
  public String[] getAvailablePlayerLabels() {
    return playerTypes.stream().filter(Type::isVisible).map(Type::getLabel).toArray(String[]::new);
  }



  /**
   * PlayerType indicates what kind of entity is controlling a player, whether an AI, human, or a
   * remote (network) player.
   */
  @AllArgsConstructor
  @EqualsAndHashCode
  public abstract static class Type {

    @Getter private final String label;

    @Getter(AccessLevel.PACKAGE)
    private final boolean visible;

    Type(final String label) {
      this(label, true);
    }

    /**
     * Each PlayerType is backed by an {@code IRemotePlayer} instance. Given a player name this
     * method will create the corresponding {@code IRemotePlayer} instance.
     */
    public abstract Player newPlayerWithName(String name);
  }

  public static class AiType extends Type {
    private final AiProvider aiProvider;

    public AiType(final AiProvider aiProvide) {
      super(aiProvide.getLabel());
      this.aiProvider = aiProvide;
    }

    @Override
    public Player newPlayerWithName(final String name) {
      return this.aiProvider.create(name, this);
    }
  }
}
