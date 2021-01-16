package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;

public class DoubleMetaphone implements StringEncoder {
   private static final String VOWELS = "AEIOUY";
   private static final String[] SILENT_START = new String[]{"GN", "KN", "PN", "WR", "PS"};
   private static final String[] L_R_N_M_B_H_F_V_W_SPACE = new String[]{"L", "R", "N", "M", "B", "H", "F", "V", "W", " "};
   private static final String[] ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER = new String[]{"ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER"};
   private static final String[] L_T_K_S_N_M_B_Z = new String[]{"L", "T", "K", "S", "N", "M", "B", "Z"};
   private int maxCodeLen = 4;

   public DoubleMetaphone() {
      super();
   }

   public String doubleMetaphone(String var1) {
      return this.doubleMetaphone(var1, false);
   }

   public String doubleMetaphone(String var1, boolean var2) {
      var1 = this.cleanInput(var1);
      if (var1 == null) {
         return null;
      } else {
         boolean var3 = this.isSlavoGermanic(var1);
         int var4 = this.isSilentStart(var1) ? 1 : 0;
         DoubleMetaphone.DoubleMetaphoneResult var5 = new DoubleMetaphone.DoubleMetaphoneResult(this.getMaxCodeLen());

         while(!var5.isComplete() && var4 <= var1.length() - 1) {
            switch(var1.charAt(var4)) {
            case 'A':
            case 'E':
            case 'I':
            case 'O':
            case 'U':
            case 'Y':
               var4 = this.handleAEIOUY(var5, var4);
               break;
            case 'B':
               var5.append('P');
               var4 = this.charAt(var1, var4 + 1) == 'B' ? var4 + 2 : var4 + 1;
               break;
            case 'C':
               var4 = this.handleC(var1, var5, var4);
               break;
            case 'D':
               var4 = this.handleD(var1, var5, var4);
               break;
            case 'F':
               var5.append('F');
               var4 = this.charAt(var1, var4 + 1) == 'F' ? var4 + 2 : var4 + 1;
               break;
            case 'G':
               var4 = this.handleG(var1, var5, var4, var3);
               break;
            case 'H':
               var4 = this.handleH(var1, var5, var4);
               break;
            case 'J':
               var4 = this.handleJ(var1, var5, var4, var3);
               break;
            case 'K':
               var5.append('K');
               var4 = this.charAt(var1, var4 + 1) == 'K' ? var4 + 2 : var4 + 1;
               break;
            case 'L':
               var4 = this.handleL(var1, var5, var4);
               break;
            case 'M':
               var5.append('M');
               var4 = this.conditionM0(var1, var4) ? var4 + 2 : var4 + 1;
               break;
            case 'N':
               var5.append('N');
               var4 = this.charAt(var1, var4 + 1) == 'N' ? var4 + 2 : var4 + 1;
               break;
            case 'P':
               var4 = this.handleP(var1, var5, var4);
               break;
            case 'Q':
               var5.append('K');
               var4 = this.charAt(var1, var4 + 1) == 'Q' ? var4 + 2 : var4 + 1;
               break;
            case 'R':
               var4 = this.handleR(var1, var5, var4, var3);
               break;
            case 'S':
               var4 = this.handleS(var1, var5, var4, var3);
               break;
            case 'T':
               var4 = this.handleT(var1, var5, var4);
               break;
            case 'V':
               var5.append('F');
               var4 = this.charAt(var1, var4 + 1) == 'V' ? var4 + 2 : var4 + 1;
               break;
            case 'W':
               var4 = this.handleW(var1, var5, var4);
               break;
            case 'X':
               var4 = this.handleX(var1, var5, var4);
               break;
            case 'Z':
               var4 = this.handleZ(var1, var5, var4, var3);
               break;
            case '\u00c7':
               var5.append('S');
               ++var4;
               break;
            case '\u00d1':
               var5.append('N');
               ++var4;
               break;
            default:
               ++var4;
            }
         }

         return var2 ? var5.getAlternate() : var5.getPrimary();
      }
   }

