package dev.blockhit.config;

import dev.blockhit.BlockhitClient;
import dev.blockhit.sound.SoundCatalog;
import java.util.Locale;
import org.polyfrost.oneconfig.api.config.v1.*;
import org.polyfrost.oneconfig.api.config.v1.annotations.*;
import org.polyfrost.oneconfig.api.ui.v1.OptionWidgets;

@SuppressWarnings("unused")
public final class BlockhitConfig extends Config {
  @Switch(title = "Enable Blockhit", category = "General", subcategory = "General")
  public boolean enabled = true;

  @SoundChoice(title = "Sound")
  public int sound = SoundCatalog.indexOf("random.anvil_land");

  @Slider(
      title = "Volume",
      min = 0,
      max = 200,
      formatter = Percent.class,
      category = "General",
      subcategory = "Sound")
  public float volume = 40;

  @Slider(
      title = "Pitch",
      description = "1.00x is the original pitch.",
      min = .5f,
      max = 2,
      step = .05f,
      formatter = Pitch.class,
      category = "General",
      subcategory = "Sound")
  public float pitch = 1.5f;

  @Switch(
      title = "Replace hurt sound",
      description = "Replace the normal hurt sound when Blockhit plays.",
      category = "General",
      subcategory = "Sound")
  public boolean replaceHurt = false;

  @Slider(
      title = "Pitch variation",
      description = "0 keeps every hit at the same pitch.",
      min = 0,
      max = .5f,
      step = .01f,
      formatter = Pitch.class,
      category = "Advanced",
      subcategory = "Playback")
  public float pitchVariation = 0;

  @Switch(
      title = "Allow overlapping sounds",
      description = "Let the previous sound finish when another hit arrives.",
      category = "Advanced",
      subcategory = "Playback")
  public boolean overlap = false;

  @Switch(title = "Enable second sound", category = "Advanced", subcategory = "Second sound")
  public boolean secondSound = false;

  @SoundChoice(title = "Sound", category = "Advanced", subcategory = "Second sound")
  public int layerSound = SoundCatalog.indexOf("note.hat");

  @Slider(
      title = "Volume",
      min = 0,
      max = 200,
      formatter = Percent.class,
      category = "Advanced",
      subcategory = "Second sound")
  public float layerVolume = 20;

  @Slider(
      title = "Pitch",
      min = .5f,
      max = 2,
      step = .05f,
      formatter = Pitch.class,
      category = "Advanced",
      subcategory = "Second sound")
  public float layerPitch = 1;

  @Slider(
      title = "Release grace",
      description = "Match hits reported shortly after you release block. 0 disables this.",
      min = 0,
      max = 300,
      step = 5,
      formatter = Milliseconds.class,
      category = "Advanced",
      subcategory = "Timing")
  public int releaseGrace = 75;

  @Slider(
      title = "Sound cooldown",
      description = "Minimum time between sounds. 0 plays every matching hit.",
      min = 0,
      max = 500,
      step = 10,
      formatter = Milliseconds.class,
      category = "Advanced",
      subcategory = "Timing")
  public int minimumInterval = 0;

  @Switch(title = "Mute while in menus", category = "Advanced", subcategory = "Where to play")
  public boolean muteInMenus = false;

  @Switch(title = "Singleplayer only", category = "Advanced", subcategory = "Where to play")
  public boolean singleplayerOnly = false;

  @Switch(title = "Use custom sound", category = "Advanced", subcategory = "Resource packs")
  public boolean customSound = false;

  @Text(
      title = "Sound ID",
      description = "For example mypack:blockhit. Choose a normal sound to turn this off.",
      category = "Advanced",
      subcategory = "Resource packs")
  public String customId = "minecraft:random.anvil_land";

  public BlockhitConfig() {
    super("blockhit.json", "assets/blockhit/icon.png", "Blockhit", Category.QOL);
    addAliases(new String[] {"block hit", "blocking sound", "sword sound"});
    hideIf("layerSound", "secondSound");
    hideIf("layerVolume", "secondSound");
    hideIf("layerPitch", "secondSound");
    hideIf("customId", "customSound");
  }

  public Property<?> control(String name) {
    return getProperty(name);
  }

  public void updateSoundLabels() {
    getProperty("sound")
        .addMetadata("description", customSound ? customId : SoundCatalog.name(sound));
    getProperty("layerSound").addMetadata("description", SoundCatalog.name(layerSound));
    OptionWidgets.refresh(getProperty("sound"));
    OptionWidgets.refresh(getProperty("layerSound"));
  }

  public void selectSound(int index, boolean layer) {
    getProperty(layer ? "layerSound" : "sound").setAs(index);
    if (!layer) getProperty("customSound").setAs(false);
    updateSoundLabels();
    save();
  }

  @Override
  protected void onInitialized(Tree tree) {
    getProperty("sound")
        .addMetadata("runnable", (Runnable) () -> BlockhitClient.get().chooseSound(false));
    getProperty("layerSound")
        .addMetadata("runnable", (Runnable) () -> BlockhitClient.get().chooseSound(true));
    updateSoundLabels();
  }

  public static final class Percent implements ValueFormatter {
    public String format(java.lang.Number value) {
      return Math.round(value.floatValue()) + "%";
    }
  }

  public static final class Pitch implements ValueFormatter {
    public String format(java.lang.Number value) {
      return String.format(Locale.ROOT, "%.2fx", value.floatValue());
    }
  }

  public static final class Milliseconds implements ValueFormatter {
    public String format(java.lang.Number value) {
      return value.intValue() + " ms";
    }
  }
}
