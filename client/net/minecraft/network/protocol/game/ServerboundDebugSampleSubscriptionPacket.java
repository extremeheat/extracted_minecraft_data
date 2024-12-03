package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public record ServerboundDebugSampleSubscriptionPacket(RemoteDebugSampleType sampleType) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundDebugSampleSubscriptionPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundDebugSampleSubscriptionPacket>codec(ServerboundDebugSampleSubscriptionPacket::write, ServerboundDebugSampleSubscriptionPacket::new);

   private ServerboundDebugSampleSubscriptionPacket(FriendlyByteBuf var1) {
      this((RemoteDebugSampleType)var1.readEnum(RemoteDebugSampleType.class));
   }

   public ServerboundDebugSampleSubscriptionPacket(RemoteDebugSampleType var1) {
      super();
      this.sampleType = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.sampleType);
   }

   public PacketType<ServerboundDebugSampleSubscriptionPacket> type() {
      return GamePacketTypes.SERVERBOUND_DEBUG_SAMPLE_SUBSCRIPTION;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleDebugSampleSubscription(this);
   }
}