   public Object encode(Object var1) throws EncoderException {
      if (!(var1 instanceof String)) {
         throw new EncoderException("DoubleMetaphone encode parameter is not of type String");
      } else {
         return this.doubleMetaphone((String)var1);
      }
   }

   public String encode(String var1) {
      return this.doubleMetaphone(var1);
   }

   public boolean isDoubleMetaphoneEqual(String var1, String var2) {
      return this.isDoubleMetaphoneEqual(var1, var2, false);
   }

   public boolean isDoubleMetaphoneEqual(String var1, String var2, boolean var3) {
      return StringUtils.equals(this.doubleMetaphone(var1, var3), this.doubleMetaphone(var2, var3));
   }

   public int getMaxCodeLen() {
      return this.maxCodeLen;
   }

   public void setMaxCodeLen(int var1) {
      this.maxCodeLen = var1;
   }

   private int handleAEIOUY(DoubleMetaphone.DoubleMetaphoneResult var1, int var2) {
      if (var2 == 0) {
         var1.append('A');
      }

      return var2 + 1;
   }

   private int handleC(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (this.conditionC0(var1, var3)) {
         var2.append('K');
         var3 += 2;
      } else if (var3 == 0 && contains(var1, var3, 6, "CAESAR")) {
         var2.append('S');
         var3 += 2;
      } else if (contains(var1, var3, 2, "CH")) {
         var3 = this.handleCH(var1, var2, var3);
      } else if (contains(var1, var3, 2, "CZ") && !contains(var1, var3 - 2, 4, "WICZ")) {
         var2.append('S', 'X');
         var3 += 2;
      } else if (contains(var1, var3 + 1, 3, "CIA")) {
         var2.append('X');
         var3 += 3;
      } else {
         if (contains(var1, var3, 2, "CC") && (var3 != 1 || this.charAt(var1, 0) != 'M')) {
            return this.handleCC(var1, var2, var3);
         }

         if (contains(var1, var3, 2, "CK", "CG", "CQ")) {
            var2.append('K');
            var3 += 2;
         } else if (contains(var1, var3, 2, "CI", "CE", "CY")) {
            if (contains(var1, var3, 3, "CIO", "CIE", "CIA")) {
               var2.append('S', 'X');
            } else {
               var2.append('S');
            }

            var3 += 2;
         } else {
            var2.append('K');
            if (contains(var1, var3 + 1, 2, " C", " Q", " G")) {
               var3 += 3;
            } else if (contains(var1, var3 + 1, 1, "C", "K", "Q") && !contains(var1, var3 + 1, 2, "CE", "CI")) {
               var3 += 2;
            } else {
               ++var3;
            }
         }
      }

      return var3;
   }

   private int handleCC(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (contains(var1, var3 + 2, 1, "I", "E", "H") && !contains(var1, var3 + 2, 2, "HU")) {
         if ((var3 != 1 || this.charAt(var1, var3 - 1) != 'A') && !contains(var1, var3 - 1, 5, "UCCEE", "UCCES")) {
            var2.append('X');
         } else {
            var2.append("KS");
         }

         var3 += 3;
      } else {
         var2.append('K');
         var3 += 2;
      }

      return var3;
   }

   private int handleCH(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (var3 > 0 && contains(var1, var3, 4, "CHAE")) {
         var2.append('K', 'X');
         return var3 + 2;
      } else if (this.conditionCH0(var1, var3)) {
         var2.append('K');
         return var3 + 2;
      } else if (this.conditionCH1(var1, var3)) {
         var2.append('K');
         return var3 + 2;
      } else {
         if (var3 > 0) {
            if (contains(var1, 0, 2, "MC")) {
               var2.append('K');
            } else {
               var2.append('X', 'K');
            }
         } else {
            var2.append('X');
         }

         return var3 + 2;
      }
   }

   private int handleD(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (contains(var1, var3, 2, "DG")) {
         if (contains(var1, var3 + 2, 1, "I", "E", "Y")) {
            var2.append('J');
            var3 += 3;
         } else {
            var2.append("TK");
            var3 += 2;
         }
      } else if (contains(var1, var3, 2, "DT", "DD")) {
         var2.append('T');
         var3 += 2;
      } else {
         var2.append('T');
         ++var3;
      }

      return var3;
   }

