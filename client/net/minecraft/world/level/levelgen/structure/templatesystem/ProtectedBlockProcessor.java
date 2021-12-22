package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.Feature;

public class ProtectedBlockProcessor extends StructureProcessor {
   public final ResourceLocation cannotReplace;
   public static final Codec<ProtectedBlockProcessor> CODEC;

   public ProtectedBlockProcessor(ResourceLocation var1) {
      super();
      this.cannotReplace = var1;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      return Feature.isReplaceable(this.cannotReplace).test(var1.getBlockState(var5.pos)) ? var5 : null;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.PROTECTED_BLOCKS;
   }

   static {
      CODEC = ResourceLocation.CODEC.xmap(ProtectedBlockProcessor::new, (var0) -> {
         return var0.cannotReplace;
      });
   }
}
