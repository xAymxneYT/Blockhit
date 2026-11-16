package dev.blockhit.sound;

import java.util.*;

final class SoundNames {
  private static final Map<String, String> NAMES =
      Map.ofEntries(
          Map.entry("random.anvil_land", "Anvil Impact"),
          Map.entry("random.anvil_use", "Anvil Repair"),
          Map.entry("random.anvil_break", "Anvil Break"),
          Map.entry("random.successful_hit", "Successful Bow Hit"),
          Map.entry("random.bowhit", "Arrow Impact"),
          Map.entry("random.bow", "Bow Shot"),
          Map.entry("random.levelup", "Level Up"),
          Map.entry("random.orb", "Experience Orb"),
          Map.entry("random.break", "Item Break"),
          Map.entry("random.chestclosed", "Chest Close"),
          Map.entry("random.chestopen", "Chest Open"),
          Map.entry("random.wood_click", "Wooden Button Click"),
          Map.entry("random.click", "Click"),
          Map.entry("gui.button.press", "Button Click"),
          Map.entry("note.bd", "Bass Drum"),
          Map.entry("note.bassattack", "Bass Guitar"),
          Map.entry("note.harp", "Harp"),
          Map.entry("note.hat", "Hi-hat"),
          Map.entry("note.pling", "Pling"),
          Map.entry("note.snare", "Snare Drum"),
          Map.entry("note.bass", "Bass Note"),
          Map.entry("ambient.cave.cave", "Cave Ambience"),
          Map.entry("ambient.weather.rain", "Rain"),
          Map.entry("ambient.weather.thunder", "Thunder"),
          Map.entry("fire.fire", "Fire Crackle"),
          Map.entry("fire.ignite", "Fire Ignite"),
          Map.entry("liquid.lavapop", "Lava Pop"),
          Map.entry("liquid.lava", "Lava Bubbles"),
          Map.entry("liquid.water", "Flowing Water"),
          Map.entry("tile.piston.in", "Piston Retract"),
          Map.entry("tile.piston.out", "Piston Extend"),
          Map.entry("game.potion.smash", "Potion Smash"),
          Map.entry("game.tnt.primed", "TNT Fuse"),
          Map.entry("creeper.primed", "Creeper Fuse"),
          Map.entry("item.fireCharge.use", "Fire Charge"),
          Map.entry("portal.portal", "Portal Hum"),
          Map.entry("portal.travel", "Portal Travel"),
          Map.entry("portal.trigger", "Portal Enter"),
          Map.entry("random.explode", "Explosion"),
          Map.entry("random.fizz", "Fizz"),
          Map.entry("random.door_close", "Door Close"),
          Map.entry("random.door_open", "Door Open"),
          Map.entry("random.burp", "Burp"),
          Map.entry("random.drink", "Drink"),
          Map.entry("random.eat", "Eat"),
          Map.entry("random.pop", "Item Pop"),
          Map.entry("random.splash", "Water Splash"),
          Map.entry("minecart.base", "Minecart Rolling"),
          Map.entry("minecart.inside", "Inside Minecart"),
          Map.entry("mob.villager.yes", "Villager - Trade Accepted"),
          Map.entry("mob.villager.no", "Villager - Trade Refused"),
          Map.entry("mob.villager.haggle", "Villager - Trading"),
          Map.entry("mob.chicken.plop", "Chicken - Lay Egg"),
          Map.entry("mob.zombie.metal", "Zombie - Hit Iron Door"),
          Map.entry("mob.zombie.wood", "Zombie - Hit Wooden Door"),
          Map.entry("mob.zombie.woodbreak", "Zombie - Break Wooden Door"),
          Map.entry("mob.zombie.infect", "Zombie - Infect Villager"),
          Map.entry("mob.zombie.unfect", "Zombie Villager - Cured"),
          Map.entry("mob.zombie.remedy", "Zombie Villager - Curing"),
          Map.entry("mob.creeper.say", "Creeper - Hurt"),
          Map.entry("mob.cat.hitt", "Cat - Hurt"),
          Map.entry("mob.cat.purreow", "Cat - Purr Meow"),
          Map.entry("mob.silverfish.kill", "Silverfish - Death"),
          Map.entry("mob.irongolem.throw", "Iron Golem - Attack"),
          Map.entry("mob.horse.soft", "Horse - Grass Steps"),
          Map.entry("mob.horse.wood", "Horse - Wooden Steps"),
          Map.entry("mob.horse.leather", "Horse - Saddle"),
          Map.entry("mob.horse.armor", "Horse - Equip Armor"),
          Map.entry("mob.enderdragon.end", "Ender Dragon - Death"),
          Map.entry("mob.zombiepig.zpig", "Zombie Pigman - Idle"),
          Map.entry("mob.zombiepig.zpigangry", "Zombie Pigman - Angry"),
          Map.entry("mob.zombiepig.zpigdeath", "Zombie Pigman - Death"),
          Map.entry("mob.zombiepig.zpighurt", "Zombie Pigman - Hurt"));

  private SoundNames() {}

  static String name(String id) {
    String named = NAMES.get(id);
    if (named != null) return named;
    if (id.startsWith("dig.")) return words(id.substring(4).replace("cloth", "wool")) + " Break";
    if (id.startsWith("step."))
      return words(id.substring(5).replace("cloth", "wool")) + " Footstep";
    if (id.startsWith("records.")) return "Music Disc - " + words(id.substring(8));
    if (id.startsWith("music.")) return "Music - " + words(id.substring(6));
    if (id.startsWith("fireworks."))
      return "Firework - " + words(id.substring(10).replace("_far", " distant"));
    if (id.startsWith("mob.")) {
      String path =
          id.substring(4)
              .replace("horse.donkey", "donkey")
              .replace("horse.skeleton", "skeleton_horse")
              .replace("horse.zombie", "zombie_horse")
              .replace("guardian.elder", "elder_guardian")
              .replace("guardian.land", "guardian_on_land");
      int split = path.indexOf('.');
      String creature =
          words(
              path.substring(0, split)
                  .replace("enderdragon", "ender_dragon")
                  .replace("endermen", "enderman")
                  .replace("irongolem", "iron_golem")
                  .replace("magmacube", "magma_cube"));
      String action = path.substring(split + 1);
      action =
          switch (action) {
            case "say" -> "idle";
            case "hit", "hitt" -> "hurt";
            case "walk", "step" -> "footstep";
            default -> action;
          };
      return creature + " - " + words(action);
    }
    if (id.startsWith("game."))
      return words(
          id.substring(5)
              .replace("hurt.fall.big", "heavy_fall")
              .replace("hurt.fall.small", "light_fall")
              .replace("swim.splash", "splash")
              .replace("die", "death"));
    return words(id);
  }

  private static String words(String value) {
    String text = value.replaceAll("([a-z])([A-Z])", "$1 $2").replace('.', ' ').replace('_', ' ');
    StringJoiner result = new StringJoiner(" ");
    for (String word : text.split(" +"))
      if (!word.isEmpty()) result.add(Character.toUpperCase(word.charAt(0)) + word.substring(1));
    return result.toString();
  }
}
