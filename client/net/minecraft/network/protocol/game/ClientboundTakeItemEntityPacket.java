package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundTakeItemEntityPacket(int itemId, int playerId, int amount) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundTakeItemEntityPacket> STREAM_CODEC;

   public ClientboundTakeItemEntityPacket {
      super();
   }

   public PacketType<ClientboundTakeItemEntityPacket> type() {
      return GamePacketTypes.CLIENTBOUND_TAKE_ITEM_ENTITY;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleTakeItemEntity(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundTakeItemEntityPacket::itemId, ByteBufCodecs.VAR_INT, ClientboundTakeItemEntityPacket::playerId, ByteBufCodecs.VAR_INT, ClientboundTakeItemEntityPacket::amount, ClientboundTakeItemEntityPacket::new);
   }
}
