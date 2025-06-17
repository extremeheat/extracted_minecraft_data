package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.dialog.Dialog;

public record ClientboundShowDialogPacket(Holder<Dialog> dialog) implements Packet<ClientCommonPacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundShowDialogPacket> STREAM_CODEC;
   public static final StreamCodec<ByteBuf, ClientboundShowDialogPacket> CONTEXT_FREE_STREAM_CODEC;

   public ClientboundShowDialogPacket(Holder<Dialog> var1) {
      super();
      this.dialog = var1;
   }

   public PacketType<ClientboundShowDialogPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_SHOW_DIALOG;
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleShowDialog(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Dialog.STREAM_CODEC, ClientboundShowDialogPacket::dialog, ClientboundShowDialogPacket::new);
      CONTEXT_FREE_STREAM_CODEC = StreamCodec.composite(Dialog.CONTEXT_FREE_STREAM_CODEC.map(Holder::direct, Holder::value), ClientboundShowDialogPacket::dialog, ClientboundShowDialogPacket::new);
   }
}
