package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.core.SerializableUUID;
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
      int[] var2 = new int[4];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = var1.readInt();
      }

      UUID var5 = SerializableUUID.uuidFromIntArray(var2);
      String var4 = var1.readUtf(16);
      this.gameProfile = new GameProfile(var5, var4);
   }

   public void write(FriendlyByteBuf var1) {
      int[] var2 = SerializableUUID.uuidToIntArray(this.gameProfile.getId());
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var1.writeInt(var5);
      }

      var1.writeUtf(this.gameProfile.getName());
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleGameProfile(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
