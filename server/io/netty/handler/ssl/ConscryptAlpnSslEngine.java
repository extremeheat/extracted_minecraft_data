package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.conscrypt.AllocatedBuffer;
import org.conscrypt.BufferAllocator;
import org.conscrypt.HandshakeListener;

abstract class ConscryptAlpnSslEngine extends JdkSslEngine {
   private static final boolean USE_BUFFER_ALLOCATOR = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.conscrypt.useBufferAllocator", true);

   static ConscryptAlpnSslEngine newClientEngine(SSLEngine var0, ByteBufAllocator var1, JdkApplicationProtocolNegotiator var2) {
      return new ConscryptAlpnSslEngine.ClientEngine(var0, var1, var2);
   }

   static ConscryptAlpnSslEngine newServerEngine(SSLEngine var0, ByteBufAllocator var1, JdkApplicationProtocolNegotiator var2) {
      return new ConscryptAlpnSslEngine.ServerEngine(var0, var1, var2);
   }

   private ConscryptAlpnSslEngine(SSLEngine var1, ByteBufAllocator var2, List<String> var3) {
      super(var1);
      if (USE_BUFFER_ALLOCATOR) {
         org.conscrypt.Conscrypt.setBufferAllocator(var1, new ConscryptAlpnSslEngine.BufferAllocatorAdapter(var2));
      }

      org.conscrypt.Conscrypt.setApplicationProtocols(var1, (String[])var3.toArray(new String[var3.size()]));
   }

   final int calculateOutNetBufSize(int var1, int var2) {
      long var3 = (long)org.conscrypt.Conscrypt.maxSealOverhead(this.getWrappedEngine()) * (long)var2;
      return (int)Math.min(2147483647L, (long)var1 + var3);
   }

   final SSLEngineResult unwrap(ByteBuffer[] var1, ByteBuffer[] var2) throws SSLException {
      return org.conscrypt.Conscrypt.unwrap(this.getWrappedEngine(), var1, var2);
   }

   // $FF: synthetic method
   ConscryptAlpnSslEngine(SSLEngine var1, ByteBufAllocator var2, List var3, Object var4) {
      this(var1, var2, var3);
   }

   private static final class BufferAdapter extends AllocatedBuffer {
      private final ByteBuf nettyBuffer;
      private final ByteBuffer buffer;

      BufferAdapter(ByteBuf var1) {
         super();
         this.nettyBuffer = var1;
         this.buffer = var1.nioBuffer(0, var1.capacity());
      }

      public ByteBuffer nioBuffer() {
         return this.buffer;
      }

      public AllocatedBuffer retain() {
         this.nettyBuffer.retain();
         return this;
      }

      public AllocatedBuffer release() {
         this.nettyBuffer.release();
         return this;
      }
   }

   private static final class BufferAllocatorAdapter extends BufferAllocator {
      private final ByteBufAllocator alloc;

      BufferAllocatorAdapter(ByteBufAllocator var1) {
         super();
         this.alloc = var1;
      }

      public AllocatedBuffer allocateDirectBuffer(int var1) {
         return new ConscryptAlpnSslEngine.BufferAdapter(this.alloc.directBuffer(var1));
      }
   }

   private static final class ServerEngine extends ConscryptAlpnSslEngine {
      private final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector;

      ServerEngine(SSLEngine var1, ByteBufAllocator var2, JdkApplicationProtocolNegotiator var3) {
         super(var1, var2, var3.protocols(), null);
         org.conscrypt.Conscrypt.setHandshakeListener(var1, new HandshakeListener() {
            public void onHandshakeFinished() throws SSLException {
               ServerEngine.this.selectProtocol();
            }
         });
         this.protocolSelector = (JdkApplicationProtocolNegotiator.ProtocolSelector)ObjectUtil.checkNotNull(var3.protocolSelectorFactory().newSelector(this, new LinkedHashSet(var3.protocols())), "protocolSelector");
      }

      private void selectProtocol() throws SSLException {
         try {
            String var1 = org.conscrypt.Conscrypt.getApplicationProtocol(this.getWrappedEngine());
            this.protocolSelector.select(var1 != null ? Collections.singletonList(var1) : Collections.emptyList());
         } catch (Throwable var2) {
            throw SslUtils.toSSLHandshakeException(var2);
         }
      }
   }

   private static final class ClientEngine extends ConscryptAlpnSslEngine {
      private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener;

      ClientEngine(SSLEngine var1, ByteBufAllocator var2, JdkApplicationProtocolNegotiator var3) {
         super(var1, var2, var3.protocols(), null);
         org.conscrypt.Conscrypt.setHandshakeListener(var1, new HandshakeListener() {
            public void onHandshakeFinished() throws SSLException {
               ClientEngine.this.selectProtocol();
            }
         });
         this.protocolListener = (JdkApplicationProtocolNegotiator.ProtocolSelectionListener)ObjectUtil.checkNotNull(var3.protocolListenerFactory().newListener(this, var3.protocols()), "protocolListener");
      }

      private void selectProtocol() throws SSLException {
         String var1 = org.conscrypt.Conscrypt.getApplicationProtocol(this.getWrappedEngine());

         try {
            this.protocolListener.selected(var1);
         } catch (Throwable var3) {
            throw SslUtils.toSSLHandshakeException(var3);
         }
      }
   }
}
