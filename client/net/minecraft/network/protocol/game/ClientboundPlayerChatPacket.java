package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerChatPacket(
   UUID a, int b, @Nullable MessageSignature c, SignedMessageBody.Packed d, @Nullable Component e, FilterMask f, ChatType.BoundNetwork g
) implements Packet<ClientGamePacketListener> {
   private final UUID sender;
   private final int index;
   @Nullable
   private final MessageSignature signature;
   private final SignedMessageBody.Packed body;
   @Nullable
   private final Component unsignedContent;
   private final FilterMask filterMask;
   private final ChatType.BoundNetwork chatType;

   public ClientboundPlayerChatPacket(FriendlyByteBuf var1) {
      this(
         var1.readUUID(),
         var1.readVarInt(),
         var1.readNullable(MessageSignature::read),
         new SignedMessageBody.Packed(var1),
         var1.readNullable(FriendlyByteBuf::readComponent),
         FilterMask.read(var1),
         new ChatType.BoundNetwork(var1)
      );
   }

   public ClientboundPlayerChatPacket(
      UUID var1,
      int var2,
      @Nullable MessageSignature var3,
      SignedMessageBody.Packed var4,
      @Nullable Component var5,
      FilterMask var6,
      ChatType.BoundNetwork var7
   ) {
      super();
      this.sender = var1;
      this.index = var2;
      this.signature = var3;
      this.body = var4;
      this.unsignedContent = var5;
      this.filterMask = var6;
      this.chatType = var7;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.sender);
      var1.writeVarInt(this.index);
      var1.writeNullable(this.signature, MessageSignature::write);
      this.body.write(var1);
      var1.writeNullable(this.unsignedContent, FriendlyByteBuf::writeComponent);
      FilterMask.write(var1, this.filterMask);
      this.chatType.write(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerChat(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }
}
