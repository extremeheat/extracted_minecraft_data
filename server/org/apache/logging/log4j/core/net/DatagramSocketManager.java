package org.apache.logging.log4j.core.net;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.util.Strings;

public class DatagramSocketManager extends AbstractSocketManager {
   private static final DatagramSocketManager.DatagramSocketManagerFactory FACTORY = new DatagramSocketManager.DatagramSocketManagerFactory();

   protected DatagramSocketManager(String var1, OutputStream var2, InetAddress var3, String var4, int var5, Layout<? extends Serializable> var6, int var7) {
      super(var1, var2, var3, var4, var5, var6, true, var7);
   }

   public static DatagramSocketManager getSocketManager(String var0, int var1, Layout<? extends Serializable> var2, int var3) {
      if (Strings.isEmpty(var0)) {
         throw new IllegalArgumentException("A host name is required");
      } else if (var1 <= 0) {
         throw new IllegalArgumentException("A port value is required");
      } else {
         return (DatagramSocketManager)getManager("UDP:" + var0 + ':' + var1, new DatagramSocketManager.FactoryData(var0, var1, var2, var3), FACTORY);
      }
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap(super.getContentFormat());
      var1.put("protocol", "udp");
      var1.put("direction", "out");
      return var1;
   }

   private static class DatagramSocketManagerFactory implements ManagerFactory<DatagramSocketManager, DatagramSocketManager.FactoryData> {
      private DatagramSocketManagerFactory() {
         super();
      }

      public DatagramSocketManager createManager(String var1, DatagramSocketManager.FactoryData var2) {
         InetAddress var3;
         try {
            var3 = InetAddress.getByName(var2.host);
         } catch (UnknownHostException var5) {
            DatagramSocketManager.LOGGER.error((String)("Could not find address of " + var2.host), (Throwable)var5);
            return null;
         }

         DatagramOutputStream var4 = new DatagramOutputStream(var2.host, var2.port, var2.layout.getHeader(), var2.layout.getFooter());
         return new DatagramSocketManager(var1, var4, var3, var2.host, var2.port, var2.layout, var2.bufferSize);
      }

      // $FF: synthetic method
      DatagramSocketManagerFactory(Object var1) {
         this();
      }
   }

   private static class FactoryData {
      private final String host;
      private final int port;
      private final Layout<? extends Serializable> layout;
      private final int bufferSize;

      public FactoryData(String var1, int var2, Layout<? extends Serializable> var3, int var4) {
         super();
         this.host = var1;
         this.port = var2;
         this.layout = var3;
         this.bufferSize = var4;
      }
   }
}
