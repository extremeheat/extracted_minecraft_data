package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ClientInformation;

public record CommonListenerCookie(GameProfile gameProfile, int latency, ClientInformation clientInformation, boolean transferred) {
   public CommonListenerCookie(GameProfile gameProfile, int latency, ClientInformation clientInformation, boolean transferred) {
      super();
      this.gameProfile = gameProfile;
      this.latency = latency;
      this.clientInformation = clientInformation;
      this.transferred = transferred;
   }

   public static CommonListenerCookie createInitial(GameProfile var0, boolean var1) {
      return new CommonListenerCookie(var0, 0, ClientInformation.createDefault(), var1);
   }

   public GameProfile gameProfile() {
      return this.gameProfile;
   }

   public int latency() {
      return this.latency;
   }

   public ClientInformation clientInformation() {
      return this.clientInformation;
   }

   public boolean transferred() {
      return this.transferred;
   }
}
