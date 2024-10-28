package net.minecraft.world.entity.monster.creaking;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

public class CreakingAi {
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Creaking>>> SENSOR_TYPES;
   protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES;

   public CreakingAi() {
      super();
   }

   static void initCoreActivity(Brain<Creaking> var0) {
      var0.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim<Creaking>(0.8F) {
         protected boolean checkExtraStartConditions(ServerLevel var1, Creaking var2) {
            return var2.canMove() && super.checkExtraStartConditions(var1, (LivingEntity)var2);
         }
      }, new LookAtTargetSink(45, 90), new MoveToTargetSink()));
   }

   static void initIdleActivity(Brain<Creaking> var0) {
      var0.addActivity(Activity.IDLE, 10, ImmutableList.of(StartAttacking.create((var0x, var1) -> {
         return var1.isActive();
      }, (var0x, var1) -> {
         return var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
      }), SetEntityLookTargetSometimes.create(8.0F, UniformInt.of(30, 60)), new RunOne(ImmutableList.of(Pair.of(RandomStroll.stroll(0.2F), 2), Pair.of(SetWalkTargetFromLookTarget.create(0.2F, 3), 2), Pair.of(new DoNothing(30, 60), 1)))));
   }

   static void initFightActivity(Brain<Creaking> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F), MeleeAttack.create(Creaking::canMove, 40), StopAttackingIfTargetInvalid.create()), MemoryModuleType.ATTACK_TARGET);
   }

   public static Brain.Provider<Creaking> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   public static Brain<Creaking> makeBrain(Brain<Creaking> var0) {
      initCoreActivity(var0);
      initIdleActivity(var0);
      initFightActivity(var0);
      var0.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var0.setDefaultActivity(Activity.IDLE);
      var0.useDefaultActivity();
      return var0;
   }

   public static void updateActivity(Creaking var0) {
      if (!var0.canMove()) {
         var0.getBrain().useDefaultActivity();
      } else {
         var0.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
      }

   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);
   }
}
