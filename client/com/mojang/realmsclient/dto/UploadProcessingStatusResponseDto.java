package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.Nullable;

public record UploadProcessingStatusResponseDto(@Nullable Result result) implements ReflectionBasedSerialization {
   public UploadProcessingStatusResponseDto {
      super();
   }

   @SerializedName("result")
   public @Nullable Result result() {
      return this.result;
   }

   public static record Result(@Nullable String status) implements ReflectionBasedSerialization {
      public Result {
         super();
      }

      @SerializedName("status")
      public @Nullable String status() {
         return this.status;
      }
   }
}
