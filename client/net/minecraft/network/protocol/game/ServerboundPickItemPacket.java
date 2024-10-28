package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPickItemPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPickItemPacket> STREAM_CODEC = Packet.codec(ServerboundPickItemPacket::write, ServerboundPickItemPacket::new);
   private final int slot;

   public ServerboundPickItemPacket(int var1) {
      super();
      this.slot = var1;
   }

   private ServerboundPickItemPacket(FriendlyByteBuf var1) {
      super();
      this.slot = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.slot);
   }

   public PacketType<ServerboundPickItemPacket> type() {
      return GamePacketTypes.SERVERBOUND_PICK_ITEM;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePickItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
