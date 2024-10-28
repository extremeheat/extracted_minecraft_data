package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetCarriedItemPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetCarriedItemPacket> STREAM_CODEC = Packet.codec(ClientboundSetCarriedItemPacket::write, ClientboundSetCarriedItemPacket::new);
   private final int slot;

   public ClientboundSetCarriedItemPacket(int var1) {
      super();
      this.slot = var1;
   }

   private ClientboundSetCarriedItemPacket(FriendlyByteBuf var1) {
      super();
      this.slot = var1.readByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.slot);
   }

   public PacketType<ClientboundSetCarriedItemPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_CARRIED_ITEM;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
