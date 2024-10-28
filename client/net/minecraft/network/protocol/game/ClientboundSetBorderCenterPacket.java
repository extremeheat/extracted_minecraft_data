package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderCenterPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetBorderCenterPacket> STREAM_CODEC = Packet.codec(ClientboundSetBorderCenterPacket::write, ClientboundSetBorderCenterPacket::new);
   private final double newCenterX;
   private final double newCenterZ;

   public ClientboundSetBorderCenterPacket(WorldBorder var1) {
      super();
      this.newCenterX = var1.getCenterX();
      this.newCenterZ = var1.getCenterZ();
   }

   private ClientboundSetBorderCenterPacket(FriendlyByteBuf var1) {
      super();
      this.newCenterX = var1.readDouble();
      this.newCenterZ = var1.readDouble();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.newCenterX);
      var1.writeDouble(this.newCenterZ);
   }

   public PacketType<ClientboundSetBorderCenterPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_BORDER_CENTER;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorderCenter(this);
   }

   public double getNewCenterZ() {
      return this.newCenterZ;
   }

   public double getNewCenterX() {
      return this.newCenterX;
   }
}
