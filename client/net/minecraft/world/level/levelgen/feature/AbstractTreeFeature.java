package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
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
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;

public abstract class AbstractTreeFeature<T extends FeatureConfiguration> extends Feature<T> {
   public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> var1, boolean var2) {
      super(var1, var2);
   }

   protected static boolean isFree(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Block var1 = var0x.getBlock();
         return var0x.isAir() || var0x.is(BlockTags.LEAVES) || var1 == Blocks.GRASS_BLOCK || Block.equalsDirt(var1) || var1.is(BlockTags.LOGS) || var1.is(BlockTags.SAPLINGS) || var1 == Blocks.VINE;
      });
   }

   protected static boolean isAir(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, BlockState::isAir);
   }

   protected static boolean isDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return Block.equalsDirt(var0x.getBlock());
      });
   }

   protected static boolean isBlockWater(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.getBlock() == Blocks.WATER;
      });
   }

   protected static boolean isLeaves(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.is(BlockTags.LEAVES);
      });
   }

   protected static boolean isAirOrLeaves(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         return var0x.isAir() || var0x.is(BlockTags.LEAVES);
      });
   }

   protected static boolean isGrassOrDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Block var1 = var0x.getBlock();
         return Block.equalsDirt(var1) || var1 == Blocks.GRASS_BLOCK;
      });
   }

   protected static boolean isGrassOrDirtOrFarmland(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, (var0x) -> {
         Block var1 = var0x.getBlock();
         return Block.equalsDirt(var1) || var1 == Blocks.GRASS_BLOCK || var1 == Blocks.FARMLAND;
      });
   }

   protected static boolean isReplaceablePlant(LevelSimulatedReader var0, BlockPos var1) {
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

   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3) {
      this.setBlockKnownShape(var1, var2, var3);
   }

   protected final void setBlock(Set<BlockPos> var1, LevelWriter var2, BlockPos var3, BlockState var4, BoundingBox var5) {
      this.setBlockKnownShape(var2, var3, var4);
      var5.expand(new BoundingBox(var3, var3));
      if (BlockTags.LOGS.contains(var4.getBlock())) {
         var1.add(var3.immutable());
      }

   }

   private void setBlockKnownShape(LevelWriter var1, BlockPos var2, BlockState var3) {
      if (this.doUpdate) {
         var1.setBlock(var2, var3, 19);
      } else {
         var1.setBlock(var2, var3, 18);
      }

   }

   public final boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, T var5) {
      HashSet var6 = Sets.newHashSet();
      BoundingBox var7 = BoundingBox.getUnknownBox();
      boolean var8 = this.doPlace(var6, var1, var3, var4, var7);
      if (var7.x0 > var7.x1) {
         return false;
      } else {
         ArrayList var9 = Lists.newArrayList();
         boolean var10 = true;

         for(int var11 = 0; var11 < 6; ++var11) {
            var9.add(Sets.newHashSet());
         }

         BitSetDiscreteVoxelShape var35 = new BitSetDiscreteVoxelShape(var7.getXSpan(), var7.getYSpan(), var7.getZSpan());
         BlockPos.PooledMutableBlockPos var12 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var13 = null;

         try {
            if (var8 && !var6.isEmpty()) {
               Iterator var14 = Lists.newArrayList(var6).iterator();

               while(var14.hasNext()) {
                  BlockPos var15 = (BlockPos)var14.next();
                  if (var7.isInside(var15)) {
                     var35.setFull(var15.getX() - var7.x0, var15.getY() - var7.y0, var15.getZ() - var7.z0, true, true);
                  }

                  Direction[] var16 = Direction.values();
                  int var17 = var16.length;

                  for(int var18 = 0; var18 < var17; ++var18) {
                     Direction var19 = var16[var18];
                     var12.set((Vec3i)var15).move(var19);
                     if (!var6.contains(var12)) {
                        BlockState var20 = var1.getBlockState(var12);
                        if (var20.hasProperty(BlockStateProperties.DISTANCE)) {
                           ((Set)var9.get(0)).add(var12.immutable());
                           this.setBlockKnownShape(var1, var12, (BlockState)var20.setValue(BlockStateProperties.DISTANCE, 1));
                           if (var7.isInside(var12)) {
                              var35.setFull(var12.getX() - var7.x0, var12.getY() - var7.y0, var12.getZ() - var7.z0, true, true);
                           }
                        }
                     }
                  }
               }
            }

            for(int var36 = 1; var36 < 6; ++var36) {
               Set var37 = (Set)var9.get(var36 - 1);
               Set var38 = (Set)var9.get(var36);
               Iterator var39 = var37.iterator();

               while(var39.hasNext()) {
                  BlockPos var40 = (BlockPos)var39.next();
                  if (var7.isInside(var40)) {
                     var35.setFull(var40.getX() - var7.x0, var40.getY() - var7.y0, var40.getZ() - var7.z0, true, true);
                  }

                  Direction[] var41 = Direction.values();
                  int var42 = var41.length;

                  for(int var21 = 0; var21 < var42; ++var21) {
                     Direction var22 = var41[var21];
                     var12.set((Vec3i)var40).move(var22);
                     if (!var37.contains(var12) && !var38.contains(var12)) {
                        BlockState var23 = var1.getBlockState(var12);
                        if (var23.hasProperty(BlockStateProperties.DISTANCE)) {
                           int var24 = (Integer)var23.getValue(BlockStateProperties.DISTANCE);
                           if (var24 > var36 + 1) {
                              BlockState var25 = (BlockState)var23.setValue(BlockStateProperties.DISTANCE, var36 + 1);
                              this.setBlockKnownShape(var1, var12, var25);
                              if (var7.isInside(var12)) {
                                 var35.setFull(var12.getX() - var7.x0, var12.getY() - var7.y0, var12.getZ() - var7.z0, true, true);
                              }

                              var38.add(var12.immutable());
                           }
                        }
                     }
                  }
               }
            }
         } catch (Throwable var33) {
            var13 = var33;
            throw var33;
         } finally {
            if (var12 != null) {
               if (var13 != null) {
                  try {
                     var12.close();
                  } catch (Throwable var32) {
                     var13.addSuppressed(var32);
                  }
               } else {
                  var12.close();
               }
            }

         }

         StructureTemplate.updateShapeAtEdge(var1, 3, var35, var7.x0, var7.y0, var7.z0);
         return var8;
      }
   }

   protected abstract boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5);
}
