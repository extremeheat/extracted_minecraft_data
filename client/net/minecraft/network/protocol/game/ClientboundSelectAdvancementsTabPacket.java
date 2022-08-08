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
      this.tab = (ResourceLocation)var1.readNullable(FriendlyByteBuf::readResourceLocation);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeNullable(this.tab, FriendlyByteBuf::writeResourceLocation);
   }

   @Nullable
   public ResourceLocation getTab() {
      return this.tab;
   }
}
