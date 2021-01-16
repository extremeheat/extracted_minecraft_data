package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class TrunkVineDecorator extends TreeDecorator {
   public static final Codec<TrunkVineDecorator> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   public static final TrunkVineDecorator INSTANCE = new TrunkVineDecorator();

   public TrunkVineDecorator() {
      super();
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.TRUNK_VINE;
   }

   public void place(WorldGenLevel var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BoundingBox var6) {
      var3.forEach((var5x) -> {
         BlockPos var6x;
         if (var2.nextInt(3) > 0) {
            var6x = var5x.west();
            if (Feature.isAir(var1, var6x)) {
               this.placeVine(var1, var6x, VineBlock.EAST, var5, var6);
            }
         }

         if (var2.nextInt(3) > 0) {
            var6x = var5x.east();
            if (Feature.isAir(var1, var6x)) {
               this.placeVine(var1, var6x, VineBlock.WEST, var5, var6);
            }
         }

         if (var2.nextInt(3) > 0) {
            var6x = var5x.north();
            if (Feature.isAir(var1, var6x)) {
               this.placeVine(var1, var6x, VineBlock.SOUTH, var5, var6);
            }
         }

         if (var2.nextInt(3) > 0) {
            var6x = var5x.south();
            if (Feature.isAir(var1, var6x)) {
               this.placeVine(var1, var6x, VineBlock.NORTH, var5, var6);
            }
         }

      });
   }
}
