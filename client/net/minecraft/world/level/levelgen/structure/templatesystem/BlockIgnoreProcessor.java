package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockIgnoreProcessor extends StructureProcessor {
   public static final MapCodec<BlockIgnoreProcessor> CODEC;
   public static final BlockIgnoreProcessor STRUCTURE_BLOCK;
   public static final BlockIgnoreProcessor AIR;
   public static final BlockIgnoreProcessor STRUCTURE_AND_AIR;
   private final ImmutableList<Block> toIgnore;

   public BlockIgnoreProcessor(final List<Block> toIgnore) {
      super();
      this.toIgnore = ImmutableList.copyOf(toIgnore);
   }

   public StructureTemplate.@Nullable StructureBlockInfo processBlock(final LevelReader level, final BlockPos targetPosition, final BlockPos referencePos, final StructureTemplate.StructureBlockInfo originalBlockInfo, final StructureTemplate.StructureBlockInfo processedBlockInfo, final StructurePlaceSettings settings) {
      return this.toIgnore.contains(processedBlockInfo.state().getBlock()) ? null : processedBlockInfo;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLOCK_IGNORE;
   }

   static {
      CODEC = BlockState.CODEC.xmap(BlockBehaviour.BlockStateBase::getBlock, Block::defaultBlockState).listOf().fieldOf("blocks").xmap(BlockIgnoreProcessor::new, (p) -> p.toIgnore);
      STRUCTURE_BLOCK = new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
      AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR));
      STRUCTURE_AND_AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
   }
}
