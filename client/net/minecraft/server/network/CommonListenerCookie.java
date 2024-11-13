package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ClientInformation;

public record CommonListenerCookie(GameProfile gameProfile, int latency, ClientInformation clientInformation, boolean transferred) {
   public CommonListenerCookie(GameProfile var1, int var2, ClientInformation var3, boolean var4) {
      super();
      this.gameProfile = var1;
      this.latency = var2;
      this.clientInformation = var3;
      this.transferred = var4;
   }

   public static CommonListenerCookie createInitial(GameProfile var0, boolean var1) {
      return new CommonListenerCookie(var0, 0, ClientInformation.createDefault(), var1);
   }
}
