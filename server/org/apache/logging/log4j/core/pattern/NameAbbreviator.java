package org.apache.logging.log4j.core.pattern;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public abstract class NameAbbreviator {
   private static final NameAbbreviator DEFAULT = new NameAbbreviator.NOPAbbreviator();

   public NameAbbreviator() {
      super();
   }

   public static NameAbbreviator getAbbreviator(String var0) {
      if (var0.length() > 0) {
         String var1 = var0.trim();
         if (var1.isEmpty()) {
            return DEFAULT;
         } else {
            boolean var2;
            String var3;
            if (var1.length() > 1 && var1.charAt(0) == '-') {
               var2 = true;
               var3 = var1.substring(1);
            } else {
               var2 = false;
               var3 = var1;
            }

            int var4;
            for(var4 = 0; var4 < var3.length() && var3.charAt(var4) >= '0' && var3.charAt(var4) <= '9'; ++var4) {
            }

            if (var4 == var3.length()) {
               return new NameAbbreviator.MaxElementAbbreviator(Integer.parseInt(var3), var2 ? NameAbbreviator.MaxElementAbbreviator.Strategy.DROP : NameAbbreviator.MaxElementAbbreviator.Strategy.RETAIN);
            } else {
               ArrayList var5 = new ArrayList(5);

               for(int var8 = 0; var8 < var1.length() && var8 >= 0; ++var8) {
                  int var9 = var8;
                  int var7;
                  if (var1.charAt(var8) == '*') {
                     var7 = 2147483647;
                     var9 = var8 + 1;
                  } else if (var1.charAt(var8) >= '0' && var1.charAt(var8) <= '9') {
                     var7 = var1.charAt(var8) - 48;
                     var9 = var8 + 1;
                  } else {
                     var7 = 0;
                  }

                  char var6 = 0;
                  if (var9 < var1.length()) {
                     var6 = var1.charAt(var9);
                     if (var6 == '.') {
                        var6 = 0;
                     }
                  }

                  var5.add(new NameAbbreviator.PatternAbbreviatorFragment(var7, var6));
                  var8 = var1.indexOf(46, var8);
                  if (var8 == -1) {
                     break;
                  }
               }

               return new NameAbbreviator.PatternAbbreviator(var5);
            }
         }
      } else {
         return DEFAULT;
      }
   }

   public static NameAbbreviator getDefaultAbbreviator() {
      return DEFAULT;
   }

   public abstract void abbreviate(String var1, StringBuilder var2);

   private static class PatternAbbreviator extends NameAbbreviator {
      private final NameAbbreviator.PatternAbbreviatorFragment[] fragments;

      public PatternAbbreviator(List<NameAbbreviator.PatternAbbreviatorFragment> var1) {
         super();
         if (var1.isEmpty()) {
            throw new IllegalArgumentException("fragments must have at least one element");
         } else {
            this.fragments = new NameAbbreviator.PatternAbbreviatorFragment[var1.size()];
            var1.toArray(this.fragments);
         }
      }

      public void abbreviate(String var1, StringBuilder var2) {
         int var3 = var2.length();
         int var4 = var3 + var1.length();
         StringBuilder var5 = var2.append(var1);

         for(int var6 = 0; var6 < this.fragments.length - 1 && var3 < var1.length(); ++var6) {
            var3 = this.fragments[var6].abbreviate(var5, var3);
         }

         for(NameAbbreviator.PatternAbbreviatorFragment var7 = this.fragments[this.fragments.length - 1]; var3 < var4 && var3 >= 0; var3 = var7.abbreviate(var5, var3)) {
         }

      }
   }

   private static class PatternAbbreviatorFragment {
      private final int charCount;
      private final char ellipsis;

      public PatternAbbreviatorFragment(int var1, char var2) {
         super();
         this.charCount = var1;
         this.ellipsis = var2;
      }

      public int abbreviate(StringBuilder var1, int var2) {
         int var3 = var2 < 0 ? 0 : var2;
         int var4 = var1.length();
         int var5 = -1;

         for(int var6 = var3; var6 < var4; ++var6) {
            if (var1.charAt(var6) == '.') {
               var5 = var6;
               break;
            }
         }

         if (var5 != -1) {
            if (var5 - var2 > this.charCount) {
               var1.delete(var2 + this.charCount, var5);
               var5 = var2 + this.charCount;
               if (this.ellipsis != 0) {
                  var1.insert(var5, this.ellipsis);
                  ++var5;
               }
            }

            ++var5;
         }

         return var5;
      }
   }

   private static class MaxElementAbbreviator extends NameAbbreviator {
      private final int count;
      private final NameAbbreviator.MaxElementAbbreviator.Strategy strategy;

      public MaxElementAbbreviator(int var1, NameAbbreviator.MaxElementAbbreviator.Strategy var2) {
         super();
         this.count = Math.max(var1, var2.minCount);
         this.strategy = var2;
      }

      public void abbreviate(String var1, StringBuilder var2) {
         this.strategy.abbreviate(this.count, var1, var2);
      }

      private static enum Strategy {
         DROP(0) {
            void abbreviate(int var1, String var2, StringBuilder var3) {
               int var4 = 0;

               for(int var6 = 0; var6 < var1; ++var6) {
                  int var5 = var2.indexOf(46, var4);
                  if (var5 == -1) {
                     var3.append(var2);
                     return;
                  }

                  var4 = var5 + 1;
               }

               var3.append(var2, var4, var2.length());
            }
         },
         RETAIN(1) {
            void abbreviate(int var1, String var2, StringBuilder var3) {
               int var4 = var2.length() - 1;

               for(int var5 = var1; var5 > 0; --var5) {
                  var4 = var2.lastIndexOf(46, var4 - 1);
                  if (var4 == -1) {
                     var3.append(var2);
                     return;
                  }
               }

               var3.append(var2, var4 + 1, var2.length());
            }
         };

         final int minCount;

         private Strategy(int var3) {
            this.minCount = var3;
         }

         abstract void abbreviate(int var1, String var2, StringBuilder var3);

         // $FF: synthetic method
         Strategy(int var3, Object var4) {
            this(var3);
         }
      }
   }

   private static class NOPAbbreviator extends NameAbbreviator {
      public NOPAbbreviator() {
         super();
      }

      public void abbreviate(String var1, StringBuilder var2) {
         var2.append(var1);
      }
   }
}
