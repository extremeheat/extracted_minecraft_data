package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;

public class TrunkVineDecorator extends TreeDecorator {
   public static final MapCodec<TrunkVineDecorator> CODEC = MapCodec.unit(() -> TrunkVineDecorator.INSTANCE);
   public static final TrunkVineDecorator INSTANCE = new TrunkVineDecorator();

   public TrunkVineDecorator() {
      super();
   }

   @Override
   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.TRUNK_VINE;
   }

   @Override
   public void place(TreeDecorator.Context var1) {
      RandomSource var2 = var1.random();
      var1.logs().forEach(var2x -> {
         if (var2.nextInt(3) > 0) {
            BlockPos var3 = var2x.west();
            if (var1.isAir(var3)) {
               var1.placeVine(var3, VineBlock.EAST);
            }
         }

         if (var2.nextInt(3) > 0) {
            BlockPos var4 = var2x.east();
            if (var1.isAir(var4)) {
               var1.placeVine(var4, VineBlock.WEST);
            }
         }

         if (var2.nextInt(3) > 0) {
            BlockPos var5 = var2x.north();
            if (var1.isAir(var5)) {
               var1.placeVine(var5, VineBlock.SOUTH);
            }
         }

         if (var2.nextInt(3) > 0) {
            BlockPos var6 = var2x.south();
            if (var1.isAir(var6)) {
               var1.placeVine(var6, VineBlock.NORTH);
            }
         }
      });
   }
}
