package io.netty.handler.ssl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLParameters;

final class Java8SslUtils {
   private Java8SslUtils() {
      super();
   }

   static List<String> getSniHostNames(SSLParameters var0) {
      List var1 = var0.getServerNames();
      if (var1 != null && !var1.isEmpty()) {
         ArrayList var2 = new ArrayList(var1.size());
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            SNIServerName var4 = (SNIServerName)var3.next();
            if (!(var4 instanceof SNIHostName)) {
               throw new IllegalArgumentException("Only " + SNIHostName.class.getName() + " instances are supported, but found: " + var4);
            }

            var2.add(((SNIHostName)var4).getAsciiName());
         }

         return var2;
      } else {
         return Collections.emptyList();
      }
   }

   static void setSniHostNames(SSLParameters var0, List<String> var1) {
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.add(new SNIHostName(var4));
      }

      var0.setServerNames(var2);
   }

   static boolean getUseCipherSuitesOrder(SSLParameters var0) {
      return var0.getUseCipherSuitesOrder();
   }

   static void setUseCipherSuitesOrder(SSLParameters var0, boolean var1) {
      var0.setUseCipherSuitesOrder(var1);
   }

   static void setSNIMatchers(SSLParameters var0, Collection<?> var1) {
      var0.setSNIMatchers(var1);
   }

   static boolean checkSniHostnameMatch(Collection<?> var0, String var1) {
      if (var0 != null && !var0.isEmpty()) {
         SNIHostName var2 = new SNIHostName(var1);
         Iterator var3 = var0.iterator();

         SNIMatcher var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (SNIMatcher)var3.next();
         } while(var4.getType() != 0 || !var4.matches(var2));

         return true;
      } else {
         return true;
      }
   }
}
