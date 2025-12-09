package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

public abstract class StructureProcessor {
   public StructureProcessor() {
      super();
   }

   public StructureTemplate.@Nullable StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      return var5;
   }

   protected abstract StructureProcessorType<?> getType();

   public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor var1, BlockPos var2, BlockPos var3, List<StructureTemplate.StructureBlockInfo> var4, List<StructureTemplate.StructureBlockInfo> var5, StructurePlaceSettings var6) {
      return var5;
   }
}
