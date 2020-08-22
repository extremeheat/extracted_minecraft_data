package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CocoaDecorator extends TreeDecorator {
   private final float probability;

   public CocoaDecorator(float var1) {
      super(TreeDecoratorType.COCOA);
      this.probability = var1;
   }

   public CocoaDecorator(Dynamic var1) {
      this(var1.get("probability").asFloat(0.0F));
   }

   public void place(LevelAccessor var1, Random var2, List var3, List var4, Set var5, BoundingBox var6) {
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
                  if (AbstractTreeFeature.isAir(var1, var9)) {
                     BlockState var10 = (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, var2.nextInt(3))).setValue(CocoaBlock.FACING, var7);
                     this.setBlock(var1, var9, var10, var5, var6);
                  }
               }
            }

         });
      }
   }

   public Object serialize(DynamicOps var1) {
      return (new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("type"), var1.createString(Registry.TREE_DECORATOR_TYPES.getKey(this.type).toString()), var1.createString("probability"), var1.createFloat(this.probability))))).getValue();
   }
}
