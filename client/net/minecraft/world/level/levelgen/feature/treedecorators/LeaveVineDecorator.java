package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LeaveVineDecorator extends TreeDecorator {
   public static final MapCodec<LeaveVineDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(LeaveVineDecorator::new, (var0) -> var0.probability);
   private final float probability;

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.LEAVE_VINE;
   }

   public LeaveVineDecorator(float var1) {
      super();
      this.probability = var1;
   }

   public void place(TreeDecorator.Context var1) {
      RandomSource var2 = var1.random();
      var1.leaves().forEach((var3) -> {
         if (var2.nextFloat() < this.probability) {
            BlockPos var4 = var3.west();
            if (var1.isAir(var4)) {
               addHangingVine(var4, VineBlock.EAST, var1);
            }
         }

         if (var2.nextFloat() < this.probability) {
            BlockPos var5 = var3.east();
            if (var1.isAir(var5)) {
               addHangingVine(var5, VineBlock.WEST, var1);
            }
         }

         if (var2.nextFloat() < this.probability) {
            BlockPos var6 = var3.north();
            if (var1.isAir(var6)) {
               addHangingVine(var6, VineBlock.SOUTH, var1);
            }
         }

         if (var2.nextFloat() < this.probability) {
            BlockPos var7 = var3.south();
            if (var1.isAir(var7)) {
               addHangingVine(var7, VineBlock.NORTH, var1);
            }
         }

      });
   }

   private static void addHangingVine(BlockPos var0, BooleanProperty var1, TreeDecorator.Context var2) {
      var2.placeVine(var0, var1);
      int var3 = 4;

      for(BlockPos var4 = var0.below(); var2.isAir(var4) && var3 > 0; --var3) {
         var2.placeVine(var4, var1);
         var4 = var4.below();
      }

   }
}
