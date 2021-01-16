package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.BitSet;

@GwtCompatible(
   emulated = true
)
public abstract class CharMatcher implements Predicate<Character> {
   /** @deprecated */
   @Deprecated
   public static final CharMatcher WHITESPACE = whitespace();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher BREAKING_WHITESPACE = breakingWhitespace();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher ASCII = ascii();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher DIGIT = digit();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher JAVA_DIGIT = javaDigit();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher JAVA_LETTER = javaLetter();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher JAVA_LETTER_OR_DIGIT = javaLetterOrDigit();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher JAVA_UPPER_CASE = javaUpperCase();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher JAVA_LOWER_CASE = javaLowerCase();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher JAVA_ISO_CONTROL = javaIsoControl();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher INVISIBLE = invisible();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher SINGLE_WIDTH = singleWidth();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher ANY = any();
   /** @deprecated */
   @Deprecated
   public static final CharMatcher NONE = none();
   private static final int DISTINCT_CHARS = 65536;

   public static CharMatcher any() {
      return CharMatcher.Any.INSTANCE;
   }

   public static CharMatcher none() {
      return CharMatcher.None.INSTANCE;
   }

   public static CharMatcher whitespace() {
      return CharMatcher.Whitespace.INSTANCE;
   }

   public static CharMatcher breakingWhitespace() {
      return CharMatcher.BreakingWhitespace.INSTANCE;
   }

   public static CharMatcher ascii() {
      return CharMatcher.Ascii.INSTANCE;
   }

   public static CharMatcher digit() {
      return CharMatcher.Digit.INSTANCE;
   }

   public static CharMatcher javaDigit() {
      return CharMatcher.JavaDigit.INSTANCE;
   }

   public static CharMatcher javaLetter() {
      return CharMatcher.JavaLetter.INSTANCE;
   }

   public static CharMatcher javaLetterOrDigit() {
      return CharMatcher.JavaLetterOrDigit.INSTANCE;
   }

   public static CharMatcher javaUpperCase() {
      return CharMatcher.JavaUpperCase.INSTANCE;
   }

   public static CharMatcher javaLowerCase() {
      return CharMatcher.JavaLowerCase.INSTANCE;
   }

   public static CharMatcher javaIsoControl() {
      return CharMatcher.JavaIsoControl.INSTANCE;
   }

   public static CharMatcher invisible() {
      return CharMatcher.Invisible.INSTANCE;
   }

   public static CharMatcher singleWidth() {
      return CharMatcher.SingleWidth.INSTANCE;
   }

   public static CharMatcher is(char var0) {
      return new CharMatcher.Is(var0);
   }

   public static CharMatcher isNot(char var0) {
      return new CharMatcher.IsNot(var0);
   }

   public static CharMatcher anyOf(CharSequence var0) {
      switch(var0.length()) {
      case 0:
         return none();
      case 1:
         return is(var0.charAt(0));
      case 2:
         return isEither(var0.charAt(0), var0.charAt(1));
      default:
         return new CharMatcher.AnyOf(var0);
      }
   }

   public static CharMatcher noneOf(CharSequence var0) {
      return anyOf(var0).negate();
   }

   public static CharMatcher inRange(char var0, char var1) {
      return new CharMatcher.InRange(var0, var1);
   }

   public static CharMatcher forPredicate(Predicate<? super Character> var0) {
      return (CharMatcher)(var0 instanceof CharMatcher ? (CharMatcher)var0 : new CharMatcher.ForPredicate(var0));
   }

   protected CharMatcher() {
      super();
   }

   public abstract boolean matches(char var1);

   public CharMatcher negate() {
      return new CharMatcher.Negated(this);
   }

   public CharMatcher and(CharMatcher var1) {
      return new CharMatcher.And(this, var1);
   }

   public CharMatcher or(CharMatcher var1) {
      return new CharMatcher.Or(this, var1);
   }

   public CharMatcher precomputed() {
      return Platform.precomputeCharMatcher(this);
   }

   @GwtIncompatible
   CharMatcher precomputedInternal() {
      BitSet var1 = new BitSet();
      this.setBits(var1);
      int var2 = var1.cardinality();
      if (var2 * 2 <= 65536) {
         return precomputedPositive(var2, var1, this.toString());
      } else {
         var1.flip(0, 65536);
         int var3 = 65536 - var2;
         String var4 = ".negate()";
         final String var5 = this.toString();
         String var6 = var5.endsWith(var4) ? var5.substring(0, var5.length() - var4.length()) : var5 + var4;
         return new CharMatcher.NegatedFastMatcher(precomputedPositive(var3, var1, var6)) {
            public String toString() {
               return var5;
            }
         };
      }
   }

