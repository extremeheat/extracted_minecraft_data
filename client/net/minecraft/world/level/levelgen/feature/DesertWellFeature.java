package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertWellFeature extends Feature<NoneFeatureConfiguration> {
   private static final BlockStatePredicate IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
   private final BlockState sand = Blocks.SAND.defaultBlockState();
   private final BlockState sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
   private final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
   private final BlockState water = Blocks.WATER.defaultBlockState();

   public DesertWellFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      var3 = var3.above();

      while (var2.isEmptyBlock(var3) && var3.getY() > var2.getMinBuildHeight() + 2) {
         var3 = var3.below();
      }

      if (!IS_SAND.test(var2.getBlockState(var3))) {
         return false;
      } else {
         for (int var4 = -2; var4 <= 2; var4++) {
            for (int var5 = -2; var5 <= 2; var5++) {
               if (var2.isEmptyBlock(var3.offset(var4, -1, var5)) && var2.isEmptyBlock(var3.offset(var4, -2, var5))) {
                  return false;
               }
            }
         }

         for (int var9 = -2; var9 <= 0; var9++) {
            for (int var12 = -2; var12 <= 2; var12++) {
               for (int var6 = -2; var6 <= 2; var6++) {
                  var2.setBlock(var3.offset(var12, var9, var6), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3, this.water, 2);

         for (Direction var13 : Direction.Plane.HORIZONTAL) {
            var2.setBlock(var3.relative(var13), this.water, 2);
         }

         BlockPos var11 = var3.below();
         var2.setBlock(var11, this.sand, 2);

         for (Direction var18 : Direction.Plane.HORIZONTAL) {
            var2.setBlock(var11.relative(var18), this.sand, 2);
         }

         for (int var15 = -2; var15 <= 2; var15++) {
            for (int var19 = -2; var19 <= 2; var19++) {
               if (var15 == -2 || var15 == 2 || var19 == -2 || var19 == 2) {
                  var2.setBlock(var3.offset(var15, 1, var19), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3.offset(2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(-2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, 2), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, -2), this.sandSlab, 2);

         for (int var16 = -1; var16 <= 1; var16++) {
            for (int var20 = -1; var20 <= 1; var20++) {
               if (var16 == 0 && var20 == 0) {
                  var2.setBlock(var3.offset(var16, 4, var20), this.sandstone, 2);
               } else {
                  var2.setBlock(var3.offset(var16, 4, var20), this.sandSlab, 2);
               }
            }
         }

         for (int var17 = 1; var17 <= 3; var17++) {
            var2.setBlock(var3.offset(-1, var17, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(-1, var17, 1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var17, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var17, 1), this.sandstone, 2);
         }

         List var21 = List.of(var3, var3.east(), var3.south(), var3.west(), var3.north());
         RandomSource var7 = var1.random();
         placeSusSand(var2, Util.<BlockPos>getRandom(var21, var7).below(1));
         placeSusSand(var2, Util.<BlockPos>getRandom(var21, var7).below(2));
         return true;
      }
   }

   private static void placeSusSand(WorldGenLevel var0, BlockPos var1) {
      var0.setBlock(var1, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
      var0.getBlockEntity(var1, BlockEntityType.BRUSHABLE_BLOCK)
         .ifPresent(var1x -> var1x.setLootTable(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, var1.asLong()));
   }
}
