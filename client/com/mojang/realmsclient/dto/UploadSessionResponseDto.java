package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public record UploadSessionResponseDto(@Nullable Result result) implements ReflectionBasedSerialization {
   public UploadSessionResponseDto {
      super();
   }

   @SerializedName("result")
   public @Nullable Result result() {
      return this.result;
   }

   public static record Result(@Nullable String token, @Nullable String uploadUrl, @Nullable String statusUrl, @Nullable String completeUrl, @Nullable String cancelUrl, @Nullable String renewUrl, @Nullable String expiresAtUtc, @Nullable Boolean supportsBlockUpload, @Nullable BigDecimal maxSinglePutSizeBytes, @Nullable BigDecimal recommendedBlockSizeBytes, @Nullable BigDecimal maxBlockCount, @Nullable BigDecimal maxUploadSizeBytes, @Nullable Set<String> acceptedArchiveFormats) implements ReflectionBasedSerialization {
      public Result {
         super();
      }

      @SerializedName("token")
      public @Nullable String token() {
         return this.token;
      }

      @SerializedName("uploadUrl")
      public @Nullable String uploadUrl() {
         return this.uploadUrl;
      }

      @SerializedName("statusUrl")
      public @Nullable String statusUrl() {
         return this.statusUrl;
      }

      @SerializedName("completeUrl")
      public @Nullable String completeUrl() {
         return this.completeUrl;
      }

      @SerializedName("cancelUrl")
      public @Nullable String cancelUrl() {
         return this.cancelUrl;
      }

      @SerializedName("renewUrl")
      public @Nullable String renewUrl() {
         return this.renewUrl;
      }

      @SerializedName("expiresAtUtc")
      public @Nullable String expiresAtUtc() {
         return this.expiresAtUtc;
      }

      @SerializedName("supportsBlockUpload")
      public @Nullable Boolean supportsBlockUpload() {
         return this.supportsBlockUpload;
      }

      @SerializedName("maxSinglePutSizeBytes")
      public @Nullable BigDecimal maxSinglePutSizeBytes() {
         return this.maxSinglePutSizeBytes;
      }

      @SerializedName("recommendedBlockSizeBytes")
      public @Nullable BigDecimal recommendedBlockSizeBytes() {
         return this.recommendedBlockSizeBytes;
      }

      @SerializedName("maxBlockCount")
      public @Nullable BigDecimal maxBlockCount() {
         return this.maxBlockCount;
      }

      @SerializedName("maxUploadSizeBytes")
      public @Nullable BigDecimal maxUploadSizeBytes() {
         return this.maxUploadSizeBytes;
      }

      @SerializedName("acceptedArchiveFormats")
      public @Nullable Set<String> acceptedArchiveFormats() {
         return this.acceptedArchiveFormats;
      }
   }
}
