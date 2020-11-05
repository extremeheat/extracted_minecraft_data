package net.minecraft.world.level.levelgen.feature;

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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class TreeFeature extends Feature<TreeConfiguration> {
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

   private static boolean isGrassOrDirtOrFarmland(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return isDirt(var0x) || var0x.is(Blocks.FARMLAND);
      });
   }

   private static boolean isReplaceablePlant(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Material var1 = var0x.getMaterial();
         return var1 == Material.REPLACEABLE_PLANT;
      });
   }

   public static void setBlockKnownShape(LevelWriter var0, BlockPos var1, BlockState var2) {
      var0.setBlock(var1, var2, 19);
   }

   public static boolean validTreePos(LevelSimulatedReader var0, BlockPos var1) {
      return isAirOrLeaves(var0, var1) || isReplaceablePlant(var0, var1) || isBlockWater(var0, var1);
   }

   private boolean doPlace(WorldGenLevel var1, Random var2, BlockPos var3, Set<BlockPos> var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7) {
      int var8 = var7.trunkPlacer.getTreeHeight(var2);
      int var9 = var7.foliagePlacer.foliageHeight(var2, var8, var7);
      int var10 = var8 - var9;
      int var11 = var7.foliagePlacer.foliageRadius(var2, var10);
      BlockPos var12;
      int var14;
      if (!var7.fromSapling) {
         int var13 = var1.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, var3).getY();
         var14 = var1.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var3).getY();
         if (var14 - var13 > var7.maxWaterDepth) {
            return false;
         }

         int var15;
         if (var7.heightmap == Heightmap.Types.OCEAN_FLOOR) {
            var15 = var13;
         } else if (var7.heightmap == Heightmap.Types.WORLD_SURFACE) {
            var15 = var14;
         } else {
            var15 = var1.getHeightmapPos(var7.heightmap, var3).getY();
         }

         var12 = new BlockPos(var3.getX(), var15, var3.getZ());
      } else {
         var12 = var3;
      }

      if (var12.getY() >= var1.getMinBuildHeight() + 1 && var12.getY() + var8 + 1 <= var1.getMaxBuildHeight()) {
         if (!isGrassOrDirtOrFarmland(var1, var12.below())) {
            return false;
         } else {
            OptionalInt var16 = var7.minimumSize.minClippedHeight();
            var14 = this.getMaxFreeTreeHeight(var1, var8, var12, var7);
            if (var14 >= var8 || var16.isPresent() && var14 >= var16.getAsInt()) {
               List var17 = var7.trunkPlacer.placeTrunk(var1, var2, var14, var12, var4, var6, var7);
               var17.forEach((var8x) -> {
                  var7.foliagePlacer.createFoliage(var1, var2, var7, var14, var8x, var9, var11, var5, var6);
               });
               return true;
            } else {
               return false;
            }
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

   public final boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, TreeConfiguration var5) {
      HashSet var6 = Sets.newHashSet();
      HashSet var7 = Sets.newHashSet();
      HashSet var8 = Sets.newHashSet();
      BoundingBox var9 = BoundingBox.getUnknownBox();
      boolean var10 = this.doPlace(var1, var3, var4, var6, var7, var9, var5);
      if (var9.x0 <= var9.x1 && var10 && !var6.isEmpty()) {
         if (!var5.decorators.isEmpty()) {
            ArrayList var11 = Lists.newArrayList(var6);
            ArrayList var12 = Lists.newArrayList(var7);
            var11.sort(Comparator.comparingInt(Vec3i::getY));
            var12.sort(Comparator.comparingInt(Vec3i::getY));
            var5.decorators.forEach((var6x) -> {
               var6x.place(var1, var3, var11, var12, var8, var9);
            });
         }

         DiscreteVoxelShape var13 = this.updateLeaves(var1, var9, var6, var8);
         StructureTemplate.updateShapeAtEdge(var1, 3, var13, var9.x0, var9.y0, var9.z0);
         return true;
      } else {
         return false;
      }
   }

   private DiscreteVoxelShape updateLeaves(LevelAccessor var1, BoundingBox var2, Set<BlockPos> var3, Set<BlockPos> var4) {
      ArrayList var5 = Lists.newArrayList();
      BitSetDiscreteVoxelShape var6 = new BitSetDiscreteVoxelShape(var2.getXSpan(), var2.getYSpan(), var2.getZSpan());
      boolean var7 = true;

      for(int var8 = 0; var8 < 6; ++var8) {
         var5.add(Sets.newHashSet());
      }

      BlockPos.MutableBlockPos var21 = new BlockPos.MutableBlockPos();
      Iterator var9 = Lists.newArrayList(var4).iterator();

      BlockPos var10;
      while(var9.hasNext()) {
         var10 = (BlockPos)var9.next();
         if (var2.isInside(var10)) {
            var6.fill(var10.getX() - var2.x0, var10.getY() - var2.y0, var10.getZ() - var2.z0);
         }
      }

      var9 = Lists.newArrayList(var3).iterator();

      while(var9.hasNext()) {
         var10 = (BlockPos)var9.next();
         if (var2.isInside(var10)) {
            var6.fill(var10.getX() - var2.x0, var10.getY() - var2.y0, var10.getZ() - var2.z0);
         }

         Direction[] var11 = Direction.values();
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            Direction var14 = var11[var13];
            var21.setWithOffset(var10, var14);
            if (!var3.contains(var21)) {
               BlockState var15 = var1.getBlockState(var21);
               if (var15.hasProperty(BlockStateProperties.DISTANCE)) {
                  ((Set)var5.get(0)).add(var21.immutable());
                  setBlockKnownShape(var1, var21, (BlockState)var15.setValue(BlockStateProperties.DISTANCE, 1));
                  if (var2.isInside(var21)) {
                     var6.fill(var21.getX() - var2.x0, var21.getY() - var2.y0, var21.getZ() - var2.z0);
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
            if (var2.isInside(var26)) {
               var6.fill(var26.getX() - var2.x0, var26.getY() - var2.y0, var26.getZ() - var2.z0);
            }

            Direction[] var27 = Direction.values();
            int var28 = var27.length;

            for(int var16 = 0; var16 < var28; ++var16) {
               Direction var17 = var27[var16];
               var21.setWithOffset(var26, var17);
               if (!var23.contains(var21) && !var24.contains(var21)) {
                  BlockState var18 = var1.getBlockState(var21);
                  if (var18.hasProperty(BlockStateProperties.DISTANCE)) {
                     int var19 = (Integer)var18.getValue(BlockStateProperties.DISTANCE);
                     if (var19 > var22 + 1) {
                        BlockState var20 = (BlockState)var18.setValue(BlockStateProperties.DISTANCE, var22 + 1);
                        setBlockKnownShape(var1, var21, var20);
                        if (var2.isInside(var21)) {
                           var6.fill(var21.getX() - var2.x0, var21.getY() - var2.y0, var21.getZ() - var2.z0);
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
