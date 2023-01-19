package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundEntityTagQuery implements Packet<ServerGamePacketListener> {
   private final int transactionId;
   private final int entityId;

   public ServerboundEntityTagQuery(int var1, int var2) {
      super();
      this.transactionId = var1;
      this.entityId = var2;
   }

   public ServerboundEntityTagQuery(FriendlyByteBuf var1) {
      super();
      this.transactionId = var1.readVarInt();
      this.entityId = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeVarInt(this.entityId);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
