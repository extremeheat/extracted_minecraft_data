package net.minecraft.world.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class MobEffectIds {
   public static final ResourceKey<MobEffect> SPEED = createKey("speed");
   public static final ResourceKey<MobEffect> SLOWNESS = createKey("slowness");
   public static final ResourceKey<MobEffect> HASTE = createKey("haste");
   public static final ResourceKey<MobEffect> MINING_FATIGUE = createKey("mining_fatigue");
   public static final ResourceKey<MobEffect> STRENGTH = createKey("strength");
   public static final ResourceKey<MobEffect> INSTANT_HEALTH = createKey("instant_health");
   public static final ResourceKey<MobEffect> INSTANT_DAMAGE = createKey("instant_damage");
   public static final ResourceKey<MobEffect> JUMP_BOOST = createKey("jump_boost");
   public static final ResourceKey<MobEffect> NAUSEA = createKey("nausea");
   public static final ResourceKey<MobEffect> REGENERATION = createKey("regeneration");
   public static final ResourceKey<MobEffect> RESISTANCE = createKey("resistance");
   public static final ResourceKey<MobEffect> FIRE_RESISTANCE = createKey("fire_resistance");
   public static final ResourceKey<MobEffect> WATER_BREATHING = createKey("water_breathing");
   public static final ResourceKey<MobEffect> INVISIBILITY = createKey("invisibility");
   public static final ResourceKey<MobEffect> BLINDNESS = createKey("blindness");
   public static final ResourceKey<MobEffect> NIGHT_VISION = createKey("night_vision");
   public static final ResourceKey<MobEffect> HUNGER = createKey("hunger");
   public static final ResourceKey<MobEffect> WEAKNESS = createKey("weakness");
   public static final ResourceKey<MobEffect> POISON = createKey("poison");
   public static final ResourceKey<MobEffect> WITHER = createKey("wither");
   public static final ResourceKey<MobEffect> HEALTH_BOOST = createKey("health_boost");
   public static final ResourceKey<MobEffect> ABSORPTION = createKey("absorption");
   public static final ResourceKey<MobEffect> SATURATION = createKey("saturation");
   public static final ResourceKey<MobEffect> GLOWING = createKey("glowing");
   public static final ResourceKey<MobEffect> LEVITATION = createKey("levitation");
   public static final ResourceKey<MobEffect> LUCK = createKey("luck");
   public static final ResourceKey<MobEffect> UNLUCK = createKey("unluck");
   public static final ResourceKey<MobEffect> SLOW_FALLING = createKey("slow_falling");
   public static final ResourceKey<MobEffect> CONDUIT_POWER = createKey("conduit_power");
   public static final ResourceKey<MobEffect> DOLPHINS_GRACE = createKey("dolphins_grace");
   public static final ResourceKey<MobEffect> BAD_OMEN = createKey("bad_omen");
   public static final ResourceKey<MobEffect> HERO_OF_THE_VILLAGE = createKey("hero_of_the_village");
   public static final ResourceKey<MobEffect> DARKNESS = createKey("darkness");
   public static final ResourceKey<MobEffect> TRIAL_OMEN = createKey("trial_omen");
   public static final ResourceKey<MobEffect> RAID_OMEN = createKey("raid_omen");
   public static final ResourceKey<MobEffect> WIND_CHARGED = createKey("wind_charged");
   public static final ResourceKey<MobEffect> WEAVING = createKey("weaving");
   public static final ResourceKey<MobEffect> OOZING = createKey("oozing");
   public static final ResourceKey<MobEffect> INFESTED = createKey("infested");
   public static final ResourceKey<MobEffect> BREATH_OF_THE_NAUTILUS = createKey("breath_of_the_nautilus");
   public static final ResourceKey<MobEffect> FREEZING = createKey("freezing");

   public MobEffectIds() {
      super();
   }

   private static ResourceKey<MobEffect> createKey(final String location) {
      return ResourceKey.create(Registries.MOB_EFFECT, Identifier.withDefaultNamespace(location));
   }
}
