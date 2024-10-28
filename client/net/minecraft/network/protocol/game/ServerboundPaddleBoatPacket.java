package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPaddleBoatPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPaddleBoatPacket> STREAM_CODEC = Packet.codec(ServerboundPaddleBoatPacket::write, ServerboundPaddleBoatPacket::new);
   private final boolean left;
   private final boolean right;

   public ServerboundPaddleBoatPacket(boolean var1, boolean var2) {
      super();
      this.left = var1;
      this.right = var2;
   }

   private ServerboundPaddleBoatPacket(FriendlyByteBuf var1) {
      super();
      this.left = var1.readBoolean();
      this.right = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.left);
      var1.writeBoolean(this.right);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePaddleBoat(this);
   }

   public PacketType<ServerboundPaddleBoatPacket> type() {
      return GamePacketTypes.SERVERBOUND_PADDLE_BOAT;
   }

   public boolean getLeft() {
      return this.left;
   }

   public boolean getRight() {
      return this.right;
   }
}
