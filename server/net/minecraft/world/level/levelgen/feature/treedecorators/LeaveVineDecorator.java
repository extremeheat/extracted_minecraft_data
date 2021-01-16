package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class LeaveVineDecorator extends TreeDecorator {
   public static final Codec<LeaveVineDecorator> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   public static final LeaveVineDecorator INSTANCE = new LeaveVineDecorator();

   public LeaveVineDecorator() {
      super();
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.LEAVE_VINE;
   }

   public void place(WorldGenLevel var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BoundingBox var6) {
      var4.forEach((var5x) -> {
         BlockPos var6x;
         if (var2.nextInt(4) == 0) {
            var6x = var5x.west();
            if (Feature.isAir(var1, var6x)) {
               this.addHangingVine(var1, var6x, VineBlock.EAST, var5, var6);
            }
         }

         if (var2.nextInt(4) == 0) {
            var6x = var5x.east();
            if (Feature.isAir(var1, var6x)) {
               this.addHangingVine(var1, var6x, VineBlock.WEST, var5, var6);
            }
         }

         if (var2.nextInt(4) == 0) {
            var6x = var5x.north();
            if (Feature.isAir(var1, var6x)) {
               this.addHangingVine(var1, var6x, VineBlock.SOUTH, var5, var6);
            }
         }

         if (var2.nextInt(4) == 0) {
            var6x = var5x.south();
            if (Feature.isAir(var1, var6x)) {
               this.addHangingVine(var1, var6x, VineBlock.NORTH, var5, var6);
            }
         }

      });
   }

   private void addHangingVine(LevelSimulatedRW var1, BlockPos var2, BooleanProperty var3, Set<BlockPos> var4, BoundingBox var5) {
      this.placeVine(var1, var2, var3, var4, var5);
      int var6 = 4;

      for(var2 = var2.below(); Feature.isAir(var1, var2) && var6 > 0; --var6) {
         this.placeVine(var1, var2, var3, var4, var5);
         var2 = var2.below();
      }

   }
}
