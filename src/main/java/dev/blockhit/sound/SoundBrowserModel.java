package dev.blockhit.sound;

import java.util.*;
import java.util.function.Predicate;

public final class SoundBrowserModel {
  private SoundBrowserModel() {}

  public static List<SoundEntry> filter(
      List<SoundEntry> entries, SoundGroup group, String query, Predicate<SoundEntry> available) {
    String term = query.strip().toLowerCase(Locale.ROOT);
    return entries.stream()
        .filter(available)
        .filter(
            e ->
                group == SoundGroup.ALL
                    || group == SoundGroup.RECOMMENDED && e.recommended()
                    || e.group() == group)
        .filter(e -> (e.name() + " " + e.id()).toLowerCase(Locale.ROOT).contains(term))
        .sorted(
            Comparator.comparingInt(
                    (SoundEntry e) -> e.recommended() ? e.recommendation() : Integer.MAX_VALUE)
                .thenComparing(SoundEntry::name))
        .toList();
  }
}
