package net.minecraft.world.entity.monster.breeze;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

public class BreezeAi {
   public static final float SPEED_MULTIPLIER_WHEN_SLIDING = 0.6F;
   public static final float JUMP_CIRCLE_INNER_RADIUS = 4.0F;
   public static final float JUMP_CIRCLE_MIDDLE_RADIUS = 8.0F;
   public static final float JUMP_CIRCLE_OUTER_RADIUS = 24.0F;
   static final List<SensorType<? extends Sensor<? super Breeze>>> SENSOR_TYPES;
   static final List<MemoryModuleType<?>> MEMORY_TYPES;
   private static final int TICKS_TO_REMEMBER_SEEN_TARGET = 100;

   public BreezeAi() {
      super();
   }

   protected static Brain<?> makeBrain(Breeze var0, Brain<Breeze> var1) {
      initCoreActivity(var1);
      initIdleActivity(var1);
      initFightActivity(var0, var1);
      var1.setCoreActivities(Set.of(Activity.CORE));
      var1.setDefaultActivity(Activity.FIGHT);
      var1.useDefaultActivity();
      return var1;
   }

   private static void initCoreActivity(Brain<Breeze> var0) {
      var0.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim(0.8F), new LookAtTargetSink(45, 90)));
   }

   private static void initIdleActivity(Brain<Breeze> var0) {
      var0.addActivity(Activity.IDLE, ImmutableList.of(Pair.of(0, StartAttacking.create((var0x, var1) -> {
         return var1.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
      })), Pair.of(1, StartAttacking.create((var0x, var1) -> {
         return var1.getHurtBy();
      })), Pair.of(2, new SlideToTargetSink(20, 40)), Pair.of(3, new RunOne(ImmutableList.of(Pair.of(new DoNothing(20, 100), 1), Pair.of(RandomStroll.stroll(0.6F), 2))))));
   }

   private static void initFightActivity(Breeze var0, Brain<Breeze> var1) {
      Activity var10001 = Activity.FIGHT;
      Integer var10002 = 0;
      BiPredicate var10003 = Sensor.wasEntityAttackableLastNTicks(var0, 100).negate();
      Objects.requireNonNull(var10003);
      var1.addActivityWithConditions(var10001, ImmutableList.of(Pair.of(var10002, StopAttackingIfTargetInvalid.create(var10003::test)), Pair.of(1, new Shoot()), Pair.of(2, new LongJump()), Pair.of(3, new ShootWhenStuck()), Pair.of(4, new Slide())), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT)));
   }

   static void updateActivity(Breeze var0) {
      var0.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.NEAREST_PLAYERS, SensorType.BREEZE_ATTACK_ENTITY_SENSOR);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.BREEZE_JUMP_COOLDOWN, MemoryModuleType.BREEZE_JUMP_INHALING, MemoryModuleType.BREEZE_SHOOT, MemoryModuleType.BREEZE_SHOOT_CHARGING, MemoryModuleType.BREEZE_SHOOT_RECOVERING, MemoryModuleType.BREEZE_SHOOT_COOLDOWN, new MemoryModuleType[]{MemoryModuleType.BREEZE_JUMP_TARGET, MemoryModuleType.BREEZE_LEAVING_WATER, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PATH});
   }

   public static class SlideToTargetSink extends MoveToTargetSink {
      @VisibleForTesting
      public SlideToTargetSink(int var1, int var2) {
         super(var1, var2);
      }

      protected void start(ServerLevel var1, Mob var2, long var3) {
         super.start(var1, var2, var3);
         var2.playSound(SoundEvents.BREEZE_SLIDE);
         var2.setPose(Pose.SLIDING);
      }

      protected void stop(ServerLevel var1, Mob var2, long var3) {
         super.stop(var1, var2, var3);
         var2.setPose(Pose.STANDING);
         if (var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 60L);
         }

      }

      // $FF: synthetic method
      protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
         this.start(var1, (Mob)var2, var3);
      }
   }
}
