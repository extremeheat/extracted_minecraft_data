package io.netty.channel.kqueue;

import io.netty.channel.DefaultFileRegion;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.ThrowableUtil;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;

final class BsdSocket extends Socket {
   private static final Errors.NativeIoException SENDFILE_CONNECTION_RESET_EXCEPTION;
   private static final ClosedChannelException SENDFILE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Native.class, "sendfile(..)");
   private static final int APPLE_SND_LOW_AT_MAX = 131072;
   private static final int FREEBSD_SND_LOW_AT_MAX = 32768;
   static final int BSD_SND_LOW_AT_MAX = Math.min(131072, 32768);

   BsdSocket(int var1) {
      super(var1);
   }

   void setAcceptFilter(AcceptFilter var1) throws IOException {
      setAcceptFilter(this.intValue(), var1.filterName(), var1.filterArgs());
   }

   void setTcpNoPush(boolean var1) throws IOException {
      setTcpNoPush(this.intValue(), var1 ? 1 : 0);
   }

   void setSndLowAt(int var1) throws IOException {
      setSndLowAt(this.intValue(), var1);
   }

   boolean isTcpNoPush() throws IOException {
      return getTcpNoPush(this.intValue()) != 0;
   }

   int getSndLowAt() throws IOException {
      return getSndLowAt(this.intValue());
   }

   AcceptFilter getAcceptFilter() throws IOException {
      String[] var1 = getAcceptFilter(this.intValue());
      return var1 == null ? AcceptFilter.PLATFORM_UNSUPPORTED : new AcceptFilter(var1[0], var1[1]);
   }

   PeerCredentials getPeerCredentials() throws IOException {
      return getPeerCredentials(this.intValue());
   }

   long sendFile(DefaultFileRegion var1, long var2, long var4, long var6) throws IOException {
      var1.open();
      long var8 = sendFile(this.intValue(), var1, var2, var4, var6);
      return var8 >= 0L ? var8 : (long)Errors.ioResult("sendfile", (int)var8, SENDFILE_CONNECTION_RESET_EXCEPTION, SENDFILE_CLOSED_CHANNEL_EXCEPTION);
   }

   public static BsdSocket newSocketStream() {
      return new BsdSocket(newSocketStream0());
   }

   public static BsdSocket newSocketDgram() {
      return new BsdSocket(newSocketDgram0());
   }

   public static BsdSocket newSocketDomain() {
      return new BsdSocket(newSocketDomain0());
   }

   private static native long sendFile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

   private static native String[] getAcceptFilter(int var0) throws IOException;

   private static native int getTcpNoPush(int var0) throws IOException;

   private static native int getSndLowAt(int var0) throws IOException;

   private static native PeerCredentials getPeerCredentials(int var0) throws IOException;

   private static native void setAcceptFilter(int var0, String var1, String var2) throws IOException;

   private static native void setTcpNoPush(int var0, int var1) throws IOException;

   private static native void setSndLowAt(int var0, int var1) throws IOException;

   static {
      SENDFILE_CONNECTION_RESET_EXCEPTION = Errors.newConnectionResetException("syscall:sendfile", Errors.ERRNO_EPIPE_NEGATIVE);
   }
}
