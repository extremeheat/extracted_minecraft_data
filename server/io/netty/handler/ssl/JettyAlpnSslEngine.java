package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.LinkedHashSet;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.eclipse.jetty.alpn.ALPN;
import org.eclipse.jetty.alpn.ALPN.ClientProvider;
import org.eclipse.jetty.alpn.ALPN.ServerProvider;

abstract class JettyAlpnSslEngine extends JdkSslEngine {
   private static final boolean available = initAvailable();

   static boolean isAvailable() {
      return available;
   }

   private static boolean initAvailable() {
      if (PlatformDependent.javaVersion() <= 8) {
         try {
            Class.forName("sun.security.ssl.ALPNExtension", true, (ClassLoader)null);
            return true;
         } catch (Throwable var1) {
         }
      }

      return false;
   }

   static JettyAlpnSslEngine newClientEngine(SSLEngine var0, JdkApplicationProtocolNegotiator var1) {
      return new JettyAlpnSslEngine.ClientEngine(var0, var1);
   }

   static JettyAlpnSslEngine newServerEngine(SSLEngine var0, JdkApplicationProtocolNegotiator var1) {
      return new JettyAlpnSslEngine.ServerEngine(var0, var1);
   }

   private JettyAlpnSslEngine(SSLEngine var1) {
      super(var1);
   }

   // $FF: synthetic method
   JettyAlpnSslEngine(SSLEngine var1, Object var2) {
      this(var1);
   }

   private static final class ServerEngine extends JettyAlpnSslEngine {
      ServerEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2) {
         super(var1, null);
         ObjectUtil.checkNotNull(var2, "applicationNegotiator");
         final JdkApplicationProtocolNegotiator.ProtocolSelector var3 = (JdkApplicationProtocolNegotiator.ProtocolSelector)ObjectUtil.checkNotNull(var2.protocolSelectorFactory().newSelector(this, new LinkedHashSet(var2.protocols())), "protocolSelector");
         ALPN.put(var1, new ServerProvider() {
            public String select(List<String> var1) throws SSLException {
               try {
                  return var3.select(var1);
               } catch (Throwable var3x) {
                  throw SslUtils.toSSLHandshakeException(var3x);
               }
            }

            public void unsupported() {
               var3.unsupported();
            }
         });
      }

      public void closeInbound() throws SSLException {
         try {
            ALPN.remove(this.getWrappedEngine());
         } finally {
            super.closeInbound();
         }

      }

      public void closeOutbound() {
         try {
            ALPN.remove(this.getWrappedEngine());
         } finally {
            super.closeOutbound();
         }

      }
   }

   private static final class ClientEngine extends JettyAlpnSslEngine {
      ClientEngine(SSLEngine var1, final JdkApplicationProtocolNegotiator var2) {
         super(var1, null);
         ObjectUtil.checkNotNull(var2, "applicationNegotiator");
         final JdkApplicationProtocolNegotiator.ProtocolSelectionListener var3 = (JdkApplicationProtocolNegotiator.ProtocolSelectionListener)ObjectUtil.checkNotNull(var2.protocolListenerFactory().newListener(this, var2.protocols()), "protocolListener");
         ALPN.put(var1, new ClientProvider() {
            public List<String> protocols() {
               return var2.protocols();
            }

            public void selected(String var1) throws SSLException {
               try {
                  var3.selected(var1);
               } catch (Throwable var3x) {
                  throw SslUtils.toSSLHandshakeException(var3x);
               }
            }

            public void unsupported() {
               var3.unsupported();
            }
         });
      }

      public void closeInbound() throws SSLException {
         try {
            ALPN.remove(this.getWrappedEngine());
         } finally {
            super.closeInbound();
         }

      }

      public void closeOutbound() {
         try {
            ALPN.remove(this.getWrappedEngine());
         } finally {
            super.closeOutbound();
         }

      }
   }
}
