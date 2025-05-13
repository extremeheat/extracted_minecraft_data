package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundClearDialogPacket implements Packet<ClientCommonPacketListener> {
   public static final ClientboundClearDialogPacket INSTANCE = new ClientboundClearDialogPacket();
   public static final StreamCodec<ByteBuf, ClientboundClearDialogPacket> STREAM_CODEC;

   private ClientboundClearDialogPacket() {
      super();
   }

   public PacketType<ClientboundClearDialogPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_CLEAR_DIALOG;
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleClearDialog(this);
   }

   static {
      STREAM_CODEC = StreamCodec.<ByteBuf, ClientboundClearDialogPacket>unit(INSTANCE);
   }
}
