package com.mojang.realmsclient.dto;

public class RealmsDescriptionDto extends ValueObject {
   public String name;
   public String description;

   public RealmsDescriptionDto(String var1, String var2) {
      super();
      this.name = var1;
      this.description = var2;
   }
}
