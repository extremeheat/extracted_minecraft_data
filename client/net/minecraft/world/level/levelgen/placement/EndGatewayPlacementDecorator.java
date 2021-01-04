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
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class EndGatewayPlacementDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
   public EndGatewayPlacementDecorator(Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, NoneDecoratorConfiguration var4, BlockPos var5) {
      if (var3.nextInt(700) == 0) {
         int var6 = var3.nextInt(16);
         int var7 = var3.nextInt(16);
         int var8 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var5.offset(var6, 0, var7)).getY();
         if (var8 > 0) {
            int var9 = var8 + 3 + var3.nextInt(7);
            return Stream.of(var5.offset(var6, var9, var7));
         }
      }

      return Stream.empty();
   }
}
