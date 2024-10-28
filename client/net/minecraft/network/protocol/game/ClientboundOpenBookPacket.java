package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public class ClientboundOpenBookPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundOpenBookPacket> STREAM_CODEC = Packet.codec(ClientboundOpenBookPacket::write, ClientboundOpenBookPacket::new);
   private final InteractionHand hand;

   public ClientboundOpenBookPacket(InteractionHand var1) {
      super();
      this.hand = var1;
   }

   private ClientboundOpenBookPacket(FriendlyByteBuf var1) {
      super();
      this.hand = (InteractionHand)var1.readEnum(InteractionHand.class);
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.hand);
   }

   public PacketType<ClientboundOpenBookPacket> type() {
      return GamePacketTypes.CLIENTBOUND_OPEN_BOOK;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleOpenBook(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
