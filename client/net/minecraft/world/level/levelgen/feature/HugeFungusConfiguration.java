package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
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
      }), BlockPredicate.CODEC.fieldOf("replaceable_blocks").forGetter((var0x) -> {
         return var0x.replaceableBlocks;
      }), Codec.BOOL.fieldOf("planted").orElse(false).forGetter((var0x) -> {
         return var0x.planted;
      })).apply(var0, HugeFungusConfiguration::new);
   });
   public final BlockState validBaseState;
   public final BlockState stemState;
   public final BlockState hatState;
   public final BlockState decorState;
   public final BlockPredicate replaceableBlocks;
   public final boolean planted;

   public HugeFungusConfiguration(BlockState var1, BlockState var2, BlockState var3, BlockState var4, BlockPredicate var5, boolean var6) {
      super();
      this.validBaseState = var1;
      this.stemState = var2;
      this.hatState = var3;
      this.decorState = var4;
      this.replaceableBlocks = var5;
      this.planted = var6;
   }
}
