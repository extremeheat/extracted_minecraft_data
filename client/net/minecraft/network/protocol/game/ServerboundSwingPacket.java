package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public class ServerboundSwingPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSwingPacket> STREAM_CODEC = Packet.codec(ServerboundSwingPacket::write, ServerboundSwingPacket::new);
   private final InteractionHand hand;

   public ServerboundSwingPacket(InteractionHand var1) {
      super();
      this.hand = var1;
   }

   private ServerboundSwingPacket(FriendlyByteBuf var1) {
      super();
      this.hand = (InteractionHand)var1.readEnum(InteractionHand.class);
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.hand);
   }

   public PacketType<ServerboundSwingPacket> type() {
      return GamePacketTypes.SERVERBOUND_SWING;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleAnimate(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
