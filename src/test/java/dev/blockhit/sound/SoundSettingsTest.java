package dev.blockhit.sound;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SoundSettingsTest {
  @Test
  void zeroVariationKeepsExactPitch() {
    assertEquals(1.5f, SoundSettings.pitch(1.5f, 0, 0));
  }

  @Test
  void variationStaysWithinMinecraftPitchBounds() {
    assertEquals(.5f, SoundSettings.pitch(.5f, .5f, 0));
    assertEquals(2f, SoundSettings.pitch(2f, .5f, 1));
  }

  @Test
  void malformedConfigCannotProduceNonFiniteAudioValues() {
    assertTrue(Float.isFinite(SoundSettings.pitch(Float.NaN, Float.POSITIVE_INFINITY, Float.NaN)));
    assertEquals(.5f, SoundSettings.volume(Float.NaN));
    assertEquals(0, SoundSettings.volume(-100));
    assertEquals(2, SoundSettings.volume(1000));
  }

  @Test
  void customIdsAcceptVanillaAndResourcePackEvents() {
    assertEquals("minecraft:note.hat", SoundSettings.normalizeId(" note.hat "));
    assertEquals("mypack:combat/block", SoundSettings.normalizeId("mypack:combat/block"));
    assertEquals("", SoundSettings.normalizeId("../bad sound"));
    assertEquals("", SoundSettings.normalizeId("MOD:BLOCK"));
  }

  @Test
  void catalogContainsEveryVanillaEventWithoutDuplicates() {
    String[] ids = SoundCatalog.labels();
    assertEquals(246, ids.length);
    assertEquals(ids.length, new java.util.HashSet<>(java.util.List.of(ids)).size());
    for (String id : ids) assertEquals(id, SoundCatalog.id(SoundCatalog.indexOf(id)));
    assertEquals("random.anvil_land", SoundCatalog.id(SoundCatalog.indexOf("random.anvil_land")));
  }
}
