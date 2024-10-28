package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockIgnoreProcessor extends StructureProcessor {
   public static final MapCodec<BlockIgnoreProcessor> CODEC;
   public static final BlockIgnoreProcessor STRUCTURE_BLOCK;
   public static final BlockIgnoreProcessor AIR;
   public static final BlockIgnoreProcessor STRUCTURE_AND_AIR;
   private final ImmutableList<Block> toIgnore;

   public BlockIgnoreProcessor(List<Block> var1) {
      super();
      this.toIgnore = ImmutableList.copyOf(var1);
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      return this.toIgnore.contains(var5.state().getBlock()) ? null : var5;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLOCK_IGNORE;
   }

   static {
      CODEC = BlockState.CODEC.xmap(BlockBehaviour.BlockStateBase::getBlock, Block::defaultBlockState).listOf().fieldOf("blocks").xmap(BlockIgnoreProcessor::new, (var0) -> {
         return var0.toIgnore;
      });
      STRUCTURE_BLOCK = new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
      AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR));
      STRUCTURE_AND_AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
   }
}
