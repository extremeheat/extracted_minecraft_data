package net.minecraft.world.entity.monster.creaking;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
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
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
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
      var0.addActivity(Activity.IDLE, 10, ImmutableList.of(StartAttacking.create((var0x, var1) -> var1.isActive(), (var0x, var1) -> var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)), SetEntityLookTargetSometimes.create(8.0F, UniformInt.of(30, 60)), new RunOne(ImmutableList.of(Pair.of(RandomStroll.stroll(0.3F), 2), Pair.of(SetWalkTargetFromLookTarget.create(0.3F, 3), 2), Pair.of(new DoNothing(30, 60), 1)))));
   }

   static void initFightActivity(Creaking var0, Brain<Creaking> var1) {
      var1.addActivityWithConditions(Activity.FIGHT, 10, ImmutableList.of(SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F), MeleeAttack.create(Creaking::canMove, 40), StopAttackingIfTargetInvalid.create((StopAttackingIfTargetInvalid.StopAttackCondition)((var1x, var2) -> !isAttackTargetStillReachable(var0, var2)))), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)));
   }

   private static boolean isAttackTargetStillReachable(Creaking var0, LivingEntity var1) {
      Optional var2 = var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS);
      return (Boolean)var2.map((var1x) -> {
         boolean var10000;
         if (var1 instanceof Player var2) {
            if (var1x.contains(var2)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }).orElse(false);
   }

   public static Brain.Provider<Creaking> brainProvider() {
      return Brain.<Creaking>provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   public static Brain<Creaking> makeBrain(Creaking var0, Brain<Creaking> var1) {
      initCoreActivity(var1);
      initIdleActivity(var1);
      initFightActivity(var0, var1);
      var1.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var1.setDefaultActivity(Activity.IDLE);
      var1.useDefaultActivity();
      return var1;
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
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);
   }
}
