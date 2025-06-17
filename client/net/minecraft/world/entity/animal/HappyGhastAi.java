package net.minecraft.world.entity.animal;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

public class HappyGhastAi {
   private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25F;
   private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 1.1F;
   private static final double BABY_GHAST_CLOSE_ENOUGH_DIST = 3.0;
   private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(3, 16);
   private static final ImmutableList<SensorType<? extends Sensor<? super HappyGhast>>> SENSOR_TYPES;
   private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

   public HappyGhastAi() {
      super();
   }

   public static Brain.Provider<HappyGhast> brainProvider() {
      return Brain.<HappyGhast>provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected static Brain<?> makeBrain(Brain<HappyGhast> var0) {
      initCoreActivity(var0);
      initIdleActivity(var0);
      initPanicActivity(var0);
      var0.setCoreActivities(Set.of(Activity.CORE));
      var0.setDefaultActivity(Activity.IDLE);
      var0.useDefaultActivity();
      return var0;
   }

   private static void initCoreActivity(Brain<HappyGhast> var0) {
      var0.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim(0.8F), new AnimalPanic(2.0F, 0), new LookAtTargetSink(45, 90), new MoveToTargetSink(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
   }

   private static void initIdleActivity(Brain<HappyGhast> var0) {
      var0.addActivity(Activity.IDLE, ImmutableList.of(Pair.of(1, new FollowTemptation((var0x) -> 1.25F, (var0x) -> 3.0, true)), Pair.of(2, BabyFollowAdult.create(ADULT_FOLLOW_RANGE, (var0x) -> 1.1F, MemoryModuleType.NEAREST_VISIBLE_PLAYER, true)), Pair.of(3, BabyFollowAdult.create(ADULT_FOLLOW_RANGE, (var0x) -> 1.1F, MemoryModuleType.NEAREST_VISIBLE_ADULT, true)), Pair.of(4, new RunOne(ImmutableList.of(Pair.of(RandomStroll.fly(1.0F), 1), Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1))))));
   }

   private static void initPanicActivity(Brain<HappyGhast> var0) {
      var0.addActivityWithConditions(Activity.PANIC, ImmutableList.of(), Set.of(Pair.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_PRESENT)));
   }

   public static void updateActivity(HappyGhast var0) {
      var0.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.PANIC, Activity.IDLE));
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.HAPPY_GHAST_TEMPTATIONS, SensorType.NEAREST_ADULT_ANY_TYPE, SensorType.NEAREST_PLAYERS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_VISIBLE_ADULT, new MemoryModuleType[]{MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS});
   }
}
