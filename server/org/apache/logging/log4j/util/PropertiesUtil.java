package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class PropertiesUtil {
   private static final PropertiesUtil LOG4J_PROPERTIES = new PropertiesUtil("log4j2.component.properties");
   private final Properties props;

   public PropertiesUtil(Properties var1) {
      super();
      this.props = var1;
   }

   public PropertiesUtil(String var1) {
      super();
      Properties var2 = new Properties();
      Iterator var3 = LoaderUtil.findResources(var1).iterator();

      while(var3.hasNext()) {
         URL var4 = (URL)var3.next();

         try {
            InputStream var5 = var4.openStream();
            Throwable var6 = null;

            try {
               var2.load(var5);
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (IOException var18) {
            LowLevelLogUtil.logException("Unable to read " + var4.toString(), var18);
         }
      }

      this.props = var2;
   }

   static Properties loadClose(InputStream var0, Object var1) {
      Properties var2 = new Properties();
      if (null != var0) {
         try {
            var2.load(var0);
         } catch (IOException var12) {
            LowLevelLogUtil.logException("Unable to read " + var1, var12);
         } finally {
            try {
               var0.close();
            } catch (IOException var11) {
               LowLevelLogUtil.logException("Unable to close " + var1, var11);
            }

         }
      }

      return var2;
   }

   public static PropertiesUtil getProperties() {
      return LOG4J_PROPERTIES;
   }

   public boolean getBooleanProperty(String var1) {
      return this.getBooleanProperty(var1, false);
   }

   public boolean getBooleanProperty(String var1, boolean var2) {
      String var3 = this.getStringProperty(var1);
      return var3 == null ? var2 : "true".equalsIgnoreCase(var3);
   }

   public Charset getCharsetProperty(String var1) {
      return this.getCharsetProperty(var1, Charset.defaultCharset());
   }

   public Charset getCharsetProperty(String var1, Charset var2) {
      String var3 = this.getStringProperty(var1);
      return var3 == null ? var2 : Charset.forName(var3);
   }

   public double getDoubleProperty(String var1, double var2) {
      String var4 = this.getStringProperty(var1);
      if (var4 != null) {
         try {
            return Double.parseDouble(var4);
         } catch (Exception var6) {
            return var2;
         }
      } else {
         return var2;
      }
   }

   public int getIntegerProperty(String var1, int var2) {
      String var3 = this.getStringProperty(var1);
      if (var3 != null) {
         try {
            return Integer.parseInt(var3);
         } catch (Exception var5) {
            return var2;
         }
      } else {
         return var2;
      }
   }

   public long getLongProperty(String var1, long var2) {
      String var4 = this.getStringProperty(var1);
      if (var4 != null) {
         try {
            return Long.parseLong(var4);
         } catch (Exception var6) {
            return var2;
         }
      } else {
         return var2;
      }
   }

   public String getStringProperty(String var1) {
      String var2 = null;

      try {
         var2 = System.getProperty(var1);
      } catch (SecurityException var4) {
      }

      return var2 == null ? this.props.getProperty(var1) : var2;
   }

   public String getStringProperty(String var1, String var2) {
      String var3 = this.getStringProperty(var1);
      return var3 == null ? var2 : var3;
   }

   public static Properties getSystemProperties() {
      try {
         return new Properties(System.getProperties());
      } catch (SecurityException var1) {
         LowLevelLogUtil.logException("Unable to access system properties.", var1);
         return new Properties();
      }
   }

   public static Properties extractSubset(Properties var0, String var1) {
      Properties var2 = new Properties();
      if (var1 != null && var1.length() != 0) {
         String var3 = var1.charAt(var1.length() - 1) != '.' ? var1 + '.' : var1;
         ArrayList var4 = new ArrayList();
         Iterator var5 = var0.stringPropertyNames().iterator();

         String var6;
         while(var5.hasNext()) {
            var6 = (String)var5.next();
            if (var6.startsWith(var3)) {
               var2.setProperty(var6.substring(var3.length()), var0.getProperty(var6));
               var4.add(var6);
            }
         }

         var5 = var4.iterator();

         while(var5.hasNext()) {
            var6 = (String)var5.next();
            var0.remove(var6);
         }

         return var2;
      } else {
         return var2;
      }
   }

   public static Map<String, Properties> partitionOnCommonPrefixes(Properties var0) {
      ConcurrentHashMap var1 = new ConcurrentHashMap();

      String var3;
      String var4;
      for(Iterator var2 = var0.stringPropertyNames().iterator(); var2.hasNext(); ((Properties)var1.get(var4)).setProperty(var3.substring(var3.indexOf(46) + 1), var0.getProperty(var3))) {
         var3 = (String)var2.next();
         var4 = var3.substring(0, var3.indexOf(46));
         if (!var1.containsKey(var4)) {
            var1.put(var4, new Properties());
         }
      }

      return var1;
   }

   public boolean isOsWindows() {
      return this.getStringProperty("os.name").startsWith("Windows");
   }
}
