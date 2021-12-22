package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
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

   public static boolean isFree(LevelSimulatedReader var0, BlockPos var1) {
      return validTreePos(var0, var1) || var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.is(BlockTags.LOGS);
      });
   }

   private static boolean isVine(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.is(Blocks.VINE);
      });
   }

   private static boolean isBlockWater(LevelSimulatedReader var0, BlockPos var1) {
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
         return var1 == Material.REPLACEABLE_PLANT;
      });
   }

   private static void setBlockKnownShape(LevelWriter var0, BlockPos var1, BlockState var2) {
      var0.setBlock(var1, var2, 19);
   }

   public static boolean validTreePos(LevelSimulatedReader var0, BlockPos var1) {
      return isAirOrLeaves(var0, var1) || isReplaceablePlant(var0, var1) || isBlockWater(var0, var1);
   }

   private boolean doPlace(WorldGenLevel var1, Random var2, BlockPos var3, BiConsumer<BlockPos, BlockState> var4, BiConsumer<BlockPos, BlockState> var5, TreeConfiguration var6) {
      int var7 = var6.trunkPlacer.getTreeHeight(var2);
      int var8 = var6.foliagePlacer.foliageHeight(var2, var7, var6);
      int var9 = var7 - var8;
      int var10 = var6.foliagePlacer.foliageRadius(var2, var9);
      if (var3.getY() >= var1.getMinBuildHeight() + 1 && var3.getY() + var7 + 1 <= var1.getMaxBuildHeight()) {
         OptionalInt var11 = var6.minimumSize.minClippedHeight();
         int var12 = this.getMaxFreeTreeHeight(var1, var7, var3, var6);
         if (var12 >= var7 || var11.isPresent() && var12 >= var11.getAsInt()) {
            List var13 = var6.trunkPlacer.placeTrunk(var1, var4, var2, var12, var3, var6);
            var13.forEach((var7x) -> {
               var6.foliagePlacer.createFoliage(var1, var5, var2, var6, var12, var7x, var8, var10);
            });
            return true;
         } else {
            return false;
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
               if (!isFree(var1, var5) || !var4.ignoreVines && isVine(var1, var5)) {
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
      Random var3 = var1.random();
      BlockPos var4 = var1.origin();
      TreeConfiguration var5 = (TreeConfiguration)var1.config();
      HashSet var6 = Sets.newHashSet();
      HashSet var7 = Sets.newHashSet();
      HashSet var8 = Sets.newHashSet();
      BiConsumer var9 = (var2x, var3x) -> {
         var6.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      BiConsumer var10 = (var2x, var3x) -> {
         var7.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      BiConsumer var11 = (var2x, var3x) -> {
         var8.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      boolean var12 = this.doPlace(var2, var3, var4, var9, var10, var5);
      if (var12 && (!var6.isEmpty() || !var7.isEmpty())) {
         if (!var5.decorators.isEmpty()) {
            ArrayList var13 = Lists.newArrayList(var6);
            ArrayList var14 = Lists.newArrayList(var7);
            var13.sort(Comparator.comparingInt(Vec3i::getY));
            var14.sort(Comparator.comparingInt(Vec3i::getY));
            var5.decorators.forEach((var5x) -> {
               var5x.place(var2, var11, var3, var13, var14);
            });
         }

         return (Boolean)BoundingBox.encapsulatingPositions(Iterables.concat(var6, var7, var8)).map((var3x) -> {
            DiscreteVoxelShape var4 = updateLeaves(var2, var3x, var6, var8);
            StructureTemplate.updateShapeAtEdge(var2, 3, var4, var3x.minX(), var3x.minY(), var3x.minZ());
            return true;
         }).orElse(false);
      } else {
         return false;
      }
   }

   private static DiscreteVoxelShape updateLeaves(LevelAccessor var0, BoundingBox var1, Set<BlockPos> var2, Set<BlockPos> var3) {
      ArrayList var4 = Lists.newArrayList();
      BitSetDiscreteVoxelShape var5 = new BitSetDiscreteVoxelShape(var1.getXSpan(), var1.getYSpan(), var1.getZSpan());
      boolean var6 = true;

      for(int var7 = 0; var7 < 6; ++var7) {
         var4.add(Sets.newHashSet());
      }

      BlockPos.MutableBlockPos var20 = new BlockPos.MutableBlockPos();
      Iterator var8 = Lists.newArrayList(var3).iterator();

      BlockPos var9;
      while(var8.hasNext()) {
         var9 = (BlockPos)var8.next();
         if (var1.isInside(var9)) {
            var5.fill(var9.getX() - var1.minX(), var9.getY() - var1.minY(), var9.getZ() - var1.minZ());
         }
      }

      var8 = Lists.newArrayList(var2).iterator();

      while(var8.hasNext()) {
         var9 = (BlockPos)var8.next();
         if (var1.isInside(var9)) {
            var5.fill(var9.getX() - var1.minX(), var9.getY() - var1.minY(), var9.getZ() - var1.minZ());
         }

         Direction[] var10 = Direction.values();
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            Direction var13 = var10[var12];
            var20.setWithOffset(var9, (Direction)var13);
            if (!var2.contains(var20)) {
               BlockState var14 = var0.getBlockState(var20);
               if (var14.hasProperty(BlockStateProperties.DISTANCE)) {
                  ((Set)var4.get(0)).add(var20.immutable());
                  setBlockKnownShape(var0, var20, (BlockState)var14.setValue(BlockStateProperties.DISTANCE, 1));
                  if (var1.isInside(var20)) {
                     var5.fill(var20.getX() - var1.minX(), var20.getY() - var1.minY(), var20.getZ() - var1.minZ());
                  }
               }
            }
         }
      }

      for(int var21 = 1; var21 < 6; ++var21) {
         Set var22 = (Set)var4.get(var21 - 1);
         Set var23 = (Set)var4.get(var21);
         Iterator var24 = var22.iterator();

         while(var24.hasNext()) {
            BlockPos var25 = (BlockPos)var24.next();
            if (var1.isInside(var25)) {
               var5.fill(var25.getX() - var1.minX(), var25.getY() - var1.minY(), var25.getZ() - var1.minZ());
            }

            Direction[] var26 = Direction.values();
            int var27 = var26.length;

            for(int var15 = 0; var15 < var27; ++var15) {
               Direction var16 = var26[var15];
               var20.setWithOffset(var25, (Direction)var16);
               if (!var22.contains(var20) && !var23.contains(var20)) {
                  BlockState var17 = var0.getBlockState(var20);
                  if (var17.hasProperty(BlockStateProperties.DISTANCE)) {
                     int var18 = (Integer)var17.getValue(BlockStateProperties.DISTANCE);
                     if (var18 > var21 + 1) {
                        BlockState var19 = (BlockState)var17.setValue(BlockStateProperties.DISTANCE, var21 + 1);
                        setBlockKnownShape(var0, var20, var19);
                        if (var1.isInside(var20)) {
                           var5.fill(var20.getX() - var1.minX(), var20.getY() - var1.minY(), var20.getZ() - var1.minZ());
                        }

                        var23.add(var20.immutable());
                     }
                  }
               }
            }
         }
      }

      return var5;
   }
}