   private int handleG(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3, boolean var4) {
      if (this.charAt(var1, var3 + 1) == 'H') {
         var3 = this.handleGH(var1, var2, var3);
      } else if (this.charAt(var1, var3 + 1) == 'N') {
         if (var3 == 1 && this.isVowel(this.charAt(var1, 0)) && !var4) {
            var2.append("KN", "N");
         } else if (!contains(var1, var3 + 2, 2, "EY") && this.charAt(var1, var3 + 1) != 'Y' && !var4) {
            var2.append("N", "KN");
         } else {
            var2.append("KN");
         }

         var3 += 2;
      } else if (contains(var1, var3 + 1, 2, "LI") && !var4) {
         var2.append("KL", "L");
         var3 += 2;
      } else if (var3 == 0 && (this.charAt(var1, var3 + 1) == 'Y' || contains(var1, var3 + 1, 2, ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER))) {
         var2.append('K', 'J');
         var3 += 2;
      } else if ((contains(var1, var3 + 1, 2, "ER") || this.charAt(var1, var3 + 1) == 'Y') && !contains(var1, 0, 6, "DANGER", "RANGER", "MANGER") && !contains(var1, var3 - 1, 1, "E", "I") && !contains(var1, var3 - 1, 3, "RGY", "OGY")) {
         var2.append('K', 'J');
         var3 += 2;
      } else if (!contains(var1, var3 + 1, 1, "E", "I", "Y") && !contains(var1, var3 - 1, 4, "AGGI", "OGGI")) {
         if (this.charAt(var1, var3 + 1) == 'G') {
            var3 += 2;
            var2.append('K');
         } else {
            ++var3;
            var2.append('K');
         }
      } else {
         if (!contains(var1, 0, 4, "VAN ", "VON ") && !contains(var1, 0, 3, "SCH") && !contains(var1, var3 + 1, 2, "ET")) {
            if (contains(var1, var3 + 1, 3, "IER")) {
               var2.append('J');
            } else {
               var2.append('J', 'K');
            }
         } else {
            var2.append('K');
         }

         var3 += 2;
      }

      return var3;
   }

   private int handleGH(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (var3 > 0 && !this.isVowel(this.charAt(var1, var3 - 1))) {
         var2.append('K');
         var3 += 2;
      } else if (var3 == 0) {
         if (this.charAt(var1, var3 + 2) == 'I') {
            var2.append('J');
         } else {
            var2.append('K');
         }

         var3 += 2;
      } else if (var3 > 1 && contains(var1, var3 - 2, 1, "B", "H", "D") || var3 > 2 && contains(var1, var3 - 3, 1, "B", "H", "D") || var3 > 3 && contains(var1, var3 - 4, 1, "B", "H")) {
         var3 += 2;
      } else {
         if (var3 > 2 && this.charAt(var1, var3 - 1) == 'U' && contains(var1, var3 - 3, 1, "C", "G", "L", "R", "T")) {
            var2.append('F');
         } else if (var3 > 0 && this.charAt(var1, var3 - 1) != 'I') {
            var2.append('K');
         }

         var3 += 2;
      }

      return var3;
   }

   private int handleH(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if ((var3 == 0 || this.isVowel(this.charAt(var1, var3 - 1))) && this.isVowel(this.charAt(var1, var3 + 1))) {
         var2.append('H');
         var3 += 2;
      } else {
         ++var3;
      }

      return var3;
   }

