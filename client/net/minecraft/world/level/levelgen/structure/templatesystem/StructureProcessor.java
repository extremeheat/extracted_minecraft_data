package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

public abstract class StructureProcessor {
   public StructureProcessor() {
      super();
   }

   @Nullable
   public abstract StructureTemplate.StructureBlockInfo processBlock(
      LevelReader var1,
      BlockPos var2,
      BlockPos var3,
      StructureTemplate.StructureBlockInfo var4,
      StructureTemplate.StructureBlockInfo var5,
      StructurePlaceSettings var6
   );

   protected abstract StructureProcessorType<?> getType();

   public void finalizeStructure(
      LevelAccessor var1, BlockPos var2, BlockPos var3, StructurePlaceSettings var4, List<StructureTemplate.StructureBlockInfo> var5
   ) {
   }
}
