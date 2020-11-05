package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class LakeLavaPlacementDecorator extends FeatureDecorator<ChanceDecoratorConfiguration> {
   public LakeLavaPlacementDecorator(Codec<ChanceDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, ChanceDecoratorConfiguration var3, BlockPos var4) {
      if (var2.nextInt(var3.chance / 10) == 0) {
         int var5 = var2.nextInt(16) + var4.getX();
         int var6 = var2.nextInt(16) + var4.getZ();
         int var7 = var2.nextInt(var2.nextInt(var1.getGenDepth() - 8) + 8);
         if (var7 < var1.getSeaLevel() || var2.nextInt(var3.chance / 8) == 0) {
            return Stream.of(new BlockPos(var5, var7, var6));
         }
      }

      return Stream.empty();
   }
}
