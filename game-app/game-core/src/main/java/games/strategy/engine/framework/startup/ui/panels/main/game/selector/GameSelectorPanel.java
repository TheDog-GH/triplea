package games.strategy.engine.framework.startup.ui.panels.main.game.selector;

import static org.triplea.swing.SwingComponents.DialogWithLinksParams;
import static org.triplea.swing.SwingComponents.DialogWithLinksTypes;

import games.strategy.engine.data.GameData;
import games.strategy.engine.data.properties.IEditableProperty;
import games.strategy.engine.data.properties.PropertiesUi;
import games.strategy.engine.framework.HeadlessAutoSaveType;
import games.strategy.engine.framework.HtmlUtils;
import games.strategy.engine.framework.I18nEngineFramework;
import games.strategy.engine.framework.I18nResourceBundle;
import games.strategy.engine.framework.map.download.DownloadMapsWindow;
import games.strategy.engine.framework.map.file.system.loader.InstalledMapsListing;
import games.strategy.engine.framework.startup.mc.ClientModel;
import games.strategy.engine.framework.startup.ui.FileBackedGamePropertiesCache;
import games.strategy.engine.framework.startup.ui.IGamePropertiesCache;
import games.strategy.engine.framework.system.SystemProperties;
import games.strategy.engine.framework.ui.GameChooser;
import games.strategy.engine.framework.ui.background.BackgroundTaskRunner;
import games.strategy.engine.framework.ui.background.TaskRunner;
import games.strategy.triplea.ResourceLoader;
import games.strategy.triplea.UrlConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.triplea.injection.Injections;
import org.triplea.swing.DialogBuilder;
import org.triplea.swing.JButtonBuilder;
import org.triplea.swing.SwingAction;
import org.triplea.swing.SwingComponents;
import org.triplea.swing.jpanel.GridBagConstraintsBuilder;

/**
 * Left hand side panel of the launcher screen that has various info, like selected game and engine
 * version.
 */
public final class GameSelectorPanel extends JPanel implements Observer {
  private static final long serialVersionUID = -4598107601238030020L;

  private final GameSelectorModel model;
  private final IGamePropertiesCache gamePropertiesCache = new FileBackedGamePropertiesCache();
  private final Map<String, Object> originalPropertiesMap = new HashMap<>();
  private final JLabel nameText = new JLabel();
  private final JLabel saveGameText = new JLabel();
  private final JLabel roundText = new JLabel();
  private final JButton loadSavedGame = new JButton();
  private final JButton loadNewGame = new JButton();
  private final JButton mapOptions = new JButton();

  public GameSelectorPanel(final GameSelectorModel model) {
    this.model = model;
    final GameData data = model.getGameData();
    if (data != null) {
      setOriginalPropertiesMap(data);
      gamePropertiesCache.loadCachedGamePropertiesInto(data);
    }

    initButtons(model);

    setLayout(new GridBagLayout());

    fillComponent(model);

    updateGameData();
  }

