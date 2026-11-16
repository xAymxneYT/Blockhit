package dev.blockhit.sound;

public final class SoundSettings {
  private SoundSettings() {}

  public static float volume(float percent) {
    return clamp(percent, 0, 200, 50) / 100f;
  }

  public static float pitch(float base, float variation, float random) {
    return clamp(
        clamp(base, .5f, 2, 1) + (clamp(random, 0, 1, .5f) * 2 - 1) * clamp(variation, 0, .5f, 0),
        .5f,
        2,
        1);
  }

  public static float clamp(float value, float min, float max, float fallback) {
    return Float.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
  }

  public static String normalizeId(String value) {
    if (value == null) return "";
    String id = value.trim();
    if (!id.contains(":")) id = "minecraft:" + id;
    return id.matches("[a-z0-9_.-]+:[a-zA-Z0-9_./-]+") ? id : "";
  }
}
