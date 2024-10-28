package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderLerpSizePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetBorderLerpSizePacket> STREAM_CODEC = Packet.codec(ClientboundSetBorderLerpSizePacket::write, ClientboundSetBorderLerpSizePacket::new);
   private final double oldSize;
   private final double newSize;
   private final long lerpTime;

   public ClientboundSetBorderLerpSizePacket(WorldBorder var1) {
      super();
      this.oldSize = var1.getSize();
      this.newSize = var1.getLerpTarget();
      this.lerpTime = var1.getLerpRemainingTime();
   }

   private ClientboundSetBorderLerpSizePacket(FriendlyByteBuf var1) {
      super();
      this.oldSize = var1.readDouble();
      this.newSize = var1.readDouble();
      this.lerpTime = var1.readVarLong();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.oldSize);
      var1.writeDouble(this.newSize);
      var1.writeVarLong(this.lerpTime);
   }

   public PacketType<ClientboundSetBorderLerpSizePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_BORDER_LERP_SIZE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorderLerpSize(this);
   }

   public double getOldSize() {
      return this.oldSize;
   }

   public double getNewSize() {
      return this.newSize;
   }

   public long getLerpTime() {
      return this.lerpTime;
   }
}
