package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.Nullable;

public record InviteCodeRequestDto(@Nullable String code, long realmId, boolean enabled, @Nullable Long expirationDate) implements ReflectionBasedSerialization {
   public InviteCodeRequestDto {
      super();
   }

   @SerializedName("linkId")
   public @Nullable String code() {
      return this.code;
   }

   @SerializedName("realmId")
   public long realmId() {
      return this.realmId;
   }

   @SerializedName("enabled")
   public boolean enabled() {
      return this.enabled;
   }

   @SerializedName("expirationDate")
   public @Nullable Long expirationDate() {
      return this.expirationDate;
   }
}
