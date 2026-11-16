package dev.blockhit.sound;

public record SoundEntry(int index, String id, String name, SoundGroup group, int recommendation) {
  public boolean recommended() {
    return recommendation >= 0;
  }
}
