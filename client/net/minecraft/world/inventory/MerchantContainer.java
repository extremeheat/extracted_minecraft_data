package net.minecraft.world.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantContainer implements Container {
   private final Merchant merchant;
   private final NonNullList<ItemStack> itemStacks;
   @Nullable
   private MerchantOffer activeOffer;
   private int selectionHint;
   private int futureXp;

   public MerchantContainer(Merchant var1) {
      super();
      this.itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
      this.merchant = var1;
   }

   public int getContainerSize() {
      return this.itemStacks.size();
   }

   public boolean isEmpty() {
      Iterator var1 = this.itemStacks.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.isEmpty());

      return false;
   }

   public ItemStack getItem(int var1) {
      return (ItemStack)this.itemStacks.get(var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      ItemStack var3 = (ItemStack)this.itemStacks.get(var1);
      if (var1 == 2 && !var3.isEmpty()) {
         return ContainerHelper.removeItem(this.itemStacks, var1, var3.getCount());
      } else {
         ItemStack var4 = ContainerHelper.removeItem(this.itemStacks, var1, var2);
         if (!var4.isEmpty() && this.isPaymentSlot(var1)) {
            this.updateSellItem();
         }

         return var4;
      }
   }

   private boolean isPaymentSlot(int var1) {
      return var1 == 0 || var1 == 1;
   }

   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.itemStacks, var1);
   }

   public void setItem(int var1, ItemStack var2) {
      this.itemStacks.set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
      if (this.isPaymentSlot(var1)) {
         this.updateSellItem();
      }

   }

   public boolean stillValid(Player var1) {
      return this.merchant.getTradingPlayer() == var1;
   }

   public void setChanged() {
      this.updateSellItem();
   }

   public void updateSellItem() {
      this.activeOffer = null;
      ItemStack var1;
      ItemStack var2;
      if (((ItemStack)this.itemStacks.get(0)).isEmpty()) {
         var1 = (ItemStack)this.itemStacks.get(1);
         var2 = ItemStack.EMPTY;
      } else {
         var1 = (ItemStack)this.itemStacks.get(0);
         var2 = (ItemStack)this.itemStacks.get(1);
      }

      if (var1.isEmpty()) {
         this.setItem(2, ItemStack.EMPTY);
         this.futureXp = 0;
      } else {
         MerchantOffers var3 = this.merchant.getOffers();
         if (!var3.isEmpty()) {
            MerchantOffer var4 = var3.getRecipeFor(var1, var2, this.selectionHint);
            if (var4 == null || var4.isOutOfStock()) {
               this.activeOffer = var4;
               var4 = var3.getRecipeFor(var2, var1, this.selectionHint);
            }

            if (var4 != null && !var4.isOutOfStock()) {
               this.activeOffer = var4;
               this.setItem(2, var4.assemble());
               this.futureXp = var4.getXp();
            } else {
               this.setItem(2, ItemStack.EMPTY);
               this.futureXp = 0;
            }
         }

         this.merchant.notifyTradeUpdated(this.getItem(2));
      }
   }

   @Nullable
   public MerchantOffer getActiveOffer() {
      return this.activeOffer;
   }

   public void setSelectionHint(int var1) {
      this.selectionHint = var1;
      this.updateSellItem();
   }

   public void clearContent() {
      this.itemStacks.clear();
   }

   public int getFutureXp() {
      return this.futureXp;
   }
}
