package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class GiveGiftToHero extends Behavior<Villager> {
   private static final int THROW_GIFT_AT_DISTANCE = 5;
   private static final int MIN_TIME_BETWEEN_GIFTS = 600;
   private static final int MAX_TIME_BETWEEN_GIFTS = 6600;
   private static final int TIME_TO_DELAY_FOR_HEAD_TO_FINISH_TURNING = 20;
   private static final Map<VillagerProfession, ResourceKey<LootTable>> GIFTS = ImmutableMap.builder()
      .put(VillagerProfession.ARMORER, BuiltInLootTables.ARMORER_GIFT)
      .put(VillagerProfession.BUTCHER, BuiltInLootTables.BUTCHER_GIFT)
      .put(VillagerProfession.CARTOGRAPHER, BuiltInLootTables.CARTOGRAPHER_GIFT)
      .put(VillagerProfession.CLERIC, BuiltInLootTables.CLERIC_GIFT)
      .put(VillagerProfession.FARMER, BuiltInLootTables.FARMER_GIFT)
      .put(VillagerProfession.FISHERMAN, BuiltInLootTables.FISHERMAN_GIFT)
      .put(VillagerProfession.FLETCHER, BuiltInLootTables.FLETCHER_GIFT)
      .put(VillagerProfession.LEATHERWORKER, BuiltInLootTables.LEATHERWORKER_GIFT)
      .put(VillagerProfession.LIBRARIAN, BuiltInLootTables.LIBRARIAN_GIFT)
      .put(VillagerProfession.MASON, BuiltInLootTables.MASON_GIFT)
      .put(VillagerProfession.SHEPHERD, BuiltInLootTables.SHEPHERD_GIFT)
      .put(VillagerProfession.TOOLSMITH, BuiltInLootTables.TOOLSMITH_GIFT)
      .put(VillagerProfession.WEAPONSMITH, BuiltInLootTables.WEAPONSMITH_GIFT)
      .build();
   private static final float SPEED_MODIFIER = 0.5F;
   private int timeUntilNextGift = 600;
   private boolean giftGivenDuringThisRun;
   private long timeSinceStart;

   public GiveGiftToHero(int var1) {
      super(
         ImmutableMap.of(
            MemoryModuleType.WALK_TARGET,
            MemoryStatus.REGISTERED,
            MemoryModuleType.LOOK_TARGET,
            MemoryStatus.REGISTERED,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryStatus.REGISTERED,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryStatus.VALUE_PRESENT
         ),
         var1
      );
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      if (!this.isHeroVisible(var2)) {
         return false;
      } else if (this.timeUntilNextGift > 0) {
         this.timeUntilNextGift--;
         return false;
      } else {
         return true;
      }
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      this.giftGivenDuringThisRun = false;
      this.timeSinceStart = var3;
      Player var5 = this.getNearestTargetableHero(var2).get();
      var2.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, var5);
      BehaviorUtils.lookAtEntity(var2, var5);
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return this.isHeroVisible(var2) && !this.giftGivenDuringThisRun;
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      Player var5 = this.getNearestTargetableHero(var2).get();
      BehaviorUtils.lookAtEntity(var2, var5);
      if (this.isWithinThrowingDistance(var2, var5)) {
         if (var3 - this.timeSinceStart > 20L) {
            this.throwGift(var2, var5);
            this.giftGivenDuringThisRun = true;
         }
      } else {
         BehaviorUtils.setWalkAndLookTargetMemories(var2, var5, 0.5F, 5);
      }
   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      this.timeUntilNextGift = calculateTimeUntilNextGift(var1);
      var2.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   private void throwGift(Villager var1, LivingEntity var2) {
      var1.dropFromGiftLootTable(getLootTableToThrow(var1), var2x -> BehaviorUtils.throwItem(var1, var2x, var2.position()));
   }

   private static ResourceKey<LootTable> getLootTableToThrow(Villager var0) {
      if (var0.isBaby()) {
         return BuiltInLootTables.BABY_VILLAGER_GIFT;
      } else {
         VillagerProfession var1 = var0.getVillagerData().getProfession();
         return GIFTS.getOrDefault(var1, BuiltInLootTables.UNEMPLOYED_GIFT);
      }
   }

   private boolean isHeroVisible(Villager var1) {
      return this.getNearestTargetableHero(var1).isPresent();
   }

   private Optional<Player> getNearestTargetableHero(Villager var1) {
      return var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
   }

   private boolean isHero(Player var1) {
      return var1.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
   }

   private boolean isWithinThrowingDistance(Villager var1, Player var2) {
      BlockPos var3 = var2.blockPosition();
      BlockPos var4 = var1.blockPosition();
      return var4.closerThan(var3, 5.0);
   }

   private static int calculateTimeUntilNextGift(ServerLevel var0) {
      return 600 + var0.random.nextInt(6001);
   }
}
