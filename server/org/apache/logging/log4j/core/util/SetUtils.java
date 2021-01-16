package org.apache.logging.log4j.core.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class SetUtils {
   private SetUtils() {
      super();
   }

   public static String[] prefixSet(Set<String> var0, String var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         if (var4.startsWith(var1)) {
            var2.add(var4);
         }
      }

      return (String[])var2.toArray(new String[var2.size()]);
   }
}
