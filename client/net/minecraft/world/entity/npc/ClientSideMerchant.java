package net.minecraft.world.entity.npc;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class ClientSideMerchant implements Merchant {
   private final Player source;
   private MerchantOffers offers = new MerchantOffers();
   private int xp;

   public ClientSideMerchant(Player var1) {
      super();
      this.source = var1;
   }

   public Player getTradingPlayer() {
      return this.source;
   }

   public void setTradingPlayer(@Nullable Player var1) {
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   public void overrideOffers(MerchantOffers var1) {
      this.offers = var1;
   }

   public void notifyTrade(MerchantOffer var1) {
      var1.increaseUses();
   }

   public void notifyTradeUpdated(ItemStack var1) {
   }

   public boolean isClientSide() {
      return this.source.level().isClientSide;
   }

   public int getVillagerXp() {
      return this.xp;
   }

   public void overrideXp(int var1) {
      this.xp = var1;
   }

   public boolean showProgressBar() {
      return true;
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }
}
