package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.GameProfile;

public class RefreshRequest {
   private String clientToken;
   private String accessToken;
   private GameProfile selectedProfile;
   private boolean requestUser;

   public RefreshRequest(String var1, String var2) {
      this(var1, var2, (GameProfile)null);
   }

   public RefreshRequest(String var1, String var2, GameProfile var3) {
      super();
      this.requestUser = true;
      this.clientToken = var2;
      this.accessToken = var1;
      this.selectedProfile = var3;
   }
}
