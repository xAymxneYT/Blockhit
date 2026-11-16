package dev.blockhit.sound;

import net.minecraft.client.sound.instance.AbstractSoundInstance;
import net.minecraft.client.sound.instance.SoundInstance;
import net.minecraft.resource.Identifier;

public final class BlockhitSound extends AbstractSoundInstance {
  public BlockhitSound(Identifier id, float volume, float pitch) {
    super(id);
    this.volume = volume;
    this.pitch = pitch;
    this.attenuation = SoundInstance.Attenuation.NONE;
  }
}
