package io.netty.channel.epoll;

import io.netty.channel.DefaultFileRegion;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.ThrowableUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.ClosedChannelException;

final class LinuxSocket extends Socket {
   private static final long MAX_UINT32_T = 4294967295L;
   private static final Errors.NativeIoException SENDFILE_CONNECTION_RESET_EXCEPTION;
   private static final ClosedChannelException SENDFILE_CLOSED_CHANNEL_EXCEPTION;

   public LinuxSocket(int var1) {
      super(var1);
   }

   void setTcpDeferAccept(int var1) throws IOException {
      setTcpDeferAccept(this.intValue(), var1);
   }

   void setTcpQuickAck(boolean var1) throws IOException {
      setTcpQuickAck(this.intValue(), var1 ? 1 : 0);
   }

   void setTcpCork(boolean var1) throws IOException {
      setTcpCork(this.intValue(), var1 ? 1 : 0);
   }

   void setTcpNotSentLowAt(long var1) throws IOException {
      if (var1 >= 0L && var1 <= 4294967295L) {
         setTcpNotSentLowAt(this.intValue(), (int)var1);
      } else {
         throw new IllegalArgumentException("tcpNotSentLowAt must be a uint32_t");
      }
   }

   void setTcpFastOpen(int var1) throws IOException {
      setTcpFastOpen(this.intValue(), var1);
   }

   void setTcpFastOpenConnect(boolean var1) throws IOException {
      setTcpFastOpenConnect(this.intValue(), var1 ? 1 : 0);
   }

   boolean isTcpFastOpenConnect() throws IOException {
      return isTcpFastOpenConnect(this.intValue()) != 0;
   }

   void setTcpKeepIdle(int var1) throws IOException {
      setTcpKeepIdle(this.intValue(), var1);
   }

   void setTcpKeepIntvl(int var1) throws IOException {
      setTcpKeepIntvl(this.intValue(), var1);
   }

   void setTcpKeepCnt(int var1) throws IOException {
      setTcpKeepCnt(this.intValue(), var1);
   }

   void setTcpUserTimeout(int var1) throws IOException {
      setTcpUserTimeout(this.intValue(), var1);
   }

   void setIpFreeBind(boolean var1) throws IOException {
      setIpFreeBind(this.intValue(), var1 ? 1 : 0);
   }

   void setIpTransparent(boolean var1) throws IOException {
      setIpTransparent(this.intValue(), var1 ? 1 : 0);
   }

   void setIpRecvOrigDestAddr(boolean var1) throws IOException {
      setIpRecvOrigDestAddr(this.intValue(), var1 ? 1 : 0);
   }

   void getTcpInfo(EpollTcpInfo var1) throws IOException {
      getTcpInfo(this.intValue(), var1.info);
   }

   void setTcpMd5Sig(InetAddress var1, byte[] var2) throws IOException {
      NativeInetAddress var3 = NativeInetAddress.newInstance(var1);
      setTcpMd5Sig(this.intValue(), var3.address(), var3.scopeId(), var2);
   }

   boolean isTcpCork() throws IOException {
      return isTcpCork(this.intValue()) != 0;
   }

   int getTcpDeferAccept() throws IOException {
      return getTcpDeferAccept(this.intValue());
   }

   boolean isTcpQuickAck() throws IOException {
      return isTcpQuickAck(this.intValue()) != 0;
   }

   long getTcpNotSentLowAt() throws IOException {
      return (long)getTcpNotSentLowAt(this.intValue()) & 4294967295L;
   }

   int getTcpKeepIdle() throws IOException {
      return getTcpKeepIdle(this.intValue());
   }

   int getTcpKeepIntvl() throws IOException {
      return getTcpKeepIntvl(this.intValue());
   }

   int getTcpKeepCnt() throws IOException {
      return getTcpKeepCnt(this.intValue());
   }

   int getTcpUserTimeout() throws IOException {
      return getTcpUserTimeout(this.intValue());
   }

   boolean isIpFreeBind() throws IOException {
      return isIpFreeBind(this.intValue()) != 0;
   }

   boolean isIpTransparent() throws IOException {
      return isIpTransparent(this.intValue()) != 0;
   }

   boolean isIpRecvOrigDestAddr() throws IOException {
      return isIpRecvOrigDestAddr(this.intValue()) != 0;
   }

   PeerCredentials getPeerCredentials() throws IOException {
      return getPeerCredentials(this.intValue());
   }

   long sendFile(DefaultFileRegion var1, long var2, long var4, long var6) throws IOException {
      var1.open();
      long var8 = sendFile(this.intValue(), var1, var2, var4, var6);
      return var8 >= 0L ? var8 : (long)Errors.ioResult("sendfile", (int)var8, SENDFILE_CONNECTION_RESET_EXCEPTION, SENDFILE_CLOSED_CHANNEL_EXCEPTION);
   }

   public static LinuxSocket newSocketStream() {
      return new LinuxSocket(newSocketStream0());
   }

   public static LinuxSocket newSocketDgram() {
      return new LinuxSocket(newSocketDgram0());
   }

   public static LinuxSocket newSocketDomain() {
      return new LinuxSocket(newSocketDomain0());
   }

   private static native long sendFile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

   private static native int getTcpDeferAccept(int var0) throws IOException;

   private static native int isTcpQuickAck(int var0) throws IOException;

   private static native int isTcpCork(int var0) throws IOException;

   private static native int getTcpNotSentLowAt(int var0) throws IOException;

   private static native int getTcpKeepIdle(int var0) throws IOException;

   private static native int getTcpKeepIntvl(int var0) throws IOException;

   private static native int getTcpKeepCnt(int var0) throws IOException;

   private static native int getTcpUserTimeout(int var0) throws IOException;

   private static native int isIpFreeBind(int var0) throws IOException;

   private static native int isIpTransparent(int var0) throws IOException;

   private static native int isIpRecvOrigDestAddr(int var0) throws IOException;

   private static native void getTcpInfo(int var0, long[] var1) throws IOException;

   private static native PeerCredentials getPeerCredentials(int var0) throws IOException;

   private static native int isTcpFastOpenConnect(int var0) throws IOException;

   private static native void setTcpDeferAccept(int var0, int var1) throws IOException;

   private static native void setTcpQuickAck(int var0, int var1) throws IOException;

   private static native void setTcpCork(int var0, int var1) throws IOException;

   private static native void setTcpNotSentLowAt(int var0, int var1) throws IOException;

   private static native void setTcpFastOpen(int var0, int var1) throws IOException;

   private static native void setTcpFastOpenConnect(int var0, int var1) throws IOException;

   private static native void setTcpKeepIdle(int var0, int var1) throws IOException;

   private static native void setTcpKeepIntvl(int var0, int var1) throws IOException;

   private static native void setTcpKeepCnt(int var0, int var1) throws IOException;

   private static native void setTcpUserTimeout(int var0, int var1) throws IOException;

   private static native void setIpFreeBind(int var0, int var1) throws IOException;

   private static native void setIpTransparent(int var0, int var1) throws IOException;

   private static native void setIpRecvOrigDestAddr(int var0, int var1) throws IOException;

   private static native void setTcpMd5Sig(int var0, byte[] var1, int var2, byte[] var3) throws IOException;

   static {
      SENDFILE_CONNECTION_RESET_EXCEPTION = Errors.newConnectionResetException("syscall:sendfile(...)", Errors.ERRNO_EPIPE_NEGATIVE);
      SENDFILE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Native.class, "sendfile(...)");
   }
}
