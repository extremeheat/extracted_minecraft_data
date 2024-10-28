package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class ShowTradesToPlayer extends Behavior<Villager> {
   private static final int MAX_LOOK_TIME = 900;
   private static final int STARTING_LOOK_TIME = 40;
   @Nullable
   private ItemStack playerItemStack;
   private final List<ItemStack> displayItems = Lists.newArrayList();
   private int cycleCounter;
   private int displayIndex;
   private int lookTime;

   public ShowTradesToPlayer(int var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT), var1, var2);
   }

   public boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      Brain var3 = var2.getBrain();
      if (var3.getMemory(MemoryModuleType.INTERACTION_TARGET).isEmpty()) {
         return false;
      } else {
         LivingEntity var4 = (LivingEntity)var3.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
         return var4.getType() == EntityType.PLAYER && var2.isAlive() && var4.isAlive() && !var2.isBaby() && var2.distanceToSqr(var4) <= 17.0;
      }
   }

   public boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return this.checkExtraStartConditions(var1, var2) && this.lookTime > 0 && var2.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   public void start(ServerLevel var1, Villager var2, long var3) {
      super.start(var1, var2, var3);
      this.lookAtTarget(var2);
      this.cycleCounter = 0;
      this.displayIndex = 0;
      this.lookTime = 40;
   }

   public void tick(ServerLevel var1, Villager var2, long var3) {
      LivingEntity var5 = this.lookAtTarget(var2);
      this.findItemsToDisplay(var5, var2);
      if (!this.displayItems.isEmpty()) {
         this.displayCyclingItems(var2);
      } else {
         clearHeldItem(var2);
         this.lookTime = Math.min(this.lookTime, 40);
      }

      --this.lookTime;
   }

   public void stop(ServerLevel var1, Villager var2, long var3) {
      super.stop(var1, var2, var3);
      var2.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
      clearHeldItem(var2);
      this.playerItemStack = null;
   }

   private void findItemsToDisplay(LivingEntity var1, Villager var2) {
      boolean var3 = false;
      ItemStack var4 = var1.getMainHandItem();
      if (this.playerItemStack == null || !ItemStack.isSameItem(this.playerItemStack, var4)) {
         this.playerItemStack = var4;
         var3 = true;
         this.displayItems.clear();
      }

      if (var3 && !this.playerItemStack.isEmpty()) {
         this.updateDisplayItems(var2);
         if (!this.displayItems.isEmpty()) {
            this.lookTime = 900;
            this.displayFirstItem(var2);
         }
      }

   }

   private void displayFirstItem(Villager var1) {
      displayAsHeldItem(var1, (ItemStack)this.displayItems.get(0));
   }

   private void updateDisplayItems(Villager var1) {
      Iterator var2 = var1.getOffers().iterator();

      while(var2.hasNext()) {
         MerchantOffer var3 = (MerchantOffer)var2.next();
         if (!var3.isOutOfStock() && this.playerItemStackMatchesCostOfOffer(var3)) {
            this.displayItems.add(var3.assemble());
         }
      }

   }

   private boolean playerItemStackMatchesCostOfOffer(MerchantOffer var1) {
      return ItemStack.isSameItem(this.playerItemStack, var1.getCostA()) || ItemStack.isSameItem(this.playerItemStack, var1.getCostB());
   }

   private static void clearHeldItem(Villager var0) {
      var0.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      var0.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
   }

   private static void displayAsHeldItem(Villager var0, ItemStack var1) {
      var0.setItemSlot(EquipmentSlot.MAINHAND, var1);
      var0.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   private LivingEntity lookAtTarget(Villager var1) {
      Brain var2 = var1.getBrain();
      LivingEntity var3 = (LivingEntity)var2.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      var2.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(var3, true)));
      return var3;
   }

   private void displayCyclingItems(Villager var1) {
      if (this.displayItems.size() >= 2 && ++this.cycleCounter >= 40) {
         ++this.displayIndex;
         this.cycleCounter = 0;
         if (this.displayIndex > this.displayItems.size() - 1) {
            this.displayIndex = 0;
         }

         displayAsHeldItem(var1, (ItemStack)this.displayItems.get(this.displayIndex));
      }

   }

   // $FF: synthetic method
   public void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   public void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
