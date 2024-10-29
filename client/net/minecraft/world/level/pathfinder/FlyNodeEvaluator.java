package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FlyNodeEvaluator extends WalkNodeEvaluator {
   private final Long2ObjectMap<PathType> pathTypeByPosCache = new Long2ObjectOpenHashMap();
   private static final float SMALL_MOB_SIZE = 1.0F;
   private static final float SMALL_MOB_INFLATED_START_NODE_BOUNDING_BOX = 1.1F;
   private static final int MAX_START_NODE_CANDIDATES = 10;

   public FlyNodeEvaluator() {
      super();
   }

   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      this.pathTypeByPosCache.clear();
      var2.onPathfindingStart();
   }

   public void done() {
      this.mob.onPathfindingDone();
      this.pathTypeByPosCache.clear();
      super.done();
   }

   public Node getStart() {
      int var1;
      if (this.canFloat() && this.mob.isInWater()) {
         var1 = this.mob.getBlockY();
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos(this.mob.getX(), (double)var1, this.mob.getZ());

         for(BlockState var3 = this.currentContext.getBlockState(var2); var3.is(Blocks.WATER); var3 = this.currentContext.getBlockState(var2)) {
            ++var1;
            var2.set(this.mob.getX(), (double)var1, this.mob.getZ());
         }
      } else {
         var1 = Mth.floor(this.mob.getY() + 0.5);
      }

      BlockPos var5 = BlockPos.containing(this.mob.getX(), (double)var1, this.mob.getZ());
      if (!this.canStartAt(var5)) {
         Iterator var6 = this.iteratePathfindingStartNodeCandidatePositions(this.mob).iterator();

         while(var6.hasNext()) {
            BlockPos var4 = (BlockPos)var6.next();
            if (this.canStartAt(var4)) {
               return super.getStartNode(var4);
            }
         }
      }

      return super.getStartNode(var5);
   }

   protected boolean canStartAt(BlockPos var1) {
      PathType var2 = this.getCachedPathType(var1.getX(), var1.getY(), var1.getZ());
      return this.mob.getPathfindingMalus(var2) >= 0.0F;
   }

   public Target getTarget(double var1, double var3, double var5) {
      return this.getTargetNodeAt(var1, var3, var5);
   }

   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      Node var4 = this.findAcceptedNode(var2.x, var2.y, var2.z + 1);
      if (this.isOpen(var4)) {
         var1[var3++] = var4;
      }

      Node var5 = this.findAcceptedNode(var2.x - 1, var2.y, var2.z);
      if (this.isOpen(var5)) {
         var1[var3++] = var5;
      }

      Node var6 = this.findAcceptedNode(var2.x + 1, var2.y, var2.z);
      if (this.isOpen(var6)) {
         var1[var3++] = var6;
      }

      Node var7 = this.findAcceptedNode(var2.x, var2.y, var2.z - 1);
      if (this.isOpen(var7)) {
         var1[var3++] = var7;
      }

      Node var8 = this.findAcceptedNode(var2.x, var2.y + 1, var2.z);
      if (this.isOpen(var8)) {
         var1[var3++] = var8;
      }

      Node var9 = this.findAcceptedNode(var2.x, var2.y - 1, var2.z);
      if (this.isOpen(var9)) {
         var1[var3++] = var9;
      }

      Node var10 = this.findAcceptedNode(var2.x, var2.y + 1, var2.z + 1);
      if (this.isOpen(var10) && this.hasMalus(var4) && this.hasMalus(var8)) {
         var1[var3++] = var10;
      }

      Node var11 = this.findAcceptedNode(var2.x - 1, var2.y + 1, var2.z);
      if (this.isOpen(var11) && this.hasMalus(var5) && this.hasMalus(var8)) {
         var1[var3++] = var11;
      }

      Node var12 = this.findAcceptedNode(var2.x + 1, var2.y + 1, var2.z);
      if (this.isOpen(var12) && this.hasMalus(var6) && this.hasMalus(var8)) {
         var1[var3++] = var12;
      }

      Node var13 = this.findAcceptedNode(var2.x, var2.y + 1, var2.z - 1);
      if (this.isOpen(var13) && this.hasMalus(var7) && this.hasMalus(var8)) {
         var1[var3++] = var13;
      }

      Node var14 = this.findAcceptedNode(var2.x, var2.y - 1, var2.z + 1);
      if (this.isOpen(var14) && this.hasMalus(var4) && this.hasMalus(var9)) {
         var1[var3++] = var14;
      }

      Node var15 = this.findAcceptedNode(var2.x - 1, var2.y - 1, var2.z);
      if (this.isOpen(var15) && this.hasMalus(var5) && this.hasMalus(var9)) {
         var1[var3++] = var15;
      }

      Node var16 = this.findAcceptedNode(var2.x + 1, var2.y - 1, var2.z);
      if (this.isOpen(var16) && this.hasMalus(var6) && this.hasMalus(var9)) {
         var1[var3++] = var16;
      }

      Node var17 = this.findAcceptedNode(var2.x, var2.y - 1, var2.z - 1);
      if (this.isOpen(var17) && this.hasMalus(var7) && this.hasMalus(var9)) {
         var1[var3++] = var17;
      }

      Node var18 = this.findAcceptedNode(var2.x + 1, var2.y, var2.z - 1);
      if (this.isOpen(var18) && this.hasMalus(var7) && this.hasMalus(var6)) {
         var1[var3++] = var18;
      }

      Node var19 = this.findAcceptedNode(var2.x + 1, var2.y, var2.z + 1);
      if (this.isOpen(var19) && this.hasMalus(var4) && this.hasMalus(var6)) {
         var1[var3++] = var19;
      }

      Node var20 = this.findAcceptedNode(var2.x - 1, var2.y, var2.z - 1);
      if (this.isOpen(var20) && this.hasMalus(var7) && this.hasMalus(var5)) {
         var1[var3++] = var20;
      }

      Node var21 = this.findAcceptedNode(var2.x - 1, var2.y, var2.z + 1);
      if (this.isOpen(var21) && this.hasMalus(var4) && this.hasMalus(var5)) {
         var1[var3++] = var21;
      }

      Node var22 = this.findAcceptedNode(var2.x + 1, var2.y + 1, var2.z - 1);
      if (this.isOpen(var22) && this.hasMalus(var18) && this.hasMalus(var7) && this.hasMalus(var6) && this.hasMalus(var8) && this.hasMalus(var13) && this.hasMalus(var12)) {
         var1[var3++] = var22;
      }

      Node var23 = this.findAcceptedNode(var2.x + 1, var2.y + 1, var2.z + 1);
      if (this.isOpen(var23) && this.hasMalus(var19) && this.hasMalus(var4) && this.hasMalus(var6) && this.hasMalus(var8) && this.hasMalus(var10) && this.hasMalus(var12)) {
         var1[var3++] = var23;
      }

      Node var24 = this.findAcceptedNode(var2.x - 1, var2.y + 1, var2.z - 1);
      if (this.isOpen(var24) && this.hasMalus(var20) && this.hasMalus(var7) && this.hasMalus(var5) && this.hasMalus(var8) && this.hasMalus(var13) && this.hasMalus(var11)) {
         var1[var3++] = var24;
      }

      Node var25 = this.findAcceptedNode(var2.x - 1, var2.y + 1, var2.z + 1);
      if (this.isOpen(var25) && this.hasMalus(var21) && this.hasMalus(var4) && this.hasMalus(var5) && this.hasMalus(var8) && this.hasMalus(var10) && this.hasMalus(var11)) {
         var1[var3++] = var25;
      }

      Node var26 = this.findAcceptedNode(var2.x + 1, var2.y - 1, var2.z - 1);
      if (this.isOpen(var26) && this.hasMalus(var18) && this.hasMalus(var7) && this.hasMalus(var6) && this.hasMalus(var9) && this.hasMalus(var17) && this.hasMalus(var16)) {
         var1[var3++] = var26;
      }

      Node var27 = this.findAcceptedNode(var2.x + 1, var2.y - 1, var2.z + 1);
      if (this.isOpen(var27) && this.hasMalus(var19) && this.hasMalus(var4) && this.hasMalus(var6) && this.hasMalus(var9) && this.hasMalus(var14) && this.hasMalus(var16)) {
         var1[var3++] = var27;
      }

      Node var28 = this.findAcceptedNode(var2.x - 1, var2.y - 1, var2.z - 1);
      if (this.isOpen(var28) && this.hasMalus(var20) && this.hasMalus(var7) && this.hasMalus(var5) && this.hasMalus(var9) && this.hasMalus(var17) && this.hasMalus(var15)) {
         var1[var3++] = var28;
      }

      Node var29 = this.findAcceptedNode(var2.x - 1, var2.y - 1, var2.z + 1);
      if (this.isOpen(var29) && this.hasMalus(var21) && this.hasMalus(var4) && this.hasMalus(var5) && this.hasMalus(var9) && this.hasMalus(var14) && this.hasMalus(var15)) {
         var1[var3++] = var29;
      }

      return var3;
   }

   private boolean hasMalus(@Nullable Node var1) {
      return var1 != null && var1.costMalus >= 0.0F;
   }

   private boolean isOpen(@Nullable Node var1) {
      return var1 != null && !var1.closed;
   }

   @Nullable
   protected Node findAcceptedNode(int var1, int var2, int var3) {
      Node var4 = null;
      PathType var5 = this.getCachedPathType(var1, var2, var3);
      float var6 = this.mob.getPathfindingMalus(var5);
      if (var6 >= 0.0F) {
         var4 = this.getNode(var1, var2, var3);
         var4.type = var5;
         var4.costMalus = Math.max(var4.costMalus, var6);
         if (var5 == PathType.WALKABLE) {
            ++var4.costMalus;
         }
      }

      return var4;
   }

   protected PathType getCachedPathType(int var1, int var2, int var3) {
      return (PathType)this.pathTypeByPosCache.computeIfAbsent(BlockPos.asLong(var1, var2, var3), (var4) -> {
         return this.getPathTypeOfMob(this.currentContext, var1, var2, var3, this.mob);
      });
   }

   public PathType getPathType(PathfindingContext var1, int var2, int var3, int var4) {
      PathType var5 = var1.getPathTypeFromState(var2, var3, var4);
      if (var5 == PathType.OPEN && var3 >= var1.level().getMinY() + 1) {
         BlockPos var6 = new BlockPos(var2, var3 - 1, var4);
         PathType var7 = var1.getPathTypeFromState(var6.getX(), var6.getY(), var6.getZ());
         if (var7 != PathType.DAMAGE_FIRE && var7 != PathType.LAVA) {
            if (var7 == PathType.DAMAGE_OTHER) {
               var5 = PathType.DAMAGE_OTHER;
            } else if (var7 == PathType.COCOA) {
               var5 = PathType.COCOA;
            } else if (var7 == PathType.FENCE) {
               if (!var6.equals(var1.mobPosition())) {
                  var5 = PathType.FENCE;
               }
            } else {
               var5 = var7 != PathType.WALKABLE && var7 != PathType.OPEN && var7 != PathType.WATER ? PathType.WALKABLE : PathType.OPEN;
            }
         } else {
            var5 = PathType.DAMAGE_FIRE;
         }
      }

      if (var5 == PathType.WALKABLE || var5 == PathType.OPEN) {
         var5 = checkNeighbourBlocks(var1, var2, var3, var4, var5);
      }

      return var5;
   }

   private Iterable<BlockPos> iteratePathfindingStartNodeCandidatePositions(Mob var1) {
      AABB var2 = var1.getBoundingBox();
      boolean var3 = var2.getSize() < 1.0;
      if (!var3) {
         return List.of(BlockPos.containing(var2.minX, (double)var1.getBlockY(), var2.minZ), BlockPos.containing(var2.minX, (double)var1.getBlockY(), var2.maxZ), BlockPos.containing(var2.maxX, (double)var1.getBlockY(), var2.minZ), BlockPos.containing(var2.maxX, (double)var1.getBlockY(), var2.maxZ));
      } else {
         double var4 = Math.max(0.0, 1.100000023841858 - var2.getZsize());
         double var6 = Math.max(0.0, 1.100000023841858 - var2.getXsize());
         double var8 = Math.max(0.0, 1.100000023841858 - var2.getYsize());
         AABB var10 = var2.inflate(var6, var8, var4);
         return BlockPos.randomBetweenClosed(var1.getRandom(), 10, Mth.floor(var10.minX), Mth.floor(var10.minY), Mth.floor(var10.minZ), Mth.floor(var10.maxX), Mth.floor(var10.maxY), Mth.floor(var10.maxZ));
      }
   }
}
