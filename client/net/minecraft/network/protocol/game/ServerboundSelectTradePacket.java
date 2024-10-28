package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundSelectTradePacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSelectTradePacket> STREAM_CODEC = Packet.codec(ServerboundSelectTradePacket::write, ServerboundSelectTradePacket::new);
   private final int item;

   public ServerboundSelectTradePacket(int var1) {
      super();
      this.item = var1;
   }

   private ServerboundSelectTradePacket(FriendlyByteBuf var1) {
      super();
      this.item = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.item);
   }

   public PacketType<ServerboundSelectTradePacket> type() {
      return GamePacketTypes.SERVERBOUND_SELECT_TRADE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSelectTrade(this);
   }

   public int getItem() {
      return this.item;
   }
}
