package games.strategy.engine.framework.startup.ui.posted.game.pbem;

import games.strategy.engine.data.properties.GameProperties;
import games.strategy.engine.framework.I18nEngineFramework;
import games.strategy.engine.framework.I18nResourceBundle;
import games.strategy.engine.framework.startup.ui.posted.game.HelpTexts;
import games.strategy.triplea.settings.ClientSetting;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import org.triplea.java.ViewModelListener;
import org.triplea.swing.DocumentListenerBuilder;
import org.triplea.swing.JButtonBuilder;
import org.triplea.swing.JCheckBoxBuilder;
import org.triplea.swing.JComboBoxBuilder;
import org.triplea.swing.JTextFieldBuilder;
import org.triplea.swing.SwingComponents;
import org.triplea.swing.jpanel.GridBagConstraintsBuilder;
import org.triplea.swing.jpanel.JPanelBuilder;

/** An editor for modifying email senders. */
public class EmailSenderEditor implements ViewModelListener<EmailSenderEditorViewModel> {

  private static final int FIELD_LENGTH = 20;
  private final EmailSenderEditorViewModel viewModel = new EmailSenderEditorViewModel(this);

  private boolean syncToModel;
  private JPanel contents;

  private final JComboBox<String> emailProviderSelectionBox;

  private final JButton helpButton;

  private final JLabel smtpServerLabel;
  private final JTextField smtpServerField;

  private final JLabel smtpPortLabel;
  private final JTextField smtpPortField;

  private final JCheckBox useTlsCheckBox;

  private final JLabel subjectLabel;
  private final JTextField subjectField;

  private final JLabel toAddressLabel;
  private final JTextField toAddressField;

  private final JLabel userNameLabel;
  private final JTextField userNameField;

  private final JLabel passwordLabel;
  private final JPasswordField passwordField;

  private final JCheckBox rememberPassword;
  private final JButton rememberPasswordHelpButton;

  private final JCheckBox sendEmailAfterCombatMoveCheckBox;

  private final JButton testEmailButton;

