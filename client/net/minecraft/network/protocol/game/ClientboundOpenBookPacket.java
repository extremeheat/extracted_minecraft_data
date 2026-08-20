package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public record ClientboundOpenBookPacket(InteractionHand hand) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundOpenBookPacket> STREAM_CODEC;

   public ClientboundOpenBookPacket {
      super();
   }

   public PacketType<ClientboundOpenBookPacket> type() {
      return GamePacketTypes.CLIENTBOUND_OPEN_BOOK;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleOpenBook(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(InteractionHand.STREAM_CODEC, ClientboundOpenBookPacket::hand, ClientboundOpenBookPacket::new);
   }
}
