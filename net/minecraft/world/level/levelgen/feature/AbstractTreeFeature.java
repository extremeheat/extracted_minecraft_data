package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public abstract class AbstractTreeFeature extends Feature {
   public AbstractTreeFeature(Function var1) {
      super(var1);
   }

   protected static boolean isFree(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Block var1 = var0x.getBlock();
         return var0x.isAir() || var0x.is(BlockTags.LEAVES) || isDirt(var1) || var1.is(BlockTags.LOGS) || var1.is(BlockTags.SAPLINGS) || var1 == Blocks.VINE;
      });
   }

   public static boolean isAir(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, BlockState::isAir);
   }

   protected static boolean isDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Block var1 = var0x.getBlock();
         return isDirt(var1) && var1 != Blocks.GRASS_BLOCK && var1 != Blocks.MYCELIUM;
      });
   }

   protected static boolean isVine(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.getBlock() == Blocks.VINE;
      });
   }

   public static boolean isBlockWater(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.getBlock() == Blocks.WATER;
      });
   }

   public static boolean isAirOrLeaves(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.isAir() || var0x.is(BlockTags.LEAVES);
      });
   }

   public static boolean isGrassOrDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return isDirt(var0x.getBlock());
      });
   }

   protected static boolean isGrassOrDirtOrFarmland(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Block var1 = var0x.getBlock();
         return isDirt(var1) || var1 == Blocks.FARMLAND;
      });
   }

   public static boolean isReplaceablePlant(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Material var1 = var0x.getMaterial();
         return var1 == Material.REPLACEABLE_PLANT;
      });
   }

   protected void setDirtAt(LevelSimulatedRW var1, BlockPos var2) {
      if (!isDirt(var1, var2)) {
         this.setBlock(var1, var2, Blocks.DIRT.defaultBlockState());
      }

   }

   protected boolean placeLog(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, BoundingBox var5, TreeConfiguration var6) {
      if (!isAirOrLeaves(var1, var3) && !isReplaceablePlant(var1, var3) && !isBlockWater(var1, var3)) {
         return false;
      } else {
         this.setBlock(var1, var3, var6.trunkProvider.getState(var2, var3), var5);
         var4.add(var3.immutable());
         return true;
      }
   }

   protected boolean placeLeaf(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, BoundingBox var5, TreeConfiguration var6) {
      if (!isAirOrLeaves(var1, var3) && !isReplaceablePlant(var1, var3) && !isBlockWater(var1, var3)) {
         return false;
      } else {
         this.setBlock(var1, var3, var6.leavesProvider.getState(var2, var3), var5);
         var4.add(var3.immutable());
         return true;
      }
   }

   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3) {
      this.setBlockKnownShape(var1, var2, var3);
   }

   protected final void setBlock(LevelWriter var1, BlockPos var2, BlockState var3, BoundingBox var4) {
      this.setBlockKnownShape(var1, var2, var3);
      var4.expand(new BoundingBox(var2, var2));
   }

   private void setBlockKnownShape(LevelWriter var1, BlockPos var2, BlockState var3) {
      var1.setBlock(var2, var3, 19);
   }

   public final boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, TreeConfiguration var5) {
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

   private DiscreteVoxelShape updateLeaves(LevelAccessor var1, BoundingBox var2, Set var3, Set var4) {
      ArrayList var5 = Lists.newArrayList();
      BitSetDiscreteVoxelShape var6 = new BitSetDiscreteVoxelShape(var2.getXSpan(), var2.getYSpan(), var2.getZSpan());
      boolean var7 = true;

      for(int var8 = 0; var8 < 6; ++var8) {
         var5.add(Sets.newHashSet());
      }

      BlockPos.PooledMutableBlockPos var31 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var9 = null;

      try {
         Iterator var10 = Lists.newArrayList(var4).iterator();

         BlockPos var11;
         while(var10.hasNext()) {
            var11 = (BlockPos)var10.next();
            if (var2.isInside(var11)) {
               var6.setFull(var11.getX() - var2.x0, var11.getY() - var2.y0, var11.getZ() - var2.z0, true, true);
            }
         }

         var10 = Lists.newArrayList(var3).iterator();

         while(var10.hasNext()) {
            var11 = (BlockPos)var10.next();
            if (var2.isInside(var11)) {
               var6.setFull(var11.getX() - var2.x0, var11.getY() - var2.y0, var11.getZ() - var2.z0, true, true);
            }

            Direction[] var12 = Direction.values();
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               Direction var15 = var12[var14];
               var31.set((Vec3i)var11).move(var15);
               if (!var3.contains(var31)) {
                  BlockState var16 = var1.getBlockState(var31);
                  if (var16.hasProperty(BlockStateProperties.DISTANCE)) {
                     ((Set)var5.get(0)).add(var31.immutable());
                     this.setBlockKnownShape(var1, var31, (BlockState)var16.setValue(BlockStateProperties.DISTANCE, 1));
                     if (var2.isInside(var31)) {
                        var6.setFull(var31.getX() - var2.x0, var31.getY() - var2.y0, var31.getZ() - var2.z0, true, true);
                     }
                  }
               }
            }
         }

         for(int var32 = 1; var32 < 6; ++var32) {
            Set var33 = (Set)var5.get(var32 - 1);
            Set var34 = (Set)var5.get(var32);
            Iterator var35 = var33.iterator();

            while(var35.hasNext()) {
               BlockPos var36 = (BlockPos)var35.next();
               if (var2.isInside(var36)) {
                  var6.setFull(var36.getX() - var2.x0, var36.getY() - var2.y0, var36.getZ() - var2.z0, true, true);
               }

               Direction[] var37 = Direction.values();
               int var38 = var37.length;

               for(int var17 = 0; var17 < var38; ++var17) {
                  Direction var18 = var37[var17];
                  var31.set((Vec3i)var36).move(var18);
                  if (!var33.contains(var31) && !var34.contains(var31)) {
                     BlockState var19 = var1.getBlockState(var31);
                     if (var19.hasProperty(BlockStateProperties.DISTANCE)) {
                        int var20 = (Integer)var19.getValue(BlockStateProperties.DISTANCE);
                        if (var20 > var32 + 1) {
                           BlockState var21 = (BlockState)var19.setValue(BlockStateProperties.DISTANCE, var32 + 1);
                           this.setBlockKnownShape(var1, var31, var21);
                           if (var2.isInside(var31)) {
                              var6.setFull(var31.getX() - var2.x0, var31.getY() - var2.y0, var31.getZ() - var2.z0, true, true);
                           }

                           var34.add(var31.immutable());
                        }
                     }
                  }
               }
            }
         }
      } catch (Throwable var29) {
         var9 = var29;
         throw var29;
      } finally {
         if (var31 != null) {
            if (var9 != null) {
               try {
                  var31.close();
               } catch (Throwable var28) {
                  var9.addSuppressed(var28);
               }
            } else {
               var31.close();
            }
         }

      }

      return var6;
   }

   protected abstract boolean doPlace(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, Set var5, BoundingBox var6, TreeConfiguration var7);
}