  public EmailSenderEditor(final Runnable readyCallback) {
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    emailProviderSelectionBox =
        JComboBoxBuilder.builder()
            .items(EmailSenderEditorViewModel.getProviderOptions())
            .selectedItem(viewModel.getSelectedProvider())
            .itemSelectedAction(
                provider -> {
                  if (syncToModel) {
                    viewModel.setSelectedProvider(provider);
                  }
                })
            .build();

    helpButton =
        new JButtonBuilder(bundle.getString("Button.Help.Lbl"))
            .actionListener(
                () ->
                    JOptionPane.showMessageDialog(
                        contents,
                        new JLabel(viewModel.getEmailHelpText()),
                        bundle.getString("startup.EmailSenderEditor.dlg.Help.Ttl"),
                        JOptionPane.INFORMATION_MESSAGE))
            .toolTip(bundle.getString("Button.Help.Tltp"))
            .build();

    smtpServerLabel = new JLabel(bundle.getString("startup.EmailSenderEditor.lbl.SmtpServer"));
    smtpServerField =
        JTextFieldBuilder.builder()
            .text(viewModel.getSmtpServer())
            .textListener(
                server -> {
                  if (syncToModel) {
                    viewModel.setSmtpServer(server);
                  }
                })
            .columns(FIELD_LENGTH)
            .build();

    smtpPortLabel = new JLabel(bundle.getString("startup.EmailSenderEditor.lbl.Port"));
    smtpPortField =
        JTextFieldBuilder.builder()
            .text(viewModel.getSmtpPort())
            .textListener(
                smtpPort -> {
                  if (syncToModel) {
                    viewModel.setSmtpPort(smtpPort);
                  }
                })
            .columns(FIELD_LENGTH)
            .build();

    useTlsCheckBox =
        new JCheckBoxBuilder(bundle.getString("startup.EmailSenderEditor.chkbx.UseTls.Lbl"))
            .selected(viewModel.isUseTls())
            .actionListener(
                useTls -> {
                  if (syncToModel) {
                    viewModel.setUseTls(useTls);
                  }
                })
            .build();

    subjectLabel = new JLabel(bundle.getString("startup.EmailSenderEditor.lbl.Subject"));
    subjectField =
        JTextFieldBuilder.builder()
            .text(viewModel.getSubject())
            .textListener(
                subject -> {
                  if (syncToModel) {
                    viewModel.setSubject(subject);
                  }
                })
            .columns(FIELD_LENGTH)
            .build();

    toAddressLabel = new JLabel(bundle.getString("startup.EmailSenderEditor.lbl.To"));
    toAddressField =
        JTextFieldBuilder.builder()
            .text(viewModel.getToAddress())
            .textListener(
                toAddress -> {
                  if (syncToModel) {
                    viewModel.setToAddress(toAddress);
                  }
                })
            .columns(FIELD_LENGTH)
            .build();

    userNameLabel = new JLabel(bundle.getString("startup.EmailSenderEditor.lbl.EmailUsername"));
    userNameField =
        JTextFieldBuilder.builder()
            .text(viewModel.getEmailUsername())
            .textListener(
                fieldValue -> {
                  if (syncToModel) {
                    viewModel.setEmailUsername(fieldValue);
                  }
                })
            .columns(FIELD_LENGTH)
            .build();

    passwordLabel = new JLabel(bundle.getString("startup.EmailSenderEditor.lbl.EmailPassword"));
    passwordField = new JPasswordField(viewModel.getEmailPassword(), FIELD_LENGTH);

    rememberPassword =
        new JCheckBoxBuilder(bundle.getString("Checkbox.RememberPassword.Lbl"))
            .actionListener(viewModel::setRememberPassword)
            .bind(ClientSetting.rememberEmailPassword)
            .build();
    rememberPasswordHelpButton =
        new JButtonBuilder(bundle.getString("Button.Help.Lbl"))
            .actionListener(
                button ->
                    JOptionPane.showMessageDialog(
                        button,
                        HelpTexts.rememberPlayByEmailPassword(),
                        bundle.getString("startup.EmailSenderEditor.dlg.Help.RememberPassword.Ttl"),
                        JOptionPane.INFORMATION_MESSAGE))
            .build();

    sendEmailAfterCombatMoveCheckBox =
        new JCheckBoxBuilder(
                bundle.getString("startup.EmailSenderEditor.chkbx.EmailAfterCombat.Lbl"))
            .selected(viewModel.isSendEmailAfterCombatMove())
            .actionListener(
                sendEmailAfterCombatMove -> {
                  if (syncToModel) {
                    viewModel.setSendEmailAfterCombatMove(sendEmailAfterCombatMove);
                  }
                })
            .build();

    testEmailButton =
        new JButtonBuilder(bundle.getString("startup.EmailSenderEditor.btn.SendTestEmail.Lbl"))
            .enabled(viewModel.isTestEmailButtonEnabled())
            .actionListener(viewModel::sendTestEmail)
            .build();

    viewModel.setValidatedFieldsChangedListener(readyCallback);
    this.viewModelChanged(viewModel);

    new DocumentListenerBuilder(() -> viewModel.setEmailPassword(passwordField.getPassword()))
        .attachTo(passwordField);
    syncToModel = true;
  }

  JPanel build() {
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    toggleFieldVisibility();

    contents =
        new JPanelBuilder()
            .border(new TitledBorder(bundle.getString("startup.EmailSenderEditor.Title.Contents")))
            .gridBagLayout()
            .build();
    int row = 0;

    row++;
    contents.add(
        new JLabel(bundle.getString("startup.EmailSenderEditor.lbl.EmailProvider")),
        new GridBagConstraintsBuilder(0, row).build());
    contents.add(emailProviderSelectionBox, new GridBagConstraintsBuilder(1, row).build());
    contents.add(helpButton, new GridBagConstraintsBuilder(2, row).build());

    row++;
    contents.add(smtpServerLabel, new GridBagConstraintsBuilder(0, row).build());
    contents.add(smtpServerField, new GridBagConstraintsBuilder(1, row).build());

    row++;
    contents.add(smtpPortLabel, new GridBagConstraintsBuilder(0, row).build());
    contents.add(smtpPortField, new GridBagConstraintsBuilder(1, row).build());

    row++;
    contents.add(useTlsCheckBox, new GridBagConstraintsBuilder(0, row).build());

    row++;
    contents.add(toAddressLabel, new GridBagConstraintsBuilder(0, row).build());
    contents.add(toAddressField, new GridBagConstraintsBuilder(1, row).build());
    contents.add(testEmailButton, new GridBagConstraintsBuilder(2, row).build());

    row++;
    contents.add(new JPanel(), new GridBagConstraintsBuilder(0, row).gridWidth(2).build());

    row++;
    contents.add(subjectLabel, new GridBagConstraintsBuilder(0, row).build());
    contents.add(subjectField, new GridBagConstraintsBuilder(1, row).build());

    row++;
    contents.add(userNameLabel, new GridBagConstraintsBuilder(0, row).build());
    contents.add(userNameField, new GridBagConstraintsBuilder(1, row).build());

    row++;
    contents.add(passwordLabel, new GridBagConstraintsBuilder(0, row).build());
    contents.add(passwordField, new GridBagConstraintsBuilder(1, row).build());

    row++;
    contents.add(
        new JPanelBuilder()
            .boxLayoutHorizontal()
            .add(rememberPassword)
            .add(rememberPasswordHelpButton)
            .build(),
        new GridBagConstraintsBuilder(1, row).build());

    row++;
    contents.add(
        sendEmailAfterCombatMoveCheckBox,
        new GridBagConstraintsBuilder(1, row).gridWidth(2).build());

    return contents;
  }

