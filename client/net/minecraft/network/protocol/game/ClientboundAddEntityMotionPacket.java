package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.phys.Vec3;

public record ClientboundAddEntityMotionPacket(int id, Vec3 impulse) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundAddEntityMotionPacket> STREAM_CODEC;

   public ClientboundAddEntityMotionPacket {
      super();
   }

   public PacketType<ClientboundAddEntityMotionPacket> type() {
      return GamePacketTypes.CLIENTBOUND_ADD_ENTITY_MOTION;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleAddEntityMotion(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundAddEntityMotionPacket::id, Vec3.LP_STREAM_CODEC, ClientboundAddEntityMotionPacket::impulse, ClientboundAddEntityMotionPacket::new);
   }
}
