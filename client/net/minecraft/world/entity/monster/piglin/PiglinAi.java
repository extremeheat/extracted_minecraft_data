package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IntRange;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BackUpIfTooClose;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.CopyMemoryWithExpiry;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.ai.behavior.DismountOrSkipMounting;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.GoToCelebrateLocation;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.Mount;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StartCelebratingIfTargetDead;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.StopBeingAngryIfTargetDead;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class PiglinAi {
   public static final Item BARTERING_ITEM;
   private static final IntRange TIME_BETWEEN_HUNTS;
   private static final IntRange RIDE_START_INTERVAL;
   private static final IntRange RIDE_DURATION;
   private static final IntRange RETREAT_DURATION;
   private static final IntRange AVOID_ZOMBIFIED_DURATION;
   private static final IntRange BABY_AVOID_NEMESIS_DURATION;

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

   protected static void initMemories(Piglin var0) {
      int var1 = TIME_BETWEEN_HUNTS.randomValue(var0.level.random);
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, (long)var1);
   }

   private static void initCoreActivity(Brain<Piglin> var0) {
      var0.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink(), new InteractWithDoor(), babyAvoidNemesis(), avoidZombified(), new StopHoldingItemIfNoLongerAdmiring(), new StartAdmiringItemIfSeen(120), new StartCelebratingIfTargetDead(300, PiglinAi::wantsToDance), new StopBeingAngryIfTargetDead()));
   }

   private static void initIdleActivity(Brain<Piglin> var0) {
      var0.addActivity(Activity.IDLE, 10, ImmutableList.of(new SetEntityLookTarget(PiglinAi::isPlayerHoldingLovedItem, 14.0F), new StartAttacking(AbstractPiglin::isAdult, PiglinAi::findNearestValidAttackTarget), new RunIf(Piglin::canHunt, new StartHuntingHoglin()), avoidRepellent(), babySometimesRideBabyHoglin(), createIdleLookBehaviors(), createIdleMovementBehaviors(), new SetLookAndInteract(EntityType.PLAYER, 4)));
   }

   private static void initFightActivity(Piglin var0, Brain<Piglin> var1) {
      var1.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(new StopAttackingIfTargetInvalid((var1x) -> {
         return !isNearestValidAttackTarget(var0, var1x);
      }), new RunIf(PiglinAi::hasCrossbow, new BackUpIfTooClose(5, 0.75F)), new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F), new MeleeAttack(20), new CrossbowAttack(), new RememberIfHoglinWasKilled(), new EraseMemoryIf(PiglinAi::isNearZombified, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
   }

   private static void initCelebrateActivity(Brain<Piglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(Activity.CELEBRATE, 10, ImmutableList.of(avoidRepellent(), new SetEntityLookTarget(PiglinAi::isPlayerHoldingLovedItem, 14.0F), new StartAttacking(AbstractPiglin::isAdult, PiglinAi::findNearestValidAttackTarget), new RunIf((var0x) -> {
         return !var0x.isDancing();
      }, new GoToCelebrateLocation(2, 1.0F)), new RunIf(Piglin::isDancing, new GoToCelebrateLocation(4, 0.6F)), new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.PIGLIN, 8.0F), 1), Pair.of(new RandomStroll(0.6F, 2, 1), 1), Pair.of(new DoNothing(10, 20), 1)))), MemoryModuleType.CELEBRATE_LOCATION);
   }

   private static void initAdmireItemActivity(Brain<Piglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(Activity.ADMIRE_ITEM, 10, ImmutableList.of(new GoToWantedItem(PiglinAi::isNotHoldingLovedItemInOffHand, 1.0F, true, 9), new StopAdmiringIfItemTooFarAway(9), new StopAdmiringIfTiredOfTryingToReachItem(200, 200)), MemoryModuleType.ADMIRING_ITEM);
   }

   private static void initRetreatActivity(Brain<Piglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1.0F, 12, true), createIdleLookBehaviors(), createIdleMovementBehaviors(), new EraseMemoryIf(PiglinAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
   }

   private static void initRideHoglinActivity(Brain<Piglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(Activity.RIDE, 10, ImmutableList.of(new Mount(0.8F), new SetEntityLookTarget(PiglinAi::isPlayerHoldingLovedItem, 8.0F), new RunIf(Entity::isPassenger, createIdleLookBehaviors()), new DismountOrSkipMounting(8, PiglinAi::wantsToStopRiding)), MemoryModuleType.RIDE_TARGET);
   }

   private static RunOne<Piglin> createIdleLookBehaviors() {
      return new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN, 8.0F), 1), Pair.of(new SetEntityLookTarget(8.0F), 1), Pair.of(new DoNothing(30, 60), 1)));
   }

   private static RunOne<Piglin> createIdleMovementBehaviors() {
      return new RunOne(ImmutableList.of(Pair.of(new RandomStroll(0.6F), 2), Pair.of(InteractWith.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new RunIf(PiglinAi::doesntSeeAnyPlayerHoldingLovedItem, new SetWalkTargetFromLookTarget(0.6F, 3)), 2), Pair.of(new DoNothing(30, 60), 1)));
   }

   private static SetWalkTargetAwayFrom<BlockPos> avoidRepellent() {
      return SetWalkTargetAwayFrom.pos(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, false);
   }

   private static CopyMemoryWithExpiry<Piglin, LivingEntity> babyAvoidNemesis() {
      return new CopyMemoryWithExpiry(Piglin::isBaby, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.AVOID_TARGET, BABY_AVOID_NEMESIS_DURATION);
   }

   private static CopyMemoryWithExpiry<Piglin, LivingEntity> avoidZombified() {
      return new CopyMemoryWithExpiry(PiglinAi::isNearZombified, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, AVOID_ZOMBIFIED_DURATION);
   }

   protected static void updateActivity(Piglin var0) {
      Brain var1 = var0.getBrain();
      Activity var2 = (Activity)var1.getActiveNonCoreActivity().orElse((Object)null);
      var1.setActiveActivityToFirstValid(ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
      Activity var3 = (Activity)var1.getActiveNonCoreActivity().orElse((Object)null);
      if (var2 != var3) {
         getSoundForCurrentActivity(var0).ifPresent(var0::playSound);
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
         boolean var3 = var0.equipItemIfPossible(var2);
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
      boolean var3;
      if (var0.isAdult()) {
         var3 = isBarterCurrency(var2);
         if (var1 && var3) {
            throwItems(var0, getBarterResponseItems(var0));
         } else if (!var3) {
            boolean var4 = var0.equipItemIfPossible(var2);
            if (!var4) {
               putInInventory(var0, var2);
            }
         }
      } else {
         var3 = var0.equipItemIfPossible(var2);
         if (!var3) {
            ItemStack var5 = var0.getMainHandItem();
            if (isLovedItem(var5)) {
               putInInventory(var0, var5);
            } else {
               throwItems(var0, Collections.singletonList(var5));
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
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            ItemStack var4 = (ItemStack)var3.next();
            BehaviorUtils.throwItem(var0, var4, var2.add(0.0D, 1.0D, 0.0D));
         }
      }

   }

   private static List<ItemStack> getBarterResponseItems(Piglin var0) {
      LootTable var1 = var0.level.getServer().getLootTables().get(BuiltInLootTables.PIGLIN_BARTERING);
      List var2 = var1.getRandomItems((new LootContext.Builder((ServerLevel)var0.level)).withParameter(LootContextParams.THIS_ENTITY, var0).withRandom(var0.level.random).create(LootContextParamSets.PIGLIN_BARTER));
      return var2;
   }

   private static boolean wantsToDance(LivingEntity var0, LivingEntity var1) {
      if (var1.getType() != EntityType.HOGLIN) {
         return false;
      } else {
         return (new Random(var0.level.getGameTime())).nextFloat() < 0.1F;
      }
   }

   protected static boolean wantsToPickup(Piglin var0, ItemStack var1) {
      if (var0.isBaby() && var1.is((Tag)ItemTags.IGNORED_BY_PIGLIN_BABIES)) {
         return false;
      } else if (var1.is((Tag)ItemTags.PIGLIN_REPELLENTS)) {
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
         } else if (!isLovedItem(var1)) {
            return var0.canReplaceCurrentItem(var1);
         } else {
            return isNotHoldingLovedItemInOffHand(var0) && var2;
         }
      }
   }

   protected static boolean isLovedItem(ItemStack var0) {
      return var0.is((Tag)ItemTags.PIGLIN_LOVED);
   }

   private static boolean wantsToStopRiding(Piglin var0, Entity var1) {
      if (!(var1 instanceof Mob)) {
         return false;
      } else {
         Mob var2 = (Mob)var1;
         return !var2.isBaby() || !var2.isAlive() || wasHurtRecently(var0) || wasHurtRecently(var2) || var2 instanceof Piglin && var2.getVehicle() == null;
      }
   }

   private static boolean isNearestValidAttackTarget(Piglin var0, LivingEntity var1) {
      return findNearestValidAttackTarget(var0).filter((var1x) -> {
         return var1x == var1;
      }).isPresent();
   }

   private static boolean isNearZombified(Piglin var0) {
      Brain var1 = var0.getBrain();
      if (var1.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
         LivingEntity var2 = (LivingEntity)var1.getMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();
         return var0.closerThan(var2, 6.0D);
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
         if (var2.isPresent() && isAttackAllowed((LivingEntity)var2.get())) {
            return var2;
         } else {
            Optional var3;
            if (var1.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER)) {
               var3 = var1.getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
               if (var3.isPresent()) {
                  return var3;
               }
            }

            var3 = var1.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
            if (var3.isPresent()) {
               return var3;
            } else {
               Optional var4 = var1.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
               return var4.isPresent() && isAttackAllowed((LivingEntity)var4.get()) ? var4 : Optional.empty();
            }
         }
      }
   }

   public static void angerNearbyPiglins(Player var0, boolean var1) {
      List var2 = var0.level.getEntitiesOfClass(Piglin.class, var0.getBoundingBox().inflate(16.0D));
      var2.stream().filter(PiglinAi::isIdle).filter((var2x) -> {
         return !var1 || BehaviorUtils.canSee(var2x, var0);
      }).forEach((var1x) -> {
         if (var1x.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
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

         getAvoidTarget(var0).ifPresent((var2x) -> {
            if (var2x.getType() != var1.getType()) {
               var2.eraseMemory(MemoryModuleType.AVOID_TARGET);
            }

         });
         if (var0.isBaby()) {
            var2.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, var1, 100L);
            if (isAttackAllowed(var1)) {
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
         if (isAttackAllowed(var1)) {
            if (!BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(var0, var1, 4.0D)) {
               if (var1.getType() == EntityType.PLAYER && var0.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
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
      return var0.getBrain().getActiveNonCoreActivity().map((var1) -> {
         return getSoundForActivity(var0, var1);
      });
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
      return !var1.hasMemoryValue(MemoryModuleType.AVOID_TARGET) ? false : ((LivingEntity)var1.getMemory(MemoryModuleType.AVOID_TARGET).get()).closerThan(var0, 12.0D);
   }

   protected static boolean hasAnyoneNearbyHuntedRecently(Piglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY) || getVisibleAdultPiglins(var0).stream().anyMatch((var0x) -> {
         return var0x.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY);
      });
   }

   private static List<AbstractPiglin> getVisibleAdultPiglins(Piglin var0) {
      return (List)var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse(ImmutableList.of());
   }

   private static List<AbstractPiglin> getAdultPiglins(AbstractPiglin var0) {
      return (List)var0.getBrain().getMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse(ImmutableList.of());
   }

   public static boolean isWearingGold(LivingEntity var0) {
      Iterable var1 = var0.getArmorSlots();
      Iterator var2 = var1.iterator();

      Item var4;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         ItemStack var3 = (ItemStack)var2.next();
         var4 = var3.getItem();
      } while(!(var4 instanceof ArmorItem) || ((ArmorItem)var4).getMaterial() != ArmorMaterials.GOLD);

      return true;
   }

   private static void stopWalking(Piglin var0) {
      var0.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var0.getNavigation().stop();
   }

   private static RunSometimes<Piglin> babySometimesRideBabyHoglin() {
      return new RunSometimes(new CopyMemoryWithExpiry(Piglin::isBaby, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, RIDE_DURATION), RIDE_START_INTERVAL);
   }

   protected static void broadcastAngerTarget(AbstractPiglin var0, LivingEntity var1) {
      getAdultPiglins(var0).forEach((var1x) -> {
         if (var1.getType() != EntityType.HOGLIN || var1x.canHunt() && ((Hoglin)var1).canBeHunted()) {
            setAngerTargetIfCloserThanCurrent(var1x, var1);
         }
      });
   }

   protected static void broadcastUniversalAnger(AbstractPiglin var0) {
      getAdultPiglins(var0).forEach((var0x) -> {
         getNearestVisibleTargetablePlayer(var0x).ifPresent((var1) -> {
            setAngerTarget(var0x, var1);
         });
      });
   }

   protected static void broadcastDontKillAnyMoreHoglinsForAWhile(Piglin var0) {
      getVisibleAdultPiglins(var0).forEach(PiglinAi::dontKillAnyMoreHoglinsForAWhile);
   }

   protected static void setAngerTarget(AbstractPiglin var0, LivingEntity var1) {
      if (isAttackAllowed(var1)) {
         var0.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         var0.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, var1.getUUID(), 600L);
         if (var1.getType() == EntityType.HOGLIN && var0.canHunt()) {
            dontKillAnyMoreHoglinsForAWhile(var0);
         }

         if (var1.getType() == EntityType.PLAYER && var0.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
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
      return var0.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) ? var0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) : Optional.empty();
   }

   private static void broadcastRetreat(Piglin var0, LivingEntity var1) {
      getVisibleAdultPiglins(var0).stream().filter((var0x) -> {
         return var0x instanceof Piglin;
      }).forEach((var1x) -> {
         retreatFromNearestTarget((Piglin)var1x, var1);
      });
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
         LivingEntity var2 = (LivingEntity)var1.getMemory(MemoryModuleType.AVOID_TARGET).get();
         EntityType var3 = var2.getType();
         if (var3 == EntityType.HOGLIN) {
            return piglinsEqualOrOutnumberHoglins(var0);
         } else if (isZombified(var3)) {
            return !var1.isMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, var2);
         } else {
            return false;
         }
      }
   }

   private static boolean piglinsEqualOrOutnumberHoglins(Piglin var0) {
      return !hoglinsOutnumberPiglins(var0);
   }

   private static boolean hoglinsOutnumberPiglins(Piglin var0) {
      int var1 = (Integer)var0.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
      int var2 = (Integer)var0.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
      return var2 > var1;
   }

   private static void setAvoidTargetAndDontHuntForAWhile(Piglin var0, LivingEntity var1) {
      var0.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
      var0.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      var0.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, var1, (long)RETREAT_DURATION.randomValue(var0.level.random));
      dontKillAnyMoreHoglinsForAWhile(var0);
   }

   protected static void dontKillAnyMoreHoglinsForAWhile(AbstractPiglin var0) {
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, (long)TIME_BETWEEN_HUNTS.randomValue(var0.level.random));
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
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.ADMIRING_ITEM, true, 120L);
   }

   private static boolean isAdmiringItem(Piglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_ITEM);
   }

   private static boolean isBarterCurrency(ItemStack var0) {
      return var0.is(BARTERING_ITEM);
   }

   private static boolean isFood(ItemStack var0) {
      return var0.is((Tag)ItemTags.PIGLIN_FOOD);
   }

   private static boolean isAttackAllowed(LivingEntity var0) {
      return EntitySelector.ATTACK_ALLOWED.test(var0);
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

   static {
      BARTERING_ITEM = Items.GOLD_INGOT;
      TIME_BETWEEN_HUNTS = TimeUtil.rangeOfSeconds(30, 120);
      RIDE_START_INTERVAL = TimeUtil.rangeOfSeconds(10, 40);
      RIDE_DURATION = TimeUtil.rangeOfSeconds(10, 30);
      RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);
      AVOID_ZOMBIFIED_DURATION = TimeUtil.rangeOfSeconds(5, 7);
      BABY_AVOID_NEMESIS_DURATION = TimeUtil.rangeOfSeconds(5, 7);
   }
}
