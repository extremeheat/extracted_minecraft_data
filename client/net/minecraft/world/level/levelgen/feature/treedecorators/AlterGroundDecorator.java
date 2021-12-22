package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class AlterGroundDecorator extends TreeDecorator {
   public static final Codec<AlterGroundDecorator> CODEC;
   private final BlockStateProvider provider;

   public AlterGroundDecorator(BlockStateProvider var1) {
      super();
      this.provider = var1;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.ALTER_GROUND;
   }

   public void place(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, List<BlockPos> var4, List<BlockPos> var5) {
      if (!var4.isEmpty()) {
         int var6 = ((BlockPos)var4.get(0)).getY();
         var4.stream().filter((var1x) -> {
            return var1x.getY() == var6;
         }).forEach((var4x) -> {
            this.placeCircle(var1, var2, var3, var4x.west().north());
            this.placeCircle(var1, var2, var3, var4x.east(2).north());
            this.placeCircle(var1, var2, var3, var4x.west().south(2));
            this.placeCircle(var1, var2, var3, var4x.east(2).south(2));

            for(int var5 = 0; var5 < 5; ++var5) {
               int var6 = var3.nextInt(64);
               int var7 = var6 % 8;
               int var8 = var6 / 8;
               if (var7 == 0 || var7 == 7 || var8 == 0 || var8 == 7) {
                  this.placeCircle(var1, var2, var3, var4x.offset(-3 + var7, 0, -3 + var8));
               }
            }

         });
      }
   }

   private void placeCircle(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, BlockPos var4) {
      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            if (Math.abs(var5) != 2 || Math.abs(var6) != 2) {
               this.placeBlockAt(var1, var2, var3, var4.offset(var5, 0, var6));
            }
         }
      }

   }

   private void placeBlockAt(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, BlockPos var4) {
      for(int var5 = 2; var5 >= -3; --var5) {
         BlockPos var6 = var4.above(var5);
         if (Feature.isGrassOrDirt(var1, var6)) {
            var2.accept(var6, this.provider.getState(var3, var4));
            break;
         }

         if (!Feature.isAir(var1, var6) && var5 < 0) {
            break;
         }
      }

   }

   static {
      CODEC = BlockStateProvider.CODEC.fieldOf("provider").xmap(AlterGroundDecorator::new, (var0) -> {
         return var0.provider;
      }).codec();
   }
}
