package org.apache.logging.log4j.core.net.ssl;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManagerFactory;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "KeyStore",
   category = "Core",
   printObject = true
)
public class KeyStoreConfiguration extends AbstractKeyStoreConfiguration {
   private final String keyManagerFactoryAlgorithm;

   public KeyStoreConfiguration(String var1, String var2, String var3, String var4) throws StoreConfigurationException {
      super(var1, var2, var3);
      this.keyManagerFactoryAlgorithm = var4 == null ? KeyManagerFactory.getDefaultAlgorithm() : var4;
   }

   @PluginFactory
   public static KeyStoreConfiguration createKeyStoreConfiguration(@PluginAttribute("location") String var0, @PluginAttribute(value = "password",sensitive = true) String var1, @PluginAttribute("type") String var2, @PluginAttribute("keyManagerFactoryAlgorithm") String var3) throws StoreConfigurationException {
      return new KeyStoreConfiguration(var0, var1, var2, var3);
   }

   public KeyManagerFactory initKeyManagerFactory() throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
      KeyManagerFactory var1 = KeyManagerFactory.getInstance(this.keyManagerFactoryAlgorithm);
      var1.init(this.getKeyStore(), this.getPasswordAsCharArray());
      return var1;
   }

   public int hashCode() {
      boolean var1 = true;
      int var2 = super.hashCode();
      var2 = 31 * var2 + (this.keyManagerFactoryAlgorithm == null ? 0 : this.keyManagerFactoryAlgorithm.hashCode());
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
         KeyStoreConfiguration var2 = (KeyStoreConfiguration)var1;
         if (this.keyManagerFactoryAlgorithm == null) {
            if (var2.keyManagerFactoryAlgorithm != null) {
               return false;
            }
         } else if (!this.keyManagerFactoryAlgorithm.equals(var2.keyManagerFactoryAlgorithm)) {
            return false;
         }

         return true;
      }
   }
}
