package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class VillagerGoalPackages {
   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getCorePackage(VillagerProfession var0, float var1) {
      return ImmutableList.of(Pair.of(0, new Swim(0.4F, 0.8F)), Pair.of(0, new InteractWithDoor()), Pair.of(0, new LookAtTargetSink(45, 90)), Pair.of(0, new VillagerPanicTrigger()), Pair.of(0, new WakeUp()), Pair.of(0, new ReactToBell()), Pair.of(0, new SetRaidStatus()), Pair.of(1, new MoveToTargetSink(200)), Pair.of(2, new LookAndFollowTradingPlayerSink(var1)), Pair.of(5, new PickUpItems()), Pair.of(10, new AcquirePoi(var0.getJobPoiType(), MemoryModuleType.JOB_SITE, true)), Pair.of(10, new AcquirePoi(PoiType.HOME, MemoryModuleType.HOME, false)), new Pair[]{Pair.of(10, new AcquirePoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT, true)), Pair.of(10, new AssignProfessionFromJobSite()), Pair.of(10, new ResetProfession())});
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getWorkPackage(VillagerProfession var0, float var1) {
      return ImmutableList.of(getMinimalLookBehavior(), Pair.of(5, new RunOne(ImmutableList.of(Pair.of(new WorkAtPoi(), 7), Pair.of(new StrollAroundPoi(MemoryModuleType.JOB_SITE, 4), 2), Pair.of(new StrollToPoi(MemoryModuleType.JOB_SITE, 1, 10), 5), Pair.of(new StrollToPoiList(MemoryModuleType.SECONDARY_JOB_SITE, 0.4F, 1, 6, MemoryModuleType.JOB_SITE), 5), Pair.of(new HarvestFarmland(), var0 == VillagerProfession.FARMER ? 2 : 5)))), Pair.of(10, new ShowTradesToPlayer(400, 1600)), Pair.of(10, new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.JOB_SITE, var1, 9, 100, 1200)), Pair.of(3, new GiveGiftToHero(100)), Pair.of(3, new ValidateNearbyPoi(var0.getJobPoiType(), MemoryModuleType.JOB_SITE)), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPlayPackage(float var0) {
      return ImmutableList.of(Pair.of(0, new MoveToTargetSink(100)), getFullLookBehavior(), Pair.of(5, new PlayTagWithOtherKids()), Pair.of(5, new RunOne(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, var0, 2), 2), Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, var0, 2), 1), Pair.of(new VillageBoundRandomStroll(var0), 1), Pair.of(new SetWalkTargetFromLookTarget(var0, 2), 1), Pair.of(new JumpOnBed(var0), 2), Pair.of(new DoNothing(20, 40), 2)))), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRestPackage(VillagerProfession var0, float var1) {
      return ImmutableList.of(Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, var1, 1, 150, 1200)), Pair.of(3, new ValidateNearbyPoi(PoiType.HOME, MemoryModuleType.HOME)), Pair.of(3, new SleepInBed()), Pair.of(5, new RunOne(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new SetClosestHomeAsWalkTarget(var1), 1), Pair.of(new InsideBrownianWalk(var1), 4), Pair.of(new GoToClosestVillage(var1, 4), 2), Pair.of(new DoNothing(20, 40), 2)))), getMinimalLookBehavior(), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getMeetPackage(VillagerProfession var0, float var1) {
      return ImmutableList.of(Pair.of(2, new RunOne(ImmutableList.of(Pair.of(new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 40), 2), Pair.of(new SocializeAtBell(), 2)))), Pair.of(10, new ShowTradesToPlayer(400, 1600)), Pair.of(10, new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, var1, 6, 100, 200)), Pair.of(3, new GiveGiftToHero(100)), Pair.of(3, new ValidateNearbyPoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT)), Pair.of(3, new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new TradeWithVillager(), 1)))), getFullLookBehavior(), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getIdlePackage(VillagerProfession var0, float var1) {
      return ImmutableList.of(Pair.of(2, new RunOne(ImmutableList.of(Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, var1, 2), 2), Pair.of(new InteractWith(EntityType.VILLAGER, 8, Villager::canBreed, Villager::canBreed, MemoryModuleType.BREED_TARGET, var1, 2), 1), Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, var1, 2), 1), Pair.of(new VillageBoundRandomStroll(var1), 1), Pair.of(new SetWalkTargetFromLookTarget(var1, 2), 1), Pair.of(new JumpOnBed(var1), 1), Pair.of(new DoNothing(30, 60), 1)))), Pair.of(3, new GiveGiftToHero(100)), Pair.of(3, new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(3, new ShowTradesToPlayer(400, 1600)), Pair.of(3, new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new TradeWithVillager(), 1)))), Pair.of(3, new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new MakeLove(), 1)))), getFullLookBehavior(), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPanicPackage(VillagerProfession var0, float var1) {
      float var2 = var1 * 1.5F;
      return ImmutableList.of(Pair.of(0, new VillagerCalmDown()), Pair.of(1, new SetWalkTargetAwayFromEntity(MemoryModuleType.NEAREST_HOSTILE, var2)), Pair.of(1, new SetWalkTargetAwayFromEntity(MemoryModuleType.HURT_BY_ENTITY, var2)), Pair.of(3, new VillageBoundRandomStroll(var2, 2, 2)), getMinimalLookBehavior());
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPreRaidPackage(VillagerProfession var0, float var1) {
      return ImmutableList.of(Pair.of(0, new RingBell()), Pair.of(0, new RunOne(ImmutableList.of(Pair.of(new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, var1 * 1.5F, 2, 150, 200), 6), Pair.of(new VillageBoundRandomStroll(var1 * 1.5F), 2)))), getMinimalLookBehavior(), Pair.of(99, new ResetRaidStatus()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRaidPackage(VillagerProfession var0, float var1) {
      return ImmutableList.of(Pair.of(0, new RunOne(ImmutableList.of(Pair.of(new GoOutsideToCelebrate(var1), 5), Pair.of(new VictoryStroll(var1 * 1.1F), 2)))), Pair.of(0, new Celebrate(600, 600)), Pair.of(2, new LocateHidingPlaceDuringRaid(24, var1 * 1.4F)), getMinimalLookBehavior(), Pair.of(99, new ResetRaidStatus()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHidePackage(VillagerProfession var0, float var1) {
      boolean var2 = true;
      return ImmutableList.of(Pair.of(0, new SetHiddenState(15, 2)), Pair.of(1, new LocateHidingPlace(32, var1 * 1.25F, 2)), getMinimalLookBehavior());
   }

   private static Pair<Integer, Behavior<LivingEntity>> getFullLookBehavior() {
      return Pair.of(5, new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.CAT, 8.0F), 8), Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), 2), Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 2), Pair.of(new SetEntityLookTarget(MobCategory.CREATURE, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.WATER_CREATURE, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.MONSTER, 8.0F), 1), Pair.of(new DoNothing(30, 60), 2))));
   }

   private static Pair<Integer, Behavior<LivingEntity>> getMinimalLookBehavior() {
      return Pair.of(5, new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), 2), Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 2), Pair.of(new DoNothing(30, 60), 8))));
   }
}
