package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class HugeFungusConfiguration implements FeatureConfiguration {
   public static final Codec<HugeFungusConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockState.CODEC.fieldOf("valid_base_block").forGetter((var0x) -> {
         return var0x.validBaseState;
      }), BlockState.CODEC.fieldOf("stem_state").forGetter((var0x) -> {
         return var0x.stemState;
      }), BlockState.CODEC.fieldOf("hat_state").forGetter((var0x) -> {
         return var0x.hatState;
      }), BlockState.CODEC.fieldOf("decor_state").forGetter((var0x) -> {
         return var0x.decorState;
      }), Codec.BOOL.fieldOf("planted").orElse(false).forGetter((var0x) -> {
         return var0x.planted;
      })).apply(var0, HugeFungusConfiguration::new);
   });
   public static final HugeFungusConfiguration HUGE_CRIMSON_FUNGI_PLANTED_CONFIG;
   public static final HugeFungusConfiguration HUGE_CRIMSON_FUNGI_NOT_PLANTED_CONFIG;
   public static final HugeFungusConfiguration HUGE_WARPED_FUNGI_PLANTED_CONFIG;
   public static final HugeFungusConfiguration HUGE_WARPED_FUNGI_NOT_PLANTED_CONFIG;
   public final BlockState validBaseState;
   public final BlockState stemState;
   public final BlockState hatState;
   public final BlockState decorState;
   public final boolean planted;

   public HugeFungusConfiguration(BlockState var1, BlockState var2, BlockState var3, BlockState var4, boolean var5) {
      super();
      this.validBaseState = var1;
      this.stemState = var2;
      this.hatState = var3;
      this.decorState = var4;
      this.planted = var5;
   }

   static {
      HUGE_CRIMSON_FUNGI_PLANTED_CONFIG = new HugeFungusConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true);
      HUGE_CRIMSON_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfiguration(HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.validBaseState, HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.stemState, HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.hatState, HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.decorState, false);
      HUGE_WARPED_FUNGI_PLANTED_CONFIG = new HugeFungusConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true);
      HUGE_WARPED_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfiguration(HUGE_WARPED_FUNGI_PLANTED_CONFIG.validBaseState, HUGE_WARPED_FUNGI_PLANTED_CONFIG.stemState, HUGE_WARPED_FUNGI_PLANTED_CONFIG.hatState, HUGE_WARPED_FUNGI_PLANTED_CONFIG.decorState, false);
   }
}
