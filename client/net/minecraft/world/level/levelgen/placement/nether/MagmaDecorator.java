package net.minecraft.world.level.levelgen.placement.nether;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class MagmaDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
   public MagmaDecorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, NoneDecoratorConfiguration var3, BlockPos var4) {
      int var5 = var1.getSeaLevel();
      int var6 = var5 - 5 + var2.nextInt(10);
      return Stream.of(new BlockPos(var4.getX(), var6, var4.getZ()));
   }
}
