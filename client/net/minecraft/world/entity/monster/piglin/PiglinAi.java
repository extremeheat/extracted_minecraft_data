package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BackUpIfTooClose;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.CopyMemoryWithExpiry;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.ai.behavior.DismountOrSkipMounting;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.GoToTargetLocation;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.Mount;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StartCelebratingIfTargetDead;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.StopBeingAngryIfTargetDead;
import net.minecraft.world.entity.ai.behavior.TriggerGate;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class PiglinAi {
   public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
   public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
   public static final Item BARTERING_ITEM = Items.GOLD_INGOT;
   private static final int PLAYER_ANGER_RANGE = 16;
   private static final int ANGER_DURATION = 600;
   private static final int ADMIRE_DURATION = 119;
   private static final int MAX_DISTANCE_TO_WALK_TO_ITEM = 9;
   private static final int MAX_TIME_TO_WALK_TO_ITEM = 200;
   private static final int HOW_LONG_TIME_TO_DISABLE_ADMIRE_WALKING_IF_CANT_REACH_ITEM = 200;
   private static final int CELEBRATION_TIME = 300;
   protected static final UniformInt TIME_BETWEEN_HUNTS = TimeUtil.rangeOfSeconds(30, 120);
   private static final int BABY_FLEE_DURATION_AFTER_GETTING_HIT = 100;
   private static final int HIT_BY_PLAYER_MEMORY_TIMEOUT = 400;
   private static final int MAX_WALK_DISTANCE_TO_START_RIDING = 8;
   private static final UniformInt RIDE_START_INTERVAL = TimeUtil.rangeOfSeconds(10, 40);
   private static final UniformInt RIDE_DURATION = TimeUtil.rangeOfSeconds(10, 30);
   private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);
   private static final int MELEE_ATTACK_COOLDOWN = 20;
   private static final int EAT_COOLDOWN = 200;
   private static final int DESIRED_DISTANCE_FROM_ENTITY_WHEN_AVOIDING = 12;
   private static final int MAX_LOOK_DIST = 8;
   private static final int MAX_LOOK_DIST_FOR_PLAYER_HOLDING_LOVED_ITEM = 14;
   private static final int INTERACTION_RANGE = 8;
   private static final int MIN_DESIRED_DIST_FROM_TARGET_WHEN_HOLDING_CROSSBOW = 5;
   private static final float SPEED_WHEN_STRAFING_BACK_FROM_TARGET = 0.75F;
   private static final int DESIRED_DISTANCE_FROM_ZOMBIFIED = 6;
   private static final UniformInt AVOID_ZOMBIFIED_DURATION = TimeUtil.rangeOfSeconds(5, 7);
   private static final UniformInt BABY_AVOID_NEMESIS_DURATION = TimeUtil.rangeOfSeconds(5, 7);
   private static final float PROBABILITY_OF_CELEBRATION_DANCE = 0.1F;
   private static final float SPEED_MULTIPLIER_WHEN_AVOIDING = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_RETREATING = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_MOUNTING = 0.8F;
   private static final float SPEED_MULTIPLIER_WHEN_GOING_TO_WANTED_ITEM = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_GOING_TO_CELEBRATE_LOCATION = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_DANCING = 0.6F;
   private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.6F;

   public PiglinAi() {
      super();
   }

   protected static Brain<?> makeBrain(Piglin var0, Brain<Piglin> var1) {
      initCoreActivity(var1);
      initIdleActivity(var1);
      initAdmireItemActivity(var1);
      initFightActivity(var0, var1);
      initCelebrateActivity(var1);
      initRetreatActivity(var1);
      initRideHoglinActivity(var1);
      var1.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var1.setDefaultActivity(Activity.IDLE);
      var1.useDefaultActivity();
      return var1;
   }

   protected static void initMemories(Piglin var0, RandomSource var1) {
      int var2 = TIME_BETWEEN_HUNTS.sample(var1);
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, (long)var2);
   }

   private static void initCoreActivity(Brain<Piglin> var0) {
      var0.addActivity(
         Activity.CORE,
         0,
         ImmutableList.of(
            new LookAtTargetSink(45, 90),
            new MoveToTargetSink(),
            InteractWithDoor.create(),
            babyAvoidNemesis(),
            avoidZombified(),
            StopHoldingItemIfNoLongerAdmiring.create(),
            StartAdmiringItemIfSeen.create(119),
            StartCelebratingIfTargetDead.create(300, PiglinAi::wantsToDance),
            StopBeingAngryIfTargetDead.create()
         )
      );
   }

   private static void initIdleActivity(Brain<Piglin> var0) {
      var0.addActivity(
         Activity.IDLE,
         10,
         ImmutableList.of(
            SetEntityLookTarget.create(PiglinAi::isPlayerHoldingLovedItem, 14.0F),
            StartAttacking.create(AbstractPiglin::isAdult, PiglinAi::findNearestValidAttackTarget),
            BehaviorBuilder.triggerIf(Piglin::canHunt, StartHuntingHoglin.create()),
            avoidRepellent(),
            babySometimesRideBabyHoglin(),
            createIdleLookBehaviors(),
            createIdleMovementBehaviors(),
            SetLookAndInteract.create(EntityType.PLAYER, 4)
         )
      );
   }

   private static void initFightActivity(Piglin var0, Brain<Piglin> var1) {
      var1.addActivityAndRemoveMemoryWhenStopped(
         Activity.FIGHT,
         10,
         ImmutableList.of(
            StopAttackingIfTargetInvalid.create(var1x -> !isNearestValidAttackTarget(var0, var1x)),
            BehaviorBuilder.triggerIf(PiglinAi::hasCrossbow, BackUpIfTooClose.create(5, 0.75F)),
            SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F),
            MeleeAttack.create(20),
            new CrossbowAttack(),
            RememberIfHoglinWasKilled.create(),
            EraseMemoryIf.create(PiglinAi::isNearZombified, MemoryModuleType.ATTACK_TARGET)
         ),
         MemoryModuleType.ATTACK_TARGET
      );
   }

   private static void initCelebrateActivity(Brain<Piglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(
         Activity.CELEBRATE,
         10,
         ImmutableList.of(
            avoidRepellent(),
            SetEntityLookTarget.create(PiglinAi::isPlayerHoldingLovedItem, 14.0F),
            StartAttacking.create(AbstractPiglin::isAdult, PiglinAi::findNearestValidAttackTarget),
            BehaviorBuilder.triggerIf(var0x -> !var0x.isDancing(), GoToTargetLocation.create(MemoryModuleType.CELEBRATE_LOCATION, 2, 1.0F)),
            BehaviorBuilder.triggerIf(Piglin::isDancing, GoToTargetLocation.create(MemoryModuleType.CELEBRATE_LOCATION, 4, 0.6F)),
            new RunOne(
               ImmutableList.of(
                  Pair.of(SetEntityLookTarget.create(EntityType.PIGLIN, 8.0F), 1),
                  Pair.of(RandomStroll.stroll(0.6F, 2, 1), 1),
                  Pair.of(new DoNothing(10, 20), 1)
               )
            )
         ),
         MemoryModuleType.CELEBRATE_LOCATION
      );
   }

   private static void initAdmireItemActivity(Brain<Piglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(
         Activity.ADMIRE_ITEM,
         10,
         ImmutableList.of(
            GoToWantedItem.create(PiglinAi::isNotHoldingLovedItemInOffHand, 1.0F, true, 9),
            StopAdmiringIfItemTooFarAway.create(9),
            StopAdmiringIfTiredOfTryingToReachItem.create(200, 200)
         ),
         MemoryModuleType.ADMIRING_ITEM
      );
   }

   private static void initRetreatActivity(Brain<Piglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(
         Activity.AVOID,
         10,
         ImmutableList.of(
            SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1.0F, 12, true),
            createIdleLookBehaviors(),
            createIdleMovementBehaviors(),
            EraseMemoryIf.create(PiglinAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET)
         ),
         MemoryModuleType.AVOID_TARGET
      );
   }

   private static void initRideHoglinActivity(Brain<Piglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(
         Activity.RIDE,
         10,
         ImmutableList.of(
            Mount.create(0.8F),
            SetEntityLookTarget.create(PiglinAi::isPlayerHoldingLovedItem, 8.0F),
            BehaviorBuilder.sequence(
               BehaviorBuilder.triggerIf(Entity::isPassenger),
               TriggerGate.triggerOneShuffled(
                  ImmutableList.builder().addAll(createLookBehaviors()).add(Pair.of(BehaviorBuilder.triggerIf(var0x -> true), 1)).build()
               )
            ),
            DismountOrSkipMounting.create(8, PiglinAi::wantsToStopRiding)
         ),
         MemoryModuleType.RIDE_TARGET
      );
   }

   private static ImmutableList<Pair<OneShot<LivingEntity>, Integer>> createLookBehaviors() {
      return ImmutableList.of(
         Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 1),
         Pair.of(SetEntityLookTarget.create(EntityType.PIGLIN, 8.0F), 1),
         Pair.of(SetEntityLookTarget.create(8.0F), 1)
      );
   }

   private static RunOne<LivingEntity> createIdleLookBehaviors() {
      return new RunOne<>(ImmutableList.builder().addAll(createLookBehaviors()).add(Pair.of(new DoNothing(30, 60), 1)).build());
   }

   private static RunOne<Piglin> createIdleMovementBehaviors() {
      return new RunOne<>(
         ImmutableList.of(
            Pair.of(RandomStroll.stroll(0.6F), 2),
            Pair.of(InteractWith.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2),
            Pair.of(BehaviorBuilder.triggerIf(PiglinAi::doesntSeeAnyPlayerHoldingLovedItem, SetWalkTargetFromLookTarget.create(0.6F, 3)), 2),
            Pair.of(new DoNothing(30, 60), 1)
         )
      );
   }

   private static BehaviorControl<PathfinderMob> avoidRepellent() {
      return SetWalkTargetAwayFrom.pos(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, false);
   }

   private static BehaviorControl<Piglin> babyAvoidNemesis() {
      return CopyMemoryWithExpiry.create(Piglin::isBaby, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.AVOID_TARGET, BABY_AVOID_NEMESIS_DURATION);
   }

   private static BehaviorControl<Piglin> avoidZombified() {
      return CopyMemoryWithExpiry.create(
         PiglinAi::isNearZombified, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, AVOID_ZOMBIFIED_DURATION
      );
   }

   protected static void updateActivity(Piglin var0) {
      Brain var1 = var0.getBrain();
      Activity var2 = var1.getActiveNonCoreActivity().orElse(null);
      var1.setActiveActivityToFirstValid(
         ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE)
      );
      Activity var3 = var1.getActiveNonCoreActivity().orElse(null);
      if (var2 != var3) {
         getSoundForCurrentActivity(var0).ifPresent(var0::makeSound);
      }

      var0.setAggressive(var1.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
      if (!var1.hasMemoryValue(MemoryModuleType.RIDE_TARGET) && isBabyRidingBaby(var0)) {
         var0.stopRiding();
      }

      if (!var1.hasMemoryValue(MemoryModuleType.CELEBRATE_LOCATION)) {
         var1.eraseMemory(MemoryModuleType.DANCING);
      }

      var0.setDancing(var1.hasMemoryValue(MemoryModuleType.DANCING));
   }

   private static boolean isBabyRidingBaby(Piglin var0) {
      if (!var0.isBaby()) {
         return false;
      } else {
         Entity var1 = var0.getVehicle();
         return var1 instanceof Piglin && ((Piglin)var1).isBaby() || var1 instanceof Hoglin && ((Hoglin)var1).isBaby();
      }
   }

   protected static void pickUpItem(Piglin var0, ItemEntity var1) {
      stopWalking(var0);
      ItemStack var2;
      if (var1.getItem().is(Items.GOLD_NUGGET)) {
         var0.take(var1, var1.getItem().getCount());
         var2 = var1.getItem();
         var1.discard();
      } else {
         var0.take(var1, 1);
         var2 = removeOneItemFromItemEntity(var1);
      }

      if (isLovedItem(var2)) {
         var0.getBrain().eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
         holdInOffhand(var0, var2);
         admireGoldItem(var0);
      } else if (isFood(var2) && !hasEatenRecently(var0)) {
         eat(var0);
      } else {
         boolean var3 = !var0.equipItemIfPossible(var2).equals(ItemStack.EMPTY);
         if (!var3) {
            putInInventory(var0, var2);
         }
      }
   }

   private static void holdInOffhand(Piglin var0, ItemStack var1) {
      if (isHoldingItemInOffHand(var0)) {
         var0.spawnAtLocation(var0.getItemInHand(InteractionHand.OFF_HAND));
      }

      var0.holdInOffHand(var1);
   }

   private static ItemStack removeOneItemFromItemEntity(ItemEntity var0) {
      ItemStack var1 = var0.getItem();
      ItemStack var2 = var1.split(1);
      if (var1.isEmpty()) {
         var0.discard();
      } else {
         var0.setItem(var1);
      }

      return var2;
   }

   protected static void stopHoldingOffHandItem(Piglin var0, boolean var1) {
      ItemStack var2 = var0.getItemInHand(InteractionHand.OFF_HAND);
      var0.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
      if (var0.isAdult()) {
         boolean var3 = isBarterCurrency(var2);
         if (var1 && var3) {
            throwItems(var0, getBarterResponseItems(var0));
         } else if (!var3) {
            boolean var4 = !var0.equipItemIfPossible(var2).isEmpty();
            if (!var4) {
               putInInventory(var0, var2);
            }
         }
      } else {
         boolean var5 = !var0.equipItemIfPossible(var2).isEmpty();
         if (!var5) {
            ItemStack var6 = var0.getMainHandItem();
            if (isLovedItem(var6)) {
               putInInventory(var0, var6);
            } else {
               throwItems(var0, Collections.singletonList(var6));
            }

            var0.holdInMainHand(var2);
         }
      }
   }

   protected static void cancelAdmiring(Piglin var0) {
      if (isAdmiringItem(var0) && !var0.getOffhandItem().isEmpty()) {
         var0.spawnAtLocation(var0.getOffhandItem());
         var0.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
      }
   }

   private static void putInInventory(Piglin var0, ItemStack var1) {
      ItemStack var2 = var0.addToInventory(var1);
      throwItemsTowardRandomPos(var0, Collections.singletonList(var2));
   }

   private static void throwItems(Piglin var0, List<ItemStack> var1) {
      Optional var2 = var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
      if (var2.isPresent()) {
         throwItemsTowardPlayer(var0, (Player)var2.get(), var1);
      } else {
         throwItemsTowardRandomPos(var0, var1);
      }
   }

   private static void throwItemsTowardRandomPos(Piglin var0, List<ItemStack> var1) {
      throwItemsTowardPos(var0, var1, getRandomNearbyPos(var0));
   }

   private static void throwItemsTowardPlayer(Piglin var0, Player var1, List<ItemStack> var2) {
      throwItemsTowardPos(var0, var2, var1.position());
   }

   private static void throwItemsTowardPos(Piglin var0, List<ItemStack> var1, Vec3 var2) {
      if (!var1.isEmpty()) {
         var0.swing(InteractionHand.OFF_HAND);

         for (ItemStack var4 : var1) {
            BehaviorUtils.throwItem(var0, var4, var2.add(0.0, 1.0, 0.0));
         }
      }
   }

   private static List<ItemStack> getBarterResponseItems(Piglin var0) {
      LootTable var1 = var0.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.PIGLIN_BARTERING);
      return var1.getRandomItems(
         new LootParams.Builder((ServerLevel)var0.level()).withParameter(LootContextParams.THIS_ENTITY, var0).create(LootContextParamSets.PIGLIN_BARTER)
      );
   }

   private static boolean wantsToDance(LivingEntity var0, LivingEntity var1) {
      return var1.getType() != EntityType.HOGLIN ? false : RandomSource.create(var0.level().getGameTime()).nextFloat() < 0.1F;
   }

   protected static boolean wantsToPickup(Piglin var0, ItemStack var1) {
      if (var0.isBaby() && var1.is(ItemTags.IGNORED_BY_PIGLIN_BABIES)) {
         return false;
      } else if (var1.is(ItemTags.PIGLIN_REPELLENTS)) {
         return false;
      } else if (isAdmiringDisabled(var0) && var0.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
         return false;
      } else if (isBarterCurrency(var1)) {
         return isNotHoldingLovedItemInOffHand(var0);
      } else {
         boolean var2 = var0.canAddToInventory(var1);
         if (var1.is(Items.GOLD_NUGGET)) {
            return var2;
         } else if (isFood(var1)) {
            return !hasEatenRecently(var0) && var2;
         } else {
            return !isLovedItem(var1) ? var0.canReplaceCurrentItem(var1) : isNotHoldingLovedItemInOffHand(var0) && var2;
         }
      }
   }

   protected static boolean isLovedItem(ItemStack var0) {
      return var0.is(ItemTags.PIGLIN_LOVED);
   }

   private static boolean wantsToStopRiding(Piglin var0, Entity var1) {
      return !(var1 instanceof Mob var2)
         ? false
         : !var2.isBaby() || !var2.isAlive() || wasHurtRecently(var0) || wasHurtRecently(var2) || var2 instanceof Piglin && var2.getVehicle() == null;
   }

   private static boolean isNearestValidAttackTarget(Piglin var0, LivingEntity var1) {
      return findNearestValidAttackTarget(var0).filter(var1x -> var1x == var1).isPresent();
   }

   private static boolean isNearZombified(Piglin var0) {
      Brain var1 = var0.getBrain();
      if (var1.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
         LivingEntity var2 = var1.getMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();
         return var0.closerThan(var2, 6.0);
      } else {
         return false;
      }
   }

   private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Piglin var0) {
      Brain var1 = var0.getBrain();
      if (isNearZombified(var0)) {
         return Optional.empty();
      } else {
         Optional var2 = BehaviorUtils.getLivingEntityFromUUIDMemory(var0, MemoryModuleType.ANGRY_AT);
         if (var2.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(var0, (LivingEntity)var2.get())) {
            return var2;
         } else {
            if (var1.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER)) {
               Optional var3 = var1.getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
               if (var3.isPresent()) {
                  return var3;
               }
            }

            Optional var5 = var1.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
            if (var5.isPresent()) {
               return var5;
            } else {
               Optional var4 = var1.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
               return var4.isPresent() && Sensor.isEntityAttackable(var0, (LivingEntity)var4.get()) ? var4 : Optional.empty();
            }
         }
      }
   }

   public static void angerNearbyPiglins(Player var0, boolean var1) {
      List var2 = var0.level().getEntitiesOfClass(Piglin.class, var0.getBoundingBox().inflate(16.0));
      var2.stream().filter(PiglinAi::isIdle).filter(var2x -> !var1 || BehaviorUtils.canSee(var2x, var0)).forEach(var1x -> {
         if (var1x.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            setAngerTargetToNearestTargetablePlayerIfFound(var1x, var0);
         } else {
            setAngerTarget(var1x, var0);
         }
      });
   }

   public static InteractionResult mobInteract(Piglin var0, Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (canAdmire(var0, var3)) {
         ItemStack var4 = var3.split(1);
         holdInOffhand(var0, var4);
         admireGoldItem(var0);
         stopWalking(var0);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }

   protected static boolean canAdmire(Piglin var0, ItemStack var1) {
      return !isAdmiringDisabled(var0) && !isAdmiringItem(var0) && var0.isAdult() && isBarterCurrency(var1);
   }

   protected static void wasHurtBy(Piglin var0, LivingEntity var1) {
      if (!(var1 instanceof Piglin)) {
         if (isHoldingItemInOffHand(var0)) {
            stopHoldingOffHandItem(var0, false);
         }

         Brain var2 = var0.getBrain();
         var2.eraseMemory(MemoryModuleType.CELEBRATE_LOCATION);
         var2.eraseMemory(MemoryModuleType.DANCING);
         var2.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
         if (var1 instanceof Player) {
            var2.setMemoryWithExpiry(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
         }

         getAvoidTarget(var0).ifPresent(var2x -> {
            if (var2x.getType() != var1.getType()) {
               var2.eraseMemory(MemoryModuleType.AVOID_TARGET);
            }
         });
         if (var0.isBaby()) {
            var2.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, var1, 100L);
            if (Sensor.isEntityAttackableIgnoringLineOfSight(var0, var1)) {
               broadcastAngerTarget(var0, var1);
            }
         } else if (var1.getType() == EntityType.HOGLIN && hoglinsOutnumberPiglins(var0)) {
            setAvoidTargetAndDontHuntForAWhile(var0, var1);
            broadcastRetreat(var0, var1);
         } else {
            maybeRetaliate(var0, var1);
         }
      }
   }

   protected static void maybeRetaliate(AbstractPiglin var0, LivingEntity var1) {
      if (!var0.getBrain().isActive(Activity.AVOID)) {
         if (Sensor.isEntityAttackableIgnoringLineOfSight(var0, var1)) {
            if (!BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(var0, var1, 4.0)) {
               if (var1.getType() == EntityType.PLAYER && var0.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                  setAngerTargetToNearestTargetablePlayerIfFound(var0, var1);
                  broadcastUniversalAnger(var0);
               } else {
                  setAngerTarget(var0, var1);
                  broadcastAngerTarget(var0, var1);
               }
            }
         }
      }
   }

   public static Optional<SoundEvent> getSoundForCurrentActivity(Piglin var0) {
      return var0.getBrain().getActiveNonCoreActivity().map(var1 -> getSoundForActivity(var0, var1));
   }

   private static SoundEvent getSoundForActivity(Piglin var0, Activity var1) {
      if (var1 == Activity.FIGHT) {
         return SoundEvents.PIGLIN_ANGRY;
      } else if (var0.isConverting()) {
         return SoundEvents.PIGLIN_RETREAT;
      } else if (var1 == Activity.AVOID && isNearAvoidTarget(var0)) {
         return SoundEvents.PIGLIN_RETREAT;
      } else if (var1 == Activity.ADMIRE_ITEM) {
         return SoundEvents.PIGLIN_ADMIRING_ITEM;
      } else if (var1 == Activity.CELEBRATE) {
         return SoundEvents.PIGLIN_CELEBRATE;
      } else if (seesPlayerHoldingLovedItem(var0)) {
         return SoundEvents.PIGLIN_JEALOUS;
      } else {
         return isNearRepellent(var0) ? SoundEvents.PIGLIN_RETREAT : SoundEvents.PIGLIN_AMBIENT;
      }
   }

   private static boolean isNearAvoidTarget(Piglin var0) {
      Brain var1 = var0.getBrain();
      return !var1.hasMemoryValue(MemoryModuleType.AVOID_TARGET) ? false : var1.getMemory(MemoryModuleType.AVOID_TARGET).get().closerThan(var0, 12.0);
   }

   protected static List<AbstractPiglin> getVisibleAdultPiglins(Piglin var0) {
      return var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse(ImmutableList.of());
   }

   private static List<AbstractPiglin> getAdultPiglins(AbstractPiglin var0) {
      return var0.getBrain().getMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse(ImmutableList.of());
   }

   public static boolean isWearingGold(LivingEntity var0) {
      for (ItemStack var3 : var0.getArmorAndBodyArmorSlots()) {
         Item var4 = var3.getItem();
         if (var4 instanceof ArmorItem && ((ArmorItem)var4).getMaterial().is(ArmorMaterials.GOLD)) {
            return true;
         }
      }

      return false;
   }

   private static void stopWalking(Piglin var0) {
      var0.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var0.getNavigation().stop();
   }

   private static BehaviorControl<LivingEntity> babySometimesRideBabyHoglin() {
      SetEntityLookTargetSometimes.Ticker var0 = new SetEntityLookTargetSometimes.Ticker(RIDE_START_INTERVAL);
      return CopyMemoryWithExpiry.create(
         var1 -> var1.isBaby() && var0.tickDownAndCheck(var1.level().random),
         MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
         MemoryModuleType.RIDE_TARGET,
         RIDE_DURATION
      );
   }

   protected static void broadcastAngerTarget(AbstractPiglin var0, LivingEntity var1) {
      getAdultPiglins(var0).forEach(var1x -> {
         if (var1.getType() != EntityType.HOGLIN || var1x.canHunt() && ((Hoglin)var1).canBeHunted()) {
            setAngerTargetIfCloserThanCurrent(var1x, var1);
         }
      });
   }

   protected static void broadcastUniversalAnger(AbstractPiglin var0) {
      getAdultPiglins(var0).forEach(var0x -> getNearestVisibleTargetablePlayer(var0x).ifPresent(var1 -> setAngerTarget(var0x, var1)));
   }

   protected static void setAngerTarget(AbstractPiglin var0, LivingEntity var1) {
      if (Sensor.isEntityAttackableIgnoringLineOfSight(var0, var1)) {
         var0.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         var0.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, var1.getUUID(), 600L);
         if (var1.getType() == EntityType.HOGLIN && var0.canHunt()) {
            dontKillAnyMoreHoglinsForAWhile(var0);
         }

         if (var1.getType() == EntityType.PLAYER && var0.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            var0.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
         }
      }
   }

   private static void setAngerTargetToNearestTargetablePlayerIfFound(AbstractPiglin var0, LivingEntity var1) {
      Optional var2 = getNearestVisibleTargetablePlayer(var0);
      if (var2.isPresent()) {
         setAngerTarget(var0, (LivingEntity)var2.get());
      } else {
         setAngerTarget(var0, var1);
      }
   }

   private static void setAngerTargetIfCloserThanCurrent(AbstractPiglin var0, LivingEntity var1) {
      Optional var2 = getAngerTarget(var0);
      LivingEntity var3 = BehaviorUtils.getNearestTarget(var0, var2, var1);
      if (!var2.isPresent() || var2.get() != var3) {
         setAngerTarget(var0, var3);
      }
   }

   private static Optional<LivingEntity> getAngerTarget(AbstractPiglin var0) {
      return BehaviorUtils.getLivingEntityFromUUIDMemory(var0, MemoryModuleType.ANGRY_AT);
   }

   public static Optional<LivingEntity> getAvoidTarget(Piglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) ? var0.getBrain().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
   }

   public static Optional<Player> getNearestVisibleTargetablePlayer(AbstractPiglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
         ? var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
         : Optional.empty();
   }

   private static void broadcastRetreat(Piglin var0, LivingEntity var1) {
      getVisibleAdultPiglins(var0).stream().filter(var0x -> var0x instanceof Piglin).forEach(var1x -> retreatFromNearestTarget((Piglin)var1x, var1));
   }

   private static void retreatFromNearestTarget(Piglin var0, LivingEntity var1) {
      Brain var2 = var0.getBrain();
      LivingEntity var3 = BehaviorUtils.getNearestTarget(var0, var2.getMemory(MemoryModuleType.AVOID_TARGET), var1);
      var3 = BehaviorUtils.getNearestTarget(var0, var2.getMemory(MemoryModuleType.ATTACK_TARGET), var3);
      setAvoidTargetAndDontHuntForAWhile(var0, var3);
   }

   private static boolean wantsToStopFleeing(Piglin var0) {
      Brain var1 = var0.getBrain();
      if (!var1.hasMemoryValue(MemoryModuleType.AVOID_TARGET)) {
         return true;
      } else {
         LivingEntity var2 = var1.getMemory(MemoryModuleType.AVOID_TARGET).get();
         EntityType var3 = var2.getType();
         if (var3 == EntityType.HOGLIN) {
            return piglinsEqualOrOutnumberHoglins(var0);
         } else {
            return isZombified(var3) ? !var1.isMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, var2) : false;
         }
      }
   }

   private static boolean piglinsEqualOrOutnumberHoglins(Piglin var0) {
      return !hoglinsOutnumberPiglins(var0);
   }

   private static boolean hoglinsOutnumberPiglins(Piglin var0) {
      int var1 = var0.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
      int var2 = var0.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
      return var2 > var1;
   }

   private static void setAvoidTargetAndDontHuntForAWhile(Piglin var0, LivingEntity var1) {
      var0.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
      var0.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      var0.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, var1, (long)RETREAT_DURATION.sample(var0.level().random));
      dontKillAnyMoreHoglinsForAWhile(var0);
   }

   protected static void dontKillAnyMoreHoglinsForAWhile(AbstractPiglin var0) {
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, (long)TIME_BETWEEN_HUNTS.sample(var0.level().random));
   }

   private static void eat(Piglin var0) {
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.ATE_RECENTLY, true, 200L);
   }

   private static Vec3 getRandomNearbyPos(Piglin var0) {
      Vec3 var1 = LandRandomPos.getPos(var0, 4, 2);
      return var1 == null ? var0.position() : var1;
   }

   private static boolean hasEatenRecently(Piglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.ATE_RECENTLY);
   }

   protected static boolean isIdle(AbstractPiglin var0) {
      return var0.getBrain().isActive(Activity.IDLE);
   }

   private static boolean hasCrossbow(LivingEntity var0) {
      return var0.isHolding(Items.CROSSBOW);
   }

   private static void admireGoldItem(LivingEntity var0) {
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.ADMIRING_ITEM, true, 119L);
   }

   private static boolean isAdmiringItem(Piglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_ITEM);
   }

   private static boolean isBarterCurrency(ItemStack var0) {
      return var0.is(BARTERING_ITEM);
   }

   private static boolean isFood(ItemStack var0) {
      return var0.is(ItemTags.PIGLIN_FOOD);
   }

   private static boolean isNearRepellent(Piglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
   }

   private static boolean seesPlayerHoldingLovedItem(LivingEntity var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
   }

   private static boolean doesntSeeAnyPlayerHoldingLovedItem(LivingEntity var0) {
      return !seesPlayerHoldingLovedItem(var0);
   }

   public static boolean isPlayerHoldingLovedItem(LivingEntity var0) {
      return var0.getType() == EntityType.PLAYER && var0.isHolding(PiglinAi::isLovedItem);
   }

   private static boolean isAdmiringDisabled(Piglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_DISABLED);
   }

   private static boolean wasHurtRecently(LivingEntity var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
   }

   private static boolean isHoldingItemInOffHand(Piglin var0) {
      return !var0.getOffhandItem().isEmpty();
   }

   private static boolean isNotHoldingLovedItemInOffHand(Piglin var0) {
      return var0.getOffhandItem().isEmpty() || !isLovedItem(var0.getOffhandItem());
   }

   public static boolean isZombified(EntityType<?> var0) {
      return var0 == EntityType.ZOMBIFIED_PIGLIN || var0 == EntityType.ZOGLIN;
   }
}