   @GwtIncompatible
   private static CharMatcher precomputedPositive(int var0, BitSet var1, String var2) {
      switch(var0) {
      case 0:
         return none();
      case 1:
         return is((char)var1.nextSetBit(0));
      case 2:
         char var3 = (char)var1.nextSetBit(0);
         char var4 = (char)var1.nextSetBit(var3 + 1);
         return isEither(var3, var4);
      default:
         return (CharMatcher)(isSmall(var0, var1.length()) ? SmallCharMatcher.from(var1, var2) : new CharMatcher.BitSetMatcher(var1, var2));
      }
   }

   @GwtIncompatible
   private static boolean isSmall(int var0, int var1) {
      return var0 <= 1023 && var1 > var0 * 4 * 16;
   }

   @GwtIncompatible
   void setBits(BitSet var1) {
      for(int var2 = 65535; var2 >= 0; --var2) {
         if (this.matches((char)var2)) {
            var1.set(var2);
         }
      }

   }

   public boolean matchesAnyOf(CharSequence var1) {
      return !this.matchesNoneOf(var1);
   }

   public boolean matchesAllOf(CharSequence var1) {
      for(int var2 = var1.length() - 1; var2 >= 0; --var2) {
         if (!this.matches(var1.charAt(var2))) {
            return false;
         }
      }

      return true;
   }

   public boolean matchesNoneOf(CharSequence var1) {
      return this.indexIn(var1) == -1;
   }

   public int indexIn(CharSequence var1) {
      return this.indexIn(var1, 0);
   }

   public int indexIn(CharSequence var1, int var2) {
      int var3 = var1.length();
      Preconditions.checkPositionIndex(var2, var3);

      for(int var4 = var2; var4 < var3; ++var4) {
         if (this.matches(var1.charAt(var4))) {
            return var4;
         }
      }

      return -1;
   }

   public int lastIndexIn(CharSequence var1) {
      for(int var2 = var1.length() - 1; var2 >= 0; --var2) {
         if (this.matches(var1.charAt(var2))) {
            return var2;
         }
      }

      return -1;
   }

   public int countIn(CharSequence var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var1.length(); ++var3) {
         if (this.matches(var1.charAt(var3))) {
            ++var2;
         }
      }

