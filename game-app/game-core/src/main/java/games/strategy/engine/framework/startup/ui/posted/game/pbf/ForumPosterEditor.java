package games.strategy.engine.framework.startup.ui.posted.game.pbf;

import games.strategy.engine.framework.I18nEngineFramework;
import games.strategy.engine.framework.I18nResourceBundle;
import games.strategy.engine.framework.startup.ui.posted.game.HelpTexts;
import games.strategy.triplea.settings.ClientSetting;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.triplea.java.ViewModelListener;
import org.triplea.swing.DocumentListenerBuilder;
import org.triplea.swing.JButtonBuilder;
import org.triplea.swing.JCheckBoxBuilder;
import org.triplea.swing.SwingComponents;
import org.triplea.swing.jpanel.JPanelBuilder;

/** A class for selecting which Forum poster to use. */
class ForumPosterEditor extends JPanel implements ViewModelListener<ForumPosterEditorViewModel> {
  private static final long serialVersionUID = -6069315084412575053L;

  private final ForumPosterEditorViewModel viewModel;
  private final JButton viewPosts;
  private final JButton testForum;
  private final JLabel helpMessage = new JLabel();
  private final JButton helpButton;
  private final JTextField topicIdField = new JTextField();
  private final JLabel usernameLabel;
  private final JTextField usernameField = new JTextField();
  private final JLabel passwordLabel;
  private final JPasswordField passwordField = new JPasswordField();
  private final JLabel otpLabel;
  private final JTextField otpField = new JTextField();
  private final JLabel topicIdLabel;
  private final JLabel forumLabel;
  private final JCheckBox attachSaveGameToSummary;
  private final JCheckBox alsoPostAfterCombatMove;
  private final JComboBox<String> forums = new JComboBox<>();
  private final JCheckBox rememberPassword;
  private final JButton rememberPasswordHelpButton;

  ForumPosterEditor(final ForumPosterEditorViewModel viewModel) {
    super(new GridBagLayout());

    final I18nResourceBundle bundle = I18nEngineFramework.get();
    viewPosts = new JButton(bundle.getString("startup.ForumPosterEditor.btn.ViewForum.Txt"));
    testForum = new JButton(bundle.getString("startup.ForumPosterEditor.btn.TestForum.Txt"));
    helpButton =
        new JButtonBuilder(bundle.getString("Button.Help.Lbl"))
            .actionListener(
                () ->
                    JOptionPane.showMessageDialog(
                        this,
                        helpMessage,
                        bundle.getString("startup.ForumPosterEditor.dlg.Help.Ttl"),
                        JOptionPane.INFORMATION_MESSAGE))
            .toolTip(bundle.getString("Button.Help.Tltp"))
            .build();
    usernameLabel = new JLabel(bundle.getString("startup.ForumPosterEditor.lbl.ForumUsername.Txt"));
    passwordLabel = new JLabel(bundle.getString("startup.ForumPosterEditor.lbl.ForumPassword.Txt"));
    otpLabel = new JLabel(bundle.getString("startup.ForumPosterEditor.lbl.OtpCode.Txt"));
    topicIdLabel = new JLabel(bundle.getString("startup.ForumPosterEditor.lbl.TopicId.Txt"));
    forumLabel = new JLabel(bundle.getString("startup.ForumPosterEditor.lbl.Forum.Txt"));
    attachSaveGameToSummary =
        new JCheckBox(
            bundle.getString("startup.ForumPosterEditor.chkbx.AttachSaveGameSummary.Txt"));
    alsoPostAfterCombatMove =
        new JCheckBox(
            bundle.getString("startup.ForumPosterEditor.chkbx.AlsoPostAfterCombatMove.Txt"));
    rememberPassword =
        new JCheckBoxBuilder(bundle.getString("Checkbox.RememberPassword.Lbl"))
            .bind(ClientSetting.rememberForumPassword)
            .build();
    rememberPasswordHelpButton =
        new JButtonBuilder(bundle.getString("Button.Help.Lbl"))
            .actionListener(
                button ->
                    JOptionPane.showMessageDialog(
                        button,
                        HelpTexts.rememberPlayByForumPassword(),
                        bundle.getString("startup.ForumPosterEditor.dlg.RememberPasswordHelp.Ttl"),
                        JOptionPane.INFORMATION_MESSAGE))
            .build();

    this.viewModel = viewModel;
    viewModel.setView(this);
    // If password is already stored we already remember
    if (viewModel.isForumPasswordValid()) {
      rememberPassword.setSelected(true);
    }
    rememberPassword.addActionListener(
        e -> viewModel.setRememberPassword(rememberPassword.isSelected()));

    final int bottomSpace = 1;
    final int labelSpace = 2;
    int row = 0;

    add(
        forumLabel,
        new GridBagConstraints(
            0,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, bottomSpace, labelSpace),
            0,
            0));

