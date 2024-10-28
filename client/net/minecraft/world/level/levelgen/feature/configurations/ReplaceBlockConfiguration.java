package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;

public class ReplaceBlockConfiguration implements FeatureConfiguration {
   public static final Codec<ReplaceBlockConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets").forGetter((var0x) -> {
         return var0x.targetStates;
      })).apply(var0, ReplaceBlockConfiguration::new);
   });
   public final List<OreConfiguration.TargetBlockState> targetStates;

   public ReplaceBlockConfiguration(BlockState var1, BlockState var2) {
      this(ImmutableList.of(OreConfiguration.target(new BlockStateMatchTest(var1), var2)));
   }

   public ReplaceBlockConfiguration(List<OreConfiguration.TargetBlockState> var1) {
      super();
      this.targetStates = var1;
   }
}
