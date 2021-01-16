package io.netty.util.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;

public final class SocketUtils {
   private SocketUtils() {
      super();
   }

   public static void connect(final Socket var0, final SocketAddress var1, final int var2) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               var0.connect(var1, var2);
               return null;
            }
         });
      } catch (PrivilegedActionException var4) {
         throw (IOException)var4.getCause();
      }
   }

   public static void bind(final Socket var0, final SocketAddress var1) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               var0.bind(var1);
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public static boolean connect(final SocketChannel var0, final SocketAddress var1) throws IOException {
      try {
         return (Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
            public Boolean run() throws IOException {
               return var0.connect(var1);
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public static void bind(final SocketChannel var0, final SocketAddress var1) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               var0.bind(var1);
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public static SocketChannel accept(final ServerSocketChannel var0) throws IOException {
      try {
         return (SocketChannel)AccessController.doPrivileged(new PrivilegedExceptionAction<SocketChannel>() {
            public SocketChannel run() throws IOException {
               return var0.accept();
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (IOException)var2.getCause();
      }
   }

   public static void bind(final DatagramChannel var0, final SocketAddress var1) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               var0.bind(var1);
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public static SocketAddress localSocketAddress(final ServerSocket var0) {
      return (SocketAddress)AccessController.doPrivileged(new PrivilegedAction<SocketAddress>() {
         public SocketAddress run() {
            return var0.getLocalSocketAddress();
         }
      });
   }

   public static InetAddress addressByName(final String var0) throws UnknownHostException {
      try {
         return (InetAddress)AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>() {
            public InetAddress run() throws UnknownHostException {
               return InetAddress.getByName(var0);
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (UnknownHostException)var2.getCause();
      }
   }

   public static InetAddress[] allAddressesByName(final String var0) throws UnknownHostException {
      try {
         return (InetAddress[])AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress[]>() {
            public InetAddress[] run() throws UnknownHostException {
               return InetAddress.getAllByName(var0);
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (UnknownHostException)var2.getCause();
      }
   }

   public static InetSocketAddress socketAddress(final String var0, final int var1) {
      return (InetSocketAddress)AccessController.doPrivileged(new PrivilegedAction<InetSocketAddress>() {
         public InetSocketAddress run() {
            return new InetSocketAddress(var0, var1);
         }
      });
   }

   public static Enumeration<InetAddress> addressesFromNetworkInterface(final NetworkInterface var0) {
      return (Enumeration)AccessController.doPrivileged(new PrivilegedAction<Enumeration<InetAddress>>() {
         public Enumeration<InetAddress> run() {
            return var0.getInetAddresses();
         }
      });
   }

   public static InetAddress loopbackAddress() {
      return (InetAddress)AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
         public InetAddress run() {
            if (PlatformDependent.javaVersion() >= 7) {
               return InetAddress.getLoopbackAddress();
            } else {
               try {
                  return InetAddress.getByName((String)null);
               } catch (UnknownHostException var2) {
                  throw new IllegalStateException(var2);
               }
            }
         }
      });
   }

   public static byte[] hardwareAddressFromNetworkInterface(final NetworkInterface var0) throws SocketException {
      try {
         return (byte[])AccessController.doPrivileged(new PrivilegedExceptionAction<byte[]>() {
            public byte[] run() throws SocketException {
               return var0.getHardwareAddress();
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (SocketException)var2.getCause();
      }
   }
}
