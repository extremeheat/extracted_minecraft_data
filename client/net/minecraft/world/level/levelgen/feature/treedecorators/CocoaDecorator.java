package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

public class CocoaDecorator extends TreeDecorator {
   public static final Codec<CocoaDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(CocoaDecorator::new, (var0) -> {
      return var0.probability;
   }).codec();
   private final float probability;

   public CocoaDecorator(float var1) {
      super();
      this.probability = var1;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.COCOA;
   }

   public void place(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, List<BlockPos> var4, List<BlockPos> var5) {
      if (!(var3.nextFloat() >= this.probability)) {
         int var6 = ((BlockPos)var4.get(0)).getY();
         var4.stream().filter((var1x) -> {
            return var1x.getY() - var6 <= 2;
         }).forEach((var3x) -> {
            Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

            while(var4.hasNext()) {
               Direction var5 = (Direction)var4.next();
               if (var3.nextFloat() <= 0.25F) {
                  Direction var6 = var5.getOpposite();
                  BlockPos var7 = var3x.offset(var6.getStepX(), 0, var6.getStepZ());
                  if (Feature.isAir(var1, var7)) {
                     var2.accept(var7, (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, var3.nextInt(3))).setValue(CocoaBlock.FACING, var5));
                  }
               }
            }

         });
      }
   }
}
