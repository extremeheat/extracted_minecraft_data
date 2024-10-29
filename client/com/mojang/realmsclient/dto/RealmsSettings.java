package com.mojang.realmsclient.dto;

public record RealmsSettings(boolean hardcore) {
   public RealmsSettings(boolean var1) {
      super();
      this.hardcore = var1;
   }

   public boolean hardcore() {
      return this.hardcore;
   }
}
