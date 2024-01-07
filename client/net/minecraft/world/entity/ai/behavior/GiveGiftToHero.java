package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class GiveGiftToHero extends Behavior<Villager> {
   private static final int THROW_GIFT_AT_DISTANCE = 5;
   private static final int MIN_TIME_BETWEEN_GIFTS = 600;
   private static final int MAX_TIME_BETWEEN_GIFTS = 6600;
   private static final int TIME_TO_DELAY_FOR_HEAD_TO_FINISH_TURNING = 20;
   private static final Map<VillagerProfession, ResourceLocation> GIFTS = Util.make(Maps.newHashMap(), var0 -> {
      var0.put(VillagerProfession.ARMORER, BuiltInLootTables.ARMORER_GIFT);
      var0.put(VillagerProfession.BUTCHER, BuiltInLootTables.BUTCHER_GIFT);
      var0.put(VillagerProfession.CARTOGRAPHER, BuiltInLootTables.CARTOGRAPHER_GIFT);
      var0.put(VillagerProfession.CLERIC, BuiltInLootTables.CLERIC_GIFT);
      var0.put(VillagerProfession.FARMER, BuiltInLootTables.FARMER_GIFT);
      var0.put(VillagerProfession.FISHERMAN, BuiltInLootTables.FISHERMAN_GIFT);
      var0.put(VillagerProfession.FLETCHER, BuiltInLootTables.FLETCHER_GIFT);
      var0.put(VillagerProfession.LEATHERWORKER, BuiltInLootTables.LEATHERWORKER_GIFT);
      var0.put(VillagerProfession.LIBRARIAN, BuiltInLootTables.LIBRARIAN_GIFT);
      var0.put(VillagerProfession.MASON, BuiltInLootTables.MASON_GIFT);
      var0.put(VillagerProfession.SHEPHERD, BuiltInLootTables.SHEPHERD_GIFT);
      var0.put(VillagerProfession.TOOLSMITH, BuiltInLootTables.TOOLSMITH_GIFT);
      var0.put(VillagerProfession.WEAPONSMITH, BuiltInLootTables.WEAPONSMITH_GIFT);
   });
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
         --this.timeUntilNextGift;
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
      for(ItemStack var5 : this.getItemToThrow(var1)) {
         BehaviorUtils.throwItem(var1, var5, var2.position());
      }
   }

   private List<ItemStack> getItemToThrow(Villager var1) {
      if (var1.isBaby()) {
         return ImmutableList.of(new ItemStack(Items.POPPY));
      } else {
         VillagerProfession var2 = var1.getVillagerData().getProfession();
         if (GIFTS.containsKey(var2)) {
            LootTable var3 = var1.level().getServer().getLootData().getLootTable(GIFTS.get(var2));
            LootParams var4 = new LootParams.Builder((ServerLevel)var1.level())
               .withParameter(LootContextParams.ORIGIN, var1.position())
               .withParameter(LootContextParams.THIS_ENTITY, var1)
               .create(LootContextParamSets.GIFT);
            return var3.getRandomItems(var4);
         } else {
            return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
         }
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
