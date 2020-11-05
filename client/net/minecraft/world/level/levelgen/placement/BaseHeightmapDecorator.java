package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public abstract class BaseHeightmapDecorator<DC extends DecoratorConfiguration> extends EdgeDecorator<DC> {
   public BaseHeightmapDecorator(Codec<DC> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, DC var3, BlockPos var4) {
      int var5 = var4.getX();
      int var6 = var4.getZ();
      int var7 = var1.getHeight(this.type(var3), var5, var6);
      return var7 > var1.getMinBuildHeight() ? Stream.of(new BlockPos(var5, var7, var6)) : Stream.of();
   }
}
