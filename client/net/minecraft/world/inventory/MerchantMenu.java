package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantMenu extends AbstractContainerMenu {
   protected static final int PAYMENT1_SLOT = 0;
   protected static final int PAYMENT2_SLOT = 1;
   protected static final int RESULT_SLOT = 2;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   private static final int SELLSLOT1_X = 136;
   private static final int SELLSLOT2_X = 162;
   private static final int BUYSLOT_X = 220;
   private static final int ROW_Y = 37;
   private final Merchant trader;
   private final MerchantContainer tradeContainer;
   private int merchantLevel;
   private boolean showProgressBar;
   private boolean canRestock;

   public MerchantMenu(int var1, Inventory var2) {
      this(var1, var2, new ClientSideMerchant(var2.player));
   }

   public MerchantMenu(int var1, Inventory var2, Merchant var3) {
      super(MenuType.MERCHANT, var1);
      this.trader = var3;
      this.tradeContainer = new MerchantContainer(var3);
      this.addSlot(new Slot(this.tradeContainer, 0, 136, 37));
      this.addSlot(new Slot(this.tradeContainer, 1, 162, 37));
      this.addSlot(new MerchantResultSlot(var2.player, var3, this.tradeContainer, 2, 220, 37));

      int var4;
      for(var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var2, var5 + var4 * 9 + 9, 108 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(var2, var4, 108 + var4 * 18, 142));
      }

   }

   public void setShowProgressBar(boolean var1) {
      this.showProgressBar = var1;
   }

   public void slotsChanged(Container var1) {
      this.tradeContainer.updateSellItem();
      super.slotsChanged(var1);
   }

   public void setSelectionHint(int var1) {
      this.tradeContainer.setSelectionHint(var1);
   }

   public boolean stillValid(Player var1) {
      return this.trader.getTradingPlayer() == var1;
   }

   public int getTraderXp() {
      return this.trader.getVillagerXp();
   }

   public int getFutureTraderXp() {
      return this.tradeContainer.getFutureXp();
   }

   public void setXp(int var1) {
      this.trader.overrideXp(var1);
   }

   public int getTraderLevel() {
      return this.merchantLevel;
   }

   public void setMerchantLevel(int var1) {
      this.merchantLevel = var1;
   }

   public void setCanRestock(boolean var1) {
      this.canRestock = var1;
   }

   public boolean canRestock() {
      return this.canRestock;
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return false;
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 2) {
            if (!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
            this.playTradeSound();
         } else if (var2 != 0 && var2 != 1) {
            if (var2 >= 3 && var2 < 30) {
               if (!this.moveItemStackTo(var5, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 30 && var2 < 39 && !this.moveItemStackTo(var5, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }

   private void playTradeSound() {
      if (!this.trader.isClientSide()) {
         Entity var1 = (Entity)this.trader;
         var1.level().playLocalSound(var1.getX(), var1.getY(), var1.getZ(), this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
      }

   }

   public void removed(Player var1) {
      super.removed(var1);
      this.trader.setTradingPlayer((Player)null);
      if (!this.trader.isClientSide()) {
         if (!var1.isAlive() || var1 instanceof ServerPlayer && ((ServerPlayer)var1).hasDisconnected()) {
            ItemStack var2 = this.tradeContainer.removeItemNoUpdate(0);
            if (!var2.isEmpty()) {
               var1.drop(var2, false);
            }

            var2 = this.tradeContainer.removeItemNoUpdate(1);
            if (!var2.isEmpty()) {
               var1.drop(var2, false);
            }
         } else if (var1 instanceof ServerPlayer) {
            var1.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(0));
            var1.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(1));
         }

      }
   }

   public void tryMoveItems(int var1) {
      if (var1 >= 0 && this.getOffers().size() > var1) {
         ItemStack var2 = this.tradeContainer.getItem(0);
         if (!var2.isEmpty()) {
            if (!this.moveItemStackTo(var2, 3, 39, true)) {
               return;
            }

            this.tradeContainer.setItem(0, var2);
         }

         ItemStack var3 = this.tradeContainer.getItem(1);
         if (!var3.isEmpty()) {
            if (!this.moveItemStackTo(var3, 3, 39, true)) {
               return;
            }

            this.tradeContainer.setItem(1, var3);
         }

         if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
            MerchantOffer var4 = (MerchantOffer)this.getOffers().get(var1);
            this.moveFromInventoryToPaymentSlot(0, var4.getItemCostA());
            var4.getItemCostB().ifPresent((var1x) -> {
               this.moveFromInventoryToPaymentSlot(1, var1x);
            });
         }

      }
   }

   private void moveFromInventoryToPaymentSlot(int var1, ItemCost var2) {
      for(int var3 = 3; var3 < 39; ++var3) {
         ItemStack var4 = ((Slot)this.slots.get(var3)).getItem();
         if (!var4.isEmpty() && var2.test(var4)) {
            ItemStack var5 = this.tradeContainer.getItem(var1);
            if (var5.isEmpty() || ItemStack.isSameItemSameComponents(var4, var5)) {
               int var6 = var4.getMaxStackSize();
               int var7 = Math.min(var6 - var5.getCount(), var4.getCount());
               ItemStack var8 = var4.copyWithCount(var5.getCount() + var7);
               var4.shrink(var7);
               this.tradeContainer.setItem(var1, var8);
               if (var8.getCount() >= var6) {
                  break;
               }
            }
         }
      }

   }

   public void setOffers(MerchantOffers var1) {
      this.trader.overrideOffers(var1);
   }

   public MerchantOffers getOffers() {
      return this.trader.getOffers();
   }

   public boolean showProgressBar() {
      return this.showProgressBar;
   }
}
