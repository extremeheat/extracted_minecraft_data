package io.netty.handler.ssl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class IdentityCipherSuiteFilter implements CipherSuiteFilter {
   public static final IdentityCipherSuiteFilter INSTANCE = new IdentityCipherSuiteFilter(true);
   public static final IdentityCipherSuiteFilter INSTANCE_DEFAULTING_TO_SUPPORTED_CIPHERS = new IdentityCipherSuiteFilter(false);
   private final boolean defaultToDefaultCiphers;

   private IdentityCipherSuiteFilter(boolean var1) {
      super();
      this.defaultToDefaultCiphers = var1;
   }

   public String[] filterCipherSuites(Iterable<String> var1, List<String> var2, Set<String> var3) {
      if (var1 == null) {
         return this.defaultToDefaultCiphers ? (String[])var2.toArray(new String[var2.size()]) : (String[])var3.toArray(new String[var3.size()]);
      } else {
         ArrayList var4 = new ArrayList(var3.size());
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            if (var6 == null) {
               break;
            }

            var4.add(var6);
         }

         return (String[])var4.toArray(new String[var4.size()]);
      }
   }
}
