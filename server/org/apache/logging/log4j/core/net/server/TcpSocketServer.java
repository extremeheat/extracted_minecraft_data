package org.apache.logging.log4j.core.net.server;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.util.BasicCommandLineArguments;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.message.EntryMessage;

public class TcpSocketServer<T extends InputStream> extends AbstractSocketServer<T> {
   private final ConcurrentMap<Long, TcpSocketServer<T>.SocketHandler> handlers;
   private final ServerSocket serverSocket;

   public static TcpSocketServer<InputStream> createJsonSocketServer(int var0) throws IOException {
      LOGGER.entry(new Object[]{"createJsonSocketServer", var0});
      TcpSocketServer var1 = new TcpSocketServer(var0, new JsonInputStreamLogEventBridge());
      return (TcpSocketServer)LOGGER.exit(var1);
   }

   public static TcpSocketServer<ObjectInputStream> createSerializedSocketServer(int var0) throws IOException {
      LOGGER.entry(new Object[]{var0});
      TcpSocketServer var1 = new TcpSocketServer(var0, new ObjectInputStreamLogEventBridge());
      return (TcpSocketServer)LOGGER.exit(var1);
   }

   public static TcpSocketServer<ObjectInputStream> createSerializedSocketServer(int var0, int var1, InetAddress var2) throws IOException {
      LOGGER.entry(new Object[]{var0});
      TcpSocketServer var3 = new TcpSocketServer(var0, var1, var2, new ObjectInputStreamLogEventBridge());
      return (TcpSocketServer)LOGGER.exit(var3);
   }

   public static TcpSocketServer<InputStream> createXmlSocketServer(int var0) throws IOException {
      LOGGER.entry(new Object[]{var0});
      TcpSocketServer var1 = new TcpSocketServer(var0, new XmlInputStreamLogEventBridge());
      return (TcpSocketServer)LOGGER.exit(var1);
   }

   public static void main(String[] var0) throws Exception {
      TcpSocketServer.CommandLineArguments var1 = (TcpSocketServer.CommandLineArguments)BasicCommandLineArguments.parseCommandLine(var0, TcpSocketServer.class, new TcpSocketServer.CommandLineArguments());
      if (!var1.isHelp()) {
         if (var1.getConfigLocation() != null) {
            ConfigurationFactory.setConfigurationFactory(new AbstractSocketServer.ServerConfigurationFactory(var1.getConfigLocation()));
         }

         TcpSocketServer var2 = createSerializedSocketServer(var1.getPort(), var1.getBacklog(), var1.getLocalBindAddress());
         Thread var3 = var2.startNewThread();
         if (var1.isInteractive()) {
            var2.awaitTermination(var3);
         }

      }
   }

   public TcpSocketServer(int var1, int var2, InetAddress var3, LogEventBridge<T> var4) throws IOException {
      this(var1, var4, new ServerSocket(var1, var2, var3));
   }

   public TcpSocketServer(int var1, LogEventBridge<T> var2) throws IOException {
      this(var1, var2, extracted(var1));
   }

   private static ServerSocket extracted(int var0) throws IOException {
      return new ServerSocket(var0);
   }

   public TcpSocketServer(int var1, LogEventBridge<T> var2, ServerSocket var3) throws IOException {
      super(var1, var2);
      this.handlers = new ConcurrentHashMap();
      this.serverSocket = var3;
   }

   public void run() {
      EntryMessage var1 = this.logger.traceEntry();

      while(this.isActive()) {
         if (this.serverSocket.isClosed()) {
            return;
         }

         try {
            this.logger.debug((String)"Listening for a connection {}...", (Object)this.serverSocket);
            Socket var2 = this.serverSocket.accept();
            this.logger.debug((String)"Acepted connection on {}...", (Object)this.serverSocket);
            this.logger.debug((String)"Socket accepted: {}", (Object)var2);
            var2.setSoLinger(true, 0);
            TcpSocketServer.SocketHandler var3 = new TcpSocketServer.SocketHandler(var2);
            this.handlers.put(var3.getId(), var3);
            var3.start();
         } catch (IOException var7) {
            if (this.serverSocket.isClosed()) {
               this.logger.traceExit(var1);
               return;
            }

            this.logger.error((String)"Exception encountered on accept. Ignoring. Stack trace :", (Throwable)var7);
         }
      }

      Iterator var8 = this.handlers.entrySet().iterator();

      while(var8.hasNext()) {
         Entry var9 = (Entry)var8.next();
         TcpSocketServer.SocketHandler var4 = (TcpSocketServer.SocketHandler)var9.getValue();
         var4.shutdown();

         try {
            var4.join();
         } catch (InterruptedException var6) {
         }
      }

      this.logger.traceExit(var1);
   }

   public void shutdown() throws IOException {
      EntryMessage var1 = this.logger.traceEntry();
      this.setActive(false);
      Thread.currentThread().interrupt();
      this.serverSocket.close();
      this.logger.traceExit(var1);
   }

   private class SocketHandler extends Log4jThread {
      private final T inputStream;
      private volatile boolean shutdown = false;

      public SocketHandler(Socket var2) throws IOException {
         super();
         this.inputStream = TcpSocketServer.this.logEventInput.wrapStream(var2.getInputStream());
      }

      public void run() {
         EntryMessage var1 = TcpSocketServer.this.logger.traceEntry();
         boolean var2 = false;

         try {
            try {
               while(!this.shutdown) {
                  TcpSocketServer.this.logEventInput.logEvents(this.inputStream, TcpSocketServer.this);
               }
            } catch (EOFException var9) {
               var2 = true;
            } catch (OptionalDataException var10) {
               TcpSocketServer.this.logger.error((String)("OptionalDataException eof=" + var10.eof + " length=" + var10.length), (Throwable)var10);
            } catch (IOException var11) {
               TcpSocketServer.this.logger.error((String)"IOException encountered while reading from socket", (Throwable)var11);
            }

            if (!var2) {
               Closer.closeSilently(this.inputStream);
            }
         } finally {
            TcpSocketServer.this.handlers.remove(this.getId());
         }

         TcpSocketServer.this.logger.traceExit(var1);
      }

      public void shutdown() {
         this.shutdown = true;
         this.interrupt();
      }
   }

   protected static class CommandLineArguments extends AbstractSocketServer.CommandLineArguments {
      @Parameter(
         names = {"--backlog", "-b"},
         validateWith = PositiveInteger.class,
         description = "Server socket backlog."
      )
      private int backlog = 50;

      protected CommandLineArguments() {
         super();
      }

      int getBacklog() {
         return this.backlog;
      }

      void setBacklog(int var1) {
         this.backlog = var1;
      }
   }
}
