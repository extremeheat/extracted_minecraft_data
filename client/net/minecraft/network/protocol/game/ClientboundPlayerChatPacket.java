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

public record ClientboundPlayerChatPacket(UUID sender, int index, @Nullable MessageSignature signature, SignedMessageBody.Packed body, @Nullable Component unsignedContent, FilterMask filterMask, ChatType.Bound chatType) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerChatPacket> STREAM_CODEC = Packet.codec(ClientboundPlayerChatPacket::write, ClientboundPlayerChatPacket::new);

   private ClientboundPlayerChatPacket(RegistryFriendlyByteBuf var1) {
      this(var1.readUUID(), var1.readVarInt(), (MessageSignature)var1.readNullable(MessageSignature::read), new SignedMessageBody.Packed(var1), (Component)FriendlyByteBuf.readNullable(var1, ComponentSerialization.TRUSTED_STREAM_CODEC), FilterMask.read(var1), (ChatType.Bound)ChatType.Bound.STREAM_CODEC.decode(var1));
   }

   public ClientboundPlayerChatPacket(UUID var1, int var2, @Nullable MessageSignature var3, SignedMessageBody.Packed var4, @Nullable Component var5, FilterMask var6, ChatType.Bound var7) {
      super();
      this.sender = var1;
      this.index = var2;
      this.signature = var3;
      this.body = var4;
      this.unsignedContent = var5;
      this.filterMask = var6;
      this.chatType = var7;
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

   public PacketType<ClientboundPlayerChatPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_CHAT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerChat(this);
   }

   public boolean isSkippable() {
      return true;
   }

   public UUID sender() {
      return this.sender;
   }

   public int index() {
      return this.index;
   }

   @Nullable
   public MessageSignature signature() {
      return this.signature;
   }

   public SignedMessageBody.Packed body() {
      return this.body;
   }

   @Nullable
   public Component unsignedContent() {
      return this.unsignedContent;
   }

   public FilterMask filterMask() {
      return this.filterMask;
   }

   public ChatType.Bound chatType() {
      return this.chatType;
   }
}
