package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundGameProfilePacket implements Packet<ClientLoginPacketListener> {
   private GameProfile gameProfile;

   public ClientboundGameProfilePacket() {
      super();
   }

   public ClientboundGameProfilePacket(GameProfile var1) {
      super();
      this.gameProfile = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      String var2 = var1.readUtf(36);
      String var3 = var1.readUtf(16);
      UUID var4 = UUID.fromString(var2);
      this.gameProfile = new GameProfile(var4, var3);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      UUID var2 = this.gameProfile.getId();
      var1.writeUtf(var2 == null ? "" : var2.toString());
      var1.writeUtf(this.gameProfile.getName());
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleGameProfile(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
