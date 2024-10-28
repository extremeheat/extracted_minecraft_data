package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ParticleTypes {
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
   public static final ParticleType<ColorParticleOption> ENTITY_EFFECT;
   public static final SimpleParticleType EXPLOSION_EMITTER;
   public static final SimpleParticleType EXPLOSION;
   public static final SimpleParticleType GUST;
   public static final SimpleParticleType SMALL_GUST;
   public static final SimpleParticleType GUST_EMITTER_LARGE;
   public static final SimpleParticleType GUST_EMITTER_SMALL;
   public static final SimpleParticleType SONIC_BOOM;
   public static final ParticleType<BlockParticleOption> FALLING_DUST;
   public static final SimpleParticleType FIREWORK;
   public static final SimpleParticleType FISHING;
   public static final SimpleParticleType FLAME;
   public static final SimpleParticleType INFESTED;
   public static final SimpleParticleType CHERRY_LEAVES;
   public static final SimpleParticleType SCULK_SOUL;
   public static final ParticleType<SculkChargeParticleOptions> SCULK_CHARGE;
   public static final SimpleParticleType SCULK_CHARGE_POP;
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
   public static final SimpleParticleType ITEM_COBWEB;
   public static final SimpleParticleType ITEM_SNOWBALL;
   public static final SimpleParticleType LARGE_SMOKE;
   public static final SimpleParticleType LAVA;
   public static final SimpleParticleType MYCELIUM;
   public static final SimpleParticleType NOTE;
   public static final SimpleParticleType POOF;
   public static final SimpleParticleType PORTAL;
   public static final SimpleParticleType RAIN;
   public static final SimpleParticleType SMOKE;
   public static final SimpleParticleType WHITE_SMOKE;
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
   public static final ParticleType<ShriekParticleOption> SHRIEK;
   public static final SimpleParticleType EGG_CRACK;
   public static final SimpleParticleType DUST_PLUME;
   public static final SimpleParticleType TRIAL_SPAWNER_DETECTED_PLAYER;
   public static final SimpleParticleType TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS;
   public static final SimpleParticleType VAULT_CONNECTION;
   public static final ParticleType<BlockParticleOption> DUST_PILLAR;
   public static final SimpleParticleType OMINOUS_SPAWNING;
   public static final SimpleParticleType RAID_OMEN;
   public static final SimpleParticleType TRIAL_OMEN;
   public static final Codec<ParticleOptions> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOptions> STREAM_CODEC;

   public ParticleTypes() {
      super();
   }

   private static SimpleParticleType register(String var0, boolean var1) {
      return (SimpleParticleType)Registry.register(BuiltInRegistries.PARTICLE_TYPE, (String)var0, new SimpleParticleType(var1));
   }

   private static <T extends ParticleOptions> ParticleType<T> register(String var0, boolean var1, ParticleOptions.Deserializer<T> var2, final Function<ParticleType<T>, MapCodec<T>> var3, final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> var4) {
      return (ParticleType)Registry.register(BuiltInRegistries.PARTICLE_TYPE, (String)var0, new ParticleType<T>(var1, var2) {
         public MapCodec<T> codec() {
            return (MapCodec)var3.apply(this);
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
            return (StreamCodec)var4.apply(this);
         }
      });
   }

   static {
      BLOCK = register("block", false, BlockParticleOption.DESERIALIZER, BlockParticleOption::codec, BlockParticleOption::streamCodec);
      BLOCK_MARKER = register("block_marker", true, BlockParticleOption.DESERIALIZER, BlockParticleOption::codec, BlockParticleOption::streamCodec);
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
      DUST = register("dust", false, DustParticleOptions.DESERIALIZER, (var0) -> {
         return DustParticleOptions.CODEC;
      }, (var0) -> {
         return DustParticleOptions.STREAM_CODEC;
      });
      DUST_COLOR_TRANSITION = register("dust_color_transition", false, DustColorTransitionOptions.DESERIALIZER, (var0) -> {
         return DustColorTransitionOptions.CODEC;
      }, (var0) -> {
         return DustColorTransitionOptions.STREAM_CODEC;
      });
      EFFECT = register("effect", false);
      ELDER_GUARDIAN = register("elder_guardian", true);
      ENCHANTED_HIT = register("enchanted_hit", false);
      ENCHANT = register("enchant", false);
      END_ROD = register("end_rod", false);
      ENTITY_EFFECT = register("entity_effect", false, ColorParticleOption.DESERIALIZER, ColorParticleOption::codec, ColorParticleOption::streamCodec);
      EXPLOSION_EMITTER = register("explosion_emitter", true);
      EXPLOSION = register("explosion", true);
      GUST = register("gust", true);
      SMALL_GUST = register("small_gust", false);
      GUST_EMITTER_LARGE = register("gust_emitter_large", true);
      GUST_EMITTER_SMALL = register("gust_emitter_small", true);
      SONIC_BOOM = register("sonic_boom", true);
      FALLING_DUST = register("falling_dust", false, BlockParticleOption.DESERIALIZER, BlockParticleOption::codec, BlockParticleOption::streamCodec);
      FIREWORK = register("firework", false);
      FISHING = register("fishing", false);
      FLAME = register("flame", false);
      INFESTED = register("infested", false);
      CHERRY_LEAVES = register("cherry_leaves", false);
      SCULK_SOUL = register("sculk_soul", false);
      SCULK_CHARGE = register("sculk_charge", true, SculkChargeParticleOptions.DESERIALIZER, (var0) -> {
         return SculkChargeParticleOptions.CODEC;
      }, (var0) -> {
         return SculkChargeParticleOptions.STREAM_CODEC;
      });
      SCULK_CHARGE_POP = register("sculk_charge_pop", true);
      SOUL_FIRE_FLAME = register("soul_fire_flame", false);
      SOUL = register("soul", false);
      FLASH = register("flash", false);
      HAPPY_VILLAGER = register("happy_villager", false);
      COMPOSTER = register("composter", false);
      HEART = register("heart", false);
      INSTANT_EFFECT = register("instant_effect", false);
      ITEM = register("item", false, ItemParticleOption.DESERIALIZER, ItemParticleOption::codec, ItemParticleOption::streamCodec);
      VIBRATION = register("vibration", true, VibrationParticleOption.DESERIALIZER, (var0) -> {
         return VibrationParticleOption.CODEC;
      }, (var0) -> {
         return VibrationParticleOption.STREAM_CODEC;
      });
      ITEM_SLIME = register("item_slime", false);
      ITEM_COBWEB = register("item_cobweb", false);
      ITEM_SNOWBALL = register("item_snowball", false);
      LARGE_SMOKE = register("large_smoke", false);
      LAVA = register("lava", false);
      MYCELIUM = register("mycelium", false);
      NOTE = register("note", false);
      POOF = register("poof", true);
      PORTAL = register("portal", false);
      RAIN = register("rain", false);
      SMOKE = register("smoke", false);
      WHITE_SMOKE = register("white_smoke", false);
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
      SHRIEK = register("shriek", false, ShriekParticleOption.DESERIALIZER, (var0) -> {
         return ShriekParticleOption.CODEC;
      }, (var0) -> {
         return ShriekParticleOption.STREAM_CODEC;
      });
      EGG_CRACK = register("egg_crack", false);
      DUST_PLUME = register("dust_plume", false);
      TRIAL_SPAWNER_DETECTED_PLAYER = register("trial_spawner_detection", true);
      TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS = register("trial_spawner_detection_ominous", true);
      VAULT_CONNECTION = register("vault_connection", true);
      DUST_PILLAR = register("dust_pillar", true, BlockParticleOption.DESERIALIZER, BlockParticleOption::codec, BlockParticleOption::streamCodec);
      OMINOUS_SPAWNING = register("ominous_spawning", true);
      RAID_OMEN = register("raid_omen", false);
      TRIAL_OMEN = register("trial_omen", false);
      CODEC = BuiltInRegistries.PARTICLE_TYPE.byNameCodec().dispatch("type", ParticleOptions::getType, ParticleType::codec);
      STREAM_CODEC = ByteBufCodecs.registry(Registries.PARTICLE_TYPE).dispatch(ParticleOptions::getType, ParticleType::streamCodec);
   }
}
