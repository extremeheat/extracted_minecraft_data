package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SulfurSpikeBlock extends SpeleothemBlock {
   public static final MapCodec<SulfurSpikeBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockState.CODEC.fieldOf("block_to_grow_on").forGetter((b) -> b.blockToGrowOn), propertiesCodec()).apply(i, SulfurSpikeBlock::new));

   public MapCodec<SulfurSpikeBlock> codec() {
      return CODEC;
   }

   public SulfurSpikeBlock(final BlockState blockToGrowOn, final BlockBehaviour.Properties properties) {
      super(blockToGrowOn, properties);
   }
}
