package net.minecraft.network.protocol.status;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.RegistryOps;

public record ClientboundStatusResponsePacket(ServerStatus status) implements Packet<ClientStatusPacketListener> {
   private static final RegistryOps<JsonElement> OPS;
   public static final StreamCodec<ByteBuf, ClientboundStatusResponsePacket> STREAM_CODEC;

   public ClientboundStatusResponsePacket(ServerStatus var1) {
      super();
      this.status = var1;
   }

   public PacketType<ClientboundStatusResponsePacket> type() {
      return StatusPacketTypes.CLIENTBOUND_STATUS_RESPONSE;
   }

   public void handle(ClientStatusPacketListener var1) {
      var1.handleStatusResponse(this);
   }

   static {
      OPS = RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE);
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.lenientJson(32767).apply(ByteBufCodecs.fromCodec(OPS, ServerStatus.CODEC)), ClientboundStatusResponsePacket::status, ClientboundStatusResponsePacket::new);
   }
}
