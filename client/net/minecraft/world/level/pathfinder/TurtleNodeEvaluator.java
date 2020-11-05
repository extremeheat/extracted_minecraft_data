package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleNodeEvaluator extends WalkNodeEvaluator {
   private float oldWalkableCost;
   private float oldWaterBorderCost;

   public TurtleNodeEvaluator() {
      super();
   }

   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      var2.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.oldWalkableCost = var2.getPathfindingMalus(BlockPathTypes.WALKABLE);
      var2.setPathfindingMalus(BlockPathTypes.WALKABLE, 6.0F);
      this.oldWaterBorderCost = var2.getPathfindingMalus(BlockPathTypes.WATER_BORDER);
      var2.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 4.0F);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WALKABLE, this.oldWalkableCost);
      this.mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, this.oldWaterBorderCost);
      super.done();
   }

   public Node getStart() {
      return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(this.getNode(Mth.floor(var1), Mth.floor(var3 + 0.5D), Mth.floor(var5)));
   }

   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      boolean var4 = true;
      BlockPos var5 = new BlockPos(var2.x, var2.y, var2.z);
      double var6 = this.inWaterDependentPosHeight(var5);
      Node var8 = this.getAcceptedNode(var2.x, var2.y, var2.z + 1, 1, var6);
      Node var9 = this.getAcceptedNode(var2.x - 1, var2.y, var2.z, 1, var6);
      Node var10 = this.getAcceptedNode(var2.x + 1, var2.y, var2.z, 1, var6);
      Node var11 = this.getAcceptedNode(var2.x, var2.y, var2.z - 1, 1, var6);
      Node var12 = this.getAcceptedNode(var2.x, var2.y + 1, var2.z, 0, var6);
      Node var13 = this.getAcceptedNode(var2.x, var2.y - 1, var2.z, 1, var6);
      if (var8 != null && !var8.closed) {
         var1[var3++] = var8;
      }

      if (var9 != null && !var9.closed) {
         var1[var3++] = var9;
      }

      if (var10 != null && !var10.closed) {
         var1[var3++] = var10;
      }

      if (var11 != null && !var11.closed) {
         var1[var3++] = var11;
      }

      if (var12 != null && !var12.closed) {
         var1[var3++] = var12;
      }

      if (var13 != null && !var13.closed) {
         var1[var3++] = var13;
      }

      boolean var14 = var11 == null || var11.type == BlockPathTypes.OPEN || var11.costMalus != 0.0F;
      boolean var15 = var8 == null || var8.type == BlockPathTypes.OPEN || var8.costMalus != 0.0F;
      boolean var16 = var10 == null || var10.type == BlockPathTypes.OPEN || var10.costMalus != 0.0F;
      boolean var17 = var9 == null || var9.type == BlockPathTypes.OPEN || var9.costMalus != 0.0F;
      Node var18;
      if (var14 && var17) {
         var18 = this.getAcceptedNode(var2.x - 1, var2.y, var2.z - 1, 1, var6);
         if (var18 != null && !var18.closed) {
            var1[var3++] = var18;
         }
      }

      if (var14 && var16) {
         var18 = this.getAcceptedNode(var2.x + 1, var2.y, var2.z - 1, 1, var6);
         if (var18 != null && !var18.closed) {
            var1[var3++] = var18;
         }
      }

      if (var15 && var17) {
         var18 = this.getAcceptedNode(var2.x - 1, var2.y, var2.z + 1, 1, var6);
         if (var18 != null && !var18.closed) {
            var1[var3++] = var18;
         }
      }

      if (var15 && var16) {
         var18 = this.getAcceptedNode(var2.x + 1, var2.y, var2.z + 1, 1, var6);
         if (var18 != null && !var18.closed) {
            var1[var3++] = var18;
         }
      }

      return var3;
   }

   private double inWaterDependentPosHeight(BlockPos var1) {
      if (!this.mob.isInWater()) {
         BlockPos var2 = var1.below();
         VoxelShape var3 = this.level.getBlockState(var2).getCollisionShape(this.level, var2);
         return (double)var2.getY() + (var3.isEmpty() ? 0.0D : var3.max(Direction.Axis.Y));
      } else {
         return (double)var1.getY() + 0.5D;
      }
   }

   @Nullable
   private Node getAcceptedNode(int var1, int var2, int var3, int var4, double var5) {
      Node var7 = null;
      BlockPos var8 = new BlockPos(var1, var2, var3);
      double var9 = this.inWaterDependentPosHeight(var8);
      if (var9 - var5 > 1.125D) {
         return null;
      } else {
         BlockPathTypes var11 = this.getBlockPathType(this.level, var1, var2, var3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
         float var12 = this.mob.getPathfindingMalus(var11);
         double var13 = (double)this.mob.getBbWidth() / 2.0D;
         if (var12 >= 0.0F) {
            var7 = this.getNode(var1, var2, var3);
            var7.type = var11;
            var7.costMalus = Math.max(var7.costMalus, var12);
         }

         if (var11 != BlockPathTypes.WATER && var11 != BlockPathTypes.WALKABLE) {
            if (var7 == null && var4 > 0 && var11 != BlockPathTypes.FENCE && var11 != BlockPathTypes.UNPASSABLE_RAIL && var11 != BlockPathTypes.TRAPDOOR) {
               var7 = this.getAcceptedNode(var1, var2 + 1, var3, var4 - 1, var5);
            }

            if (var11 == BlockPathTypes.OPEN) {
               AABB var15 = new AABB((double)var1 - var13 + 0.5D, (double)var2 + 0.001D, (double)var3 - var13 + 0.5D, (double)var1 + var13 + 0.5D, (double)((float)var2 + this.mob.getBbHeight()), (double)var3 + var13 + 0.5D);
               if (!this.mob.level.noCollision(this.mob, var15)) {
                  return null;
               }

               BlockPathTypes var16 = this.getBlockPathType(this.level, var1, var2 - 1, var3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
               if (var16 == BlockPathTypes.BLOCKED) {
                  var7 = this.getNode(var1, var2, var3);
                  var7.type = BlockPathTypes.WALKABLE;
                  var7.costMalus = Math.max(var7.costMalus, var12);
                  return var7;
               }

               if (var16 == BlockPathTypes.WATER) {
                  var7 = this.getNode(var1, var2, var3);
                  var7.type = BlockPathTypes.WATER;
                  var7.costMalus = Math.max(var7.costMalus, var12);
                  return var7;
               }

               int var17 = 0;

               while(var2 > this.level.getMinBuildHeight() && var11 == BlockPathTypes.OPEN) {
                  --var2;
                  if (var17++ >= this.mob.getMaxFallDistance()) {
                     return null;
                  }

                  var11 = this.getBlockPathType(this.level, var1, var2, var3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
                  var12 = this.mob.getPathfindingMalus(var11);
                  if (var11 != BlockPathTypes.OPEN && var12 >= 0.0F) {
                     var7 = this.getNode(var1, var2, var3);
                     var7.type = var11;
                     var7.costMalus = Math.max(var7.costMalus, var12);
                     break;
                  }

                  if (var12 < 0.0F) {
                     return null;
                  }
               }
            }

            return var7;
         } else {
            if (var2 < this.mob.level.getSeaLevel() - 10 && var7 != null) {
               ++var7.costMalus;
            }

            return var7;
         }
      }
   }

   protected BlockPathTypes evaluateBlockPathType(BlockGetter var1, boolean var2, boolean var3, BlockPos var4, BlockPathTypes var5) {
      if (var5 == BlockPathTypes.RAIL && !(var1.getBlockState(var4).getBlock() instanceof BaseRailBlock) && !(var1.getBlockState(var4.below()).getBlock() instanceof BaseRailBlock)) {
         var5 = BlockPathTypes.UNPASSABLE_RAIL;
      }

      if (var5 == BlockPathTypes.DOOR_OPEN || var5 == BlockPathTypes.DOOR_WOOD_CLOSED || var5 == BlockPathTypes.DOOR_IRON_CLOSED) {
         var5 = BlockPathTypes.BLOCKED;
      }

      if (var5 == BlockPathTypes.LEAVES) {
         var5 = BlockPathTypes.BLOCKED;
      }

      return var5;
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
      BlockPathTypes var6 = getBlockPathTypeRaw(var1, var5.set(var2, var3, var4));
      if (var6 == BlockPathTypes.WATER) {
         Direction[] var12 = Direction.values();
         int var13 = var12.length;

         for(int var9 = 0; var9 < var13; ++var9) {
            Direction var10 = var12[var9];
            BlockPathTypes var11 = getBlockPathTypeRaw(var1, var5.set(var2, var3, var4).move(var10));
            if (var11 == BlockPathTypes.BLOCKED) {
               return BlockPathTypes.WATER_BORDER;
            }
         }

         return BlockPathTypes.WATER;
      } else {
         if (var6 == BlockPathTypes.OPEN && var3 >= 1) {
            BlockState var7 = var1.getBlockState(new BlockPos(var2, var3 - 1, var4));
            BlockPathTypes var8 = getBlockPathTypeRaw(var1, var5.set(var2, var3 - 1, var4));
            if (var8 != BlockPathTypes.WALKABLE && var8 != BlockPathTypes.OPEN && var8 != BlockPathTypes.LAVA) {
               var6 = BlockPathTypes.WALKABLE;
            } else {
               var6 = BlockPathTypes.OPEN;
            }

            if (var8 == BlockPathTypes.DAMAGE_FIRE || var7.is(Blocks.MAGMA_BLOCK) || var7.is(BlockTags.CAMPFIRES)) {
               var6 = BlockPathTypes.DAMAGE_FIRE;
            }

            if (var8 == BlockPathTypes.DAMAGE_CACTUS) {
               var6 = BlockPathTypes.DAMAGE_CACTUS;
            }

            if (var8 == BlockPathTypes.DAMAGE_OTHER) {
               var6 = BlockPathTypes.DAMAGE_OTHER;
            }
         }

         if (var6 == BlockPathTypes.WALKABLE) {
            var6 = checkNeighbourBlocks(var1, var5.set(var2, var3, var4), var6);
         }

         return var6;
      }
   }
}
