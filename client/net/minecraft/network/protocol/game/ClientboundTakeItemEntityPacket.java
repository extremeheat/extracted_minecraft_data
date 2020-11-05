package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundTakeItemEntityPacket implements Packet<ClientGamePacketListener> {
   private int itemId;
   private int playerId;
   private int amount;

   public ClientboundTakeItemEntityPacket() {
      super();
   }

   public ClientboundTakeItemEntityPacket(int var1, int var2, int var3) {
      super();
      this.itemId = var1;
      this.playerId = var2;
      this.amount = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.itemId = var1.readVarInt();
      this.playerId = var1.readVarInt();
      this.amount = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.itemId);
      var1.writeVarInt(this.playerId);
      var1.writeVarInt(this.amount);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTakeItemEntity(this);
   }

   public int getItemId() {
      return this.itemId;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public int getAmount() {
      return this.amount;
   }
}
