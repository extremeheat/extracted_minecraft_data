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
   // $FF: renamed from: xp int
   private int field_461;

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
      return this.source.getLevel().isClientSide;
   }

   public int getVillagerXp() {
      return this.field_461;
   }

   public void overrideXp(int var1) {
      this.field_461 = var1;
   }

   public boolean showProgressBar() {
      return true;
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }
}
