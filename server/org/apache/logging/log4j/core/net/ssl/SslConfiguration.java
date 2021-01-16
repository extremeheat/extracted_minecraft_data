package org.apache.logging.log4j.core.net.ssl;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "Ssl",
   category = "Core",
   printObject = true
)
public class SslConfiguration {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private final KeyStoreConfiguration keyStoreConfig;
   private final TrustStoreConfiguration trustStoreConfig;
   private final SSLContext sslContext;
   private final String protocol;

   private SslConfiguration(String var1, KeyStoreConfiguration var2, TrustStoreConfiguration var3) {
      super();
      this.keyStoreConfig = var2;
      this.trustStoreConfig = var3;
      this.protocol = var1 == null ? "SSL" : var1;
      this.sslContext = this.createSslContext();
   }

   public SSLSocketFactory getSslSocketFactory() {
      return this.sslContext.getSocketFactory();
   }

   public SSLServerSocketFactory getSslServerSocketFactory() {
      return this.sslContext.getServerSocketFactory();
   }

   private SSLContext createSslContext() {
      SSLContext var1 = null;

      try {
         var1 = this.createSslContextBasedOnConfiguration();
         LOGGER.debug("Creating SSLContext with the given parameters");
      } catch (TrustStoreConfigurationException var3) {
         var1 = this.createSslContextWithTrustStoreFailure();
      } catch (KeyStoreConfigurationException var4) {
         var1 = this.createSslContextWithKeyStoreFailure();
      }

      return var1;
   }

   private SSLContext createSslContextWithTrustStoreFailure() {
      SSLContext var1;
      try {
         var1 = this.createSslContextWithDefaultTrustManagerFactory();
         LOGGER.debug("Creating SSLContext with default truststore");
      } catch (KeyStoreConfigurationException var3) {
         var1 = this.createDefaultSslContext();
         LOGGER.debug("Creating SSLContext with default configuration");
      }

      return var1;
   }

   private SSLContext createSslContextWithKeyStoreFailure() {
      SSLContext var1;
      try {
         var1 = this.createSslContextWithDefaultKeyManagerFactory();
         LOGGER.debug("Creating SSLContext with default keystore");
      } catch (TrustStoreConfigurationException var3) {
         var1 = this.createDefaultSslContext();
         LOGGER.debug("Creating SSLContext with default configuration");
      }

      return var1;
   }

   private SSLContext createSslContextBasedOnConfiguration() throws KeyStoreConfigurationException, TrustStoreConfigurationException {
      return this.createSslContext(false, false);
   }

   private SSLContext createSslContextWithDefaultKeyManagerFactory() throws TrustStoreConfigurationException {
      try {
         return this.createSslContext(true, false);
      } catch (KeyStoreConfigurationException var2) {
         LOGGER.debug("Exception occured while using default keystore. This should be a BUG");
         return null;
      }
   }

   private SSLContext createSslContextWithDefaultTrustManagerFactory() throws KeyStoreConfigurationException {
      try {
         return this.createSslContext(false, true);
      } catch (TrustStoreConfigurationException var2) {
         LOGGER.debug("Exception occured while using default truststore. This should be a BUG");
         return null;
      }
   }

   private SSLContext createDefaultSslContext() {
      try {
         return SSLContext.getDefault();
      } catch (NoSuchAlgorithmException var2) {
         LOGGER.error("Failed to create an SSLContext with default configuration", var2);
         return null;
      }
   }

   private SSLContext createSslContext(boolean var1, boolean var2) throws KeyStoreConfigurationException, TrustStoreConfigurationException {
      try {
         KeyManager[] var3 = null;
         TrustManager[] var4 = null;
         SSLContext var5 = SSLContext.getInstance(this.protocol);
         if (!var1) {
            KeyManagerFactory var6 = this.loadKeyManagerFactory();
            var3 = var6.getKeyManagers();
         }

         if (!var2) {
            TrustManagerFactory var9 = this.loadTrustManagerFactory();
            var4 = var9.getTrustManagers();
         }

         var5.init(var3, var4, (SecureRandom)null);
         return var5;
      } catch (NoSuchAlgorithmException var7) {
         LOGGER.error("No Provider supports a TrustManagerFactorySpi implementation for the specified protocol", var7);
         throw new TrustStoreConfigurationException(var7);
      } catch (KeyManagementException var8) {
         LOGGER.error("Failed to initialize the SSLContext", var8);
         throw new KeyStoreConfigurationException(var8);
      }
   }

   private TrustManagerFactory loadTrustManagerFactory() throws TrustStoreConfigurationException {
      if (this.trustStoreConfig == null) {
         throw new TrustStoreConfigurationException(new Exception("The trustStoreConfiguration is null"));
      } else {
         try {
            return this.trustStoreConfig.initTrustManagerFactory();
         } catch (NoSuchAlgorithmException var2) {
            LOGGER.error("The specified algorithm is not available from the specified provider", var2);
            throw new TrustStoreConfigurationException(var2);
         } catch (KeyStoreException var3) {
            LOGGER.error("Failed to initialize the TrustManagerFactory", var3);
            throw new TrustStoreConfigurationException(var3);
         }
      }
   }

   private KeyManagerFactory loadKeyManagerFactory() throws KeyStoreConfigurationException {
      if (this.keyStoreConfig == null) {
         throw new KeyStoreConfigurationException(new Exception("The keyStoreConfiguration is null"));
      } else {
         try {
            return this.keyStoreConfig.initKeyManagerFactory();
         } catch (NoSuchAlgorithmException var2) {
            LOGGER.error("The specified algorithm is not available from the specified provider", var2);
            throw new KeyStoreConfigurationException(var2);
         } catch (KeyStoreException var3) {
            LOGGER.error("Failed to initialize the TrustManagerFactory", var3);
            throw new KeyStoreConfigurationException(var3);
         } catch (UnrecoverableKeyException var4) {
            LOGGER.error("The key cannot be recovered (e.g. the given password is wrong)", var4);
            throw new KeyStoreConfigurationException(var4);
         }
      }
   }

   @PluginFactory
   public static SslConfiguration createSSLConfiguration(@PluginAttribute("protocol") String var0, @PluginElement("KeyStore") KeyStoreConfiguration var1, @PluginElement("TrustStore") TrustStoreConfiguration var2) {
      return new SslConfiguration(var0, var1, var2);
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.keyStoreConfig == null ? 0 : this.keyStoreConfig.hashCode());
      var3 = 31 * var3 + (this.protocol == null ? 0 : this.protocol.hashCode());
      var3 = 31 * var3 + (this.sslContext == null ? 0 : this.sslContext.hashCode());
      var3 = 31 * var3 + (this.trustStoreConfig == null ? 0 : this.trustStoreConfig.hashCode());
      return var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         SslConfiguration var2 = (SslConfiguration)var1;
         if (this.keyStoreConfig == null) {
            if (var2.keyStoreConfig != null) {
               return false;
            }
         } else if (!this.keyStoreConfig.equals(var2.keyStoreConfig)) {
            return false;
         }

         if (this.protocol == null) {
            if (var2.protocol != null) {
               return false;
            }
         } else if (!this.protocol.equals(var2.protocol)) {
            return false;
         }

         if (this.sslContext == null) {
            if (var2.sslContext != null) {
               return false;
            }
         } else if (!this.sslContext.equals(var2.sslContext)) {
            return false;
         }

         if (this.trustStoreConfig == null) {
            if (var2.trustStoreConfig != null) {
               return false;
            }
         } else if (!this.trustStoreConfig.equals(var2.trustStoreConfig)) {
            return false;
         }

         return true;
      }
   }
}
