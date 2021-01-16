package org.apache.logging.log4j.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.core.util.NullOutputStream;
import org.apache.logging.log4j.util.Strings;

public class TcpSocketManager extends AbstractSocketManager {
   public static final int DEFAULT_RECONNECTION_DELAY_MILLIS = 30000;
   private static final int DEFAULT_PORT = 4560;
   private static final TcpSocketManager.TcpSocketManagerFactory FACTORY = new TcpSocketManager.TcpSocketManagerFactory();
   private final int reconnectionDelay;
   private TcpSocketManager.Reconnector reconnector;
   private Socket socket;
   private final SocketOptions socketOptions;
   private final boolean retry;
   private final boolean immediateFail;
   private final int connectTimeoutMillis;

   /** @deprecated */
   @Deprecated
   public TcpSocketManager(String var1, OutputStream var2, Socket var3, InetAddress var4, String var5, int var6, int var7, int var8, boolean var9, Layout<? extends Serializable> var10, int var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, (SocketOptions)null);
   }

   public TcpSocketManager(String var1, OutputStream var2, Socket var3, InetAddress var4, String var5, int var6, int var7, int var8, boolean var9, Layout<? extends Serializable> var10, int var11, SocketOptions var12) {
      super(var1, var2, var4, var5, var6, var10, true, var11);
      this.connectTimeoutMillis = var7;
      this.reconnectionDelay = var8;
      this.socket = var3;
      this.immediateFail = var9;
      this.retry = var8 > 0;
      if (var3 == null) {
         this.reconnector = this.createReconnector();
         this.reconnector.start();
      }

      this.socketOptions = var12;
   }

   /** @deprecated */
   @Deprecated
   public static TcpSocketManager getSocketManager(String var0, int var1, int var2, int var3, boolean var4, Layout<? extends Serializable> var5, int var6) {
      return getSocketManager(var0, var1, var2, var3, var4, var5, var6, (SocketOptions)null);
   }

   public static TcpSocketManager getSocketManager(String var0, int var1, int var2, int var3, boolean var4, Layout<? extends Serializable> var5, int var6, SocketOptions var7) {
      if (Strings.isEmpty(var0)) {
         throw new IllegalArgumentException("A host name is required");
      } else {
         if (var1 <= 0) {
            var1 = 4560;
         }

         if (var3 == 0) {
            var3 = 30000;
         }

         return (TcpSocketManager)getManager("TCP:" + var0 + ':' + var1, new TcpSocketManager.FactoryData(var0, var1, var2, var3, var4, var5, var6, var7), FACTORY);
      }
   }

   protected void write(byte[] var1, int var2, int var3, boolean var4) {
      if (this.socket == null) {
         if (this.reconnector != null && !this.immediateFail) {
            this.reconnector.latch();
         }

         if (this.socket == null) {
            String var5 = "Error writing to " + this.getName() + " socket not available";
            throw new AppenderLoggingException(var5);
         }
      }

      synchronized(this) {
         try {
            OutputStream var6 = this.getOutputStream();
            var6.write(var1, var2, var3);
            if (var4) {
               var6.flush();
            }
         } catch (IOException var9) {
            if (this.retry && this.reconnector == null) {
               this.reconnector = this.createReconnector();
               this.reconnector.start();
            }

            String var7 = "Error writing to " + this.getName();
            throw new AppenderLoggingException(var7, var9);
         }

      }
   }

   protected synchronized boolean closeOutputStream() {
      boolean var1 = super.closeOutputStream();
      if (this.reconnector != null) {
         this.reconnector.shutdown();
         this.reconnector.interrupt();
         this.reconnector = null;
      }

      Socket var2 = this.socket;
      this.socket = null;
      if (var2 != null) {
         try {
            var2.close();
         } catch (IOException var4) {
            LOGGER.error((String)"Could not close socket {}", (Object)this.socket);
            return false;
         }
      }

      return var1;
   }

   public int getConnectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap(super.getContentFormat());
      var1.put("protocol", "tcp");
      var1.put("direction", "out");
      return var1;
   }

   private TcpSocketManager.Reconnector createReconnector() {
      TcpSocketManager.Reconnector var1 = new TcpSocketManager.Reconnector(this);
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   }

   protected Socket createSocket(InetAddress var1, int var2) throws IOException {
      return this.createSocket(var1.getHostName(), var2);
   }

   protected Socket createSocket(String var1, int var2) throws IOException {
      Socket var3 = new Socket();
      var3.connect(new InetSocketAddress(var1, var2), this.connectTimeoutMillis);
      if (this.socketOptions != null) {
         this.socketOptions.apply(var3);
      }

      return var3;
   }

   public SocketOptions getSocketOptions() {
      return this.socketOptions;
   }

   public Socket getSocket() {
      return this.socket;
   }

   protected static class TcpSocketManagerFactory implements ManagerFactory<TcpSocketManager, TcpSocketManager.FactoryData> {
      protected TcpSocketManagerFactory() {
         super();
      }

      public TcpSocketManager createManager(String var1, TcpSocketManager.FactoryData var2) {
         InetAddress var3;
         try {
            var3 = InetAddress.getByName(var2.host);
         } catch (UnknownHostException var7) {
            TcpSocketManager.LOGGER.error((String)("Could not find address of " + var2.host), (Object)var7, (Object)var7);
            return null;
         }

         Socket var5 = null;

         try {
            var5 = createSocket(var2);
            OutputStream var9 = var5.getOutputStream();
            return new TcpSocketManager(var1, var9, var5, var3, var2.host, var2.port, var2.connectTimeoutMillis, var2.reconnectDelayMillis, var2.immediateFail, var2.layout, var2.bufferSize, var2.socketOptions);
         } catch (IOException var8) {
            TcpSocketManager.LOGGER.error((String)("TcpSocketManager (" + var1 + ") " + var8), (Throwable)var8);
            NullOutputStream var4 = NullOutputStream.getInstance();
            if (var2.reconnectDelayMillis == 0) {
               Closer.closeSilently(var5);
               return null;
            } else {
               return new TcpSocketManager(var1, var4, (Socket)null, var3, var2.host, var2.port, var2.connectTimeoutMillis, var2.reconnectDelayMillis, var2.immediateFail, var2.layout, var2.bufferSize, var2.socketOptions);
            }
         }
      }

      static Socket createSocket(TcpSocketManager.FactoryData var0) throws IOException, SocketException {
         Socket var1 = new Socket();
         var1.connect(new InetSocketAddress(var0.host, var0.port), var0.connectTimeoutMillis);
         SocketOptions var2 = var0.socketOptions;
         if (var2 != null) {
            var2.apply(var1);
         }

         return var1;
      }
   }

   private static class FactoryData {
      private final String host;
      private final int port;
      private final int connectTimeoutMillis;
      private final int reconnectDelayMillis;
      private final boolean immediateFail;
      private final Layout<? extends Serializable> layout;
      private final int bufferSize;
      private final SocketOptions socketOptions;

      public FactoryData(String var1, int var2, int var3, int var4, boolean var5, Layout<? extends Serializable> var6, int var7, SocketOptions var8) {
         super();
         this.host = var1;
         this.port = var2;
         this.connectTimeoutMillis = var3;
         this.reconnectDelayMillis = var4;
         this.immediateFail = var5;
         this.layout = var6;
         this.bufferSize = var7;
         this.socketOptions = var8;
      }
   }

   private class Reconnector extends Log4jThread {
      private final CountDownLatch latch = new CountDownLatch(1);
      private boolean shutdown = false;
      private final Object owner;

      public Reconnector(OutputStreamManager var2) {
         super("TcpSocketManager-Reconnector");
         this.owner = var2;
      }

      public void latch() {
         try {
            this.latch.await();
         } catch (InterruptedException var2) {
         }

      }

      public void shutdown() {
         this.shutdown = true;
      }

      public void run() {
         while(!this.shutdown) {
            try {
               sleep((long)TcpSocketManager.this.reconnectionDelay);
               Socket var1 = TcpSocketManager.this.createSocket(TcpSocketManager.this.inetAddress, TcpSocketManager.this.port);
               OutputStream var2 = var1.getOutputStream();
               synchronized(this.owner) {
                  try {
                     TcpSocketManager.this.getOutputStream().close();
                  } catch (IOException var13) {
                  }

                  TcpSocketManager.this.setOutputStream(var2);
                  TcpSocketManager.this.socket = var1;
                  TcpSocketManager.this.reconnector = null;
                  this.shutdown = true;
               }

               TcpSocketManager.LOGGER.debug("Connection to " + TcpSocketManager.this.host + ':' + TcpSocketManager.this.port + " reestablished.");
            } catch (InterruptedException var15) {
               TcpSocketManager.LOGGER.debug("Reconnection interrupted.");
            } catch (ConnectException var16) {
               TcpSocketManager.LOGGER.debug(TcpSocketManager.this.host + ':' + TcpSocketManager.this.port + " refused connection");
            } catch (IOException var17) {
               TcpSocketManager.LOGGER.debug("Unable to reconnect to " + TcpSocketManager.this.host + ':' + TcpSocketManager.this.port);
            } finally {
               this.latch.countDown();
            }
         }

      }
   }
}
