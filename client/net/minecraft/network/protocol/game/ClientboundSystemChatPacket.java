package net.minecraft.network.protocol.game;

import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundSystemChatPacket(Component a, int b) implements Packet<ClientGamePacketListener> {
   private final Component content;
   private final int typeId;

   public ClientboundSystemChatPacket(FriendlyByteBuf var1) {
      this(var1.readComponent(), var1.readVarInt());
   }

   public ClientboundSystemChatPacket(Component var1, int var2) {
      super();
      this.content = var1;
      this.typeId = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeComponent(this.content);
      var1.writeVarInt(this.typeId);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSystemChat(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }

   public ChatType resolveType(Registry<ChatType> var1) {
      return Objects.requireNonNull((ChatType)var1.byId(this.typeId), "Invalid chat type");
   }
}
