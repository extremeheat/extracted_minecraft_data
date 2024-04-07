package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPlayerChatPacket(
   UUID sender,
   int index,
   @Nullable MessageSignature signature,
   SignedMessageBody.Packed body,
   @Nullable Component unsignedContent,
   FilterMask filterMask,
   ChatType.Bound chatType
) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerChatPacket> STREAM_CODEC = Packet.codec(
      ClientboundPlayerChatPacket::write, ClientboundPlayerChatPacket::new
   );

   private ClientboundPlayerChatPacket(RegistryFriendlyByteBuf var1) {
      this(
         var1.readUUID(),
         var1.readVarInt(),
         var1.readNullable(MessageSignature::read),
         new SignedMessageBody.Packed(var1),
         FriendlyByteBuf.readNullable(var1, ComponentSerialization.TRUSTED_STREAM_CODEC),
         FilterMask.read(var1),
         ChatType.Bound.STREAM_CODEC.decode(var1)
      );
   }

   public ClientboundPlayerChatPacket(
      UUID sender,
      int index,
      @Nullable MessageSignature signature,
      SignedMessageBody.Packed body,
      @Nullable Component unsignedContent,
      FilterMask filterMask,
      ChatType.Bound chatType
   ) {
      super();
      this.sender = sender;
      this.index = index;
      this.signature = signature;
      this.body = body;
      this.unsignedContent = unsignedContent;
      this.filterMask = filterMask;
      this.chatType = chatType;
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeUUID(this.sender);
      var1.writeVarInt(this.index);
      var1.writeNullable(this.signature, MessageSignature::write);
      this.body.write(var1);
      FriendlyByteBuf.writeNullable(var1, this.unsignedContent, ComponentSerialization.TRUSTED_STREAM_CODEC);
      FilterMask.write(var1, this.filterMask);
      ChatType.Bound.STREAM_CODEC.encode(var1, this.chatType);
   }

   @Override
   public PacketType<ClientboundPlayerChatPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_CHAT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerChat(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }
}
