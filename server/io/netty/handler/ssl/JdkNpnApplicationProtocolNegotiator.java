package io.netty.handler.ssl;

import javax.net.ssl.SSLEngine;

/** @deprecated */
@Deprecated
public final class JdkNpnApplicationProtocolNegotiator extends JdkBaseApplicationProtocolNegotiator {
   private static final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory NPN_WRAPPER = new JdkApplicationProtocolNegotiator.SslEngineWrapperFactory() {
      {
         if (!JettyNpnSslEngine.isAvailable()) {
            throw new RuntimeException("NPN unsupported. Is your classpath configured correctly? See https://wiki.eclipse.org/Jetty/Feature/NPN");
         }
      }

      public SSLEngine wrapSslEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2, boolean var3) {
         return new JettyNpnSslEngine(var1, var2, var3);
      }
   };

   public JdkNpnApplicationProtocolNegotiator(Iterable<String> var1) {
      this(false, var1);
   }

   public JdkNpnApplicationProtocolNegotiator(String... var1) {
      this(false, var1);
   }

   public JdkNpnApplicationProtocolNegotiator(boolean var1, Iterable<String> var2) {
      this(var1, var1, var2);
   }

   public JdkNpnApplicationProtocolNegotiator(boolean var1, String... var2) {
      this(var1, var1, var2);
   }

   public JdkNpnApplicationProtocolNegotiator(boolean var1, boolean var2, Iterable<String> var3) {
      this(var1 ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY, var2 ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY, var3);
   }

   public JdkNpnApplicationProtocolNegotiator(boolean var1, boolean var2, String... var3) {
      this(var1 ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY, var2 ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY, var3);
   }

   public JdkNpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var1, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var2, Iterable<String> var3) {
      super(NPN_WRAPPER, var1, var2, var3);
   }

   public JdkNpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var1, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var2, String... var3) {
      super(NPN_WRAPPER, var1, var2, var3);
   }
}
