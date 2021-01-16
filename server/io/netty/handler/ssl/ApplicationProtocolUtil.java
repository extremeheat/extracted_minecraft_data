package io.netty.handler.ssl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class ApplicationProtocolUtil {
   private static final int DEFAULT_LIST_SIZE = 2;

   private ApplicationProtocolUtil() {
      super();
   }

   static List<String> toList(Iterable<String> var0) {
      return toList(2, (Iterable)var0);
   }

   static List<String> toList(int var0, Iterable<String> var1) {
      if (var1 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList(var0);
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            if (var4 == null || var4.isEmpty()) {
               throw new IllegalArgumentException("protocol cannot be null or empty");
            }

            var2.add(var4);
         }

         if (var2.isEmpty()) {
            throw new IllegalArgumentException("protocols cannot empty");
         } else {
            return var2;
         }
      }
   }

   static List<String> toList(String... var0) {
      return toList(2, (String[])var0);
   }

   static List<String> toList(int var0, String... var1) {
      if (var1 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList(var0);
         String[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            if (var6 == null || var6.isEmpty()) {
               throw new IllegalArgumentException("protocol cannot be null or empty");
            }

            var2.add(var6);
         }

         if (var2.isEmpty()) {
            throw new IllegalArgumentException("protocols cannot empty");
         } else {
            return var2;
         }
      }
   }
}
