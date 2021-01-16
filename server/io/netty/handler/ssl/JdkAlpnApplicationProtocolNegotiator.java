package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import javax.net.ssl.SSLEngine;

/** @deprecated */
@Deprecated
public final class JdkAlpnApplicationProtocolNegotiator extends JdkBaseApplicationProtocolNegotiator {
   private static final boolean AVAILABLE = Conscrypt.isAvailable() || jdkAlpnSupported() || JettyAlpnSslEngine.isAvailable();
   private static final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory ALPN_WRAPPER;

   public JdkAlpnApplicationProtocolNegotiator(Iterable<String> var1) {
      this(false, var1);
   }

   public JdkAlpnApplicationProtocolNegotiator(String... var1) {
      this(false, var1);
   }

   public JdkAlpnApplicationProtocolNegotiator(boolean var1, Iterable<String> var2) {
      this(var1, var1, var2);
   }

   public JdkAlpnApplicationProtocolNegotiator(boolean var1, String... var2) {
      this(var1, var1, var2);
   }

   public JdkAlpnApplicationProtocolNegotiator(boolean var1, boolean var2, Iterable<String> var3) {
      this(var2 ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY, var1 ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY, var3);
   }

   public JdkAlpnApplicationProtocolNegotiator(boolean var1, boolean var2, String... var3) {
      this(var2 ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY, var1 ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY, var3);
   }

   public JdkAlpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var1, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var2, Iterable<String> var3) {
      super(ALPN_WRAPPER, var1, var2, var3);
   }

   public JdkAlpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var1, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var2, String... var3) {
      super(ALPN_WRAPPER, var1, var2, var3);
   }

   static boolean jdkAlpnSupported() {
      return PlatformDependent.javaVersion() >= 9 && Java9SslUtils.supportsAlpn();
   }

   static {
      ALPN_WRAPPER = (JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)(AVAILABLE ? new JdkAlpnApplicationProtocolNegotiator.AlpnWrapper() : new JdkAlpnApplicationProtocolNegotiator.FailureWrapper());
   }

   private static final class AlpnWrapper extends JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory {
      private AlpnWrapper() {
         super();
      }

      public SSLEngine wrapSslEngine(SSLEngine var1, ByteBufAllocator var2, JdkApplicationProtocolNegotiator var3, boolean var4) {
         if (Conscrypt.isEngineSupported(var1)) {
            return var4 ? ConscryptAlpnSslEngine.newServerEngine(var1, var2, var3) : ConscryptAlpnSslEngine.newClientEngine(var1, var2, var3);
         } else if (JdkAlpnApplicationProtocolNegotiator.jdkAlpnSupported()) {
            return new Java9SslEngine(var1, var3, var4);
         } else if (JettyAlpnSslEngine.isAvailable()) {
            return var4 ? JettyAlpnSslEngine.newServerEngine(var1, var3) : JettyAlpnSslEngine.newClientEngine(var1, var3);
         } else {
            throw new RuntimeException("Unable to wrap SSLEngine of type " + var1.getClass().getName());
         }
      }

      // $FF: synthetic method
      AlpnWrapper(Object var1) {
         this();
      }
   }

   private static final class FailureWrapper extends JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory {
      private FailureWrapper() {
         super();
      }

      public SSLEngine wrapSslEngine(SSLEngine var1, ByteBufAllocator var2, JdkApplicationProtocolNegotiator var3, boolean var4) {
         throw new RuntimeException("ALPN unsupported. Is your classpath configured correctly? For Conscrypt, add the appropriate Conscrypt JAR to classpath and set the security provider. For Jetty-ALPN, see http://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-starting");
      }

      // $FF: synthetic method
      FailureWrapper(Object var1) {
         this();
      }
   }
}
