package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSelectAdvancementsTabPacket implements Packet<ClientGamePacketListener> {
   @Nullable
   private final ResourceLocation tab;

   public ClientboundSelectAdvancementsTabPacket(@Nullable ResourceLocation var1) {
      super();
      this.tab = var1;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSelectAdvancementsTab(this);
   }

   public ClientboundSelectAdvancementsTabPacket(FriendlyByteBuf var1) {
      super();
      if (var1.readBoolean()) {
         this.tab = var1.readResourceLocation();
      } else {
         this.tab = null;
      }

   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.tab != null);
      if (this.tab != null) {
         var1.writeResourceLocation(this.tab);
      }

   }

   @Nullable
   public ResourceLocation getTab() {
      return this.tab;
   }
}
