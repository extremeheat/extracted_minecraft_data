package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public abstract class SimpleFeatureDecorator<DC extends DecoratorConfiguration> extends FeatureDecorator<DC> {
   public SimpleFeatureDecorator(Codec<DC> var1) {
      super(var1);
   }

   public final Stream<BlockPos> getPositions(DecorationContext var1, Random var2, DC var3, BlockPos var4) {
      return this.place(var2, var3, var4);
   }

   protected abstract Stream<BlockPos> place(Random var1, DC var2, BlockPos var3);
}
