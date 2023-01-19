package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundGameProfilePacket implements Packet<ClientLoginPacketListener> {
   private final GameProfile gameProfile;

   public ClientboundGameProfilePacket(GameProfile var1) {
      super();
      this.gameProfile = var1;
   }

   public ClientboundGameProfilePacket(FriendlyByteBuf var1) {
      super();
      this.gameProfile = var1.readGameProfile();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeGameProfile(this.gameProfile);
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleGameProfile(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
