package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class GiveGiftToHero extends Behavior {
   private static final Map gifts = (Map)Util.make(Maps.newHashMap(), (var0) -> {
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
   private int timeUntilNextGift = 600;
   private boolean giftGivenDuringThisRun;
   private long timeSinceStart;

   public GiveGiftToHero(int var1) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT), var1);
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
      Player var5 = (Player)this.getNearestTargetableHero(var2).get();
      var2.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)var5);
      BehaviorUtils.lookAtEntity(var2, var5);
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return this.isHeroVisible(var2) && !this.giftGivenDuringThisRun;
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      Player var5 = (Player)this.getNearestTargetableHero(var2).get();
      BehaviorUtils.lookAtEntity(var2, var5);
      if (this.isWithinThrowingDistance(var2, var5)) {
         if (var3 - this.timeSinceStart > 20L) {
            this.throwGift(var2, var5);
            this.giftGivenDuringThisRun = true;
         }
      } else {
         BehaviorUtils.walkToEntity(var2, var5, 5);
      }

   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      this.timeUntilNextGift = calculateTimeUntilNextGift(var1);
      var2.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   private void throwGift(Villager var1, LivingEntity var2) {
      List var3 = this.getItemToThrow(var1);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         ItemStack var5 = (ItemStack)var4.next();
         BehaviorUtils.throwItem(var1, var5, var2);
      }

   }

   private List getItemToThrow(Villager var1) {
      if (var1.isBaby()) {
         return ImmutableList.of(new ItemStack(Items.POPPY));
      } else {
         VillagerProfession var2 = var1.getVillagerData().getProfession();
         if (gifts.containsKey(var2)) {
            LootTable var3 = var1.level.getServer().getLootTables().get((ResourceLocation)gifts.get(var2));
            LootContext.Builder var4 = (new LootContext.Builder((ServerLevel)var1.level)).withParameter(LootContextParams.BLOCK_POS, new BlockPos(var1)).withParameter(LootContextParams.THIS_ENTITY, var1).withRandom(var1.getRandom());
            return var3.getRandomItems(var4.create(LootContextParamSets.GIFT));
         } else {
            return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
         }
      }
   }

   private boolean isHeroVisible(Villager var1) {
      return this.getNearestTargetableHero(var1).isPresent();
   }

   private Optional getNearestTargetableHero(Villager var1) {
      return var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
   }

   private boolean isHero(Player var1) {
      return var1.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
   }

   private boolean isWithinThrowingDistance(Villager var1, Player var2) {
      BlockPos var3 = new BlockPos(var2);
      BlockPos var4 = new BlockPos(var1);
      return var4.closerThan(var3, 5.0D);
   }

   private static int calculateTimeUntilNextGift(ServerLevel var0) {
      return 600 + var0.random.nextInt(6001);
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }
}
