package io.netty.channel.epoll;

import io.netty.channel.unix.Errors;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Locale;

public final class Native {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Native.class);
   public static final int EPOLLIN;
   public static final int EPOLLOUT;
   public static final int EPOLLRDHUP;
   public static final int EPOLLET;
   public static final int EPOLLERR;
   public static final boolean IS_SUPPORTING_SENDMMSG;
   public static final boolean IS_SUPPORTING_TCP_FASTOPEN;
   public static final int TCP_MD5SIG_MAXKEYLEN;
   public static final String KERNEL_VERSION;
   private static final Errors.NativeIoException SENDMMSG_CONNECTION_RESET_EXCEPTION;
   private static final Errors.NativeIoException SPLICE_CONNECTION_RESET_EXCEPTION;
   private static final ClosedChannelException SENDMMSG_CLOSED_CHANNEL_EXCEPTION;
   private static final ClosedChannelException SPLICE_CLOSED_CHANNEL_EXCEPTION;

   public static FileDescriptor newEventFd() {
      return new FileDescriptor(eventFd());
   }

   public static FileDescriptor newTimerFd() {
      return new FileDescriptor(timerFd());
   }

   private static native int eventFd();

   private static native int timerFd();

   public static native void eventFdWrite(int var0, long var1);

   public static native void eventFdRead(int var0);

   static native void timerFdRead(int var0);

   public static FileDescriptor newEpollCreate() {
      return new FileDescriptor(epollCreate());
   }

   private static native int epollCreate();

   public static int epollWait(FileDescriptor var0, EpollEventArray var1, FileDescriptor var2, int var3, int var4) throws IOException {
      int var5 = epollWait0(var0.intValue(), var1.memoryAddress(), var1.length(), var2.intValue(), var3, var4);
      if (var5 < 0) {
         throw Errors.newIOException("epoll_wait", var5);
      } else {
         return var5;
      }
   }

   private static native int epollWait0(int var0, long var1, int var3, int var4, int var5, int var6);

   public static void epollCtlAdd(int var0, int var1, int var2) throws IOException {
      int var3 = epollCtlAdd0(var0, var1, var2);
      if (var3 < 0) {
         throw Errors.newIOException("epoll_ctl", var3);
      }
   }

   private static native int epollCtlAdd0(int var0, int var1, int var2);

   public static void epollCtlMod(int var0, int var1, int var2) throws IOException {
      int var3 = epollCtlMod0(var0, var1, var2);
      if (var3 < 0) {
         throw Errors.newIOException("epoll_ctl", var3);
      }
   }

   private static native int epollCtlMod0(int var0, int var1, int var2);

   public static void epollCtlDel(int var0, int var1) throws IOException {
      int var2 = epollCtlDel0(var0, var1);
      if (var2 < 0) {
         throw Errors.newIOException("epoll_ctl", var2);
      }
   }

   private static native int epollCtlDel0(int var0, int var1);

   public static int splice(int var0, long var1, int var3, long var4, long var6) throws IOException {
      int var8 = splice0(var0, var1, var3, var4, var6);
      return var8 >= 0 ? var8 : Errors.ioResult("splice", var8, SPLICE_CONNECTION_RESET_EXCEPTION, SPLICE_CLOSED_CHANNEL_EXCEPTION);
   }

   private static native int splice0(int var0, long var1, int var3, long var4, long var6);

   public static int sendmmsg(int var0, NativeDatagramPacketArray.NativeDatagramPacket[] var1, int var2, int var3) throws IOException {
      int var4 = sendmmsg0(var0, var1, var2, var3);
      return var4 >= 0 ? var4 : Errors.ioResult("sendmmsg", var4, SENDMMSG_CONNECTION_RESET_EXCEPTION, SENDMMSG_CLOSED_CHANNEL_EXCEPTION);
   }

   private static native int sendmmsg0(int var0, NativeDatagramPacketArray.NativeDatagramPacket[] var1, int var2, int var3);

   public static native int sizeofEpollEvent();

   public static native int offsetofEpollData();

   private static void loadNativeLibrary() {
      String var0 = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
      if (!var0.startsWith("linux")) {
         throw new IllegalStateException("Only supported on Linux");
      } else {
         String var1 = "netty_transport_native_epoll";
         String var2 = var1 + '_' + PlatformDependent.normalizedArch();
         ClassLoader var3 = PlatformDependent.getClassLoader(Native.class);

         try {
            NativeLibraryLoader.load(var2, var3);
         } catch (UnsatisfiedLinkError var7) {
            UnsatisfiedLinkError var4 = var7;

            try {
               NativeLibraryLoader.load(var1, var3);
               logger.debug("Failed to load {}", var2, var4);
            } catch (UnsatisfiedLinkError var6) {
               ThrowableUtil.addSuppressed(var7, (Throwable)var6);
               throw var7;
            }
         }

      }
   }

   private Native() {
      super();
   }

   static {
      try {
         offsetofEpollData();
      } catch (UnsatisfiedLinkError var1) {
         loadNativeLibrary();
      }

      Socket.initialize();
      EPOLLIN = NativeStaticallyReferencedJniMethods.epollin();
      EPOLLOUT = NativeStaticallyReferencedJniMethods.epollout();
      EPOLLRDHUP = NativeStaticallyReferencedJniMethods.epollrdhup();
      EPOLLET = NativeStaticallyReferencedJniMethods.epollet();
      EPOLLERR = NativeStaticallyReferencedJniMethods.epollerr();
      IS_SUPPORTING_SENDMMSG = NativeStaticallyReferencedJniMethods.isSupportingSendmmsg();
      IS_SUPPORTING_TCP_FASTOPEN = NativeStaticallyReferencedJniMethods.isSupportingTcpFastopen();
      TCP_MD5SIG_MAXKEYLEN = NativeStaticallyReferencedJniMethods.tcpMd5SigMaxKeyLen();
      KERNEL_VERSION = NativeStaticallyReferencedJniMethods.kernelVersion();
      SENDMMSG_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Native.class, "sendmmsg(...)");
      SPLICE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Native.class, "splice(...)");
      SENDMMSG_CONNECTION_RESET_EXCEPTION = Errors.newConnectionResetException("syscall:sendmmsg(...)", Errors.ERRNO_EPIPE_NEGATIVE);
      SPLICE_CONNECTION_RESET_EXCEPTION = Errors.newConnectionResetException("syscall:splice(...)", Errors.ERRNO_EPIPE_NEGATIVE);
   }
}
