package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CocoaDecorator extends TreeDecorator {
   public static final MapCodec<CocoaDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(CocoaDecorator::new, (var0) -> {
      return var0.probability;
   });
   private final float probability;

   public CocoaDecorator(float var1) {
      super();
      this.probability = var1;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.COCOA;
   }

   public void place(TreeDecorator.Context var1) {
      RandomSource var2 = var1.random();
      if (!(var2.nextFloat() >= this.probability)) {
         ObjectArrayList var3 = var1.logs();
         int var4 = ((BlockPos)var3.get(0)).getY();
         var3.stream().filter((var1x) -> {
            return var1x.getY() - var4 <= 2;
         }).forEach((var2x) -> {
            Iterator var3 = Direction.Plane.HORIZONTAL.iterator();

            while(var3.hasNext()) {
               Direction var4 = (Direction)var3.next();
               if (var2.nextFloat() <= 0.25F) {
                  Direction var5 = var4.getOpposite();
                  BlockPos var6 = var2x.offset(var5.getStepX(), 0, var5.getStepZ());
                  if (var1.isAir(var6)) {
                     var1.setBlock(var6, (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, var2.nextInt(3))).setValue(CocoaBlock.FACING, var4));
                  }
               }
            }

         });
      }
   }
}
