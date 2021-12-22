package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundHelloPacket implements Packet<ServerLoginPacketListener> {
   private final GameProfile gameProfile;

   public ServerboundHelloPacket(GameProfile var1) {
      super();
      this.gameProfile = var1;
   }

   public ServerboundHelloPacket(FriendlyByteBuf var1) {
      super();
      this.gameProfile = new GameProfile((UUID)null, var1.readUtf(16));
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.gameProfile.getName());
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleHello(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
