package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Registry;

public class ParticleTypes {
   public static final SimpleParticleType AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect", false);
   public static final SimpleParticleType ANGRY_VILLAGER = register("angry_villager", false);
   public static final ParticleType<BlockParticleOption> BLOCK;
   public static final ParticleType<BlockParticleOption> BLOCK_MARKER;
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
   public static final ParticleType<DustParticleOptions> DUST;
   public static final ParticleType<DustColorTransitionOptions> DUST_COLOR_TRANSITION;
   public static final SimpleParticleType EFFECT;
   public static final SimpleParticleType ELDER_GUARDIAN;
   public static final SimpleParticleType ENCHANTED_HIT;
   public static final SimpleParticleType ENCHANT;
   public static final SimpleParticleType END_ROD;
   public static final SimpleParticleType ENTITY_EFFECT;
   public static final SimpleParticleType EXPLOSION_EMITTER;
   public static final SimpleParticleType EXPLOSION;
   public static final ParticleType<BlockParticleOption> FALLING_DUST;
   public static final SimpleParticleType FIREWORK;
   public static final SimpleParticleType FISHING;
   public static final SimpleParticleType FLAME;
   public static final SimpleParticleType SOUL_FIRE_FLAME;
   public static final SimpleParticleType SOUL;
   public static final SimpleParticleType FLASH;
   public static final SimpleParticleType HAPPY_VILLAGER;
   public static final SimpleParticleType COMPOSTER;
   public static final SimpleParticleType HEART;
   public static final SimpleParticleType INSTANT_EFFECT;
   public static final ParticleType<ItemParticleOption> ITEM;
   public static final ParticleType<VibrationParticleOption> VIBRATION;
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
   public static final SimpleParticleType FALLING_SPORE_BLOSSOM;
   public static final SimpleParticleType ASH;
   public static final SimpleParticleType CRIMSON_SPORE;
   public static final SimpleParticleType WARPED_SPORE;
   public static final SimpleParticleType SPORE_BLOSSOM_AIR;
   public static final SimpleParticleType DRIPPING_OBSIDIAN_TEAR;
   public static final SimpleParticleType FALLING_OBSIDIAN_TEAR;
   public static final SimpleParticleType LANDING_OBSIDIAN_TEAR;
   public static final SimpleParticleType REVERSE_PORTAL;
   public static final SimpleParticleType WHITE_ASH;
   public static final SimpleParticleType SMALL_FLAME;
   public static final SimpleParticleType SNOWFLAKE;
   public static final SimpleParticleType DRIPPING_DRIPSTONE_LAVA;
   public static final SimpleParticleType FALLING_DRIPSTONE_LAVA;
   public static final SimpleParticleType DRIPPING_DRIPSTONE_WATER;
   public static final SimpleParticleType FALLING_DRIPSTONE_WATER;
   public static final SimpleParticleType GLOW_SQUID_INK;
   public static final SimpleParticleType GLOW;
   public static final SimpleParticleType WAX_ON;
   public static final SimpleParticleType WAX_OFF;
   public static final SimpleParticleType ELECTRIC_SPARK;
   public static final SimpleParticleType SCRAPE;
   public static final Codec<ParticleOptions> CODEC;

   public ParticleTypes() {
      super();
   }

   private static SimpleParticleType register(String var0, boolean var1) {
      return (SimpleParticleType)Registry.register(Registry.PARTICLE_TYPE, (String)var0, new SimpleParticleType(var1));
   }

   private static <T extends ParticleOptions> ParticleType<T> register(String var0, ParticleOptions.Deserializer<T> var1, final Function<ParticleType<T>, Codec<T>> var2) {
      return (ParticleType)Registry.register(Registry.PARTICLE_TYPE, (String)var0, new ParticleType<T>(false, var1) {
         public Codec<T> codec() {
            return (Codec)var2.apply(this);
         }
      });
   }

   static {
      BLOCK = register("block", BlockParticleOption.DESERIALIZER, BlockParticleOption::codec);
      BLOCK_MARKER = register("block_marker", BlockParticleOption.DESERIALIZER, BlockParticleOption::codec);
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
      DUST = register("dust", DustParticleOptions.DESERIALIZER, (var0) -> {
         return DustParticleOptions.CODEC;
      });
      DUST_COLOR_TRANSITION = register("dust_color_transition", DustColorTransitionOptions.DESERIALIZER, (var0) -> {
         return DustColorTransitionOptions.CODEC;
      });
      EFFECT = register("effect", false);
      ELDER_GUARDIAN = register("elder_guardian", true);
      ENCHANTED_HIT = register("enchanted_hit", false);
      ENCHANT = register("enchant", false);
      END_ROD = register("end_rod", false);
      ENTITY_EFFECT = register("entity_effect", false);
      EXPLOSION_EMITTER = register("explosion_emitter", true);
      EXPLOSION = register("explosion", true);
      FALLING_DUST = register("falling_dust", BlockParticleOption.DESERIALIZER, BlockParticleOption::codec);
      FIREWORK = register("firework", false);
      FISHING = register("fishing", false);
      FLAME = register("flame", false);
      SOUL_FIRE_FLAME = register("soul_fire_flame", false);
      SOUL = register("soul", false);
      FLASH = register("flash", false);
      HAPPY_VILLAGER = register("happy_villager", false);
      COMPOSTER = register("composter", false);
      HEART = register("heart", false);
      INSTANT_EFFECT = register("instant_effect", false);
      ITEM = register("item", ItemParticleOption.DESERIALIZER, ItemParticleOption::codec);
      VIBRATION = register("vibration", VibrationParticleOption.DESERIALIZER, (var0) -> {
         return VibrationParticleOption.CODEC;
      });
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
      FALLING_SPORE_BLOSSOM = register("falling_spore_blossom", false);
      ASH = register("ash", false);
      CRIMSON_SPORE = register("crimson_spore", false);
      WARPED_SPORE = register("warped_spore", false);
      SPORE_BLOSSOM_AIR = register("spore_blossom_air", false);
      DRIPPING_OBSIDIAN_TEAR = register("dripping_obsidian_tear", false);
      FALLING_OBSIDIAN_TEAR = register("falling_obsidian_tear", false);
      LANDING_OBSIDIAN_TEAR = register("landing_obsidian_tear", false);
      REVERSE_PORTAL = register("reverse_portal", false);
      WHITE_ASH = register("white_ash", false);
      SMALL_FLAME = register("small_flame", false);
      SNOWFLAKE = register("snowflake", false);
      DRIPPING_DRIPSTONE_LAVA = register("dripping_dripstone_lava", false);
      FALLING_DRIPSTONE_LAVA = register("falling_dripstone_lava", false);
      DRIPPING_DRIPSTONE_WATER = register("dripping_dripstone_water", false);
      FALLING_DRIPSTONE_WATER = register("falling_dripstone_water", false);
      GLOW_SQUID_INK = register("glow_squid_ink", true);
      GLOW = register("glow", true);
      WAX_ON = register("wax_on", true);
      WAX_OFF = register("wax_off", true);
      ELECTRIC_SPARK = register("electric_spark", true);
      SCRAPE = register("scrape", true);
      CODEC = Registry.PARTICLE_TYPE.byNameCodec().dispatch("type", ParticleOptions::getType, ParticleType::codec);
   }
}
