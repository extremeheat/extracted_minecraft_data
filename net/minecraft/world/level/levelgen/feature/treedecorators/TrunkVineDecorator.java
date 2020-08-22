package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class TrunkVineDecorator extends TreeDecorator {
   public TrunkVineDecorator() {
      super(TreeDecoratorType.TRUNK_VINE);
   }

   public TrunkVineDecorator(Dynamic var1) {
      this();
   }

   public void place(LevelAccessor var1, Random var2, List var3, List var4, Set var5, BoundingBox var6) {
      var3.forEach((var5x) -> {
         BlockPos var6x;
         if (var2.nextInt(3) > 0) {
            var6x = var5x.west();
            if (AbstractTreeFeature.isAir(var1, var6x)) {
               this.placeVine(var1, var6x, VineBlock.EAST, var5, var6);
            }
         }

         if (var2.nextInt(3) > 0) {
            var6x = var5x.east();
            if (AbstractTreeFeature.isAir(var1, var6x)) {
               this.placeVine(var1, var6x, VineBlock.WEST, var5, var6);
            }
         }

         if (var2.nextInt(3) > 0) {
            var6x = var5x.north();
            if (AbstractTreeFeature.isAir(var1, var6x)) {
               this.placeVine(var1, var6x, VineBlock.SOUTH, var5, var6);
            }
         }

         if (var2.nextInt(3) > 0) {
            var6x = var5x.south();
            if (AbstractTreeFeature.isAir(var1, var6x)) {
               this.placeVine(var1, var6x, VineBlock.NORTH, var5, var6);
            }
         }

      });
   }

   public Object serialize(DynamicOps var1) {
      return (new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("type"), var1.createString(Registry.TREE_DECORATOR_TYPES.getKey(this.type).toString()))))).getValue();
   }
}
