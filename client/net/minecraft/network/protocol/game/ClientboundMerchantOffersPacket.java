package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.trading.MerchantOffers;

public class ClientboundMerchantOffersPacket implements Packet<ClientGamePacketListener> {
   private final int containerId;
   private final MerchantOffers offers;
   private final int villagerLevel;
   private final int villagerXp;
   private final boolean showProgress;
   private final boolean canRestock;

   public ClientboundMerchantOffersPacket(int var1, MerchantOffers var2, int var3, int var4, boolean var5, boolean var6) {
      super();
      this.containerId = var1;
      this.offers = var2;
      this.villagerLevel = var3;
      this.villagerXp = var4;
      this.showProgress = var5;
      this.canRestock = var6;
   }

   public ClientboundMerchantOffersPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readVarInt();
      this.offers = MerchantOffers.createFromStream(var1);
      this.villagerLevel = var1.readVarInt();
      this.villagerXp = var1.readVarInt();
      this.showProgress = var1.readBoolean();
      this.canRestock = var1.readBoolean();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.containerId);
      this.offers.writeToStream(var1);
      var1.writeVarInt(this.villagerLevel);
      var1.writeVarInt(this.villagerXp);
      var1.writeBoolean(this.showProgress);
      var1.writeBoolean(this.canRestock);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMerchantOffers(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   public int getVillagerLevel() {
      return this.villagerLevel;
   }

   public int getVillagerXp() {
      return this.villagerXp;
   }

   public boolean showProgress() {
      return this.showProgress;
   }

   public boolean canRestock() {
      return this.canRestock;
   }
}
