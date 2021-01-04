package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class NopProcessor extends StructureProcessor {
   public static final NopProcessor INSTANCE = new NopProcessor();

   private NopProcessor() {
      super();
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings var5) {
      return var4;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.NOP;
   }

   protected <T> Dynamic<T> getDynamic(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.emptyMap());
   }
}
