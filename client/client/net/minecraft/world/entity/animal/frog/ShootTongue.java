package net.minecraft.world.entity.animal.frog;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class ShootTongue extends Behavior<Frog> {
   public static final int TIME_OUT_DURATION = 100;
   public static final int CATCH_ANIMATION_DURATION = 6;
   public static final int TONGUE_ANIMATION_DURATION = 10;
   private static final float EATING_DISTANCE = 1.75F;
   private static final float EATING_MOVEMENT_FACTOR = 0.75F;
   public static final int UNREACHABLE_TONGUE_TARGETS_COOLDOWN_DURATION = 100;
   public static final int MAX_UNREACHBLE_TONGUE_TARGETS_IN_MEMORY = 5;
   private int eatAnimationTimer;
   private int calculatePathCounter;
   private final SoundEvent tongueSound;
   private final SoundEvent eatSound;
   private Vec3 itemSpawnPos;
   private ShootTongue.State state = ShootTongue.State.DONE;

   public ShootTongue(SoundEvent var1, SoundEvent var2) {
      super(
         ImmutableMap.of(
            MemoryModuleType.WALK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.LOOK_TARGET,
            MemoryStatus.REGISTERED,
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.IS_PANICKING,
            MemoryStatus.VALUE_ABSENT
         ),
         100
      );
      this.tongueSound = var1;
      this.eatSound = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Frog var2) {
      LivingEntity var3 = var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      boolean var4 = this.canPathfindToTarget(var2, var3);
      if (!var4) {
         var2.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         this.addUnreachableTargetToMemory(var2, var3);
      }

      return var4 && var2.getPose() != Pose.CROAKING && Frog.canEat(var3);
   }

   protected boolean canStillUse(ServerLevel var1, Frog var2, long var3) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
         && this.state != ShootTongue.State.DONE
         && !var2.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
   }

   protected void start(ServerLevel var1, Frog var2, long var3) {
      LivingEntity var5 = var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      BehaviorUtils.lookAtEntity(var2, var5);
      var2.setTongueTarget(var5);
      var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(var5.position(), 2.0F, 0));
      this.calculatePathCounter = 10;
      this.state = ShootTongue.State.MOVE_TO_TARGET;
   }

   protected void stop(ServerLevel var1, Frog var2, long var3) {
      var2.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      var2.eraseTongueTarget();
      var2.setPose(Pose.STANDING);
   }

   private void eatEntity(ServerLevel var1, Frog var2) {
      var1.playSound(null, var2, this.eatSound, SoundSource.NEUTRAL, 2.0F, 1.0F);
      Optional var3 = var2.getTongueTarget();
      if (var3.isPresent()) {
         Entity var4 = (Entity)var3.get();
         if (var4.isAlive()) {
            var2.doHurtTarget(var4);
            if (!var4.isAlive()) {
               var4.remove(Entity.RemovalReason.KILLED);
            }
         }
      }
   }

   protected void tick(ServerLevel var1, Frog var2, long var3) {
      LivingEntity var5 = var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      var2.setTongueTarget(var5);
      switch (this.state) {
         case MOVE_TO_TARGET:
            if (var5.distanceTo(var2) < 1.75F) {
               var1.playSound(null, var2, this.tongueSound, SoundSource.NEUTRAL, 2.0F, 1.0F);
               var2.setPose(Pose.USING_TONGUE);
               var5.setDeltaMovement(var5.position().vectorTo(var2.position()).normalize().scale(0.75));
               this.itemSpawnPos = var5.position();
               this.eatAnimationTimer = 0;
               this.state = ShootTongue.State.CATCH_ANIMATION;
            } else if (this.calculatePathCounter <= 0) {
               var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(var5.position(), 2.0F, 0));
               this.calculatePathCounter = 10;
            } else {
               this.calculatePathCounter--;
            }
            break;
         case CATCH_ANIMATION:
            if (this.eatAnimationTimer++ >= 6) {
               this.state = ShootTongue.State.EAT_ANIMATION;
               this.eatEntity(var1, var2);
            }
            break;
         case EAT_ANIMATION:
            if (this.eatAnimationTimer >= 10) {
               this.state = ShootTongue.State.DONE;
            } else {
               this.eatAnimationTimer++;
            }
         case DONE:
      }
   }

   private boolean canPathfindToTarget(Frog var1, LivingEntity var2) {
      Path var3 = var1.getNavigation().createPath(var2, 0);
      return var3 != null && var3.getDistToTarget() < 1.75F;
   }

   private void addUnreachableTargetToMemory(Frog var1, LivingEntity var2) {
      List var3 = var1.getBrain().getMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS).orElseGet(ArrayList::new);
      boolean var4 = !var3.contains(var2.getUUID());
      if (var3.size() == 5 && var4) {
         var3.remove(0);
      }

      if (var4) {
         var3.add(var2.getUUID());
      }

      var1.getBrain().setMemoryWithExpiry(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS, var3, 100L);
   }

   static enum State {
      MOVE_TO_TARGET,
      CATCH_ANIMATION,
      EAT_ANIMATION,
      DONE;

      private State() {
      }
   }
}
