package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.Nullable;

public record RealmsDescriptionDto(@Nullable String name, String description) implements ReflectionBasedSerialization {
   public RealmsDescriptionDto {
      super();
   }

   @SerializedName("name")
   public @Nullable String name() {
      return this.name;
   }

   @SerializedName("description")
   public String description() {
      return this.description;
   }
}
