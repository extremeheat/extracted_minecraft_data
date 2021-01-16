package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class LakeWaterPlacementDecorator extends FeatureDecorator<ChanceDecoratorConfiguration> {
   public LakeWaterPlacementDecorator(Codec<ChanceDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, ChanceDecoratorConfiguration var3, BlockPos var4) {
      if (var2.nextInt(var3.chance) == 0) {
         int var5 = var2.nextInt(16) + var4.getX();
         int var6 = var2.nextInt(16) + var4.getZ();
         int var7 = var2.nextInt(var1.getGenDepth());
         return Stream.of(new BlockPos(var5, var7, var6));
      } else {
         return Stream.empty();
      }
   }
}
