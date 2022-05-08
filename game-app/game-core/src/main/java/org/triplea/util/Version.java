package org.triplea.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.Nonnull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.triplea.java.RemoveOnNextMajorRelease;

/** Represents a version string. versions are of the form major.minor.point */
@Getter
@EqualsAndHashCode
public final class Version implements Serializable, Comparable<Version> {
  private static final long serialVersionUID = -4770210855326775333L;

  /** Indicates engine incompatible releases. */
  private final int major;
  /** Indicates engine compatible releases. */
  private final int minor;
  /**
   * Point (build number), unused, kept for serialization compatibility.
   *
   * @deprecated Do not use, use 'buildNumber' instead.
   */
  @RemoveOnNextMajorRelease @Deprecated private int point;

  private final String buildNumber;

  /** version must be of the from xx.xx.xx or xx.xx or xx where xx is a positive integer */
  public Version(final String version) {
    final String[] parts = version.split("\\.", -1);
    if (parts.length == 0) {
      throw new IllegalArgumentException("Invalid version String: " + version);
    }

    major = Integer.parseInt(parts[0]);
    minor = parts.length <= 1 ? 0 : Integer.parseInt(parts[1]);
    buildNumber = parts.length <= 2 ? "" : parts[2];
  }

  @Override
  public int compareTo(@Nonnull final Version other) {
    checkNotNull(other);

    return Comparator.comparingInt(Version::getMajor)
        .thenComparingInt(Version::getMinor)
        .compare(this, other);
  }

  /**
   * Indicates this version is greater than the specified version.
   *
   * @param other The version to compare.
   * @return {@code true} if this version is greater than the specified version; otherwise {@code
   *     false}.
   */
  public boolean isGreaterThan(final Version other) {
    checkNotNull(other);

    return compareTo(other) > 0;
  }

  /** Creates a complete version string with '.' as separator. */
  @Override
  public String toString() {
    return String.join(".", String.valueOf(major), String.valueOf(minor))
        + (buildNumber.isEmpty() ? "" : "." + buildNumber);
  }

  /**
   * Indicates this engine version is compatible with the specified map minimum engine version.
   *
   * @param mapMinimumEngineVersion The minimum engine version required by the map.
   * @return {@code true} if this engine version is compatible with the specified map minimum engine
   *     version; otherwise {@code false}.
   */
  public boolean isCompatibleWithMapMinimumEngineVersion(final Version mapMinimumEngineVersion) {
    checkNotNull(mapMinimumEngineVersion);

    return major > mapMinimumEngineVersion.major
        || (major == mapMinimumEngineVersion.major && minor >= mapMinimumEngineVersion.minor);
  }
}