  private void initButtons(final GameSelectorModel model) {
    final I18nResourceBundle bundle = I18nEngineFramework.get();

    loadSavedGame.setText(bundle.getString("startup.GameSelectorPanel.btn.OpenSavedGame.Lbl"));
    loadSavedGame.setToolTipText(
        bundle.getString("startup.GameSelectorPanel.btn.OpenSavedGame.Tltp"));
    loadNewGame.setText(bundle.getString("startup.GameSelectorPanel.btn.SelectGame.Lbl"));
    loadNewGame.setToolTipText(
        HtmlUtils.getHtml(bundle.getString("startup.GameSelectorPanel.btn.SelectGame.Tltp")));
    mapOptions.setText(bundle.getString("startup.GameSelectorPanel.btn.GameOptions.Lbl"));
    mapOptions.setToolTipText(
        HtmlUtils.getHtml(bundle.getString("startup.GameSelectorPanel.btn.GameOptions.Tltp")));

    loadSavedGame.addActionListener(
        e -> {
          if (canSelectLocalGameData()) {
            selectSavedGameFile();
          } else if (canChangeHostBotGameData()) {
            final ClientModel clientModelForHostBots = model.getClientModelForHostBots();
            if (clientModelForHostBots != null) {
              final JPopupMenu menu = new JPopupMenu();
              menu.add(
                  clientModelForHostBots.getHostBotChangeGameToSaveGameClientAction(
                      JOptionPane.getFrameForComponent(this)));
              menu.add(
                  clientModelForHostBots.getHostBotChangeToAutosaveClientAction(
                      GameSelectorPanel.this, HeadlessAutoSaveType.DEFAULT));
              menu.add(
                  clientModelForHostBots.getHostBotChangeToAutosaveClientAction(
                      GameSelectorPanel.this, HeadlessAutoSaveType.ODD_ROUND));
              menu.add(
                  clientModelForHostBots.getHostBotChangeToAutosaveClientAction(
                      GameSelectorPanel.this, HeadlessAutoSaveType.EVEN_ROUND));
              menu.add(
                  clientModelForHostBots.getHostBotChangeToAutosaveClientAction(
                      GameSelectorPanel.this, HeadlessAutoSaveType.END_TURN));
              menu.add(
                  clientModelForHostBots.getHostBotChangeToAutosaveClientAction(
                      GameSelectorPanel.this, HeadlessAutoSaveType.BEFORE_BATTLE));
              menu.add(
                  clientModelForHostBots.getHostBotChangeToAutosaveClientAction(
                      GameSelectorPanel.this, HeadlessAutoSaveType.AFTER_BATTLE));
              menu.add(
                  clientModelForHostBots.getHostBotChangeToAutosaveClientAction(
                      GameSelectorPanel.this, HeadlessAutoSaveType.AFTER_COMBAT_MOVE));
              menu.add(
                  clientModelForHostBots.getHostBotChangeToAutosaveClientAction(
                      GameSelectorPanel.this, HeadlessAutoSaveType.AFTER_NON_COMBAT_MOVE));
              menu.add(
                  clientModelForHostBots.getHostBotGetGameSaveClientAction(GameSelectorPanel.this));
              final Point point = loadSavedGame.getLocation();
              menu.show(GameSelectorPanel.this, point.x + loadSavedGame.getWidth(), point.y);
            }
          }
        });
    mapOptions.addActionListener(
        e -> {
          if (canSelectLocalGameData()) {
            selectGameOptions();
          } else if (canChangeHostBotGameData()) {
            final ClientModel clientModelForHostBots = model.getClientModelForHostBots();
            if (clientModelForHostBots != null) {
              clientModelForHostBots
                  .getHostBotChangeGameOptionsClientAction(GameSelectorPanel.this)
                  .actionPerformed(e);
            }
          }
        });
  }

  private void fillComponent(final GameSelectorModel model) {
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    final JLabel logoLabel =
        new JLabel(
            new ImageIcon(
                ResourceLoader.loadImageAsset(Path.of("launch_screens", "triplea-logo.png"))));

    int row = 0;
    add(
        logoLabel,
        new GridBagConstraintsBuilder(0, row)
            .gridWidth(2)
            .insets(new Insets(10, 10, 3, 5))
            .build());
    row++;

    add(
        new JLabel(bundle.getString("startup.GameSelectorPanel.lbl.JavaVersion")),
        buildGridCell(0, row, new Insets(10, 10, 3, 5)));
    add(
        new JLabel(SystemProperties.getJavaVersion()),
        buildGridCell(1, row, new Insets(10, 0, 3, 0)));
    row++;

    add(
        new JLabel(bundle.getString("startup.GameSelectorPanel.lbl.EngineVersion")),
        buildGridCell(0, row, new Insets(0, 10, 3, 5)));
    add(
        new JLabel(Injections.getInstance().getEngineVersion().toString()),
        buildGridCell(1, row, new Insets(0, 0, 3, 0)));
    row++;

    add(
        new JLabel(bundle.getString("startup.GameSelectorPanel.lbl.GameName")),
        buildGridCell(0, row, new Insets(0, 10, 3, 5)));
    add(nameText, buildGridCell(1, row, new Insets(0, 0, 3, 0)));
    row++;

    add(
        new JLabel(bundle.getString("startup.GameSelectorPanel.lbl.GameRound")),
        buildGridCell(0, row, new Insets(0, 10, 3, 5)));
    add(roundText, buildGridCell(1, row, new Insets(0, 0, 3, 0)));
    row++;

    add(
        new JLabel(bundle.getString("startup.GameSelectorPanel.lbl.LoadedSaveGame")),
        buildGridCell(0, row, new Insets(20, 10, 3, 5)));
    row++;

    add(saveGameText, buildGridRow(0, row, new Insets(0, 10, 3, 5)));
    row++;

    add(loadNewGame, buildGridRow(0, row, new Insets(25, 10, 10, 10)));
    row++;

    add(loadSavedGame, buildGridRow(0, row, new Insets(0, 10, 10, 10)));
    row++;

    final JButton downloadMapButton =
        new JButtonBuilder()
            .title(bundle.getString("startup.GameSelectorPanel.btn.DownloadMaps.Lbl"))
            .toolTip(bundle.getString("startup.GameSelectorPanel.btn.DownloadMaps.Tltp"))
            .actionListener(DownloadMapsWindow::showDownloadMapsWindow)
            .build();
    add(downloadMapButton, buildGridRow(0, row, new Insets(0, 10, 10, 10)));
    row++;

    add(mapOptions, buildGridRow(0, row, new Insets(25, 10, 10, 10)));
    row++;

    // spacer
    add(
        new JPanel(),
        new GridBagConstraints(
            0,
            row,
            2,
            1,
            1,
            1,
            GridBagConstraints.CENTER,
            GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0),
            0,
            0));

