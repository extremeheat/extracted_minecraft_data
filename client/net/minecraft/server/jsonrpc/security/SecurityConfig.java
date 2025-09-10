package net.minecraft.server.jsonrpc.security;

import java.security.SecureRandom;

public record SecurityConfig(String secretKey) {
   private static final String SECRET_KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

   public SecurityConfig(String var1) {
      super();
      this.secretKey = var1;
   }

   public static boolean isValid(String var0) {
      return var0 != null && !var0.isEmpty() ? var0.matches("^[a-zA-Z0-9]{40}$") : false;
   }

   public static String generateSecretKey() {
      SecureRandom var0 = new SecureRandom();
      StringBuilder var1 = new StringBuilder(40);

      for(int var2 = 0; var2 < 40; ++var2) {
         var1.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(var0.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length())));
      }

      return var1.toString();
   }
}
