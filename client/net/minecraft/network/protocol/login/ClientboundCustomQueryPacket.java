package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomQueryPacket implements Packet<ClientLoginPacketListener> {
   private int transactionId;
   private ResourceLocation identifier;
   private FriendlyByteBuf data;

   public ClientboundCustomQueryPacket() {
      super();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.transactionId = var1.readVarInt();
      this.identifier = var1.readResourceLocation();
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         this.data = new FriendlyByteBuf(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.transactionId);
      var1.writeResourceLocation(this.identifier);
      var1.writeBytes(this.data.copy());
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleCustomQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }
}
