package org.apache.logging.log4j.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.util.Strings;

public class SslSocketManager extends TcpSocketManager {
   public static final int DEFAULT_PORT = 6514;
   private static final SslSocketManager.SslSocketManagerFactory FACTORY = new SslSocketManager.SslSocketManagerFactory();
   private final SslConfiguration sslConfig;

   /** @deprecated */
   public SslSocketManager(String var1, OutputStream var2, Socket var3, SslConfiguration var4, InetAddress var5, String var6, int var7, int var8, int var9, boolean var10, Layout<? extends Serializable> var11, int var12) {
      super(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11, var12, (SocketOptions)null);
      this.sslConfig = var4;
   }

   public SslSocketManager(String var1, OutputStream var2, Socket var3, SslConfiguration var4, InetAddress var5, String var6, int var7, int var8, int var9, boolean var10, Layout<? extends Serializable> var11, int var12, SocketOptions var13) {
      super(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      this.sslConfig = var4;
   }

   /** @deprecated */
   @Deprecated
   public static SslSocketManager getSocketManager(SslConfiguration var0, String var1, int var2, int var3, int var4, boolean var5, Layout<? extends Serializable> var6, int var7) {
      return getSocketManager(var0, var1, var2, var3, var4, var5, var6, var7, (SocketOptions)null);
   }

   public static SslSocketManager getSocketManager(SslConfiguration var0, String var1, int var2, int var3, int var4, boolean var5, Layout<? extends Serializable> var6, int var7, SocketOptions var8) {
      if (Strings.isEmpty(var1)) {
         throw new IllegalArgumentException("A host name is required");
      } else {
         if (var2 <= 0) {
            var2 = 6514;
         }

         if (var4 == 0) {
            var4 = 30000;
         }

         return (SslSocketManager)getManager("TLS:" + var1 + ':' + var2, new SslSocketManager.SslFactoryData(var0, var1, var2, var3, var4, var5, var6, var7, var8), FACTORY);
      }
   }

   protected Socket createSocket(String var1, int var2) throws IOException {
      SSLSocketFactory var3 = createSslSocketFactory(this.sslConfig);
      InetSocketAddress var4 = new InetSocketAddress(var1, var2);
      Socket var5 = var3.createSocket();
      var5.connect(var4, this.getConnectTimeoutMillis());
      return var5;
   }

   private static SSLSocketFactory createSslSocketFactory(SslConfiguration var0) {
      SSLSocketFactory var1;
      if (var0 != null) {
         var1 = var0.getSslSocketFactory();
      } else {
         var1 = (SSLSocketFactory)SSLSocketFactory.getDefault();
      }

      return var1;
   }

   private static class SslSocketManagerFactory implements ManagerFactory<SslSocketManager, SslSocketManager.SslFactoryData> {
      private SslSocketManagerFactory() {
         super();
      }

      public SslSocketManager createManager(String var1, SslSocketManager.SslFactoryData var2) {
         InetAddress var3 = null;
         Object var4 = null;
         Socket var5 = null;

         try {
            var3 = this.resolveAddress(var2.host);
            var5 = this.createSocket(var2);
            var4 = var5.getOutputStream();
            this.checkDelay(var2.delayMillis, (OutputStream)var4);
         } catch (IOException var7) {
            SslSocketManager.LOGGER.error((String)"SslSocketManager ({})", (Object)var1, (Object)var7);
            var4 = new ByteArrayOutputStream();
         } catch (SslSocketManager.SslSocketManagerFactory.TlsSocketManagerFactoryException var8) {
            SslSocketManager.LOGGER.catching(Level.DEBUG, var8);
            Closer.closeSilently(var5);
            return null;
         }

         return new SslSocketManager(var1, (OutputStream)var4, var5, var2.sslConfiguration, var3, var2.host, var2.port, var2.connectTimeoutMillis, var2.delayMillis, var2.immediateFail, var2.layout, var2.bufferSize, var2.socketOptions);
      }

      private InetAddress resolveAddress(String var1) throws SslSocketManager.SslSocketManagerFactory.TlsSocketManagerFactoryException {
         try {
            InetAddress var2 = InetAddress.getByName(var1);
            return var2;
         } catch (UnknownHostException var4) {
            SslSocketManager.LOGGER.error((String)"Could not find address of {}", (Object)var1, (Object)var4);
            throw new SslSocketManager.SslSocketManagerFactory.TlsSocketManagerFactoryException();
         }
      }

      private void checkDelay(int var1, OutputStream var2) throws SslSocketManager.SslSocketManagerFactory.TlsSocketManagerFactoryException {
         if (var1 == 0 && var2 == null) {
            throw new SslSocketManager.SslSocketManagerFactory.TlsSocketManagerFactoryException();
         }
      }

      private Socket createSocket(SslSocketManager.SslFactoryData var1) throws IOException {
         SSLSocketFactory var2 = SslSocketManager.createSslSocketFactory(var1.sslConfiguration);
         SSLSocket var3 = (SSLSocket)var2.createSocket();
         SocketOptions var4 = var1.socketOptions;
         if (var4 != null) {
            var4.apply(var3);
         }

         var3.connect(new InetSocketAddress(var1.host, var1.port), var1.connectTimeoutMillis);
         if (var4 != null) {
            var4.apply(var3);
         }

         return var3;
      }

      // $FF: synthetic method
      SslSocketManagerFactory(Object var1) {
         this();
      }

      private static class TlsSocketManagerFactoryException extends Exception {
         private static final long serialVersionUID = 1L;

         private TlsSocketManagerFactoryException() {
            super();
         }

         // $FF: synthetic method
         TlsSocketManagerFactoryException(Object var1) {
            this();
         }
      }
   }

   private static class SslFactoryData {
      protected SslConfiguration sslConfiguration;
      private final String host;
      private final int port;
      private final int connectTimeoutMillis;
      private final int delayMillis;
      private final boolean immediateFail;
      private final Layout<? extends Serializable> layout;
      private final int bufferSize;
      private final SocketOptions socketOptions;

      public SslFactoryData(SslConfiguration var1, String var2, int var3, int var4, int var5, boolean var6, Layout<? extends Serializable> var7, int var8, SocketOptions var9) {
         super();
         this.host = var2;
         this.port = var3;
         this.connectTimeoutMillis = var4;
         this.delayMillis = var5;
         this.immediateFail = var6;
         this.layout = var7;
         this.sslConfiguration = var1;
         this.bufferSize = var8;
         this.socketOptions = var9;
      }
   }
}
