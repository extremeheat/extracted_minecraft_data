package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;

public class RealmsDescriptionDto extends ValueObject implements ReflectionBasedSerialization {
   @SerializedName("name")
   public String name;
   @SerializedName("description")
   public String description;

   public RealmsDescriptionDto(String var1, String var2) {
      super();
      this.name = var1;
      this.description = var2;
   }
}
