package dev.blockhit.timing;

public final class BlockWindow {
  private boolean blocking;
  private boolean recorded;
  private long lastBlocking;
  private boolean played;
  private long lastSound;

  public void observe(boolean value, long now) {
    if (value || blocking) {
      lastBlocking = now;
      recorded = true;
    }
    blocking = value;
  }

  public boolean matches(boolean swordHeld, boolean blockingNow, long now, int graceMs) {
    if (!swordHeld) return false;
    if (blockingNow) return true;
    long age = now - lastBlocking;
    return recorded && graceMs > 0 && age >= 0 && age <= Math.min(300, graceMs) * 1_000_000L;
  }

  public boolean allow(long now, int intervalMs) {
    long interval = Math.max(0, Math.min(500, intervalMs)) * 1_000_000L;
    if (played && now - lastSound < interval) return false;
    lastSound = now;
    played = true;
    return true;
  }

  public void clear() {
    blocking = recorded = played = false;
    lastBlocking = lastSound = 0;
  }
}
