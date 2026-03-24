package net.minecraft.world.entity.npc;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jspecify.annotations.Nullable;

public class ClientSideMerchant implements Merchant {
   private final Player source;
   private MerchantOffers offers = new MerchantOffers();
   private int xp;

   public ClientSideMerchant(final Player source) {
      super();
      this.source = source;
   }

   public Player getTradingPlayer() {
      return this.source;
   }

   public void setTradingPlayer(final @Nullable Player player) {
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   public void overrideOffers(final MerchantOffers offers) {
      this.offers = offers;
   }

   public void notifyTrade(final MerchantOffer offer) {
      offer.increaseUses();
   }

   public void notifyTradeUpdated(final ItemStack itemStack) {
   }

   public boolean isClientSide() {
      return this.source.level().isClientSide();
   }

   public boolean stillValid(final Player player) {
      return this.source == player;
   }

   public int getVillagerXp() {
      return this.xp;
   }

   public void overrideXp(final int xp) {
      this.xp = xp;
   }

   public boolean showProgressBar() {
      return true;
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }
}
