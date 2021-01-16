package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class MatchRatingApproachEncoder implements StringEncoder {
   private static final String SPACE = " ";
   private static final String EMPTY = "";
   private static final int ONE = 1;
   private static final int TWO = 2;
   private static final int THREE = 3;
   private static final int FOUR = 4;
   private static final int FIVE = 5;
   private static final int SIX = 6;
   private static final int SEVEN = 7;
   private static final int EIGHT = 8;
   private static final int ELEVEN = 11;
   private static final int TWELVE = 12;
   private static final String PLAIN_ASCII = "AaEeIiOoUuAaEeIiOoUuYyAaEeIiOoUuYyAaOoNnAaEeIiOoUuYyAaCcOoUu";
   private static final String UNICODE = "\u00c0\u00e0\u00c8\u00e8\u00cc\u00ec\u00d2\u00f2\u00d9\u00f9\u00c1\u00e1\u00c9\u00e9\u00cd\u00ed\u00d3\u00f3\u00da\u00fa\u00dd\u00fd\u00c2\u00e2\u00ca\u00ea\u00ce\u00ee\u00d4\u00f4\u00db\u00fb\u0176\u0177\u00c3\u00e3\u00d5\u00f5\u00d1\u00f1\u00c4\u00e4\u00cb\u00eb\u00cf\u00ef\u00d6\u00f6\u00dc\u00fc\u0178\u00ff\u00c5\u00e5\u00c7\u00e7\u0150\u0151\u0170\u0171";
   private static final String[] DOUBLE_CONSONANT = new String[]{"BB", "CC", "DD", "FF", "GG", "HH", "JJ", "KK", "LL", "MM", "NN", "PP", "QQ", "RR", "SS", "TT", "VV", "WW", "XX", "YY", "ZZ"};

   public MatchRatingApproachEncoder() {
      super();
   }

   String cleanName(String var1) {
      String var2 = var1.toUpperCase(Locale.ENGLISH);
      String[] var3 = new String[]{"\\-", "[&]", "\\'", "\\.", "[\\,]"};
      String[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         var2 = var2.replaceAll(var7, "");
      }

      var2 = this.removeAccents(var2);
      var2 = var2.replaceAll("\\s+", "");
      return var2;
   }

   public final Object encode(Object var1) throws EncoderException {
      if (!(var1 instanceof String)) {
         throw new EncoderException("Parameter supplied to Match Rating Approach encoder is not of type java.lang.String");
      } else {
         return this.encode((String)var1);
      }
   }

   public final String encode(String var1) {
      if (var1 != null && !"".equalsIgnoreCase(var1) && !" ".equalsIgnoreCase(var1) && var1.length() != 1) {
         var1 = this.cleanName(var1);
         var1 = this.removeVowels(var1);
         var1 = this.removeDoubleConsonants(var1);
         var1 = this.getFirst3Last3(var1);
         return var1;
      } else {
         return "";
      }
   }

   String getFirst3Last3(String var1) {
      int var2 = var1.length();
      if (var2 > 6) {
         String var3 = var1.substring(0, 3);
         String var4 = var1.substring(var2 - 3, var2);
         return var3 + var4;
      } else {
         return var1;
      }
   }

   int getMinRating(int var1) {
      boolean var2 = false;
      byte var3;
      if (var1 <= 4) {
         var3 = 5;
      } else if (var1 >= 5 && var1 <= 7) {
         var3 = 4;
      } else if (var1 >= 8 && var1 <= 11) {
         var3 = 3;
      } else if (var1 == 12) {
         var3 = 2;
      } else {
         var3 = 1;
      }

      return var3;
   }

   public boolean isEncodeEquals(String var1, String var2) {
      if (var1 != null && !"".equalsIgnoreCase(var1) && !" ".equalsIgnoreCase(var1)) {
         if (var2 != null && !"".equalsIgnoreCase(var2) && !" ".equalsIgnoreCase(var2)) {
            if (var1.length() != 1 && var2.length() != 1) {
               if (var1.equalsIgnoreCase(var2)) {
                  return true;
               } else {
                  var1 = this.cleanName(var1);
                  var2 = this.cleanName(var2);
                  var1 = this.removeVowels(var1);
                  var2 = this.removeVowels(var2);
                  var1 = this.removeDoubleConsonants(var1);
                  var2 = this.removeDoubleConsonants(var2);
                  var1 = this.getFirst3Last3(var1);
                  var2 = this.getFirst3Last3(var2);
                  if (Math.abs(var1.length() - var2.length()) >= 3) {
                     return false;
                  } else {
                     int var3 = Math.abs(var1.length() + var2.length());
                     boolean var4 = false;
                     int var6 = this.getMinRating(var3);
                     int var5 = this.leftToRightThenRightToLeftProcessing(var1, var2);
                     return var5 >= var6;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   boolean isVowel(String var1) {
      return var1.equalsIgnoreCase("E") || var1.equalsIgnoreCase("A") || var1.equalsIgnoreCase("O") || var1.equalsIgnoreCase("I") || var1.equalsIgnoreCase("U");
   }

   int leftToRightThenRightToLeftProcessing(String var1, String var2) {
      char[] var3 = var1.toCharArray();
      char[] var4 = var2.toCharArray();
      int var5 = var1.length() - 1;
      int var6 = var2.length() - 1;
      String var7 = "";
      String var8 = "";
      String var9 = "";
      String var10 = "";

      for(int var11 = 0; var11 < var3.length && var11 <= var6; ++var11) {
         var7 = var1.substring(var11, var11 + 1);
         var8 = var1.substring(var5 - var11, var5 - var11 + 1);
         var9 = var2.substring(var11, var11 + 1);
         var10 = var2.substring(var6 - var11, var6 - var11 + 1);
         if (var7.equals(var9)) {
            var3[var11] = ' ';
            var4[var11] = ' ';
         }

         if (var8.equals(var10)) {
            var3[var5 - var11] = ' ';
            var4[var6 - var11] = ' ';
         }
      }

      String var13 = (new String(var3)).replaceAll("\\s+", "");
      String var12 = (new String(var4)).replaceAll("\\s+", "");
      return var13.length() > var12.length() ? Math.abs(6 - var13.length()) : Math.abs(6 - var12.length());
   }

   String removeAccents(String var1) {
      if (var1 == null) {
         return null;
      } else {
         StringBuilder var2 = new StringBuilder();
         int var3 = var1.length();

         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var1.charAt(var4);
            int var6 = "\u00c0\u00e0\u00c8\u00e8\u00cc\u00ec\u00d2\u00f2\u00d9\u00f9\u00c1\u00e1\u00c9\u00e9\u00cd\u00ed\u00d3\u00f3\u00da\u00fa\u00dd\u00fd\u00c2\u00e2\u00ca\u00ea\u00ce\u00ee\u00d4\u00f4\u00db\u00fb\u0176\u0177\u00c3\u00e3\u00d5\u00f5\u00d1\u00f1\u00c4\u00e4\u00cb\u00eb\u00cf\u00ef\u00d6\u00f6\u00dc\u00fc\u0178\u00ff\u00c5\u00e5\u00c7\u00e7\u0150\u0151\u0170\u0171".indexOf(var5);
            if (var6 > -1) {
               var2.append("AaEeIiOoUuAaEeIiOoUuYyAaEeIiOoUuYyAaOoNnAaEeIiOoUuYyAaCcOoUu".charAt(var6));
            } else {
               var2.append(var5);
            }
         }

         return var2.toString();
      }
   }

   String removeDoubleConsonants(String var1) {
      String var2 = var1.toUpperCase();
      String[] var3 = DOUBLE_CONSONANT;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var2.contains(var6)) {
            String var7 = var6.substring(0, 1);
            var2 = var2.replace(var6, var7);
         }
      }

      return var2;
   }

   String removeVowels(String var1) {
      String var2 = var1.substring(0, 1);
      var1 = var1.replaceAll("A", "");
      var1 = var1.replaceAll("E", "");
      var1 = var1.replaceAll("I", "");
      var1 = var1.replaceAll("O", "");
      var1 = var1.replaceAll("U", "");
      var1 = var1.replaceAll("\\s{2,}\\b", " ");
      return this.isVowel(var2) ? var2 + var1 : var1;
   }
}
