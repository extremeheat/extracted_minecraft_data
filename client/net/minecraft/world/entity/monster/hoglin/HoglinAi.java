package net.minecraft.world.entity.monster.hoglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.BecomePassiveIfMemoryPresent;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;

public class HoglinAi {
   public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
   public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
   private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);
   private static final int ATTACK_DURATION = 200;
   private static final int DESIRED_DISTANCE_FROM_PIGLIN_WHEN_IDLING = 8;
   private static final int DESIRED_DISTANCE_FROM_PIGLIN_WHEN_RETREATING = 15;
   private static final int ATTACK_INTERVAL = 40;
   private static final int BABY_ATTACK_INTERVAL = 15;
   private static final int REPELLENT_PACIFY_TIME = 200;
   private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
   private static final float SPEED_MULTIPLIER_WHEN_AVOIDING_REPELLENT = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_RETREATING = 1.3F;
   private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 0.6F;
   private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.4F;
   private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 0.6F;

   public HoglinAi() {
      super();
   }

   protected static Brain<?> makeBrain(Brain<Hoglin> var0) {
      initCoreActivity(var0);
      initIdleActivity(var0);
      initFightActivity(var0);
      initRetreatActivity(var0);
      var0.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var0.setDefaultActivity(Activity.IDLE);
      var0.useDefaultActivity();
      return var0;
   }

   private static void initCoreActivity(Brain<Hoglin> var0) {
      var0.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink()));
   }

   private static void initIdleActivity(Brain<Hoglin> var0) {
      var0.addActivity(
         Activity.IDLE,
         10,
         ImmutableList.of(
            BecomePassiveIfMemoryPresent.create(MemoryModuleType.NEAREST_REPELLENT, 200),
            new AnimalMakeLove(EntityType.HOGLIN, 0.6F, 2),
            SetWalkTargetAwayFrom.pos(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true),
            StartAttacking.create(HoglinAi::findNearestValidAttackTarget),
            BehaviorBuilder.triggerIf(Hoglin::isAdult, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, 0.4F, 8, false)),
            SetEntityLookTargetSometimes.create(8.0F, UniformInt.of(30, 60)),
            BabyFollowAdult.create(ADULT_FOLLOW_RANGE, 0.6F),
            createIdleMovementBehaviors()
         )
      );
   }

   private static void initFightActivity(Brain<Hoglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(
         Activity.FIGHT,
         10,
         ImmutableList.of(
            BecomePassiveIfMemoryPresent.create(MemoryModuleType.NEAREST_REPELLENT, 200),
            new AnimalMakeLove(EntityType.HOGLIN, 0.6F, 2),
            SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F),
            BehaviorBuilder.triggerIf(Hoglin::isAdult, MeleeAttack.create(40)),
            BehaviorBuilder.triggerIf(AgeableMob::isBaby, MeleeAttack.create(15)),
            StopAttackingIfTargetInvalid.create(),
            EraseMemoryIf.create(HoglinAi::isBreeding, MemoryModuleType.ATTACK_TARGET)
         ),
         MemoryModuleType.ATTACK_TARGET
      );
   }

   private static void initRetreatActivity(Brain<Hoglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(
         Activity.AVOID,
         10,
         ImmutableList.of(
            SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false),
            createIdleMovementBehaviors(),
            SetEntityLookTargetSometimes.create(8.0F, UniformInt.of(30, 60)),
            EraseMemoryIf.create(HoglinAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET)
         ),
         MemoryModuleType.AVOID_TARGET
      );
   }

   private static RunOne<Hoglin> createIdleMovementBehaviors() {
      return new RunOne<>(
         ImmutableList.of(Pair.of(RandomStroll.stroll(0.4F), 2), Pair.of(SetWalkTargetFromLookTarget.create(0.4F, 3), 2), Pair.of(new DoNothing(30, 60), 1))
      );
   }

   protected static void updateActivity(Hoglin var0) {
      Brain var1 = var0.getBrain();
      Activity var2 = var1.getActiveNonCoreActivity().orElse(null);
      var1.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.AVOID, Activity.IDLE));
      Activity var3 = var1.getActiveNonCoreActivity().orElse(null);
      if (var2 != var3) {
         getSoundForCurrentActivity(var0).ifPresent(var0::makeSound);
      }

      var0.setAggressive(var1.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
   }

   protected static void onHitTarget(Hoglin var0, LivingEntity var1) {
      if (!var0.isBaby()) {
         if (var1.getType() == EntityType.PIGLIN && piglinsOutnumberHoglins(var0)) {
            setAvoidTarget(var0, var1);
            broadcastRetreat(var0, var1);
         } else {
            broadcastAttackTarget(var0, var1);
         }
      }
   }

   private static void broadcastRetreat(Hoglin var0, LivingEntity var1) {
      getVisibleAdultHoglins(var0).forEach(var1x -> retreatFromNearestTarget(var1x, var1));
   }

   private static void retreatFromNearestTarget(Hoglin var0, LivingEntity var1) {
      Brain var3 = var0.getBrain();
      LivingEntity var2 = BehaviorUtils.getNearestTarget(var0, var3.getMemory(MemoryModuleType.AVOID_TARGET), var1);
      var2 = BehaviorUtils.getNearestTarget(var0, var3.getMemory(MemoryModuleType.ATTACK_TARGET), var2);
      setAvoidTarget(var0, var2);
   }

   private static void setAvoidTarget(Hoglin var0, LivingEntity var1) {
      var0.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      var0.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, var1, (long)RETREAT_DURATION.sample(var0.level().random));
   }

   private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Hoglin var0) {
      return !isPacified(var0) && !isBreeding(var0) ? var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
   }

   static boolean isPosNearNearestRepellent(Hoglin var0, BlockPos var1) {
      Optional var2 = var0.getBrain().getMemory(MemoryModuleType.NEAREST_REPELLENT);
      return var2.isPresent() && ((BlockPos)var2.get()).closerThan(var1, 8.0);
   }

   private static boolean wantsToStopFleeing(Hoglin var0) {
      return var0.isAdult() && !piglinsOutnumberHoglins(var0);
   }

   private static boolean piglinsOutnumberHoglins(Hoglin var0) {
      if (var0.isBaby()) {
         return false;
      } else {
         int var1 = var0.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
         int var2 = var0.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1;
         return var1 > var2;
      }
   }

   protected static void wasHurtBy(Hoglin var0, LivingEntity var1) {
      Brain var2 = var0.getBrain();
      var2.eraseMemory(MemoryModuleType.PACIFIED);
      var2.eraseMemory(MemoryModuleType.BREED_TARGET);
      if (var0.isBaby()) {
         retreatFromNearestTarget(var0, var1);
      } else {
         maybeRetaliate(var0, var1);
      }
   }

   private static void maybeRetaliate(Hoglin var0, LivingEntity var1) {
      if (!var0.getBrain().isActive(Activity.AVOID) || var1.getType() != EntityType.PIGLIN) {
         if (var1.getType() != EntityType.HOGLIN) {
            if (!BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(var0, var1, 4.0)) {
               if (Sensor.isEntityAttackable(var0, var1)) {
                  setAttackTarget(var0, var1);
                  broadcastAttackTarget(var0, var1);
               }
            }
         }
      }
   }

   private static void setAttackTarget(Hoglin var0, LivingEntity var1) {
      Brain var2 = var0.getBrain();
      var2.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      var2.eraseMemory(MemoryModuleType.BREED_TARGET);
      var2.setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, var1, 200L);
   }

   private static void broadcastAttackTarget(Hoglin var0, LivingEntity var1) {
      getVisibleAdultHoglins(var0).forEach(var1x -> setAttackTargetIfCloserThanCurrent(var1x, var1));
   }

   private static void setAttackTargetIfCloserThanCurrent(Hoglin var0, LivingEntity var1) {
      if (!isPacified(var0)) {
         Optional var2 = var0.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
         LivingEntity var3 = BehaviorUtils.getNearestTarget(var0, var2, var1);
         setAttackTarget(var0, var3);
      }
   }

   public static Optional<SoundEvent> getSoundForCurrentActivity(Hoglin var0) {
      return var0.getBrain().getActiveNonCoreActivity().map(var1 -> getSoundForActivity(var0, var1));
   }

   private static SoundEvent getSoundForActivity(Hoglin var0, Activity var1) {
      if (var1 == Activity.AVOID || var0.isConverting()) {
         return SoundEvents.HOGLIN_RETREAT;
      } else if (var1 == Activity.FIGHT) {
         return SoundEvents.HOGLIN_ANGRY;
      } else {
         return isNearRepellent(var0) ? SoundEvents.HOGLIN_RETREAT : SoundEvents.HOGLIN_AMBIENT;
      }
   }

   private static List<Hoglin> getVisibleAdultHoglins(Hoglin var0) {
      return var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse(ImmutableList.of());
   }

   private static boolean isNearRepellent(Hoglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
   }

   private static boolean isBreeding(Hoglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
   }

   protected static boolean isPacified(Hoglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.PACIFIED);
   }
}
