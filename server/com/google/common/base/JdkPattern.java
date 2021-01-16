package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@GwtIncompatible
final class JdkPattern extends CommonPattern implements Serializable {
   private final Pattern pattern;
   private static final long serialVersionUID = 0L;

   JdkPattern(Pattern var1) {
      super();
      this.pattern = (Pattern)Preconditions.checkNotNull(var1);
   }

   CommonMatcher matcher(CharSequence var1) {
      return new JdkPattern.JdkMatcher(this.pattern.matcher(var1));
   }

   String pattern() {
      return this.pattern.pattern();
   }

   int flags() {
      return this.pattern.flags();
   }

   public String toString() {
      return this.pattern.toString();
   }

   public int hashCode() {
      return this.pattern.hashCode();
   }

   public boolean equals(Object var1) {
      return !(var1 instanceof JdkPattern) ? false : this.pattern.equals(((JdkPattern)var1).pattern);
   }

   private static final class JdkMatcher extends CommonMatcher {
      final Matcher matcher;

      JdkMatcher(Matcher var1) {
         super();
         this.matcher = (Matcher)Preconditions.checkNotNull(var1);
      }

      boolean matches() {
         return this.matcher.matches();
      }

      boolean find() {
         return this.matcher.find();
      }

      boolean find(int var1) {
         return this.matcher.find(var1);
      }

      String replaceAll(String var1) {
         return this.matcher.replaceAll(var1);
      }

      int end() {
         return this.matcher.end();
      }

      int start() {
         return this.matcher.start();
      }
   }
}
