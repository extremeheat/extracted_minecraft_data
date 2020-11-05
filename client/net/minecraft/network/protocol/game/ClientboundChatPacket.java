package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundChatPacket implements Packet<ClientGamePacketListener> {
   private Component message;
   private ChatType type;
   private UUID sender;

   public ClientboundChatPacket() {
      super();
   }

   public ClientboundChatPacket(Component var1, ChatType var2, UUID var3) {
      super();
      this.message = var1;
      this.type = var2;
      this.sender = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.message = var1.readComponent();
      this.type = ChatType.getForIndex(var1.readByte());
      this.sender = var1.readUUID();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeComponent(this.message);
      var1.writeByte(this.type.getIndex());
      var1.writeUUID(this.sender);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChat(this);
   }

   public Component getMessage() {
      return this.message;
   }

   public boolean isSystem() {
      return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
   }

   public ChatType getType() {
      return this.type;
   }

   public UUID getSender() {
      return this.sender;
   }

   public boolean isSkippable() {
      return true;
   }
}