    viewModel.getForumSelectionOptions().forEach(forums::addItem);
    forums.addActionListener(
        e -> {
          viewModel.setForumSelection((String) forums.getSelectedItem());

          usernameField.setText(viewModel.getForumUsername());
          passwordField.setText(String.valueOf(viewModel.getForumPassword()));
          SwingComponents.highlightLabelIfNotValid(viewModel.isForumUsernameValid(), usernameLabel);
          SwingComponents.highlightLabelIfNotValid(viewModel.isForumPasswordValid(), passwordLabel);
          testForum.setEnabled(viewModel.isTestForumPostButtonEnabled());
          helpMessage.setText(viewModel.getForumProviderHelpText());
        });
    forums.setSelectedItem(viewModel.getForumSelection());
    add(
        forums,
        new GridBagConstraints(
            1,
            row,
            1,
            1,
            1.0,
            0,
            GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, bottomSpace, 0),
            0,
            0));

    add(
        helpButton,
        new GridBagConstraints(
            2,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.EAST,
            GridBagConstraints.NONE,
            new Insets(0, 2, bottomSpace, 0),
            0,
            0));

    row++;
    add(
        topicIdLabel,
        new GridBagConstraints(
            0,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, bottomSpace, labelSpace),
            0,
            0));

    new DocumentListenerBuilder(
            () -> {
              viewModel.setTopicId(topicIdField.getText().trim());
              SwingComponents.highlightLabelIfNotValid(viewModel.isTopicIdValid(), topicIdLabel);
              viewPosts.setEnabled(viewModel.isViewForumPostButtonEnabled());
              testForum.setEnabled(viewModel.isTestForumPostButtonEnabled());
            })
        .attachTo(topicIdField);
    add(
        topicIdField,
        new GridBagConstraints(
            1,
            row,
            1,
            1,
            1.0,
            0,
            GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, bottomSpace, 0),
            0,
            0));

    viewPosts.addActionListener(e -> viewModel.viewForumButtonClicked());
    add(
        viewPosts,
        new GridBagConstraints(
            2,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.EAST,
            GridBagConstraints.NONE,
            new Insets(0, 2, bottomSpace, 0),
            0,
            0));
    row++;

    add(
        usernameLabel,
        new GridBagConstraints(
            0,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, bottomSpace, labelSpace),
            0,
            0));

    new DocumentListenerBuilder(
            () -> {
              viewModel.setForumUsername(usernameField.getText().trim());
              SwingComponents.highlightLabelIfNotValid(
                  viewModel.isForumUsernameValid(), usernameLabel);
              testForum.setEnabled(viewModel.isTestForumPostButtonEnabled());
            })
        .attachTo(usernameField);

    add(
        usernameField,
        new GridBagConstraints(
            1,
            row,
            1,
            1,
            1.0,
            0,
            GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, bottomSpace, 0),
            0,
            0));
    row++;

    add(
        passwordLabel,
        new GridBagConstraints(
            0,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, bottomSpace, labelSpace),
            0,
            0));
    new DocumentListenerBuilder(
            () -> {
              viewModel.setForumPassword(passwordField.getPassword());
              SwingComponents.highlightLabelIfNotValid(
                  viewModel.isForumPasswordValid(), passwordLabel);
              testForum.setEnabled(viewModel.isTestForumPostButtonEnabled());
            })
        .attachTo(passwordField);
    add(
        passwordField,
        new GridBagConstraints(
            1,
            row,
            1,
            1,
            1.0,
            0,
            GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, bottomSpace, 0),
            0,
            0));
    row++;
    add(
        otpLabel,
        new GridBagConstraints(
            0,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, bottomSpace, labelSpace),
            0,
            0));
    add(
        otpField,
        new GridBagConstraints(
            1,
            row,
            1,
            1,
            1.0,
            0,
            GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, bottomSpace, 0),
            0,
            0));
    new DocumentListenerBuilder(() -> viewModel.setOtpCode(otpField.getText())).attachTo(otpField);
    row++;
    add(
        new JPanelBuilder()
            .boxLayoutHorizontal()
            .add(rememberPassword)
            .add(rememberPasswordHelpButton)
            .build(),
        new GridBagConstraints(
            1,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, bottomSpace, labelSpace),
            0,
            0));
    add(
        new JLabel(""),
        new GridBagConstraints(
            0,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, bottomSpace, labelSpace),
            0,
            0));
    row++;

    attachSaveGameToSummary.addChangeListener(
        e -> viewModel.setAttachSaveGameToSummary(attachSaveGameToSummary.isSelected()));
    add(
        attachSaveGameToSummary,
        new GridBagConstraints(
            1,
            row,
            2,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0),
            0,
            0));

    testForum.addActionListener(e -> viewModel.testPostButtonClicked());
    add(
        testForum,
        new GridBagConstraints(
            2,
            row,
            1,
            1,
            0,
            0,
            GridBagConstraints.EAST,
            GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0),
            0,
            0));
    row++;
    alsoPostAfterCombatMove.addActionListener(
        e -> viewModel.setAlsoPostAfterCombatMove(alsoPostAfterCombatMove.isSelected()));
    add(
        alsoPostAfterCombatMove,
        new GridBagConstraints(
            1,
            row,
            2,
            1,
            0,
            0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0),
            0,
            0));
  }

  @Override
  public void viewModelChanged(final ForumPosterEditorViewModel forumPosterEditorViewModel) {
    forums.setSelectedItem(forumPosterEditorViewModel.getForumSelection());
    topicIdField.setText(forumPosterEditorViewModel.getTopicId());
    usernameField.setText(forumPosterEditorViewModel.getForumUsername());
    passwordField.setText(String.valueOf(forumPosterEditorViewModel.getForumPassword()));
    alsoPostAfterCombatMove.setSelected(forumPosterEditorViewModel.isAlsoPostAfterCombatMove());
    attachSaveGameToSummary.setSelected(forumPosterEditorViewModel.isAttachSaveGameToSummary());
    SwingComponents.highlightLabelIfNotValid(
        forumPosterEditorViewModel.isTopicIdValid(), topicIdLabel);
    SwingComponents.highlightLabelIfNotValid(
        forumPosterEditorViewModel.isForumUsernameValid(), usernameLabel);
    SwingComponents.highlightLabelIfNotValid(
        forumPosterEditorViewModel.isForumPasswordValid(), passwordLabel);
    viewPosts.setEnabled(forumPosterEditorViewModel.isViewForumPostButtonEnabled());
    testForum.setEnabled(forumPosterEditorViewModel.isTestForumPostButtonEnabled());
  }

  boolean shouldRevokeTokenOnShutdown() {
    return viewModel.shouldRevokeTokenOnShutdown();
  }

  void revokeToken() {
    viewModel.revokeToken();
  }

  void requestToken() {
    viewModel.acquireTokenAndDeletePassword();
  }
}
