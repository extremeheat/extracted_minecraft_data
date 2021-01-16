package org.apache.logging.log4j.core.net.server;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEventListener;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.util.BasicCommandLineArguments;
import org.apache.logging.log4j.core.util.InetAddressConverter;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.util.Strings;

public abstract class AbstractSocketServer<T extends InputStream> extends LogEventListener implements Runnable {
   protected static final int MAX_PORT = 65534;
   private volatile boolean active = true;
   protected final LogEventBridge<T> logEventInput;
   protected final Logger logger;

   public AbstractSocketServer(int var1, LogEventBridge<T> var2) {
      super();
      this.logger = LogManager.getLogger(this.getClass().getName() + '.' + var1);
      this.logEventInput = (LogEventBridge)Objects.requireNonNull(var2, "LogEventInput");
   }

   protected boolean isActive() {
      return this.active;
   }

   protected void setActive(boolean var1) {
      this.active = var1;
   }

   public Thread startNewThread() {
      Log4jThread var1 = new Log4jThread(this);
      var1.start();
      return var1;
   }

   public abstract void shutdown() throws Exception;

   public void awaitTermination(Thread var1) throws Exception {
      BufferedReader var2 = new BufferedReader(new InputStreamReader(System.in));

      String var3;
      do {
         var3 = var2.readLine();
      } while(var3 != null && !var3.equalsIgnoreCase("quit") && !var3.equalsIgnoreCase("stop") && !var3.equalsIgnoreCase("exit"));

      this.shutdown();
      var1.join();
   }

   protected static class ServerConfigurationFactory extends XmlConfigurationFactory {
      private final String path;

      public ServerConfigurationFactory(String var1) {
         super();
         this.path = var1;
      }

      public Configuration getConfiguration(LoggerContext var1, String var2, URI var3) {
         if (Strings.isNotEmpty(this.path)) {
            File var4 = null;
            ConfigurationSource var5 = null;

            try {
               var4 = new File(this.path);
               FileInputStream var6 = new FileInputStream(var4);
               var5 = new ConfigurationSource(var6, var4);
            } catch (FileNotFoundException var9) {
            }

            if (var5 == null) {
               try {
                  URL var10 = new URL(this.path);
                  var5 = new ConfigurationSource(var10.openStream(), var10);
               } catch (IOException var8) {
               }
            }

            try {
               if (var5 != null) {
                  return new XmlConfiguration(var1, var5);
               }
            } catch (Exception var7) {
            }

            System.err.println("Unable to process configuration at " + this.path + ", using default.");
         }

         return super.getConfiguration(var1, var2, var3);
      }
   }

   protected static class CommandLineArguments extends BasicCommandLineArguments {
      @Parameter(
         names = {"--config", "-c"},
         description = "Log4j configuration file location (path or URL)."
      )
      private String configLocation;
      @Parameter(
         names = {"--interactive", "-i"},
         description = "Accepts commands on standard input (\"exit\" is the only command)."
      )
      private boolean interactive;
      @Parameter(
         names = {"--port", "-p"},
         validateWith = PositiveInteger.class,
         description = "Server socket port."
      )
      private int port;
      @Parameter(
         names = {"--localbindaddress", "-a"},
         converter = InetAddressConverter.class,
         description = "Server socket local bind address."
      )
      private InetAddress localBindAddress;

      protected CommandLineArguments() {
         super();
      }

      String getConfigLocation() {
         return this.configLocation;
      }

      int getPort() {
         return this.port;
      }

      protected boolean isInteractive() {
         return this.interactive;
      }

      void setConfigLocation(String var1) {
         this.configLocation = var1;
      }

      void setInteractive(boolean var1) {
         this.interactive = var1;
      }

      void setPort(int var1) {
         this.port = var1;
      }

      InetAddress getLocalBindAddress() {
         return this.localBindAddress;
      }

      void setLocalBindAddress(InetAddress var1) {
         this.localBindAddress = var1;
      }
   }
}
