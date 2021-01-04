package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class MonsterRoomPlacementDecorator extends FeatureDecorator<MonsterRoomPlacementConfiguration> {
   public MonsterRoomPlacementDecorator(Function<Dynamic<?>, ? extends MonsterRoomPlacementConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, MonsterRoomPlacementConfiguration var4, BlockPos var5) {
      int var6 = var4.chance;
      return IntStream.range(0, var6).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16);
         int var5x = var3.nextInt(var2.getGenDepth());
         int var6 = var3.nextInt(16);
         return var5.offset(var4, var5x, var6);
      });
   }
}
