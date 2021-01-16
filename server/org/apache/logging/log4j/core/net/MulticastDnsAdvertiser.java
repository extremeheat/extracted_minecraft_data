package org.apache.logging.log4j.core.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

@Plugin(
   name = "multicastdns",
   category = "Core",
   elementType = "advertiser",
   printObject = false
)
public class MulticastDnsAdvertiser implements Advertiser {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private static final int MAX_LENGTH = 255;
   private static final int DEFAULT_PORT = 4555;
   private static Object jmDNS = initializeJmDns();
   private static Class<?> jmDNSClass;
   private static Class<?> serviceInfoClass;

   public MulticastDnsAdvertiser() {
      super();
   }

   public Object advertise(Map<String, String> var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((String)var4.getKey()).length() <= 255 && ((String)var4.getValue()).length() <= 255) {
            var2.put(var4.getKey(), var4.getValue());
         }
      }

      String var14 = (String)var2.get("protocol");
      String var15 = "._log4j._" + (var14 != null ? var14 : "tcp") + ".local.";
      String var5 = (String)var2.get("port");
      int var6 = Integers.parseInt(var5, 4555);
      String var7 = (String)var2.get("name");
      if (jmDNS != null) {
         boolean var8 = false;

         try {
            jmDNSClass.getMethod("create");
            var8 = true;
         } catch (NoSuchMethodException var13) {
         }

         Object var9;
         if (var8) {
            var9 = buildServiceInfoVersion3(var15, var6, var7, var2);
         } else {
            var9 = buildServiceInfoVersion1(var15, var6, var7, var2);
         }

         try {
            Method var10 = jmDNSClass.getMethod("registerService", serviceInfoClass);
            var10.invoke(jmDNS, var9);
         } catch (InvocationTargetException | IllegalAccessException var11) {
            LOGGER.warn((String)"Unable to invoke registerService method", (Throwable)var11);
         } catch (NoSuchMethodException var12) {
            LOGGER.warn((String)"No registerService method", (Throwable)var12);
         }

         return var9;
      } else {
         LOGGER.warn("JMDNS not available - will not advertise ZeroConf support");
         return null;
      }
   }

   public void unadvertise(Object var1) {
      if (jmDNS != null) {
         try {
            Method var2 = jmDNSClass.getMethod("unregisterService", serviceInfoClass);
            var2.invoke(jmDNS, var1);
         } catch (InvocationTargetException | IllegalAccessException var3) {
            LOGGER.warn((String)"Unable to invoke unregisterService method", (Throwable)var3);
         } catch (NoSuchMethodException var4) {
            LOGGER.warn((String)"No unregisterService method", (Throwable)var4);
         }
      }

   }

   private static Object createJmDnsVersion1() {
      try {
         return jmDNSClass.getConstructor().newInstance();
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException var1) {
         LOGGER.warn((String)"Unable to instantiate JMDNS", (Throwable)var1);
         return null;
      }
   }

   private static Object createJmDnsVersion3() {
      try {
         Method var0 = jmDNSClass.getMethod("create");
         return var0.invoke((Object)null, (Object[])null);
      } catch (InvocationTargetException | IllegalAccessException var1) {
         LOGGER.warn((String)"Unable to invoke create method", (Throwable)var1);
      } catch (NoSuchMethodException var2) {
         LOGGER.warn((String)"Unable to get create method", (Throwable)var2);
      }

      return null;
   }

   private static Object buildServiceInfoVersion1(String var0, int var1, String var2, Map<String, String> var3) {
      Hashtable var4 = new Hashtable(var3);

      try {
         return serviceInfoClass.getConstructor(String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Hashtable.class).newInstance(var0, var2, var1, 0, 0, var4);
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException var6) {
         LOGGER.warn((String)"Unable to construct ServiceInfo instance", (Throwable)var6);
      } catch (NoSuchMethodException var7) {
         LOGGER.warn((String)"Unable to get ServiceInfo constructor", (Throwable)var7);
      }

      return null;
   }

   private static Object buildServiceInfoVersion3(String var0, int var1, String var2, Map<String, String> var3) {
      try {
         return serviceInfoClass.getMethod("create", String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Map.class).invoke((Object)null, var0, var2, var1, 0, 0, var3);
      } catch (InvocationTargetException | IllegalAccessException var5) {
         LOGGER.warn((String)"Unable to invoke create method", (Throwable)var5);
      } catch (NoSuchMethodException var6) {
         LOGGER.warn((String)"Unable to find create method", (Throwable)var6);
      }

      return null;
   }

   private static Object initializeJmDns() {
      try {
         jmDNSClass = LoaderUtil.loadClass("javax.jmdns.JmDNS");
         serviceInfoClass = LoaderUtil.loadClass("javax.jmdns.ServiceInfo");
         boolean var0 = false;

         try {
            jmDNSClass.getMethod("create");
            var0 = true;
         } catch (NoSuchMethodException var2) {
         }

         return var0 ? createJmDnsVersion3() : createJmDnsVersion1();
      } catch (ExceptionInInitializerError | ClassNotFoundException var3) {
         LOGGER.warn((String)"JmDNS or serviceInfo class not found", (Throwable)var3);
         return null;
      }
   }
}