    loadNewGame.addActionListener(
        e -> {
          if (canSelectLocalGameData()) {
            selectGameFile();
          } else if (canChangeHostBotGameData()) {
            final ClientModel clientModelForHostBots = model.getClientModelForHostBots();
            if (clientModelForHostBots != null) {
              clientModelForHostBots
                  .getHostBotSetMapClientAction(GameSelectorPanel.this)
                  .actionPerformed(e);
            }
          }
        });
  }

  private static GridBagConstraints buildGridCell(final int x, final int y, final Insets insets) {
    return buildGrid(x, y, insets, 1);
  }

  private static GridBagConstraints buildGridRow(final int x, final int y, final Insets insets) {
    return buildGrid(x, y, insets, 2);
  }

  private static GridBagConstraints buildGrid(
      final int x, final int y, final Insets insets, final int width) {
    final int gridHeight = 1;
    final double weigthX = 0;
    final double weigthY = 0;
    final int anchor = GridBagConstraints.WEST;
    final int fill = GridBagConstraints.NONE;
    final int ipadx = 0;
    final int ipady = 0;

    return new GridBagConstraints(
        x, y, width, gridHeight, weigthX, weigthY, anchor, fill, insets, ipadx, ipady);
  }

  private void setOriginalPropertiesMap(final GameData data) {
    originalPropertiesMap.clear();
    if (data != null) {
      for (final IEditableProperty<?> property : data.getProperties().getEditableProperties()) {
        originalPropertiesMap.put(property.getName(), property.getValue());
      }
    }
  }

  private void selectGameOptions() {
    // backup current game properties before showing dialog
    final Map<String, Object> currentPropertiesMap = new HashMap<>();
    for (final IEditableProperty<?> property :
        model.getGameData().getProperties().getEditableProperties()) {
      currentPropertiesMap.put(property.getName(), property.getValue());
    }
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    final PropertiesUi panel = new PropertiesUi(model.getGameData().getProperties(), true);
    final JScrollPane scroll = new JScrollPane(panel);
    scroll.setBorder(null);
    scroll.getViewport().setBorder(null);
    final JOptionPane pane = new JOptionPane(scroll, JOptionPane.PLAIN_MESSAGE);
    final String ok = bundle.getString("Option.OK");
    final String cancel = bundle.getString("Option.Cancel");
    final String makeDefault = bundle.getString("Option.MakeDefault");
    final String reset = bundle.getString("Option.Reset");
    pane.setOptions(new Object[] {ok, makeDefault, reset, cancel});
    final JDialog window =
        pane.createDialog(
            JOptionPane.getFrameForComponent(this),
            bundle.getString("startup.GameSelectorPanel.dlg.GameOptions"));
    window.setVisible(true);
    final Object buttonPressed = pane.getValue();
    if (buttonPressed == null || buttonPressed.equals(cancel)) {
      // restore properties, if cancel was pressed, or window was closed
      for (final IEditableProperty<?> property :
          model.getGameData().getProperties().getEditableProperties()) {
        property.validateAndSet(currentPropertiesMap.get(property.getName()));
      }
    } else if (buttonPressed.equals(reset)) {
      if (!originalPropertiesMap.isEmpty()) {
        // restore properties, if cancel was pressed, or window was closed
        for (final IEditableProperty<?> property :
            model.getGameData().getProperties().getEditableProperties()) {
          property.validateAndSet(originalPropertiesMap.get(property.getName()));
        }
        selectGameOptions();
      }
    } else if (buttonPressed.equals(makeDefault)) {
      gamePropertiesCache.cacheGameProperties(model.getGameData());
    }
  }

  private void updateGameData() {
    SwingAction.invokeNowOrLater(
        () -> {
          nameText.setText(model.getGameName());
          roundText.setText(model.getGameRound());
          saveGameText.setText(model.getFileName());

          final boolean canSelectGameData = canSelectLocalGameData();
          final boolean canChangeHostBotGameData = canChangeHostBotGameData();
          loadSavedGame.setEnabled(canSelectGameData || canChangeHostBotGameData);
          loadNewGame.setEnabled(canSelectGameData || canChangeHostBotGameData);
          // Disable game options if there are none.
          mapOptions.setEnabled(
              canChangeHostBotGameData
                  || (canSelectGameData
                      && model.getGameData() != null
                      && !model.getGameData().getProperties().getEditableProperties().isEmpty()));
        });
  }

  private boolean canSelectLocalGameData() {
    return model != null && model.isCanSelect();
  }

  private boolean canChangeHostBotGameData() {
    return model != null && model.isHostIsHeadlessBot();
  }

  @Override
  public void update(final Observable o, final Object arg) {
    updateGameData();
  }

  private void selectSavedGameFile() {
    final I18nResourceBundle bundle = I18nEngineFramework.get();

    final Consumer<Exception> loadSaveGameException =
        e ->
            SwingComponents.showDialogWithLinks(
                DialogWithLinksParams.builder()
                    .title(
                        bundle.getString("startup.GameSelectorPanel.dlg.LoadSaveGameException.Ttl"))
                    .dialogType(DialogWithLinksTypes.ERROR)
                    .dialogText(
                        bundle.getString(
                            "startup.GameSelectorPanel.dlg.LoadSaveGameException.Txt",
                            e.getMessage(),
                            UrlConstants.GITHUB_ISSUES))
                    .build());

    GameFileSelector.builder()
        .fileDoesNotExistAction(
            file ->
                DialogBuilder.builder()
                    .parent(this)
                    .title(bundle.getString("startup.GameSelectorPanel.dlg.FileNotFound.Ttl"))
                    .errorMessage(
                        bundle.getString(
                            "startup.GameSelectorPanel.dlg.FileNotFound.errMsg",
                            file.toAbsolutePath()))
                    .showDialog())
        .build()
        .selectGameFile(JOptionPane.getFrameForComponent(this))
        .ifPresent(
            file ->
                TaskRunner.builder()
                    .waitDialogTitle(
                        bundle.getString("startup.GameSelectorPanel.dlg.LoadSaveGame.Ttl"))
                    .exceptionHandler(loadSaveGameException)
                    .build()
                    .run(
                        () -> {
                          if (model.load(file)) {
                            setOriginalPropertiesMap(model.getGameData());
                          }
                        }));
  }

  private void selectGameFile() {
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    try {
      final InstalledMapsListing installedMapsListing =
          BackgroundTaskRunner.runInBackgroundAndReturn(
              bundle.getString("startup.GameSelectorPanel.tsk.InstallMaps.Msg"),
              InstalledMapsListing::parseMapFiles);

      GameChooser.chooseGame(
          JOptionPane.getFrameForComponent(this),
          installedMapsListing,
          model.getGameName(),
          this::gameSelected);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void gameSelected(final Path gameFile) {
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    BackgroundTaskRunner.runInBackground(
        bundle.getString("startup.GameSelectorPanel.tsk.LoadFile.Msg"),
        () -> {
          model.load(gameFile);
          // warning: NPE check is not to protect against concurrency, another thread could still
          // null
          // out game data.
          // The NPE check is to protect against the case where there are errors loading game, in
          // which case we'll have a null game data.
          if (model.getGameData() != null) {
            setOriginalPropertiesMap(model.getGameData());
            // only for new games, not saved games, we set the default options, and set them only
            // once
            // (the first time it is loaded)
            gamePropertiesCache.loadCachedGamePropertiesInto(model.getGameData());
          }
        });
  }
}
