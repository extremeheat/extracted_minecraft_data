package org.apache.commons.io.serialization;

import java.util.regex.Pattern;

final class RegexpClassNameMatcher implements ClassNameMatcher {
   private final Pattern pattern;

   public RegexpClassNameMatcher(String var1) {
      this(Pattern.compile(var1));
   }

   public RegexpClassNameMatcher(Pattern var1) {
      super();
      if (var1 == null) {
         throw new IllegalArgumentException("Null pattern");
      } else {
         this.pattern = var1;
      }
   }

   public boolean matches(String var1) {
      return this.pattern.matcher(var1).matches();
   }
}
