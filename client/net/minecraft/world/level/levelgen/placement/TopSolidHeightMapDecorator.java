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

public class TopSolidHeightMapDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
   public TopSolidHeightMapDecorator(Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, NoneDecoratorConfiguration var4, BlockPos var5) {
      int var6 = var3.nextInt(16);
      int var7 = var3.nextInt(16);
      int var8 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var5.getX() + var6, var5.getZ() + var7);
      return Stream.of(new BlockPos(var5.getX() + var6, var8, var5.getZ() + var7));
   }
}
