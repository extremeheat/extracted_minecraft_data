package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSelectAdvancementsTabPacket implements Packet<ClientGamePacketListener> {
   @Nullable
   private ResourceLocation tab;

   public ClientboundSelectAdvancementsTabPacket() {
      super();
   }

   public ClientboundSelectAdvancementsTabPacket(@Nullable ResourceLocation var1) {
      super();
      this.tab = var1;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSelectAdvancementsTab(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      if (var1.readBoolean()) {
         this.tab = var1.readResourceLocation();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
