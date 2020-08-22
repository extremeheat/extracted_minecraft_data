package com.mojang.realmsclient.gui.screens;

public class UploadResult {
   public final int statusCode;
   public final String errorMessage;

   public UploadResult(int var1, String var2) {
      this.statusCode = var1;
      this.errorMessage = var2;
   }

   public static class Builder {
      private int statusCode = -1;
      private String errorMessage;

      public UploadResult.Builder withStatusCode(int var1) {
         this.statusCode = var1;
         return this;
      }

      public UploadResult.Builder withErrorMessage(String var1) {
         this.errorMessage = var1;
         return this;
      }

      public UploadResult build() {
         return new UploadResult(this.statusCode, this.errorMessage);
      }
   }
}
