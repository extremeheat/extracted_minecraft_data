package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.Nullable;

public record DownloadSessionResponseDto(@Nullable Result result) implements ReflectionBasedSerialization {
   public DownloadSessionResponseDto {
      super();
   }

   @SerializedName("result")
   public @Nullable Result result() {
      return this.result;
   }

   public static record Result(@Nullable String downloadUrl, @Nullable String status) implements ReflectionBasedSerialization {
      public Result {
         super();
      }

      @SerializedName("downloadUrl")
      public @Nullable String downloadUrl() {
         return this.downloadUrl;
      }

      @SerializedName("status")
      public @Nullable String status() {
         return this.status;
      }
   }
}
