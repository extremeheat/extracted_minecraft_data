package net.minecraft.world.entity.ai.navigation;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public abstract class PathNavigation {
   protected final Mob mob;
   protected final Level level;
   @Nullable
   protected Path path;
   protected double speedModifier;
   private final AttributeInstance dist;
   protected int tick;
   protected int lastStuckCheck;
   protected Vec3 lastStuckCheckPos;
   protected Vec3 timeoutCachedNode;
   protected long timeoutTimer;
   protected long lastTimeoutCheck;
   protected double timeoutLimit;
   protected float maxDistanceToWaypoint;
   protected boolean hasDelayedRecomputation;
   protected long timeLastRecompute;
   protected NodeEvaluator nodeEvaluator;
   private BlockPos targetPos;
   private int reachRange;
   private PathFinder pathFinder;

   public PathNavigation(Mob var1, Level var2) {
      super();
      this.lastStuckCheckPos = Vec3.ZERO;
      this.timeoutCachedNode = Vec3.ZERO;
      this.maxDistanceToWaypoint = 0.5F;
      this.mob = var1;
      this.level = var2;
      this.dist = var1.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
      this.pathFinder = this.createPathFinder(Mth.floor(this.dist.getValue() * 16.0D));
   }

   public BlockPos getTargetPos() {
      return this.targetPos;
   }

   protected abstract PathFinder createPathFinder(int var1);

   public void setSpeedModifier(double var1) {
      this.speedModifier = var1;
   }

   public float getMaxDist() {
      return (float)this.dist.getValue();
   }

   public boolean hasDelayedRecomputation() {
      return this.hasDelayedRecomputation;
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
   public Path createPath(BlockPos var1, int var2) {
      return this.createPath(ImmutableSet.of(var1), 8, false, var2);
   }

   @Nullable
   public Path createPath(Entity var1, int var2) {
      return this.createPath(ImmutableSet.of(new BlockPos(var1)), 16, true, var2);
   }

   @Nullable
   protected Path createPath(Set<BlockPos> var1, int var2, boolean var3, int var4) {
      if (var1.isEmpty()) {
         return null;
      } else if (this.mob.y < 0.0D) {
         return null;
      } else if (!this.canUpdatePath()) {
         return null;
      } else if (this.path != null && !this.path.isDone() && var1.contains(this.targetPos)) {
         return this.path;
      } else {
         this.level.getProfiler().push("pathfind");
         float var5 = this.getMaxDist();
         BlockPos var6 = var3 ? (new BlockPos(this.mob)).above() : new BlockPos(this.mob);
         int var7 = (int)(var5 + (float)var2);
         PathNavigationRegion var8 = new PathNavigationRegion(this.level, var6.offset(-var7, -var7, -var7), var6.offset(var7, var7, var7));
         Path var9 = this.pathFinder.findPath(var8, this.mob, var1, var5, var4);
         this.level.getProfiler().pop();
         if (var9 != null && var9.getTarget() != null) {
            this.targetPos = var9.getTarget();
            this.reachRange = var4;
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

         this.trimPath();
         if (this.path.getSize() <= 0) {
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
            this.updatePath();
         } else if (this.path != null && this.path.getIndex() < this.path.getSize()) {
            var1 = this.getTempMobPos();
            Vec3 var2 = this.path.getPos(this.mob, this.path.getIndex());
            if (var1.y > var2.y && !this.mob.onGround && Mth.floor(var1.x) == Mth.floor(var2.x) && Mth.floor(var1.z) == Mth.floor(var2.z)) {
               this.path.setIndex(this.path.getIndex() + 1);
            }
         }

         DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
         if (!this.isDone()) {
            var1 = this.path.currentPos(this.mob);
            BlockPos var3 = new BlockPos(var1);
            this.mob.getMoveControl().setWantedPosition(var1.x, this.level.getBlockState(var3.below()).isAir() ? var1.y : WalkNodeEvaluator.getFloorLevel(this.level, var3), var1.z, this.speedModifier);
         }
      }
   }

   protected void updatePath() {
      Vec3 var1 = this.getTempMobPos();
      this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
      Vec3 var2 = this.path.currentPos();
      if (Math.abs(this.mob.x - (var2.x + 0.5D)) < (double)this.maxDistanceToWaypoint && Math.abs(this.mob.z - (var2.z + 0.5D)) < (double)this.maxDistanceToWaypoint && Math.abs(this.mob.y - var2.y) < 1.0D) {
         this.path.setIndex(this.path.getIndex() + 1);
      }

      this.doStuckDetection(var1);
   }

   protected void doStuckDetection(Vec3 var1) {
      if (this.tick - this.lastStuckCheck > 100) {
         if (var1.distanceToSqr(this.lastStuckCheckPos) < 2.25D) {
            this.stop();
         }

         this.lastStuckCheck = this.tick;
         this.lastStuckCheckPos = var1;
      }

      if (this.path != null && !this.path.isDone()) {
         Vec3 var2 = this.path.currentPos();
         if (var2.equals(this.timeoutCachedNode)) {
            this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck;
         } else {
            this.timeoutCachedNode = var2;
            double var3 = var1.distanceTo(this.timeoutCachedNode);
            this.timeoutLimit = this.mob.getSpeed() > 0.0F ? var3 / (double)this.mob.getSpeed() * 1000.0D : 0.0D;
         }

         if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 3.0D) {
            this.timeoutCachedNode = Vec3.ZERO;
            this.timeoutTimer = 0L;
            this.timeoutLimit = 0.0D;
            this.stop();
         }

         this.lastTimeoutCheck = Util.getMillis();
      }

   }

   public boolean isDone() {
      return this.path == null || this.path.isDone();
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
         for(int var1 = 0; var1 < this.path.getSize(); ++var1) {
            Node var2 = this.path.get(var1);
            Node var3 = var1 + 1 < this.path.getSize() ? this.path.get(var1 + 1) : null;
            BlockState var4 = this.level.getBlockState(new BlockPos(var2.x, var2.y, var2.z));
            Block var5 = var4.getBlock();
            if (var5 == Blocks.CAULDRON) {
               this.path.set(var1, var2.cloneMove(var2.x, var2.y + 1, var2.z));
               if (var3 != null && var2.y >= var3.y) {
                  this.path.set(var1 + 1, var3.cloneMove(var3.x, var2.y + 1, var3.z));
               }
            }
         }

      }
   }

   protected abstract boolean canMoveDirectly(Vec3 var1, Vec3 var2, int var3, int var4, int var5);

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

   public void recomputePath(BlockPos var1) {
      if (this.path != null && !this.path.isDone() && this.path.getSize() != 0) {
         Node var2 = this.path.last();
         Vec3 var3 = new Vec3(((double)var2.x + this.mob.x) / 2.0D, ((double)var2.y + this.mob.y) / 2.0D, ((double)var2.z + this.mob.z) / 2.0D);
         if (var1.closerThan(var3, (double)(this.path.getSize() - this.path.getIndex()))) {
            this.recomputePath();
         }

      }
   }
}
