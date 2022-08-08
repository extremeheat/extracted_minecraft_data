package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.StopBeingAngryIfTargetDead;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;

public class PiglinBruteAi {
   private static final int ANGER_DURATION = 600;
   private static final int MELEE_ATTACK_COOLDOWN = 20;
   private static final double ACTIVITY_SOUND_LIKELIHOOD_PER_TICK = 0.0125;
   private static final int MAX_LOOK_DIST = 8;
   private static final int INTERACTION_RANGE = 8;
   private static final double TARGETING_RANGE = 12.0;
   private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.6F;
   private static final int HOME_CLOSE_ENOUGH_DISTANCE = 2;
   private static final int HOME_TOO_FAR_DISTANCE = 100;
   private static final int HOME_STROLL_AROUND_DISTANCE = 5;

   public PiglinBruteAi() {
      super();
   }

   protected static Brain<?> makeBrain(PiglinBrute var0, Brain<PiglinBrute> var1) {
      initCoreActivity(var0, var1);
      initIdleActivity(var0, var1);
      initFightActivity(var0, var1);
      var1.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var1.setDefaultActivity(Activity.IDLE);
      var1.useDefaultActivity();
      return var1;
   }

   protected static void initMemories(PiglinBrute var0) {
      GlobalPos var1 = GlobalPos.of(var0.level.dimension(), var0.blockPosition());
      var0.getBrain().setMemory(MemoryModuleType.HOME, (Object)var1);
   }

   private static void initCoreActivity(PiglinBrute var0, Brain<PiglinBrute> var1) {
      var1.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink(), new InteractWithDoor(), new StopBeingAngryIfTargetDead()));
   }

   private static void initIdleActivity(PiglinBrute var0, Brain<PiglinBrute> var1) {
      var1.addActivity(Activity.IDLE, 10, ImmutableList.of(new StartAttacking(PiglinBruteAi::findNearestValidAttackTarget), createIdleLookBehaviors(), createIdleMovementBehaviors(), new SetLookAndInteract(EntityType.PLAYER, 4)));
   }

   private static void initFightActivity(PiglinBrute var0, Brain<PiglinBrute> var1) {
      var1.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(new StopAttackingIfTargetInvalid((var1x) -> {
         return !isNearestValidAttackTarget(var0, var1x);
      }), new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F), new MeleeAttack(20)), MemoryModuleType.ATTACK_TARGET);
   }

   private static RunOne<PiglinBrute> createIdleLookBehaviors() {
      return new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN_BRUTE, 8.0F), 1), Pair.of(new SetEntityLookTarget(8.0F), 1), Pair.of(new DoNothing(30, 60), 1)));
   }

   private static RunOne<PiglinBrute> createIdleMovementBehaviors() {
      return new RunOne(ImmutableList.of(Pair.of(new RandomStroll(0.6F), 2), Pair.of(InteractWith.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(InteractWith.of(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new StrollToPoi(MemoryModuleType.HOME, 0.6F, 2, 100), 2), Pair.of(new StrollAroundPoi(MemoryModuleType.HOME, 0.6F, 5), 2), Pair.of(new DoNothing(30, 60), 1)));
   }

   protected static void updateActivity(PiglinBrute var0) {
      Brain var1 = var0.getBrain();
      Activity var2 = (Activity)var1.getActiveNonCoreActivity().orElse((Object)null);
      var1.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
      Activity var3 = (Activity)var1.getActiveNonCoreActivity().orElse((Object)null);
      if (var2 != var3) {
         playActivitySound(var0);
      }

      var0.setAggressive(var1.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
   }

   private static boolean isNearestValidAttackTarget(AbstractPiglin var0, LivingEntity var1) {
      return findNearestValidAttackTarget(var0).filter((var1x) -> {
         return var1x == var1;
      }).isPresent();
   }

   private static Optional<? extends LivingEntity> findNearestValidAttackTarget(AbstractPiglin var0) {
      Optional var1 = BehaviorUtils.getLivingEntityFromUUIDMemory(var0, MemoryModuleType.ANGRY_AT);
      if (var1.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(var0, (LivingEntity)var1.get())) {
         return var1;
      } else {
         Optional var2 = getTargetIfWithinRange(var0, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
         return var2.isPresent() ? var2 : var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
      }
   }

   private static Optional<? extends LivingEntity> getTargetIfWithinRange(AbstractPiglin var0, MemoryModuleType<? extends LivingEntity> var1) {
      return var0.getBrain().getMemory(var1).filter((var1x) -> {
         return var1x.closerThan(var0, 12.0);
      });
   }

   protected static void wasHurtBy(PiglinBrute var0, LivingEntity var1) {
      if (!(var1 instanceof AbstractPiglin)) {
         PiglinAi.maybeRetaliate(var0, var1);
      }
   }

   protected static void setAngerTarget(PiglinBrute var0, LivingEntity var1) {
      var0.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, var1.getUUID(), 600L);
   }

   protected static void maybePlayActivitySound(PiglinBrute var0) {
      if ((double)var0.level.random.nextFloat() < 0.0125) {
         playActivitySound(var0);
      }

   }

   private static void playActivitySound(PiglinBrute var0) {
      var0.getBrain().getActiveNonCoreActivity().ifPresent((var1) -> {
         if (var1 == Activity.FIGHT) {
            var0.playAngrySound();
         }

      });
   }
}
