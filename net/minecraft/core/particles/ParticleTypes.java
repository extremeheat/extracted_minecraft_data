package net.minecraft.core.particles;

import net.minecraft.core.Registry;

public class ParticleTypes {
   public static final SimpleParticleType AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect", false);
   public static final SimpleParticleType ANGRY_VILLAGER = register("angry_villager", false);
   public static final SimpleParticleType BARRIER = register("barrier", false);
   public static final ParticleType BLOCK;
   public static final SimpleParticleType BUBBLE;
   public static final SimpleParticleType CLOUD;
   public static final SimpleParticleType CRIT;
   public static final SimpleParticleType DAMAGE_INDICATOR;
   public static final SimpleParticleType DRAGON_BREATH;
   public static final SimpleParticleType DRIPPING_LAVA;
   public static final SimpleParticleType FALLING_LAVA;
   public static final SimpleParticleType LANDING_LAVA;
   public static final SimpleParticleType DRIPPING_WATER;
   public static final SimpleParticleType FALLING_WATER;
   public static final ParticleType DUST;
   public static final SimpleParticleType EFFECT;
   public static final SimpleParticleType ELDER_GUARDIAN;
   public static final SimpleParticleType ENCHANTED_HIT;
   public static final SimpleParticleType ENCHANT;
   public static final SimpleParticleType END_ROD;
   public static final SimpleParticleType ENTITY_EFFECT;
   public static final SimpleParticleType EXPLOSION_EMITTER;
   public static final SimpleParticleType EXPLOSION;
   public static final ParticleType FALLING_DUST;
   public static final SimpleParticleType FIREWORK;
   public static final SimpleParticleType FISHING;
   public static final SimpleParticleType FLAME;
   public static final SimpleParticleType FLASH;
   public static final SimpleParticleType HAPPY_VILLAGER;
   public static final SimpleParticleType COMPOSTER;
   public static final SimpleParticleType HEART;
   public static final SimpleParticleType INSTANT_EFFECT;
   public static final ParticleType ITEM;
   public static final SimpleParticleType ITEM_SLIME;
   public static final SimpleParticleType ITEM_SNOWBALL;
   public static final SimpleParticleType LARGE_SMOKE;
   public static final SimpleParticleType LAVA;
   public static final SimpleParticleType MYCELIUM;
   public static final SimpleParticleType NOTE;
   public static final SimpleParticleType POOF;
   public static final SimpleParticleType PORTAL;
   public static final SimpleParticleType RAIN;
   public static final SimpleParticleType SMOKE;
   public static final SimpleParticleType SNEEZE;
   public static final SimpleParticleType SPIT;
   public static final SimpleParticleType SQUID_INK;
   public static final SimpleParticleType SWEEP_ATTACK;
   public static final SimpleParticleType TOTEM_OF_UNDYING;
   public static final SimpleParticleType UNDERWATER;
   public static final SimpleParticleType SPLASH;
   public static final SimpleParticleType WITCH;
   public static final SimpleParticleType BUBBLE_POP;
   public static final SimpleParticleType CURRENT_DOWN;
   public static final SimpleParticleType BUBBLE_COLUMN_UP;
   public static final SimpleParticleType NAUTILUS;
   public static final SimpleParticleType DOLPHIN;
   public static final SimpleParticleType CAMPFIRE_COSY_SMOKE;
   public static final SimpleParticleType CAMPFIRE_SIGNAL_SMOKE;
   public static final SimpleParticleType DRIPPING_HONEY;
   public static final SimpleParticleType FALLING_HONEY;
   public static final SimpleParticleType LANDING_HONEY;
   public static final SimpleParticleType FALLING_NECTAR;

   private static SimpleParticleType register(String var0, boolean var1) {
      return (SimpleParticleType)Registry.register(Registry.PARTICLE_TYPE, (String)var0, new SimpleParticleType(var1));
   }

   private static ParticleType register(String var0, ParticleOptions.Deserializer var1) {
      return (ParticleType)Registry.register(Registry.PARTICLE_TYPE, (String)var0, new ParticleType(false, var1));
   }

   static {
      BLOCK = register("block", BlockParticleOption.DESERIALIZER);
      BUBBLE = register("bubble", false);
      CLOUD = register("cloud", false);
      CRIT = register("crit", false);
      DAMAGE_INDICATOR = register("damage_indicator", true);
      DRAGON_BREATH = register("dragon_breath", false);
      DRIPPING_LAVA = register("dripping_lava", false);
      FALLING_LAVA = register("falling_lava", false);
      LANDING_LAVA = register("landing_lava", false);
      DRIPPING_WATER = register("dripping_water", false);
      FALLING_WATER = register("falling_water", false);
      DUST = register("dust", DustParticleOptions.DESERIALIZER);
      EFFECT = register("effect", false);
      ELDER_GUARDIAN = register("elder_guardian", true);
      ENCHANTED_HIT = register("enchanted_hit", false);
      ENCHANT = register("enchant", false);
      END_ROD = register("end_rod", false);
      ENTITY_EFFECT = register("entity_effect", false);
      EXPLOSION_EMITTER = register("explosion_emitter", true);
      EXPLOSION = register("explosion", true);
      FALLING_DUST = register("falling_dust", BlockParticleOption.DESERIALIZER);
      FIREWORK = register("firework", false);
      FISHING = register("fishing", false);
      FLAME = register("flame", false);
      FLASH = register("flash", false);
      HAPPY_VILLAGER = register("happy_villager", false);
      COMPOSTER = register("composter", false);
      HEART = register("heart", false);
      INSTANT_EFFECT = register("instant_effect", false);
      ITEM = register("item", ItemParticleOption.DESERIALIZER);
      ITEM_SLIME = register("item_slime", false);
      ITEM_SNOWBALL = register("item_snowball", false);
      LARGE_SMOKE = register("large_smoke", false);
      LAVA = register("lava", false);
      MYCELIUM = register("mycelium", false);
      NOTE = register("note", false);
      POOF = register("poof", true);
      PORTAL = register("portal", false);
      RAIN = register("rain", false);
      SMOKE = register("smoke", false);
      SNEEZE = register("sneeze", false);
      SPIT = register("spit", true);
      SQUID_INK = register("squid_ink", true);
      SWEEP_ATTACK = register("sweep_attack", true);
      TOTEM_OF_UNDYING = register("totem_of_undying", false);
      UNDERWATER = register("underwater", false);
      SPLASH = register("splash", false);
      WITCH = register("witch", false);
      BUBBLE_POP = register("bubble_pop", false);
      CURRENT_DOWN = register("current_down", false);
      BUBBLE_COLUMN_UP = register("bubble_column_up", false);
      NAUTILUS = register("nautilus", false);
      DOLPHIN = register("dolphin", false);
      CAMPFIRE_COSY_SMOKE = register("campfire_cosy_smoke", true);
      CAMPFIRE_SIGNAL_SMOKE = register("campfire_signal_smoke", true);
      DRIPPING_HONEY = register("dripping_honey", false);
      FALLING_HONEY = register("falling_honey", false);
      LANDING_HONEY = register("landing_honey", false);
      FALLING_NECTAR = register("falling_nectar", false);
   }
}
