package org.apache.logging.log4j.core.jmx;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.status.StatusLogger;

public class LoggerContextAdmin extends NotificationBroadcasterSupport implements LoggerContextAdminMBean, PropertyChangeListener {
   private static final int PAGE = 4096;
   private static final int TEXT_BUFFER = 65536;
   private static final int BUFFER_SIZE = 2048;
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private final AtomicLong sequenceNo = new AtomicLong();
   private final ObjectName objectName;
   private final LoggerContext loggerContext;

   public LoggerContextAdmin(LoggerContext var1, Executor var2) {
      super(var2, new MBeanNotificationInfo[]{createNotificationInfo()});
      this.loggerContext = (LoggerContext)Objects.requireNonNull(var1, "loggerContext");

      try {
         String var3 = Server.escape(var1.getName());
         String var4 = String.format("org.apache.logging.log4j2:type=%s", var3);
         this.objectName = new ObjectName(var4);
      } catch (Exception var5) {
         throw new IllegalStateException(var5);
      }

      var1.addPropertyChangeListener(this);
   }

   private static MBeanNotificationInfo createNotificationInfo() {
      String[] var0 = new String[]{"com.apache.logging.log4j.core.jmx.config.reconfigured"};
      String var1 = Notification.class.getName();
      String var2 = "Configuration reconfigured";
      return new MBeanNotificationInfo(var0, var1, "Configuration reconfigured");
   }

   public String getStatus() {
      return this.loggerContext.getState().toString();
   }

   public String getName() {
      return this.loggerContext.getName();
   }

   private Configuration getConfig() {
      return this.loggerContext.getConfiguration();
   }

   public String getConfigLocationUri() {
      if (this.loggerContext.getConfigLocation() != null) {
         return String.valueOf(this.loggerContext.getConfigLocation());
      } else {
         return this.getConfigName() != null ? String.valueOf((new File(this.getConfigName())).toURI()) : "";
      }
   }

   public void setConfigLocationUri(String var1) throws URISyntaxException, IOException {
      if (var1 != null && !var1.isEmpty()) {
         LOGGER.debug("---------");
         LOGGER.debug("Remote request to reconfigure using location " + var1);
         File var2 = new File(var1);
         ConfigurationSource var3 = null;
         if (var2.exists()) {
            LOGGER.debug("Opening config file {}", var2.getAbsolutePath());
            var3 = new ConfigurationSource(new FileInputStream(var2), var2);
         } else {
            URL var4 = new URL(var1);
            LOGGER.debug("Opening config URL {}", var4);
            var3 = new ConfigurationSource(var4.openStream(), var4);
         }

         Configuration var5 = ConfigurationFactory.getInstance().getConfiguration(this.loggerContext, var3);
         this.loggerContext.start(var5);
         LOGGER.debug("Completed remote request to reconfigure.");
      } else {
         throw new IllegalArgumentException("Missing configuration location");
      }
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if ("config".equals(var1.getPropertyName())) {
         Notification var2 = new Notification("com.apache.logging.log4j.core.jmx.config.reconfigured", this.getObjectName(), this.nextSeqNo(), this.now(), (String)null);
         this.sendNotification(var2);
      }
   }

   public String getConfigText() throws IOException {
      return this.getConfigText(StandardCharsets.UTF_8.name());
   }

   public String getConfigText(String var1) throws IOException {
      try {
         ConfigurationSource var2 = this.loggerContext.getConfiguration().getConfigurationSource();
         ConfigurationSource var6 = var2.resetInputStream();
         Charset var4 = Charset.forName(var1);
         return this.readContents(var6.getInputStream(), var4);
      } catch (Exception var5) {
         StringWriter var3 = new StringWriter(2048);
         var5.printStackTrace(new PrintWriter(var3));
         return var3.toString();
      }
   }

   private String readContents(InputStream var1, Charset var2) throws IOException {
      InputStreamReader var3 = null;

      try {
         var3 = new InputStreamReader(var1, var2);
         StringBuilder var4 = new StringBuilder(65536);
         char[] var5 = new char[4096];
         boolean var6 = true;

         int var11;
         while((var11 = var3.read(var5)) >= 0) {
            var4.append(var5, 0, var11);
         }

         String var7 = var4.toString();
         return var7;
      } finally {
         Closer.closeSilently(var1);
         Closer.closeSilently(var3);
      }
   }

   public void setConfigText(String var1, String var2) {
      LOGGER.debug("---------");
      LOGGER.debug("Remote request to reconfigure from config text.");

      try {
         ByteArrayInputStream var3 = new ByteArrayInputStream(var1.getBytes(var2));
         ConfigurationSource var7 = new ConfigurationSource(var3);
         Configuration var5 = ConfigurationFactory.getInstance().getConfiguration(this.loggerContext, var7);
         this.loggerContext.start(var5);
         LOGGER.debug("Completed remote request to reconfigure from config text.");
      } catch (Exception var6) {
         String var4 = "Could not reconfigure from config text";
         LOGGER.error("Could not reconfigure from config text", var6);
         throw new IllegalArgumentException("Could not reconfigure from config text", var6);
      }
   }

   public String getConfigName() {
      return this.getConfig().getName();
   }

   public String getConfigClassName() {
      return this.getConfig().getClass().getName();
   }

   public String getConfigFilter() {
      return String.valueOf(this.getConfig().getFilter());
   }

   public Map<String, String> getConfigProperties() {
      return this.getConfig().getProperties();
   }

   public ObjectName getObjectName() {
      return this.objectName;
   }

   private long nextSeqNo() {
      return this.sequenceNo.getAndIncrement();
   }

   private long now() {
      return System.currentTimeMillis();
   }
}
