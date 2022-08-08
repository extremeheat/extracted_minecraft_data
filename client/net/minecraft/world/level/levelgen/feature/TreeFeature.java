package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class TreeFeature extends Feature<TreeConfiguration> {
   private static final int BLOCK_UPDATE_FLAGS = 19;

   public TreeFeature(Codec<TreeConfiguration> var1) {
      super(var1);
   }

   private static boolean isVine(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.is(Blocks.VINE);
      });
   }

   public static boolean isBlockWater(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.is(Blocks.WATER);
      });
   }

   public static boolean isAirOrLeaves(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.isAir() || var0x.is(BlockTags.LEAVES);
      });
   }

   private static boolean isReplaceablePlant(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Material var1 = var0x.getMaterial();
         return var1 == Material.REPLACEABLE_PLANT || var1 == Material.REPLACEABLE_WATER_PLANT || var1 == Material.REPLACEABLE_FIREPROOF_PLANT;
      });
   }

   private static void setBlockKnownShape(LevelWriter var0, BlockPos var1, BlockState var2) {
      var0.setBlock(var1, var2, 19);
   }

   public static boolean validTreePos(LevelSimulatedReader var0, BlockPos var1) {
      return isAirOrLeaves(var0, var1) || isReplaceablePlant(var0, var1) || isBlockWater(var0, var1);
   }

   private boolean doPlace(WorldGenLevel var1, RandomSource var2, BlockPos var3, BiConsumer<BlockPos, BlockState> var4, BiConsumer<BlockPos, BlockState> var5, BiConsumer<BlockPos, BlockState> var6, TreeConfiguration var7) {
      int var8 = var7.trunkPlacer.getTreeHeight(var2);
      int var9 = var7.foliagePlacer.foliageHeight(var2, var8, var7);
      int var10 = var8 - var9;
      int var11 = var7.foliagePlacer.foliageRadius(var2, var10);
      BlockPos var12 = (BlockPos)var7.rootPlacer.map((var2x) -> {
         return var2x.getTrunkOrigin(var3, var2);
      }).orElse(var3);
      int var13 = Math.min(var3.getY(), var12.getY());
      int var14 = Math.max(var3.getY(), var12.getY()) + var8 + 1;
      if (var13 >= var1.getMinBuildHeight() + 1 && var14 <= var1.getMaxBuildHeight()) {
         OptionalInt var15 = var7.minimumSize.minClippedHeight();
         int var16 = this.getMaxFreeTreeHeight(var1, var8, var12, var7);
         if (var16 < var8 && (var15.isEmpty() || var16 < var15.getAsInt())) {
            return false;
         } else if (var7.rootPlacer.isPresent() && !((RootPlacer)var7.rootPlacer.get()).placeRoots(var1, var4, var2, var3, var12, var7)) {
            return false;
         } else {
            List var17 = var7.trunkPlacer.placeTrunk(var1, var5, var2, var16, var12, var7);
            var17.forEach((var7x) -> {
               var7.foliagePlacer.createFoliage(var1, var6, var2, var7, var16, var7x, var9, var11);
            });
            return true;
         }
      } else {
         return false;
      }
   }

   private int getMaxFreeTreeHeight(LevelSimulatedReader var1, int var2, BlockPos var3, TreeConfiguration var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for(int var6 = 0; var6 <= var2 + 1; ++var6) {
         int var7 = var4.minimumSize.getSizeAtHeight(var2, var6);

         for(int var8 = -var7; var8 <= var7; ++var8) {
            for(int var9 = -var7; var9 <= var7; ++var9) {
               var5.setWithOffset(var3, var8, var6, var9);
               if (!var4.trunkPlacer.isFree(var1, var5) || !var4.ignoreVines && isVine(var1, var5)) {
                  return var6 - 2;
               }
            }
         }
      }

      return var2;
   }

   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3) {
      setBlockKnownShape(var1, var2, var3);
   }

   public final boolean place(FeaturePlaceContext<TreeConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      RandomSource var3 = var1.random();
      BlockPos var4 = var1.origin();
      TreeConfiguration var5 = (TreeConfiguration)var1.config();
      HashSet var6 = Sets.newHashSet();
      HashSet var7 = Sets.newHashSet();
      HashSet var8 = Sets.newHashSet();
      HashSet var9 = Sets.newHashSet();
      BiConsumer var10 = (var2x, var3x) -> {
         var6.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      BiConsumer var11 = (var2x, var3x) -> {
         var7.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      BiConsumer var12 = (var2x, var3x) -> {
         var8.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      BiConsumer var13 = (var2x, var3x) -> {
         var9.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      boolean var14 = this.doPlace(var2, var3, var4, var10, var11, var12, var5);
      if (var14 && (!var7.isEmpty() || !var8.isEmpty())) {
         if (!var5.decorators.isEmpty()) {
            TreeDecorator.Context var15 = new TreeDecorator.Context(var2, var13, var3, var7, var8, var6);
            var5.decorators.forEach((var1x) -> {
               var1x.place(var15);
            });
         }

         return (Boolean)BoundingBox.encapsulatingPositions(Iterables.concat(var6, var7, var8, var9)).map((var4x) -> {
            DiscreteVoxelShape var5 = updateLeaves(var2, var4x, var7, var9, var6);
            StructureTemplate.updateShapeAtEdge(var2, 3, var5, var4x.minX(), var4x.minY(), var4x.minZ());
            return true;
         }).orElse(false);
      } else {
         return false;
      }
   }

   private static DiscreteVoxelShape updateLeaves(LevelAccessor var0, BoundingBox var1, Set<BlockPos> var2, Set<BlockPos> var3, Set<BlockPos> var4) {
      ArrayList var5 = Lists.newArrayList();
      BitSetDiscreteVoxelShape var6 = new BitSetDiscreteVoxelShape(var1.getXSpan(), var1.getYSpan(), var1.getZSpan());
      boolean var7 = true;

      for(int var8 = 0; var8 < 6; ++var8) {
         var5.add(Sets.newHashSet());
      }

      BlockPos.MutableBlockPos var21 = new BlockPos.MutableBlockPos();
      Iterator var9 = Lists.newArrayList(Sets.union(var3, var4)).iterator();

      BlockPos var10;
      while(var9.hasNext()) {
         var10 = (BlockPos)var9.next();
         if (var1.isInside(var10)) {
            var6.fill(var10.getX() - var1.minX(), var10.getY() - var1.minY(), var10.getZ() - var1.minZ());
         }
      }

      var9 = Lists.newArrayList(var2).iterator();

      while(var9.hasNext()) {
         var10 = (BlockPos)var9.next();
         if (var1.isInside(var10)) {
            var6.fill(var10.getX() - var1.minX(), var10.getY() - var1.minY(), var10.getZ() - var1.minZ());
         }

         Direction[] var11 = Direction.values();
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            Direction var14 = var11[var13];
            var21.setWithOffset(var10, (Direction)var14);
            if (!var2.contains(var21)) {
               BlockState var15 = var0.getBlockState(var21);
               if (var15.hasProperty(BlockStateProperties.DISTANCE)) {
                  ((Set)var5.get(0)).add(var21.immutable());
                  setBlockKnownShape(var0, var21, (BlockState)var15.setValue(BlockStateProperties.DISTANCE, 1));
                  if (var1.isInside(var21)) {
                     var6.fill(var21.getX() - var1.minX(), var21.getY() - var1.minY(), var21.getZ() - var1.minZ());
                  }
               }
            }
         }
      }

      for(int var22 = 1; var22 < 6; ++var22) {
         Set var23 = (Set)var5.get(var22 - 1);
         Set var24 = (Set)var5.get(var22);
         Iterator var25 = var23.iterator();

         while(var25.hasNext()) {
            BlockPos var26 = (BlockPos)var25.next();
            if (var1.isInside(var26)) {
               var6.fill(var26.getX() - var1.minX(), var26.getY() - var1.minY(), var26.getZ() - var1.minZ());
            }

            Direction[] var27 = Direction.values();
            int var28 = var27.length;

            for(int var16 = 0; var16 < var28; ++var16) {
               Direction var17 = var27[var16];
               var21.setWithOffset(var26, (Direction)var17);
               if (!var23.contains(var21) && !var24.contains(var21)) {
                  BlockState var18 = var0.getBlockState(var21);
                  if (var18.hasProperty(BlockStateProperties.DISTANCE)) {
                     int var19 = (Integer)var18.getValue(BlockStateProperties.DISTANCE);
                     if (var19 > var22 + 1) {
                        BlockState var20 = (BlockState)var18.setValue(BlockStateProperties.DISTANCE, var22 + 1);
                        setBlockKnownShape(var0, var21, var20);
                        if (var1.isInside(var21)) {
                           var6.fill(var21.getX() - var1.minX(), var21.getY() - var1.minY(), var21.getZ() - var1.minZ());
                        }

                        var24.add(var21.immutable());
                     }
                  }
               }
            }
         }
      }

      return var6;
   }
}
