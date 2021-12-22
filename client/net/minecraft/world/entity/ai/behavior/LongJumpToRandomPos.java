package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LongJumpToRandomPos<E extends Mob> extends Behavior<E> {
   private static final int FIND_JUMP_TRIES = 20;
   private static final int PREPARE_JUMP_DURATION = 40;
   private static final int MIN_PATHFIND_DISTANCE_TO_VALID_JUMP = 8;
   public static final int TIME_OUT_DURATION = 200;
   private final UniformInt timeBetweenLongJumps;
   private final int maxLongJumpHeight;
   private final int maxLongJumpWidth;
   private final float maxJumpVelocity;
   private final List<LongJumpToRandomPos.PossibleJump> jumpCandidates = new ArrayList();
   private Optional<Vec3> initialPosition = Optional.empty();
   private Optional<LongJumpToRandomPos.PossibleJump> chosenJump = Optional.empty();
   private int findJumpTries;
   private long prepareJumpStart;
   private Function<E, SoundEvent> getJumpSound;

   public LongJumpToRandomPos(UniformInt var1, int var2, int var3, float var4, Function<E, SoundEvent> var5) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), 200);
      this.timeBetweenLongJumps = var1;
      this.maxLongJumpHeight = var2;
      this.maxLongJumpWidth = var3;
      this.maxJumpVelocity = var4;
      this.getJumpSound = var5;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      return var2.isOnGround() && !var1.getBlockState(var2.blockPosition()).is(Blocks.HONEY_BLOCK);
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      boolean var5 = this.initialPosition.isPresent() && ((Vec3)this.initialPosition.get()).equals(var2.position()) && this.findJumpTries > 0 && (this.chosenJump.isPresent() || !this.jumpCandidates.isEmpty());
      if (!var5 && !var2.getBrain().getMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isPresent()) {
         var2.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object)(this.timeBetweenLongJumps.sample(var1.random) / 2));
      }

      return var5;
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      this.chosenJump = Optional.empty();
      this.findJumpTries = 20;
      this.jumpCandidates.clear();
      this.initialPosition = Optional.of(var2.position());
      BlockPos var5 = var2.blockPosition();
      int var6 = var5.getX();
      int var7 = var5.getY();
      int var8 = var5.getZ();
      Iterable var9 = BlockPos.betweenClosed(var6 - this.maxLongJumpWidth, var7 - this.maxLongJumpHeight, var8 - this.maxLongJumpWidth, var6 + this.maxLongJumpWidth, var7 + this.maxLongJumpHeight, var8 + this.maxLongJumpWidth);
      PathNavigation var10 = var2.getNavigation();
      Iterator var11 = var9.iterator();

      while(true) {
         BlockPos var12;
         double var13;
         do {
            if (!var11.hasNext()) {
               return;
            }

            var12 = (BlockPos)var11.next();
            var13 = var12.distSqr(var5);
         } while(var6 == var12.getX() && var8 == var12.getZ());

         if (var10.isStableDestination(var12) && var2.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(var2.level, var12.mutable())) == 0.0F) {
            Optional var15 = this.calculateOptimalJumpVector(var2, Vec3.atCenterOf(var12));
            var15.ifPresent((var4) -> {
               this.jumpCandidates.add(new LongJumpToRandomPos.PossibleJump(new BlockPos(var12), var4, Mth.ceil(var13)));
            });
         }
      }
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      if (this.chosenJump.isPresent()) {
         if (var3 - this.prepareJumpStart >= 40L) {
            var2.setYRot(var2.yBodyRot);
            var2.setDiscardFriction(true);
            Vec3 var5 = ((LongJumpToRandomPos.PossibleJump)this.chosenJump.get()).getJumpVector();
            double var6 = var5.length();
            double var8 = var6 + var2.getJumpBoostPower();
            var2.setDeltaMovement(var5.scale(var8 / var6));
            var2.getBrain().setMemory(MemoryModuleType.LONG_JUMP_MID_JUMP, (Object)true);
            var1.playSound((Player)null, var2, (SoundEvent)this.getJumpSound.apply(var2), SoundSource.NEUTRAL, 1.0F, 1.0F);
         }
      } else {
         --this.findJumpTries;
         Optional var10 = WeightedRandom.getRandomItem(var1.random, this.jumpCandidates);
         if (var10.isPresent()) {
            this.jumpCandidates.remove(var10.get());
            var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosTracker(((LongJumpToRandomPos.PossibleJump)var10.get()).getJumpTarget())));
            PathNavigation var11 = var2.getNavigation();
            Path var7 = var11.createPath(((LongJumpToRandomPos.PossibleJump)var10.get()).getJumpTarget(), 0, 8);
            if (var7 == null || !var7.canReach()) {
               this.chosenJump = var10;
               this.prepareJumpStart = var3;
            }
         }
      }

   }

   private Optional<Vec3> calculateOptimalJumpVector(Mob var1, Vec3 var2) {
      Optional var3 = Optional.empty();

      for(int var4 = 65; var4 < 85; var4 += 5) {
         Optional var5 = this.calculateJumpVectorForAngle(var1, var2, var4);
         if (!var3.isPresent() || var5.isPresent() && ((Vec3)var5.get()).lengthSqr() < ((Vec3)var3.get()).lengthSqr()) {
            var3 = var5;
         }
      }

      return var3;
   }

   private Optional<Vec3> calculateJumpVectorForAngle(Mob var1, Vec3 var2, int var3) {
      Vec3 var4 = var1.position();
      Vec3 var5 = (new Vec3(var2.field_414 - var4.field_414, 0.0D, var2.field_416 - var4.field_416)).normalize().scale(0.5D);
      var2 = var2.subtract(var5);
      Vec3 var6 = var2.subtract(var4);
      float var7 = (float)var3 * 3.1415927F / 180.0F;
      double var8 = Math.atan2(var6.field_416, var6.field_414);
      double var10 = var6.subtract(0.0D, var6.field_415, 0.0D).lengthSqr();
      double var12 = Math.sqrt(var10);
      double var14 = var6.field_415;
      double var16 = Math.sin((double)(2.0F * var7));
      double var18 = 0.08D;
      double var20 = Math.pow(Math.cos((double)var7), 2.0D);
      double var22 = Math.sin((double)var7);
      double var24 = Math.cos((double)var7);
      double var26 = Math.sin(var8);
      double var28 = Math.cos(var8);
      double var30 = var10 * 0.08D / (var12 * var16 - 2.0D * var14 * var20);
      if (var30 < 0.0D) {
         return Optional.empty();
      } else {
         double var32 = Math.sqrt(var30);
         if (var32 > (double)this.maxJumpVelocity) {
            return Optional.empty();
         } else {
            double var34 = var32 * var24;
            double var36 = var32 * var22;
            int var38 = Mth.ceil(var12 / var34) * 2;
            double var39 = 0.0D;
            Vec3 var41 = null;

            for(int var42 = 0; var42 < var38 - 1; ++var42) {
               var39 += var12 / (double)var38;
               double var43 = var22 / var24 * var39 - Math.pow(var39, 2.0D) * 0.08D / (2.0D * var30 * Math.pow(var24, 2.0D));
               double var45 = var39 * var28;
               double var47 = var39 * var26;
               Vec3 var49 = new Vec3(var4.field_414 + var45, var4.field_415 + var43, var4.field_416 + var47);
               if (var41 != null && !this.isClearTransition(var1, var41, var49)) {
                  return Optional.empty();
               }

               var41 = var49;
            }

            return Optional.of((new Vec3(var34 * var28, var36, var34 * var26)).scale(0.949999988079071D));
         }
      }
   }

   private boolean isClearTransition(Mob var1, Vec3 var2, Vec3 var3) {
      EntityDimensions var4 = var1.getDimensions(Pose.LONG_JUMPING);
      Vec3 var5 = var3.subtract(var2);
      double var6 = (double)Math.min(var4.width, var4.height);
      int var8 = Mth.ceil(var5.length() / var6);
      Vec3 var9 = var5.normalize();
      Vec3 var10 = var2;

      for(int var11 = 0; var11 < var8; ++var11) {
         var10 = var11 == var8 - 1 ? var3 : var10.add(var9.scale(var6 * 0.8999999761581421D));
         AABB var12 = var4.makeBoundingBox(var10);
         if (!var1.level.noCollision(var1, var12)) {
            return false;
         }
      }

      return true;
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Mob)var2, var3);
   }

   public static class PossibleJump extends WeightedEntry.IntrusiveBase {
      private final BlockPos jumpTarget;
      private final Vec3 jumpVector;

      public PossibleJump(BlockPos var1, Vec3 var2, int var3) {
         super(var3);
         this.jumpTarget = var1;
         this.jumpVector = var2;
      }

      public BlockPos getJumpTarget() {
         return this.jumpTarget;
      }

      public Vec3 getJumpVector() {
         return this.jumpVector;
      }
   }
}
