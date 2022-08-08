package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerChatPacket(PlayerChatMessage a, ChatType.BoundNetwork b) implements Packet<ClientGamePacketListener> {
   private final PlayerChatMessage message;
   private final ChatType.BoundNetwork chatType;

   public ClientboundPlayerChatPacket(FriendlyByteBuf var1) {
      this(new PlayerChatMessage(var1), new ChatType.BoundNetwork(var1));
   }

   public ClientboundPlayerChatPacket(PlayerChatMessage var1, ChatType.BoundNetwork var2) {
      super();
      this.message = var1;
      this.chatType = var2;
   }

   public void write(FriendlyByteBuf var1) {
      this.message.write(var1);
      this.chatType.write(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerChat(this);
   }

   public boolean isSkippable() {
      return true;
   }

   public Optional<ChatType.Bound> resolveChatType(RegistryAccess var1) {
      return this.chatType.resolve(var1);
   }

   public PlayerChatMessage message() {
      return this.message;
   }

   public ChatType.BoundNetwork chatType() {
      return this.chatType;
   }
}
