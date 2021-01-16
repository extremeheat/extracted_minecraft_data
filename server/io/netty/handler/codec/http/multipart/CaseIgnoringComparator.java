package io.netty.handler.codec.http.multipart;

import java.io.Serializable;
import java.util.Comparator;

final class CaseIgnoringComparator implements Comparator<CharSequence>, Serializable {
   private static final long serialVersionUID = 4582133183775373862L;
   static final CaseIgnoringComparator INSTANCE = new CaseIgnoringComparator();

   private CaseIgnoringComparator() {
      super();
   }

   public int compare(CharSequence var1, CharSequence var2) {
      int var3 = var1.length();
      int var4 = var2.length();
      int var5 = Math.min(var3, var4);

      for(int var6 = 0; var6 < var5; ++var6) {
         char var7 = var1.charAt(var6);
         char var8 = var2.charAt(var6);
         if (var7 != var8) {
            var7 = Character.toUpperCase(var7);
            var8 = Character.toUpperCase(var8);
            if (var7 != var8) {
               var7 = Character.toLowerCase(var7);
               var8 = Character.toLowerCase(var8);
               if (var7 != var8) {
                  return var7 - var8;
               }
            }
         }
      }

      return var3 - var4;
   }

   private Object readResolve() {
      return INSTANCE;
   }
}
