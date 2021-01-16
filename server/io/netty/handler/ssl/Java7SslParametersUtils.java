package io.netty.handler.ssl;

import java.security.AlgorithmConstraints;
import javax.net.ssl.SSLParameters;

final class Java7SslParametersUtils {
   private Java7SslParametersUtils() {
      super();
   }

   static void setAlgorithmConstraints(SSLParameters var0, Object var1) {
      var0.setAlgorithmConstraints((AlgorithmConstraints)var1);
   }
}
