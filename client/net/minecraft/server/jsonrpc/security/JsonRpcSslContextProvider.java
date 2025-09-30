package net.minecraft.server.jsonrpc.security;

import com.mojang.logging.LogUtils;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.slf4j.Logger;

public class JsonRpcSslContextProvider {
   private static final String PASSWORD_ENV_VARIABLE_KEY = "MINECRAFT_MANAGEMENT_TLS_KEYSTORE_PASSWORD";
   private static final String PASSWORD_SYSTEM_PROPERTY_KEY = "management.tls.keystore.password";
   private static final Logger log = LogUtils.getLogger();

   public JsonRpcSslContextProvider() {
      super();
   }

   public static SslContext createFrom(String var0, String var1) throws Exception {
      if (var0.isEmpty()) {
         throw new IllegalArgumentException("TLS is enabled but keystore is not configured");
      } else {
         File var2 = new File(var0);
         if (var2.exists() && var2.isFile()) {
            String var3 = getKeystorePassword(var1);
            return loadKeystoreFromPath(var2, var3);
         } else {
            throw new IllegalArgumentException("Supplied keystore is not a file or does not exist: '" + var0 + "'");
         }
      }
   }

   private static String getKeystorePassword(String var0) {
      String var1 = (String)System.getenv().get("MINECRAFT_MANAGEMENT_TLS_KEYSTORE_PASSWORD");
      if (var1 != null) {
         return var1;
      } else {
         String var2 = System.getProperty("management.tls.keystore.password", (String)null);
         return var2 != null ? var2 : var0;
      }
   }

   private static SslContext loadKeystoreFromPath(File var0, String var1) throws Exception {
      KeyStore var2 = KeyStore.getInstance("PKCS12");
      FileInputStream var3 = new FileInputStream(var0);

      try {
         var2.load(var3, var1.toCharArray());
      } catch (Throwable var7) {
         try {
            ((InputStream)var3).close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      ((InputStream)var3).close();
      KeyManagerFactory var8 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      var8.init(var2, var1.toCharArray());
      TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      var4.init(var2);
      return SslContextBuilder.forServer(var8).trustManager(var4).build();
   }

   public static void printInstructions() {
      log.info("To use TLS for the management server, please follow these steps:");
      log.info("1. Set the server property 'management-server-tls-enabled' to 'true' to enable TLS");
      log.info("2. Create a keystore file of type PKCS12 containing your server certificate and private key");
      log.info("3. Set the server property 'management-server-tls-keystore' to the path of your keystore file");
      log.info("4. Set the keystore password via the environment variable 'MINECRAFT_MANAGEMENT_TLS_KEYSTORE_PASSWORD', or system property 'management.tls.keystore.password', or server property 'management-server-tls-keystore-password'");
      log.info("5. Restart the server to apply the changes.");
   }
}
