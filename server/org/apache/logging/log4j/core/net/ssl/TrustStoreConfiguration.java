package org.apache.logging.log4j.core.net.ssl;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManagerFactory;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "TrustStore",
   category = "Core",
   printObject = true
)
public class TrustStoreConfiguration extends AbstractKeyStoreConfiguration {
   private final String trustManagerFactoryAlgorithm;

   public TrustStoreConfiguration(String var1, String var2, String var3, String var4) throws StoreConfigurationException {
      super(var1, var2, var3);
      this.trustManagerFactoryAlgorithm = var4 == null ? TrustManagerFactory.getDefaultAlgorithm() : var4;
   }

   @PluginFactory
   public static TrustStoreConfiguration createKeyStoreConfiguration(@PluginAttribute("location") String var0, @PluginAttribute(value = "password",sensitive = true) String var1, @PluginAttribute("type") String var2, @PluginAttribute("trustManagerFactoryAlgorithm") String var3) throws StoreConfigurationException {
      return new TrustStoreConfiguration(var0, var1, var2, var3);
   }

   public TrustManagerFactory initTrustManagerFactory() throws NoSuchAlgorithmException, KeyStoreException {
      TrustManagerFactory var1 = TrustManagerFactory.getInstance(this.trustManagerFactoryAlgorithm);
      var1.init(this.getKeyStore());
      return var1;
   }

   public int hashCode() {
      boolean var1 = true;
      int var2 = super.hashCode();
      var2 = 31 * var2 + (this.trustManagerFactoryAlgorithm == null ? 0 : this.trustManagerFactoryAlgorithm.hashCode());
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
         TrustStoreConfiguration var2 = (TrustStoreConfiguration)var1;
         if (this.trustManagerFactoryAlgorithm == null) {
            if (var2.trustManagerFactoryAlgorithm != null) {
               return false;
            }
         } else if (!this.trustManagerFactoryAlgorithm.equals(var2.trustManagerFactoryAlgorithm)) {
            return false;
         }

         return true;
      }
   }
}
