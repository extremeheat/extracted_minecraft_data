package net.minecraft.world.inventory;

import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantResultSlot extends Slot {
   private final MerchantContainer slots;
   private final Player player;
   private int removeCount;
   private final Merchant merchant;

   public MerchantResultSlot(Player var1, Merchant var2, MerchantContainer var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.player = var1;
      this.merchant = var2;
      this.slots = var3;
   }

   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   public ItemStack remove(int var1) {
      if (this.hasItem()) {
         this.removeCount += Math.min(var1, this.getItem().getCount());
      }

      return super.remove(var1);
   }

   protected void onQuickCraft(ItemStack var1, int var2) {
      this.removeCount += var2;
      this.checkTakeAchievements(var1);
   }

   protected void checkTakeAchievements(ItemStack var1) {
      var1.onCraftedBy(this.player.level(), this.player, this.removeCount);
      this.removeCount = 0;
   }

   public void onTake(Player var1, ItemStack var2) {
      this.checkTakeAchievements(var2);
      MerchantOffer var3 = this.slots.getActiveOffer();
      if (var3 != null) {
         ItemStack var4 = this.slots.getItem(0);
         ItemStack var5 = this.slots.getItem(1);
         if (var3.take(var4, var5) || var3.take(var5, var4)) {
            this.merchant.notifyTrade(var3);
            var1.awardStat(Stats.TRADED_WITH_VILLAGER);
            this.slots.setItem(0, var4);
            this.slots.setItem(1, var5);
         }

         this.merchant.overrideXp(this.merchant.getVillagerXp() + var3.getXp());
      }

   }
}
