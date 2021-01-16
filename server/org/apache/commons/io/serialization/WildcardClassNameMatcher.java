package org.apache.commons.io.serialization;

import org.apache.commons.io.FilenameUtils;

final class WildcardClassNameMatcher implements ClassNameMatcher {
   private final String pattern;

   public WildcardClassNameMatcher(String var1) {
      super();
      this.pattern = var1;
   }

   public boolean matches(String var1) {
      return FilenameUtils.wildcardMatch(var1, this.pattern);
   }
}
