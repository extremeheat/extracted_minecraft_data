package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TradeWithVillager extends Behavior<Villager> {
   private static final int INTERACT_DIST_SQR = 5;
   private static final float SPEED_MODIFIER = 0.5F;
   private Set<Item> trades = ImmutableSet.of();

   public TradeWithVillager() {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      return BehaviorUtils.targetIsValid(var2.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return this.checkExtraStartConditions(var1, var2);
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      Villager var5 = (Villager)var2.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      BehaviorUtils.lockGazeAndWalkToEachOther(var2, var5, 0.5F);
      this.trades = figureOutWhatIAmWillingToTrade(var2, var5);
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      Villager var5 = (Villager)var2.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      if (!(var2.distanceToSqr(var5) > 5.0D)) {
         BehaviorUtils.lockGazeAndWalkToEachOther(var2, var5, 0.5F);
         var2.gossip(var1, var5, var3);
         if (var2.hasExcessFood() && (var2.getVillagerData().getProfession() == VillagerProfession.FARMER || var5.wantsMoreFood())) {
            throwHalfStack(var2, Villager.FOOD_POINTS.keySet(), var5);
         }

         if (var5.getVillagerData().getProfession() == VillagerProfession.FARMER && var2.getInventory().countItem(Items.WHEAT) > Items.WHEAT.getMaxStackSize() / 2) {
            throwHalfStack(var2, ImmutableSet.of(Items.WHEAT), var5);
         }

         if (!this.trades.isEmpty() && var2.getInventory().hasAnyOf(this.trades)) {
            throwHalfStack(var2, this.trades, var5);
         }

      }
   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      var2.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
   }

   private static Set<Item> figureOutWhatIAmWillingToTrade(Villager var0, Villager var1) {
      ImmutableSet var2 = var1.getVillagerData().getProfession().getRequestedItems();
      ImmutableSet var3 = var0.getVillagerData().getProfession().getRequestedItems();
      return (Set)var2.stream().filter((var1x) -> {
         return !var3.contains(var1x);
      }).collect(Collectors.toSet());
   }

   private static void throwHalfStack(Villager var0, Set<Item> var1, LivingEntity var2) {
      SimpleContainer var3 = var0.getInventory();
      ItemStack var4 = ItemStack.EMPTY;
      int var5 = 0;

      while(var5 < var3.getContainerSize()) {
         ItemStack var6;
         Item var7;
         int var8;
         label28: {
            var6 = var3.getItem(var5);
            if (!var6.isEmpty()) {
               var7 = var6.getItem();
               if (var1.contains(var7)) {
                  if (var6.getCount() > var6.getMaxStackSize() / 2) {
                     var8 = var6.getCount() / 2;
                     break label28;
                  }

                  if (var6.getCount() > 24) {
                     var8 = var6.getCount() - 24;
                     break label28;
                  }
               }
            }

            ++var5;
            continue;
         }

         var6.shrink(var8);
         var4 = new ItemStack(var7, var8);
         break;
      }

      if (!var4.isEmpty()) {
         BehaviorUtils.throwItem(var0, var4, var2.position());
      }

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

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
