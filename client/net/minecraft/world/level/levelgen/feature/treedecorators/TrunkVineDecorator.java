package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;

public class TrunkVineDecorator extends TreeDecorator {
   public static final MapCodec<TrunkVineDecorator> CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final TrunkVineDecorator INSTANCE = new TrunkVineDecorator();

   public TrunkVineDecorator() {
      super();
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.TRUNK_VINE;
   }

   public void place(TreeDecorator.Context var1) {
      RandomSource var2 = var1.random();
      var1.logs().forEach((var2x) -> {
         BlockPos var3;
         if (var2.nextInt(3) > 0) {
            var3 = var2x.west();
            if (var1.isAir(var3)) {
               var1.placeVine(var3, VineBlock.EAST);
            }
         }

         if (var2.nextInt(3) > 0) {
            var3 = var2x.east();
            if (var1.isAir(var3)) {
               var1.placeVine(var3, VineBlock.WEST);
            }
         }

         if (var2.nextInt(3) > 0) {
            var3 = var2x.north();
            if (var1.isAir(var3)) {
               var1.placeVine(var3, VineBlock.SOUTH);
            }
         }

         if (var2.nextInt(3) > 0) {
            var3 = var2x.south();
            if (var1.isAir(var3)) {
               var1.placeVine(var3, VineBlock.NORTH);
            }
         }

      });
   }
}
