package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundTakeItemEntityPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundTakeItemEntityPacket> STREAM_CODEC = Packet.codec(ClientboundTakeItemEntityPacket::write, ClientboundTakeItemEntityPacket::new);
   private final int itemId;
   private final int playerId;
   private final int amount;

   public ClientboundTakeItemEntityPacket(int var1, int var2, int var3) {
      super();
      this.itemId = var1;
      this.playerId = var2;
      this.amount = var3;
   }

   private ClientboundTakeItemEntityPacket(FriendlyByteBuf var1) {
      super();
      this.itemId = var1.readVarInt();
      this.playerId = var1.readVarInt();
      this.amount = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.itemId);
      var1.writeVarInt(this.playerId);
      var1.writeVarInt(this.amount);
   }

   public PacketType<ClientboundTakeItemEntityPacket> type() {
      return GamePacketTypes.CLIENTBOUND_TAKE_ITEM_ENTITY;
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
