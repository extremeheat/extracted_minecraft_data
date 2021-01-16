package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public class OctalUnescaper extends CharSequenceTranslator {
   public OctalUnescaper() {
      super();
   }

   public int translate(CharSequence var1, int var2, Writer var3) throws IOException {
      int var4 = var1.length() - var2 - 1;
      StringBuilder var5 = new StringBuilder();
      if (var1.charAt(var2) == '\\' && var4 > 0 && this.isOctalDigit(var1.charAt(var2 + 1))) {
         int var6 = var2 + 1;
         int var7 = var2 + 2;
         int var8 = var2 + 3;
         var5.append(var1.charAt(var6));
         if (var4 > 1 && this.isOctalDigit(var1.charAt(var7))) {
            var5.append(var1.charAt(var7));
            if (var4 > 2 && this.isZeroToThree(var1.charAt(var6)) && this.isOctalDigit(var1.charAt(var8))) {
               var5.append(var1.charAt(var8));
            }
         }

         var3.write(Integer.parseInt(var5.toString(), 8));
         return 1 + var5.length();
      } else {
         return 0;
      }
   }

   private boolean isOctalDigit(char var1) {
      return var1 >= '0' && var1 <= '7';
   }

   private boolean isZeroToThree(char var1) {
      return var1 >= '0' && var1 <= '3';
   }
}
