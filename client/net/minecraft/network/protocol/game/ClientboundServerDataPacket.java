package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundServerDataPacket implements Packet<ClientGamePacketListener> {
   private final Optional<Component> motd;
   private final Optional<String> iconBase64;
   private final boolean previewsChat;

   public ClientboundServerDataPacket(@Nullable Component var1, @Nullable String var2, boolean var3) {
      super();
      this.motd = Optional.ofNullable(var1);
      this.iconBase64 = Optional.ofNullable(var2);
      this.previewsChat = var3;
   }

   public ClientboundServerDataPacket(FriendlyByteBuf var1) {
      super();
      this.motd = var1.readOptional(FriendlyByteBuf::readComponent);
      this.iconBase64 = var1.readOptional(FriendlyByteBuf::readUtf);
      this.previewsChat = var1.readBoolean();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeOptional(this.motd, FriendlyByteBuf::writeComponent);
      var1.writeOptional(this.iconBase64, FriendlyByteBuf::writeUtf);
      var1.writeBoolean(this.previewsChat);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleServerData(this);
   }

   public Optional<Component> getMotd() {
      return this.motd;
   }

   public Optional<String> getIconBase64() {
      return this.iconBase64;
   }

   public boolean previewsChat() {
      return this.previewsChat;
   }
}
