package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class NopProcessor extends StructureProcessor {
   public static final Codec<NopProcessor> CODEC = Codec.unit(() -> NopProcessor.INSTANCE);
   public static final NopProcessor INSTANCE = new NopProcessor();

   private NopProcessor() {
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
      return var5;
   }

   @Override
   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.NOP;
   }
}
