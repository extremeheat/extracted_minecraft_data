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
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class TreeFeature extends Feature<TreeConfiguration> {
   private static final int BLOCK_UPDATE_FLAGS = 19;

   public TreeFeature(Codec<TreeConfiguration> var1) {
      super(var1);
   }

   private static boolean isVine(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, var0x -> var0x.is(Blocks.VINE));
   }

   public static boolean isAirOrLeaves(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, var0x -> var0x.isAir() || var0x.is(BlockTags.LEAVES));
   }

   private static void setBlockKnownShape(LevelWriter var0, BlockPos var1, BlockState var2) {
      var0.setBlock(var1, var2, 19);
   }

   public static boolean validTreePos(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, var0x -> var0x.isAir() || var0x.is(BlockTags.REPLACEABLE_BY_TREES));
   }

   private boolean doPlace(
      WorldGenLevel var1,
      RandomSource var2,
      BlockPos var3,
      BiConsumer<BlockPos, BlockState> var4,
      BiConsumer<BlockPos, BlockState> var5,
      FoliagePlacer.FoliageSetter var6,
      TreeConfiguration var7
   ) {
      int var8 = var7.trunkPlacer.getTreeHeight(var2);
      int var9 = var7.foliagePlacer.foliageHeight(var2, var8, var7);
      int var10 = var8 - var9;
      int var11 = var7.foliagePlacer.foliageRadius(var2, var10);
      BlockPos var12 = var7.rootPlacer.<BlockPos>map(var2x -> var2x.getTrunkOrigin(var3, var2)).orElse(var3);
      int var13 = Math.min(var3.getY(), var12.getY());
      int var14 = Math.max(var3.getY(), var12.getY()) + var8 + 1;
      if (var13 >= var1.getMinBuildHeight() + 1 && var14 <= var1.getMaxBuildHeight()) {
         OptionalInt var15 = var7.minimumSize.minClippedHeight();
         int var16 = this.getMaxFreeTreeHeight(var1, var8, var12, var7);
         if (var16 >= var8 || !var15.isEmpty() && var16 >= var15.getAsInt()) {
            if (var7.rootPlacer.isPresent() && !var7.rootPlacer.get().placeRoots(var1, var4, var2, var3, var12, var7)) {
               return false;
            } else {
               List var17 = var7.trunkPlacer.placeTrunk(var1, var5, var2, var16, var12, var7);
               var17.forEach(var7x -> var7.foliagePlacer.createFoliage(var1, var6, var2, var7, var16, var7x, var9, var11));
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private int getMaxFreeTreeHeight(LevelSimulatedReader var1, int var2, BlockPos var3, TreeConfiguration var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for (int var6 = 0; var6 <= var2 + 1; var6++) {
         int var7 = var4.minimumSize.getSizeAtHeight(var2, var6);

         for (int var8 = -var7; var8 <= var7; var8++) {
            for (int var9 = -var7; var9 <= var7; var9++) {
               var5.setWithOffset(var3, var8, var6, var9);
               if (!var4.trunkPlacer.isFree(var1, var5) || !var4.ignoreVines && isVine(var1, var5)) {
                  return var6 - 2;
               }
            }
         }
      }

      return var2;
   }

   @Override
   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3) {
      setBlockKnownShape(var1, var2, var3);
   }

   @Override
   public final boolean place(FeaturePlaceContext<TreeConfiguration> var1) {
      final WorldGenLevel var2 = var1.level();
      RandomSource var3 = var1.random();
      BlockPos var4 = var1.origin();
      TreeConfiguration var5 = (TreeConfiguration)var1.config();
      HashSet var6 = Sets.newHashSet();
      HashSet var7 = Sets.newHashSet();
      final HashSet var8 = Sets.newHashSet();
      HashSet var9 = Sets.newHashSet();
      BiConsumer var10 = (var2x, var3x) -> {
         var6.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      BiConsumer var11 = (var2x, var3x) -> {
         var7.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      FoliagePlacer.FoliageSetter var12 = new FoliagePlacer.FoliageSetter() {
         @Override
         public void set(BlockPos var1, BlockState var2x) {
            var8.add(var1.immutable());
            var2.setBlock(var1, var2x, 19);
         }

         @Override
         public boolean isSet(BlockPos var1) {
            return var8.contains(var1);
         }
      };
      BiConsumer var13 = (var2x, var3x) -> {
         var9.add(var2x.immutable());
         var2.setBlock(var2x, var3x, 19);
      };
      boolean var14 = this.doPlace(var2, var3, var4, var10, var11, var12, var5);
      if (var14 && (!var7.isEmpty() || !var8.isEmpty())) {
         if (!var5.decorators.isEmpty()) {
            TreeDecorator.Context var15 = new TreeDecorator.Context(var2, var13, var3, var7, var8, var6);
            var5.decorators.forEach(var1x -> var1x.place(var15));
         }

         return BoundingBox.encapsulatingPositions(Iterables.concat(var6, var7, var8, var9)).map(var4x -> {
            DiscreteVoxelShape var5x = updateLeaves(var2, var4x, var7, var9, var6);
            StructureTemplate.updateShapeAtEdge(var2, 3, var5x, var4x.minX(), var4x.minY(), var4x.minZ());
            return true;
         }).orElse(false);
      } else {
         return false;
      }
   }

   private static DiscreteVoxelShape updateLeaves(LevelAccessor var0, BoundingBox var1, Set<BlockPos> var2, Set<BlockPos> var3, Set<BlockPos> var4) {
      BitSetDiscreteVoxelShape var5 = new BitSetDiscreteVoxelShape(var1.getXSpan(), var1.getYSpan(), var1.getZSpan());
      byte var6 = 7;
      ArrayList var7 = Lists.newArrayList();

      for (int var8 = 0; var8 < 7; var8++) {
         var7.add(Sets.newHashSet());
      }

      for (BlockPos var9 : Lists.newArrayList(Sets.union(var3, var4))) {
         if (var1.isInside(var9)) {
            var5.fill(var9.getX() - var1.minX(), var9.getY() - var1.minY(), var9.getZ() - var1.minZ());
         }
      }

      BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
      int var24 = 0;
      ((Set)var7.get(0)).addAll(var2);

      while (true) {
         while (var24 >= 7 || !((Set)var7.get(var24)).isEmpty()) {
            if (var24 >= 7) {
               return var5;
            }

            Iterator var10 = ((Set)var7.get(var24)).iterator();
            BlockPos var11 = (BlockPos)var10.next();
            var10.remove();
            if (var1.isInside(var11)) {
               if (var24 != 0) {
                  BlockState var12 = var0.getBlockState(var11);
                  setBlockKnownShape(var0, var11, var12.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(var24)));
               }

               var5.fill(var11.getX() - var1.minX(), var11.getY() - var1.minY(), var11.getZ() - var1.minZ());

               for (Direction var15 : Direction.values()) {
                  var23.setWithOffset(var11, var15);
                  if (var1.isInside(var23)) {
                     int var16 = var23.getX() - var1.minX();
                     int var17 = var23.getY() - var1.minY();
                     int var18 = var23.getZ() - var1.minZ();
                     if (!var5.isFull(var16, var17, var18)) {
                        BlockState var19 = var0.getBlockState(var23);
                        OptionalInt var20 = LeavesBlock.getOptionalDistanceAt(var19);
                        if (!var20.isEmpty()) {
                           int var21 = Math.min(var20.getAsInt(), var24 + 1);
                           if (var21 < 7) {
                              ((Set)var7.get(var21)).add(var23.immutable());
                              var24 = Math.min(var24, var21);
                           }
                        }
                     }
                  }
               }
            }
         }

         var24++;
      }
   }
}
