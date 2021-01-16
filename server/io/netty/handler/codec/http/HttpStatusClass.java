package io.netty.handler.codec.http;

import io.netty.util.AsciiString;

public enum HttpStatusClass {
   INFORMATIONAL(100, 200, "Informational"),
   SUCCESS(200, 300, "Success"),
   REDIRECTION(300, 400, "Redirection"),
   CLIENT_ERROR(400, 500, "Client Error"),
   SERVER_ERROR(500, 600, "Server Error"),
   UNKNOWN(0, 0, "Unknown Status") {
      public boolean contains(int var1) {
         return var1 < 100 || var1 >= 600;
      }
   };

   private final int min;
   private final int max;
   private final AsciiString defaultReasonPhrase;

   public static HttpStatusClass valueOf(int var0) {
      if (INFORMATIONAL.contains(var0)) {
         return INFORMATIONAL;
      } else if (SUCCESS.contains(var0)) {
         return SUCCESS;
      } else if (REDIRECTION.contains(var0)) {
         return REDIRECTION;
      } else if (CLIENT_ERROR.contains(var0)) {
         return CLIENT_ERROR;
      } else {
         return SERVER_ERROR.contains(var0) ? SERVER_ERROR : UNKNOWN;
      }
   }

   public static HttpStatusClass valueOf(CharSequence var0) {
      if (var0 != null && var0.length() == 3) {
         char var1 = var0.charAt(0);
         return isDigit(var1) && isDigit(var0.charAt(1)) && isDigit(var0.charAt(2)) ? valueOf(digit(var1) * 100) : UNKNOWN;
      } else {
         return UNKNOWN;
      }
   }

   private static int digit(char var0) {
      return var0 - 48;
   }

   private static boolean isDigit(char var0) {
      return var0 >= '0' && var0 <= '9';
   }

   private HttpStatusClass(int var3, int var4, String var5) {
      this.min = var3;
      this.max = var4;
      this.defaultReasonPhrase = AsciiString.cached(var5);
   }

   public boolean contains(int var1) {
      return var1 >= this.min && var1 < this.max;
   }

   AsciiString defaultReasonPhrase() {
      return this.defaultReasonPhrase;
   }

   // $FF: synthetic method
   HttpStatusClass(int var3, int var4, String var5, Object var6) {
      this(var3, var4, var5);
   }
}
