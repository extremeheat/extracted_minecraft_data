package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.LinkedHashSet;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.eclipse.jetty.npn.NextProtoNego;
import org.eclipse.jetty.npn.NextProtoNego.ClientProvider;
import org.eclipse.jetty.npn.NextProtoNego.ServerProvider;

final class JettyNpnSslEngine extends JdkSslEngine {
   private static boolean available;

   static boolean isAvailable() {
      updateAvailability();
      return available;
   }

   private static void updateAvailability() {
      if (!available) {
         try {
            Class.forName("sun.security.ssl.NextProtoNegoExtension", true, (ClassLoader)null);
            available = true;
         } catch (Exception var1) {
         }

      }
   }

   JettyNpnSslEngine(SSLEngine var1, final JdkApplicationProtocolNegotiator var2, boolean var3) {
      super(var1);
      ObjectUtil.checkNotNull(var2, "applicationNegotiator");
      if (var3) {
         final JdkApplicationProtocolNegotiator.ProtocolSelectionListener var4 = (JdkApplicationProtocolNegotiator.ProtocolSelectionListener)ObjectUtil.checkNotNull(var2.protocolListenerFactory().newListener(this, var2.protocols()), "protocolListener");
         NextProtoNego.put(var1, new ServerProvider() {
            public void unsupported() {
               var4.unsupported();
            }

            public List<String> protocols() {
               return var2.protocols();
            }

            public void protocolSelected(String var1) {
               try {
                  var4.selected(var1);
               } catch (Throwable var3) {
                  PlatformDependent.throwException(var3);
               }

            }
         });
      } else {
         final JdkApplicationProtocolNegotiator.ProtocolSelector var5 = (JdkApplicationProtocolNegotiator.ProtocolSelector)ObjectUtil.checkNotNull(var2.protocolSelectorFactory().newSelector(this, new LinkedHashSet(var2.protocols())), "protocolSelector");
         NextProtoNego.put(var1, new ClientProvider() {
            public boolean supports() {
               return true;
            }

            public void unsupported() {
               var5.unsupported();
            }

            public String selectProtocol(List<String> var1) {
               try {
                  return var5.select(var1);
               } catch (Throwable var3) {
                  PlatformDependent.throwException(var3);
                  return null;
               }
            }
         });
      }

   }

   public void closeInbound() throws SSLException {
      NextProtoNego.remove(this.getWrappedEngine());
      super.closeInbound();
   }

   public void closeOutbound() {
      NextProtoNego.remove(this.getWrappedEngine());
      super.closeOutbound();
   }
}
