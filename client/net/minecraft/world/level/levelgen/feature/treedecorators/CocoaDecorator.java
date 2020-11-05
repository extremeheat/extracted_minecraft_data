package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

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

   public void place(WorldGenLevel var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BoundingBox var6) {
      if (var2.nextFloat() < this.probability) {
         int var7 = ((BlockPos)var3.get(0)).getY();
         var3.stream().filter((var1x) -> {
            return var1x.getY() - var7 <= 2;
         }).forEach((var5x) -> {
            Iterator var6x = Direction.Plane.HORIZONTAL.iterator();

            while(var6x.hasNext()) {
               Direction var7 = (Direction)var6x.next();
               if (var2.nextFloat() <= 0.25F) {
                  Direction var8 = var7.getOpposite();
                  BlockPos var9 = var5x.offset(var8.getStepX(), 0, var8.getStepZ());
                  if (Feature.isAir(var1, var9)) {
                     BlockState var10 = (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, var2.nextInt(3))).setValue(CocoaBlock.FACING, var7);
                     this.setBlock(var1, var9, var10, var5, var6);
                  }
               }
            }

         });
      }
   }
}
