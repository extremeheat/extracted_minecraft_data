package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LavaSubmergedBlockProcessor extends StructureProcessor {
   public static final Codec<LavaSubmergedBlockProcessor> CODEC = Codec.unit(() -> LavaSubmergedBlockProcessor.INSTANCE);
   public static final LavaSubmergedBlockProcessor INSTANCE = new LavaSubmergedBlockProcessor();

   public LavaSubmergedBlockProcessor() {
      super();
   }

   @Nullable
   @Override
   public StructureTemplate.StructureBlockInfo processBlock(
      LevelReader var1,
      BlockPos var2,
      BlockPos var3,
      StructureTemplate.StructureBlockInfo var4,
      StructureTemplate.StructureBlockInfo var5,
      StructurePlaceSettings var6
   ) {
      BlockPos var7 = var5.pos;
      boolean var8 = var1.getBlockState(var7).is(Blocks.LAVA);
      return var8 && !Block.isShapeFullBlock(var5.state.getShape(var1, var7))
         ? new StructureTemplate.StructureBlockInfo(var7, Blocks.LAVA.defaultBlockState(), var5.nbt)
         : var5;
   }

   @Override
   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
   }
}
