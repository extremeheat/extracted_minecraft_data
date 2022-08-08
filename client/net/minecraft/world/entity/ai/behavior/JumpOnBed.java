package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class JumpOnBed extends Behavior<Mob> {
   private static final int MAX_TIME_TO_REACH_BED = 100;
   private static final int MIN_JUMPS = 3;
   private static final int MAX_JUMPS = 6;
   private static final int COOLDOWN_BETWEEN_JUMPS = 5;
   private final float speedModifier;
   @Nullable
   private BlockPos targetBed;
   private int remainingTimeToReachBed;
   private int remainingJumps;
   private int remainingCooldownUntilNextJump;

   public JumpOnBed(float var1) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      return var2.isBaby() && this.nearBed(var1, var2);
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      super.start(var1, var2, var3);
      this.getNearestBed(var2).ifPresent((var3x) -> {
         this.targetBed = var3x;
         this.remainingTimeToReachBed = 100;
         this.remainingJumps = 3 + var1.random.nextInt(4);
         this.remainingCooldownUntilNextJump = 0;
         this.startWalkingTowardsBed(var2, var3x);
      });
   }

   protected void stop(ServerLevel var1, Mob var2, long var3) {
      super.stop(var1, var2, var3);
      this.targetBed = null;
      this.remainingTimeToReachBed = 0;
      this.remainingJumps = 0;
      this.remainingCooldownUntilNextJump = 0;
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      return var2.isBaby() && this.targetBed != null && this.isBed(var1, this.targetBed) && !this.tiredOfWalking(var1, var2) && !this.tiredOfJumping(var1, var2);
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   protected void tick(ServerLevel var1, Mob var2, long var3) {
      if (!this.onOrOverBed(var1, var2)) {
         --this.remainingTimeToReachBed;
      } else if (this.remainingCooldownUntilNextJump > 0) {
         --this.remainingCooldownUntilNextJump;
      } else {
         if (this.onBedSurface(var1, var2)) {
            var2.getJumpControl().jump();
            --this.remainingJumps;
            this.remainingCooldownUntilNextJump = 5;
         }

      }
   }

   private void startWalkingTowardsBed(Mob var1, BlockPos var2) {
      var1.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var2, this.speedModifier, 0)));
   }

   private boolean nearBed(ServerLevel var1, Mob var2) {
      return this.onOrOverBed(var1, var2) || this.getNearestBed(var2).isPresent();
   }

   private boolean onOrOverBed(ServerLevel var1, Mob var2) {
      BlockPos var3 = var2.blockPosition();
      BlockPos var4 = var3.below();
      return this.isBed(var1, var3) || this.isBed(var1, var4);
   }

   private boolean onBedSurface(ServerLevel var1, Mob var2) {
      return this.isBed(var1, var2.blockPosition());
   }

   private boolean isBed(ServerLevel var1, BlockPos var2) {
      return var1.getBlockState(var2).is(BlockTags.BEDS);
   }

   private Optional<BlockPos> getNearestBed(Mob var1) {
      return var1.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
   }

   private boolean tiredOfWalking(ServerLevel var1, Mob var2) {
      return !this.onOrOverBed(var1, var2) && this.remainingTimeToReachBed <= 0;
   }

   private boolean tiredOfJumping(ServerLevel var1, Mob var2) {
      return this.onOrOverBed(var1, var2) && this.remainingJumps <= 0;
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Mob)var2, var3);
   }
}
