package net.minecraft.world.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

public class Foods {
   public static final FoodProperties APPLE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.3F).build();
   public static final FoodProperties BAKED_POTATO = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.6F).build();
   public static final FoodProperties BEEF = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.3F).build();
   public static final FoodProperties BEETROOT = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.6F).build();
   public static final FoodProperties BEETROOT_SOUP = stew(6).build();
   public static final FoodProperties BREAD = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.6F).build();
   public static final FoodProperties CARROT = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.6F).build();
   public static final FoodProperties CHICKEN;
   public static final FoodProperties CHORUS_FRUIT;
   public static final FoodProperties COD;
   public static final FoodProperties COOKED_BEEF;
   public static final FoodProperties COOKED_CHICKEN;
   public static final FoodProperties COOKED_COD;
   public static final FoodProperties COOKED_MUTTON;
   public static final FoodProperties COOKED_PORKCHOP;
   public static final FoodProperties COOKED_RABBIT;
   public static final FoodProperties COOKED_SALMON;
   public static final FoodProperties COOKIE;
   public static final FoodProperties DRIED_KELP;
   public static final FoodProperties ENCHANTED_GOLDEN_APPLE;
   public static final FoodProperties GOLDEN_APPLE;
   public static final FoodProperties GOLDEN_CARROT;
   public static final FoodProperties HONEY_BOTTLE;
   public static final FoodProperties MELON_SLICE;
   public static final FoodProperties MUSHROOM_STEW;
   public static final FoodProperties MUTTON;
   public static final FoodProperties POISONOUS_POTATO;
   public static final FoodProperties PORKCHOP;
   public static final FoodProperties POTATO;
   public static final FoodProperties PUFFERFISH;
   public static final FoodProperties PUMPKIN_PIE;
   public static final FoodProperties RABBIT;
   public static final FoodProperties RABBIT_STEW;
   public static final FoodProperties ROTTEN_FLESH;
   public static final FoodProperties SALMON;
   public static final FoodProperties SPIDER_EYE;
   public static final FoodProperties SUSPICIOUS_STEW;
   public static final FoodProperties SWEET_BERRIES;
   public static final FoodProperties GLOW_BERRIES;
   public static final FoodProperties TROPICAL_FISH;
   public static final FoodProperties OMINOUS_BOTTLE;

   public Foods() {
      super();
   }

   private static FoodProperties.Builder stew(int var0) {
      return (new FoodProperties.Builder()).nutrition(var0).saturationModifier(0.6F).usingConvertsTo(Items.BOWL);
   }

   static {
      CHICKEN = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F).build();
      CHORUS_FRUIT = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.3F).alwaysEdible().build();
      COD = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).build();
      COOKED_BEEF = (new FoodProperties.Builder()).nutrition(8).saturationModifier(0.8F).build();
      COOKED_CHICKEN = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.6F).build();
      COOKED_COD = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.6F).build();
      COOKED_MUTTON = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.8F).build();
      COOKED_PORKCHOP = (new FoodProperties.Builder()).nutrition(8).saturationModifier(0.8F).build();
      COOKED_RABBIT = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.6F).build();
      COOKED_SALMON = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.8F).build();
      COOKIE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).build();
      DRIED_KELP = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).fast().build();
      ENCHANTED_GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(1.2F).effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0F).effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F).effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0F).alwaysEdible().build();
      GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(1.2F).effect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0), 1.0F).alwaysEdible().build();
      GOLDEN_CARROT = (new FoodProperties.Builder()).nutrition(6).saturationModifier(1.2F).build();
      HONEY_BOTTLE = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.1F).build();
      MELON_SLICE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).build();
      MUSHROOM_STEW = stew(6).build();
      MUTTON = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).build();
      POISONOUS_POTATO = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 0.6F).build();
      PORKCHOP = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.3F).build();
      POTATO = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).build();
      PUFFERFISH = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.1F).effect(new MobEffectInstance(MobEffects.POISON, 1200, 1), 1.0F).effect(new MobEffectInstance(MobEffects.HUNGER, 300, 2), 1.0F).effect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 1.0F).build();
      PUMPKIN_PIE = (new FoodProperties.Builder()).nutrition(8).saturationModifier(0.3F).build();
      RABBIT = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.3F).build();
      RABBIT_STEW = stew(10).build();
      ROTTEN_FLESH = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.1F).effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8F).build();
      SALMON = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).build();
      SPIDER_EYE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.8F).effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 1.0F).build();
      SUSPICIOUS_STEW = stew(6).alwaysEdible().build();
      SWEET_BERRIES = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).build();
      GLOW_BERRIES = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).build();
      TROPICAL_FISH = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.1F).build();
      OMINOUS_BOTTLE = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.1F).build();
   }
}
