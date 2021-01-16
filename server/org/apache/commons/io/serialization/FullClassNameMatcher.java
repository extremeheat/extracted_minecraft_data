package org.apache.commons.io.serialization;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class FullClassNameMatcher implements ClassNameMatcher {
   private final Set<String> classesSet;

   public FullClassNameMatcher(String... var1) {
      super();
      this.classesSet = Collections.unmodifiableSet(new HashSet(Arrays.asList(var1)));
   }

   public boolean matches(String var1) {
      return this.classesSet.contains(var1);
   }
}
