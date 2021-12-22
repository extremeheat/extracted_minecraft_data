package net.minecraft.world.entity.ai.navigation;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public abstract class PathNavigation {
   private static final int MAX_TIME_RECOMPUTE = 20;
   protected final Mob mob;
   protected final Level level;
   @Nullable
   protected Path path;
   protected double speedModifier;
   protected int tick;
   protected int lastStuckCheck;
   protected Vec3 lastStuckCheckPos;
   protected Vec3i timeoutCachedNode;
   protected long timeoutTimer;
   protected long lastTimeoutCheck;
   protected double timeoutLimit;
   protected float maxDistanceToWaypoint;
   protected boolean hasDelayedRecomputation;
   protected long timeLastRecompute;
   protected NodeEvaluator nodeEvaluator;
   @Nullable
   private BlockPos targetPos;
   private int reachRange;
   private float maxVisitedNodesMultiplier;
   private final PathFinder pathFinder;
   private boolean isStuck;

   public PathNavigation(Mob var1, Level var2) {
      super();
      this.lastStuckCheckPos = Vec3.ZERO;
      this.timeoutCachedNode = Vec3i.ZERO;
      this.maxDistanceToWaypoint = 0.5F;
      this.maxVisitedNodesMultiplier = 1.0F;
      this.mob = var1;
      this.level = var2;
      int var3 = Mth.floor(var1.getAttributeValue(Attributes.FOLLOW_RANGE) * 16.0D);
      this.pathFinder = this.createPathFinder(var3);
   }

   public void resetMaxVisitedNodesMultiplier() {
      this.maxVisitedNodesMultiplier = 1.0F;
   }

   public void setMaxVisitedNodesMultiplier(float var1) {
      this.maxVisitedNodesMultiplier = var1;
   }

   @Nullable
   public BlockPos getTargetPos() {
      return this.targetPos;
   }

   protected abstract PathFinder createPathFinder(int var1);

   public void setSpeedModifier(double var1) {
      this.speedModifier = var1;
   }

   public void recomputePath() {
      if (this.level.getGameTime() - this.timeLastRecompute > 20L) {
         if (this.targetPos != null) {
            this.path = null;
            this.path = this.createPath(this.targetPos, this.reachRange);
            this.timeLastRecompute = this.level.getGameTime();
            this.hasDelayedRecomputation = false;
         }
      } else {
         this.hasDelayedRecomputation = true;
      }

   }

   @Nullable
   public final Path createPath(double var1, double var3, double var5, int var7) {
      return this.createPath(new BlockPos(var1, var3, var5), var7);
   }

   @Nullable
   public Path createPath(Stream<BlockPos> var1, int var2) {
      return this.createPath((Set)var1.collect(Collectors.toSet()), 8, false, var2);
   }

   @Nullable
   public Path createPath(Set<BlockPos> var1, int var2) {
      return this.createPath(var1, 8, false, var2);
   }

   @Nullable
   public Path createPath(BlockPos var1, int var2) {
      return this.createPath(ImmutableSet.of(var1), 8, false, var2);
   }

   @Nullable
   public Path createPath(BlockPos var1, int var2, int var3) {
      return this.createPath(ImmutableSet.of(var1), 8, false, var2, (float)var3);
   }

   @Nullable
   public Path createPath(Entity var1, int var2) {
      return this.createPath(ImmutableSet.of(var1.blockPosition()), 16, true, var2);
   }

   @Nullable
   protected Path createPath(Set<BlockPos> var1, int var2, boolean var3, int var4) {
      return this.createPath(var1, var2, var3, var4, (float)this.mob.getAttributeValue(Attributes.FOLLOW_RANGE));
   }

   @Nullable
   protected Path createPath(Set<BlockPos> var1, int var2, boolean var3, int var4, float var5) {
      if (var1.isEmpty()) {
         return null;
      } else if (this.mob.getY() < (double)this.level.getMinBuildHeight()) {
         return null;
      } else if (!this.canUpdatePath()) {
         return null;
      } else if (this.path != null && !this.path.isDone() && var1.contains(this.targetPos)) {
         return this.path;
      } else {
         this.level.getProfiler().push("pathfind");
         BlockPos var6 = var3 ? this.mob.blockPosition().above() : this.mob.blockPosition();
         int var7 = (int)(var5 + (float)var2);
         PathNavigationRegion var8 = new PathNavigationRegion(this.level, var6.offset(-var7, -var7, -var7), var6.offset(var7, var7, var7));
         Path var9 = this.pathFinder.findPath(var8, this.mob, var1, var5, var4, this.maxVisitedNodesMultiplier);
         this.level.getProfiler().pop();
         if (var9 != null && var9.getTarget() != null) {
            this.targetPos = var9.getTarget();
            this.reachRange = var4;
            this.resetStuckTimeout();
         }

         return var9;
      }
   }

   public boolean moveTo(double var1, double var3, double var5, double var7) {
      return this.moveTo(this.createPath(var1, var3, var5, 1), var7);
   }

   public boolean moveTo(Entity var1, double var2) {
      Path var4 = this.createPath((Entity)var1, 1);
      return var4 != null && this.moveTo(var4, var2);
   }

   public boolean moveTo(@Nullable Path var1, double var2) {
      if (var1 == null) {
         this.path = null;
         return false;
      } else {
         if (!var1.sameAs(this.path)) {
            this.path = var1;
         }

         if (this.isDone()) {
            return false;
         } else {
            this.trimPath();
            if (this.path.getNodeCount() <= 0) {
               return false;
            } else {
               this.speedModifier = var2;
               Vec3 var4 = this.getTempMobPos();
               this.lastStuckCheck = this.tick;
               this.lastStuckCheckPos = var4;
               return true;
            }
         }
      }
   }

   @Nullable
   public Path getPath() {
      return this.path;
   }

   public void tick() {
      ++this.tick;
      if (this.hasDelayedRecomputation) {
         this.recomputePath();
      }

      if (!this.isDone()) {
         Vec3 var1;
         if (this.canUpdatePath()) {
            this.followThePath();
         } else if (this.path != null && !this.path.isDone()) {
            var1 = this.getTempMobPos();
            Vec3 var2 = this.path.getNextEntityPos(this.mob);
            if (var1.field_415 > var2.field_415 && !this.mob.isOnGround() && Mth.floor(var1.field_414) == Mth.floor(var2.field_414) && Mth.floor(var1.field_416) == Mth.floor(var2.field_416)) {
               this.path.advance();
            }
         }

         DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
         if (!this.isDone()) {
            var1 = this.path.getNextEntityPos(this.mob);
            this.mob.getMoveControl().setWantedPosition(var1.field_414, this.getGroundY(var1), var1.field_416, this.speedModifier);
         }
      }
   }

   protected double getGroundY(Vec3 var1) {
      BlockPos var2 = new BlockPos(var1);
      return this.level.getBlockState(var2.below()).isAir() ? var1.field_415 : WalkNodeEvaluator.getFloorLevel(this.level, var2);
   }

   protected void followThePath() {
      Vec3 var1 = this.getTempMobPos();
      this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
      BlockPos var2 = this.path.getNextNodePos();
      double var3 = Math.abs(this.mob.getX() - ((double)var2.getX() + 0.5D));
      double var5 = Math.abs(this.mob.getY() - (double)var2.getY());
      double var7 = Math.abs(this.mob.getZ() - ((double)var2.getZ() + 0.5D));
      boolean var9 = var3 < (double)this.maxDistanceToWaypoint && var7 < (double)this.maxDistanceToWaypoint && var5 < 1.0D;
      if (var9 || this.mob.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(var1)) {
         this.path.advance();
      }

      this.doStuckDetection(var1);
   }

   private boolean shouldTargetNextNodeInDirection(Vec3 var1) {
      if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
         return false;
      } else {
         Vec3 var2 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
         if (!var1.closerThan(var2, 2.0D)) {
            return false;
         } else if (this.canMoveDirectly(var1, this.path.getNextEntityPos(this.mob))) {
            return true;
         } else {
            Vec3 var3 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
            Vec3 var4 = var3.subtract(var2);
            Vec3 var5 = var1.subtract(var2);
            return var4.dot(var5) > 0.0D;
         }
      }
   }

   protected void doStuckDetection(Vec3 var1) {
      if (this.tick - this.lastStuckCheck > 100) {
         if (var1.distanceToSqr(this.lastStuckCheckPos) < 2.25D) {
            this.isStuck = true;
            this.stop();
         } else {
            this.isStuck = false;
         }

         this.lastStuckCheck = this.tick;
         this.lastStuckCheckPos = var1;
      }

      if (this.path != null && !this.path.isDone()) {
         BlockPos var2 = this.path.getNextNodePos();
         if (var2.equals(this.timeoutCachedNode)) {
            this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck;
         } else {
            this.timeoutCachedNode = var2;
            double var3 = var1.distanceTo(Vec3.atBottomCenterOf(this.timeoutCachedNode));
            this.timeoutLimit = this.mob.getSpeed() > 0.0F ? var3 / (double)this.mob.getSpeed() * 1000.0D : 0.0D;
         }

         if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 3.0D) {
            this.timeoutPath();
         }

         this.lastTimeoutCheck = Util.getMillis();
      }

   }

   private void timeoutPath() {
      this.resetStuckTimeout();
      this.stop();
   }

   private void resetStuckTimeout() {
      this.timeoutCachedNode = Vec3i.ZERO;
      this.timeoutTimer = 0L;
      this.timeoutLimit = 0.0D;
      this.isStuck = false;
   }

   public boolean isDone() {
      return this.path == null || this.path.isDone();
   }

   public boolean isInProgress() {
      return !this.isDone();
   }

   public void stop() {
      this.path = null;
   }

   protected abstract Vec3 getTempMobPos();

   protected abstract boolean canUpdatePath();

   protected boolean isInLiquid() {
      return this.mob.isInWaterOrBubble() || this.mob.isInLava();
   }

   protected void trimPath() {
      if (this.path != null) {
         for(int var1 = 0; var1 < this.path.getNodeCount(); ++var1) {
            Node var2 = this.path.getNode(var1);
            Node var3 = var1 + 1 < this.path.getNodeCount() ? this.path.getNode(var1 + 1) : null;
            BlockState var4 = this.level.getBlockState(new BlockPos(var2.field_333, var2.field_334, var2.field_335));
            if (var4.is(BlockTags.CAULDRONS)) {
               this.path.replaceNode(var1, var2.cloneAndMove(var2.field_333, var2.field_334 + 1, var2.field_335));
               if (var3 != null && var2.field_334 >= var3.field_334) {
                  this.path.replaceNode(var1 + 1, var2.cloneAndMove(var3.field_333, var2.field_334 + 1, var3.field_335));
               }
            }
         }

      }
   }

   protected boolean canMoveDirectly(Vec3 var1, Vec3 var2) {
      return false;
   }

   public boolean isStableDestination(BlockPos var1) {
      BlockPos var2 = var1.below();
      return this.level.getBlockState(var2).isSolidRender(this.level, var2);
   }

   public NodeEvaluator getNodeEvaluator() {
      return this.nodeEvaluator;
   }

   public void setCanFloat(boolean var1) {
      this.nodeEvaluator.setCanFloat(var1);
   }

   public boolean canFloat() {
      return this.nodeEvaluator.canFloat();
   }

   public boolean shouldRecomputePath(BlockPos var1) {
      if (this.hasDelayedRecomputation) {
         return false;
      } else if (this.path != null && !this.path.isDone() && this.path.getNodeCount() != 0) {
         Node var2 = this.path.getEndNode();
         Vec3 var3 = new Vec3(((double)var2.field_333 + this.mob.getX()) / 2.0D, ((double)var2.field_334 + this.mob.getY()) / 2.0D, ((double)var2.field_335 + this.mob.getZ()) / 2.0D);
         return var1.closerThan(var3, (double)(this.path.getNodeCount() - this.path.getNextNodeIndex()));
      } else {
         return false;
      }
   }

   public float getMaxDistanceToWaypoint() {
      return this.maxDistanceToWaypoint;
   }

   public boolean isStuck() {
      return this.isStuck;
   }
}
