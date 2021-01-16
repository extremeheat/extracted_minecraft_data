package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.Agent;

public class AuthenticationRequest {
   private Agent agent;
   private String username;
   private String password;
   private String clientToken;
   private boolean requestUser = true;

   public AuthenticationRequest(Agent var1, String var2, String var3, String var4) {
      super();
      this.agent = var1;
      this.username = var2;
      this.password = var3;
      this.clientToken = var4;
   }
}