   private int handleJ(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3, boolean var4) {
      if (!contains(var1, var3, 4, "JOSE") && !contains(var1, 0, 4, "SAN ")) {
         if (var3 == 0 && !contains(var1, var3, 4, "JOSE")) {
            var2.append('J', 'A');
         } else if (this.isVowel(this.charAt(var1, var3 - 1)) && !var4 && (this.charAt(var1, var3 + 1) == 'A' || this.charAt(var1, var3 + 1) == 'O')) {
            var2.append('J', 'H');
         } else if (var3 == var1.length() - 1) {
            var2.append('J', ' ');
         } else if (!contains(var1, var3 + 1, 1, L_T_K_S_N_M_B_Z) && !contains(var1, var3 - 1, 1, "S", "K", "L")) {
            var2.append('J');
         }

         if (this.charAt(var1, var3 + 1) == 'J') {
            var3 += 2;
         } else {
            ++var3;
         }
      } else {
         if ((var3 != 0 || this.charAt(var1, var3 + 4) != ' ') && var1.length() != 4 && !contains(var1, 0, 4, "SAN ")) {
            var2.append('J', 'H');
         } else {
            var2.append('H');
         }

         ++var3;
      }

      return var3;
   }

   private int handleL(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (this.charAt(var1, var3 + 1) == 'L') {
         if (this.conditionL0(var1, var3)) {
            var2.appendPrimary('L');
         } else {
            var2.append('L');
         }

         var3 += 2;
      } else {
         ++var3;
         var2.append('L');
      }

      return var3;
   }

   private int handleP(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (this.charAt(var1, var3 + 1) == 'H') {
         var2.append('F');
         var3 += 2;
      } else {
         var2.append('P');
         var3 = contains(var1, var3 + 1, 1, "P", "B") ? var3 + 2 : var3 + 1;
      }

      return var3;
   }

   private int handleR(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3, boolean var4) {
      if (var3 == var1.length() - 1 && !var4 && contains(var1, var3 - 2, 2, "IE") && !contains(var1, var3 - 4, 2, "ME", "MA")) {
         var2.appendAlternate('R');
      } else {
         var2.append('R');
      }

      return this.charAt(var1, var3 + 1) == 'R' ? var3 + 2 : var3 + 1;
   }

   private int handleS(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3, boolean var4) {
      if (contains(var1, var3 - 1, 3, "ISL", "YSL")) {
         ++var3;
      } else if (var3 == 0 && contains(var1, var3, 5, "SUGAR")) {
         var2.append('X', 'S');
         ++var3;
      } else if (contains(var1, var3, 2, "SH")) {
         if (contains(var1, var3 + 1, 4, "HEIM", "HOEK", "HOLM", "HOLZ")) {
            var2.append('S');
         } else {
            var2.append('X');
         }

         var3 += 2;
      } else if (!contains(var1, var3, 3, "SIO", "SIA") && !contains(var1, var3, 4, "SIAN")) {
         if ((var3 != 0 || !contains(var1, var3 + 1, 1, "M", "N", "L", "W")) && !contains(var1, var3 + 1, 1, "Z")) {
            if (contains(var1, var3, 2, "SC")) {
               var3 = this.handleSC(var1, var2, var3);
            } else {
               if (var3 == var1.length() - 1 && contains(var1, var3 - 2, 2, "AI", "OI")) {
                  var2.appendAlternate('S');
               } else {
                  var2.append('S');
               }

               var3 = contains(var1, var3 + 1, 1, "S", "Z") ? var3 + 2 : var3 + 1;
            }
         } else {
            var2.append('S', 'X');
            var3 = contains(var1, var3 + 1, 1, "Z") ? var3 + 2 : var3 + 1;
         }
      } else {
         if (var4) {
            var2.append('S');
         } else {
            var2.append('S', 'X');
         }

         var3 += 3;
      }

      return var3;
   }

   private int handleSC(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (this.charAt(var1, var3 + 2) == 'H') {
         if (contains(var1, var3 + 3, 2, "OO", "ER", "EN", "UY", "ED", "EM")) {
            if (contains(var1, var3 + 3, 2, "ER", "EN")) {
               var2.append("X", "SK");
            } else {
               var2.append("SK");
            }
         } else if (var3 == 0 && !this.isVowel(this.charAt(var1, 3)) && this.charAt(var1, 3) != 'W') {
            var2.append('X', 'S');
         } else {
            var2.append('X');
         }
      } else if (contains(var1, var3 + 2, 1, "I", "E", "Y")) {
         var2.append('S');
      } else {
         var2.append("SK");
      }

      return var3 + 3;
   }

