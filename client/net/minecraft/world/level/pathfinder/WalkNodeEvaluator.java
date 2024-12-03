package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WalkNodeEvaluator extends NodeEvaluator {
   public static final double SPACE_BETWEEN_WALL_POSTS = 0.5;
   private static final double DEFAULT_MOB_JUMP_HEIGHT = 1.125;
   private final Long2ObjectMap<PathType> pathTypesByPosCacheByMob = new Long2ObjectOpenHashMap();
   private final Object2BooleanMap<AABB> collisionCache = new Object2BooleanOpenHashMap();
   private final Node[] reusableNeighbors;

   public WalkNodeEvaluator() {
      super();
      this.reusableNeighbors = new Node[Direction.Plane.HORIZONTAL.length()];
   }

   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      var2.onPathfindingStart();
   }

   public void done() {
      this.mob.onPathfindingDone();
      this.pathTypesByPosCacheByMob.clear();
      this.collisionCache.clear();
      super.done();
   }

   public Node getStart() {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      int var1 = this.mob.getBlockY();
      BlockState var3 = this.currentContext.getBlockState(var2.set(this.mob.getX(), (double)var1, this.mob.getZ()));
      if (!this.mob.canStandOnFluid(var3.getFluidState())) {
         if (this.canFloat() && this.mob.isInWater()) {
            while(true) {
               if (!var3.is(Blocks.WATER) && var3.getFluidState() != Fluids.WATER.getSource(false)) {
                  --var1;
                  break;
               }

               ++var1;
               var3 = this.currentContext.getBlockState(var2.set(this.mob.getX(), (double)var1, this.mob.getZ()));
            }
         } else if (this.mob.onGround()) {
            var1 = Mth.floor(this.mob.getY() + 0.5);
         } else {
            var2.set(this.mob.getX(), this.mob.getY() + 1.0, this.mob.getZ());

            while(var2.getY() > this.currentContext.level().getMinY()) {
               var1 = var2.getY();
               var2.setY(var2.getY() - 1);
               BlockState var4 = this.currentContext.getBlockState(var2);
               if (!var4.isAir() && !var4.isPathfindable(PathComputationType.LAND)) {
                  break;
               }
            }
         }
      } else {
         while(this.mob.canStandOnFluid(var3.getFluidState())) {
            ++var1;
            var3 = this.currentContext.getBlockState(var2.set(this.mob.getX(), (double)var1, this.mob.getZ()));
         }

         --var1;
      }

      BlockPos var6 = this.mob.blockPosition();
      if (!this.canStartAt(var2.set(var6.getX(), var1, var6.getZ()))) {
         AABB var5 = this.mob.getBoundingBox();
         if (this.canStartAt(var2.set(var5.minX, (double)var1, var5.minZ)) || this.canStartAt(var2.set(var5.minX, (double)var1, var5.maxZ)) || this.canStartAt(var2.set(var5.maxX, (double)var1, var5.minZ)) || this.canStartAt(var2.set(var5.maxX, (double)var1, var5.maxZ))) {
            return this.getStartNode(var2);
         }
      }

      return this.getStartNode(new BlockPos(var6.getX(), var1, var6.getZ()));
   }

   protected Node getStartNode(BlockPos var1) {
      Node var2 = this.getNode(var1);
      var2.type = this.getCachedPathType(var2.x, var2.y, var2.z);
      var2.costMalus = this.mob.getPathfindingMalus(var2.type);
      return var2;
   }

   protected boolean canStartAt(BlockPos var1) {
      PathType var2 = this.getCachedPathType(var1.getX(), var1.getY(), var1.getZ());
      return var2 != PathType.OPEN && this.mob.getPathfindingMalus(var2) >= 0.0F;
   }

   public Target getTarget(double var1, double var3, double var5) {
      return this.getTargetNodeAt(var1, var3, var5);
   }

   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      int var4 = 0;
      PathType var5 = this.getCachedPathType(var2.x, var2.y + 1, var2.z);
      PathType var6 = this.getCachedPathType(var2.x, var2.y, var2.z);
      if (this.mob.getPathfindingMalus(var5) >= 0.0F && var6 != PathType.STICKY_HONEY) {
         var4 = Mth.floor(Math.max(1.0F, this.mob.maxUpStep()));
      }

      double var7 = this.getFloorLevel(new BlockPos(var2.x, var2.y, var2.z));

      for(Direction var10 : Direction.Plane.HORIZONTAL) {
         Node var11 = this.findAcceptedNode(var2.x + var10.getStepX(), var2.y, var2.z + var10.getStepZ(), var4, var7, var10, var6);
         this.reusableNeighbors[var10.get2DDataValue()] = var11;
         if (this.isNeighborValid(var11, var2)) {
            var1[var3++] = var11;
         }
      }

      for(Direction var14 : Direction.Plane.HORIZONTAL) {
         Direction var15 = var14.getClockWise();
         if (this.isDiagonalValid(var2, this.reusableNeighbors[var14.get2DDataValue()], this.reusableNeighbors[var15.get2DDataValue()])) {
            Node var12 = this.findAcceptedNode(var2.x + var14.getStepX() + var15.getStepX(), var2.y, var2.z + var14.getStepZ() + var15.getStepZ(), var4, var7, var14, var6);
            if (this.isDiagonalValid(var12)) {
               var1[var3++] = var12;
            }
         }
      }

      return var3;
   }

   protected boolean isNeighborValid(@Nullable Node var1, Node var2) {
      return var1 != null && !var1.closed && (var1.costMalus >= 0.0F || var2.costMalus < 0.0F);
   }

   protected boolean isDiagonalValid(Node var1, @Nullable Node var2, @Nullable Node var3) {
      if (var3 != null && var2 != null && var3.y <= var1.y && var2.y <= var1.y) {
         if (var2.type != PathType.WALKABLE_DOOR && var3.type != PathType.WALKABLE_DOOR) {
            boolean var4 = var3.type == PathType.FENCE && var2.type == PathType.FENCE && (double)this.mob.getBbWidth() < 0.5;
            return (var3.y < var1.y || var3.costMalus >= 0.0F || var4) && (var2.y < var1.y || var2.costMalus >= 0.0F || var4);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean isDiagonalValid(@Nullable Node var1) {
      if (var1 != null && !var1.closed) {
         if (var1.type == PathType.WALKABLE_DOOR) {
            return false;
         } else {
            return var1.costMalus >= 0.0F;
         }
      } else {
         return false;
      }
   }

   private static boolean doesBlockHavePartialCollision(PathType var0) {
      return var0 == PathType.FENCE || var0 == PathType.DOOR_WOOD_CLOSED || var0 == PathType.DOOR_IRON_CLOSED;
   }

   private boolean canReachWithoutCollision(Node var1) {
      AABB var2 = this.mob.getBoundingBox();
      Vec3 var3 = new Vec3((double)var1.x - this.mob.getX() + var2.getXsize() / 2.0, (double)var1.y - this.mob.getY() + var2.getYsize() / 2.0, (double)var1.z - this.mob.getZ() + var2.getZsize() / 2.0);
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
      CollisionGetter var2 = this.currentContext.level();
      return (this.canFloat() || this.isAmphibious()) && var2.getFluidState(var1).is(FluidTags.WATER) ? (double)var1.getY() + 0.5 : getFloorLevel(var2, var1);
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
   protected Node findAcceptedNode(int var1, int var2, int var3, int var4, double var5, Direction var7, PathType var8) {
      Node var9 = null;
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
      double var11 = this.getFloorLevel(var10.set(var1, var2, var3));
      if (var11 - var5 > this.getMobJumpHeight()) {
         return null;
      } else {
         PathType var13 = this.getCachedPathType(var1, var2, var3);
         float var14 = this.mob.getPathfindingMalus(var13);
         if (var14 >= 0.0F) {
            var9 = this.getNodeAndUpdateCostToMax(var1, var2, var3, var13, var14);
         }

         if (doesBlockHavePartialCollision(var8) && var9 != null && var9.costMalus >= 0.0F && !this.canReachWithoutCollision(var9)) {
            var9 = null;
         }

         if (var13 != PathType.WALKABLE && (!this.isAmphibious() || var13 != PathType.WATER)) {
            if ((var9 == null || var9.costMalus < 0.0F) && var4 > 0 && (var13 != PathType.FENCE || this.canWalkOverFences()) && var13 != PathType.UNPASSABLE_RAIL && var13 != PathType.TRAPDOOR && var13 != PathType.POWDER_SNOW) {
               var9 = this.tryJumpOn(var1, var2, var3, var4, var5, var7, var8, var10);
            } else if (!this.isAmphibious() && var13 == PathType.WATER && !this.canFloat()) {
               var9 = this.tryFindFirstNonWaterBelow(var1, var2, var3, var9);
            } else if (var13 == PathType.OPEN) {
               var9 = this.tryFindFirstGroundNodeBelow(var1, var2, var3);
            } else if (doesBlockHavePartialCollision(var13) && var9 == null) {
               var9 = this.getClosedNode(var1, var2, var3, var13);
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

   private Node getNodeAndUpdateCostToMax(int var1, int var2, int var3, PathType var4, float var5) {
      Node var6 = this.getNode(var1, var2, var3);
      var6.type = var4;
      var6.costMalus = Math.max(var6.costMalus, var5);
      return var6;
   }

   private Node getBlockedNode(int var1, int var2, int var3) {
      Node var4 = this.getNode(var1, var2, var3);
      var4.type = PathType.BLOCKED;
      var4.costMalus = -1.0F;
      return var4;
   }

   private Node getClosedNode(int var1, int var2, int var3, PathType var4) {
      Node var5 = this.getNode(var1, var2, var3);
      var5.closed = true;
      var5.type = var4;
      var5.costMalus = var4.getMalus();
      return var5;
   }

   @Nullable
   private Node tryJumpOn(int var1, int var2, int var3, int var4, double var5, Direction var7, PathType var8, BlockPos.MutableBlockPos var9) {
      Node var10 = this.findAcceptedNode(var1, var2 + 1, var3, var4 - 1, var5, var7, var8);
      if (var10 == null) {
         return null;
      } else if (this.mob.getBbWidth() >= 1.0F) {
         return var10;
      } else if (var10.type != PathType.OPEN && var10.type != PathType.WALKABLE) {
         return var10;
      } else {
         double var11 = (double)(var1 - var7.getStepX()) + 0.5;
         double var13 = (double)(var3 - var7.getStepZ()) + 0.5;
         double var15 = (double)this.mob.getBbWidth() / 2.0;
         AABB var17 = new AABB(var11 - var15, this.getFloorLevel(var9.set(var11, (double)(var2 + 1), var13)) + 0.001, var13 - var15, var11 + var15, (double)this.mob.getBbHeight() + this.getFloorLevel(var9.set((double)var10.x, (double)var10.y, (double)var10.z)) - 0.002, var13 + var15);
         return this.hasCollisions(var17) ? null : var10;
      }
   }

   @Nullable
   private Node tryFindFirstNonWaterBelow(int var1, int var2, int var3, @Nullable Node var4) {
      --var2;

      while(var2 > this.mob.level().getMinY()) {
         PathType var5 = this.getCachedPathType(var1, var2, var3);
         if (var5 != PathType.WATER) {
            return var4;
         }

         var4 = this.getNodeAndUpdateCostToMax(var1, var2, var3, var5, this.mob.getPathfindingMalus(var5));
         --var2;
      }

      return var4;
   }

   private Node tryFindFirstGroundNodeBelow(int var1, int var2, int var3) {
      for(int var4 = var2 - 1; var4 >= this.mob.level().getMinY(); --var4) {
         if (var2 - var4 > this.mob.getMaxFallDistance()) {
            return this.getBlockedNode(var1, var4, var3);
         }

         PathType var5 = this.getCachedPathType(var1, var4, var3);
         float var6 = this.mob.getPathfindingMalus(var5);
         if (var5 != PathType.OPEN) {
            if (var6 >= 0.0F) {
               return this.getNodeAndUpdateCostToMax(var1, var4, var3, var5, var6);
            }

            return this.getBlockedNode(var1, var4, var3);
         }
      }

      return this.getBlockedNode(var1, var2, var3);
   }

   private boolean hasCollisions(AABB var1) {
      return this.collisionCache.computeIfAbsent(var1, (var2) -> !this.currentContext.level().noCollision(this.mob, var1));
   }

   protected PathType getCachedPathType(int var1, int var2, int var3) {
      return (PathType)this.pathTypesByPosCacheByMob.computeIfAbsent(BlockPos.asLong(var1, var2, var3), (var4) -> this.getPathTypeOfMob(this.currentContext, var1, var2, var3, this.mob));
   }

   public PathType getPathTypeOfMob(PathfindingContext var1, int var2, int var3, int var4, Mob var5) {
      Set var6 = this.getPathTypeWithinMobBB(var1, var2, var3, var4);
      if (var6.contains(PathType.FENCE)) {
         return PathType.FENCE;
      } else if (var6.contains(PathType.UNPASSABLE_RAIL)) {
         return PathType.UNPASSABLE_RAIL;
      } else {
         PathType var7 = PathType.BLOCKED;

         for(PathType var9 : var6) {
            if (var5.getPathfindingMalus(var9) < 0.0F) {
               return var9;
            }

            if (var5.getPathfindingMalus(var9) >= var5.getPathfindingMalus(var7)) {
               var7 = var9;
            }
         }

         if (this.entityWidth <= 1 && var7 != PathType.OPEN && var5.getPathfindingMalus(var7) == 0.0F && this.getPathType(var1, var2, var3, var4) == PathType.OPEN) {
            return PathType.OPEN;
         } else {
            return var7;
         }
      }
   }

   public Set<PathType> getPathTypeWithinMobBB(PathfindingContext var1, int var2, int var3, int var4) {
      EnumSet var5 = EnumSet.noneOf(PathType.class);

      for(int var6 = 0; var6 < this.entityWidth; ++var6) {
         for(int var7 = 0; var7 < this.entityHeight; ++var7) {
            for(int var8 = 0; var8 < this.entityDepth; ++var8) {
               int var9 = var6 + var2;
               int var10 = var7 + var3;
               int var11 = var8 + var4;
               PathType var12 = this.getPathType(var1, var9, var10, var11);
               BlockPos var13 = this.mob.blockPosition();
               boolean var14 = this.canPassDoors();
               if (var12 == PathType.DOOR_WOOD_CLOSED && this.canOpenDoors() && var14) {
                  var12 = PathType.WALKABLE_DOOR;
               }

               if (var12 == PathType.DOOR_OPEN && !var14) {
                  var12 = PathType.BLOCKED;
               }

               if (var12 == PathType.RAIL && this.getPathType(var1, var13.getX(), var13.getY(), var13.getZ()) != PathType.RAIL && this.getPathType(var1, var13.getX(), var13.getY() - 1, var13.getZ()) != PathType.RAIL) {
                  var12 = PathType.UNPASSABLE_RAIL;
               }

               var5.add(var12);
            }
         }
      }

      return var5;
   }

   public PathType getPathType(PathfindingContext var1, int var2, int var3, int var4) {
      return getPathTypeStatic(var1, new BlockPos.MutableBlockPos(var2, var3, var4));
   }

   public static PathType getPathTypeStatic(Mob var0, BlockPos var1) {
      return getPathTypeStatic(new PathfindingContext(var0.level(), var0), var1.mutable());
   }

   public static PathType getPathTypeStatic(PathfindingContext var0, BlockPos.MutableBlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      PathType var5 = var0.getPathTypeFromState(var2, var3, var4);
      if (var5 == PathType.OPEN && var3 >= var0.level().getMinY() + 1) {
         PathType var10000;
         switch (var0.getPathTypeFromState(var2, var3 - 1, var4)) {
            case OPEN:
            case WATER:
            case LAVA:
            case WALKABLE:
               var10000 = PathType.OPEN;
               break;
            case DAMAGE_FIRE:
               var10000 = PathType.DAMAGE_FIRE;
               break;
            case DAMAGE_OTHER:
               var10000 = PathType.DAMAGE_OTHER;
               break;
            case STICKY_HONEY:
               var10000 = PathType.STICKY_HONEY;
               break;
            case POWDER_SNOW:
               var10000 = PathType.DANGER_POWDER_SNOW;
               break;
            case DAMAGE_CAUTIOUS:
               var10000 = PathType.DAMAGE_CAUTIOUS;
               break;
            case TRAPDOOR:
               var10000 = PathType.DANGER_TRAPDOOR;
               break;
            default:
               var10000 = checkNeighbourBlocks(var0, var2, var3, var4, PathType.WALKABLE);
         }

         return var10000;
      } else {
         return var5;
      }
   }

   public static PathType checkNeighbourBlocks(PathfindingContext var0, int var1, int var2, int var3, PathType var4) {
      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            for(int var7 = -1; var7 <= 1; ++var7) {
               if (var5 != 0 || var7 != 0) {
                  PathType var8 = var0.getPathTypeFromState(var1 + var5, var2 + var6, var3 + var7);
                  if (var8 == PathType.DAMAGE_OTHER) {
                     return PathType.DANGER_OTHER;
                  }

                  if (var8 == PathType.DAMAGE_FIRE || var8 == PathType.LAVA) {
                     return PathType.DANGER_FIRE;
                  }

                  if (var8 == PathType.WATER) {
                     return PathType.WATER_BORDER;
                  }

                  if (var8 == PathType.DAMAGE_CAUTIOUS) {
                     return PathType.DAMAGE_CAUTIOUS;
                  }
               }
            }
         }
      }

      return var4;
   }

   protected static PathType getPathTypeFromState(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      Block var3 = var2.getBlock();
      if (var2.isAir()) {
         return PathType.OPEN;
      } else if (!var2.is(BlockTags.TRAPDOORS) && !var2.is(Blocks.LILY_PAD) && !var2.is(Blocks.BIG_DRIPLEAF)) {
         if (var2.is(Blocks.POWDER_SNOW)) {
            return PathType.POWDER_SNOW;
         } else if (!var2.is(Blocks.CACTUS) && !var2.is(Blocks.SWEET_BERRY_BUSH)) {
            if (var2.is(Blocks.HONEY_BLOCK)) {
               return PathType.STICKY_HONEY;
            } else if (var2.is(Blocks.COCOA)) {
               return PathType.COCOA;
            } else if (!var2.is(Blocks.WITHER_ROSE) && !var2.is(Blocks.POINTED_DRIPSTONE)) {
               FluidState var4 = var2.getFluidState();
               if (var4.is(FluidTags.LAVA)) {
                  return PathType.LAVA;
               } else if (isBurningBlock(var2)) {
                  return PathType.DAMAGE_FIRE;
               } else if (var3 instanceof DoorBlock) {
                  DoorBlock var5 = (DoorBlock)var3;
                  if ((Boolean)var2.getValue(DoorBlock.OPEN)) {
                     return PathType.DOOR_OPEN;
                  } else {
                     return var5.type().canOpenByHand() ? PathType.DOOR_WOOD_CLOSED : PathType.DOOR_IRON_CLOSED;
                  }
               } else if (var3 instanceof BaseRailBlock) {
                  return PathType.RAIL;
               } else if (var3 instanceof LeavesBlock) {
                  return PathType.LEAVES;
               } else if (!var2.is(BlockTags.FENCES) && !var2.is(BlockTags.WALLS) && (!(var3 instanceof FenceGateBlock) || (Boolean)var2.getValue(FenceGateBlock.OPEN))) {
                  if (!var2.isPathfindable(PathComputationType.LAND)) {
                     return PathType.BLOCKED;
                  } else {
                     return var4.is(FluidTags.WATER) ? PathType.WATER : PathType.OPEN;
                  }
               } else {
                  return PathType.FENCE;
               }
            } else {
               return PathType.DAMAGE_CAUTIOUS;
            }
         } else {
            return PathType.DAMAGE_OTHER;
         }
      } else {
         return PathType.TRAPDOOR;
      }
   }
}
