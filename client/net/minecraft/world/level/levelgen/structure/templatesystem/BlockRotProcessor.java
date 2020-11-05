package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class BlockRotProcessor extends StructureProcessor {
   public static final Codec<BlockRotProcessor> CODEC;
   private final float integrity;

   public BlockRotProcessor(float var1) {
      super();
      this.integrity = var1;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      Random var7 = var6.getRandom(var5.pos);
      return this.integrity < 1.0F && var7.nextFloat() > this.integrity ? null : var5;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLOCK_ROT;
   }

   static {
      CODEC = Codec.FLOAT.fieldOf("integrity").orElse(1.0F).xmap(BlockRotProcessor::new, (var0) -> {
         return var0.integrity;
      }).codec();
   }
}
