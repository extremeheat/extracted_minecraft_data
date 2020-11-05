package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class EndGatewayPlacementDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
   public EndGatewayPlacementDecorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, NoneDecoratorConfiguration var3, BlockPos var4) {
      if (var2.nextInt(700) == 0) {
         int var5 = var2.nextInt(16) + var4.getX();
         int var6 = var2.nextInt(16) + var4.getZ();
         int var7 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var5, var6);
         if (var7 > 0) {
            int var8 = var7 + 3 + var2.nextInt(7);
            return Stream.of(new BlockPos(var5, var8, var6));
         }
      }

      return Stream.empty();
   }
}
