package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class CarvingMaskDecorator extends FeatureDecorator<DecoratorCarvingMaskConfig> {
   public CarvingMaskDecorator(Function<Dynamic<?>, ? extends DecoratorCarvingMaskConfig> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DecoratorCarvingMaskConfig var4, BlockPos var5) {
      ChunkAccess var6 = var1.getChunk(var5);
      ChunkPos var7 = var6.getPos();
      BitSet var8 = var6.getCarvingMask(var4.step);
      return IntStream.range(0, var8.length()).filter((var3x) -> {
         return var8.get(var3x) && var3.nextFloat() < var4.probability;
      }).mapToObj((var1x) -> {
         int var2 = var1x & 15;
         int var3 = var1x >> 4 & 15;
         int var4 = var1x >> 8;
         return new BlockPos(var7.getMinBlockX() + var2, var4, var7.getMinBlockZ() + var3);
      });
   }
}
