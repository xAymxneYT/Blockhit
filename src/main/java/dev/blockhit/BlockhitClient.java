package dev.blockhit;

import dev.blockhit.config.BlockhitConfig;
import dev.blockhit.sound.*;
import dev.blockhit.timing.BlockWindow;
import dev.blockhit.ui.SoundBrowserPage;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.SoundPool;
import net.minecraft.client.sound.instance.SoundInstance;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.resource.Identifier;
import net.minecraft.world.World;
import org.polyfrost.oneconfig.api.notifications.v1.Notifications;

public final class BlockhitClient implements ClientModInitializer {
  private static BlockhitClient instance;
  private final BlockWindow window = new BlockWindow();
  private BlockhitConfig config;
  private World world;
  private boolean muteCurrentHurt;
  private SoundInstance primarySound;
  private SoundInstance secondarySound;

  public static BlockhitClient get() {
    return instance;
  }

  @Override
  public void onInitializeClient() {
    instance = this;
    config = new BlockhitConfig();
  }

  public void observeBlocking() {
    Minecraft mc = Minecraft.getInstance();
    if (world != mc.world) {
      world = mc.world;
      window.clear();
      stopSounds(mc);
      muteCurrentHurt = false;
    }
    if (mc.player == null || !config.enabled) {
      window.clear();
      stopSounds(mc);
      muteCurrentHurt = false;
      return;
    }
    window.observe(mc.player.isSwordBlocking(), System.nanoTime());
  }

  public void beginHurt(LivingEntity entity, byte event) {
    Minecraft mc = Minecraft.getInstance();
    if (entity != mc.player || event != 2) return;
    muteCurrentHurt = false;
    observeBlocking();
    if (mc.world == null
        || !config.enabled
        || mc.player.isSpectator()
        || (config.singleplayerOnly && !mc.isSingleplayer())
        || (config.muteInMenus && mc.screen != null)) return;
    long now = System.nanoTime();
    boolean swordHeld =
        mc.player.getItemInHand() != null
            && mc.player.getItemInHand().getItem() instanceof SwordItem;
    if (!window.matches(swordHeld, mc.player.isSwordBlocking(), now, config.releaseGrace)) return;
    if (!window.allow(now, config.minimumInterval)) return;
    boolean played = play();
    muteCurrentHurt = played && config.replaceHurt;
  }

  public void endHurt(LivingEntity entity) {
    if (entity == Minecraft.getInstance().player) muteCurrentHurt = false;
  }

  public boolean muteHurt(LivingEntity entity) {
    return entity == Minecraft.getInstance().player && muteCurrentHurt;
  }

  public void chooseSound(boolean layer) {
    SoundBrowserPage.open(config, layer);
  }

  public boolean available(SoundEntry entry) {
    SoundPool pool =
        Minecraft.getInstance().getSoundManager().get(new Identifier("minecraft", entry.id()));
    return pool != null && pool.getWeight() > 0;
  }

  public boolean previewSound(SoundEntry entry, boolean layer) {
    Minecraft mc = Minecraft.getInstance();
    stopSounds(mc);
    return sound(
        mc,
        entry.id(),
        layer ? config.layerVolume : config.volume,
        layer ? config.layerPitch : config.pitch,
        0,
        layer);
  }

  public void stopPreview() {
    stopSounds(Minecraft.getInstance());
  }

  public void preview() {
    if (!play())
      Notifications.error(
          "Blockhit", "Sound not found or volume is zero. Check the sound ID and resource pack.");
  }

  private boolean play() {
    Minecraft mc = Minecraft.getInstance();
    if (!config.overlap) stopSounds(mc);
    boolean primary =
        sound(
            mc,
            config.customSound ? config.customId : SoundCatalog.id(config.sound),
            config.volume,
            config.pitch,
            config.pitchVariation,
            false);
    boolean layer =
        config.secondSound
            && sound(
                mc,
                SoundCatalog.id(config.layerSound),
                config.layerVolume,
                config.layerPitch,
                config.pitchVariation,
                true);
    return primary || layer;
  }

  private void stopSounds(Minecraft mc) {
    if (primarySound != null) mc.getSoundManager().stop(primarySound);
    if (secondarySound != null) mc.getSoundManager().stop(secondarySound);
    primarySound = secondarySound = null;
  }

  private boolean sound(
      Minecraft mc, String raw, float volume, float pitch, float variation, boolean layer) {
    String normalized = SoundSettings.normalizeId(raw);
    float gain = SoundSettings.volume(volume);
    if (normalized.isEmpty() || gain <= 0) return false;
    Identifier id = new Identifier(normalized);
    SoundPool pool = mc.getSoundManager().get(id);
    if (pool == null || pool.getWeight() <= 0) return false;
    float randomizedPitch =
        SoundSettings.pitch(pitch, variation, ThreadLocalRandom.current().nextFloat());
    SoundInstance sound = new BlockhitSound(id, gain, randomizedPitch);
    mc.getSoundManager().play(sound);
    if (layer) secondarySound = sound;
    else primarySound = sound;
    return true;
  }
}
