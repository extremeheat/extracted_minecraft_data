package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class LongJumpToRandomPos<E extends Mob> extends Behavior<E> {
   protected static final int FIND_JUMP_TRIES = 20;
   private static final int PREPARE_JUMP_DURATION = 40;
   protected static final int MIN_PATHFIND_DISTANCE_TO_VALID_JUMP = 8;
   private static final int TIME_OUT_DURATION = 200;
   private static final List<Integer> ALLOWED_ANGLES = Lists.newArrayList(new Integer[]{65, 70, 75, 80});
   private final UniformInt timeBetweenLongJumps;
   protected final int maxLongJumpHeight;
   protected final int maxLongJumpWidth;
   protected final float maxJumpVelocity;
   protected List<LongJumpToRandomPos.PossibleJump> jumpCandidates = Lists.newArrayList();
   protected Optional<Vec3> initialPosition = Optional.empty();
   @Nullable
   protected Vec3 chosenJump;
   protected int findJumpTries;
   protected long prepareJumpStart;
   private final Function<E, SoundEvent> getJumpSound;
   private final BiPredicate<E, BlockPos> acceptableLandingSpot;

   public LongJumpToRandomPos(UniformInt var1, int var2, int var3, float var4, Function<E, SoundEvent> var5) {
      this(var1, var2, var3, var4, var5, LongJumpToRandomPos::defaultAcceptableLandingSpot);
   }

   public static <E extends Mob> boolean defaultAcceptableLandingSpot(E var0, BlockPos var1) {
      Level var2 = var0.level;
      BlockPos var3 = var1.below();
      return var2.getBlockState(var3).isSolidRender(var2, var3)
         && var0.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(var2, var1.mutable())) == 0.0F;
   }

   public LongJumpToRandomPos(UniformInt var1, int var2, int var3, float var4, Function<E, SoundEvent> var5, BiPredicate<E, BlockPos> var6) {
      super(
         ImmutableMap.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryStatus.REGISTERED,
            MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.LONG_JUMP_MID_JUMP,
            MemoryStatus.VALUE_ABSENT
         ),
         200
      );
      this.timeBetweenLongJumps = var1;
      this.maxLongJumpHeight = var2;
      this.maxLongJumpWidth = var3;
      this.maxJumpVelocity = var4;
      this.getJumpSound = var5;
      this.acceptableLandingSpot = var6;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      boolean var3 = var2.isOnGround() && !var2.isInWater() && !var2.isInLava() && !var1.getBlockState(var2.blockPosition()).is(Blocks.HONEY_BLOCK);
      if (!var3) {
         var2.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenLongJumps.sample(var1.random) / 2);
      }

      return var3;
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      boolean var5 = this.initialPosition.isPresent()
         && this.initialPosition.get().equals(var2.position())
         && this.findJumpTries > 0
         && !var2.isInWaterOrBubble()
         && (this.chosenJump != null || !this.jumpCandidates.isEmpty());
      if (!var5 && var2.getBrain().getMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
         var2.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenLongJumps.sample(var1.random) / 2);
         var2.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
      }

      return var5;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      this.chosenJump = null;
      this.findJumpTries = 20;
      this.initialPosition = Optional.of(var2.position());
      BlockPos var5 = var2.blockPosition();
      int var6 = var5.getX();
      int var7 = var5.getY();
      int var8 = var5.getZ();
      this.jumpCandidates = BlockPos.betweenClosedStream(
            var6 - this.maxLongJumpWidth,
            var7 - this.maxLongJumpHeight,
            var8 - this.maxLongJumpWidth,
            var6 + this.maxLongJumpWidth,
            var7 + this.maxLongJumpHeight,
            var8 + this.maxLongJumpWidth
         )
         .filter(var1x -> !var1x.equals(var5))
         .map(var1x -> new LongJumpToRandomPos.PossibleJump(var1x.immutable(), Mth.ceil(var5.distSqr(var1x))))
         .collect(Collectors.toCollection(Lists::newArrayList));
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      if (this.chosenJump != null) {
         if (var3 - this.prepareJumpStart >= 40L) {
            var2.setYRot(var2.yBodyRot);
            var2.setDiscardFriction(true);
            double var5 = this.chosenJump.length();
            double var7 = var5 + var2.getJumpBoostPower();
            var2.setDeltaMovement(this.chosenJump.scale(var7 / var5));
            var2.getBrain().setMemory(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
            var1.playSound(null, var2, this.getJumpSound.apply((E)var2), SoundSource.NEUTRAL, 1.0F, 1.0F);
         }
      } else {
         --this.findJumpTries;
         this.pickCandidate(var1, (E)var2, var3);
      }
   }

   protected void pickCandidate(ServerLevel var1, E var2, long var3) {
      while(!this.jumpCandidates.isEmpty()) {
         Optional var5 = this.getJumpCandidate(var1);
         if (!var5.isEmpty()) {
            LongJumpToRandomPos.PossibleJump var6 = (LongJumpToRandomPos.PossibleJump)var5.get();
            BlockPos var7 = var6.getJumpTarget();
            if (this.isAcceptableLandingPosition(var1, (E)var2, var7)) {
               Vec3 var8 = Vec3.atCenterOf(var7);
               Vec3 var9 = this.calculateOptimalJumpVector(var2, var8);
               if (var9 != null) {
                  var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(var7));
                  PathNavigation var10 = var2.getNavigation();
                  Path var11 = var10.createPath(var7, 0, 8);
                  if (var11 == null || !var11.canReach()) {
                     this.chosenJump = var9;
                     this.prepareJumpStart = var3;
                     return;
                  }
               }
            }
         }
      }
   }

   protected Optional<LongJumpToRandomPos.PossibleJump> getJumpCandidate(ServerLevel var1) {
      Optional var2 = WeightedRandom.getRandomItem(var1.random, this.jumpCandidates);
      var2.ifPresent(this.jumpCandidates::remove);
      return var2;
   }

   private boolean isAcceptableLandingPosition(ServerLevel var1, E var2, BlockPos var3) {
      BlockPos var4 = var2.blockPosition();
      int var5 = var4.getX();
      int var6 = var4.getZ();
      return var5 == var3.getX() && var6 == var3.getZ() ? false : this.acceptableLandingSpot.test((E)var2, var3);
   }

   @Nullable
   protected Vec3 calculateOptimalJumpVector(Mob var1, Vec3 var2) {
      ArrayList var3 = Lists.newArrayList(ALLOWED_ANGLES);
      Collections.shuffle(var3);

      for(int var5 : var3) {
         Vec3 var6 = this.calculateJumpVectorForAngle(var1, var2, var5);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }

   @Nullable
   private Vec3 calculateJumpVectorForAngle(Mob var1, Vec3 var2, int var3) {
      Vec3 var4 = var1.position();
      Vec3 var5 = new Vec3(var2.x - var4.x, 0.0, var2.z - var4.z).normalize().scale(0.5);
      var2 = var2.subtract(var5);
      Vec3 var6 = var2.subtract(var4);
      float var7 = (float)var3 * 3.1415927F / 180.0F;
      double var8 = Math.atan2(var6.z, var6.x);
      double var10 = var6.subtract(0.0, var6.y, 0.0).lengthSqr();
      double var12 = Math.sqrt(var10);
      double var14 = var6.y;
      double var16 = Math.sin((double)(2.0F * var7));
      double var18 = 0.08;
      double var20 = Math.pow(Math.cos((double)var7), 2.0);
      double var22 = Math.sin((double)var7);
      double var24 = Math.cos((double)var7);
      double var26 = Math.sin(var8);
      double var28 = Math.cos(var8);
      double var30 = var10 * 0.08 / (var12 * var16 - 2.0 * var14 * var20);
      if (var30 < 0.0) {
         return null;
      } else {
         double var32 = Math.sqrt(var30);
         if (var32 > (double)this.maxJumpVelocity) {
            return null;
         } else {
            double var34 = var32 * var24;
            double var36 = var32 * var22;
            int var38 = Mth.ceil(var12 / var34) * 2;
            double var39 = 0.0;
            Vec3 var41 = null;
            EntityDimensions var42 = var1.getDimensions(Pose.LONG_JUMPING);

            for(int var43 = 0; var43 < var38 - 1; ++var43) {
               var39 += var12 / (double)var38;
               double var44 = var22 / var24 * var39 - Math.pow(var39, 2.0) * 0.08 / (2.0 * var30 * Math.pow(var24, 2.0));
               double var46 = var39 * var28;
               double var48 = var39 * var26;
               Vec3 var50 = new Vec3(var4.x + var46, var4.y + var44, var4.z + var48);
               if (var41 != null && !this.isClearTransition(var1, var42, var41, var50)) {
                  return null;
               }

               var41 = var50;
            }

            return new Vec3(var34 * var28, var36, var34 * var26).scale(0.949999988079071);
         }
      }
   }

   private boolean isClearTransition(Mob var1, EntityDimensions var2, Vec3 var3, Vec3 var4) {
      Vec3 var5 = var4.subtract(var3);
      double var6 = (double)Math.min(var2.width, var2.height);
      int var8 = Mth.ceil(var5.length() / var6);
      Vec3 var9 = var5.normalize();
      Vec3 var10 = var3;

      for(int var11 = 0; var11 < var8; ++var11) {
         var10 = var11 == var8 - 1 ? var4 : var10.add(var9.scale(var6 * 0.8999999761581421));
         if (!var1.level.noCollision(var1, var2.makeBoundingBox(var10))) {
            return false;
         }
      }

      return true;
   }

   public static class PossibleJump extends WeightedEntry.IntrusiveBase {
      private final BlockPos jumpTarget;

      public PossibleJump(BlockPos var1, int var2) {
         super(var2);
         this.jumpTarget = var1;
      }

      public BlockPos getJumpTarget() {
         return this.jumpTarget;
      }
   }
}
