package net.minecraft.world.item.trading;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;

public interface Merchant {
   void setTradingPlayer(@Nullable Player var1);

   @Nullable
   Player getTradingPlayer();

   MerchantOffers getOffers();

   void overrideOffers(MerchantOffers var1);

   void notifyTrade(MerchantOffer var1);

   void notifyTradeUpdated(ItemStack var1);

   int getVillagerXp();

   void overrideXp(int var1);

   boolean showProgressBar();

   SoundEvent getNotifyTradeSound();

   default boolean canRestock() {
      return false;
   }

   default void openTradingScreen(Player var1, Component var2, int var3) {
      OptionalInt var4 = var1.openMenu(new SimpleMenuProvider((var1x, var2x, var3x) -> {
         return new MerchantMenu(var1x, var2x, this);
      }, var2));
      if (var4.isPresent()) {
         MerchantOffers var5 = this.getOffers();
         if (!var5.isEmpty()) {
            var1.sendMerchantOffers(var4.getAsInt(), var5, var3, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
         }
      }

   }

   boolean isClientSide();
}
