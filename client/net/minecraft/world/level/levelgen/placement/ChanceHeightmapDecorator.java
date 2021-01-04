package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap;

public class ChanceHeightmapDecorator extends FeatureDecorator<DecoratorChance> {
   public ChanceHeightmapDecorator(Function<Dynamic<?>, ? extends DecoratorChance> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DecoratorChance var4, BlockPos var5) {
      if (var3.nextFloat() < 1.0F / (float)var4.chance) {
         int var6 = var3.nextInt(16);
         int var7 = var3.nextInt(16);
         BlockPos var8 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var5.offset(var6, 0, var7));
         return Stream.of(var8);
      } else {
         return Stream.empty();
      }
   }
}
