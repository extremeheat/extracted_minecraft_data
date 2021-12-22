package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

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

   public void place(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, List<BlockPos> var4, List<BlockPos> var5) {
      var4.forEach((var3x) -> {
         BlockPos var4;
         if (var3.nextInt(3) > 0) {
            var4 = var3x.west();
            if (Feature.isAir(var1, var4)) {
               placeVine(var2, var4, VineBlock.EAST);
            }
         }

         if (var3.nextInt(3) > 0) {
            var4 = var3x.east();
            if (Feature.isAir(var1, var4)) {
               placeVine(var2, var4, VineBlock.WEST);
            }
         }

         if (var3.nextInt(3) > 0) {
            var4 = var3x.north();
            if (Feature.isAir(var1, var4)) {
               placeVine(var2, var4, VineBlock.SOUTH);
            }
         }

         if (var3.nextInt(3) > 0) {
            var4 = var3x.south();
            if (Feature.isAir(var1, var4)) {
               placeVine(var2, var4, VineBlock.NORTH);
            }
         }

      });
   }
}
