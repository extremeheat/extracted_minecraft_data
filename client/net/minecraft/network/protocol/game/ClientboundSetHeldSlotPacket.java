package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetHeldSlotPacket(int slot) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundSetHeldSlotPacket> STREAM_CODEC;

   public ClientboundSetHeldSlotPacket(int var1) {
      super();
      this.slot = var1;
   }

   public PacketType<ClientboundSetHeldSlotPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_HELD_SLOT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetHeldSlot(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundSetHeldSlotPacket::slot, ClientboundSetHeldSlotPacket::new);
   }
}
