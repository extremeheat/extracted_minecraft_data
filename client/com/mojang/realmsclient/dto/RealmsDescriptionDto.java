package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

public class RealmsDescriptionDto extends ValueObject implements ReflectionBasedSerialization {
   @SerializedName("name")
   @Nullable
   public String name;
   @SerializedName("description")
   public String description;

   public RealmsDescriptionDto(@Nullable String var1, String var2) {
      super();
      this.name = var1;
      this.description = var2;
   }
}
