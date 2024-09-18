package com.mojang.realmsclient.gui.screens;

import javax.annotation.Nullable;

public class UploadResult {
   public final int statusCode;
   @Nullable
   public final String errorMessage;

   UploadResult(int var1, String var2) {
      super();
      this.statusCode = var1;
      this.errorMessage = var2;
   }

   @Nullable
   public String getSimplifiedErrorMessage() {
      if (this.statusCode >= 200 && this.statusCode < 300) {
         return null;
      } else {
         return this.statusCode == 400 && this.errorMessage != null ? this.errorMessage : String.valueOf(this.statusCode);
      }
   }

   public static class Builder {
      private int statusCode = -1;
      private String errorMessage;

      public Builder() {
         super();
      }

      public UploadResult.Builder withStatusCode(int var1) {
         this.statusCode = var1;
         return this;
      }

      public UploadResult.Builder withErrorMessage(@Nullable String var1) {
         this.errorMessage = var1;
         return this;
      }

      public UploadResult build() {
         return new UploadResult(this.statusCode, this.errorMessage);
      }
   }
}
