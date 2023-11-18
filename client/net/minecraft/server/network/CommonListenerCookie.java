package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ClientInformation;

public record CommonListenerCookie(GameProfile a, int b, ClientInformation c) {
   private final GameProfile gameProfile;
   private final int latency;
   private final ClientInformation clientInformation;

   public CommonListenerCookie(GameProfile var1, int var2, ClientInformation var3) {
      super();
      this.gameProfile = var1;
      this.latency = var2;
      this.clientInformation = var3;
   }

   public static CommonListenerCookie createInitial(GameProfile var0) {
      return new CommonListenerCookie(var0, 0, ClientInformation.createDefault());
   }
}
