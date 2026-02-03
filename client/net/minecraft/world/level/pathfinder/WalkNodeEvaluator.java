package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import java.util.Set;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

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

   public void prepare(final PathNavigationRegion level, final Mob entity) {
      super.prepare(level, entity);
      entity.onPathfindingStart();
   }

   public void done() {
      this.mob.onPathfindingDone();
      this.pathTypesByPosCacheByMob.clear();
      this.collisionCache.clear();
      super.done();
   }

   public Node getStart() {
      BlockPos.MutableBlockPos reusablePos = new BlockPos.MutableBlockPos();
      int startY = this.mob.getBlockY();
      BlockState blockState = this.currentContext.getBlockState(reusablePos.set(this.mob.getX(), (double)startY, this.mob.getZ()));
      if (!this.mob.canStandOnFluid(blockState.getFluidState())) {
         if (this.canFloat() && this.mob.isInWater()) {
            while(true) {
               if (!blockState.is(Blocks.WATER) && blockState.getFluidState() != Fluids.WATER.getSource(false)) {
                  --startY;
                  break;
               }

               ++startY;
               blockState = this.currentContext.getBlockState(reusablePos.set(this.mob.getX(), (double)startY, this.mob.getZ()));
            }
         } else if (this.mob.onGround()) {
            startY = Mth.floor(this.mob.getY() + 0.5);
         } else {
            reusablePos.set(this.mob.getX(), this.mob.getY() + 1.0, this.mob.getZ());

            while(reusablePos.getY() > this.currentContext.level().getMinY()) {
               startY = reusablePos.getY();
               reusablePos.setY(reusablePos.getY() - 1);
               BlockState belowBlockState = this.currentContext.getBlockState(reusablePos);
               if (!belowBlockState.isAir() && !belowBlockState.isPathfindable(PathComputationType.LAND)) {
                  break;
               }
            }
         }
      } else {
         while(this.mob.canStandOnFluid(blockState.getFluidState())) {
            ++startY;
            blockState = this.currentContext.getBlockState(reusablePos.set(this.mob.getX(), (double)startY, this.mob.getZ()));
         }

         --startY;
      }

      BlockPos startPos = this.mob.blockPosition();
      if (!this.canStartAt(reusablePos.set(startPos.getX(), startY, startPos.getZ()))) {
         AABB mobBB = this.mob.getBoundingBox();
         if (this.canStartAt(reusablePos.set(mobBB.minX, (double)startY, mobBB.minZ)) || this.canStartAt(reusablePos.set(mobBB.minX, (double)startY, mobBB.maxZ)) || this.canStartAt(reusablePos.set(mobBB.maxX, (double)startY, mobBB.minZ)) || this.canStartAt(reusablePos.set(mobBB.maxX, (double)startY, mobBB.maxZ))) {
            return this.getStartNode(reusablePos);
         }
      }

      return this.getStartNode(new BlockPos(startPos.getX(), startY, startPos.getZ()));
   }

   protected Node getStartNode(final BlockPos pos) {
      Node node = this.getNode(pos);
      node.type = this.getCachedPathType(node.x, node.y, node.z);
      node.costMalus = this.mob.getPathfindingMalus(node.type);
      return node;
   }

   protected boolean canStartAt(final BlockPos pos) {
      PathType blockPathType = this.getCachedPathType(pos.getX(), pos.getY(), pos.getZ());
      return blockPathType != PathType.OPEN && this.mob.getPathfindingMalus(blockPathType) >= 0.0F;
   }

   public Target getTarget(final double x, final double y, final double z) {
      return this.getTargetNodeAt(x, y, z);
   }

   public int getNeighbors(final Node[] neighbors, final Node pos) {
      int p = 0;
      int jumpSize = 0;
      PathType blockPathTypeAbove = this.getCachedPathType(pos.x, pos.y + 1, pos.z);
      PathType blockPathTypeCurrent = this.getCachedPathType(pos.x, pos.y, pos.z);
      if (this.mob.getPathfindingMalus(blockPathTypeAbove) >= 0.0F && blockPathTypeCurrent != PathType.STICKY_HONEY) {
         jumpSize = Mth.floor(Math.max(1.0F, this.mob.maxUpStep()));
      }

      double posHeight = this.getFloorLevel(new BlockPos(pos.x, pos.y, pos.z));

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         Node node = this.findAcceptedNode(pos.x + direction.getStepX(), pos.y, pos.z + direction.getStepZ(), jumpSize, posHeight, direction, blockPathTypeCurrent);
         this.reusableNeighbors[direction.get2DDataValue()] = node;
         if (this.isNeighborValid(node, pos)) {
            neighbors[p++] = node;
         }
      }

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         Direction secondDirection = direction.getClockWise();
         if (this.isDiagonalValid(pos, this.reusableNeighbors[direction.get2DDataValue()], this.reusableNeighbors[secondDirection.get2DDataValue()])) {
            Node diagonalNode = this.findAcceptedNode(pos.x + direction.getStepX() + secondDirection.getStepX(), pos.y, pos.z + direction.getStepZ() + secondDirection.getStepZ(), jumpSize, posHeight, direction, blockPathTypeCurrent);
            if (this.isDiagonalValid(diagonalNode)) {
               neighbors[p++] = diagonalNode;
            }
         }
      }

      return p;
   }

   protected boolean isNeighborValid(final @Nullable Node neighbor, final Node current) {
      return neighbor != null && !neighbor.closed && (neighbor.costMalus >= 0.0F || current.costMalus < 0.0F);
   }

   protected boolean isDiagonalValid(final Node pos, final @Nullable Node ew, final @Nullable Node ns) {
      if (ns != null && ew != null && ns.y <= pos.y && ew.y <= pos.y) {
         if (ew.type != PathType.WALKABLE_DOOR && ns.type != PathType.WALKABLE_DOOR) {
            boolean canPassBetweenPosts = ns.type == PathType.FENCE && ew.type == PathType.FENCE && (double)this.mob.getBbWidth() < 0.5;
            return (ns.y < pos.y || ns.costMalus >= 0.0F || canPassBetweenPosts) && (ew.y < pos.y || ew.costMalus >= 0.0F || canPassBetweenPosts);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean isDiagonalValid(final @Nullable Node diagonal) {
      if (diagonal != null && !diagonal.closed) {
         if (diagonal.type == PathType.WALKABLE_DOOR) {
            return false;
         } else {
            return diagonal.costMalus >= 0.0F;
         }
      } else {
         return false;
      }
   }

   private static boolean doesBlockHavePartialCollision(final PathType type) {
      return type == PathType.FENCE || type == PathType.DOOR_WOOD_CLOSED || type == PathType.DOOR_IRON_CLOSED;
   }

   private boolean canReachWithoutCollision(final Node posTo) {
      AABB bb = this.mob.getBoundingBox();
      Vec3 delta = new Vec3((double)posTo.x - this.mob.getX() + bb.getXsize() / 2.0, (double)posTo.y - this.mob.getY() + bb.getYsize() / 2.0, (double)posTo.z - this.mob.getZ() + bb.getZsize() / 2.0);
      int steps = Mth.ceil(delta.length() / bb.getSize());
      delta = delta.scale((double)(1.0F / (float)steps));

      for(int i = 1; i <= steps; ++i) {
         bb = bb.move(delta);
         if (this.hasCollisions(bb)) {
            return false;
         }
      }

      return true;
   }

   protected double getFloorLevel(final BlockPos pos) {
      BlockGetter level = this.currentContext.level();
      return (this.canFloat() || this.isAmphibious()) && level.getFluidState(pos).is(FluidTags.WATER) ? (double)pos.getY() + 0.5 : getFloorLevel(level, pos);
   }

   public static double getFloorLevel(final BlockGetter level, final BlockPos pos) {
      BlockPos target = pos.below();
      VoxelShape shape = level.getBlockState(target).getCollisionShape(level, target);
      return (double)target.getY() + (shape.isEmpty() ? 0.0 : shape.max(Direction.Axis.Y));
   }

   protected boolean isAmphibious() {
      return false;
   }

   protected @Nullable Node findAcceptedNode(final int x, final int y, final int z, final int jumpSize, final double nodeHeight, final Direction travelDirection, final PathType blockPathTypeCurrent) {
      Node best = null;
      BlockPos.MutableBlockPos reusablePos = new BlockPos.MutableBlockPos();
      double maxYTarget = this.getFloorLevel(reusablePos.set(x, y, z));
      if (maxYTarget - nodeHeight > this.getMobJumpHeight()) {
         return null;
      } else {
         PathType pathType = this.getCachedPathType(x, y, z);
         float pathCost = this.mob.getPathfindingMalus(pathType);
         if (pathCost >= 0.0F) {
            best = this.getNodeAndUpdateCostToMax(x, y, z, pathType, pathCost);
         }

         if (doesBlockHavePartialCollision(blockPathTypeCurrent) && best != null && best.costMalus >= 0.0F && !this.canReachWithoutCollision(best)) {
            best = null;
         }

         if (pathType != PathType.WALKABLE && (!this.isAmphibious() || pathType != PathType.WATER)) {
            if ((best == null || best.costMalus < 0.0F) && jumpSize > 0 && (pathType != PathType.FENCE || this.canWalkOverFences()) && pathType != PathType.UNPASSABLE_RAIL && pathType != PathType.TRAPDOOR && pathType != PathType.POWDER_SNOW) {
               best = this.tryJumpOn(x, y, z, jumpSize, nodeHeight, travelDirection, blockPathTypeCurrent, reusablePos);
            } else if (!this.isAmphibious() && pathType == PathType.WATER && !this.canFloat()) {
               best = this.tryFindFirstNonWaterBelow(x, y, z, best);
            } else if (pathType == PathType.OPEN) {
               best = this.tryFindFirstGroundNodeBelow(x, y, z);
            } else if (doesBlockHavePartialCollision(pathType) && best == null) {
               best = this.getClosedNode(x, y, z, pathType);
            }

            return best;
         } else {
            return best;
         }
      }
   }

   private double getMobJumpHeight() {
      return Math.max(1.125, (double)this.mob.maxUpStep());
   }

   private Node getNodeAndUpdateCostToMax(final int x, final int y, final int z, final PathType pathType, final float cost) {
      Node node = this.getNode(x, y, z);
      node.type = pathType;
      node.costMalus = Math.max(node.costMalus, cost);
      return node;
   }

   private Node getBlockedNode(final int x, final int y, final int z) {
      Node node = this.getNode(x, y, z);
      node.type = PathType.BLOCKED;
      node.costMalus = -1.0F;
      return node;
   }

   private Node getClosedNode(final int x, final int y, final int z, final PathType pathType) {
      Node node = this.getNode(x, y, z);
      node.closed = true;
      node.type = pathType;
      node.costMalus = pathType.getMalus();
      return node;
   }

   private @Nullable Node tryJumpOn(final int x, final int y, final int z, final int jumpSize, final double nodeHeight, final Direction travelDirection, final PathType blockPathTypeCurrent, final BlockPos.MutableBlockPos reusablePos) {
      Node nodeAbove = this.findAcceptedNode(x, y + 1, z, jumpSize - 1, nodeHeight, travelDirection, blockPathTypeCurrent);
      if (nodeAbove == null) {
         return null;
      } else if (this.mob.getBbWidth() >= 1.0F) {
         return nodeAbove;
      } else if (nodeAbove.type != PathType.OPEN && nodeAbove.type != PathType.WALKABLE) {
         return nodeAbove;
      } else {
         double centerX = (double)(x - travelDirection.getStepX()) + 0.5;
         double centerZ = (double)(z - travelDirection.getStepZ()) + 0.5;
         double halfWidth = (double)this.mob.getBbWidth() / 2.0;
         AABB grow = new AABB(centerX - halfWidth, this.getFloorLevel(reusablePos.set(centerX, (double)(y + 1), centerZ)) + 0.001, centerZ - halfWidth, centerX + halfWidth, (double)this.mob.getBbHeight() + this.getFloorLevel(reusablePos.set((double)nodeAbove.x, (double)nodeAbove.y, (double)nodeAbove.z)) - 0.002, centerZ + halfWidth);
         return this.hasCollisions(grow) ? null : nodeAbove;
      }
   }

   private @Nullable Node tryFindFirstNonWaterBelow(final int x, int y, final int z, @Nullable Node best) {
      --y;

      while(y > this.mob.level().getMinY()) {
         PathType pathTypeLocal = this.getCachedPathType(x, y, z);
         if (pathTypeLocal != PathType.WATER) {
            return best;
         }

         best = this.getNodeAndUpdateCostToMax(x, y, z, pathTypeLocal, this.mob.getPathfindingMalus(pathTypeLocal));
         --y;
      }

      return best;
   }

   private Node tryFindFirstGroundNodeBelow(final int x, final int y, final int z) {
      for(int currentY = y - 1; currentY >= this.mob.level().getMinY(); --currentY) {
         if (y - currentY > this.mob.getMaxFallDistance()) {
            return this.getBlockedNode(x, currentY, z);
         }

         PathType pathType = this.getCachedPathType(x, currentY, z);
         float pathCost = this.mob.getPathfindingMalus(pathType);
         if (pathType != PathType.OPEN) {
            if (pathCost >= 0.0F) {
               return this.getNodeAndUpdateCostToMax(x, currentY, z, pathType, pathCost);
            }

            return this.getBlockedNode(x, currentY, z);
         }
      }

      return this.getBlockedNode(x, y, z);
   }

   private boolean hasCollisions(final AABB aabb) {
      return this.collisionCache.computeIfAbsent(aabb, (bb) -> !this.currentContext.level().noCollision(this.mob, aabb));
   }

   protected PathType getCachedPathType(final int x, final int y, final int z) {
      return (PathType)this.pathTypesByPosCacheByMob.computeIfAbsent(BlockPos.asLong(x, y, z), (k) -> this.getPathTypeOfMob(this.currentContext, x, y, z, this.mob));
   }

   public PathType getPathTypeOfMob(final PathfindingContext context, final int x, final int y, final int z, final Mob mob) {
      Set<PathType> blockTypes = this.getPathTypeWithinMobBB(context, x, y, z);
      if (blockTypes.contains(PathType.FENCE)) {
         return PathType.FENCE;
      } else if (blockTypes.contains(PathType.UNPASSABLE_RAIL)) {
         return PathType.UNPASSABLE_RAIL;
      } else {
         PathType blockType = PathType.BLOCKED;

         for(PathType type : blockTypes) {
            if (mob.getPathfindingMalus(type) < 0.0F) {
               return type;
            }

            if (mob.getPathfindingMalus(type) >= mob.getPathfindingMalus(blockType)) {
               blockType = type;
            }
         }

         if (this.entityWidth <= 1 && blockType != PathType.OPEN && mob.getPathfindingMalus(blockType) == 0.0F && this.getPathType(context, x, y, z) == PathType.OPEN) {
            return PathType.OPEN;
         } else {
            return blockType;
         }
      }
   }

   public Set<PathType> getPathTypeWithinMobBB(final PathfindingContext context, final int x, final int y, final int z) {
      EnumSet<PathType> blockTypes = EnumSet.noneOf(PathType.class);

      for(int dx = 0; dx < this.entityWidth; ++dx) {
         for(int dy = 0; dy < this.entityHeight; ++dy) {
            for(int dz = 0; dz < this.entityDepth; ++dz) {
               int xx = dx + x;
               int yy = dy + y;
               int zz = dz + z;
               PathType blockType = this.getPathType(context, xx, yy, zz);
               BlockPos mobPosition = this.mob.blockPosition();
               boolean canPassDoors = this.canPassDoors();
               if (blockType == PathType.DOOR_WOOD_CLOSED && this.canOpenDoors() && canPassDoors) {
                  blockType = PathType.WALKABLE_DOOR;
               }

               if (blockType == PathType.DOOR_OPEN && !canPassDoors) {
                  blockType = PathType.BLOCKED;
               }

               if (blockType == PathType.RAIL && this.getPathType(context, mobPosition.getX(), mobPosition.getY(), mobPosition.getZ()) != PathType.RAIL && this.getPathType(context, mobPosition.getX(), mobPosition.getY() - 1, mobPosition.getZ()) != PathType.RAIL) {
                  blockType = PathType.UNPASSABLE_RAIL;
               }

               blockTypes.add(blockType);
            }
         }
      }

      return blockTypes;
   }

   public PathType getPathType(final PathfindingContext context, final int x, final int y, final int z) {
      return getPathTypeStatic(context, new BlockPos.MutableBlockPos(x, y, z));
   }

   public static PathType getPathTypeStatic(final Mob mob, final BlockPos pos) {
      return getPathTypeStatic(new PathfindingContext(mob.level(), mob), pos.mutable());
   }

   public static PathType getPathTypeStatic(final PathfindingContext context, final BlockPos.MutableBlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      PathType blockPathType = context.getPathTypeFromState(x, y, z);
      if (blockPathType == PathType.OPEN && y >= context.level().getMinY() + 1) {
         PathType var10000;
         switch (context.getPathTypeFromState(x, y - 1, z)) {
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
               var10000 = checkNeighbourBlocks(context, x, y, z, PathType.WALKABLE);
         }

         return var10000;
      } else {
         return blockPathType;
      }
   }

   public static PathType checkNeighbourBlocks(final PathfindingContext context, final int x, final int y, final int z, final PathType blockPathType) {
      for(int dx = -1; dx <= 1; ++dx) {
         for(int dy = -1; dy <= 1; ++dy) {
            for(int dz = -1; dz <= 1; ++dz) {
               if (dx != 0 || dz != 0) {
                  PathType pathType = context.getPathTypeFromState(x + dx, y + dy, z + dz);
                  if (pathType == PathType.DAMAGE_OTHER) {
                     return PathType.DANGER_OTHER;
                  }

                  if (pathType == PathType.DAMAGE_FIRE || pathType == PathType.LAVA) {
                     return PathType.DANGER_FIRE;
                  }

                  if (pathType == PathType.WATER) {
                     return PathType.WATER_BORDER;
                  }

                  if (pathType == PathType.DAMAGE_CAUTIOUS) {
                     return PathType.DAMAGE_CAUTIOUS;
                  }
               }
            }
         }
      }

      return blockPathType;
   }

   protected static PathType getPathTypeFromState(final BlockGetter level, final BlockPos pos) {
      BlockState blockState = level.getBlockState(pos);
      Block block = blockState.getBlock();
      if (blockState.isAir()) {
         return PathType.OPEN;
      } else if (!blockState.is(BlockTags.TRAPDOORS) && !blockState.is(Blocks.LILY_PAD) && !blockState.is(Blocks.BIG_DRIPLEAF)) {
         if (blockState.is(Blocks.POWDER_SNOW)) {
            return PathType.POWDER_SNOW;
         } else if (!blockState.is(Blocks.CACTUS) && !blockState.is(Blocks.SWEET_BERRY_BUSH)) {
            if (blockState.is(Blocks.HONEY_BLOCK)) {
               return PathType.STICKY_HONEY;
            } else if (blockState.is(Blocks.COCOA)) {
               return PathType.COCOA;
            } else if (!blockState.is(Blocks.WITHER_ROSE) && !blockState.is(Blocks.POINTED_DRIPSTONE)) {
               FluidState fluidState = blockState.getFluidState();
               if (fluidState.is(FluidTags.LAVA)) {
                  return PathType.LAVA;
               } else if (isBurningBlock(blockState)) {
                  return PathType.DAMAGE_FIRE;
               } else if (block instanceof DoorBlock) {
                  DoorBlock door = (DoorBlock)block;
                  if ((Boolean)blockState.getValue(DoorBlock.OPEN)) {
                     return PathType.DOOR_OPEN;
                  } else {
                     return door.type().canOpenByHand() ? PathType.DOOR_WOOD_CLOSED : PathType.DOOR_IRON_CLOSED;
                  }
               } else if (block instanceof BaseRailBlock) {
                  return PathType.RAIL;
               } else if (block instanceof LeavesBlock) {
                  return PathType.LEAVES;
               } else if (!blockState.is(BlockTags.FENCES) && !blockState.is(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || (Boolean)blockState.getValue(FenceGateBlock.OPEN))) {
                  if (!blockState.isPathfindable(PathComputationType.LAND)) {
                     return PathType.BLOCKED;
                  } else {
                     return fluidState.is(FluidTags.WATER) ? PathType.WATER : PathType.OPEN;
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
