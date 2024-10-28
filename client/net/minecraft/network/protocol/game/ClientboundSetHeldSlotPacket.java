package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetHeldSlotPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetHeldSlotPacket> STREAM_CODEC = Packet.codec(ClientboundSetHeldSlotPacket::write, ClientboundSetHeldSlotPacket::new);
   private final int slot;

   public ClientboundSetHeldSlotPacket(int var1) {
      super();
      this.slot = var1;
   }

   private ClientboundSetHeldSlotPacket(FriendlyByteBuf var1) {
      super();
      this.slot = var1.readByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.slot);
   }

   public PacketType<ClientboundSetHeldSlotPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_HELD_SLOT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetHeldSlot(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
