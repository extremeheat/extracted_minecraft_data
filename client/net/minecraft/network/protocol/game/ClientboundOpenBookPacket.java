package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public class ClientboundOpenBookPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundOpenBookPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundOpenBookPacket>codec(ClientboundOpenBookPacket::write, ClientboundOpenBookPacket::new);
   private final InteractionHand hand;

   public ClientboundOpenBookPacket(final InteractionHand hand) {
      super();
      this.hand = hand;
   }

   private ClientboundOpenBookPacket(final FriendlyByteBuf input) {
      super();
      this.hand = (InteractionHand)input.readEnum(InteractionHand.class);
   }

   private void write(final FriendlyByteBuf output) {
      output.writeEnum(this.hand);
   }

   public PacketType<ClientboundOpenBookPacket> type() {
      return GamePacketTypes.CLIENTBOUND_OPEN_BOOK;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleOpenBook(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
