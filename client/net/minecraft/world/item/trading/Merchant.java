package net.minecraft.world.item.trading;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface Merchant {
   void setTradingPlayer(@Nullable Player player);

   @Nullable Player getTradingPlayer();

   MerchantOffers getOffers();

   void overrideOffers(MerchantOffers offers);

   void notifyTrade(MerchantOffer offer);

   void notifyTradeUpdated(ItemStack itemStack);

   int getVillagerXp();

   void overrideXp(final int xp);

   boolean showProgressBar();

   SoundEvent getNotifyTradeSound();

   default boolean canRestock() {
      return false;
   }

   default void openTradingScreen(final Player player, final Component title, final int level) {
   }

   boolean isClientSide();

   boolean stillValid(Player player);
}