  @Override
  public void viewModelChanged(final EmailSenderEditorViewModel viewModel) {
    syncToModel = false;
    SwingUtilities.invokeLater(
        () -> {
          toggleFieldVisibility();
          emailProviderSelectionBox.setSelectedItem(viewModel.getSelectedProvider());
          SwingComponents.highlightLabelIfNotValid(viewModel.isSmtpServerValid(), smtpServerLabel);
          updateTextFieldIfNeeded(smtpServerField, viewModel.getSmtpServer());

          SwingComponents.highlightLabelIfNotValid(viewModel.isSmtpPortValid(), smtpPortLabel);
          updateTextFieldIfNeeded(smtpPortField, viewModel.getSmtpPort());
          useTlsCheckBox.setSelected(viewModel.isUseTls());

          SwingComponents.highlightLabelIfNotValid(viewModel.isToAddressValid(), toAddressLabel);
          updateTextFieldIfNeeded(toAddressField, viewModel.getToAddress());

          testEmailButton.setEnabled(viewModel.isTestEmailButtonEnabled());

          SwingComponents.highlightLabelIfNotValid(viewModel.isSubjectValid(), subjectLabel);
          updateTextFieldIfNeeded(subjectField, viewModel.getSubject());

          SwingComponents.highlightLabelIfNotValid(viewModel.isUsernameValid(), userNameLabel);
          updateTextFieldIfNeeded(userNameField, viewModel.getEmailUsername());

          SwingComponents.highlightLabelIfNotValid(viewModel.isPasswordValid(), passwordLabel);

          sendEmailAfterCombatMoveCheckBox.setSelected(viewModel.isSendEmailAfterCombatMove());
          syncToModel = true;
        });
  }

  private static void updateTextFieldIfNeeded(
      final JTextField textField, final String incomingValue) {
    // avoid updating fields to the same value; setting field text
    // resets the carat position to the end, very annoying if a user is
    // editing text.
    if (!textField.getText().equals(incomingValue)) {
      textField.setText(incomingValue);
    }
  }

  private void toggleFieldVisibility() {
    smtpServerLabel.setVisible(viewModel.showServerOptions());
    smtpServerField.setVisible(viewModel.showServerOptions());
    smtpPortLabel.setVisible(viewModel.showServerOptions());
    smtpPortField.setVisible(viewModel.showServerOptions());
    useTlsCheckBox.setVisible(viewModel.showServerOptions());
    testEmailButton.setVisible(viewModel.showEmailOptions());
    subjectField.setVisible(viewModel.showEmailOptions());
    subjectLabel.setVisible(viewModel.showEmailOptions());
    toAddressLabel.setVisible(viewModel.showEmailOptions());
    toAddressField.setVisible(viewModel.showEmailOptions());
    userNameLabel.setVisible(viewModel.showEmailOptions());
    userNameField.setVisible(viewModel.showEmailOptions());
    passwordLabel.setVisible(viewModel.showEmailOptions());
    passwordField.setVisible(viewModel.showEmailOptions());
    rememberPassword.setVisible(viewModel.showEmailOptions());
    rememberPasswordHelpButton.setVisible(viewModel.showEmailOptions());
    sendEmailAfterCombatMoveCheckBox.setVisible(viewModel.showEmailOptions());
  }

  void populateFromGameProperties(final GameProperties properties) {
    viewModel.populateFromGameProperties(properties);
  }

  void applyToGameProperties(final GameProperties properties) {
    viewModel.applyToGameProperties(properties);
  }

  boolean areFieldsValid() {
    return viewModel.areFieldsValid();
  }

  boolean isForgetPasswordOnShutdown() {
    return viewModel.isForgetPasswordOnShutdown();
  }
}
