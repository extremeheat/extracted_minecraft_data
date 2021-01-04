package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class LakeWaterPlacementDecorator extends FeatureDecorator<LakeChanceDecoratorConfig> {
   public LakeWaterPlacementDecorator(Function<Dynamic<?>, ? extends LakeChanceDecoratorConfig> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, LakeChanceDecoratorConfig var4, BlockPos var5) {
      if (var3.nextInt(var4.chance) == 0) {
         int var6 = var3.nextInt(16);
         int var7 = var3.nextInt(var2.getGenDepth());
         int var8 = var3.nextInt(16);
         return Stream.of(var5.offset(var6, var7, var8));
      } else {
         return Stream.empty();
      }
   }
}
