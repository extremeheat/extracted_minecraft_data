package io.netty.handler.ssl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class SupportedCipherSuiteFilter implements CipherSuiteFilter {
   public static final SupportedCipherSuiteFilter INSTANCE = new SupportedCipherSuiteFilter();

   private SupportedCipherSuiteFilter() {
      super();
   }

   public String[] filterCipherSuites(Iterable<String> var1, List<String> var2, Set<String> var3) {
      if (var2 == null) {
         throw new NullPointerException("defaultCiphers");
      } else if (var3 == null) {
         throw new NullPointerException("supportedCiphers");
      } else {
         ArrayList var4;
         if (var1 == null) {
            var4 = new ArrayList(var2.size());
            var1 = var2;
         } else {
            var4 = new ArrayList(var3.size());
         }

         Iterator var5 = ((Iterable)var1).iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            if (var6 == null) {
               break;
            }

            if (var3.contains(var6)) {
               var4.add(var6);
            }
         }

         return (String[])var4.toArray(new String[var4.size()]);
      }
   }
}
