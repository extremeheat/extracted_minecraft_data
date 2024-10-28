package net.minecraft.world.item.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Potions {
   public static final Holder<Potion> WATER = register("water", new Potion(new MobEffectInstance[0]));
   public static final Holder<Potion> MUNDANE = register("mundane", new Potion(new MobEffectInstance[0]));
   public static final Holder<Potion> THICK = register("thick", new Potion(new MobEffectInstance[0]));
   public static final Holder<Potion> AWKWARD = register("awkward", new Potion(new MobEffectInstance[0]));
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

   private static Holder<Potion> register(String var0, Potion var1) {
      return Registry.registerForHolder(BuiltInRegistries.POTION, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), var1);
   }

   public static Holder<Potion> bootstrap(Registry<Potion> var0) {
      return WATER;
   }

   static {
      NIGHT_VISION = register("night_vision", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.NIGHT_VISION, 3600)}));
      LONG_NIGHT_VISION = register("long_night_vision", new Potion("night_vision", new MobEffectInstance[]{new MobEffectInstance(MobEffects.NIGHT_VISION, 9600)}));
      INVISIBILITY = register("invisibility", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.INVISIBILITY, 3600)}));
      LONG_INVISIBILITY = register("long_invisibility", new Potion("invisibility", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INVISIBILITY, 9600)}));
      LEAPING = register("leaping", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.JUMP, 3600)}));
      LONG_LEAPING = register("long_leaping", new Potion("leaping", new MobEffectInstance[]{new MobEffectInstance(MobEffects.JUMP, 9600)}));
      STRONG_LEAPING = register("strong_leaping", new Potion("leaping", new MobEffectInstance[]{new MobEffectInstance(MobEffects.JUMP, 1800, 1)}));
      FIRE_RESISTANCE = register("fire_resistance", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600)}));
      LONG_FIRE_RESISTANCE = register("long_fire_resistance", new Potion("fire_resistance", new MobEffectInstance[]{new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600)}));
      SWIFTNESS = register("swiftness", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600)}));
      LONG_SWIFTNESS = register("long_swiftness", new Potion("swiftness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 9600)}));
      STRONG_SWIFTNESS = register("strong_swiftness", new Potion("swiftness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1800, 1)}));
      SLOWNESS = register("slowness", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800)}));
      LONG_SLOWNESS = register("long_slowness", new Potion("slowness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 4800)}));
      STRONG_SLOWNESS = register("strong_slowness", new Potion("slowness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 3)}));
      TURTLE_MASTER = register("turtle_master", new Potion("turtle_master", new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 3), new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 2)}));
      LONG_TURTLE_MASTER = register("long_turtle_master", new Potion("turtle_master", new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 800, 3), new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 800, 2)}));
      STRONG_TURTLE_MASTER = register("strong_turtle_master", new Potion("turtle_master", new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 5), new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 3)}));
      WATER_BREATHING = register("water_breathing", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.WATER_BREATHING, 3600)}));
      LONG_WATER_BREATHING = register("long_water_breathing", new Potion("water_breathing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WATER_BREATHING, 9600)}));
      HEALING = register("healing", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.HEAL, 1)}));
      STRONG_HEALING = register("strong_healing", new Potion("healing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.HEAL, 1, 1)}));
      HARMING = register("harming", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.HARM, 1)}));
      STRONG_HARMING = register("strong_harming", new Potion("harming", new MobEffectInstance[]{new MobEffectInstance(MobEffects.HARM, 1, 1)}));
      POISON = register("poison", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 900)}));
      LONG_POISON = register("long_poison", new Potion("poison", new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 1800)}));
      STRONG_POISON = register("strong_poison", new Potion("poison", new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 432, 1)}));
      REGENERATION = register("regeneration", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.REGENERATION, 900)}));
      LONG_REGENERATION = register("long_regeneration", new Potion("regeneration", new MobEffectInstance[]{new MobEffectInstance(MobEffects.REGENERATION, 1800)}));
      STRONG_REGENERATION = register("strong_regeneration", new Potion("regeneration", new MobEffectInstance[]{new MobEffectInstance(MobEffects.REGENERATION, 450, 1)}));
      STRENGTH = register("strength", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600)}));
      LONG_STRENGTH = register("long_strength", new Potion("strength", new MobEffectInstance[]{new MobEffectInstance(MobEffects.DAMAGE_BOOST, 9600)}));
      STRONG_STRENGTH = register("strong_strength", new Potion("strength", new MobEffectInstance[]{new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800, 1)}));
      WEAKNESS = register("weakness", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.WEAKNESS, 1800)}));
      LONG_WEAKNESS = register("long_weakness", new Potion("weakness", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WEAKNESS, 4800)}));
      LUCK = register("luck", new Potion("luck", new MobEffectInstance[]{new MobEffectInstance(MobEffects.LUCK, 6000)}));
      SLOW_FALLING = register("slow_falling", new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOW_FALLING, 1800)}));
      LONG_SLOW_FALLING = register("long_slow_falling", new Potion("slow_falling", new MobEffectInstance[]{new MobEffectInstance(MobEffects.SLOW_FALLING, 4800)}));
      WIND_CHARGED = register("wind_charged", new Potion("wind_charged", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WIND_CHARGED, 3600)}));
      WEAVING = register("weaving", new Potion("weaving", new MobEffectInstance[]{new MobEffectInstance(MobEffects.WEAVING, 3600)}));
      OOZING = register("oozing", new Potion("oozing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.OOZING, 3600)}));
      INFESTED = register("infested", new Potion("infested", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INFESTED, 3600)}));
   }
}
