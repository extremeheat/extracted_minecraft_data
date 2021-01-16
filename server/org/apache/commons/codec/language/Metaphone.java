package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class Metaphone implements StringEncoder {
   private static final String VOWELS = "AEIOU";
   private static final String FRONTV = "EIY";
   private static final String VARSON = "CSPTG";
   private int maxCodeLen = 4;

   public Metaphone() {
      super();
   }

   public String metaphone(String var1) {
      boolean var2 = false;
      int var3;
      if (var1 != null && (var3 = var1.length()) != 0) {
         if (var3 == 1) {
            return var1.toUpperCase(Locale.ENGLISH);
         } else {
            char[] var4 = var1.toUpperCase(Locale.ENGLISH).toCharArray();
            StringBuilder var5 = new StringBuilder(40);
            StringBuilder var6 = new StringBuilder(10);
            switch(var4[0]) {
            case 'A':
               if (var4[1] == 'E') {
                  var5.append(var4, 1, var4.length - 1);
               } else {
                  var5.append(var4);
               }
               break;
            case 'G':
            case 'K':
            case 'P':
               if (var4[1] == 'N') {
                  var5.append(var4, 1, var4.length - 1);
               } else {
                  var5.append(var4);
               }
               break;
            case 'W':
               if (var4[1] == 'R') {
                  var5.append(var4, 1, var4.length - 1);
               } else if (var4[1] == 'H') {
                  var5.append(var4, 1, var4.length - 1);
                  var5.setCharAt(0, 'W');
               } else {
                  var5.append(var4);
               }
               break;
            case 'X':
               var4[0] = 'S';
               var5.append(var4);
               break;
            default:
               var5.append(var4);
            }

            int var7 = var5.length();
            int var8 = 0;

            while(var6.length() < this.getMaxCodeLen() && var8 < var7) {
               char var9 = var5.charAt(var8);
               if (var9 != 'C' && this.isPreviousChar(var5, var8, var9)) {
                  ++var8;
               } else {
                  switch(var9) {
                  case 'A':
                  case 'E':
                  case 'I':
                  case 'O':
                  case 'U':
                     if (var8 == 0) {
                        var6.append(var9);
                     }
                     break;
                  case 'B':
                     if (!this.isPreviousChar(var5, var8, 'M') || !this.isLastChar(var7, var8)) {
                        var6.append(var9);
                     }
                     break;
                  case 'C':
                     if (this.isPreviousChar(var5, var8, 'S') && !this.isLastChar(var7, var8) && "EIY".indexOf(var5.charAt(var8 + 1)) >= 0) {
                        break;
                     }

                     if (this.regionMatch(var5, var8, "CIA")) {
                        var6.append('X');
                     } else if (!this.isLastChar(var7, var8) && "EIY".indexOf(var5.charAt(var8 + 1)) >= 0) {
                        var6.append('S');
                     } else if (this.isPreviousChar(var5, var8, 'S') && this.isNextChar(var5, var8, 'H')) {
                        var6.append('K');
                     } else {
                        if (this.isNextChar(var5, var8, 'H')) {
                           if (var8 == 0 && var7 >= 3 && this.isVowel(var5, 2)) {
                              var6.append('K');
                              break;
                           }

                           var6.append('X');
                           break;
                        }

                        var6.append('K');
                     }
                     break;
                  case 'D':
                     if (!this.isLastChar(var7, var8 + 1) && this.isNextChar(var5, var8, 'G') && "EIY".indexOf(var5.charAt(var8 + 2)) >= 0) {
                        var6.append('J');
                        var8 += 2;
                        break;
                     }

                     var6.append('T');
                     break;
                  case 'F':
                  case 'J':
                  case 'L':
                  case 'M':
                  case 'N':
                  case 'R':
                     var6.append(var9);
                     break;
                  case 'G':
                     if (this.isLastChar(var7, var8 + 1) && this.isNextChar(var5, var8, 'H') || !this.isLastChar(var7, var8 + 1) && this.isNextChar(var5, var8, 'H') && !this.isVowel(var5, var8 + 2) || var8 > 0 && (this.regionMatch(var5, var8, "GN") || this.regionMatch(var5, var8, "GNED"))) {
                        break;
                     }

                     if (this.isPreviousChar(var5, var8, 'G')) {
                        var2 = true;
                     } else {
                        var2 = false;
                     }

                     if (!this.isLastChar(var7, var8) && "EIY".indexOf(var5.charAt(var8 + 1)) >= 0 && !var2) {
                        var6.append('J');
                        break;
                     }

                     var6.append('K');
                     break;
                  case 'H':
                     if (!this.isLastChar(var7, var8) && (var8 <= 0 || "CSPTG".indexOf(var5.charAt(var8 - 1)) < 0) && this.isVowel(var5, var8 + 1)) {
                        var6.append('H');
                     }
                     break;
                  case 'K':
                     if (var8 > 0) {
                        if (!this.isPreviousChar(var5, var8, 'C')) {
                           var6.append(var9);
                        }
                     } else {
                        var6.append(var9);
                     }
                     break;
                  case 'P':
                     if (this.isNextChar(var5, var8, 'H')) {
                        var6.append('F');
                     } else {
                        var6.append(var9);
                     }
                     break;
                  case 'Q':
                     var6.append('K');
                     break;
                  case 'S':
                     if (!this.regionMatch(var5, var8, "SH") && !this.regionMatch(var5, var8, "SIO") && !this.regionMatch(var5, var8, "SIA")) {
                        var6.append('S');
                        break;
                     }

                     var6.append('X');
                     break;
                  case 'T':
                     if (!this.regionMatch(var5, var8, "TIA") && !this.regionMatch(var5, var8, "TIO")) {
                        if (!this.regionMatch(var5, var8, "TCH")) {
                           if (this.regionMatch(var5, var8, "TH")) {
                              var6.append('0');
                           } else {
                              var6.append('T');
                           }
                        }
                        break;
                     }

                     var6.append('X');
                     break;
                  case 'V':
                     var6.append('F');
                     break;
                  case 'W':
                  case 'Y':
                     if (!this.isLastChar(var7, var8) && this.isVowel(var5, var8 + 1)) {
                        var6.append(var9);
                     }
                     break;
                  case 'X':
                     var6.append('K');
                     var6.append('S');
                     break;
                  case 'Z':
                     var6.append('S');
                  }

                  ++var8;
               }

               if (var6.length() > this.getMaxCodeLen()) {
                  var6.setLength(this.getMaxCodeLen());
               }
            }

            return var6.toString();
         }
      } else {
         return "";
      }
   }

   private boolean isVowel(StringBuilder var1, int var2) {
      return "AEIOU".indexOf(var1.charAt(var2)) >= 0;
   }

   private boolean isPreviousChar(StringBuilder var1, int var2, char var3) {
      boolean var4 = false;
      if (var2 > 0 && var2 < var1.length()) {
         var4 = var1.charAt(var2 - 1) == var3;
      }

      return var4;
   }

   private boolean isNextChar(StringBuilder var1, int var2, char var3) {
      boolean var4 = false;
      if (var2 >= 0 && var2 < var1.length() - 1) {
         var4 = var1.charAt(var2 + 1) == var3;
      }

      return var4;
   }

   private boolean regionMatch(StringBuilder var1, int var2, String var3) {
      boolean var4 = false;
      if (var2 >= 0 && var2 + var3.length() - 1 < var1.length()) {
         String var5 = var1.substring(var2, var2 + var3.length());
         var4 = var5.equals(var3);
      }

      return var4;
   }

   private boolean isLastChar(int var1, int var2) {
      return var2 + 1 == var1;
   }

   public Object encode(Object var1) throws EncoderException {
      if (!(var1 instanceof String)) {
         throw new EncoderException("Parameter supplied to Metaphone encode is not of type java.lang.String");
      } else {
         return this.metaphone((String)var1);
      }
   }

   public String encode(String var1) {
      return this.metaphone(var1);
   }

   public boolean isMetaphoneEqual(String var1, String var2) {
      return this.metaphone(var1).equals(this.metaphone(var2));
   }

   public int getMaxCodeLen() {
      return this.maxCodeLen;
   }

   public void setMaxCodeLen(int var1) {
      this.maxCodeLen = var1;
   }
}
