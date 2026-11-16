package dev.blockhit.sound;

public enum SoundGroup {
  RECOMMENDED("Recommended"),
  ALL("All sounds"),
  COMBAT("Combat"),
  BLOCKS("Blocks"),
  MOBS("Mobs"),
  NOTES("Notes"),
  INTERFACE("Interface"),
  WORLD("World"),
  MUSIC("Music");
  public final String title;

  SoundGroup(String title) {
    this.title = title;
  }
}
