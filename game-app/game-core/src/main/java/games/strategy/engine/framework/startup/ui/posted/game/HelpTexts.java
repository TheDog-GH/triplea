package games.strategy.engine.framework.startup.ui.posted.game;

import games.strategy.engine.framework.HtmlUtils;
import games.strategy.engine.framework.I18nEngineFramework;
import games.strategy.engine.framework.I18nResourceBundle;
import lombok.experimental.UtilityClass;

/**
 * Stores help text constants and helper methods to create help texts used in the PBF and PBEM
 * screens.
 */
@UtilityClass
public class HelpTexts {

  public static final String TRIPLEA_FORUM =
      HtmlUtils.getHtml(I18nEngineFramework.get().getString("startup.HelpTexts.str.TRIPLEA_FORUM"));
  public static final String AXIS_AND_ALLIES_DOT_ORG_FORUM =
      HtmlUtils.getHtml(
          I18nEngineFramework.get().getString("startup.HelpTexts.str.AXIS_AND_ALLIES_FORUM"));
  public static final String SMTP_DISABLED =
      HtmlUtils.getHtml(I18nEngineFramework.get().getString("startup.HelpTexts.str.SMTP_DISABLED"));

  public static final String GENERIC_SMTP_SERVER =
      HtmlUtils.getHtml(
          I18nEngineFramework.get().getString("startup.HelpTexts.str.GENERIC_SMTP_SERVER"));

  public static String gmailHelpText() {
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    return emailProviderHelpText(
        bundle.getString("startup.HelpTexts.str.Gmail"),
        bundle.getString("startup.HelpTexts.str.GmailPossessive"));
  }

  public static String hotmailHelpText() {
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    return emailProviderHelpText(
        bundle.getString("startup.HelpTexts.str.Hotmail"),
        bundle.getString("startup.HelpTexts.str.HotmailPossessive"));
  }

  private static String emailProviderHelpText(final String name, final String possessiveName) {
    return HtmlUtils.getHtml(
        I18nEngineFramework.get()
            .getString("startup.HelpTexts.msg.EmailProviderHelpText", name, possessiveName));
  }

  public static String rememberPlayByForumPassword() {
    return rememberPassword(
        I18nEngineFramework.get().getString("startup.HelpTexts.str.PlayByForum"));
  }

  public static String rememberPlayByEmailPassword() {
    return rememberPassword(
        I18nEngineFramework.get().getString("startup.HelpTexts.str.PlayByEmail"));
  }

  private static String rememberPassword(final String playTypeLabel) {
    return HtmlUtils.getHtml(
        I18nEngineFramework.get()
            .getString("startup.HelpTexts.msg.RememberPassword", playTypeLabel));
  }
}
