package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.internal.tcnative.CertificateVerifier;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertPathValidatorException.Reason;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public abstract class ReferenceCountedOpenSslContext extends SslContext implements ReferenceCounted {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslContext.class);
   private static final int DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
      public Integer run() {
         return Math.max(1, SystemPropertyUtil.getInt("io.netty.handler.ssl.openssl.bioNonApplicationBufferSize", 2048));
      }
   });
   private static final Integer DH_KEY_LENGTH;
   private static final ResourceLeakDetector<ReferenceCountedOpenSslContext> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslContext.class);
   protected static final int VERIFY_DEPTH = 10;
   protected long ctx;
   private final List<String> unmodifiableCiphers;
   private final long sessionCacheSize;
   private final long sessionTimeout;
   private final OpenSslApplicationProtocolNegotiator apn;
   private final int mode;
   private final ResourceLeakTracker<ReferenceCountedOpenSslContext> leak;
   private final AbstractReferenceCounted refCnt;
   final Certificate[] keyCertChain;
   final ClientAuth clientAuth;
   final String[] protocols;
   final boolean enableOcsp;
   final OpenSslEngineMap engineMap;
   final ReadWriteLock ctxLock;
   private volatile int bioNonApplicationBufferSize;
   static final OpenSslApplicationProtocolNegotiator NONE_PROTOCOL_NEGOTIATOR = new OpenSslApplicationProtocolNegotiator() {
      public ApplicationProtocolConfig.Protocol protocol() {
         return ApplicationProtocolConfig.Protocol.NONE;
      }

      public List<String> protocols() {
         return Collections.emptyList();
      }

      public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
         return ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
      }

      public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
         return ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
      }
   };

   ReferenceCountedOpenSslContext(Iterable<String> var1, CipherSuiteFilter var2, ApplicationProtocolConfig var3, long var4, long var6, int var8, Certificate[] var9, ClientAuth var10, String[] var11, boolean var12, boolean var13, boolean var14) throws SSLException {
      this(var1, var2, toNegotiator(var3), var4, var6, var8, var9, var10, var11, var12, var13, var14);
   }

   ReferenceCountedOpenSslContext(Iterable<String> var1, CipherSuiteFilter var2, OpenSslApplicationProtocolNegotiator var3, long var4, long var6, int var8, Certificate[] var9, ClientAuth var10, String[] var11, boolean var12, boolean var13, boolean var14) throws SSLException {
      super(var12);
      this.refCnt = new AbstractReferenceCounted() {
         public ReferenceCounted touch(Object var1) {
            if (ReferenceCountedOpenSslContext.this.leak != null) {
               ReferenceCountedOpenSslContext.this.leak.record(var1);
            }

            return ReferenceCountedOpenSslContext.this;
         }

         protected void deallocate() {
            ReferenceCountedOpenSslContext.this.destroy();
            if (ReferenceCountedOpenSslContext.this.leak != null) {
               boolean var1 = ReferenceCountedOpenSslContext.this.leak.close(ReferenceCountedOpenSslContext.this);

               assert var1;
            }

         }
      };
      this.engineMap = new ReferenceCountedOpenSslContext.DefaultOpenSslEngineMap();
      this.ctxLock = new ReentrantReadWriteLock();
      this.bioNonApplicationBufferSize = DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE;
      OpenSsl.ensureAvailability();
      if (var13 && !OpenSsl.isOcspSupported()) {
         throw new IllegalStateException("OCSP is not supported.");
      } else if (var8 != 1 && var8 != 0) {
         throw new IllegalArgumentException("mode most be either SSL.SSL_MODE_SERVER or SSL.SSL_MODE_CLIENT");
      } else {
         this.leak = var14 ? leakDetector.track(this) : null;
         this.mode = var8;
         this.clientAuth = this.isServer() ? (ClientAuth)ObjectUtil.checkNotNull(var10, "clientAuth") : ClientAuth.NONE;
         this.protocols = var11;
         this.enableOcsp = var13;
         this.keyCertChain = var9 == null ? null : (Certificate[])var9.clone();
         this.unmodifiableCiphers = Arrays.asList(((CipherSuiteFilter)ObjectUtil.checkNotNull(var2, "cipherFilter")).filterCipherSuites(var1, OpenSsl.DEFAULT_CIPHERS, OpenSsl.availableJavaCipherSuites()));
         this.apn = (OpenSslApplicationProtocolNegotiator)ObjectUtil.checkNotNull(var3, "apn");
         boolean var15 = false;

         try {
            try {
               this.ctx = SSLContext.make(31, var8);
            } catch (Exception var26) {
               throw new SSLException("failed to create an SSL_CTX", var26);
            }

            SSLContext.setOptions(this.ctx, SSLContext.getOptions(this.ctx) | SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_CIPHER_SERVER_PREFERENCE | SSL.SSL_OP_NO_COMPRESSION | SSL.SSL_OP_NO_TICKET);
            SSLContext.setMode(this.ctx, SSLContext.getMode(this.ctx) | SSL.SSL_MODE_ACCEPT_MOVING_WRITE_BUFFER);
            if (DH_KEY_LENGTH != null) {
               SSLContext.setTmpDHLength(this.ctx, DH_KEY_LENGTH);
            }

            try {
               SSLContext.setCipherSuite(this.ctx, CipherSuiteConverter.toOpenSsl((Iterable)this.unmodifiableCiphers));
            } catch (SSLException var24) {
               throw var24;
            } catch (Exception var25) {
               throw new SSLException("failed to set cipher suite: " + this.unmodifiableCiphers, var25);
            }

            List var16 = var3.protocols();
            if (!var16.isEmpty()) {
               String[] var17 = (String[])var16.toArray(new String[var16.size()]);
               int var18 = opensslSelectorFailureBehavior(var3.selectorFailureBehavior());
               switch(var3.protocol()) {
               case NPN:
                  SSLContext.setNpnProtos(this.ctx, var17, var18);
                  break;
               case ALPN:
                  SSLContext.setAlpnProtos(this.ctx, var17, var18);
                  break;
               case NPN_AND_ALPN:
                  SSLContext.setNpnProtos(this.ctx, var17, var18);
                  SSLContext.setAlpnProtos(this.ctx, var17, var18);
                  break;
               default:
                  throw new Error();
               }
            }

            if (var4 <= 0L) {
               var4 = SSLContext.setSessionCacheSize(this.ctx, 20480L);
            }

            this.sessionCacheSize = var4;
            SSLContext.setSessionCacheSize(this.ctx, var4);
            if (var6 <= 0L) {
               var6 = SSLContext.setSessionCacheTimeout(this.ctx, 300L);
            }

            this.sessionTimeout = var6;
            SSLContext.setSessionCacheTimeout(this.ctx, var6);
            if (var13) {
               SSLContext.enableOcsp(this.ctx, this.isClient());
            }

            var15 = true;
         } finally {
            if (!var15) {
               this.release();
            }

         }

      }
   }

   private static int opensslSelectorFailureBehavior(ApplicationProtocolConfig.SelectorFailureBehavior var0) {
      switch(var0) {
      case NO_ADVERTISE:
         return 0;
      case CHOOSE_MY_LAST_PROTOCOL:
         return 1;
      default:
         throw new Error();
      }
   }

   public final List<String> cipherSuites() {
      return this.unmodifiableCiphers;
   }

   public final long sessionCacheSize() {
      return this.sessionCacheSize;
   }

   public final long sessionTimeout() {
      return this.sessionTimeout;
   }

   public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
      return this.apn;
   }

   public final boolean isClient() {
      return this.mode == 0;
   }

   public final SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3) {
      return this.newEngine0(var1, var2, var3, true);
   }

   protected final SslHandler newHandler(ByteBufAllocator var1, boolean var2) {
      return new SslHandler(this.newEngine0(var1, (String)null, -1, false), var2);
   }

   protected final SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      return new SslHandler(this.newEngine0(var1, var2, var3, false), var4);
   }

   SSLEngine newEngine0(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      return new ReferenceCountedOpenSslEngine(this, var1, var2, var3, var4, true);
   }

   abstract OpenSslKeyMaterialManager keyMaterialManager();

   public final SSLEngine newEngine(ByteBufAllocator var1) {
      return this.newEngine(var1, (String)null, -1);
   }

   /** @deprecated */
   @Deprecated
   public final long context() {
      Lock var1 = this.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = this.ctx;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   /** @deprecated */
   @Deprecated
   public final OpenSslSessionStats stats() {
      return this.sessionContext().stats();
   }

   /** @deprecated */
   @Deprecated
   public void setRejectRemoteInitiatedRenegotiation(boolean var1) {
      if (!var1) {
         throw new UnsupportedOperationException("Renegotiation is not supported");
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean getRejectRemoteInitiatedRenegotiation() {
      return true;
   }

   public void setBioNonApplicationBufferSize(int var1) {
      this.bioNonApplicationBufferSize = ObjectUtil.checkPositiveOrZero(var1, "bioNonApplicationBufferSize");
   }

   public int getBioNonApplicationBufferSize() {
      return this.bioNonApplicationBufferSize;
   }

   /** @deprecated */
   @Deprecated
   public final void setTicketKeys(byte[] var1) {
      this.sessionContext().setTicketKeys(var1);
   }

   public abstract OpenSslSessionContext sessionContext();

   /** @deprecated */
   @Deprecated
   public final long sslCtxPointer() {
      Lock var1 = this.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = this.ctx;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   private void destroy() {
      Lock var1 = this.ctxLock.writeLock();
      var1.lock();

      try {
         if (this.ctx != 0L) {
            if (this.enableOcsp) {
               SSLContext.disableOcsp(this.ctx);
            }

            SSLContext.free(this.ctx);
            this.ctx = 0L;
         }
      } finally {
         var1.unlock();
      }

   }

   protected static X509Certificate[] certificates(byte[][] var0) {
      X509Certificate[] var1 = new X509Certificate[var0.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = new OpenSslX509Certificate(var0[var2]);
      }

      return var1;
   }

   protected static X509TrustManager chooseTrustManager(TrustManager[] var0) {
      TrustManager[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TrustManager var4 = var1[var3];
         if (var4 instanceof X509TrustManager) {
            return (X509TrustManager)var4;
         }
      }

      throw new IllegalStateException("no X509TrustManager found");
   }

   protected static X509KeyManager chooseX509KeyManager(KeyManager[] var0) {
      KeyManager[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         KeyManager var4 = var1[var3];
         if (var4 instanceof X509KeyManager) {
            return (X509KeyManager)var4;
         }
      }

      throw new IllegalStateException("no X509KeyManager found");
   }

   static OpenSslApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig var0) {
      if (var0 == null) {
         return NONE_PROTOCOL_NEGOTIATOR;
      } else {
         switch(var0.protocol()) {
         case NPN:
         case ALPN:
         case NPN_AND_ALPN:
            switch(var0.selectedListenerFailureBehavior()) {
            case CHOOSE_MY_LAST_PROTOCOL:
            case ACCEPT:
               switch(var0.selectorFailureBehavior()) {
               case NO_ADVERTISE:
               case CHOOSE_MY_LAST_PROTOCOL:
                  return new OpenSslDefaultApplicationProtocolNegotiator(var0);
               default:
                  throw new UnsupportedOperationException("OpenSSL provider does not support " + var0.selectorFailureBehavior() + " behavior");
               }
            default:
               throw new UnsupportedOperationException("OpenSSL provider does not support " + var0.selectedListenerFailureBehavior() + " behavior");
            }
         case NONE:
            return NONE_PROTOCOL_NEGOTIATOR;
         default:
            throw new Error();
         }
      }
   }

   static boolean useExtendedTrustManager(X509TrustManager var0) {
      return PlatformDependent.javaVersion() >= 7 && var0 instanceof X509ExtendedTrustManager;
   }

   static boolean useExtendedKeyManager(X509KeyManager var0) {
      return PlatformDependent.javaVersion() >= 7 && var0 instanceof X509ExtendedKeyManager;
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

   static void setKeyMaterial(long var0, X509Certificate[] var2, PrivateKey var3, String var4) throws SSLException {
      long var5 = 0L;
      long var7 = 0L;
      long var9 = 0L;
      PemEncoded var11 = null;

      try {
         var11 = PemX509Certificate.toPEM(ByteBufAllocator.DEFAULT, true, var2);
         var7 = toBIO(ByteBufAllocator.DEFAULT, var11.retain());
         var9 = toBIO(ByteBufAllocator.DEFAULT, var11.retain());
         if (var3 != null) {
            var5 = toBIO(var3);
         }

         SSLContext.setCertificateBio(var0, var7, var5, var4 == null ? "" : var4);
         SSLContext.setCertificateChainBio(var0, var9, true);
      } catch (SSLException var17) {
         throw var17;
      } catch (Exception var18) {
         throw new SSLException("failed to set certificate and key", var18);
      } finally {
         freeBio(var5);
         freeBio(var7);
         freeBio(var9);
         if (var11 != null) {
            var11.release();
         }

      }

   }

   static void freeBio(long var0) {
      if (var0 != 0L) {
         SSL.freeBIO(var0);
      }

   }

   static long toBIO(PrivateKey var0) throws Exception {
      if (var0 == null) {
         return 0L;
      } else {
         ByteBufAllocator var1 = ByteBufAllocator.DEFAULT;
         PemEncoded var2 = PemPrivateKey.toPEM(var1, true, var0);

         long var3;
         try {
            var3 = toBIO(var1, var2.retain());
         } finally {
            var2.release();
         }

         return var3;
      }
   }

   static long toBIO(X509Certificate... var0) throws Exception {
      if (var0 == null) {
         return 0L;
      } else if (var0.length == 0) {
         throw new IllegalArgumentException("certChain can't be empty");
      } else {
         ByteBufAllocator var1 = ByteBufAllocator.DEFAULT;
         PemEncoded var2 = PemX509Certificate.toPEM(var1, true, var0);

         long var3;
         try {
            var3 = toBIO(var1, var2.retain());
         } finally {
            var2.release();
         }

         return var3;
      }
   }

   static long toBIO(ByteBufAllocator var0, PemEncoded var1) throws Exception {
      long var4;
      try {
         ByteBuf var2 = var1.content();
         if (var2.isDirect()) {
            long var29 = newBIO(var2.retainedSlice());
            return var29;
         }

         ByteBuf var3 = var0.directBuffer(var2.readableBytes());

         try {
            var3.writeBytes(var2, var2.readerIndex(), var2.readableBytes());
            var4 = newBIO(var3.retainedSlice());
         } finally {
            try {
               if (var1.isSensitive()) {
                  SslUtils.zeroout(var3);
               }
            } finally {
               var3.release();
            }

         }
      } finally {
         var1.release();
      }

      return var4;
   }

   private static long newBIO(ByteBuf var0) throws Exception {
      long var4;
      try {
         long var1 = SSL.newMemBIO();
         int var3 = var0.readableBytes();
         if (SSL.bioWrite(var1, OpenSsl.memoryAddress(var0) + (long)var0.readerIndex(), var3) != var3) {
            SSL.freeBIO(var1);
            throw new IllegalStateException("Could not write data to memory BIO");
         }

         var4 = var1;
      } finally {
         var0.release();
      }

      return var4;
   }

   static {
      Integer var0 = null;

      try {
         String var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return SystemPropertyUtil.get("jdk.tls.ephemeralDHKeySize");
            }
         });
         if (var1 != null) {
            try {
               var0 = Integer.valueOf(var1);
            } catch (NumberFormatException var3) {
               logger.debug("ReferenceCountedOpenSslContext supports -Djdk.tls.ephemeralDHKeySize={int}, but got: " + var1);
            }
         }
      } catch (Throwable var4) {
      }

      DH_KEY_LENGTH = var0;
   }

   private static final class DefaultOpenSslEngineMap implements OpenSslEngineMap {
      private final Map<Long, ReferenceCountedOpenSslEngine> engines;

      private DefaultOpenSslEngineMap() {
         super();
         this.engines = PlatformDependent.newConcurrentHashMap();
      }

      public ReferenceCountedOpenSslEngine remove(long var1) {
         return (ReferenceCountedOpenSslEngine)this.engines.remove(var1);
      }

      public void add(ReferenceCountedOpenSslEngine var1) {
         this.engines.put(var1.sslPointer(), var1);
      }

      public ReferenceCountedOpenSslEngine get(long var1) {
         return (ReferenceCountedOpenSslEngine)this.engines.get(var1);
      }

      // $FF: synthetic method
      DefaultOpenSslEngineMap(Object var1) {
         this();
      }
   }

   abstract static class AbstractCertificateVerifier extends CertificateVerifier {
      private final OpenSslEngineMap engineMap;

      AbstractCertificateVerifier(OpenSslEngineMap var1) {
         super();
         this.engineMap = var1;
      }

      public final int verify(long var1, byte[][] var3, String var4) {
         X509Certificate[] var5 = ReferenceCountedOpenSslContext.certificates(var3);
         ReferenceCountedOpenSslEngine var6 = this.engineMap.get(var1);

         try {
            this.verify(var6, var5, var4);
            return CertificateVerifier.X509_V_OK;
         } catch (Throwable var12) {
            ReferenceCountedOpenSslContext.logger.debug("verification of certificate failed", var12);
            SSLHandshakeException var8 = new SSLHandshakeException("General OpenSslEngine problem");
            var8.initCause(var12);
            var6.handshakeException = var8;
            if (var12 instanceof OpenSslCertificateException) {
               return ((OpenSslCertificateException)var12).errorCode();
            } else if (var12 instanceof CertificateExpiredException) {
               return CertificateVerifier.X509_V_ERR_CERT_HAS_EXPIRED;
            } else if (var12 instanceof CertificateNotYetValidException) {
               return CertificateVerifier.X509_V_ERR_CERT_NOT_YET_VALID;
            } else {
               if (PlatformDependent.javaVersion() >= 7) {
                  if (var12 instanceof CertificateRevokedException) {
                     return CertificateVerifier.X509_V_ERR_CERT_REVOKED;
                  }

                  for(Throwable var9 = var12.getCause(); var9 != null; var9 = var9.getCause()) {
                     if (var9 instanceof CertPathValidatorException) {
                        CertPathValidatorException var10 = (CertPathValidatorException)var9;
                        Reason var11 = var10.getReason();
                        if (var11 == BasicReason.EXPIRED) {
                           return CertificateVerifier.X509_V_ERR_CERT_HAS_EXPIRED;
                        }

                        if (var11 == BasicReason.NOT_YET_VALID) {
                           return CertificateVerifier.X509_V_ERR_CERT_NOT_YET_VALID;
                        }

                        if (var11 == BasicReason.REVOKED) {
                           return CertificateVerifier.X509_V_ERR_CERT_REVOKED;
                        }
                     }
                  }
               }

               return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
            }
         }
      }

      abstract void verify(ReferenceCountedOpenSslEngine var1, X509Certificate[] var2, String var3) throws Exception;
   }
}
