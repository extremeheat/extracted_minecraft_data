package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundChatPacket implements Packet<ClientGamePacketListener> {
   private final Component message;
   private final ChatType type;
   private final UUID sender;

   public ClientboundChatPacket(Component var1, ChatType var2, UUID var3) {
      super();
      this.message = var1;
      this.type = var2;
      this.sender = var3;
   }

   public ClientboundChatPacket(FriendlyByteBuf var1) {
      super();
      this.message = var1.readComponent();
      this.type = ChatType.getForIndex(var1.readByte());
      this.sender = var1.readUUID();
   }

   public void write(FriendlyByteBuf var1) {
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
