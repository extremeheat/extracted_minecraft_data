package net.minecraft.world.level.levelgen.placement.nether;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.FrequencyDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class HellFireDecorator extends SimpleFeatureDecorator {
   public HellFireDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, FrequencyDecoratorConfiguration var2, BlockPos var3) {
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < var1.nextInt(var1.nextInt(var2.count) + 1) + 1; ++var5) {
         int var6 = var1.nextInt(16) + var3.getX();
         int var7 = var1.nextInt(16) + var3.getZ();
         int var8 = var1.nextInt(120) + 4;
         var4.add(new BlockPos(var6, var8, var7));
      }

      return var4.stream();
   }
}
