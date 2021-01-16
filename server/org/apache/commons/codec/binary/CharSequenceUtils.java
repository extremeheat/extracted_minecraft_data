package org.apache.commons.codec.binary;

public class CharSequenceUtils {
   public CharSequenceUtils() {
      super();
   }

   static boolean regionMatches(CharSequence var0, boolean var1, int var2, CharSequence var3, int var4, int var5) {
      if (var0 instanceof String && var3 instanceof String) {
         return ((String)var0).regionMatches(var1, var2, (String)var3, var4, var5);
      } else {
         int var6 = var2;
         int var7 = var4;
         int var8 = var5;

         while(var8-- > 0) {
            char var9 = var0.charAt(var6++);
            char var10 = var3.charAt(var7++);
            if (var9 != var10) {
               if (!var1) {
                  return false;
               }

               if (Character.toUpperCase(var9) != Character.toUpperCase(var10) && Character.toLowerCase(var9) != Character.toLowerCase(var10)) {
                  return false;
               }
            }
         }

         return true;
      }
   }
}
