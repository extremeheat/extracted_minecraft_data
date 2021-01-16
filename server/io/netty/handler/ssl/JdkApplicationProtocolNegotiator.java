package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;

/** @deprecated */
@Deprecated
public interface JdkApplicationProtocolNegotiator extends ApplicationProtocolNegotiator {
   JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory();

   JdkApplicationProtocolNegotiator.ProtocolSelectorFactory protocolSelectorFactory();

   JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory protocolListenerFactory();

   public interface ProtocolSelectionListenerFactory {
      JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine var1, List<String> var2);
   }

   public interface ProtocolSelectorFactory {
      JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine var1, Set<String> var2);
   }

   public interface ProtocolSelectionListener {
      void unsupported();

      void selected(String var1) throws Exception;
   }

   public interface ProtocolSelector {
      void unsupported();

      String select(List<String> var1) throws Exception;
   }

   public abstract static class AllocatorAwareSslEngineWrapperFactory implements JdkApplicationProtocolNegotiator.SslEngineWrapperFactory {
      public AllocatorAwareSslEngineWrapperFactory() {
         super();
      }

      public final SSLEngine wrapSslEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2, boolean var3) {
         return this.wrapSslEngine(var1, ByteBufAllocator.DEFAULT, var2, var3);
      }

      abstract SSLEngine wrapSslEngine(SSLEngine var1, ByteBufAllocator var2, JdkApplicationProtocolNegotiator var3, boolean var4);
   }

   public interface SslEngineWrapperFactory {
      SSLEngine wrapSslEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2, boolean var3);
   }
}
