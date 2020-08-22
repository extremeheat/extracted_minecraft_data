package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundChatPacket implements Packet {
   private Component message;
   private ChatType type;

   public ClientboundChatPacket() {
   }

   public ClientboundChatPacket(Component var1) {
      this(var1, ChatType.SYSTEM);
   }

   public ClientboundChatPacket(Component var1, ChatType var2) {
      this.message = var1;
      this.type = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.message = var1.readComponent();
      this.type = ChatType.getForIndex(var1.readByte());
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeComponent(this.message);
      var1.writeByte(this.type.getIndex());
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

   public boolean isSkippable() {
      return true;
   }
}