      return var2;
   }

   public String removeFrom(CharSequence var1) {
      String var2 = var1.toString();
      int var3 = this.indexIn(var2);
      if (var3 == -1) {
         return var2;
      } else {
         char[] var4 = var2.toCharArray();
         int var5 = 1;

         label25:
         while(true) {
            ++var3;

            while(var3 != var4.length) {
               if (this.matches(var4[var3])) {
                  ++var5;
                  continue label25;
               }

               var4[var3 - var5] = var4[var3];
               ++var3;
            }

            return new String(var4, 0, var3 - var5);
         }
      }
   }

   public String retainFrom(CharSequence var1) {
      return this.negate().removeFrom(var1);
   }

   public String replaceFrom(CharSequence var1, char var2) {
      String var3 = var1.toString();
      int var4 = this.indexIn(var3);
      if (var4 == -1) {
         return var3;
      } else {
         char[] var5 = var3.toCharArray();
         var5[var4] = var2;

         for(int var6 = var4 + 1; var6 < var5.length; ++var6) {
            if (this.matches(var5[var6])) {
               var5[var6] = var2;
            }
         }

         return new String(var5);
      }
   }

   public String replaceFrom(CharSequence var1, CharSequence var2) {
      int var3 = var2.length();
      if (var3 == 0) {
         return this.removeFrom(var1);
      } else if (var3 == 1) {
         return this.replaceFrom(var1, var2.charAt(0));
      } else {
         String var4 = var1.toString();
         int var5 = this.indexIn(var4);
         if (var5 == -1) {
            return var4;
         } else {
            int var6 = var4.length();
            StringBuilder var7 = new StringBuilder(var6 * 3 / 2 + 16);
            int var8 = 0;

            do {
               var7.append(var4, var8, var5);
               var7.append(var2);
               var8 = var5 + 1;
               var5 = this.indexIn(var4, var8);
            } while(var5 != -1);

            var7.append(var4, var8, var6);
            return var7.toString();
         }
      }
   }

   public String trimFrom(CharSequence var1) {
      int var2 = var1.length();

      int var3;
      for(var3 = 0; var3 < var2 && this.matches(var1.charAt(var3)); ++var3) {
      }

      int var4;
      for(var4 = var2 - 1; var4 > var3 && this.matches(var1.charAt(var4)); --var4) {
      }

      return var1.subSequence(var3, var4 + 1).toString();
   }

   public String trimLeadingFrom(CharSequence var1) {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         if (!this.matches(var1.charAt(var3))) {
            return var1.subSequence(var3, var2).toString();
         }
      }

      return "";
   }

   public String trimTrailingFrom(CharSequence var1) {
      int var2 = var1.length();

      for(int var3 = var2 - 1; var3 >= 0; --var3) {
         if (!this.matches(var1.charAt(var3))) {
            return var1.subSequence(0, var3 + 1).toString();
         }
      }

      return "";
   }

   public String collapseFrom(CharSequence var1, char var2) {
      int var3 = var1.length();

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var1.charAt(var4);
         if (this.matches(var5)) {
            if (var5 != var2 || var4 != var3 - 1 && this.matches(var1.charAt(var4 + 1))) {
               StringBuilder var6 = (new StringBuilder(var3)).append(var1, 0, var4).append(var2);
               return this.finishCollapseFrom(var1, var4 + 1, var3, var2, var6, true);
            }

            ++var4;
         }
      }

      return var1.toString();
   }

   public String trimAndCollapseFrom(CharSequence var1, char var2) {
      int var3 = var1.length();
      int var4 = 0;

      int var5;
      for(var5 = var3 - 1; var4 < var3 && this.matches(var1.charAt(var4)); ++var4) {
      }

      while(var5 > var4 && this.matches(var1.charAt(var5))) {
         --var5;
      }

      return var4 == 0 && var5 == var3 - 1 ? this.collapseFrom(var1, var2) : this.finishCollapseFrom(var1, var4, var5 + 1, var2, new StringBuilder(var5 + 1 - var4), false);
   }

   private String finishCollapseFrom(CharSequence var1, int var2, int var3, char var4, StringBuilder var5, boolean var6) {
      for(int var7 = var2; var7 < var3; ++var7) {
         char var8 = var1.charAt(var7);
         if (this.matches(var8)) {
            if (!var6) {
               var5.append(var4);
               var6 = true;
            }
         } else {
            var5.append(var8);
            var6 = false;
         }
      }

      return var5.toString();
   }

   /** @deprecated */
   @Deprecated
   public boolean apply(Character var1) {
      return this.matches(var1);
   }

   public String toString() {
      return super.toString();
   }

   private static String showCharacter(char var0) {
      String var1 = "0123456789ABCDEF";
      char[] var2 = new char[]{'\\', 'u', '\u0000', '\u0000', '\u0000', '\u0000'};

      for(int var3 = 0; var3 < 4; ++var3) {
         var2[5 - var3] = var1.charAt(var0 & 15);
         var0 = (char)(var0 >> 4);
      }

      return String.copyValueOf(var2);
   }

   private static CharMatcher.IsEither isEither(char var0, char var1) {
      return new CharMatcher.IsEither(var0, var1);
   }

   private static final class ForPredicate extends CharMatcher {
      private final Predicate<? super Character> predicate;

      ForPredicate(Predicate<? super Character> var1) {
         super();
         this.predicate = (Predicate)Preconditions.checkNotNull(var1);
      }

      public boolean matches(char var1) {
         return this.predicate.apply(var1);
      }

      public boolean apply(Character var1) {
         return this.predicate.apply(Preconditions.checkNotNull(var1));
      }

      public String toString() {
         return "CharMatcher.forPredicate(" + this.predicate + ")";
      }
   }

   private static final class InRange extends CharMatcher.FastMatcher {
      private final char startInclusive;
      private final char endInclusive;

      InRange(char var1, char var2) {
         super();
         Preconditions.checkArgument(var2 >= var1);
         this.startInclusive = var1;
         this.endInclusive = var2;
      }

      public boolean matches(char var1) {
         return this.startInclusive <= var1 && var1 <= this.endInclusive;
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         var1.set(this.startInclusive, this.endInclusive + 1);
      }

      public String toString() {
         return "CharMatcher.inRange('" + CharMatcher.showCharacter(this.startInclusive) + "', '" + CharMatcher.showCharacter(this.endInclusive) + "')";
      }
   }

   private static final class AnyOf extends CharMatcher {
      private final char[] chars;

      public AnyOf(CharSequence var1) {
         super();
         this.chars = var1.toString().toCharArray();
         Arrays.sort(this.chars);
      }

      public boolean matches(char var1) {
         return Arrays.binarySearch(this.chars, var1) >= 0;
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         char[] var2 = this.chars;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var2[var4];
            var1.set(var5);
         }

      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("CharMatcher.anyOf(\"");
         char[] var2 = this.chars;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var2[var4];
            var1.append(CharMatcher.showCharacter(var5));
         }

         var1.append("\")");
         return var1.toString();
      }
   }

   private static final class IsEither extends CharMatcher.FastMatcher {
      private final char match1;
      private final char match2;

      IsEither(char var1, char var2) {
         super();
         this.match1 = var1;
         this.match2 = var2;
      }

      public boolean matches(char var1) {
         return var1 == this.match1 || var1 == this.match2;
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         var1.set(this.match1);
         var1.set(this.match2);
      }

      public String toString() {
         return "CharMatcher.anyOf(\"" + CharMatcher.showCharacter(this.match1) + CharMatcher.showCharacter(this.match2) + "\")";
      }
   }

   private static final class IsNot extends CharMatcher.FastMatcher {
      private final char match;

      IsNot(char var1) {
         super();
         this.match = var1;
      }

      public boolean matches(char var1) {
         return var1 != this.match;
      }

      public CharMatcher and(CharMatcher var1) {
         return var1.matches(this.match) ? super.and(var1) : var1;
      }

      public CharMatcher or(CharMatcher var1) {
         return (CharMatcher)(var1.matches(this.match) ? any() : this);
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         var1.set(0, this.match);
         var1.set(this.match + 1, 65536);
      }

      public CharMatcher negate() {
         return is(this.match);
      }

      public String toString() {
         return "CharMatcher.isNot('" + CharMatcher.showCharacter(this.match) + "')";
      }
   }

   private static final class Is extends CharMatcher.FastMatcher {
      private final char match;

      Is(char var1) {
         super();
         this.match = var1;
      }

      public boolean matches(char var1) {
         return var1 == this.match;
      }

      public String replaceFrom(CharSequence var1, char var2) {
         return var1.toString().replace(this.match, var2);
      }

      public CharMatcher and(CharMatcher var1) {
         return (CharMatcher)(var1.matches(this.match) ? this : none());
      }

      public CharMatcher or(CharMatcher var1) {
         return var1.matches(this.match) ? var1 : super.or(var1);
      }

      public CharMatcher negate() {
         return isNot(this.match);
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         var1.set(this.match);
      }

      public String toString() {
         return "CharMatcher.is('" + CharMatcher.showCharacter(this.match) + "')";
      }
   }

   private static final class Or extends CharMatcher {
      final CharMatcher first;
      final CharMatcher second;

      Or(CharMatcher var1, CharMatcher var2) {
         super();
         this.first = (CharMatcher)Preconditions.checkNotNull(var1);
         this.second = (CharMatcher)Preconditions.checkNotNull(var2);
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         this.first.setBits(var1);
         this.second.setBits(var1);
      }

      public boolean matches(char var1) {
         return this.first.matches(var1) || this.second.matches(var1);
      }

      public String toString() {
         return "CharMatcher.or(" + this.first + ", " + this.second + ")";
      }
   }

   private static final class And extends CharMatcher {
      final CharMatcher first;
      final CharMatcher second;

      And(CharMatcher var1, CharMatcher var2) {
         super();
         this.first = (CharMatcher)Preconditions.checkNotNull(var1);
         this.second = (CharMatcher)Preconditions.checkNotNull(var2);
      }

      public boolean matches(char var1) {
         return this.first.matches(var1) && this.second.matches(var1);
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         BitSet var2 = new BitSet();
         this.first.setBits(var2);
         BitSet var3 = new BitSet();
         this.second.setBits(var3);
         var2.and(var3);
         var1.or(var2);
      }

      public String toString() {
         return "CharMatcher.and(" + this.first + ", " + this.second + ")";
      }
   }

   private static class Negated extends CharMatcher {
      final CharMatcher original;

      Negated(CharMatcher var1) {
         super();
         this.original = (CharMatcher)Preconditions.checkNotNull(var1);
      }

      public boolean matches(char var1) {
         return !this.original.matches(var1);
      }

      public boolean matchesAllOf(CharSequence var1) {
         return this.original.matchesNoneOf(var1);
      }

      public boolean matchesNoneOf(CharSequence var1) {
         return this.original.matchesAllOf(var1);
      }

      public int countIn(CharSequence var1) {
         return var1.length() - this.original.countIn(var1);
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         BitSet var2 = new BitSet();
         this.original.setBits(var2);
         var2.flip(0, 65536);
         var1.or(var2);
      }

      public CharMatcher negate() {
         return this.original;
      }

      public String toString() {
         return this.original + ".negate()";
      }
   }

   private static final class SingleWidth extends CharMatcher.RangesMatcher {
      static final CharMatcher.SingleWidth INSTANCE = new CharMatcher.SingleWidth();

      private SingleWidth() {
         super("CharMatcher.singleWidth()", "\u0000\u05be\u05d0\u05f3\u0600\u0750\u0e00\u1e00\u2100\ufb50\ufe70\uff61".toCharArray(), "\u04f9\u05be\u05ea\u05f4\u06ff\u077f\u0e7f\u20af\u213a\ufdff\ufeff\uffdc".toCharArray());
      }
   }

   private static final class Invisible extends CharMatcher.RangesMatcher {
      private static final String RANGE_STARTS = "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u2066\u2067\u2068\u2069\u206a\u3000\ud800\ufeff\ufff9\ufffa";
      private static final String RANGE_ENDS = " \u00a0\u00ad\u0604\u061c\u06dd\u070f\u1680\u180e\u200f\u202f\u2064\u2066\u2067\u2068\u2069\u206f\u3000\uf8ff\ufeff\ufff9\ufffb";
      static final CharMatcher.Invisible INSTANCE = new CharMatcher.Invisible();

      private Invisible() {
         super("CharMatcher.invisible()", "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u2066\u2067\u2068\u2069\u206a\u3000\ud800\ufeff\ufff9\ufffa".toCharArray(), " \u00a0\u00ad\u0604\u061c\u06dd\u070f\u1680\u180e\u200f\u202f\u2064\u2066\u2067\u2068\u2069\u206f\u3000\uf8ff\ufeff\ufff9\ufffb".toCharArray());
      }
   }

   private static final class JavaIsoControl extends CharMatcher.NamedFastMatcher {
      static final CharMatcher.JavaIsoControl INSTANCE = new CharMatcher.JavaIsoControl();

      private JavaIsoControl() {
         super("CharMatcher.javaIsoControl()");
      }

      public boolean matches(char var1) {
         return var1 <= 31 || var1 >= 127 && var1 <= 159;
      }
   }

   private static final class JavaLowerCase extends CharMatcher {
      static final CharMatcher.JavaLowerCase INSTANCE = new CharMatcher.JavaLowerCase();

      private JavaLowerCase() {
         super();
      }

      public boolean matches(char var1) {
         return Character.isLowerCase(var1);
      }

      public String toString() {
         return "CharMatcher.javaLowerCase()";
      }
   }

   private static final class JavaUpperCase extends CharMatcher {
      static final CharMatcher.JavaUpperCase INSTANCE = new CharMatcher.JavaUpperCase();

      private JavaUpperCase() {
         super();
      }

      public boolean matches(char var1) {
         return Character.isUpperCase(var1);
      }

      public String toString() {
         return "CharMatcher.javaUpperCase()";
      }
   }

   private static final class JavaLetterOrDigit extends CharMatcher {
      static final CharMatcher.JavaLetterOrDigit INSTANCE = new CharMatcher.JavaLetterOrDigit();

      private JavaLetterOrDigit() {
         super();
      }

      public boolean matches(char var1) {
         return Character.isLetterOrDigit(var1);
      }

      public String toString() {
         return "CharMatcher.javaLetterOrDigit()";
      }
   }

   private static final class JavaLetter extends CharMatcher {
      static final CharMatcher.JavaLetter INSTANCE = new CharMatcher.JavaLetter();

      private JavaLetter() {
         super();
      }

      public boolean matches(char var1) {
         return Character.isLetter(var1);
      }

      public String toString() {
         return "CharMatcher.javaLetter()";
      }
   }

   private static final class JavaDigit extends CharMatcher {
      static final CharMatcher.JavaDigit INSTANCE = new CharMatcher.JavaDigit();

      private JavaDigit() {
         super();
      }

      public boolean matches(char var1) {
         return Character.isDigit(var1);
      }

      public String toString() {
         return "CharMatcher.javaDigit()";
      }
   }

   private static final class Digit extends CharMatcher.RangesMatcher {
      private static final String ZEROES = "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10";
      static final CharMatcher.Digit INSTANCE = new CharMatcher.Digit();

      private static char[] zeroes() {
         return "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".toCharArray();
      }

      private static char[] nines() {
         char[] var0 = new char["0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".length()];

         for(int var1 = 0; var1 < "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".length(); ++var1) {
            var0[var1] = (char)("0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".charAt(var1) + 9);
         }

         return var0;
      }

      private Digit() {
         super("CharMatcher.digit()", zeroes(), nines());
      }
   }

   private static class RangesMatcher extends CharMatcher {
      private final String description;
      private final char[] rangeStarts;
      private final char[] rangeEnds;

      RangesMatcher(String var1, char[] var2, char[] var3) {
         super();
         this.description = var1;
         this.rangeStarts = var2;
         this.rangeEnds = var3;
         Preconditions.checkArgument(var2.length == var3.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            Preconditions.checkArgument(var2[var4] <= var3[var4]);
            if (var4 + 1 < var2.length) {
               Preconditions.checkArgument(var3[var4] < var2[var4 + 1]);
            }
         }

      }

      public boolean matches(char var1) {
         int var2 = Arrays.binarySearch(this.rangeStarts, var1);
         if (var2 >= 0) {
            return true;
         } else {
            var2 = ~var2 - 1;
            return var2 >= 0 && var1 <= this.rangeEnds[var2];
         }
      }

      public String toString() {
         return this.description;
      }
   }

   private static final class Ascii extends CharMatcher.NamedFastMatcher {
      static final CharMatcher.Ascii INSTANCE = new CharMatcher.Ascii();

      Ascii() {
         super("CharMatcher.ascii()");
      }

      public boolean matches(char var1) {
         return var1 <= 127;
      }
   }

   private static final class BreakingWhitespace extends CharMatcher {
      static final CharMatcher INSTANCE = new CharMatcher.BreakingWhitespace();

      private BreakingWhitespace() {
         super();
      }

      public boolean matches(char var1) {
         switch(var1) {
         case '\t':
         case '\n':
         case '\u000b':
         case '\f':
         case '\r':
         case ' ':
         case '\u0085':
         case '\u1680':
         case '\u2028':
         case '\u2029':
         case '\u205f':
         case '\u3000':
            return true;
         case '\u2007':
            return false;
         default:
            return var1 >= 8192 && var1 <= 8202;
         }
      }

      public String toString() {
         return "CharMatcher.breakingWhitespace()";
      }
   }

   @VisibleForTesting
   static final class Whitespace extends CharMatcher.NamedFastMatcher {
      static final String TABLE = "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f\u00a0\f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000";
      static final int MULTIPLIER = 1682554634;
      static final int SHIFT = Integer.numberOfLeadingZeros("\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f\u00a0\f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".length() - 1);
      static final CharMatcher.Whitespace INSTANCE = new CharMatcher.Whitespace();

      Whitespace() {
         super("CharMatcher.whitespace()");
      }

      public boolean matches(char var1) {
         return "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f\u00a0\f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".charAt(1682554634 * var1 >>> SHIFT) == var1;
      }

      @GwtIncompatible
      void setBits(BitSet var1) {
         for(int var2 = 0; var2 < "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f\u00a0\f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".length(); ++var2) {
            var1.set("\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f\u00a0\f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".charAt(var2));
         }

      }
   }

   private static final class None extends CharMatcher.NamedFastMatcher {
      static final CharMatcher.None INSTANCE = new CharMatcher.None();

      private None() {
         super("CharMatcher.none()");
      }

      public boolean matches(char var1) {
         return false;
      }

      public int indexIn(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         return -1;
      }

      public int indexIn(CharSequence var1, int var2) {
         int var3 = var1.length();
         Preconditions.checkPositionIndex(var2, var3);
         return -1;
      }

      public int lastIndexIn(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         return -1;
      }

      public boolean matchesAllOf(CharSequence var1) {
         return var1.length() == 0;
      }

      public boolean matchesNoneOf(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         return true;
      }

      public String removeFrom(CharSequence var1) {
         return var1.toString();
      }

      public String replaceFrom(CharSequence var1, char var2) {
         return var1.toString();
      }

      public String replaceFrom(CharSequence var1, CharSequence var2) {
         Preconditions.checkNotNull(var2);
         return var1.toString();
      }

      public String collapseFrom(CharSequence var1, char var2) {
         return var1.toString();
      }

      public String trimFrom(CharSequence var1) {
         return var1.toString();
      }

      public String trimLeadingFrom(CharSequence var1) {
         return var1.toString();
      }

      public String trimTrailingFrom(CharSequence var1) {
         return var1.toString();
      }

      public int countIn(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         return 0;
      }

      public CharMatcher and(CharMatcher var1) {
         Preconditions.checkNotNull(var1);
         return this;
      }

      public CharMatcher or(CharMatcher var1) {
         return (CharMatcher)Preconditions.checkNotNull(var1);
      }

      public CharMatcher negate() {
         return any();
      }
   }

   private static final class Any extends CharMatcher.NamedFastMatcher {
      static final CharMatcher.Any INSTANCE = new CharMatcher.Any();

      private Any() {
         super("CharMatcher.any()");
      }

      public boolean matches(char var1) {
         return true;
      }

      public int indexIn(CharSequence var1) {
         return var1.length() == 0 ? -1 : 0;
      }

      public int indexIn(CharSequence var1, int var2) {
         int var3 = var1.length();
         Preconditions.checkPositionIndex(var2, var3);
         return var2 == var3 ? -1 : var2;
      }

      public int lastIndexIn(CharSequence var1) {
         return var1.length() - 1;
      }

      public boolean matchesAllOf(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         return true;
      }

      public boolean matchesNoneOf(CharSequence var1) {
         return var1.length() == 0;
      }

      public String removeFrom(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         return "";
      }

      public String replaceFrom(CharSequence var1, char var2) {
         char[] var3 = new char[var1.length()];
         Arrays.fill(var3, var2);
         return new String(var3);
      }

      public String replaceFrom(CharSequence var1, CharSequence var2) {
         StringBuilder var3 = new StringBuilder(var1.length() * var2.length());

         for(int var4 = 0; var4 < var1.length(); ++var4) {
            var3.append(var2);
         }

         return var3.toString();
      }

      public String collapseFrom(CharSequence var1, char var2) {
         return var1.length() == 0 ? "" : String.valueOf(var2);
      }

      public String trimFrom(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         return "";
      }

      public int countIn(CharSequence var1) {
         return var1.length();
      }

      public CharMatcher and(CharMatcher var1) {
         return (CharMatcher)Preconditions.checkNotNull(var1);
      }

      public CharMatcher or(CharMatcher var1) {
         Preconditions.checkNotNull(var1);
         return this;
      }

      public CharMatcher negate() {
         return none();
      }
   }

   @GwtIncompatible
   private static final class BitSetMatcher extends CharMatcher.NamedFastMatcher {
      private final BitSet table;

      private BitSetMatcher(BitSet var1, String var2) {
         super(var2);
         if (var1.length() + 64 < var1.size()) {
            var1 = (BitSet)var1.clone();
         }

         this.table = var1;
      }

      public boolean matches(char var1) {
         return this.table.get(var1);
      }

      void setBits(BitSet var1) {
         var1.or(this.table);
      }

      // $FF: synthetic method
      BitSetMatcher(BitSet var1, String var2, Object var3) {
         this(var1, var2);
      }
   }

   static class NegatedFastMatcher extends CharMatcher.Negated {
      NegatedFastMatcher(CharMatcher var1) {
         super(var1);
      }

      public final CharMatcher precomputed() {
         return this;
      }
   }

   abstract static class NamedFastMatcher extends CharMatcher.FastMatcher {
      private final String description;

      NamedFastMatcher(String var1) {
         super();
         this.description = (String)Preconditions.checkNotNull(var1);
      }

      public final String toString() {
         return this.description;
      }
   }

   abstract static class FastMatcher extends CharMatcher {
      FastMatcher() {
         super();
      }

      public final CharMatcher precomputed() {
         return this;
      }

      public CharMatcher negate() {
         return new CharMatcher.NegatedFastMatcher(this);
      }
   }
}
