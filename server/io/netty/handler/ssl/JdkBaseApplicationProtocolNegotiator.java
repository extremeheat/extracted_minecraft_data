package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;

class JdkBaseApplicationProtocolNegotiator implements JdkApplicationProtocolNegotiator {
   private final List<String> protocols;
   private final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory;
   private final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory;
   private final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory;
   static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory() {
      public JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine var1, Set<String> var2) {
         return new JdkBaseApplicationProtocolNegotiator.FailProtocolSelector((JdkSslEngine)var1, var2);
      }
   };
   static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory NO_FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory() {
      public JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine var1, Set<String> var2) {
         return new JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelector((JdkSslEngine)var1, var2);
      }
   };
   static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory() {
      public JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine var1, List<String> var2) {
         return new JdkBaseApplicationProtocolNegotiator.FailProtocolSelectionListener((JdkSslEngine)var1, var2);
      }
   };
   static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory NO_FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory() {
      public JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine var1, List<String> var2) {
         return new JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelectionListener((JdkSslEngine)var1, var2);
      }
   };

   JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory var1, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var2, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var3, Iterable<String> var4) {
      this(var1, var2, var3, ApplicationProtocolUtil.toList(var4));
   }

   JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory var1, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var2, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var3, String... var4) {
      this(var1, var2, var3, ApplicationProtocolUtil.toList(var4));
   }

   private JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory var1, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var2, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var3, List<String> var4) {
      super();
      this.wrapperFactory = (JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)ObjectUtil.checkNotNull(var1, "wrapperFactory");
      this.selectorFactory = (JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)ObjectUtil.checkNotNull(var2, "selectorFactory");
      this.listenerFactory = (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)ObjectUtil.checkNotNull(var3, "listenerFactory");
      this.protocols = Collections.unmodifiableList((List)ObjectUtil.checkNotNull(var4, "protocols"));
   }

   public List<String> protocols() {
      return this.protocols;
   }

   public JdkApplicationProtocolNegotiator.ProtocolSelectorFactory protocolSelectorFactory() {
      return this.selectorFactory;
   }

   public JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory protocolListenerFactory() {
      return this.listenerFactory;
   }

   public JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory() {
      return this.wrapperFactory;
   }

   private static final class FailProtocolSelectionListener extends JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelectionListener {
      FailProtocolSelectionListener(JdkSslEngine var1, List<String> var2) {
         super(var1, var2);
      }

      protected void noSelectedMatchFound(String var1) throws Exception {
         throw new SSLHandshakeException("No compatible protocols found");
      }
   }

   private static class NoFailProtocolSelectionListener implements JdkApplicationProtocolNegotiator.ProtocolSelectionListener {
      private final JdkSslEngine engineWrapper;
      private final List<String> supportedProtocols;

      NoFailProtocolSelectionListener(JdkSslEngine var1, List<String> var2) {
         super();
         this.engineWrapper = var1;
         this.supportedProtocols = var2;
      }

      public void unsupported() {
         this.engineWrapper.setNegotiatedApplicationProtocol((String)null);
      }

      public void selected(String var1) throws Exception {
         if (this.supportedProtocols.contains(var1)) {
            this.engineWrapper.setNegotiatedApplicationProtocol(var1);
         } else {
            this.noSelectedMatchFound(var1);
         }

      }

      protected void noSelectedMatchFound(String var1) throws Exception {
      }
   }

   private static final class FailProtocolSelector extends JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelector {
      FailProtocolSelector(JdkSslEngine var1, Set<String> var2) {
         super(var1, var2);
      }

      public String noSelectMatchFound() throws Exception {
         throw new SSLHandshakeException("Selected protocol is not supported");
      }
   }

   static class NoFailProtocolSelector implements JdkApplicationProtocolNegotiator.ProtocolSelector {
      private final JdkSslEngine engineWrapper;
      private final Set<String> supportedProtocols;

      NoFailProtocolSelector(JdkSslEngine var1, Set<String> var2) {
         super();
         this.engineWrapper = var1;
         this.supportedProtocols = var2;
      }

      public void unsupported() {
         this.engineWrapper.setNegotiatedApplicationProtocol((String)null);
      }

      public String select(List<String> var1) throws Exception {
         Iterator var2 = this.supportedProtocols.iterator();

         String var3;
         do {
            if (!var2.hasNext()) {
               return this.noSelectMatchFound();
            }

            var3 = (String)var2.next();
         } while(!var1.contains(var3));

         this.engineWrapper.setNegotiatedApplicationProtocol(var3);
         return var3;
      }

      public String noSelectMatchFound() throws Exception {
         this.engineWrapper.setNegotiatedApplicationProtocol((String)null);
         return null;
      }
   }
}
