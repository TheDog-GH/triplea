package games.strategy.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import games.strategy.triplea.settings.AbstractClientSettingTestCase;
import games.strategy.triplea.settings.ClientSetting;
import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonatype.goodies.prefs.memory.MemoryPreferences;
import org.triplea.util.ServiceNotAvailableException;

@SuppressWarnings("InnerClassMayBeStatic")
final class ClientFileSystemHelperTest {
  @Test
  void getRootFolder() {
    assertThrows(ServiceNotAvailableException.class, () -> ClientFileSystemHelper.getRootFolder());
  }

  @Test
  void getUserRootFolder() {
    assertTrue(
        ClientFileSystemHelper.getUserRootFolder()
            .toString()
            .endsWith(ClientFileSystemHelper.FOLDER_NAME_USER_ROOT));
  }

  @Test
  void getUserMapsFolder() {
    ClientSetting.setPreferences(new MemoryPreferences());
    assertTrue(
        ClientFileSystemHelper.getUserMapsFolder()
            .toString()
            .endsWith(ClientFileSystemHelper.FOLDER_NAME_USER_MAPS));
  }

  @Nested
  final class GetUserMapsFolderTest extends AbstractClientSettingTestCase {
    @Test
    void shouldReturnCurrentFolderWhenOverrideFolderNotSet() {
      final Path result =
          ClientFileSystemHelper.getUserMapsFolder(() -> Path.of("/path/to/current"));

      assertThat(
          result,
          is(Path.of("/path", "to", "current", ClientFileSystemHelper.FOLDER_NAME_USER_MAPS)));
    }
  }
}
