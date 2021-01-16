package io.netty.handler.ssl;

import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

final class Java9SslEngine extends JdkSslEngine {
   private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener selectionListener;
   private final Java9SslEngine.AlpnSelector alpnSelector;

   Java9SslEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2, boolean var3) {
      super(var1);
      if (var3) {
         this.selectionListener = null;
         this.alpnSelector = new Java9SslEngine.AlpnSelector(var2.protocolSelectorFactory().newSelector(this, new LinkedHashSet(var2.protocols())));
         Java9SslUtils.setHandshakeApplicationProtocolSelector(var1, this.alpnSelector);
      } else {
         this.selectionListener = var2.protocolListenerFactory().newListener(this, var2.protocols());
         this.alpnSelector = null;
         Java9SslUtils.setApplicationProtocols(var1, var2.protocols());
      }

   }

   private SSLEngineResult verifyProtocolSelection(SSLEngineResult var1) throws SSLException {
      if (var1.getHandshakeStatus() == HandshakeStatus.FINISHED) {
         if (this.alpnSelector == null) {
            try {
               String var2 = this.getApplicationProtocol();

               assert var2 != null;

               if (var2.isEmpty()) {
                  this.selectionListener.unsupported();
               } else {
                  this.selectionListener.selected(var2);
               }
            } catch (Throwable var3) {
               throw SslUtils.toSSLHandshakeException(var3);
            }
         } else {
            assert this.selectionListener == null;

            this.alpnSelector.checkUnsupported();
         }
      }

      return var1;
   }

   public SSLEngineResult wrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.verifyProtocolSelection(super.wrap(var1, var2));
   }

   public SSLEngineResult wrap(ByteBuffer[] var1, ByteBuffer var2) throws SSLException {
      return this.verifyProtocolSelection(super.wrap(var1, var2));
   }

   public SSLEngineResult wrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer var4) throws SSLException {
      return this.verifyProtocolSelection(super.wrap(var1, var2, var3, var4));
   }

   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.verifyProtocolSelection(super.unwrap(var1, var2));
   }

   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2) throws SSLException {
      return this.verifyProtocolSelection(super.unwrap(var1, var2));
   }

   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2, int var3, int var4) throws SSLException {
      return this.verifyProtocolSelection(super.unwrap(var1, var2, var3, var4));
   }

   void setNegotiatedApplicationProtocol(String var1) {
   }

   public String getNegotiatedApplicationProtocol() {
      String var1 = this.getApplicationProtocol();
      if (var1 != null) {
         return var1.isEmpty() ? null : var1;
      } else {
         return var1;
      }
   }

   public String getApplicationProtocol() {
      return Java9SslUtils.getApplicationProtocol(this.getWrappedEngine());
   }

   public String getHandshakeApplicationProtocol() {
      return Java9SslUtils.getHandshakeApplicationProtocol(this.getWrappedEngine());
   }

   public void setHandshakeApplicationProtocolSelector(BiFunction<SSLEngine, List<String>, String> var1) {
      Java9SslUtils.setHandshakeApplicationProtocolSelector(this.getWrappedEngine(), var1);
   }

   public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
      return Java9SslUtils.getHandshakeApplicationProtocolSelector(this.getWrappedEngine());
   }

   private final class AlpnSelector implements BiFunction<SSLEngine, List<String>, String> {
      private final JdkApplicationProtocolNegotiator.ProtocolSelector selector;
      private boolean called;

      AlpnSelector(JdkApplicationProtocolNegotiator.ProtocolSelector var2) {
         super();
         this.selector = var2;
      }

      public String apply(SSLEngine var1, List<String> var2) {
         assert !this.called;

         this.called = true;

         try {
            String var3 = this.selector.select(var2);
            return var3 == null ? "" : var3;
         } catch (Exception var4) {
            return null;
         }
      }

      void checkUnsupported() {
         if (!this.called) {
            String var1 = Java9SslEngine.this.getApplicationProtocol();

            assert var1 != null;

            if (var1.isEmpty()) {
               this.selector.unsupported();
            }

         }
      }
   }
}
