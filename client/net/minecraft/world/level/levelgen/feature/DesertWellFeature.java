package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
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
   private static final BlockStatePredicate IS_SAND;
   private final BlockState sand;
   private final BlockState sandSlab;
   private final BlockState sandstone;
   private final BlockState water;

   public DesertWellFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
      this.sand = Blocks.SAND.defaultBlockState();
      this.sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
      this.sandstone = Blocks.SANDSTONE.defaultBlockState();
      this.water = Blocks.WATER.defaultBlockState();
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();

      for(var3 = var3.above(); var2.isEmptyBlock(var3) && var3.getY() > var2.getMinBuildHeight() + 2; var3 = var3.below()) {
      }

      if (!IS_SAND.test(var2.getBlockState(var3))) {
         return false;
      } else {
         int var4;
         int var5;
         for(var4 = -2; var4 <= 2; ++var4) {
            for(var5 = -2; var5 <= 2; ++var5) {
               if (var2.isEmptyBlock(var3.offset(var4, -1, var5)) && var2.isEmptyBlock(var3.offset(var4, -2, var5))) {
                  return false;
               }
            }
         }

         int var6;
         for(var4 = -2; var4 <= 0; ++var4) {
            for(var5 = -2; var5 <= 2; ++var5) {
               for(var6 = -2; var6 <= 2; ++var6) {
                  var2.setBlock(var3.offset(var5, var4, var6), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3, this.water, 2);
         Iterator var8 = Direction.Plane.HORIZONTAL.iterator();

         while(var8.hasNext()) {
            Direction var10 = (Direction)var8.next();
            var2.setBlock(var3.relative(var10), this.water, 2);
         }

         BlockPos var9 = var3.below();
         var2.setBlock(var9, this.sand, 2);
         Iterator var11 = Direction.Plane.HORIZONTAL.iterator();

         while(var11.hasNext()) {
            Direction var12 = (Direction)var11.next();
            var2.setBlock(var9.relative(var12), this.sand, 2);
         }

         for(var5 = -2; var5 <= 2; ++var5) {
            for(var6 = -2; var6 <= 2; ++var6) {
               if (var5 == -2 || var5 == 2 || var6 == -2 || var6 == 2) {
                  var2.setBlock(var3.offset(var5, 1, var6), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3.offset(2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(-2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, 2), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, -2), this.sandSlab, 2);

         for(var5 = -1; var5 <= 1; ++var5) {
            for(var6 = -1; var6 <= 1; ++var6) {
               if (var5 == 0 && var6 == 0) {
                  var2.setBlock(var3.offset(var5, 4, var6), this.sandstone, 2);
               } else {
                  var2.setBlock(var3.offset(var5, 4, var6), this.sandSlab, 2);
               }
            }
         }

         for(var5 = 1; var5 <= 3; ++var5) {
            var2.setBlock(var3.offset(-1, var5, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(-1, var5, 1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var5, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var5, 1), this.sandstone, 2);
         }

         List var13 = List.of(var3, var3.east(), var3.south(), var3.west(), var3.north());
         RandomSource var7 = var1.random();
         placeSusSand(var2, ((BlockPos)Util.getRandom(var13, var7)).below(1));
         placeSusSand(var2, ((BlockPos)Util.getRandom(var13, var7)).below(2));
         return true;
      }
   }

   private static void placeSusSand(WorldGenLevel var0, BlockPos var1) {
      var0.setBlock(var1, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
      var0.getBlockEntity(var1, BlockEntityType.BRUSHABLE_BLOCK).ifPresent((var1x) -> {
         var1x.setLootTable(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, var1.asLong());
      });
   }

   static {
      IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
   }
}
