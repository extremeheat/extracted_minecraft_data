package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
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
   public static final double SPACE_BETWEEN_WALL_POSTS = 0.5;
   private static final double DEFAULT_MOB_JUMP_HEIGHT = 1.125;
   protected float oldWaterCost;
   private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap();
   private final Object2BooleanMap<AABB> collisionCache = new Object2BooleanOpenHashMap();

   public WalkNodeEvaluator() {
      super();
   }

   @Override
   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      this.oldWaterCost = var2.getPathfindingMalus(BlockPathTypes.WATER);
   }

   @Override
   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
      this.pathTypesByPosCache.clear();
      this.collisionCache.clear();
      super.done();
   }

   @Override
   public Node getStart() {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      int var1 = this.mob.getBlockY();
      BlockState var3 = this.level.getBlockState(var2.set(this.mob.getX(), (double)var1, this.mob.getZ()));
      if (!this.mob.canStandOnFluid(var3.getFluidState())) {
         if (this.canFloat() && this.mob.isInWater()) {
            while(true) {
               if (!var3.is(Blocks.WATER) && var3.getFluidState() != Fluids.WATER.getSource(false)) {
                  --var1;
                  break;
               }

               var3 = this.level.getBlockState(var2.set(this.mob.getX(), (double)(++var1), this.mob.getZ()));
            }
         } else if (this.mob.isOnGround()) {
            var1 = Mth.floor(this.mob.getY() + 0.5);
         } else {
            BlockPos var4 = this.mob.blockPosition();

            while(
               (this.level.getBlockState(var4).isAir() || this.level.getBlockState(var4).isPathfindable(this.level, var4, PathComputationType.LAND))
                  && var4.getY() > this.mob.level.getMinBuildHeight()
            ) {
               var4 = var4.below();
            }

            var1 = var4.above().getY();
         }
      } else {
         while(this.mob.canStandOnFluid(var3.getFluidState())) {
            var3 = this.level.getBlockState(var2.set(this.mob.getX(), (double)(++var1), this.mob.getZ()));
         }

         --var1;
      }

      BlockPos var7 = this.mob.blockPosition();
      if (!this.canStartAt(var2.set(var7.getX(), var1, var7.getZ()))) {
         AABB var5 = this.mob.getBoundingBox();
         if (this.canStartAt(var2.set(var5.minX, (double)var1, var5.minZ))
            || this.canStartAt(var2.set(var5.minX, (double)var1, var5.maxZ))
            || this.canStartAt(var2.set(var5.maxX, (double)var1, var5.minZ))
            || this.canStartAt(var2.set(var5.maxX, (double)var1, var5.maxZ))) {
            return this.getStartNode(var2);
         }
      }

      return this.getStartNode(new BlockPos(var7.getX(), var1, var7.getZ()));
   }

   protected Node getStartNode(BlockPos var1) {
      Node var2 = this.getNode(var1);
      var2.type = this.getBlockPathType(this.mob, var2.asBlockPos());
      var2.costMalus = this.mob.getPathfindingMalus(var2.type);
      return var2;
   }

   protected boolean canStartAt(BlockPos var1) {
      BlockPathTypes var2 = this.getBlockPathType(this.mob, var1);
      return var2 != BlockPathTypes.OPEN && this.mob.getPathfindingMalus(var2) >= 0.0F;
   }

   @Override
   public Target getGoal(double var1, double var3, double var5) {
      return this.getTargetFromNode(this.getNode(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5)));
   }

   @Override
   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      int var4 = 0;
      BlockPathTypes var5 = this.getCachedBlockType(this.mob, var2.x, var2.y + 1, var2.z);
      BlockPathTypes var6 = this.getCachedBlockType(this.mob, var2.x, var2.y, var2.z);
      if (this.mob.getPathfindingMalus(var5) >= 0.0F && var6 != BlockPathTypes.STICKY_HONEY) {
         var4 = Mth.floor(Math.max(1.0F, this.mob.maxUpStep()));
      }

      double var7 = this.getFloorLevel(new BlockPos(var2.x, var2.y, var2.z));
      Node var9 = this.findAcceptedNode(var2.x, var2.y, var2.z + 1, var4, var7, Direction.SOUTH, var6);
      if (this.isNeighborValid(var9, var2)) {
         var1[var3++] = var9;
      }

      Node var10 = this.findAcceptedNode(var2.x - 1, var2.y, var2.z, var4, var7, Direction.WEST, var6);
      if (this.isNeighborValid(var10, var2)) {
         var1[var3++] = var10;
      }

      Node var11 = this.findAcceptedNode(var2.x + 1, var2.y, var2.z, var4, var7, Direction.EAST, var6);
      if (this.isNeighborValid(var11, var2)) {
         var1[var3++] = var11;
      }

      Node var12 = this.findAcceptedNode(var2.x, var2.y, var2.z - 1, var4, var7, Direction.NORTH, var6);
      if (this.isNeighborValid(var12, var2)) {
         var1[var3++] = var12;
      }

      Node var13 = this.findAcceptedNode(var2.x - 1, var2.y, var2.z - 1, var4, var7, Direction.NORTH, var6);
      if (this.isDiagonalValid(var2, var10, var12, var13)) {
         var1[var3++] = var13;
      }

      Node var14 = this.findAcceptedNode(var2.x + 1, var2.y, var2.z - 1, var4, var7, Direction.NORTH, var6);
      if (this.isDiagonalValid(var2, var11, var12, var14)) {
         var1[var3++] = var14;
      }

      Node var15 = this.findAcceptedNode(var2.x - 1, var2.y, var2.z + 1, var4, var7, Direction.SOUTH, var6);
      if (this.isDiagonalValid(var2, var10, var9, var15)) {
         var1[var3++] = var15;
      }

      Node var16 = this.findAcceptedNode(var2.x + 1, var2.y, var2.z + 1, var4, var7, Direction.SOUTH, var6);
      if (this.isDiagonalValid(var2, var11, var9, var16)) {
         var1[var3++] = var16;
      }

      return var3;
   }

   protected boolean isNeighborValid(@Nullable Node var1, Node var2) {
      return var1 != null && !var1.closed && (var1.costMalus >= 0.0F || var2.costMalus < 0.0F);
   }

   protected boolean isDiagonalValid(Node var1, @Nullable Node var2, @Nullable Node var3, @Nullable Node var4) {
      if (var4 == null || var3 == null || var2 == null) {
         return false;
      } else if (var4.closed) {
         return false;
      } else if (var3.y > var1.y || var2.y > var1.y) {
         return false;
      } else if (var2.type != BlockPathTypes.WALKABLE_DOOR && var3.type != BlockPathTypes.WALKABLE_DOOR && var4.type != BlockPathTypes.WALKABLE_DOOR) {
         boolean var5 = var3.type == BlockPathTypes.FENCE && var2.type == BlockPathTypes.FENCE && (double)this.mob.getBbWidth() < 0.5;
         return var4.costMalus >= 0.0F && (var3.y < var1.y || var3.costMalus >= 0.0F || var5) && (var2.y < var1.y || var2.costMalus >= 0.0F || var5);
      } else {
         return false;
      }
   }

   private static boolean doesBlockHavePartialCollision(BlockPathTypes var0) {
      return var0 == BlockPathTypes.FENCE || var0 == BlockPathTypes.DOOR_WOOD_CLOSED || var0 == BlockPathTypes.DOOR_IRON_CLOSED;
   }

   private boolean canReachWithoutCollision(Node var1) {
      AABB var2 = this.mob.getBoundingBox();
      Vec3 var3 = new Vec3(
         (double)var1.x - this.mob.getX() + var2.getXsize() / 2.0,
         (double)var1.y - this.mob.getY() + var2.getYsize() / 2.0,
         (double)var1.z - this.mob.getZ() + var2.getZsize() / 2.0
      );
      int var4 = Mth.ceil(var3.length() / var2.getSize());
      var3 = var3.scale((double)(1.0F / (float)var4));

      for(int var5 = 1; var5 <= var4; ++var5) {
         var2 = var2.move(var3);
         if (this.hasCollisions(var2)) {
            return false;
         }
      }

      return true;
   }

   protected double getFloorLevel(BlockPos var1) {
      return (this.canFloat() || this.isAmphibious()) && this.level.getFluidState(var1).is(FluidTags.WATER)
         ? (double)var1.getY() + 0.5
         : getFloorLevel(this.level, var1);
   }

   public static double getFloorLevel(BlockGetter var0, BlockPos var1) {
      BlockPos var2 = var1.below();
      VoxelShape var3 = var0.getBlockState(var2).getCollisionShape(var0, var2);
      return (double)var2.getY() + (var3.isEmpty() ? 0.0 : var3.max(Direction.Axis.Y));
   }

   protected boolean isAmphibious() {
      return false;
   }

   @Nullable
   protected Node findAcceptedNode(int var1, int var2, int var3, int var4, double var5, Direction var7, BlockPathTypes var8) {
      Node var9 = null;
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
      double var11 = this.getFloorLevel(var10.set(var1, var2, var3));
      if (var11 - var5 > this.getMobJumpHeight()) {
         return null;
      } else {
         BlockPathTypes var13 = this.getCachedBlockType(this.mob, var1, var2, var3);
         float var14 = this.mob.getPathfindingMalus(var13);
         double var15 = (double)this.mob.getBbWidth() / 2.0;
         if (var14 >= 0.0F) {
            var9 = this.getNodeAndUpdateCostToMax(var1, var2, var3, var13, var14);
         }

         if (doesBlockHavePartialCollision(var8) && var9 != null && var9.costMalus >= 0.0F && !this.canReachWithoutCollision(var9)) {
            var9 = null;
         }

         if (var13 != BlockPathTypes.WALKABLE && (!this.isAmphibious() || var13 != BlockPathTypes.WATER)) {
            if ((var9 == null || var9.costMalus < 0.0F)
               && var4 > 0
               && (var13 != BlockPathTypes.FENCE || this.canWalkOverFences())
               && var13 != BlockPathTypes.UNPASSABLE_RAIL
               && var13 != BlockPathTypes.TRAPDOOR
               && var13 != BlockPathTypes.POWDER_SNOW) {
               var9 = this.findAcceptedNode(var1, var2 + 1, var3, var4 - 1, var5, var7, var8);
               if (var9 != null && (var9.type == BlockPathTypes.OPEN || var9.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0F) {
                  double var17 = (double)(var1 - var7.getStepX()) + 0.5;
                  double var19 = (double)(var3 - var7.getStepZ()) + 0.5;
                  AABB var21 = new AABB(
                     var17 - var15,
                     this.getFloorLevel(var10.set(var17, (double)(var2 + 1), var19)) + 0.001,
                     var19 - var15,
                     var17 + var15,
                     (double)this.mob.getBbHeight() + this.getFloorLevel(var10.set((double)var9.x, (double)var9.y, (double)var9.z)) - 0.002,
                     var19 + var15
                  );
                  if (this.hasCollisions(var21)) {
                     var9 = null;
                  }
               }
            }

            if (!this.isAmphibious() && var13 == BlockPathTypes.WATER && !this.canFloat()) {
               if (this.getCachedBlockType(this.mob, var1, var2 - 1, var3) != BlockPathTypes.WATER) {
                  return var9;
               }

               while(var2 > this.mob.level.getMinBuildHeight()) {
                  var13 = this.getCachedBlockType(this.mob, var1, --var2, var3);
                  if (var13 != BlockPathTypes.WATER) {
                     return var9;
                  }

                  var9 = this.getNodeAndUpdateCostToMax(var1, var2, var3, var13, this.mob.getPathfindingMalus(var13));
               }
            }

            if (var13 == BlockPathTypes.OPEN) {
               int var23 = 0;
               int var18 = var2;

               while(var13 == BlockPathTypes.OPEN) {
                  if (--var2 < this.mob.level.getMinBuildHeight()) {
                     return this.getBlockedNode(var1, var18, var3);
                  }

                  if (var23++ >= this.mob.getMaxFallDistance()) {
                     return this.getBlockedNode(var1, var2, var3);
                  }

                  var13 = this.getCachedBlockType(this.mob, var1, var2, var3);
                  var14 = this.mob.getPathfindingMalus(var13);
                  if (var13 != BlockPathTypes.OPEN && var14 >= 0.0F) {
                     var9 = this.getNodeAndUpdateCostToMax(var1, var2, var3, var13, var14);
                     break;
                  }

                  if (var14 < 0.0F) {
                     return this.getBlockedNode(var1, var2, var3);
                  }
               }
            }

            if (doesBlockHavePartialCollision(var13) && var9 == null) {
               var9 = this.getNode(var1, var2, var3);
               var9.closed = true;
               var9.type = var13;
               var9.costMalus = var13.getMalus();
            }

            return var9;
         } else {
            return var9;
         }
      }
   }

   private double getMobJumpHeight() {
      return Math.max(1.125, (double)this.mob.maxUpStep());
   }

   private Node getNodeAndUpdateCostToMax(int var1, int var2, int var3, BlockPathTypes var4, float var5) {
      Node var6 = this.getNode(var1, var2, var3);
      var6.type = var4;
      var6.costMalus = Math.max(var6.costMalus, var5);
      return var6;
   }

   private Node getBlockedNode(int var1, int var2, int var3) {
      Node var4 = this.getNode(var1, var2, var3);
      var4.type = BlockPathTypes.BLOCKED;
      var4.costMalus = -1.0F;
      return var4;
   }

   private boolean hasCollisions(AABB var1) {
      return this.collisionCache.computeIfAbsent(var1, var2 -> !this.level.noCollision(this.mob, var1));
   }

   @Override
   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5) {
      EnumSet var6 = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes var7 = BlockPathTypes.BLOCKED;
      var7 = this.getBlockPathTypes(var1, var2, var3, var4, var6, var7, var5.blockPosition());
      if (var6.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else if (var6.contains(BlockPathTypes.UNPASSABLE_RAIL)) {
         return BlockPathTypes.UNPASSABLE_RAIL;
      } else {
         BlockPathTypes var8 = BlockPathTypes.BLOCKED;

         for(BlockPathTypes var10 : var6) {
            if (var5.getPathfindingMalus(var10) < 0.0F) {
               return var10;
            }

            if (var5.getPathfindingMalus(var10) >= var5.getPathfindingMalus(var8)) {
               var8 = var10;
            }
         }

         return var7 == BlockPathTypes.OPEN && var5.getPathfindingMalus(var8) == 0.0F && this.entityWidth <= 1 ? BlockPathTypes.OPEN : var8;
      }
   }

   public BlockPathTypes getBlockPathTypes(BlockGetter var1, int var2, int var3, int var4, EnumSet<BlockPathTypes> var5, BlockPathTypes var6, BlockPos var7) {
      for(int var8 = 0; var8 < this.entityWidth; ++var8) {
         for(int var9 = 0; var9 < this.entityHeight; ++var9) {
            for(int var10 = 0; var10 < this.entityDepth; ++var10) {
               int var11 = var8 + var2;
               int var12 = var9 + var3;
               int var13 = var10 + var4;
               BlockPathTypes var14 = this.getBlockPathType(var1, var11, var12, var13);
               var14 = this.evaluateBlockPathType(var1, var7, var14);
               if (var8 == 0 && var9 == 0 && var10 == 0) {
                  var6 = var14;
               }

               var5.add(var14);
            }
         }
      }

      return var6;
   }

   protected BlockPathTypes evaluateBlockPathType(BlockGetter var1, BlockPos var2, BlockPathTypes var3) {
      boolean var4 = this.canPassDoors();
      if (var3 == BlockPathTypes.DOOR_WOOD_CLOSED && this.canOpenDoors() && var4) {
         var3 = BlockPathTypes.WALKABLE_DOOR;
      }

      if (var3 == BlockPathTypes.DOOR_OPEN && !var4) {
         var3 = BlockPathTypes.BLOCKED;
      }

      if (var3 == BlockPathTypes.RAIL
         && !(var1.getBlockState(var2).getBlock() instanceof BaseRailBlock)
         && !(var1.getBlockState(var2.below()).getBlock() instanceof BaseRailBlock)) {
         var3 = BlockPathTypes.UNPASSABLE_RAIL;
      }

      return var3;
   }

   protected BlockPathTypes getBlockPathType(Mob var1, BlockPos var2) {
      return this.getCachedBlockType(var1, var2.getX(), var2.getY(), var2.getZ());
   }

   protected BlockPathTypes getCachedBlockType(Mob var1, int var2, int var3, int var4) {
      return (BlockPathTypes)this.pathTypesByPosCache
         .computeIfAbsent(BlockPos.asLong(var2, var3, var4), var5 -> this.getBlockPathType(this.level, var2, var3, var4, var1));
   }

   @Override
   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
      return getBlockPathTypeStatic(var1, new BlockPos.MutableBlockPos(var2, var3, var4));
   }

   public static BlockPathTypes getBlockPathTypeStatic(BlockGetter var0, BlockPos.MutableBlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      BlockPathTypes var5 = getBlockPathTypeRaw(var0, var1);
      if (var5 == BlockPathTypes.OPEN && var3 >= var0.getMinBuildHeight() + 1) {
         BlockPathTypes var6 = getBlockPathTypeRaw(var0, var1.set(var2, var3 - 1, var4));
         var5 = var6 != BlockPathTypes.WALKABLE && var6 != BlockPathTypes.OPEN && var6 != BlockPathTypes.WATER && var6 != BlockPathTypes.LAVA
            ? BlockPathTypes.WALKABLE
            : BlockPathTypes.OPEN;
         if (var6 == BlockPathTypes.DAMAGE_FIRE) {
            var5 = BlockPathTypes.DAMAGE_FIRE;
         }

         if (var6 == BlockPathTypes.DAMAGE_OTHER) {
            var5 = BlockPathTypes.DAMAGE_OTHER;
         }

         if (var6 == BlockPathTypes.STICKY_HONEY) {
            var5 = BlockPathTypes.STICKY_HONEY;
         }

         if (var6 == BlockPathTypes.POWDER_SNOW) {
            var5 = BlockPathTypes.DANGER_POWDER_SNOW;
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
                  if (var9.is(Blocks.CACTUS) || var9.is(Blocks.SWEET_BERRY_BUSH)) {
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
      } else if (var2.is(BlockTags.TRAPDOORS) || var2.is(Blocks.LILY_PAD) || var2.is(Blocks.BIG_DRIPLEAF)) {
         return BlockPathTypes.TRAPDOOR;
      } else if (var2.is(Blocks.POWDER_SNOW)) {
         return BlockPathTypes.POWDER_SNOW;
      } else if (var2.is(Blocks.CACTUS) || var2.is(Blocks.SWEET_BERRY_BUSH)) {
         return BlockPathTypes.DAMAGE_OTHER;
      } else if (var2.is(Blocks.HONEY_BLOCK)) {
         return BlockPathTypes.STICKY_HONEY;
      } else if (var2.is(Blocks.COCOA)) {
         return BlockPathTypes.COCOA;
      } else {
         FluidState var5 = var0.getFluidState(var1);
         if (var5.is(FluidTags.LAVA)) {
            return BlockPathTypes.LAVA;
         } else if (isBurningBlock(var2)) {
            return BlockPathTypes.DAMAGE_FIRE;
         } else if (DoorBlock.isWoodenDoor(var2) && !var2.getValue(DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_WOOD_CLOSED;
         } else if (var3 instanceof DoorBlock && var4 == Material.METAL && !var2.getValue(DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_IRON_CLOSED;
         } else if (var3 instanceof DoorBlock && var2.getValue(DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_OPEN;
         } else if (var3 instanceof BaseRailBlock) {
            return BlockPathTypes.RAIL;
         } else if (var3 instanceof LeavesBlock) {
            return BlockPathTypes.LEAVES;
         } else if (!var2.is(BlockTags.FENCES) && !var2.is(BlockTags.WALLS) && (!(var3 instanceof FenceGateBlock) || var2.getValue(FenceGateBlock.OPEN))) {
            if (!var2.isPathfindable(var0, var1, PathComputationType.LAND)) {
               return BlockPathTypes.BLOCKED;
            } else {
               return var5.is(FluidTags.WATER) ? BlockPathTypes.WATER : BlockPathTypes.OPEN;
            }
         } else {
            return BlockPathTypes.FENCE;
         }
      }
   }

   public static boolean isBurningBlock(BlockState var0) {
      return var0.is(BlockTags.FIRE)
         || var0.is(Blocks.LAVA)
         || var0.is(Blocks.MAGMA_BLOCK)
         || CampfireBlock.isLitCampfire(var0)
         || var0.is(Blocks.LAVA_CAULDRON);
   }
}
