package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

public class JdkSslContext extends SslContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
   static final String PROTOCOL = "TLS";
   private static final String[] DEFAULT_PROTOCOLS;
   private static final List<String> DEFAULT_CIPHERS;
   private static final Set<String> SUPPORTED_CIPHERS;
   private final String[] protocols;
   private final String[] cipherSuites;
   private final List<String> unmodifiableCipherSuites;
   private final JdkApplicationProtocolNegotiator apn;
   private final ClientAuth clientAuth;
   private final SSLContext sslContext;
   private final boolean isClient;

   public JdkSslContext(SSLContext var1, boolean var2, ClientAuth var3) {
      this(var1, var2, (Iterable)null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, var3, (String[])null, false);
   }

   public JdkSslContext(SSLContext var1, boolean var2, Iterable<String> var3, CipherSuiteFilter var4, ApplicationProtocolConfig var5, ClientAuth var6) {
      this(var1, var2, var3, var4, toNegotiator(var5, !var2), var6, (String[])null, false);
   }

   JdkSslContext(SSLContext var1, boolean var2, Iterable<String> var3, CipherSuiteFilter var4, JdkApplicationProtocolNegotiator var5, ClientAuth var6, String[] var7, boolean var8) {
      super(var8);
      this.apn = (JdkApplicationProtocolNegotiator)ObjectUtil.checkNotNull(var5, "apn");
      this.clientAuth = (ClientAuth)ObjectUtil.checkNotNull(var6, "clientAuth");
      this.cipherSuites = ((CipherSuiteFilter)ObjectUtil.checkNotNull(var4, "cipherFilter")).filterCipherSuites(var3, DEFAULT_CIPHERS, SUPPORTED_CIPHERS);
      this.protocols = var7 == null ? DEFAULT_PROTOCOLS : var7;
      this.unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));
      this.sslContext = (SSLContext)ObjectUtil.checkNotNull(var1, "sslContext");
      this.isClient = var2;
   }

   public final SSLContext context() {
      return this.sslContext;
   }

   public final boolean isClient() {
      return this.isClient;
   }

   public final SSLSessionContext sessionContext() {
      return this.isServer() ? this.context().getServerSessionContext() : this.context().getClientSessionContext();
   }

   public final List<String> cipherSuites() {
      return this.unmodifiableCipherSuites;
   }

   public final long sessionCacheSize() {
      return (long)this.sessionContext().getSessionCacheSize();
   }

   public final long sessionTimeout() {
      return (long)this.sessionContext().getSessionTimeout();
   }

   public final SSLEngine newEngine(ByteBufAllocator var1) {
      return this.configureAndWrapEngine(this.context().createSSLEngine(), var1);
   }

   public final SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3) {
      return this.configureAndWrapEngine(this.context().createSSLEngine(var2, var3), var1);
   }

   private SSLEngine configureAndWrapEngine(SSLEngine var1, ByteBufAllocator var2) {
      var1.setEnabledCipherSuites(this.cipherSuites);
      var1.setEnabledProtocols(this.protocols);
      var1.setUseClientMode(this.isClient());
      if (this.isServer()) {
         switch(this.clientAuth) {
         case OPTIONAL:
            var1.setWantClientAuth(true);
            break;
         case REQUIRE:
            var1.setNeedClientAuth(true);
         case NONE:
            break;
         default:
            throw new Error("Unknown auth " + this.clientAuth);
         }
      }

      JdkApplicationProtocolNegotiator.SslEngineWrapperFactory var3 = this.apn.wrapperFactory();
      return var3 instanceof JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory ? ((JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory)var3).wrapSslEngine(var1, var2, this.apn, this.isServer()) : var3.wrapSslEngine(var1, this.apn, this.isServer());
   }

   public final JdkApplicationProtocolNegotiator applicationProtocolNegotiator() {
      return this.apn;
   }

   static JdkApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig var0, boolean var1) {
      if (var0 == null) {
         return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
      } else {
         switch(var0.protocol()) {
         case NONE:
            return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
         case ALPN:
            if (var1) {
               switch(var0.selectorFailureBehavior()) {
               case FATAL_ALERT:
                  return new JdkAlpnApplicationProtocolNegotiator(true, var0.supportedProtocols());
               case NO_ADVERTISE:
                  return new JdkAlpnApplicationProtocolNegotiator(false, var0.supportedProtocols());
               default:
                  throw new UnsupportedOperationException("JDK provider does not support " + var0.selectorFailureBehavior() + " failure behavior");
               }
            } else {
               switch(var0.selectedListenerFailureBehavior()) {
               case ACCEPT:
                  return new JdkAlpnApplicationProtocolNegotiator(false, var0.supportedProtocols());
               case FATAL_ALERT:
                  return new JdkAlpnApplicationProtocolNegotiator(true, var0.supportedProtocols());
               default:
                  throw new UnsupportedOperationException("JDK provider does not support " + var0.selectedListenerFailureBehavior() + " failure behavior");
               }
            }
         case NPN:
            if (var1) {
               switch(var0.selectedListenerFailureBehavior()) {
               case ACCEPT:
                  return new JdkNpnApplicationProtocolNegotiator(false, var0.supportedProtocols());
               case FATAL_ALERT:
                  return new JdkNpnApplicationProtocolNegotiator(true, var0.supportedProtocols());
               default:
                  throw new UnsupportedOperationException("JDK provider does not support " + var0.selectedListenerFailureBehavior() + " failure behavior");
               }
            } else {
               switch(var0.selectorFailureBehavior()) {
               case FATAL_ALERT:
                  return new JdkNpnApplicationProtocolNegotiator(true, var0.supportedProtocols());
               case NO_ADVERTISE:
                  return new JdkNpnApplicationProtocolNegotiator(false, var0.supportedProtocols());
               default:
                  throw new UnsupportedOperationException("JDK provider does not support " + var0.selectorFailureBehavior() + " failure behavior");
               }
            }
         default:
            throw new UnsupportedOperationException("JDK provider does not support " + var0.protocol() + " protocol");
         }
      }
   }

   /** @deprecated */
   @Deprecated
   protected static KeyManagerFactory buildKeyManagerFactory(File var0, File var1, String var2, KeyManagerFactory var3) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
      String var4 = Security.getProperty("ssl.KeyManagerFactory.algorithm");
      if (var4 == null) {
         var4 = "SunX509";
      }

      return buildKeyManagerFactory(var0, var4, var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   protected static KeyManagerFactory buildKeyManagerFactory(File var0, String var1, File var2, String var3, KeyManagerFactory var4) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
      return buildKeyManagerFactory(toX509Certificates(var0), var1, toPrivateKey(var2, var3), var3, var4);
   }

   static {
      SSLContext var0;
      try {
         var0 = SSLContext.getInstance("TLS");
         var0.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
      } catch (Exception var11) {
         throw new Error("failed to initialize the default SSL context", var11);
      }

      SSLEngine var2 = var0.createSSLEngine();
      String[] var3 = var2.getSupportedProtocols();
      HashSet var4 = new HashSet(var3.length);

      int var1;
      for(var1 = 0; var1 < var3.length; ++var1) {
         var4.add(var3[var1]);
      }

      ArrayList var5 = new ArrayList();
      SslUtils.addIfSupported(var4, var5, "TLSv1.2", "TLSv1.1", "TLSv1");
      if (!var5.isEmpty()) {
         DEFAULT_PROTOCOLS = (String[])var5.toArray(new String[var5.size()]);
      } else {
         DEFAULT_PROTOCOLS = var2.getEnabledProtocols();
      }

      String[] var6 = var2.getSupportedCipherSuites();
      SUPPORTED_CIPHERS = new HashSet(var6.length);

      for(var1 = 0; var1 < var6.length; ++var1) {
         String var7 = var6[var1];
         SUPPORTED_CIPHERS.add(var7);
         if (var7.startsWith("SSL_")) {
            String var8 = "TLS_" + var7.substring("SSL_".length());

            try {
               var2.setEnabledCipherSuites(new String[]{var8});
               SUPPORTED_CIPHERS.add(var8);
            } catch (IllegalArgumentException var10) {
            }
         }
      }

      ArrayList var12 = new ArrayList();
      SslUtils.addIfSupported(SUPPORTED_CIPHERS, var12, SslUtils.DEFAULT_CIPHER_SUITES);
      SslUtils.useFallbackCiphersIfDefaultIsEmpty(var12, (String[])var2.getEnabledCipherSuites());
      DEFAULT_CIPHERS = Collections.unmodifiableList(var12);
      if (logger.isDebugEnabled()) {
         logger.debug("Default protocols (JDK): {} ", (Object)Arrays.asList(DEFAULT_PROTOCOLS));
         logger.debug("Default cipher suites (JDK): {}", (Object)DEFAULT_CIPHERS);
      }

   }
}
