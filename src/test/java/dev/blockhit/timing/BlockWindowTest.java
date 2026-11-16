package dev.blockhit.timing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BlockWindowTest {
  @Test
  void currentBlockingNeedsNoHistory() {
    assertTrue(new BlockWindow().matches(true, true, 1, 0));
  }

  @Test
  void freshStateDoesNotInventARecentBlock() {
    assertFalse(new BlockWindow().matches(true, false, 1, 300));
  }

  @Test
  void graceStartsAtReleaseAndDoesNotExtendOnLaterObservations() {
    BlockWindow w = new BlockWindow();
    w.observe(true, 10_000_000L);
    w.observe(false, 20_000_000L);
    w.observe(false, 80_000_000L);
    assertTrue(w.matches(true, false, 95_000_000L, 75));
    assertFalse(w.matches(true, false, 95_000_001L, 75));
  }

  @Test
  void zeroGraceRequiresCurrentlyBlocking() {
    BlockWindow w = new BlockWindow();
    w.observe(true, 100);
    w.observe(false, 101);
    assertFalse(w.matches(true, false, 101, 0));
  }

  @Test
  void switchingAwayFromASwordRejectsGrace() {
    BlockWindow w = new BlockWindow();
    w.observe(true, 100);
    assertFalse(w.matches(false, false, 101, 300));
  }

  @Test
  void clearingWorldRemovesHistoryAndCooldown() {
    BlockWindow w = new BlockWindow();
    w.observe(true, 100);
    w.allow(100, 500);
    w.clear();
    assertFalse(w.matches(true, false, 101, 300));
    assertTrue(w.allow(101, 500));
  }

  @Test
  void intervalDropsEventsRatherThanDelayingThem() {
    BlockWindow w = new BlockWindow();
    assertTrue(w.allow(100_000_000L, 100));
    assertFalse(w.allow(150_000_000L, 100));
    assertTrue(w.allow(200_000_000L, 100));
  }

  @Test
  void zeroIntervalAllowsEveryEvent() {
    BlockWindow w = new BlockWindow();
    assertTrue(w.allow(1, 0));
    assertTrue(w.allow(1, 0));
  }
}
