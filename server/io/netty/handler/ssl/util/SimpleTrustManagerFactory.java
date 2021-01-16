package io.netty.handler.ssl.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class SimpleTrustManagerFactory extends TrustManagerFactory {
   private static final Provider PROVIDER = new Provider("", 0.0D, "") {
      private static final long serialVersionUID = -2680540247105807895L;
   };
   private static final FastThreadLocal<SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi> CURRENT_SPI = new FastThreadLocal<SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi>() {
      protected SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi initialValue() {
         return new SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi();
      }
   };

   protected SimpleTrustManagerFactory() {
      this("");
   }

   protected SimpleTrustManagerFactory(String var1) {
      super((TrustManagerFactorySpi)CURRENT_SPI.get(), PROVIDER, var1);
      ((SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi)CURRENT_SPI.get()).init(this);
      CURRENT_SPI.remove();
      if (var1 == null) {
         throw new NullPointerException("name");
      }
   }

   protected abstract void engineInit(KeyStore var1) throws Exception;

   protected abstract void engineInit(ManagerFactoryParameters var1) throws Exception;

   protected abstract TrustManager[] engineGetTrustManagers();

   static final class SimpleTrustManagerFactorySpi extends TrustManagerFactorySpi {
      private SimpleTrustManagerFactory parent;
      private volatile TrustManager[] trustManagers;

      SimpleTrustManagerFactorySpi() {
         super();
      }

      void init(SimpleTrustManagerFactory var1) {
         this.parent = var1;
      }

      protected void engineInit(KeyStore var1) throws KeyStoreException {
         try {
            this.parent.engineInit(var1);
         } catch (KeyStoreException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new KeyStoreException(var4);
         }
      }

      protected void engineInit(ManagerFactoryParameters var1) throws InvalidAlgorithmParameterException {
         try {
            this.parent.engineInit(var1);
         } catch (InvalidAlgorithmParameterException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new InvalidAlgorithmParameterException(var4);
         }
      }

      protected TrustManager[] engineGetTrustManagers() {
         TrustManager[] var1 = this.trustManagers;
         if (var1 == null) {
            var1 = this.parent.engineGetTrustManagers();
            if (PlatformDependent.javaVersion() >= 7) {
               for(int var2 = 0; var2 < var1.length; ++var2) {
                  TrustManager var3 = var1[var2];
                  if (var3 instanceof X509TrustManager && !(var3 instanceof X509ExtendedTrustManager)) {
                     var1[var2] = new X509TrustManagerWrapper((X509TrustManager)var3);
                  }
               }
            }

            this.trustManagers = var1;
         }

         return (TrustManager[])var1.clone();
      }
   }
}
