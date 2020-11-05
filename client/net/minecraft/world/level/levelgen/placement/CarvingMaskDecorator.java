package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class CarvingMaskDecorator extends FeatureDecorator<CarvingMaskDecoratorConfiguration> {
   public CarvingMaskDecorator(Codec<CarvingMaskDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, CarvingMaskDecoratorConfiguration var3, BlockPos var4) {
      ChunkPos var5 = new ChunkPos(var4);
      BitSet var6 = var1.getCarvingMask(var5, var3.step);
      return IntStream.range(0, var6.length()).filter((var3x) -> {
         return var6.get(var3x) && var2.nextFloat() < var3.probability;
      }).mapToObj((var1x) -> {
         int var2 = var1x & 15;
         int var3 = var1x >> 4 & 15;
         int var4 = var1x >> 8;
         return new BlockPos(var5.getMinBlockX() + var2, var4, var5.getMinBlockZ() + var3);
      });
   }
}