   private int handleT(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (contains(var1, var3, 4, "TION")) {
         var2.append('X');
         var3 += 3;
      } else if (contains(var1, var3, 3, "TIA", "TCH")) {
         var2.append('X');
         var3 += 3;
      } else if (!contains(var1, var3, 2, "TH") && !contains(var1, var3, 3, "TTH")) {
         var2.append('T');
         var3 = contains(var1, var3 + 1, 1, "T", "D") ? var3 + 2 : var3 + 1;
      } else {
         if (!contains(var1, var3 + 2, 2, "OM", "AM") && !contains(var1, 0, 4, "VAN ", "VON ") && !contains(var1, 0, 3, "SCH")) {
            var2.append('0', 'T');
         } else {
            var2.append('T');
         }

         var3 += 2;
      }

      return var3;
   }

   private int handleW(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (contains(var1, var3, 2, "WR")) {
         var2.append('R');
         var3 += 2;
      } else if (var3 == 0 && (this.isVowel(this.charAt(var1, var3 + 1)) || contains(var1, var3, 2, "WH"))) {
         if (this.isVowel(this.charAt(var1, var3 + 1))) {
            var2.append('A', 'F');
         } else {
            var2.append('A');
         }

         ++var3;
      } else if ((var3 != var1.length() - 1 || !this.isVowel(this.charAt(var1, var3 - 1))) && !contains(var1, var3 - 1, 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY") && !contains(var1, 0, 3, "SCH")) {
         if (contains(var1, var3, 4, "WICZ", "WITZ")) {
            var2.append("TS", "FX");
            var3 += 4;
         } else {
            ++var3;
         }
      } else {
         var2.appendAlternate('F');
         ++var3;
      }

      return var3;
   }

   private int handleX(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3) {
      if (var3 == 0) {
         var2.append('S');
         ++var3;
      } else {
         if (var3 != var1.length() - 1 || !contains(var1, var3 - 3, 3, "IAU", "EAU") && !contains(var1, var3 - 2, 2, "AU", "OU")) {
            var2.append("KS");
         }

         var3 = contains(var1, var3 + 1, 1, "C", "X") ? var3 + 2 : var3 + 1;
      }

      return var3;
   }

   private int handleZ(String var1, DoubleMetaphone.DoubleMetaphoneResult var2, int var3, boolean var4) {
      if (this.charAt(var1, var3 + 1) == 'H') {
         var2.append('J');
         var3 += 2;
      } else {
         if (!contains(var1, var3 + 1, 2, "ZO", "ZI", "ZA") && (!var4 || var3 <= 0 || this.charAt(var1, var3 - 1) == 'T')) {
            var2.append('S');
         } else {
            var2.append("S", "TS");
         }

         var3 = this.charAt(var1, var3 + 1) == 'Z' ? var3 + 2 : var3 + 1;
      }

      return var3;
   }

   private boolean conditionC0(String var1, int var2) {
      if (contains(var1, var2, 4, "CHIA")) {
         return true;
      } else if (var2 <= 1) {
         return false;
      } else if (this.isVowel(this.charAt(var1, var2 - 2))) {
         return false;
      } else if (!contains(var1, var2 - 1, 3, "ACH")) {
         return false;
      } else {
         char var3 = this.charAt(var1, var2 + 2);
         return var3 != 'I' && var3 != 'E' || contains(var1, var2 - 2, 6, "BACHER", "MACHER");
      }
   }

   private boolean conditionCH0(String var1, int var2) {
      if (var2 != 0) {
         return false;
      } else if (!contains(var1, var2 + 1, 5, "HARAC", "HARIS") && !contains(var1, var2 + 1, 3, "HOR", "HYM", "HIA", "HEM")) {
         return false;
      } else {
         return !contains(var1, 0, 5, "CHORE");
      }
   }

   private boolean conditionCH1(String var1, int var2) {
      return contains(var1, 0, 4, "VAN ", "VON ") || contains(var1, 0, 3, "SCH") || contains(var1, var2 - 2, 6, "ORCHES", "ARCHIT", "ORCHID") || contains(var1, var2 + 2, 1, "T", "S") || (contains(var1, var2 - 1, 1, "A", "O", "U", "E") || var2 == 0) && (contains(var1, var2 + 2, 1, L_R_N_M_B_H_F_V_W_SPACE) || var2 + 1 == var1.length() - 1);
   }

   private boolean conditionL0(String var1, int var2) {
      if (var2 == var1.length() - 3 && contains(var1, var2 - 1, 4, "ILLO", "ILLA", "ALLE")) {
         return true;
      } else {
         return (contains(var1, var1.length() - 2, 2, "AS", "OS") || contains(var1, var1.length() - 1, 1, "A", "O")) && contains(var1, var2 - 1, 4, "ALLE");
      }
   }

   private boolean conditionM0(String var1, int var2) {
      if (this.charAt(var1, var2 + 1) == 'M') {
         return true;
      } else {
         return contains(var1, var2 - 1, 3, "UMB") && (var2 + 1 == var1.length() - 1 || contains(var1, var2 + 2, 2, "ER"));
      }
   }

   private boolean isSlavoGermanic(String var1) {
      return var1.indexOf(87) > -1 || var1.indexOf(75) > -1 || var1.indexOf("CZ") > -1 || var1.indexOf("WITZ") > -1;
   }

   private boolean isVowel(char var1) {
      return "AEIOUY".indexOf(var1) != -1;
   }

   private boolean isSilentStart(String var1) {
      boolean var2 = false;
      String[] var3 = SILENT_START;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var1.startsWith(var6)) {
            var2 = true;
            break;
         }
      }

