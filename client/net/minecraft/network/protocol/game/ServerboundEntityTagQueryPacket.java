package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundEntityTagQueryPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundEntityTagQueryPacket> STREAM_CODEC = Packet.codec(ServerboundEntityTagQueryPacket::write, ServerboundEntityTagQueryPacket::new);
   private final int transactionId;
   private final int entityId;

   public ServerboundEntityTagQueryPacket(int var1, int var2) {
      super();
      this.transactionId = var1;
      this.entityId = var2;
   }

   private ServerboundEntityTagQueryPacket(FriendlyByteBuf var1) {
      super();
      this.transactionId = var1.readVarInt();
      this.entityId = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeVarInt(this.entityId);
   }

   public PacketType<ServerboundEntityTagQueryPacket> type() {
      return GamePacketTypes.SERVERBOUND_ENTITY_TAG_QUERY;
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
