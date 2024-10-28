package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderSizePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetBorderSizePacket> STREAM_CODEC = Packet.codec(ClientboundSetBorderSizePacket::write, ClientboundSetBorderSizePacket::new);
   private final double size;

   public ClientboundSetBorderSizePacket(WorldBorder var1) {
      super();
      this.size = var1.getLerpTarget();
   }

   private ClientboundSetBorderSizePacket(FriendlyByteBuf var1) {
      super();
      this.size = var1.readDouble();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.size);
   }

   public PacketType<ClientboundSetBorderSizePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_BORDER_SIZE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorderSize(this);
   }

   public double getSize() {
      return this.size;
   }
}
