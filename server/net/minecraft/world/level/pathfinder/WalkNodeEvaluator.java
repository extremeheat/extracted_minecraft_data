package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WalkNodeEvaluator extends NodeEvaluator {
   protected float oldWaterCost;
   private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap();
   private final Object2BooleanMap<AABB> collisionCache = new Object2BooleanOpenHashMap();

   public WalkNodeEvaluator() {
      super();
   }

   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      this.oldWaterCost = var2.getPathfindingMalus(BlockPathTypes.WATER);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
      this.pathTypesByPosCache.clear();
      this.collisionCache.clear();
      super.done();
   }

   public Node getStart() {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      int var1 = Mth.floor(this.mob.getY());
      BlockState var3 = this.level.getBlockState(var2.set(this.mob.getX(), (double)var1, this.mob.getZ()));
      BlockPos var4;
      if (!this.mob.canStandOnFluid(var3.getFluidState().getType())) {
         if (this.canFloat() && this.mob.isInWater()) {
            while(true) {
               if (var3.getBlock() != Blocks.WATER && var3.getFluidState() != Fluids.WATER.getSource(false)) {
                  --var1;
                  break;
               }

               ++var1;
               var3 = this.level.getBlockState(var2.set(this.mob.getX(), (double)var1, this.mob.getZ()));
            }
         } else if (this.mob.isOnGround()) {
            var1 = Mth.floor(this.mob.getY() + 0.5D);
         } else {
            for(var4 = this.mob.blockPosition(); (this.level.getBlockState(var4).isAir() || this.level.getBlockState(var4).isPathfindable(this.level, var4, PathComputationType.LAND)) && var4.getY() > 0; var4 = var4.below()) {
            }

            var1 = var4.above().getY();
         }
      } else {
         while(true) {
            if (!this.mob.canStandOnFluid(var3.getFluidState().getType())) {
               --var1;
               break;
            }

            ++var1;
            var3 = this.level.getBlockState(var2.set(this.mob.getX(), (double)var1, this.mob.getZ()));
         }
      }

      var4 = this.mob.blockPosition();
      BlockPathTypes var5 = this.getCachedBlockType(this.mob, var4.getX(), var1, var4.getZ());
      if (this.mob.getPathfindingMalus(var5) < 0.0F) {
         AABB var6 = this.mob.getBoundingBox();
         if (this.hasPositiveMalus(var2.set(var6.minX, (double)var1, var6.minZ)) || this.hasPositiveMalus(var2.set(var6.minX, (double)var1, var6.maxZ)) || this.hasPositiveMalus(var2.set(var6.maxX, (double)var1, var6.minZ)) || this.hasPositiveMalus(var2.set(var6.maxX, (double)var1, var6.maxZ))) {
            Node var7 = this.getNode(var2);
            var7.type = this.getBlockPathType(this.mob, var7.asBlockPos());
            var7.costMalus = this.mob.getPathfindingMalus(var7.type);
            return var7;
         }
      }

      Node var8 = this.getNode(var4.getX(), var1, var4.getZ());
      var8.type = this.getBlockPathType(this.mob, var8.asBlockPos());
      var8.costMalus = this.mob.getPathfindingMalus(var8.type);
      return var8;
   }

   private boolean hasPositiveMalus(BlockPos var1) {
      BlockPathTypes var2 = this.getBlockPathType(this.mob, var1);
      return this.mob.getPathfindingMalus(var2) >= 0.0F;
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(this.getNode(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5)));
   }

   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      int var4 = 0;
      BlockPathTypes var5 = this.getCachedBlockType(this.mob, var2.x, var2.y + 1, var2.z);
      BlockPathTypes var6 = this.getCachedBlockType(this.mob, var2.x, var2.y, var2.z);
      if (this.mob.getPathfindingMalus(var5) >= 0.0F && var6 != BlockPathTypes.STICKY_HONEY) {
         var4 = Mth.floor(Math.max(1.0F, this.mob.maxUpStep));
      }

      double var7 = getFloorLevel(this.level, new BlockPos(var2.x, var2.y, var2.z));
      Node var9 = this.getLandNode(var2.x, var2.y, var2.z + 1, var4, var7, Direction.SOUTH, var6);
      if (this.isNeighborValid(var9, var2)) {
         var1[var3++] = var9;
      }

      Node var10 = this.getLandNode(var2.x - 1, var2.y, var2.z, var4, var7, Direction.WEST, var6);
      if (this.isNeighborValid(var10, var2)) {
         var1[var3++] = var10;
      }

      Node var11 = this.getLandNode(var2.x + 1, var2.y, var2.z, var4, var7, Direction.EAST, var6);
      if (this.isNeighborValid(var11, var2)) {
         var1[var3++] = var11;
      }

      Node var12 = this.getLandNode(var2.x, var2.y, var2.z - 1, var4, var7, Direction.NORTH, var6);
      if (this.isNeighborValid(var12, var2)) {
         var1[var3++] = var12;
      }

      Node var13 = this.getLandNode(var2.x - 1, var2.y, var2.z - 1, var4, var7, Direction.NORTH, var6);
      if (this.isDiagonalValid(var2, var10, var12, var13)) {
         var1[var3++] = var13;
      }

      Node var14 = this.getLandNode(var2.x + 1, var2.y, var2.z - 1, var4, var7, Direction.NORTH, var6);
      if (this.isDiagonalValid(var2, var11, var12, var14)) {
         var1[var3++] = var14;
      }

      Node var15 = this.getLandNode(var2.x - 1, var2.y, var2.z + 1, var4, var7, Direction.SOUTH, var6);
      if (this.isDiagonalValid(var2, var10, var9, var15)) {
         var1[var3++] = var15;
      }

      Node var16 = this.getLandNode(var2.x + 1, var2.y, var2.z + 1, var4, var7, Direction.SOUTH, var6);
      if (this.isDiagonalValid(var2, var11, var9, var16)) {
         var1[var3++] = var16;
      }

      return var3;
   }

   private boolean isNeighborValid(Node var1, Node var2) {
      return var1 != null && !var1.closed && (var1.costMalus >= 0.0F || var2.costMalus < 0.0F);
   }

   private boolean isDiagonalValid(Node var1, @Nullable Node var2, @Nullable Node var3, @Nullable Node var4) {
      if (var4 != null && var3 != null && var2 != null) {
         if (var4.closed) {
            return false;
         } else if (var3.y <= var1.y && var2.y <= var1.y) {
            if (var2.type != BlockPathTypes.WALKABLE_DOOR && var3.type != BlockPathTypes.WALKABLE_DOOR && var4.type != BlockPathTypes.WALKABLE_DOOR) {
               boolean var5 = var3.type == BlockPathTypes.FENCE && var2.type == BlockPathTypes.FENCE && (double)this.mob.getBbWidth() < 0.5D;
               return var4.costMalus >= 0.0F && (var3.y < var1.y || var3.costMalus >= 0.0F || var5) && (var2.y < var1.y || var2.costMalus >= 0.0F || var5);
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean canReachWithoutCollision(Node var1) {
      Vec3 var2 = new Vec3((double)var1.x - this.mob.getX(), (double)var1.y - this.mob.getY(), (double)var1.z - this.mob.getZ());
      AABB var3 = this.mob.getBoundingBox();
      int var4 = Mth.ceil(var2.length() / var3.getSize());
      var2 = var2.scale((double)(1.0F / (float)var4));

      for(int var5 = 1; var5 <= var4; ++var5) {
         var3 = var3.move(var2);
         if (this.hasCollisions(var3)) {
            return false;
         }
      }

      return true;
   }

   public static double getFloorLevel(BlockGetter var0, BlockPos var1) {
      BlockPos var2 = var1.below();
      VoxelShape var3 = var0.getBlockState(var2).getCollisionShape(var0, var2);
      return (double)var2.getY() + (var3.isEmpty() ? 0.0D : var3.max(Direction.Axis.Y));
   }

   @Nullable
   private Node getLandNode(int var1, int var2, int var3, int var4, double var5, Direction var7, BlockPathTypes var8) {
      Node var9 = null;
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
      double var11 = getFloorLevel(this.level, var10.set(var1, var2, var3));
      if (var11 - var5 > 1.125D) {
         return null;
      } else {
         BlockPathTypes var13 = this.getCachedBlockType(this.mob, var1, var2, var3);
         float var14 = this.mob.getPathfindingMalus(var13);
         double var15 = (double)this.mob.getBbWidth() / 2.0D;
         if (var14 >= 0.0F) {
            var9 = this.getNode(var1, var2, var3);
            var9.type = var13;
            var9.costMalus = Math.max(var9.costMalus, var14);
         }

         if (var8 == BlockPathTypes.FENCE && var9 != null && var9.costMalus >= 0.0F && !this.canReachWithoutCollision(var9)) {
            var9 = null;
         }

         if (var13 == BlockPathTypes.WALKABLE) {
            return var9;
         } else {
            if ((var9 == null || var9.costMalus < 0.0F) && var4 > 0 && var13 != BlockPathTypes.FENCE && var13 != BlockPathTypes.UNPASSABLE_RAIL && var13 != BlockPathTypes.TRAPDOOR) {
               var9 = this.getLandNode(var1, var2 + 1, var3, var4 - 1, var5, var7, var8);
               if (var9 != null && (var9.type == BlockPathTypes.OPEN || var9.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0F) {
                  double var17 = (double)(var1 - var7.getStepX()) + 0.5D;
                  double var19 = (double)(var3 - var7.getStepZ()) + 0.5D;
                  AABB var21 = new AABB(var17 - var15, getFloorLevel(this.level, var10.set(var17, (double)(var2 + 1), var19)) + 0.001D, var19 - var15, var17 + var15, (double)this.mob.getBbHeight() + getFloorLevel(this.level, var10.set((double)var9.x, (double)var9.y, (double)var9.z)) - 0.002D, var19 + var15);
                  if (this.hasCollisions(var21)) {
                     var9 = null;
                  }
               }
            }

            if (var13 == BlockPathTypes.WATER && !this.canFloat()) {
               if (this.getCachedBlockType(this.mob, var1, var2 - 1, var3) != BlockPathTypes.WATER) {
                  return var9;
               }

               while(var2 > 0) {
                  --var2;
                  var13 = this.getCachedBlockType(this.mob, var1, var2, var3);
                  if (var13 != BlockPathTypes.WATER) {
                     return var9;
                  }

                  var9 = this.getNode(var1, var2, var3);
                  var9.type = var13;
                  var9.costMalus = Math.max(var9.costMalus, this.mob.getPathfindingMalus(var13));
               }
            }

            if (var13 == BlockPathTypes.OPEN) {
               int var22 = 0;
               int var18 = var2;

               while(var13 == BlockPathTypes.OPEN) {
                  --var2;
                  Node var23;
                  if (var2 < 0) {
                     var23 = this.getNode(var1, var18, var3);
                     var23.type = BlockPathTypes.BLOCKED;
                     var23.costMalus = -1.0F;
                     return var23;
                  }

                  if (var22++ >= this.mob.getMaxFallDistance()) {
                     var23 = this.getNode(var1, var2, var3);
                     var23.type = BlockPathTypes.BLOCKED;
                     var23.costMalus = -1.0F;
                     return var23;
                  }

                  var13 = this.getCachedBlockType(this.mob, var1, var2, var3);
                  var14 = this.mob.getPathfindingMalus(var13);
                  if (var13 != BlockPathTypes.OPEN && var14 >= 0.0F) {
                     var9 = this.getNode(var1, var2, var3);
                     var9.type = var13;
                     var9.costMalus = Math.max(var9.costMalus, var14);
                     break;
                  }

                  if (var14 < 0.0F) {
                     var23 = this.getNode(var1, var2, var3);
                     var23.type = BlockPathTypes.BLOCKED;
                     var23.costMalus = -1.0F;
                     return var23;
                  }
               }
            }

            if (var13 == BlockPathTypes.FENCE) {
               var9 = this.getNode(var1, var2, var3);
               var9.closed = true;
               var9.type = var13;
               var9.costMalus = var13.getMalus();
            }

            return var9;
         }
      }
   }

   private boolean hasCollisions(AABB var1) {
      return (Boolean)this.collisionCache.computeIfAbsent(var1, (var2) -> {
         return !this.level.noCollision(this.mob, var1);
      });
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      EnumSet var11 = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes var12 = BlockPathTypes.BLOCKED;
      BlockPos var13 = var5.blockPosition();
      var12 = this.getBlockPathTypes(var1, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var13);
      if (var11.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else if (var11.contains(BlockPathTypes.UNPASSABLE_RAIL)) {
         return BlockPathTypes.UNPASSABLE_RAIL;
      } else {
         BlockPathTypes var14 = BlockPathTypes.BLOCKED;
         Iterator var15 = var11.iterator();

         while(var15.hasNext()) {
            BlockPathTypes var16 = (BlockPathTypes)var15.next();
            if (var5.getPathfindingMalus(var16) < 0.0F) {
               return var16;
            }

            if (var5.getPathfindingMalus(var16) >= var5.getPathfindingMalus(var14)) {
               var14 = var16;
            }
         }

         if (var12 == BlockPathTypes.OPEN && var5.getPathfindingMalus(var14) == 0.0F && var6 <= 1) {
            return BlockPathTypes.OPEN;
         } else {
            return var14;
         }
      }
   }

   public BlockPathTypes getBlockPathTypes(BlockGetter var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, EnumSet<BlockPathTypes> var10, BlockPathTypes var11, BlockPos var12) {
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
         var5 = BlockPathTypes.WALKABLE_DOOR;
      }

      if (var5 == BlockPathTypes.DOOR_OPEN && !var3) {
         var5 = BlockPathTypes.BLOCKED;
      }

      if (var5 == BlockPathTypes.RAIL && !(var1.getBlockState(var4).getBlock() instanceof BaseRailBlock) && !(var1.getBlockState(var4.below()).getBlock() instanceof BaseRailBlock)) {
         var5 = BlockPathTypes.UNPASSABLE_RAIL;
      }

      if (var5 == BlockPathTypes.LEAVES) {
         var5 = BlockPathTypes.BLOCKED;
      }

      return var5;
   }

   private BlockPathTypes getBlockPathType(Mob var1, BlockPos var2) {
      return this.getCachedBlockType(var1, var2.getX(), var2.getY(), var2.getZ());
   }

   private BlockPathTypes getCachedBlockType(Mob var1, int var2, int var3, int var4) {
      return (BlockPathTypes)this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong(var2, var3, var4), (var5) -> {
         return this.getBlockPathType(this.level, var2, var3, var4, var1, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
      });
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
      return getBlockPathTypeStatic(var1, new BlockPos.MutableBlockPos(var2, var3, var4));
   }

   public static BlockPathTypes getBlockPathTypeStatic(BlockGetter var0, BlockPos.MutableBlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      BlockPathTypes var5 = getBlockPathTypeRaw(var0, var1);
      if (var5 == BlockPathTypes.OPEN && var3 >= 1) {
         BlockPathTypes var6 = getBlockPathTypeRaw(var0, var1.set(var2, var3 - 1, var4));
         var5 = var6 != BlockPathTypes.WALKABLE && var6 != BlockPathTypes.OPEN && var6 != BlockPathTypes.WATER && var6 != BlockPathTypes.LAVA ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
         if (var6 == BlockPathTypes.DAMAGE_FIRE) {
            var5 = BlockPathTypes.DAMAGE_FIRE;
         }

         if (var6 == BlockPathTypes.DAMAGE_CACTUS) {
            var5 = BlockPathTypes.DAMAGE_CACTUS;
         }

         if (var6 == BlockPathTypes.DAMAGE_OTHER) {
            var5 = BlockPathTypes.DAMAGE_OTHER;
         }

         if (var6 == BlockPathTypes.STICKY_HONEY) {
            var5 = BlockPathTypes.STICKY_HONEY;
         }
      }

      if (var5 == BlockPathTypes.WALKABLE) {
         var5 = checkNeighbourBlocks(var0, var1.set(var2, var3, var4), var5);
      }

      return var5;
   }

   public static BlockPathTypes checkNeighbourBlocks(BlockGetter var0, BlockPos.MutableBlockPos var1, BlockPathTypes var2) {
      int var3 = var1.getX();
      int var4 = var1.getY();
      int var5 = var1.getZ();

      for(int var6 = -1; var6 <= 1; ++var6) {
         for(int var7 = -1; var7 <= 1; ++var7) {
            for(int var8 = -1; var8 <= 1; ++var8) {
               if (var6 != 0 || var8 != 0) {
                  var1.set(var3 + var6, var4 + var7, var5 + var8);
                  BlockState var9 = var0.getBlockState(var1);
                  if (var9.is(Blocks.CACTUS)) {
                     return BlockPathTypes.DANGER_CACTUS;
                  }

                  if (var9.is(Blocks.SWEET_BERRY_BUSH)) {
                     return BlockPathTypes.DANGER_OTHER;
                  }

                  if (isBurningBlock(var9)) {
                     return BlockPathTypes.DANGER_FIRE;
                  }

                  if (var0.getFluidState(var1).is(FluidTags.WATER)) {
                     return BlockPathTypes.WATER_BORDER;
                  }
               }
            }
         }
      }

      return var2;
   }

   protected static BlockPathTypes getBlockPathTypeRaw(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      Block var3 = var2.getBlock();
      Material var4 = var2.getMaterial();
      if (var2.isAir()) {
         return BlockPathTypes.OPEN;
      } else if (!var2.is(BlockTags.TRAPDOORS) && !var2.is(Blocks.LILY_PAD)) {
         if (var2.is(Blocks.CACTUS)) {
            return BlockPathTypes.DAMAGE_CACTUS;
         } else if (var2.is(Blocks.SWEET_BERRY_BUSH)) {
            return BlockPathTypes.DAMAGE_OTHER;
         } else if (var2.is(Blocks.HONEY_BLOCK)) {
            return BlockPathTypes.STICKY_HONEY;
         } else if (var2.is(Blocks.COCOA)) {
            return BlockPathTypes.COCOA;
         } else {
            FluidState var5 = var0.getFluidState(var1);
            if (var5.is(FluidTags.WATER)) {
               return BlockPathTypes.WATER;
            } else if (var5.is(FluidTags.LAVA)) {
               return BlockPathTypes.LAVA;
            } else if (isBurningBlock(var2)) {
               return BlockPathTypes.DAMAGE_FIRE;
            } else if (DoorBlock.isWoodenDoor(var2) && !(Boolean)var2.getValue(DoorBlock.OPEN)) {
               return BlockPathTypes.DOOR_WOOD_CLOSED;
            } else if (var3 instanceof DoorBlock && var4 == Material.METAL && !(Boolean)var2.getValue(DoorBlock.OPEN)) {
               return BlockPathTypes.DOOR_IRON_CLOSED;
            } else if (var3 instanceof DoorBlock && (Boolean)var2.getValue(DoorBlock.OPEN)) {
               return BlockPathTypes.DOOR_OPEN;
            } else if (var3 instanceof BaseRailBlock) {
               return BlockPathTypes.RAIL;
            } else if (var3 instanceof LeavesBlock) {
               return BlockPathTypes.LEAVES;
            } else if (!var3.is((Tag)BlockTags.FENCES) && !var3.is((Tag)BlockTags.WALLS) && (!(var3 instanceof FenceGateBlock) || (Boolean)var2.getValue(FenceGateBlock.OPEN))) {
               return !var2.isPathfindable(var0, var1, PathComputationType.LAND) ? BlockPathTypes.BLOCKED : BlockPathTypes.OPEN;
            } else {
               return BlockPathTypes.FENCE;
            }
         }
      } else {
         return BlockPathTypes.TRAPDOOR;
      }
   }

   private static boolean isBurningBlock(BlockState var0) {
      return var0.is(BlockTags.FIRE) || var0.is(Blocks.LAVA) || var0.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(var0);
   }
}
