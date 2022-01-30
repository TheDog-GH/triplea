package games.strategy.engine.framework.startup.ui.posted.game.pbf.test.post;

import games.strategy.engine.framework.I18nEngineFramework;
import games.strategy.engine.framework.I18nResourceBundle;
import java.util.function.Supplier;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.triplea.swing.ProgressWindow;

public class SwingTestPostProgressDisplayFactory implements Supplier<TestPostProgressDisplay> {

  @Override
  public TestPostProgressDisplay get() {
    final I18nResourceBundle bundle = I18nEngineFramework.get();
    final ProgressWindow progressWindow =
        new ProgressWindow(
            null,
            bundle.getString("startup.SwingTestPostProgressDisplayFactory.ProgressWindow.Ttl"));

    progressWindow.setVisible(true);

    return new TestPostProgressDisplay() {
      @Override
      public void showSuccess(final String message) {
        SwingUtilities.invokeLater(
            () ->
                JOptionPane.showMessageDialog(
                    null,
                    message,
                    bundle.getString(
                        "startup.SwingTestPostProgressDisplayFactory.dlg.TestPostSuccess"),
                    JOptionPane.INFORMATION_MESSAGE));
      }

      @Override
      public void showFailure(final Throwable throwable) {
        SwingUtilities.invokeLater(
            () ->
                JOptionPane.showMessageDialog(
                    null,
                    throwable.getMessage(),
                    bundle.getString(
                        "startup.SwingTestPostProgressDisplayFactory.dlg.TestPostFail"),
                    JOptionPane.WARNING_MESSAGE));
      }

      @Override
      public void close() {
        progressWindow.setVisible(false);
      }
    };
  }
}
