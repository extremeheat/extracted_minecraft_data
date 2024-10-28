package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;

public class SpringConfiguration implements FeatureConfiguration {
   public static final Codec<SpringConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(FluidState.CODEC.fieldOf("state").forGetter((var0x) -> {
         return var0x.state;
      }), Codec.BOOL.fieldOf("requires_block_below").orElse(true).forGetter((var0x) -> {
         return var0x.requiresBlockBelow;
      }), Codec.INT.fieldOf("rock_count").orElse(4).forGetter((var0x) -> {
         return var0x.rockCount;
      }), Codec.INT.fieldOf("hole_count").orElse(1).forGetter((var0x) -> {
         return var0x.holeCount;
      }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("valid_blocks").forGetter((var0x) -> {
         return var0x.validBlocks;
      })).apply(var0, SpringConfiguration::new);
   });
   public final FluidState state;
   public final boolean requiresBlockBelow;
   public final int rockCount;
   public final int holeCount;
   public final HolderSet<Block> validBlocks;

   public SpringConfiguration(FluidState var1, boolean var2, int var3, int var4, HolderSet<Block> var5) {
      super();
      this.state = var1;
      this.requiresBlockBelow = var2;
      this.rockCount = var3;
      this.holeCount = var4;
      this.validBlocks = var5;
   }
}
