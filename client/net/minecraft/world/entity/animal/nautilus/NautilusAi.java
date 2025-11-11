package net.minecraft.world.entity.animal.nautilus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.ChargeAttack;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

public class NautilusAi {
   private static final float SPEED_MULTIPLIER_WHEN_IDLING_IN_WATER = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.3F;
   private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 0.4F;
   private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 1.6F;
   private static final UniformInt TIME_BETWEEN_NON_PLAYER_ATTACKS = UniformInt.of(2400, 3600);
   private static final float SPEED_WHEN_ATTACKING = 0.6F;
   private static final float ATTACK_KNOCKBACK_FORCE = 2.0F;
   private static final int ANGER_DURATION = 400;
   private static final int TIME_BETWEEN_ATTACKS = 80;
   private static final double MAX_CHARGE_DISTANCE = 12.0;
   private static final double MAX_TARGET_DETECTION_DISTANCE = 11.0;
   protected static final TargetingConditions ATTACK_TARGET_CONDITIONS = TargetingConditions.forCombat().selector((var0, var1) -> ((Boolean)var1.getGameRules().get(GameRules.MOB_GRIEFING) || !var0.getType().equals(EntityType.ARMOR_STAND)) && var1.getWorldBorder().isWithinBounds(var0.getBoundingBox()));
   protected static final ImmutableList<SensorType<? extends Sensor<? super Nautilus>>> SENSOR_TYPES;
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

   public NautilusAi() {
      super();
   }

   protected static void initMemories(AbstractNautilus var0, RandomSource var1) {
      var0.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET_COOLDOWN, TIME_BETWEEN_NON_PLAYER_ATTACKS.sample(var1));
   }

   protected static Brain.Provider<Nautilus> brainProvider() {
      return Brain.<Nautilus>provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected static Brain<?> makeBrain(Brain<Nautilus> var0) {
      initCoreActivity(var0);
      initIdleActivity(var0);
      initFightActivity(var0);
      var0.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var0.setDefaultActivity(Activity.IDLE);
      var0.useDefaultActivity();
      return var0;
   }

   private static void initCoreActivity(Brain<Nautilus> var0) {
      var0.addActivity(Activity.CORE, 0, ImmutableList.of(new AnimalPanic(1.6F), new LookAtTargetSink(45, 90), new MoveToTargetSink(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.CHARGE_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.ATTACK_TARGET_COOLDOWN)));
   }

   private static void initIdleActivity(Brain<Nautilus> var0) {
      var0.addActivity(Activity.IDLE, ImmutableList.of(Pair.of(1, new AnimalMakeLove(EntityType.NAUTILUS, 0.4F, 2)), Pair.of(2, new FollowTemptation((var0x) -> 1.3F, (var0x) -> var0x.isBaby() ? 2.5 : 3.5)), Pair.of(3, StartAttacking.create(NautilusAi::findNearestValidAttackTarget)), Pair.of(4, new GateBehavior(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableSet.of(), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL, ImmutableList.of(Pair.of(RandomStroll.swim(1.0F), 2), Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 3))))));
   }

   private static void initFightActivity(Brain<Nautilus> var0) {
      var0.addActivityWithConditions(Activity.FIGHT, ImmutableList.of(Pair.of(0, new ChargeAttack(80, ATTACK_TARGET_CONDITIONS, 0.6F, 2.0F, 12.0, 11.0, SoundEvents.NAUTILUS_DASH))), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.CHARGE_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)));
   }

   protected static Optional<? extends LivingEntity> findNearestValidAttackTarget(ServerLevel var0, AbstractNautilus var1) {
      if (!BehaviorUtils.isBreeding(var1) && var1.isInWater() && !var1.isBaby() && !var1.isTame()) {
         Optional var2 = BehaviorUtils.getLivingEntityFromUUIDMemory(var1, MemoryModuleType.ANGRY_AT).filter((var2x) -> var2x.isInWater() && Sensor.isEntityAttackableIgnoringLineOfSight(var0, var1, var2x));
         if (var2.isPresent()) {
            return var2;
         } else if (var1.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET_COOLDOWN)) {
            return Optional.empty();
         } else {
            var1.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET_COOLDOWN, TIME_BETWEEN_NON_PLAYER_ATTACKS.sample(var0.random));
            if (var0.random.nextFloat() < 0.5F) {
               return Optional.empty();
            } else {
               Optional var3 = ((NearestVisibleLivingEntities)var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty())).findClosest(NautilusAi::isHostileTarget);
               return var3;
            }
         }
      } else {
         return Optional.empty();
      }
   }

   protected static void setAngerTarget(ServerLevel var0, AbstractNautilus var1, LivingEntity var2) {
      if (Sensor.isEntityAttackableIgnoringLineOfSight(var0, var1, var2)) {
         var1.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         var1.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, var2.getUUID(), 400L);
      }

   }

   private static boolean isHostileTarget(LivingEntity var0) {
      return var0.isInWater() && var0.getType().is(EntityTypeTags.NAUTILUS_HOSTILES);
   }

   public static void updateActivity(Nautilus var0) {
      var0.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
   }

   public static Predicate<ItemStack> getTemptations() {
      return (var0) -> var0.is(ItemTags.NAUTILUS_FOOD);
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NAUTILUS_TEMPTATIONS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING, MemoryModuleType.ATTACK_TARGET, new MemoryModuleType[]{MemoryModuleType.CHARGE_COOLDOWN_TICKS, MemoryModuleType.HURT_BY, MemoryModuleType.ANGRY_AT, MemoryModuleType.ATTACK_TARGET_COOLDOWN});
   }
}
