package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

public class LavaSubmergedBlockProcessor implements StructureProcessor {
   public static final MapCodec<LavaSubmergedBlockProcessor> MAP_CODEC = MapCodec.unit(() -> INSTANCE);
   public static final LavaSubmergedBlockProcessor INSTANCE = new LavaSubmergedBlockProcessor();

   public LavaSubmergedBlockProcessor() {
      super();
   }

   public StructureTemplate.@Nullable StructureBlockInfo processBlock(final LevelReader level, final BlockPos targetPosition, final BlockPos referencePos, final StructureTemplate.StructureBlockInfo originalBlockInfo, final StructureTemplate.StructureBlockInfo processedBlockInfo, final StructurePlaceSettings settings) {
      BlockPos pos = processedBlockInfo.pos();
      boolean wasLavaBefore = level.getBlockState(pos).is(Blocks.LAVA);
      return wasLavaBefore && !Block.isShapeFullBlock(processedBlockInfo.state().getShape(level, pos)) ? new StructureTemplate.StructureBlockInfo(pos, Blocks.LAVA.defaultBlockState(), processedBlockInfo.nbt()) : processedBlockInfo;
   }

   public MapCodec<LavaSubmergedBlockProcessor> codec() {
      return MAP_CODEC;
   }
}
