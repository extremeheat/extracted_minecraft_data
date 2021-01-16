package org.apache.logging.log4j.core.net.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class AbstractKeyStoreConfiguration extends StoreConfiguration<KeyStore> {
   private final KeyStore keyStore;
   private final String keyStoreType;

   public AbstractKeyStoreConfiguration(String var1, String var2, String var3) throws StoreConfigurationException {
      super(var1, var2);
      this.keyStoreType = var3 == null ? "JKS" : var3;
      this.keyStore = this.load();
   }

   protected KeyStore load() throws StoreConfigurationException {
      LOGGER.debug("Loading keystore from file with params(location={})", this.getLocation());

      try {
         if (this.getLocation() == null) {
            throw new IOException("The location is null");
         } else {
            FileInputStream var1 = new FileInputStream(this.getLocation());
            Throwable var2 = null;

            KeyStore var4;
            try {
               KeyStore var3 = KeyStore.getInstance(this.keyStoreType);
               var3.load(var1, this.getPasswordAsCharArray());
               LOGGER.debug("Keystore successfully loaded with params(location={})", this.getLocation());
               var4 = var3;
            } catch (Throwable var18) {
               var2 = var18;
               throw var18;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var17) {
                        var2.addSuppressed(var17);
                     }
                  } else {
                     var1.close();
                  }
               }

            }

            return var4;
         }
      } catch (CertificateException var20) {
         LOGGER.error("No Provider supports a KeyStoreSpi implementation for the specified type" + this.keyStoreType, var20);
         throw new StoreConfigurationException(var20);
      } catch (NoSuchAlgorithmException var21) {
         LOGGER.error("The algorithm used to check the integrity of the keystore cannot be found", var21);
         throw new StoreConfigurationException(var21);
      } catch (KeyStoreException var22) {
         LOGGER.error(var22);
         throw new StoreConfigurationException(var22);
      } catch (FileNotFoundException var23) {
         LOGGER.error("The keystore file(" + this.getLocation() + ") is not found", var23);
         throw new StoreConfigurationException(var23);
      } catch (IOException var24) {
         LOGGER.error("Something is wrong with the format of the keystore or the given password", var24);
         throw new StoreConfigurationException(var24);
      }
   }

   public KeyStore getKeyStore() {
      return this.keyStore;
   }

   public int hashCode() {
      boolean var1 = true;
      int var2 = super.hashCode();
      var2 = 31 * var2 + (this.keyStore == null ? 0 : this.keyStore.hashCode());
      var2 = 31 * var2 + (this.keyStoreType == null ? 0 : this.keyStoreType.hashCode());
      return var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!super.equals(var1)) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         AbstractKeyStoreConfiguration var2 = (AbstractKeyStoreConfiguration)var1;
         if (this.keyStore == null) {
            if (var2.keyStore != null) {
               return false;
            }
         } else if (!this.keyStore.equals(var2.keyStore)) {
            return false;
         }

         if (this.keyStoreType == null) {
            if (var2.keyStoreType != null) {
               return false;
            }
         } else if (!this.keyStoreType.equals(var2.keyStoreType)) {
            return false;
         }

         return true;
      }
   }
}
