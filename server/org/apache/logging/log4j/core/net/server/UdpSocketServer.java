package org.apache.logging.log4j.core.net.server;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.util.BasicCommandLineArguments;

public class UdpSocketServer<T extends InputStream> extends AbstractSocketServer<T> {
   private final DatagramSocket datagramSocket;
   private final int maxBufferSize = 67584;

   public static UdpSocketServer<InputStream> createJsonSocketServer(int var0) throws IOException {
      return new UdpSocketServer(var0, new JsonInputStreamLogEventBridge());
   }

   public static UdpSocketServer<ObjectInputStream> createSerializedSocketServer(int var0) throws IOException {
      return new UdpSocketServer(var0, new ObjectInputStreamLogEventBridge());
   }

   public static UdpSocketServer<InputStream> createXmlSocketServer(int var0) throws IOException {
      return new UdpSocketServer(var0, new XmlInputStreamLogEventBridge());
   }

   public static void main(String[] var0) throws Exception {
      AbstractSocketServer.CommandLineArguments var1 = (AbstractSocketServer.CommandLineArguments)BasicCommandLineArguments.parseCommandLine(var0, UdpSocketServer.class, new AbstractSocketServer.CommandLineArguments());
      if (!var1.isHelp()) {
         if (var1.getConfigLocation() != null) {
            ConfigurationFactory.setConfigurationFactory(new AbstractSocketServer.ServerConfigurationFactory(var1.getConfigLocation()));
         }

         UdpSocketServer var2 = createSerializedSocketServer(var1.getPort());
         Thread var3 = var2.startNewThread();
         if (var1.isInteractive()) {
            var2.awaitTermination(var3);
         }

      }
   }

   public UdpSocketServer(int var1, LogEventBridge<T> var2) throws IOException {
      super(var1, var2);
      this.datagramSocket = new DatagramSocket(var1);
   }

   public void run() {
      while(this.isActive()) {
         if (this.datagramSocket.isClosed()) {
            return;
         }

         try {
            byte[] var1 = new byte[67584];
            DatagramPacket var2 = new DatagramPacket(var1, var1.length);
            this.datagramSocket.receive(var2);
            ByteArrayInputStream var3 = new ByteArrayInputStream(var2.getData(), var2.getOffset(), var2.getLength());
            this.logEventInput.logEvents(this.logEventInput.wrapStream(var3), this);
         } catch (OptionalDataException var4) {
            if (this.datagramSocket.isClosed()) {
               return;
            }

            this.logger.error((String)("OptionalDataException eof=" + var4.eof + " length=" + var4.length), (Throwable)var4);
         } catch (EOFException var5) {
            if (this.datagramSocket.isClosed()) {
               return;
            }

            this.logger.info("EOF encountered");
         } catch (IOException var6) {
            if (this.datagramSocket.isClosed()) {
               return;
            }

            this.logger.error((String)"Exception encountered on accept. Ignoring. Stack Trace :", (Throwable)var6);
         }
      }

   }

   public void shutdown() {
      this.setActive(false);
      Thread.currentThread().interrupt();
      this.datagramSocket.close();
   }
}