      return var2;
   }

   private String cleanInput(String var1) {
      if (var1 == null) {
         return null;
      } else {
         var1 = var1.trim();
         return var1.length() == 0 ? null : var1.toUpperCase(Locale.ENGLISH);
      }
   }

   protected char charAt(String var1, int var2) {
      return var2 >= 0 && var2 < var1.length() ? var1.charAt(var2) : '\u0000';
   }

   protected static boolean contains(String var0, int var1, int var2, String... var3) {
      boolean var4 = false;
      if (var1 >= 0 && var1 + var2 <= var0.length()) {
         String var5 = var0.substring(var1, var1 + var2);
         String[] var6 = var3;
         int var7 = var3.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            if (var5.equals(var9)) {
               var4 = true;
               break;
            }
         }
      }

      return var4;
   }

   public class DoubleMetaphoneResult {
      private final StringBuilder primary = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
      private final StringBuilder alternate = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
      private final int maxLength;

      public DoubleMetaphoneResult(int var2) {
         super();
         this.maxLength = var2;
      }

      public void append(char var1) {
         this.appendPrimary(var1);
         this.appendAlternate(var1);
      }

      public void append(char var1, char var2) {
         this.appendPrimary(var1);
         this.appendAlternate(var2);
      }

      public void appendPrimary(char var1) {
         if (this.primary.length() < this.maxLength) {
            this.primary.append(var1);
         }

      }

      public void appendAlternate(char var1) {
         if (this.alternate.length() < this.maxLength) {
            this.alternate.append(var1);
         }

      }

      public void append(String var1) {
         this.appendPrimary(var1);
         this.appendAlternate(var1);
      }

      public void append(String var1, String var2) {
         this.appendPrimary(var1);
         this.appendAlternate(var2);
      }

      public void appendPrimary(String var1) {
         int var2 = this.maxLength - this.primary.length();
         if (var1.length() <= var2) {
            this.primary.append(var1);
         } else {
            this.primary.append(var1.substring(0, var2));
         }

      }

      public void appendAlternate(String var1) {
         int var2 = this.maxLength - this.alternate.length();
         if (var1.length() <= var2) {
            this.alternate.append(var1);
         } else {
            this.alternate.append(var1.substring(0, var2));
         }

      }

      public String getPrimary() {
         return this.primary.toString();
      }

      public String getAlternate() {
         return this.alternate.toString();
      }

      public boolean isComplete() {
         return this.primary.length() >= this.maxLength && this.alternate.length() >= this.maxLength;
      }
   }
}
