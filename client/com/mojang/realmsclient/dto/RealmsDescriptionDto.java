package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

public record RealmsDescriptionDto(@Nullable String name, String description) implements ReflectionBasedSerialization {
   public RealmsDescriptionDto(@Nullable String var1, String var2) {
      super();
      this.name = var1;
      this.description = var2;
   }

   @SerializedName("name")
   @Nullable
   public String name() {
      return this.name;
   }

   @SerializedName("description")
   public String description() {
      return this.description;
   }
}
