package com.mojang.authlib.yggdrasil.request;

public class ValidateRequest {
   private String clientToken;
   private String accessToken;

   public ValidateRequest(String var1, String var2) {
      super();
      this.clientToken = var2;
      this.accessToken = var1;
   }
}
