package dev.blockhit.sound;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

class SoundBrowserModelTest {
  @Test
  void unavailableSoundsAreExcludedFromEveryCategory() {
    for (SoundGroup group : SoundGroup.values()) {
      assertTrue(SoundBrowserModel.filter(SoundCatalog.entries(), group, "", e -> false).isEmpty());
    }
  }

  @Test
  void recommendedSoundsComeFirstInTheirChosenOrder() {
    List<SoundEntry> sounds =
        SoundBrowserModel.filter(SoundCatalog.entries(), SoundGroup.ALL, "", e -> true);
    assertEquals("random.anvil_land", sounds.getFirst().id());
    boolean reachedOthers = false;
    for (SoundEntry entry : sounds) {
      if (!entry.recommended()) reachedOthers = true;
      else assertFalse(reachedOthers);
    }
  }

  @Test
  void searchUsesReadableNamesAndLegacyIds() {
    assertEquals(
        "mob.villager.yes",
        SoundBrowserModel.filter(
                SoundCatalog.entries(), SoundGroup.ALL, "trade accepted", e -> true)
            .getFirst()
            .id());
    assertEquals(
        "random.anvil_land",
        SoundBrowserModel.filter(SoundCatalog.entries(), SoundGroup.ALL, "anvil_land", e -> true)
            .getFirst()
            .id());
    assertEquals(
        "note.hat",
        SoundBrowserModel.filter(SoundCatalog.entries(), SoundGroup.NOTES, "HI-HAT", e -> true)
            .getFirst()
            .id());
  }

  @Test
  void categoriesDoNotLeakUnrelatedEntries() {
    for (SoundGroup group : SoundGroup.values()) {
      if (group == SoundGroup.ALL || group == SoundGroup.RECOMMENDED) continue;
      for (SoundEntry sound :
          SoundBrowserModel.filter(SoundCatalog.entries(), group, "", e -> true))
        assertEquals(group, sound.group());
    }
  }

  @Test
  void namesAreReadableAndSelectionIndexesRemainStable() {
    for (SoundEntry sound : SoundCatalog.entries()) {
      assertFalse(sound.name().contains("."), sound.id());
      assertFalse(sound.name().contains("_"), sound.id());
      assertFalse(sound.name().isBlank(), sound.id());
      assertEquals(sound.id(), SoundCatalog.id(sound.index()));
      assertFalse(SoundSettings.normalizeId(sound.id()).isEmpty(), sound.id());
    }
    assertEquals("Anvil Impact", SoundCatalog.name(SoundCatalog.indexOf("random.anvil_land")));
    assertEquals(
        "Zombie Pigman - Angry",
        SoundCatalog.name(SoundCatalog.indexOf("mob.zombiepig.zpigangry")));
  }
}
