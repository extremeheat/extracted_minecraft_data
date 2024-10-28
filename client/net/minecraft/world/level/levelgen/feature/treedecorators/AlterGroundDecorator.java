package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class AlterGroundDecorator extends TreeDecorator {
   public static final MapCodec<AlterGroundDecorator> CODEC;
   private final BlockStateProvider provider;

   public AlterGroundDecorator(BlockStateProvider var1) {
      super();
      this.provider = var1;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.ALTER_GROUND;
   }

   public void place(TreeDecorator.Context var1) {
      ArrayList var2 = Lists.newArrayList();
      ObjectArrayList var3 = var1.roots();
      ObjectArrayList var4 = var1.logs();
      if (var3.isEmpty()) {
         var2.addAll(var4);
      } else if (!var4.isEmpty() && ((BlockPos)var3.get(0)).getY() == ((BlockPos)var4.get(0)).getY()) {
         var2.addAll(var4);
         var2.addAll(var3);
      } else {
         var2.addAll(var3);
      }

      if (!var2.isEmpty()) {
         int var5 = ((BlockPos)var2.get(0)).getY();
         var2.stream().filter((var1x) -> {
            return var1x.getY() == var5;
         }).forEach((var2x) -> {
            this.placeCircle(var1, var2x.west().north());
            this.placeCircle(var1, var2x.east(2).north());
            this.placeCircle(var1, var2x.west().south(2));
            this.placeCircle(var1, var2x.east(2).south(2));

            for(int var3 = 0; var3 < 5; ++var3) {
               int var4 = var1.random().nextInt(64);
               int var5 = var4 % 8;
               int var6 = var4 / 8;
               if (var5 == 0 || var5 == 7 || var6 == 0 || var6 == 7) {
                  this.placeCircle(var1, var2x.offset(-3 + var5, 0, -3 + var6));
               }
            }

         });
      }
   }

   private void placeCircle(TreeDecorator.Context var1, BlockPos var2) {
      for(int var3 = -2; var3 <= 2; ++var3) {
         for(int var4 = -2; var4 <= 2; ++var4) {
            if (Math.abs(var3) != 2 || Math.abs(var4) != 2) {
               this.placeBlockAt(var1, var2.offset(var3, 0, var4));
            }
         }
      }

   }

   private void placeBlockAt(TreeDecorator.Context var1, BlockPos var2) {
      for(int var3 = 2; var3 >= -3; --var3) {
         BlockPos var4 = var2.above(var3);
         if (Feature.isGrassOrDirt(var1.level(), var4)) {
            var1.setBlock(var4, this.provider.getState(var1.random(), var2));
            break;
         }

         if (!var1.isAir(var4) && var3 < 0) {
            break;
         }
      }

   }

   static {
      CODEC = BlockStateProvider.CODEC.fieldOf("provider").xmap(AlterGroundDecorator::new, (var0) -> {
         return var0.provider;
      });
   }
}
