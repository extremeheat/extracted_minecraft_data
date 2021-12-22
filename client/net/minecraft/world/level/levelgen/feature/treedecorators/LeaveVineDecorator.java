package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;

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

   public void place(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, List<BlockPos> var4, List<BlockPos> var5) {
      var5.forEach((var3x) -> {
         BlockPos var4;
         if (var3.nextInt(4) == 0) {
            var4 = var3x.west();
            if (Feature.isAir(var1, var4)) {
               addHangingVine(var1, var4, VineBlock.EAST, var2);
            }
         }

         if (var3.nextInt(4) == 0) {
            var4 = var3x.east();
            if (Feature.isAir(var1, var4)) {
               addHangingVine(var1, var4, VineBlock.WEST, var2);
            }
         }

         if (var3.nextInt(4) == 0) {
            var4 = var3x.north();
            if (Feature.isAir(var1, var4)) {
               addHangingVine(var1, var4, VineBlock.SOUTH, var2);
            }
         }

         if (var3.nextInt(4) == 0) {
            var4 = var3x.south();
            if (Feature.isAir(var1, var4)) {
               addHangingVine(var1, var4, VineBlock.NORTH, var2);
            }
         }

      });
   }

   private static void addHangingVine(LevelSimulatedReader var0, BlockPos var1, BooleanProperty var2, BiConsumer<BlockPos, BlockState> var3) {
      placeVine(var3, var1, var2);
      int var4 = 4;

      for(var1 = var1.below(); Feature.isAir(var0, var1) && var4 > 0; --var4) {
         placeVine(var3, var1, var2);
         var1 = var1.below();
      }

   }
}
