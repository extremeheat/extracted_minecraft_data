package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomQueryPacket implements Packet<ClientLoginPacketListener> {
   private static final int MAX_PAYLOAD_SIZE = 1048576;
   private final int transactionId;
   private final ResourceLocation identifier;
   private final FriendlyByteBuf data;

   public ClientboundCustomQueryPacket(int var1, ResourceLocation var2, FriendlyByteBuf var3) {
      super();
      this.transactionId = var1;
      this.identifier = var2;
      this.data = var3;
   }

   public ClientboundCustomQueryPacket(FriendlyByteBuf var1) {
      super();
      this.transactionId = var1.readVarInt();
      this.identifier = var1.readResourceLocation();
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         this.data = new FriendlyByteBuf(var1.readBytes(var2));
      } else {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void write(FriendlyByteBuf var1) {
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

   public ResourceLocation getIdentifier() {
      return this.identifier;
   }

   public FriendlyByteBuf getData() {
      return this.data;
   }
}
