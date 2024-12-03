package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public record ClientboundDebugSamplePacket(long[] sample, RemoteDebugSampleType debugSampleType) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundDebugSamplePacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundDebugSamplePacket>codec(ClientboundDebugSamplePacket::write, ClientboundDebugSamplePacket::new);

   private ClientboundDebugSamplePacket(FriendlyByteBuf var1) {
      this(var1.readLongArray(), (RemoteDebugSampleType)var1.readEnum(RemoteDebugSampleType.class));
   }

   public ClientboundDebugSamplePacket(long[] var1, RemoteDebugSampleType var2) {
      super();
      this.sample = var1;
      this.debugSampleType = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeLongArray(this.sample);
      var1.writeEnum(this.debugSampleType);
   }

   public PacketType<ClientboundDebugSamplePacket> type() {
      return GamePacketTypes.CLIENTBOUND_DEBUG_SAMPLE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDebugSample(this);
   }
}
