package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WalkNodeEvaluator extends NodeEvaluator {
   protected float oldWaterCost;

   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      this.oldWaterCost = var2.getPathfindingMalus(BlockPathTypes.WATER);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
      super.done();
   }

   public Node getStart() {
      int var1;
      BlockPos var2;
      if (this.canFloat() && this.mob.isInWater()) {
         var1 = Mth.floor(this.mob.getY());
         BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos(this.mob.getX(), (double)var1, this.mob.getZ());

         for(BlockState var3 = this.level.getBlockState(var8); var3.getBlock() == Blocks.WATER || var3.getFluidState() == Fluids.WATER.getSource(false); var3 = this.level.getBlockState(var8)) {
            ++var1;
            var8.set(this.mob.getX(), (double)var1, this.mob.getZ());
         }

         --var1;
      } else if (this.mob.onGround) {
         var1 = Mth.floor(this.mob.getY() + 0.5D);
      } else {
         for(var2 = new BlockPos(this.mob); (this.level.getBlockState(var2).isAir() || this.level.getBlockState(var2).isPathfindable(this.level, var2, PathComputationType.LAND)) && var2.getY() > 0; var2 = var2.below()) {
         }

         var1 = var2.above().getY();
      }

      var2 = new BlockPos(this.mob);
      BlockPathTypes var9 = this.getBlockPathType(this.mob, var2.getX(), var1, var2.getZ());
      if (this.mob.getPathfindingMalus(var9) < 0.0F) {
         HashSet var4 = Sets.newHashSet();
         var4.add(new BlockPos(this.mob.getBoundingBox().minX, (double)var1, this.mob.getBoundingBox().minZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().minX, (double)var1, this.mob.getBoundingBox().maxZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)var1, this.mob.getBoundingBox().minZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)var1, this.mob.getBoundingBox().maxZ));
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
            BlockPathTypes var7 = this.getBlockPathType(this.mob, var6);
            if (this.mob.getPathfindingMalus(var7) >= 0.0F) {
               return this.getNode(var6.getX(), var6.getY(), var6.getZ());
            }
         }
      }

      return this.getNode(var2.getX(), var1, var2.getZ());
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(this.getNode(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5)));
   }

   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      int var4 = 0;
      BlockPathTypes var5 = this.getBlockPathType(this.mob, var2.x, var2.y + 1, var2.z);
      if (this.mob.getPathfindingMalus(var5) >= 0.0F) {
         BlockPathTypes var6 = this.getBlockPathType(this.mob, var2.x, var2.y, var2.z);
         if (var6 == BlockPathTypes.STICKY_HONEY) {
            var4 = 0;
         } else {
            var4 = Mth.floor(Math.max(1.0F, this.mob.maxUpStep));
         }
      }

      double var16 = getFloorLevel(this.level, new BlockPos(var2.x, var2.y, var2.z));
      Node var8 = this.getLandNode(var2.x, var2.y, var2.z + 1, var4, var16, Direction.SOUTH);
      if (var8 != null && !var8.closed && var8.costMalus >= 0.0F) {
         var1[var3++] = var8;
      }

      Node var9 = this.getLandNode(var2.x - 1, var2.y, var2.z, var4, var16, Direction.WEST);
      if (var9 != null && !var9.closed && var9.costMalus >= 0.0F) {
         var1[var3++] = var9;
      }

      Node var10 = this.getLandNode(var2.x + 1, var2.y, var2.z, var4, var16, Direction.EAST);
      if (var10 != null && !var10.closed && var10.costMalus >= 0.0F) {
         var1[var3++] = var10;
      }

      Node var11 = this.getLandNode(var2.x, var2.y, var2.z - 1, var4, var16, Direction.NORTH);
      if (var11 != null && !var11.closed && var11.costMalus >= 0.0F) {
         var1[var3++] = var11;
      }

      Node var12 = this.getLandNode(var2.x - 1, var2.y, var2.z - 1, var4, var16, Direction.NORTH);
      if (this.isDiagonalValid(var2, var9, var11, var12)) {
         var1[var3++] = var12;
      }

      Node var13 = this.getLandNode(var2.x + 1, var2.y, var2.z - 1, var4, var16, Direction.NORTH);
      if (this.isDiagonalValid(var2, var10, var11, var13)) {
         var1[var3++] = var13;
      }

      Node var14 = this.getLandNode(var2.x - 1, var2.y, var2.z + 1, var4, var16, Direction.SOUTH);
      if (this.isDiagonalValid(var2, var9, var8, var14)) {
         var1[var3++] = var14;
      }

      Node var15 = this.getLandNode(var2.x + 1, var2.y, var2.z + 1, var4, var16, Direction.SOUTH);
      if (this.isDiagonalValid(var2, var10, var8, var15)) {
         var1[var3++] = var15;
      }

      return var3;
   }

   private boolean isDiagonalValid(Node var1, @Nullable Node var2, @Nullable Node var3, @Nullable Node var4) {
      if (var4 != null && var3 != null && var2 != null) {
         if (var4.closed) {
            return false;
         } else if (var3.y <= var1.y && var2.y <= var1.y) {
            return var4.costMalus >= 0.0F && (var3.y < var1.y || var3.costMalus >= 0.0F) && (var2.y < var1.y || var2.costMalus >= 0.0F);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static double getFloorLevel(BlockGetter var0, BlockPos var1) {
      BlockPos var2 = var1.below();
      VoxelShape var3 = var0.getBlockState(var2).getCollisionShape(var0, var2);
      return (double)var2.getY() + (var3.isEmpty() ? 0.0D : var3.max(Direction.Axis.Y));
   }

   @Nullable
   private Node getLandNode(int var1, int var2, int var3, int var4, double var5, Direction var7) {
      Node var8 = null;
      BlockPos var9 = new BlockPos(var1, var2, var3);
      double var10 = getFloorLevel(this.level, var9);
      if (var10 - var5 > 1.125D) {
         return null;
      } else {
         BlockPathTypes var12 = this.getBlockPathType(this.mob, var1, var2, var3);
         float var13 = this.mob.getPathfindingMalus(var12);
         double var14 = (double)this.mob.getBbWidth() / 2.0D;
         if (var13 >= 0.0F) {
            var8 = this.getNode(var1, var2, var3);
            var8.type = var12;
            var8.costMalus = Math.max(var8.costMalus, var13);
         }

         if (var12 == BlockPathTypes.WALKABLE) {
            return var8;
         } else {
            if ((var8 == null || var8.costMalus < 0.0F) && var4 > 0 && var12 != BlockPathTypes.FENCE && var12 != BlockPathTypes.TRAPDOOR) {
               var8 = this.getLandNode(var1, var2 + 1, var3, var4 - 1, var5, var7);
               if (var8 != null && (var8.type == BlockPathTypes.OPEN || var8.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0F) {
                  double var16 = (double)(var1 - var7.getStepX()) + 0.5D;
                  double var18 = (double)(var3 - var7.getStepZ()) + 0.5D;
                  AABB var20 = new AABB(var16 - var14, getFloorLevel(this.level, new BlockPos(var16, (double)(var2 + 1), var18)) + 0.001D, var18 - var14, var16 + var14, (double)this.mob.getBbHeight() + getFloorLevel(this.level, new BlockPos(var8.x, var8.y, var8.z)) - 0.002D, var18 + var14);
                  if (!this.level.noCollision(this.mob, var20)) {
                     var8 = null;
                  }
               }
            }

            if (var12 == BlockPathTypes.WATER && !this.canFloat()) {
               if (this.getBlockPathType(this.mob, var1, var2 - 1, var3) != BlockPathTypes.WATER) {
                  return var8;
               }

               while(var2 > 0) {
                  --var2;
                  var12 = this.getBlockPathType(this.mob, var1, var2, var3);
                  if (var12 != BlockPathTypes.WATER) {
                     return var8;
                  }

                  var8 = this.getNode(var1, var2, var3);
                  var8.type = var12;
                  var8.costMalus = Math.max(var8.costMalus, this.mob.getPathfindingMalus(var12));
               }
            }

            if (var12 == BlockPathTypes.OPEN) {
               AABB var21 = new AABB((double)var1 - var14 + 0.5D, (double)var2 + 0.001D, (double)var3 - var14 + 0.5D, (double)var1 + var14 + 0.5D, (double)((float)var2 + this.mob.getBbHeight()), (double)var3 + var14 + 0.5D);
               if (!this.level.noCollision(this.mob, var21)) {
                  return null;
               }

               if (this.mob.getBbWidth() >= 1.0F) {
                  BlockPathTypes var17 = this.getBlockPathType(this.mob, var1, var2 - 1, var3);
                  if (var17 == BlockPathTypes.BLOCKED) {
                     var8 = this.getNode(var1, var2, var3);
                     var8.type = BlockPathTypes.WALKABLE;
                     var8.costMalus = Math.max(var8.costMalus, var13);
                     return var8;
                  }
               }

               int var22 = 0;
               int var23 = var2;

               while(var12 == BlockPathTypes.OPEN) {
                  --var2;
                  Node var19;
                  if (var2 < 0) {
                     var19 = this.getNode(var1, var23, var3);
                     var19.type = BlockPathTypes.BLOCKED;
                     var19.costMalus = -1.0F;
                     return var19;
                  }

                  var19 = this.getNode(var1, var2, var3);
                  if (var22++ >= this.mob.getMaxFallDistance()) {
                     var19.type = BlockPathTypes.BLOCKED;
                     var19.costMalus = -1.0F;
                     return var19;
                  }

                  var12 = this.getBlockPathType(this.mob, var1, var2, var3);
                  var13 = this.mob.getPathfindingMalus(var12);
                  if (var12 != BlockPathTypes.OPEN && var13 >= 0.0F) {
                     var8 = var19;
                     var19.type = var12;
                     var19.costMalus = Math.max(var19.costMalus, var13);
                     break;
                  }

                  if (var13 < 0.0F) {
                     var19.type = BlockPathTypes.BLOCKED;
                     var19.costMalus = -1.0F;
                     return var19;
                  }
               }
            }

            return var8;
         }
      }
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      EnumSet var11 = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes var12 = BlockPathTypes.BLOCKED;
      double var13 = (double)var5.getBbWidth() / 2.0D;
      BlockPos var15 = new BlockPos(var5);
      var12 = this.getBlockPathTypes(var1, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var15);
      if (var11.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else {
         BlockPathTypes var16 = BlockPathTypes.BLOCKED;
         Iterator var17 = var11.iterator();

         while(var17.hasNext()) {
            BlockPathTypes var18 = (BlockPathTypes)var17.next();
            if (var5.getPathfindingMalus(var18) < 0.0F) {
               return var18;
            }

            if (var5.getPathfindingMalus(var18) >= var5.getPathfindingMalus(var16)) {
               var16 = var18;
            }
         }

         if (var12 == BlockPathTypes.OPEN && var5.getPathfindingMalus(var16) == 0.0F) {
            return BlockPathTypes.OPEN;
         } else {
            return var16;
         }
      }
   }

   public BlockPathTypes getBlockPathTypes(BlockGetter var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, EnumSet var10, BlockPathTypes var11, BlockPos var12) {
      for(int var13 = 0; var13 < var5; ++var13) {
         for(int var14 = 0; var14 < var6; ++var14) {
            for(int var15 = 0; var15 < var7; ++var15) {
               int var16 = var13 + var2;
               int var17 = var14 + var3;
               int var18 = var15 + var4;
               BlockPathTypes var19 = this.getBlockPathType(var1, var16, var17, var18);
               var19 = this.evaluateBlockPathType(var1, var8, var9, var12, var19);
               if (var13 == 0 && var14 == 0 && var15 == 0) {
                  var11 = var19;
               }

               var10.add(var19);
            }
         }
      }

      return var11;
   }

   protected BlockPathTypes evaluateBlockPathType(BlockGetter var1, boolean var2, boolean var3, BlockPos var4, BlockPathTypes var5) {
      if (var5 == BlockPathTypes.DOOR_WOOD_CLOSED && var2 && var3) {
         var5 = BlockPathTypes.WALKABLE;
      }

      if (var5 == BlockPathTypes.DOOR_OPEN && !var3) {
         var5 = BlockPathTypes.BLOCKED;
      }

      if (var5 == BlockPathTypes.RAIL && !(var1.getBlockState(var4).getBlock() instanceof BaseRailBlock) && !(var1.getBlockState(var4.below()).getBlock() instanceof BaseRailBlock)) {
         var5 = BlockPathTypes.FENCE;
      }

      if (var5 == BlockPathTypes.LEAVES) {
         var5 = BlockPathTypes.BLOCKED;
      }

      return var5;
   }

   private BlockPathTypes getBlockPathType(Mob var1, BlockPos var2) {
      return this.getBlockPathType(var1, var2.getX(), var2.getY(), var2.getZ());
   }

   private BlockPathTypes getBlockPathType(Mob var1, int var2, int var3, int var4) {
      return this.getBlockPathType(this.level, var2, var3, var4, var1, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
      return getBlockPathTypeStatic(var1, var2, var3, var4);
   }

   public static BlockPathTypes getBlockPathTypeStatic(BlockGetter var0, int var1, int var2, int var3) {
      BlockPathTypes var4 = getBlockPathTypeRaw(var0, var1, var2, var3);
      if (var4 == BlockPathTypes.OPEN && var2 >= 1) {
         Block var5 = var0.getBlockState(new BlockPos(var1, var2 - 1, var3)).getBlock();
         BlockPathTypes var6 = getBlockPathTypeRaw(var0, var1, var2 - 1, var3);
         var4 = var6 != BlockPathTypes.WALKABLE && var6 != BlockPathTypes.OPEN && var6 != BlockPathTypes.WATER && var6 != BlockPathTypes.LAVA ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
         if (var6 == BlockPathTypes.DAMAGE_FIRE || var5 == Blocks.MAGMA_BLOCK || var5 == Blocks.CAMPFIRE) {
            var4 = BlockPathTypes.DAMAGE_FIRE;
         }

         if (var6 == BlockPathTypes.DAMAGE_CACTUS) {
            var4 = BlockPathTypes.DAMAGE_CACTUS;
         }

         if (var6 == BlockPathTypes.DAMAGE_OTHER) {
            var4 = BlockPathTypes.DAMAGE_OTHER;
         }

         if (var6 == BlockPathTypes.STICKY_HONEY) {
            var4 = BlockPathTypes.STICKY_HONEY;
         }
      }

      if (var4 == BlockPathTypes.WALKABLE) {
         var4 = checkNeighbourBlocks(var0, var1, var2, var3, var4);
      }

      return var4;
   }

   public static BlockPathTypes checkNeighbourBlocks(BlockGetter var0, int var1, int var2, int var3, BlockPathTypes var4) {
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var6 = null;

      try {
         for(int var7 = -1; var7 <= 1; ++var7) {
            for(int var8 = -1; var8 <= 1; ++var8) {
               for(int var9 = -1; var9 <= 1; ++var9) {
                  if (var7 != 0 || var9 != 0) {
                     Block var10 = var0.getBlockState(var5.set(var7 + var1, var8 + var2, var9 + var3)).getBlock();
                     if (var10 == Blocks.CACTUS) {
                        var4 = BlockPathTypes.DANGER_CACTUS;
                     } else if (var10 != Blocks.FIRE && var10 != Blocks.LAVA) {
                        if (var10 == Blocks.SWEET_BERRY_BUSH) {
                           var4 = BlockPathTypes.DANGER_OTHER;
                        }
                     } else {
                        var4 = BlockPathTypes.DANGER_FIRE;
                     }
                  }
               }
            }
         }
      } catch (Throwable var18) {
         var6 = var18;
         throw var18;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var17) {
                  var6.addSuppressed(var17);
               }
            } else {
               var5.close();
            }
         }

      }

      return var4;
   }

   protected static BlockPathTypes getBlockPathTypeRaw(BlockGetter var0, int var1, int var2, int var3) {
      BlockPos var4 = new BlockPos(var1, var2, var3);
      BlockState var5 = var0.getBlockState(var4);
      Block var6 = var5.getBlock();
      Material var7 = var5.getMaterial();
      if (var5.isAir()) {
         return BlockPathTypes.OPEN;
      } else if (!var6.is(BlockTags.TRAPDOORS) && var6 != Blocks.LILY_PAD) {
         if (var6 == Blocks.FIRE) {
            return BlockPathTypes.DAMAGE_FIRE;
         } else if (var6 == Blocks.CACTUS) {
            return BlockPathTypes.DAMAGE_CACTUS;
         } else if (var6 == Blocks.SWEET_BERRY_BUSH) {
            return BlockPathTypes.DAMAGE_OTHER;
         } else if (var6 == Blocks.HONEY_BLOCK) {
            return BlockPathTypes.STICKY_HONEY;
         } else if (var6 == Blocks.COCOA) {
            return BlockPathTypes.COCOA;
         } else if (var6 instanceof DoorBlock && var7 == Material.WOOD && !(Boolean)var5.getValue(DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_WOOD_CLOSED;
         } else if (var6 instanceof DoorBlock && var7 == Material.METAL && !(Boolean)var5.getValue(DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_IRON_CLOSED;
         } else if (var6 instanceof DoorBlock && (Boolean)var5.getValue(DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_OPEN;
         } else if (var6 instanceof BaseRailBlock) {
            return BlockPathTypes.RAIL;
         } else if (var6 instanceof LeavesBlock) {
            return BlockPathTypes.LEAVES;
         } else if (!var6.is(BlockTags.FENCES) && !var6.is(BlockTags.WALLS) && (!(var6 instanceof FenceGateBlock) || (Boolean)var5.getValue(FenceGateBlock.OPEN))) {
            FluidState var8 = var0.getFluidState(var4);
            if (var8.is(FluidTags.WATER)) {
               return BlockPathTypes.WATER;
            } else if (var8.is(FluidTags.LAVA)) {
               return BlockPathTypes.LAVA;
            } else {
               return var5.isPathfindable(var0, var4, PathComputationType.LAND) ? BlockPathTypes.OPEN : BlockPathTypes.BLOCKED;
            }
         } else {
            return BlockPathTypes.FENCE;
         }
      } else {
         return BlockPathTypes.TRAPDOOR;
      }
   }
}
