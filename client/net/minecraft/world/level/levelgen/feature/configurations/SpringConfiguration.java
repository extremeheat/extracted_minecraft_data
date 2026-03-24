package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;

public class SpringConfiguration implements FeatureConfiguration {
   public static final Codec<SpringConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(FluidState.CODEC.fieldOf("state").forGetter((c) -> c.state), Codec.BOOL.fieldOf("requires_block_below").orElse(true).forGetter((c) -> c.requiresBlockBelow), Codec.INT.fieldOf("rock_count").orElse(4).forGetter((c) -> c.rockCount), Codec.INT.fieldOf("hole_count").orElse(1).forGetter((c) -> c.holeCount), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("valid_blocks").forGetter((c) -> c.validBlocks)).apply(i, SpringConfiguration::new));
   public final FluidState state;
   public final boolean requiresBlockBelow;
   public final int rockCount;
   public final int holeCount;
   public final HolderSet<Block> validBlocks;

   public SpringConfiguration(final FluidState state, final boolean requiresBlockBelow, final int rockCount, final int holeCount, final HolderSet<Block> validBlocks) {
      super();
      this.state = state;
      this.requiresBlockBelow = requiresBlockBelow;
      this.rockCount = rockCount;
      this.holeCount = holeCount;
      this.validBlocks = validBlocks;
   }
}
