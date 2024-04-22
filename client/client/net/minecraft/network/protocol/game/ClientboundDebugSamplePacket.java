package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public record ClientboundDebugSamplePacket(long[] sample, RemoteDebugSampleType debugSampleType) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundDebugSamplePacket> STREAM_CODEC = Packet.codec(
      ClientboundDebugSamplePacket::write, ClientboundDebugSamplePacket::new
   );

   private ClientboundDebugSamplePacket(FriendlyByteBuf var1) {
      this(var1.readLongArray(), var1.readEnum(RemoteDebugSampleType.class));
   }

   public ClientboundDebugSamplePacket(long[] sample, RemoteDebugSampleType debugSampleType) {
      super();
      this.sample = sample;
      this.debugSampleType = debugSampleType;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeLongArray(this.sample);
      var1.writeEnum(this.debugSampleType);
   }

   @Override
   public PacketType<ClientboundDebugSamplePacket> type() {
      return GamePacketTypes.CLIENTBOUND_DEBUG_SAMPLE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDebugSample(this);
   }
}