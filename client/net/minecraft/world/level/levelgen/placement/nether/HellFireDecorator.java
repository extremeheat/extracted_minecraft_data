package net.minecraft.world.level.levelgen.placement.nether;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class HellFireDecorator extends SimpleFeatureDecorator<DecoratorFrequency> {
   public HellFireDecorator(Function<Dynamic<?>, ? extends DecoratorFrequency> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, DecoratorFrequency var2, BlockPos var3) {
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < var1.nextInt(var1.nextInt(var2.count) + 1) + 1; ++var5) {
         int var6 = var1.nextInt(16);
         int var7 = var1.nextInt(120) + 4;
         int var8 = var1.nextInt(16);
         var4.add(var3.offset(var6, var7, var8));
      }

      return var4.stream();
   }
}
