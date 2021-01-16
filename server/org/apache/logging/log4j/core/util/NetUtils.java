package org.apache.logging.log4j.core.util;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class NetUtils {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String UNKNOWN_LOCALHOST = "UNKNOWN_LOCALHOST";

   private NetUtils() {
      super();
   }

   public static String getLocalHostname() {
      try {
         InetAddress var0 = InetAddress.getLocalHost();
         return var0.getHostName();
      } catch (UnknownHostException var7) {
         try {
            Enumeration var1 = NetworkInterface.getNetworkInterfaces();

            while(var1.hasMoreElements()) {
               NetworkInterface var2 = (NetworkInterface)var1.nextElement();
               Enumeration var3 = var2.getInetAddresses();

               while(var3.hasMoreElements()) {
                  InetAddress var4 = (InetAddress)var3.nextElement();
                  if (!var4.isLoopbackAddress()) {
                     String var5 = var4.getHostName();
                     if (var5 != null) {
                        return var5;
                     }
                  }
               }
            }
         } catch (SocketException var6) {
            LOGGER.error((String)"Could not determine local host name", (Throwable)var7);
            return "UNKNOWN_LOCALHOST";
         }

         LOGGER.error((String)"Could not determine local host name", (Throwable)var7);
         return "UNKNOWN_LOCALHOST";
      }
   }

   public static URI toURI(String var0) {
      try {
         return new URI(var0);
      } catch (URISyntaxException var4) {
         try {
            URL var2 = new URL(var0);
            return new URI(var2.getProtocol(), var2.getHost(), var2.getPath(), (String)null);
         } catch (URISyntaxException | MalformedURLException var3) {
            return (new File(var0)).toURI();
         }
      }
   }
}
