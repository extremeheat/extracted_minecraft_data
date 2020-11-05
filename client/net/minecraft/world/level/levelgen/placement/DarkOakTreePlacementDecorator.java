package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class DarkOakTreePlacementDecorator extends EdgeDecorator<NoneDecoratorConfiguration> {
   public DarkOakTreePlacementDecorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   protected Heightmap.Types type(NoneDecoratorConfiguration var1) {
      return Heightmap.Types.MOTION_BLOCKING;
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, NoneDecoratorConfiguration var3, BlockPos var4) {
      return IntStream.range(0, 16).mapToObj((var5) -> {
         int var6 = var5 / 4;
         int var7 = var5 % 4;
         int var8 = var6 * 4 + 1 + var2.nextInt(3) + var4.getX();
         int var9 = var7 * 4 + 1 + var2.nextInt(3) + var4.getZ();
         int var10 = var1.getHeight(this.type(var3), var8, var9);
         return new BlockPos(var8, var10, var9);
      });
   }
}
