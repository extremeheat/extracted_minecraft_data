package net.minecraft.world.item.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Potions {
   public static final Holder<Potion> WATER;
   public static final Holder<Potion> MUNDANE;
   public static final Holder<Potion> THICK;
   public static final Holder<Potion> AWKWARD;
   public static final Holder<Potion> NIGHT_VISION;
   public static final Holder<Potion> LONG_NIGHT_VISION;
   public static final Holder<Potion> INVISIBILITY;
   public static final Holder<Potion> LONG_INVISIBILITY;
   public static final Holder<Potion> LEAPING;
   public static final Holder<Potion> LONG_LEAPING;
   public static final Holder<Potion> STRONG_LEAPING;
   public static final Holder<Potion> FIRE_RESISTANCE;
   public static final Holder<Potion> LONG_FIRE_RESISTANCE;
   public static final Holder<Potion> SWIFTNESS;
   public static final Holder<Potion> LONG_SWIFTNESS;
   public static final Holder<Potion> STRONG_SWIFTNESS;
   public static final Holder<Potion> SLOWNESS;
   public static final Holder<Potion> LONG_SLOWNESS;
   public static final Holder<Potion> STRONG_SLOWNESS;
   public static final Holder<Potion> TURTLE_MASTER;
   public static final Holder<Potion> LONG_TURTLE_MASTER;
   public static final Holder<Potion> STRONG_TURTLE_MASTER;
   public static final Holder<Potion> WATER_BREATHING;
   public static final Holder<Potion> LONG_WATER_BREATHING;
   public static final Holder<Potion> HEALING;
   public static final Holder<Potion> STRONG_HEALING;
   public static final Holder<Potion> HARMING;
   public static final Holder<Potion> STRONG_HARMING;
   public static final Holder<Potion> POISON;
   public static final Holder<Potion> LONG_POISON;
   public static final Holder<Potion> STRONG_POISON;
   public static final Holder<Potion> REGENERATION;
   public static final Holder<Potion> LONG_REGENERATION;
   public static final Holder<Potion> STRONG_REGENERATION;
   public static final Holder<Potion> STRENGTH;
   public static final Holder<Potion> LONG_STRENGTH;
   public static final Holder<Potion> STRONG_STRENGTH;
   public static final Holder<Potion> WEAKNESS;
   public static final Holder<Potion> LONG_WEAKNESS;
   public static final Holder<Potion> LUCK;
   public static final Holder<Potion> SLOW_FALLING;
   public static final Holder<Potion> LONG_SLOW_FALLING;
   public static final Holder<Potion> WIND_CHARGED;
   public static final Holder<Potion> WEAVING;
   public static final Holder<Potion> OOZING;
   public static final Holder<Potion> INFESTED;

   public Potions() {
      super();
   }

   private static Holder<Potion> register(final ResourceKey<Potion> key, final Potion potion) {
      return Registry.registerForHolder(BuiltInRegistries.POTION, key, potion);
   }

   public static Holder<Potion> bootstrap(final Registry<Potion> registry) {
      return WATER;
   }

   static {
      WATER = register(PotionIds.WATER, new Potion("water", new MobEffectInstance[0]));
      MUNDANE = register(PotionIds.MUNDANE, new Potion("mundane", new MobEffectInstance[0]));
      THICK = register(PotionIds.THICK, new Potion("thick", new MobEffectInstance[0]));
      AWKWARD = register(PotionIds.AWKWARD, new Potion("awkward", new MobEffectInstance[0]));
      NIGHT_VISION = register(PotionIds.NIGHT_VISION, new Potion("night_vision", new MobEffectInstance[]{new MobEffectInstance(MobEffects.NIGHT_VISION, 3600)}));
      LONG_NIGHT_VISION = register(PotionIds.LONG_NIGHT_VISION, new Potion("night_vision", new MobEffectInstance[]{new MobEffectInstance(MobEffects.NIGHT_VISION, 9600)}));
      INVISIBILITY = register(PotionIds.INVISIBILITY, new Potion("invisibility", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INVISIBILITY, 3600)}));
      LONG_INVISIBILITY = register(PotionIds.LONG_INVISIBILITY, new Potion("invisibility", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INVISIBILITY, 9600)}));
      LEAPING = register(PotionIds.LEAPING, new Potion("leaping", new MobEffectInstance[]{new MobEffectInstance(MobEffects.JUMP_BOOST, 3600)}));
      LONG_LEAPING = register(PotionIds.LONG_LEAPING, new Potion("leaping", new MobEffectInstance[]{new MobEffectInstance(MobEffects.JUMP_BOOST, 9600)}));
      STRONG_LEAPING = register(PotionIds.STRONG_LEAPING, new Potion("leaping", new MobEffectInstance[]{new MobEffectInstance(MobEffects.JUMP_BOOST, 1800, 1)}));
      FIRE_RESISTANCE = register(PotionIds.FIRE_RESISTANCE, new Potion("fire_resistance", new MobEffectInstance[]{new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600)}));
      LONG_FIRE_RESISTANCE = register(PotionIds.LONG_FIRE_RESISTANCE, new Potion("fire_resistance", new MobEffectInstance[]{new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600)}));
      SWIFTNESS = register(PotionIds.SWIFTNESS, new Potion("swiftness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SPEED, 3600)}));
      LONG_SWIFTNESS = register(PotionIds.LONG_SWIFTNESS, new Potion("swiftness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SPEED, 9600)}));
      STRONG_SWIFTNESS = register(PotionIds.STRONG_SWIFTNESS, new Potion("swiftness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SPEED, 1800, 1)}));
      SLOWNESS = register(PotionIds.SLOWNESS, new Potion("slowness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOWNESS, 1800)}));
      LONG_SLOWNESS = register(PotionIds.LONG_SLOWNESS, new Potion("slowness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOWNESS, 4800)}));
      STRONG_SLOWNESS = register(PotionIds.STRONG_SLOWNESS, new Potion("slowness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOWNESS, 400, 3)}));
      TURTLE_MASTER = register(PotionIds.TURTLE_MASTER, new Potion("turtle_master", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOWNESS, 400, 3), new MobEffectInstance(MobEffects.RESISTANCE, 400, 2)}));
      LONG_TURTLE_MASTER = register(PotionIds.LONG_TURTLE_MASTER, new Potion("turtle_master", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOWNESS, 800, 3), new MobEffectInstance(MobEffects.RESISTANCE, 800, 2)}));
      STRONG_TURTLE_MASTER = register(PotionIds.STRONG_TURTLE_MASTER, new Potion("turtle_master", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOWNESS, 400, 5), new MobEffectInstance(MobEffects.RESISTANCE, 400, 3)}));
      WATER_BREATHING = register(PotionIds.WATER_BREATHING, new Potion("water_breathing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WATER_BREATHING, 3600)}));
      LONG_WATER_BREATHING = register(PotionIds.LONG_WATER_BREATHING, new Potion("water_breathing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WATER_BREATHING, 9600)}));
      HEALING = register(PotionIds.HEALING, new Potion("healing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1)}));
      STRONG_HEALING = register(PotionIds.STRONG_HEALING, new Potion("healing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 1)}));
      HARMING = register(PotionIds.HARMING, new Potion("harming", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1)}));
      STRONG_HARMING = register(PotionIds.STRONG_HARMING, new Potion("harming", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1)}));
      POISON = register(PotionIds.POISON, new Potion("poison", new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 900)}));
      LONG_POISON = register(PotionIds.LONG_POISON, new Potion("poison", new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 1800)}));
      STRONG_POISON = register(PotionIds.STRONG_POISON, new Potion("poison", new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 432, 1)}));
      REGENERATION = register(PotionIds.REGENERATION, new Potion("regeneration", new MobEffectInstance[]{new MobEffectInstance(MobEffects.REGENERATION, 900)}));
      LONG_REGENERATION = register(PotionIds.LONG_REGENERATION, new Potion("regeneration", new MobEffectInstance[]{new MobEffectInstance(MobEffects.REGENERATION, 1800)}));
      STRONG_REGENERATION = register(PotionIds.STRONG_REGENERATION, new Potion("regeneration", new MobEffectInstance[]{new MobEffectInstance(MobEffects.REGENERATION, 450, 1)}));
      STRENGTH = register(PotionIds.STRENGTH, new Potion("strength", new MobEffectInstance[]{new MobEffectInstance(MobEffects.STRENGTH, 3600)}));
      LONG_STRENGTH = register(PotionIds.LONG_STRENGTH, new Potion("strength", new MobEffectInstance[]{new MobEffectInstance(MobEffects.STRENGTH, 9600)}));
      STRONG_STRENGTH = register(PotionIds.STRONG_STRENGTH, new Potion("strength", new MobEffectInstance[]{new MobEffectInstance(MobEffects.STRENGTH, 1800, 1)}));
      WEAKNESS = register(PotionIds.WEAKNESS, new Potion("weakness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WEAKNESS, 1800)}));
      LONG_WEAKNESS = register(PotionIds.LONG_WEAKNESS, new Potion("weakness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WEAKNESS, 4800)}));
      LUCK = register(PotionIds.LUCK, new Potion("luck", new MobEffectInstance[]{new MobEffectInstance(MobEffects.LUCK, 6000)}));
      SLOW_FALLING = register(PotionIds.SLOW_FALLING, new Potion("slow_falling", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOW_FALLING, 1800)}));
      LONG_SLOW_FALLING = register(PotionIds.LONG_SLOW_FALLING, new Potion("slow_falling", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOW_FALLING, 4800)}));
      WIND_CHARGED = register(PotionIds.WIND_CHARGED, new Potion("wind_charged", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WIND_CHARGED, 3600)}));
      WEAVING = register(PotionIds.WEAVING, new Potion("weaving", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WEAVING, 3600)}));
      OOZING = register(PotionIds.OOZING, new Potion("oozing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.OOZING, 3600)}));
      INFESTED = register(PotionIds.INFESTED, new Potion("infested", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INFESTED, 3600)}));
   }
}
