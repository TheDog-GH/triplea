package games.strategy.engine.framework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NonNls;
import org.junit.jupiter.api.Test;

class I18nResourceBundleTest {
  private static final String resourcePath = "propertyTest";
  private static final int maxTextMatchLengthWithEn = 10;

  private static int countStringsIn(final File file) {
    int sum = 0;
    if (file.isDirectory()) {
      final File[] subfiles = file.listFiles();
      final Iterator<File> fileIterator = Arrays.stream(subfiles).iterator();
      while (fileIterator.hasNext()) {
        sum += countStringsIn(fileIterator.next());
      }
    } else if (file.getName().endsWith(".java")) {
      final BufferedReader reader;
      try {
        reader = new BufferedReader(new FileReader(file));
      } catch (final FileNotFoundException e) {
        return sum;
      }
      final Pattern pattern = Pattern.compile(".*\".*\".*");
      while (1 > 0) {
        String line = null;
        try {
          line = reader.readLine();
        } catch (final IOException e) {
          break;
        }
        if (line == null) break;
        final Matcher matcher = pattern.matcher(line);
        if (matcher.find()) sum++;
      }
      try {
        reader.close();
      } catch (final IOException e) {
        return sum;
      }
    }
    return sum;
  }

  @Test
  void stringCount() {
    String currentWorkingDir = System.getProperty("user.dir");
    // final Path root = Paths.getClientFileSystemHelper.getRootFolder();
    currentWorkingDir = "C:\\Users\\evond\\git\\triplea\\game-app\\ai"; // \\src\\main\\java";
    final File rootFile = new File(currentWorkingDir).getParentFile().getParentFile();
    final int totalSum = countStringsIn(rootFile);

    assertEquals(100, totalSum);
  }

  @NonNls
  private static String errorTextNotTranslated(
      final ResourceBundle bundle, final String lang, final String key) {
    return "i18n error: Key '"
        + key
        + "' not translated into language '"
        + lang
        + "'; Check bundle '"
        + bundle.getBaseBundleName()
        + "'";
  }

  @Test
  void i18nGet() {
    final I18nResourceBundle i18nBundle = I18nTestBundle.get();
    assertNotNull(i18nBundle);
  }

  @Test
  void i18nIsSupported() {
    assertTrue(I18nResourceBundle.isSupported(Locale.ENGLISH));
  }

  @Test
  void i18nGetResourcePath() {
    final I18nResourceBundle i18nBundle = I18nTestBundle.get();
    assertEquals(resourcePath, i18nBundle.getResourcePath());
  }

  @Test
  void i18nGetText() {
    final I18nResourceBundle i18nBundle = I18nTestBundle.get();
    assertEquals("Test Text: Happy Testing", i18nBundle.getString("test.Text"));
  }

  /**
   * Add each subclass of I18nResourceBundle!
   *
   * <p>Tests for each listed subclass of I18nResourceBundle whether the properties files are having
   * a key maintained as in the onw for English (default). For string values longer than
   * maxTextMatchLengthWithEn the text must be different, as otherwise a missing translation is
   * assumed.
   */
  @Test
  void ensureLocalizedResultOfGetText() {
    // Add subclasses of I18nResourceBundle below
    final List<Class<? extends I18nResourceBundle>> subclasses = List.of(I18nEngineFramework.class);
    subclasses.forEach(
        (subclass) -> {
          // instantiate i18nBundle
          I18nResourceBundle i18nBundle = null;
          try {
            i18nBundle = subclass.getDeclaredConstructor().newInstance();
          } catch (final NoSuchMethodException
              | InvocationTargetException
              | InstantiationException
              | IllegalAccessException e) {
            assertNotNull(e);
          }
          assertNotNull(i18nBundle);
          // Check each language bundle for each key against default language (English)
          final String baseBundleName = i18nBundle.getBaseBundleName();
          final Locale localeBase = Locale.US;
          final ResourceBundle bundleEn = ResourceBundle.getBundle(baseBundleName, localeBase);

          for (final Locale locale : I18nResourceBundle.getMapSupportedLocales()) {
            if (locale.equals(localeBase)) {
              continue; // do not check against base Locale
            }
            final ResourceBundle bundle = ResourceBundle.getBundle(baseBundleName, locale);
            for (final String key : bundleEn.keySet()) {
              try {
                final String langString = bundle.getString(key);
                @NonNls final String enString = bundleEn.getString(key);
                if (enString.length() >= maxTextMatchLengthWithEn) {
                  continue; // too short to assume translation is missing from matching strings
                } else {
                  assert !enString.equals(langString)
                      : errorTextNotTranslated(bundle, locale.toString(), key);
                }
                assert 0 != langString.length()
                    : errorTextNotTranslated(bundle, locale.toString(), key);
              } catch (final MissingResourceException e) {
                assertNotNull(e);
              }
            }
          }
        });
  }

  static class I18nTestBundle extends I18nResourceBundle {

    private static I18nResourceBundle instance;

    public static I18nResourceBundle get() {
      if (instance == null) {
        instance = new I18nTestBundle();
      }
      return instance;
    }

    @Override
    public String getResourcePath() {
      return resourcePath;
    }
  }
}
