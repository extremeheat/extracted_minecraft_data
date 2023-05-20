package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundServerDataPacket implements Packet<ClientGamePacketListener> {
   private final Component motd;
   private final Optional<byte[]> iconBytes;
   private final boolean enforcesSecureChat;

   public ClientboundServerDataPacket(Component var1, Optional<byte[]> var2, boolean var3) {
      super();
      this.motd = var1;
      this.iconBytes = var2;
      this.enforcesSecureChat = var3;
   }

   public ClientboundServerDataPacket(FriendlyByteBuf var1) {
      super();
      this.motd = var1.readComponent();
      this.iconBytes = var1.readOptional(FriendlyByteBuf::readByteArray);
      this.enforcesSecureChat = var1.readBoolean();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeComponent(this.motd);
      var1.writeOptional(this.iconBytes, FriendlyByteBuf::writeByteArray);
      var1.writeBoolean(this.enforcesSecureChat);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleServerData(this);
   }

   public Component getMotd() {
      return this.motd;
   }

   public Optional<byte[]> getIconBytes() {
      return this.iconBytes;
   }

   public boolean enforcesSecureChat() {
      return this.enforcesSecureChat;
   }
}
