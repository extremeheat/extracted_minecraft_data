package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundSetCarriedItemPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSetCarriedItemPacket> STREAM_CODEC = Packet.codec(ServerboundSetCarriedItemPacket::write, ServerboundSetCarriedItemPacket::new);
   private final int slot;

   public ServerboundSetCarriedItemPacket(int var1) {
      super();
      this.slot = var1;
   }

   private ServerboundSetCarriedItemPacket(FriendlyByteBuf var1) {
      super();
      this.slot = var1.readShort();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeShort(this.slot);
   }

   public PacketType<ServerboundSetCarriedItemPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_CARRIED_ITEM;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
