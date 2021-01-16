package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.SSL;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.Lock;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.security.cert.X509Certificate;

public class ReferenceCountedOpenSslEngine extends SSLEngine implements ReferenceCounted, ApplicationProtocolAccessor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslEngine.class);
   private static final SSLException BEGIN_HANDSHAKE_ENGINE_CLOSED = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("engine closed"), ReferenceCountedOpenSslEngine.class, "beginHandshake()");
   private static final SSLException HANDSHAKE_ENGINE_CLOSED = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("engine closed"), ReferenceCountedOpenSslEngine.class, "handshake()");
   private static final SSLException RENEGOTIATION_UNSUPPORTED = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("renegotiation unsupported"), ReferenceCountedOpenSslEngine.class, "beginHandshake()");
   private static final ResourceLeakDetector<ReferenceCountedOpenSslEngine> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslEngine.class);
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV2 = 0;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV3 = 1;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1 = 2;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_1 = 3;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_2 = 4;
   private static final int[] OPENSSL_OP_NO_PROTOCOLS;
   private static final int DEFAULT_HOSTNAME_VALIDATION_FLAGS = 0;
   static final int MAX_PLAINTEXT_LENGTH;
   private static final int MAX_RECORD_SIZE;
   private static final AtomicIntegerFieldUpdater<ReferenceCountedOpenSslEngine> DESTROYED_UPDATER;
   private static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
   private static final SSLEngineResult NEED_UNWRAP_OK;
   private static final SSLEngineResult NEED_UNWRAP_CLOSED;
   private static final SSLEngineResult NEED_WRAP_OK;
   private static final SSLEngineResult NEED_WRAP_CLOSED;
   private static final SSLEngineResult CLOSED_NOT_HANDSHAKING;
   private long ssl;
   private long networkBIO;
   private boolean certificateSet;
   private ReferenceCountedOpenSslEngine.HandshakeState handshakeState;
   private boolean receivedShutdown;
   private volatile int destroyed;
   private volatile String applicationProtocol;
   private final ResourceLeakTracker<ReferenceCountedOpenSslEngine> leak;
   private final AbstractReferenceCounted refCnt;
   private volatile ClientAuth clientAuth;
   private volatile long lastAccessed;
   private String endPointIdentificationAlgorithm;
   private Object algorithmConstraints;
   private List<String> sniHostNames;
   private volatile Collection<?> matchers;
   private boolean isInboundDone;
   private boolean outboundClosed;
   final boolean jdkCompatibilityMode;
   private final boolean clientMode;
   private final ByteBufAllocator alloc;
   private final OpenSslEngineMap engineMap;
   private final OpenSslApplicationProtocolNegotiator apn;
   private final ReferenceCountedOpenSslEngine.OpenSslSession session;
   private final Certificate[] localCerts;
   private final ByteBuffer[] singleSrcBuffer;
   private final ByteBuffer[] singleDstBuffer;
   private final OpenSslKeyMaterialManager keyMaterialManager;
   private final boolean enableOcsp;
   private int maxWrapOverhead;
   private int maxWrapBufferSize;
   SSLHandshakeException handshakeException;

   ReferenceCountedOpenSslEngine(ReferenceCountedOpenSslContext var1, ByteBufAllocator var2, String var3, int var4, boolean var5, boolean var6) {
      super(var3, var4);
      this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.NOT_STARTED;
      this.refCnt = new AbstractReferenceCounted() {
         public ReferenceCounted touch(Object var1) {
            if (ReferenceCountedOpenSslEngine.this.leak != null) {
               ReferenceCountedOpenSslEngine.this.leak.record(var1);
            }

            return ReferenceCountedOpenSslEngine.this;
         }

         protected void deallocate() {
            ReferenceCountedOpenSslEngine.this.shutdown();
            if (ReferenceCountedOpenSslEngine.this.leak != null) {
               boolean var1 = ReferenceCountedOpenSslEngine.this.leak.close(ReferenceCountedOpenSslEngine.this);

               assert var1;
            }

         }
      };
      this.clientAuth = ClientAuth.NONE;
      this.lastAccessed = -1L;
      this.singleSrcBuffer = new ByteBuffer[1];
      this.singleDstBuffer = new ByteBuffer[1];
      OpenSsl.ensureAvailability();
      this.alloc = (ByteBufAllocator)ObjectUtil.checkNotNull(var2, "alloc");
      this.apn = (OpenSslApplicationProtocolNegotiator)var1.applicationProtocolNegotiator();
      this.session = new ReferenceCountedOpenSslEngine.OpenSslSession(var1.sessionContext());
      this.clientMode = var1.isClient();
      this.engineMap = var1.engineMap;
      this.localCerts = var1.keyCertChain;
      this.keyMaterialManager = var1.keyMaterialManager();
      this.enableOcsp = var1.enableOcsp;
      this.jdkCompatibilityMode = var5;
      Lock var7 = var1.ctxLock.readLock();
      var7.lock();

      long var8;
      try {
         var8 = SSL.newSSL(var1.ctx, !var1.isClient());
      } finally {
         var7.unlock();
      }

      synchronized(this) {
         this.ssl = var8;

         try {
            this.networkBIO = SSL.bioNewByteBuffer(this.ssl, var1.getBioNonApplicationBufferSize());
            this.setClientAuth(this.clientMode ? ClientAuth.NONE : var1.clientAuth);
            if (var1.protocols != null) {
               this.setEnabledProtocols(var1.protocols);
            }

            if (this.clientMode && var3 != null) {
               SSL.setTlsExtHostName(this.ssl, var3);
            }

            if (this.enableOcsp) {
               SSL.enableOcsp(this.ssl);
            }

            if (!var5) {
               SSL.setMode(this.ssl, SSL.getMode(this.ssl) | SSL.SSL_MODE_ENABLE_PARTIAL_WRITE);
            }

            this.calculateMaxWrapOverhead();
         } catch (Throwable var16) {
            SSL.freeSSL(this.ssl);
            PlatformDependent.throwException(var16);
         }
      }

      this.leak = var6 ? leakDetector.track(this) : null;
   }

   public void setOcspResponse(byte[] var1) {
      if (!this.enableOcsp) {
         throw new IllegalStateException("OCSP stapling is not enabled");
      } else if (this.clientMode) {
         throw new IllegalStateException("Not a server SSLEngine");
      } else {
         synchronized(this) {
            SSL.setOcspResponse(this.ssl, var1);
         }
      }
   }

   public byte[] getOcspResponse() {
      if (!this.enableOcsp) {
         throw new IllegalStateException("OCSP stapling is not enabled");
      } else if (!this.clientMode) {
         throw new IllegalStateException("Not a client SSLEngine");
      } else {
         synchronized(this) {
            return SSL.getOcspResponse(this.ssl);
         }
      }
   }

   public final int refCnt() {
      return this.refCnt.refCnt();
   }

   public final ReferenceCounted retain() {
      this.refCnt.retain();
      return this;
   }

   public final ReferenceCounted retain(int var1) {
      this.refCnt.retain(var1);
      return this;
   }

   public final ReferenceCounted touch() {
      this.refCnt.touch();
      return this;
   }

   public final ReferenceCounted touch(Object var1) {
      this.refCnt.touch(var1);
      return this;
   }

   public final boolean release() {
      return this.refCnt.release();
   }

   public final boolean release(int var1) {
      return this.refCnt.release(var1);
   }

   public final synchronized SSLSession getHandshakeSession() {
      switch(this.handshakeState) {
      case NOT_STARTED:
      case FINISHED:
         return null;
      default:
         return this.session;
      }
   }

   public final synchronized long sslPointer() {
      return this.ssl;
   }

   public final synchronized void shutdown() {
      if (DESTROYED_UPDATER.compareAndSet(this, 0, 1)) {
         this.engineMap.remove(this.ssl);
         SSL.freeSSL(this.ssl);
         this.ssl = this.networkBIO = 0L;
         this.isInboundDone = this.outboundClosed = true;
      }

      SSL.clearError();
   }

   private int writePlaintextData(ByteBuffer var1, int var2) {
      int var3 = var1.position();
      int var4 = var1.limit();
      int var5;
      if (var1.isDirect()) {
         var5 = SSL.writeToSSL(this.ssl, bufferAddress(var1) + (long)var3, var2);
         if (var5 > 0) {
            var1.position(var3 + var5);
         }
      } else {
         ByteBuf var6 = this.alloc.directBuffer(var2);

         try {
            var1.limit(var3 + var2);
            var6.setBytes(0, (ByteBuffer)var1);
            var1.limit(var4);
            var5 = SSL.writeToSSL(this.ssl, OpenSsl.memoryAddress(var6), var2);
            if (var5 > 0) {
               var1.position(var3 + var5);
            } else {
               var1.position(var3);
            }
         } finally {
            var6.release();
         }
      }

      return var5;
   }

   private ByteBuf writeEncryptedData(ByteBuffer var1, int var2) {
      int var3 = var1.position();
      if (var1.isDirect()) {
         SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(var1) + (long)var3, var2, false);
      } else {
         ByteBuf var4 = this.alloc.directBuffer(var2);

         try {
            int var5 = var1.limit();
            var1.limit(var3 + var2);
            var4.writeBytes(var1);
            var1.position(var3);
            var1.limit(var5);
            SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(var4), var2, false);
            return var4;
         } catch (Throwable var6) {
            var4.release();
            PlatformDependent.throwException(var6);
         }
      }

      return null;
   }

   private int readPlaintextData(ByteBuffer var1) {
      int var3 = var1.position();
      int var2;
      if (var1.isDirect()) {
         var2 = SSL.readFromSSL(this.ssl, bufferAddress(var1) + (long)var3, var1.limit() - var3);
         if (var2 > 0) {
            var1.position(var3 + var2);
         }
      } else {
         int var4 = var1.limit();
         int var5 = Math.min(this.maxEncryptedPacketLength0(), var4 - var3);
         ByteBuf var6 = this.alloc.directBuffer(var5);

         try {
            var2 = SSL.readFromSSL(this.ssl, OpenSsl.memoryAddress(var6), var5);
            if (var2 > 0) {
               var1.limit(var3 + var2);
               var6.getBytes(var6.readerIndex(), var1);
               var1.limit(var4);
            }
         } finally {
            var6.release();
         }
      }

      return var2;
   }

   final synchronized int maxWrapOverhead() {
      return this.maxWrapOverhead;
   }

   final synchronized int maxEncryptedPacketLength() {
      return this.maxEncryptedPacketLength0();
   }

   final int maxEncryptedPacketLength0() {
      return this.maxWrapOverhead + MAX_PLAINTEXT_LENGTH;
   }

   final int calculateMaxLengthForWrap(int var1, int var2) {
      return (int)Math.min((long)this.maxWrapBufferSize, (long)var1 + (long)this.maxWrapOverhead * (long)var2);
   }

   final synchronized int sslPending() {
      return this.sslPending0();
   }

   private void calculateMaxWrapOverhead() {
      this.maxWrapOverhead = SSL.getMaxWrapOverhead(this.ssl);
      this.maxWrapBufferSize = this.jdkCompatibilityMode ? this.maxEncryptedPacketLength0() : this.maxEncryptedPacketLength0() << 4;
   }

   private int sslPending0() {
      return this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED ? 0 : SSL.sslPending(this.ssl);
   }

   private boolean isBytesAvailableEnoughForWrap(int var1, int var2, int var3) {
      return (long)var1 - (long)this.maxWrapOverhead * (long)var3 >= (long)var2;
   }

   public final SSLEngineResult wrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer var4) throws SSLException {
      if (var1 == null) {
         throw new IllegalArgumentException("srcs is null");
      } else if (var4 == null) {
         throw new IllegalArgumentException("dst is null");
      } else if (var2 < var1.length && var2 + var3 <= var1.length) {
         if (var4.isReadOnly()) {
            throw new ReadOnlyBufferException();
         } else {
            synchronized(this) {
               if (this.isOutboundDone()) {
                  return !this.isInboundDone() && !this.isDestroyed() ? NEED_UNWRAP_CLOSED : CLOSED_NOT_HANDSHAKING;
               } else {
                  int var6 = 0;
                  ByteBuf var7 = null;

                  try {
                     if (var4.isDirect()) {
                        SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(var4) + (long)var4.position(), var4.remaining(), true);
                     } else {
                        var7 = this.alloc.directBuffer(var4.remaining());
                        SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(var7), var7.writableBytes(), true);
                     }

                     int var8 = SSL.bioLengthByteBuffer(this.networkBIO);
                     if (this.outboundClosed) {
                        var6 = SSL.bioFlushByteBuffer(this.networkBIO);
                        SSLEngineResult var25;
                        if (var6 <= 0) {
                           var25 = this.newResultMayFinishHandshake(HandshakeStatus.NOT_HANDSHAKING, 0, 0);
                           return var25;
                        } else if (!this.doSSLShutdown()) {
                           var25 = this.newResultMayFinishHandshake(HandshakeStatus.NOT_HANDSHAKING, 0, var6);
                           return var25;
                        } else {
                           var6 = var8 - SSL.bioLengthByteBuffer(this.networkBIO);
                           var25 = this.newResultMayFinishHandshake(HandshakeStatus.NEED_WRAP, 0, var6);
                           return var25;
                        }
                     } else {
                        HandshakeStatus var9 = HandshakeStatus.NOT_HANDSHAKING;
                        if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
                           if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.STARTED_EXPLICITLY) {
                              this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.STARTED_IMPLICITLY;
                           }

                           var6 = SSL.bioFlushByteBuffer(this.networkBIO);
                           SSLEngineResult var24;
                           if (var6 > 0 && this.handshakeException != null) {
                              var24 = this.newResult(HandshakeStatus.NEED_WRAP, 0, var6);
                              return var24;
                           }

                           var9 = this.handshake();
                           var6 = var8 - SSL.bioLengthByteBuffer(this.networkBIO);
                           if (var6 > 0) {
                              var24 = this.newResult(this.mayFinishHandshake(var9 != HandshakeStatus.FINISHED ? (var6 == var8 ? HandshakeStatus.NEED_WRAP : this.getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))) : HandshakeStatus.FINISHED), 0, var6);
                              return var24;
                           }

                           if (var9 == HandshakeStatus.NEED_UNWRAP) {
                              var24 = this.isOutboundDone() ? NEED_UNWRAP_CLOSED : NEED_UNWRAP_OK;
                              return var24;
                           }

                           if (this.outboundClosed) {
                              var6 = SSL.bioFlushByteBuffer(this.networkBIO);
                              var24 = this.newResultMayFinishHandshake(var9, 0, var6);
                              return var24;
                           }
                        }

                        int var10 = var2 + var3;
                        int var11;
                        SSLEngineResult var27;
                        if (this.jdkCompatibilityMode) {
                           var11 = 0;

                           for(int var12 = var2; var12 < var10; ++var12) {
                              ByteBuffer var13 = var1[var12];
                              if (var13 == null) {
                                 throw new IllegalArgumentException("srcs[" + var12 + "] is null");
                              }

                              if (var11 != MAX_PLAINTEXT_LENGTH) {
                                 var11 += var13.remaining();
                                 if (var11 > MAX_PLAINTEXT_LENGTH || var11 < 0) {
                                    var11 = MAX_PLAINTEXT_LENGTH;
                                 }
                              }
                           }

                           if (!this.isBytesAvailableEnoughForWrap(var4.remaining(), var11, 1)) {
                              var27 = new SSLEngineResult(Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), 0, 0);
                              return var27;
                           }
                        }

                        var11 = 0;

                        for(var6 = SSL.bioFlushByteBuffer(this.networkBIO); var2 < var10; ++var2) {
                           ByteBuffer var26 = var1[var2];
                           int var28 = var26.remaining();
                           if (var28 != 0) {
                              int var14;
                              int var15;
                              SSLEngineResult var16;
                              if (this.jdkCompatibilityMode) {
                                 var14 = this.writePlaintextData(var26, Math.min(var28, MAX_PLAINTEXT_LENGTH - var11));
                              } else {
                                 var15 = var4.remaining() - var6 - this.maxWrapOverhead;
                                 if (var15 <= 0) {
                                    var16 = new SSLEngineResult(Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), var11, var6);
                                    return var16;
                                 }

                                 var14 = this.writePlaintextData(var26, Math.min(var28, var15));
                              }

                              if (var14 <= 0) {
                                 var15 = SSL.getError(this.ssl, var14);
                                 if (var15 == SSL.SSL_ERROR_ZERO_RETURN) {
                                    if (!this.receivedShutdown) {
                                       this.closeAll();
                                       var6 += var8 - SSL.bioLengthByteBuffer(this.networkBIO);
                                       HandshakeStatus var29 = this.mayFinishHandshake(var9 != HandshakeStatus.FINISHED ? (var6 == var4.remaining() ? HandshakeStatus.NEED_WRAP : this.getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))) : HandshakeStatus.FINISHED);
                                       SSLEngineResult var17 = this.newResult(var29, var11, var6);
                                       return var17;
                                    }

                                    var16 = this.newResult(HandshakeStatus.NOT_HANDSHAKING, var11, var6);
                                    return var16;
                                 }

                                 if (var15 != SSL.SSL_ERROR_WANT_READ) {
                                    if (var15 == SSL.SSL_ERROR_WANT_WRITE) {
                                       var16 = this.newResult(Status.BUFFER_OVERFLOW, var9, var11, var6);
                                       return var16;
                                    }

                                    throw this.shutdownWithError("SSL_write");
                                 }

                                 var16 = this.newResult(HandshakeStatus.NEED_UNWRAP, var11, var6);
                                 return var16;
                              }

                              var11 += var14;
                              var15 = SSL.bioLengthByteBuffer(this.networkBIO);
                              var6 += var8 - var15;
                              var8 = var15;
                              if (this.jdkCompatibilityMode || var6 == var4.remaining()) {
                                 var16 = this.newResultMayFinishHandshake(var9, var11, var6);
                                 return var16;
                              }
                           }
                        }

                        var27 = this.newResultMayFinishHandshake(var9, var11, var6);
                        return var27;
                     }
                  } finally {
                     SSL.bioClearByteBuffer(this.networkBIO);
                     if (var7 == null) {
                        var4.position(var4.position() + var6);
                     } else {
                        assert var7.readableBytes() <= var4.remaining() : "The destination buffer " + var4 + " didn't have enough remaining space to hold the encrypted content in " + var7;

                        var4.put(var7.internalNioBuffer(var7.readerIndex(), var6));
                        var7.release();
                     }

                  }
               }
            }
         }
      } else {
         throw new IndexOutOfBoundsException("offset: " + var2 + ", length: " + var3 + " (expected: offset <= offset + length <= srcs.length (" + var1.length + "))");
      }
   }

   private SSLEngineResult newResult(HandshakeStatus var1, int var2, int var3) {
      return this.newResult(Status.OK, var1, var2, var3);
   }

   private SSLEngineResult newResult(Status var1, HandshakeStatus var2, int var3, int var4) {
      if (this.isOutboundDone()) {
         if (this.isInboundDone()) {
            var2 = HandshakeStatus.NOT_HANDSHAKING;
            this.shutdown();
         }

         return new SSLEngineResult(Status.CLOSED, var2, var3, var4);
      } else {
         return new SSLEngineResult(var1, var2, var3, var4);
      }
   }

   private SSLEngineResult newResultMayFinishHandshake(HandshakeStatus var1, int var2, int var3) throws SSLException {
      return this.newResult(this.mayFinishHandshake(var1 != HandshakeStatus.FINISHED ? this.getHandshakeStatus() : HandshakeStatus.FINISHED), var2, var3);
   }

   private SSLEngineResult newResultMayFinishHandshake(Status var1, HandshakeStatus var2, int var3, int var4) throws SSLException {
      return this.newResult(var1, this.mayFinishHandshake(var2 != HandshakeStatus.FINISHED ? this.getHandshakeStatus() : HandshakeStatus.FINISHED), var3, var4);
   }

   private SSLException shutdownWithError(String var1) {
      String var2 = SSL.getLastError();
      return this.shutdownWithError(var1, var2);
   }

   private SSLException shutdownWithError(String var1, String var2) {
      if (logger.isDebugEnabled()) {
         logger.debug("{} failed: OpenSSL error: {}", var1, var2);
      }

      this.shutdown();
      return (SSLException)(this.handshakeState == ReferenceCountedOpenSslEngine.HandshakeState.FINISHED ? new SSLException(var2) : new SSLHandshakeException(var2));
   }

   public final SSLEngineResult unwrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer[] var4, int var5, int var6) throws SSLException {
      if (var1 == null) {
         throw new NullPointerException("srcs");
      } else if (var2 < var1.length && var2 + var3 <= var1.length) {
         if (var4 == null) {
            throw new IllegalArgumentException("dsts is null");
         } else if (var5 < var4.length && var5 + var6 <= var4.length) {
            long var7 = 0L;
            int var9 = var5 + var6;

            int var10;
            for(var10 = var5; var10 < var9; ++var10) {
               ByteBuffer var11 = var4[var10];
               if (var11 == null) {
                  throw new IllegalArgumentException("dsts[" + var10 + "] is null");
               }

               if (var11.isReadOnly()) {
                  throw new ReadOnlyBufferException();
               }

               var7 += (long)var11.remaining();
            }

            var10 = var2 + var3;
            long var40 = 0L;

            for(int var13 = var2; var13 < var10; ++var13) {
               ByteBuffer var14 = var1[var13];
               if (var14 == null) {
                  throw new IllegalArgumentException("srcs[" + var13 + "] is null");
               }

               var40 += (long)var14.remaining();
            }

            synchronized(this) {
               if (this.isInboundDone()) {
                  return !this.isOutboundDone() && !this.isDestroyed() ? NEED_WRAP_CLOSED : CLOSED_NOT_HANDSHAKING;
               } else {
                  HandshakeStatus var41 = HandshakeStatus.NOT_HANDSHAKING;
                  if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
                     if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.STARTED_EXPLICITLY) {
                        this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.STARTED_IMPLICITLY;
                     }

                     var41 = this.handshake();
                     if (var41 == HandshakeStatus.NEED_WRAP) {
                        return NEED_WRAP_OK;
                     }

                     if (this.isInboundDone) {
                        return NEED_WRAP_CLOSED;
                     }
                  }

                  int var15 = this.sslPending0();
                  int var16;
                  int var17;
                  if (this.jdkCompatibilityMode) {
                     if (var40 < 5L) {
                        return this.newResultMayFinishHandshake(Status.BUFFER_UNDERFLOW, var41, 0, 0);
                     }

                     var16 = SslUtils.getEncryptedPacketLength(var1, var2);
                     if (var16 == -2) {
                        throw new NotSslRecordException("not an SSL/TLS record");
                     }

                     var17 = var16 - 5;
                     if ((long)var17 > var7) {
                        if (var17 > MAX_RECORD_SIZE) {
                           throw new SSLException("Illegal packet length: " + var17 + " > " + this.session.getApplicationBufferSize());
                        }

                        this.session.tryExpandApplicationBufferSize(var17);
                        return this.newResultMayFinishHandshake(Status.BUFFER_OVERFLOW, var41, 0, 0);
                     }

                     if (var40 < (long)var16) {
                        return this.newResultMayFinishHandshake(Status.BUFFER_UNDERFLOW, var41, 0, 0);
                     }
                  } else {
                     if (var40 == 0L && var15 <= 0) {
                        return this.newResultMayFinishHandshake(Status.BUFFER_UNDERFLOW, var41, 0, 0);
                     }

                     if (var7 == 0L) {
                        return this.newResultMayFinishHandshake(Status.BUFFER_OVERFLOW, var41, 0, 0);
                     }

                     var16 = (int)Math.min(2147483647L, var40);
                  }

                  assert var2 < var10;

                  assert var7 > 0L;

                  var17 = 0;
                  int var18 = 0;

                  try {
                     label819:
                     while(true) {
                        ByteBuffer var19 = var1[var2];
                        int var20 = var19.remaining();
                        ByteBuf var21;
                        int var22;
                        if (var20 == 0) {
                           if (var15 <= 0) {
                              ++var2;
                              if (var2 < var10) {
                                 continue;
                              }
                              break;
                           }

                           var21 = null;
                           var22 = SSL.bioLengthByteBuffer(this.networkBIO);
                        } else {
                           var22 = Math.min(var16, var20);
                           var21 = this.writeEncryptedData(var19, var22);
                        }

                        try {
                           while(true) {
                              while(true) {
                                 ByteBuffer var23 = var4[var5];
                                 if (!var23.hasRemaining()) {
                                    ++var5;
                                    if (var5 >= var9) {
                                       break label819;
                                    }
                                 } else {
                                    int var24 = this.readPlaintextData(var23);
                                    int var25 = var22 - SSL.bioLengthByteBuffer(this.networkBIO);
                                    var18 += var25;
                                    var16 -= var25;
                                    var22 -= var25;
                                    var19.position(var19.position() + var25);
                                    SSLEngineResult var26;
                                    if (var24 <= 0) {
                                       var26 = SSL.getError(this.ssl, var24);
                                       if (var26 != SSL.SSL_ERROR_WANT_READ && var26 != SSL.SSL_ERROR_WANT_WRITE) {
                                          SSLEngineResult var27;
                                          if (var26 == SSL.SSL_ERROR_ZERO_RETURN) {
                                             if (!this.receivedShutdown) {
                                                this.closeAll();
                                             }

                                             var27 = this.newResultMayFinishHandshake(this.isInboundDone() ? Status.CLOSED : Status.OK, var41, var18, var17);
                                             return var27;
                                          }

                                          var27 = this.sslReadErrorResult(SSL.getLastErrorNumber(), var18, var17);
                                          return var27;
                                       }

                                       ++var2;
                                       if (var2 < var10) {
                                          continue label819;
                                       }
                                       break label819;
                                    }

                                    var17 += var24;
                                    if (!var23.hasRemaining()) {
                                       var15 = this.sslPending0();
                                       ++var5;
                                       if (var5 >= var9) {
                                          var26 = var15 > 0 ? this.newResult(Status.BUFFER_OVERFLOW, var41, var18, var17) : this.newResultMayFinishHandshake(this.isInboundDone() ? Status.CLOSED : Status.OK, var41, var18, var17);
                                          return var26;
                                       }
                                    } else if (var16 == 0 || this.jdkCompatibilityMode) {
                                       break label819;
                                    }
                                 }
                              }
                           }
                        } finally {
                           if (var21 != null) {
                              var21.release();
                           }

                        }
                     }
                  } finally {
                     SSL.bioClearByteBuffer(this.networkBIO);
                     this.rejectRemoteInitiatedRenegotiation();
                  }

                  if (!this.receivedShutdown && (SSL.getShutdown(this.ssl) & SSL.SSL_RECEIVED_SHUTDOWN) == SSL.SSL_RECEIVED_SHUTDOWN) {
                     this.closeAll();
                  }

                  return this.newResultMayFinishHandshake(this.isInboundDone() ? Status.CLOSED : Status.OK, var41, var18, var17);
               }
            }
         } else {
            throw new IndexOutOfBoundsException("offset: " + var5 + ", length: " + var6 + " (expected: offset <= offset + length <= dsts.length (" + var4.length + "))");
         }
      } else {
         throw new IndexOutOfBoundsException("offset: " + var2 + ", length: " + var3 + " (expected: offset <= offset + length <= srcs.length (" + var1.length + "))");
      }
   }

   private SSLEngineResult sslReadErrorResult(int var1, int var2, int var3) throws SSLException {
      String var4 = SSL.getErrorString((long)var1);
      if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
         if (this.handshakeException == null && this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
            this.handshakeException = new SSLHandshakeException(var4);
         }

         return new SSLEngineResult(Status.OK, HandshakeStatus.NEED_WRAP, var2, var3);
      } else {
         throw this.shutdownWithError("SSL_read", var4);
      }
   }

   private void closeAll() throws SSLException {
      this.receivedShutdown = true;
      this.closeOutbound();
      this.closeInbound();
   }

   private void rejectRemoteInitiatedRenegotiation() throws SSLHandshakeException {
      if (!this.isDestroyed() && SSL.getHandshakeCount(this.ssl) > 1) {
         this.shutdown();
         throw new SSLHandshakeException("remote-initiated renegotiation not allowed");
      }
   }

   public final SSLEngineResult unwrap(ByteBuffer[] var1, ByteBuffer[] var2) throws SSLException {
      return this.unwrap(var1, 0, var1.length, var2, 0, var2.length);
   }

   private ByteBuffer[] singleSrcBuffer(ByteBuffer var1) {
      this.singleSrcBuffer[0] = var1;
      return this.singleSrcBuffer;
   }

   private void resetSingleSrcBuffer() {
      this.singleSrcBuffer[0] = null;
   }

   private ByteBuffer[] singleDstBuffer(ByteBuffer var1) {
      this.singleDstBuffer[0] = var1;
      return this.singleDstBuffer;
   }

   private void resetSingleDstBuffer() {
      this.singleDstBuffer[0] = null;
   }

   public final synchronized SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2, int var3, int var4) throws SSLException {
      SSLEngineResult var5;
      try {
         var5 = this.unwrap(this.singleSrcBuffer(var1), 0, 1, var2, var3, var4);
      } finally {
         this.resetSingleSrcBuffer();
      }

      return var5;
   }

   public final synchronized SSLEngineResult wrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      SSLEngineResult var3;
      try {
         var3 = this.wrap(this.singleSrcBuffer(var1), var2);
      } finally {
         this.resetSingleSrcBuffer();
      }

      return var3;
   }

   public final synchronized SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      SSLEngineResult var3;
      try {
         var3 = this.unwrap(this.singleSrcBuffer(var1), this.singleDstBuffer(var2));
      } finally {
         this.resetSingleSrcBuffer();
         this.resetSingleDstBuffer();
      }

      return var3;
   }

   public final synchronized SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2) throws SSLException {
      SSLEngineResult var3;
      try {
         var3 = this.unwrap(this.singleSrcBuffer(var1), var2);
      } finally {
         this.resetSingleSrcBuffer();
      }

      return var3;
   }

   public final Runnable getDelegatedTask() {
      return null;
   }

   public final synchronized void closeInbound() throws SSLException {
      if (!this.isInboundDone) {
         this.isInboundDone = true;
         if (this.isOutboundDone()) {
            this.shutdown();
         }

         if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.NOT_STARTED && !this.receivedShutdown) {
            throw new SSLException("Inbound closed before receiving peer's close_notify: possible truncation attack?");
         }
      }
   }

   public final synchronized boolean isInboundDone() {
      return this.isInboundDone;
   }

   public final synchronized void closeOutbound() {
      if (!this.outboundClosed) {
         this.outboundClosed = true;
         if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.NOT_STARTED && !this.isDestroyed()) {
            int var1 = SSL.getShutdown(this.ssl);
            if ((var1 & SSL.SSL_SENT_SHUTDOWN) != SSL.SSL_SENT_SHUTDOWN) {
               this.doSSLShutdown();
            }
         } else {
            this.shutdown();
         }

      }
   }

   private boolean doSSLShutdown() {
      if (SSL.isInInit(this.ssl) != 0) {
         return false;
      } else {
         int var1 = SSL.shutdownSSL(this.ssl);
         if (var1 < 0) {
            int var2 = SSL.getError(this.ssl, var1);
            if (var2 == SSL.SSL_ERROR_SYSCALL || var2 == SSL.SSL_ERROR_SSL) {
               if (logger.isDebugEnabled()) {
                  logger.debug("SSL_shutdown failed: OpenSSL error: {}", (Object)SSL.getLastError());
               }

               this.shutdown();
               return false;
            }

            SSL.clearError();
         }

         return true;
      }
   }

   public final synchronized boolean isOutboundDone() {
      return this.outboundClosed && (this.networkBIO == 0L || SSL.bioLengthNonApplication(this.networkBIO) == 0);
   }

   public final String[] getSupportedCipherSuites() {
      return (String[])OpenSsl.AVAILABLE_CIPHER_SUITES.toArray(new String[OpenSsl.AVAILABLE_CIPHER_SUITES.size()]);
   }

   public final String[] getEnabledCipherSuites() {
      String[] var1;
      synchronized(this) {
         if (this.isDestroyed()) {
            return EmptyArrays.EMPTY_STRINGS;
         }

         var1 = SSL.getCiphers(this.ssl);
      }

      if (var1 == null) {
         return EmptyArrays.EMPTY_STRINGS;
      } else {
         synchronized(this) {
            for(int var3 = 0; var3 < var1.length; ++var3) {
               String var4 = this.toJavaCipherSuite(var1[var3]);
               if (var4 != null) {
                  var1[var3] = var4;
               }
            }

            return var1;
         }
      }
   }

   public final void setEnabledCipherSuites(String[] var1) {
      ObjectUtil.checkNotNull(var1, "cipherSuites");
      StringBuilder var2 = new StringBuilder();
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var6 == null) {
            break;
         }

         String var7 = CipherSuiteConverter.toOpenSsl(var6);
         if (var7 == null) {
            var7 = var6;
         }

         if (!OpenSsl.isCipherSuiteAvailable(var7)) {
            throw new IllegalArgumentException("unsupported cipher suite: " + var6 + '(' + var7 + ')');
         }

         var2.append(var7);
         var2.append(':');
      }

      if (var2.length() == 0) {
         throw new IllegalArgumentException("empty cipher suites");
      } else {
         var2.setLength(var2.length() - 1);
         String var11 = var2.toString();
         synchronized(this) {
            if (!this.isDestroyed()) {
               try {
                  SSL.setCipherSuites(this.ssl, var11);
               } catch (Exception var9) {
                  throw new IllegalStateException("failed to enable cipher suites: " + var11, var9);
               }

            } else {
               throw new IllegalStateException("failed to enable cipher suites: " + var11);
            }
         }
      }
   }

   public final String[] getSupportedProtocols() {
      return (String[])OpenSsl.SUPPORTED_PROTOCOLS_SET.toArray(new String[OpenSsl.SUPPORTED_PROTOCOLS_SET.size()]);
   }

   public final String[] getEnabledProtocols() {
      ArrayList var1 = new ArrayList(6);
      var1.add("SSLv2Hello");
      int var2;
      synchronized(this) {
         if (this.isDestroyed()) {
            return (String[])var1.toArray(new String[1]);
         }

         var2 = SSL.getOptions(this.ssl);
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_TLSv1, "TLSv1")) {
         var1.add("TLSv1");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_TLSv1_1, "TLSv1.1")) {
         var1.add("TLSv1.1");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_TLSv1_2, "TLSv1.2")) {
         var1.add("TLSv1.2");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_SSLv2, "SSLv2")) {
         var1.add("SSLv2");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_SSLv3, "SSLv3")) {
         var1.add("SSLv3");
      }

      return (String[])var1.toArray(new String[var1.size()]);
   }

   private static boolean isProtocolEnabled(int var0, int var1, String var2) {
      return (var0 & var1) == 0 && OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(var2);
   }

   public final void setEnabledProtocols(String[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         int var2 = OPENSSL_OP_NO_PROTOCOLS.length;
         byte var3 = 0;
         String[] var4 = var1;
         int var5 = var1.length;

         int var6;
         for(var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(var7)) {
               throw new IllegalArgumentException("Protocol " + var7 + " is not supported.");
            }

            if (var7.equals("SSLv2")) {
               if (var2 > 0) {
                  var2 = 0;
               }

               if (var3 < 0) {
                  var3 = 0;
               }
            } else if (var7.equals("SSLv3")) {
               if (var2 > 1) {
                  var2 = 1;
               }

               if (var3 < 1) {
                  var3 = 1;
               }
            } else if (var7.equals("TLSv1")) {
               if (var2 > 2) {
                  var2 = 2;
               }

               if (var3 < 2) {
                  var3 = 2;
               }
            } else if (var7.equals("TLSv1.1")) {
               if (var2 > 3) {
                  var2 = 3;
               }

               if (var3 < 3) {
                  var3 = 3;
               }
            } else if (var7.equals("TLSv1.2")) {
               if (var2 > 4) {
                  var2 = 4;
               }

               if (var3 < 4) {
                  var3 = 4;
               }
            }
         }

         synchronized(this) {
            if (this.isDestroyed()) {
               throw new IllegalStateException("failed to enable protocols: " + Arrays.asList(var1));
            } else {
               SSL.clearOptions(this.ssl, SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2);
               var5 = 0;

               for(var6 = 0; var6 < var2; ++var6) {
                  var5 |= OPENSSL_OP_NO_PROTOCOLS[var6];
               }

               assert var3 != 2147483647;

               for(var6 = var3 + 1; var6 < OPENSSL_OP_NO_PROTOCOLS.length; ++var6) {
                  var5 |= OPENSSL_OP_NO_PROTOCOLS[var6];
               }

               SSL.setOptions(this.ssl, var5);
            }
         }
      }
   }

   public final SSLSession getSession() {
      return this.session;
   }

   public final synchronized void beginHandshake() throws SSLException {
      switch(this.handshakeState) {
      case NOT_STARTED:
         this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.STARTED_EXPLICITLY;
         this.handshake();
         this.calculateMaxWrapOverhead();
         break;
      case FINISHED:
         throw RENEGOTIATION_UNSUPPORTED;
      case STARTED_IMPLICITLY:
         this.checkEngineClosed(BEGIN_HANDSHAKE_ENGINE_CLOSED);
         this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.STARTED_EXPLICITLY;
         this.calculateMaxWrapOverhead();
      case STARTED_EXPLICITLY:
         break;
      default:
         throw new Error();
      }

   }

   private void checkEngineClosed(SSLException var1) throws SSLException {
      if (this.isDestroyed()) {
         throw var1;
      }
   }

   private static HandshakeStatus pendingStatus(int var0) {
      return var0 > 0 ? HandshakeStatus.NEED_WRAP : HandshakeStatus.NEED_UNWRAP;
   }

   private static boolean isEmpty(Object[] var0) {
      return var0 == null || var0.length == 0;
   }

   private static boolean isEmpty(byte[] var0) {
      return var0 == null || var0.length == 0;
   }

   private HandshakeStatus handshake() throws SSLException {
      if (this.handshakeState == ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
         return HandshakeStatus.FINISHED;
      } else {
         this.checkEngineClosed(HANDSHAKE_ENGINE_CLOSED);
         SSLHandshakeException var1 = this.handshakeException;
         if (var1 != null) {
            if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
               return HandshakeStatus.NEED_WRAP;
            } else {
               this.handshakeException = null;
               this.shutdown();
               throw var1;
            }
         } else {
            this.engineMap.add(this);
            if (this.lastAccessed == -1L) {
               this.lastAccessed = System.currentTimeMillis();
            }

            if (!this.certificateSet && this.keyMaterialManager != null) {
               this.certificateSet = true;
               this.keyMaterialManager.setKeyMaterial(this);
            }

            int var2 = SSL.doHandshake(this.ssl);
            if (var2 <= 0) {
               if (this.handshakeException != null) {
                  var1 = this.handshakeException;
                  this.handshakeException = null;
                  this.shutdown();
                  throw var1;
               } else {
                  int var3 = SSL.getError(this.ssl, var2);
                  if (var3 != SSL.SSL_ERROR_WANT_READ && var3 != SSL.SSL_ERROR_WANT_WRITE) {
                     throw this.shutdownWithError("SSL_do_handshake");
                  } else {
                     return pendingStatus(SSL.bioLengthNonApplication(this.networkBIO));
                  }
               }
            } else {
               this.session.handshakeFinished();
               this.engineMap.remove(this.ssl);
               return HandshakeStatus.FINISHED;
            }
         }
      }
   }

   private HandshakeStatus mayFinishHandshake(HandshakeStatus var1) throws SSLException {
      return var1 == HandshakeStatus.NOT_HANDSHAKING && this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED ? this.handshake() : var1;
   }

   public final synchronized HandshakeStatus getHandshakeStatus() {
      return this.needPendingStatus() ? pendingStatus(SSL.bioLengthNonApplication(this.networkBIO)) : HandshakeStatus.NOT_HANDSHAKING;
   }

   private HandshakeStatus getHandshakeStatus(int var1) {
      return this.needPendingStatus() ? pendingStatus(var1) : HandshakeStatus.NOT_HANDSHAKING;
   }

   private boolean needPendingStatus() {
      return this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.NOT_STARTED && !this.isDestroyed() && (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED || this.isInboundDone() || this.isOutboundDone());
   }

   private String toJavaCipherSuite(String var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = toJavaCipherSuitePrefix(SSL.getVersion(this.ssl));
         return CipherSuiteConverter.toJava(var1, var2);
      }
   }

   private static String toJavaCipherSuitePrefix(String var0) {
      char var1;
      if (var0 != null && !var0.isEmpty()) {
         var1 = var0.charAt(0);
      } else {
         var1 = 0;
      }

      switch(var1) {
      case 'S':
         return "SSL";
      case 'T':
         return "TLS";
      default:
         return "UNKNOWN";
      }
   }

   public final void setUseClientMode(boolean var1) {
      if (var1 != this.clientMode) {
         throw new UnsupportedOperationException();
      }
   }

   public final boolean getUseClientMode() {
      return this.clientMode;
   }

   public final void setNeedClientAuth(boolean var1) {
      this.setClientAuth(var1 ? ClientAuth.REQUIRE : ClientAuth.NONE);
   }

   public final boolean getNeedClientAuth() {
      return this.clientAuth == ClientAuth.REQUIRE;
   }

   public final void setWantClientAuth(boolean var1) {
      this.setClientAuth(var1 ? ClientAuth.OPTIONAL : ClientAuth.NONE);
   }

   public final boolean getWantClientAuth() {
      return this.clientAuth == ClientAuth.OPTIONAL;
   }

   public final synchronized void setVerify(int var1, int var2) {
      SSL.setVerify(this.ssl, var1, var2);
   }

   private void setClientAuth(ClientAuth var1) {
      if (!this.clientMode) {
         synchronized(this) {
            if (this.clientAuth != var1) {
               switch(var1) {
               case NONE:
                  SSL.setVerify(this.ssl, 0, 10);
                  break;
               case REQUIRE:
                  SSL.setVerify(this.ssl, 2, 10);
                  break;
               case OPTIONAL:
                  SSL.setVerify(this.ssl, 1, 10);
                  break;
               default:
                  throw new Error(var1.toString());
               }

               this.clientAuth = var1;
            }
         }
      }
   }

   public final void setEnableSessionCreation(boolean var1) {
      if (var1) {
         throw new UnsupportedOperationException();
      }
   }

   public final boolean getEnableSessionCreation() {
      return false;
   }

   public final synchronized SSLParameters getSSLParameters() {
      SSLParameters var1 = super.getSSLParameters();
      int var2 = PlatformDependent.javaVersion();
      if (var2 >= 7) {
         var1.setEndpointIdentificationAlgorithm(this.endPointIdentificationAlgorithm);
         Java7SslParametersUtils.setAlgorithmConstraints(var1, this.algorithmConstraints);
         if (var2 >= 8) {
            if (this.sniHostNames != null) {
               Java8SslUtils.setSniHostNames(var1, this.sniHostNames);
            }

            if (!this.isDestroyed()) {
               Java8SslUtils.setUseCipherSuitesOrder(var1, (SSL.getOptions(this.ssl) & SSL.SSL_OP_CIPHER_SERVER_PREFERENCE) != 0);
            }

            Java8SslUtils.setSNIMatchers(var1, this.matchers);
         }
      }

      return var1;
   }

   public final synchronized void setSSLParameters(SSLParameters var1) {
      int var2 = PlatformDependent.javaVersion();
      if (var2 >= 7) {
         if (var1.getAlgorithmConstraints() != null) {
            throw new IllegalArgumentException("AlgorithmConstraints are not supported.");
         }

         if (var2 >= 8) {
            if (!this.isDestroyed()) {
               if (this.clientMode) {
                  List var3 = Java8SslUtils.getSniHostNames(var1);
                  Iterator var4 = var3.iterator();

                  while(var4.hasNext()) {
                     String var5 = (String)var4.next();
                     SSL.setTlsExtHostName(this.ssl, var5);
                  }

                  this.sniHostNames = var3;
               }

               if (Java8SslUtils.getUseCipherSuitesOrder(var1)) {
                  SSL.setOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
               } else {
                  SSL.clearOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
               }
            }

            this.matchers = var1.getSNIMatchers();
         }

         String var6 = var1.getEndpointIdentificationAlgorithm();
         boolean var7 = var6 != null && !var6.isEmpty();
         SSL.setHostNameValidation(this.ssl, 0, var7 ? this.getPeerHost() : null);
         if (this.clientMode && var7) {
            SSL.setVerify(this.ssl, 2, -1);
         }

         this.endPointIdentificationAlgorithm = var6;
         this.algorithmConstraints = var1.getAlgorithmConstraints();
      }

      super.setSSLParameters(var1);
   }

   private boolean isDestroyed() {
      return this.destroyed != 0;
   }

   final boolean checkSniHostnameMatch(String var1) {
      return Java8SslUtils.checkSniHostnameMatch(this.matchers, var1);
   }

   public String getNegotiatedApplicationProtocol() {
      return this.applicationProtocol;
   }

   private static long bufferAddress(ByteBuffer var0) {
      assert var0.isDirect();

      return PlatformDependent.hasUnsafe() ? PlatformDependent.directBufferAddress(var0) : Buffer.address(var0);
   }

   static {
      OPENSSL_OP_NO_PROTOCOLS = new int[]{SSL.SSL_OP_NO_SSLv2, SSL.SSL_OP_NO_SSLv3, SSL.SSL_OP_NO_TLSv1, SSL.SSL_OP_NO_TLSv1_1, SSL.SSL_OP_NO_TLSv1_2};
      MAX_PLAINTEXT_LENGTH = SSL.SSL_MAX_PLAINTEXT_LENGTH;
      MAX_RECORD_SIZE = SSL.SSL_MAX_RECORD_LENGTH;
      DESTROYED_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ReferenceCountedOpenSslEngine.class, "destroyed");
      NEED_UNWRAP_OK = new SSLEngineResult(Status.OK, HandshakeStatus.NEED_UNWRAP, 0, 0);
      NEED_UNWRAP_CLOSED = new SSLEngineResult(Status.CLOSED, HandshakeStatus.NEED_UNWRAP, 0, 0);
      NEED_WRAP_OK = new SSLEngineResult(Status.OK, HandshakeStatus.NEED_WRAP, 0, 0);
      NEED_WRAP_CLOSED = new SSLEngineResult(Status.CLOSED, HandshakeStatus.NEED_WRAP, 0, 0);
      CLOSED_NOT_HANDSHAKING = new SSLEngineResult(Status.CLOSED, HandshakeStatus.NOT_HANDSHAKING, 0, 0);
   }

   private final class OpenSslSession implements SSLSession {
      private final OpenSslSessionContext sessionContext;
      private X509Certificate[] x509PeerCerts;
      private Certificate[] peerCerts;
      private String protocol;
      private String cipher;
      private byte[] id;
      private long creationTime;
      private volatile int applicationBufferSize;
      private Map<String, Object> values;

      OpenSslSession(OpenSslSessionContext var2) {
         super();
         this.applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
         this.sessionContext = var2;
      }

      public byte[] getId() {
         synchronized(ReferenceCountedOpenSslEngine.this) {
            return this.id == null ? EmptyArrays.EMPTY_BYTES : (byte[])this.id.clone();
         }
      }

      public SSLSessionContext getSessionContext() {
         return this.sessionContext;
      }

      public long getCreationTime() {
         synchronized(ReferenceCountedOpenSslEngine.this) {
            if (this.creationTime == 0L && !ReferenceCountedOpenSslEngine.this.isDestroyed()) {
               this.creationTime = SSL.getTime(ReferenceCountedOpenSslEngine.this.ssl) * 1000L;
            }
         }

         return this.creationTime;
      }

      public long getLastAccessedTime() {
         long var1 = ReferenceCountedOpenSslEngine.this.lastAccessed;
         return var1 == -1L ? this.getCreationTime() : var1;
      }

      public void invalidate() {
         synchronized(ReferenceCountedOpenSslEngine.this) {
            if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
               SSL.setTimeout(ReferenceCountedOpenSslEngine.this.ssl, 0L);
            }

         }
      }

      public boolean isValid() {
         synchronized(ReferenceCountedOpenSslEngine.this) {
            if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
               return System.currentTimeMillis() - SSL.getTimeout(ReferenceCountedOpenSslEngine.this.ssl) * 1000L < SSL.getTime(ReferenceCountedOpenSslEngine.this.ssl) * 1000L;
            } else {
               return false;
            }
         }
      }

      public void putValue(String var1, Object var2) {
         if (var1 == null) {
            throw new NullPointerException("name");
         } else if (var2 == null) {
            throw new NullPointerException("value");
         } else {
            Map var3 = this.values;
            if (var3 == null) {
               var3 = this.values = new HashMap(2);
            }

            Object var4 = var3.put(var1, var2);
            if (var2 instanceof SSLSessionBindingListener) {
               ((SSLSessionBindingListener)var2).valueBound(new SSLSessionBindingEvent(this, var1));
            }

            this.notifyUnbound(var4, var1);
         }
      }

      public Object getValue(String var1) {
         if (var1 == null) {
            throw new NullPointerException("name");
         } else {
            return this.values == null ? null : this.values.get(var1);
         }
      }

      public void removeValue(String var1) {
         if (var1 == null) {
            throw new NullPointerException("name");
         } else {
            Map var2 = this.values;
            if (var2 != null) {
               Object var3 = var2.remove(var1);
               this.notifyUnbound(var3, var1);
            }
         }
      }

      public String[] getValueNames() {
         Map var1 = this.values;
         return var1 != null && !var1.isEmpty() ? (String[])var1.keySet().toArray(new String[var1.size()]) : EmptyArrays.EMPTY_STRINGS;
      }

      private void notifyUnbound(Object var1, String var2) {
         if (var1 instanceof SSLSessionBindingListener) {
            ((SSLSessionBindingListener)var1).valueUnbound(new SSLSessionBindingEvent(this, var2));
         }

      }

      void handshakeFinished() throws SSLException {
         synchronized(ReferenceCountedOpenSslEngine.this) {
            if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
               this.id = SSL.getSessionId(ReferenceCountedOpenSslEngine.this.ssl);
               this.cipher = ReferenceCountedOpenSslEngine.this.toJavaCipherSuite(SSL.getCipherForSSL(ReferenceCountedOpenSslEngine.this.ssl));
               this.protocol = SSL.getVersion(ReferenceCountedOpenSslEngine.this.ssl);
               this.initPeerCerts();
               this.selectApplicationProtocol();
               ReferenceCountedOpenSslEngine.this.calculateMaxWrapOverhead();
               ReferenceCountedOpenSslEngine.this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.FINISHED;
            } else {
               throw new SSLException("Already closed");
            }
         }
      }

      private void initPeerCerts() {
         byte[][] var1 = SSL.getPeerCertChain(ReferenceCountedOpenSslEngine.this.ssl);
         if (ReferenceCountedOpenSslEngine.this.clientMode) {
            if (ReferenceCountedOpenSslEngine.isEmpty((Object[])var1)) {
               this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
               this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
            } else {
               this.peerCerts = new Certificate[var1.length];
               this.x509PeerCerts = new X509Certificate[var1.length];
               this.initCerts(var1, 0);
            }
         } else {
            byte[] var2 = SSL.getPeerCertificate(ReferenceCountedOpenSslEngine.this.ssl);
            if (ReferenceCountedOpenSslEngine.isEmpty(var2)) {
               this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
               this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
            } else if (ReferenceCountedOpenSslEngine.isEmpty((Object[])var1)) {
               this.peerCerts = new Certificate[]{new OpenSslX509Certificate(var2)};
               this.x509PeerCerts = new X509Certificate[]{new OpenSslJavaxX509Certificate(var2)};
            } else {
               this.peerCerts = new Certificate[var1.length + 1];
               this.x509PeerCerts = new X509Certificate[var1.length + 1];
               this.peerCerts[0] = new OpenSslX509Certificate(var2);
               this.x509PeerCerts[0] = new OpenSslJavaxX509Certificate(var2);
               this.initCerts(var1, 1);
            }
         }

      }

      private void initCerts(byte[][] var1, int var2) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            int var4 = var2 + var3;
            this.peerCerts[var4] = new OpenSslX509Certificate(var1[var3]);
            this.x509PeerCerts[var4] = new OpenSslJavaxX509Certificate(var1[var3]);
         }

      }

      private void selectApplicationProtocol() throws SSLException {
         ApplicationProtocolConfig.SelectedListenerFailureBehavior var1 = ReferenceCountedOpenSslEngine.this.apn.selectedListenerFailureBehavior();
         List var2 = ReferenceCountedOpenSslEngine.this.apn.protocols();
         String var3;
         switch(ReferenceCountedOpenSslEngine.this.apn.protocol()) {
         case NONE:
            break;
         case ALPN:
            var3 = SSL.getAlpnSelected(ReferenceCountedOpenSslEngine.this.ssl);
            if (var3 != null) {
               ReferenceCountedOpenSslEngine.this.applicationProtocol = this.selectApplicationProtocol(var2, var1, var3);
            }
            break;
         case NPN:
            var3 = SSL.getNextProtoNegotiated(ReferenceCountedOpenSslEngine.this.ssl);
            if (var3 != null) {
               ReferenceCountedOpenSslEngine.this.applicationProtocol = this.selectApplicationProtocol(var2, var1, var3);
            }
            break;
         case NPN_AND_ALPN:
            var3 = SSL.getAlpnSelected(ReferenceCountedOpenSslEngine.this.ssl);
            if (var3 == null) {
               var3 = SSL.getNextProtoNegotiated(ReferenceCountedOpenSslEngine.this.ssl);
            }

            if (var3 != null) {
               ReferenceCountedOpenSslEngine.this.applicationProtocol = this.selectApplicationProtocol(var2, var1, var3);
            }
            break;
         default:
            throw new Error();
         }

      }

      private String selectApplicationProtocol(List<String> var1, ApplicationProtocolConfig.SelectedListenerFailureBehavior var2, String var3) throws SSLException {
         if (var2 == ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT) {
            return var3;
         } else {
            int var4 = var1.size();

            assert var4 > 0;

            if (var1.contains(var3)) {
               return var3;
            } else if (var2 == ApplicationProtocolConfig.SelectedListenerFailureBehavior.CHOOSE_MY_LAST_PROTOCOL) {
               return (String)var1.get(var4 - 1);
            } else {
               throw new SSLException("unknown protocol " + var3);
            }
         }
      }

      public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
         synchronized(ReferenceCountedOpenSslEngine.this) {
            if (ReferenceCountedOpenSslEngine.isEmpty((Object[])this.peerCerts)) {
               throw new SSLPeerUnverifiedException("peer not verified");
            } else {
               return (Certificate[])this.peerCerts.clone();
            }
         }
      }

      public Certificate[] getLocalCertificates() {
         return ReferenceCountedOpenSslEngine.this.localCerts == null ? null : (Certificate[])ReferenceCountedOpenSslEngine.this.localCerts.clone();
      }

      public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
         synchronized(ReferenceCountedOpenSslEngine.this) {
            if (ReferenceCountedOpenSslEngine.isEmpty((Object[])this.x509PeerCerts)) {
               throw new SSLPeerUnverifiedException("peer not verified");
            } else {
               return (X509Certificate[])this.x509PeerCerts.clone();
            }
         }
      }

      public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
         Certificate[] var1 = this.getPeerCertificates();
         return ((java.security.cert.X509Certificate)var1[0]).getSubjectX500Principal();
      }

      public Principal getLocalPrincipal() {
         Certificate[] var1 = ReferenceCountedOpenSslEngine.this.localCerts;
         return var1 != null && var1.length != 0 ? ((java.security.cert.X509Certificate)var1[0]).getIssuerX500Principal() : null;
      }

      public String getCipherSuite() {
         synchronized(ReferenceCountedOpenSslEngine.this) {
            return this.cipher == null ? "SSL_NULL_WITH_NULL_NULL" : this.cipher;
         }
      }

      public String getProtocol() {
         String var1 = this.protocol;
         if (var1 == null) {
            synchronized(ReferenceCountedOpenSslEngine.this) {
               if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                  var1 = SSL.getVersion(ReferenceCountedOpenSslEngine.this.ssl);
               } else {
                  var1 = "";
               }
            }
         }

         return var1;
      }

      public String getPeerHost() {
         return ReferenceCountedOpenSslEngine.this.getPeerHost();
      }

      public int getPeerPort() {
         return ReferenceCountedOpenSslEngine.this.getPeerPort();
      }

      public int getPacketBufferSize() {
         return ReferenceCountedOpenSslEngine.this.maxEncryptedPacketLength();
      }

      public int getApplicationBufferSize() {
         return this.applicationBufferSize;
      }

      void tryExpandApplicationBufferSize(int var1) {
         if (var1 > ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH && this.applicationBufferSize != ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE) {
            this.applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE;
         }

      }
   }

   private static enum HandshakeState {
      NOT_STARTED,
      STARTED_IMPLICITLY,
      STARTED_EXPLICITLY,
      FINISHED;

      private HandshakeState() {
      }
   }
}
