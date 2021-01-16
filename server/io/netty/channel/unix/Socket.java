package io.netty.channel.unix;

import io.netty.channel.ChannelException;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.internal.ThrowableUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Socket extends FileDescriptor {
   private static final ClosedChannelException SHUTDOWN_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Socket.class, "shutdown(..)");
   private static final ClosedChannelException SEND_TO_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Socket.class, "sendTo(..)");
   private static final ClosedChannelException SEND_TO_ADDRESS_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Socket.class, "sendToAddress(..)");
   private static final ClosedChannelException SEND_TO_ADDRESSES_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Socket.class, "sendToAddresses(..)");
   private static final Errors.NativeIoException SEND_TO_CONNECTION_RESET_EXCEPTION;
   private static final Errors.NativeIoException SEND_TO_ADDRESS_CONNECTION_RESET_EXCEPTION;
   private static final Errors.NativeIoException CONNECTION_RESET_EXCEPTION_SENDMSG;
   private static final Errors.NativeIoException CONNECTION_RESET_SHUTDOWN_EXCEPTION;
   private static final Errors.NativeConnectException FINISH_CONNECT_REFUSED_EXCEPTION;
   private static final Errors.NativeConnectException CONNECT_REFUSED_EXCEPTION;
   public static final int UDS_SUN_PATH_SIZE;
   private static final AtomicBoolean INITIALIZED;

   public Socket(int var1) {
      super(var1);
   }

   public final void shutdown() throws IOException {
      this.shutdown(true, true);
   }

   public final void shutdown(boolean var1, boolean var2) throws IOException {
      int var3;
      int var4;
      do {
         var3 = this.state;
         if (isClosed(var3)) {
            throw new ClosedChannelException();
         }

         var4 = var3;
         if (var1 && !isInputShutdown(var3)) {
            var4 = inputShutdown(var3);
         }

         if (var2 && !isOutputShutdown(var4)) {
            var4 = outputShutdown(var4);
         }

         if (var4 == var3) {
            return;
         }
      } while(!this.casState(var3, var4));

      var3 = shutdown(this.fd, var1, var2);
      if (var3 < 0) {
         Errors.ioResult("shutdown", var3, CONNECTION_RESET_SHUTDOWN_EXCEPTION, SHUTDOWN_CLOSED_CHANNEL_EXCEPTION);
      }

   }

   public final boolean isShutdown() {
      int var1 = this.state;
      return isInputShutdown(var1) && isOutputShutdown(var1);
   }

   public final boolean isInputShutdown() {
      return isInputShutdown(this.state);
   }

   public final boolean isOutputShutdown() {
      return isOutputShutdown(this.state);
   }

   public final int sendTo(ByteBuffer var1, int var2, int var3, InetAddress var4, int var5) throws IOException {
      byte[] var6;
      int var7;
      if (var4 instanceof Inet6Address) {
         var6 = var4.getAddress();
         var7 = ((Inet6Address)var4).getScopeId();
      } else {
         var7 = 0;
         var6 = NativeInetAddress.ipv4MappedIpv6Address(var4.getAddress());
      }

      int var8 = sendTo(this.fd, var1, var2, var3, var6, var7, var5);
      if (var8 >= 0) {
         return var8;
      } else if (var8 == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendTo failed");
      } else {
         return Errors.ioResult("sendTo", var8, SEND_TO_CONNECTION_RESET_EXCEPTION, SEND_TO_CLOSED_CHANNEL_EXCEPTION);
      }
   }

   public final int sendToAddress(long var1, int var3, int var4, InetAddress var5, int var6) throws IOException {
      byte[] var7;
      int var8;
      if (var5 instanceof Inet6Address) {
         var7 = var5.getAddress();
         var8 = ((Inet6Address)var5).getScopeId();
      } else {
         var8 = 0;
         var7 = NativeInetAddress.ipv4MappedIpv6Address(var5.getAddress());
      }

      int var9 = sendToAddress(this.fd, var1, var3, var4, var7, var8, var6);
      if (var9 >= 0) {
         return var9;
      } else if (var9 == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendToAddress failed");
      } else {
         return Errors.ioResult("sendToAddress", var9, SEND_TO_ADDRESS_CONNECTION_RESET_EXCEPTION, SEND_TO_ADDRESS_CLOSED_CHANNEL_EXCEPTION);
      }
   }

   public final int sendToAddresses(long var1, int var3, InetAddress var4, int var5) throws IOException {
      byte[] var6;
      int var7;
      if (var4 instanceof Inet6Address) {
         var6 = var4.getAddress();
         var7 = ((Inet6Address)var4).getScopeId();
      } else {
         var7 = 0;
         var6 = NativeInetAddress.ipv4MappedIpv6Address(var4.getAddress());
      }

      int var8 = sendToAddresses(this.fd, var1, var3, var6, var7, var5);
      if (var8 >= 0) {
         return var8;
      } else if (var8 == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendToAddresses failed");
      } else {
         return Errors.ioResult("sendToAddresses", var8, CONNECTION_RESET_EXCEPTION_SENDMSG, SEND_TO_ADDRESSES_CLOSED_CHANNEL_EXCEPTION);
      }
   }

   public final DatagramSocketAddress recvFrom(ByteBuffer var1, int var2, int var3) throws IOException {
      return recvFrom(this.fd, var1, var2, var3);
   }

   public final DatagramSocketAddress recvFromAddress(long var1, int var3, int var4) throws IOException {
      return recvFromAddress(this.fd, var1, var3, var4);
   }

   public final int recvFd() throws IOException {
      int var1 = recvFd(this.fd);
      if (var1 > 0) {
         return var1;
      } else if (var1 == 0) {
         return -1;
      } else if (var1 != Errors.ERRNO_EAGAIN_NEGATIVE && var1 != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("recvFd", var1);
      } else {
         return 0;
      }
   }

   public final int sendFd(int var1) throws IOException {
      int var2 = sendFd(this.fd, var1);
      if (var2 >= 0) {
         return var2;
      } else if (var2 != Errors.ERRNO_EAGAIN_NEGATIVE && var2 != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("sendFd", var2);
      } else {
         return -1;
      }
   }

   public final boolean connect(SocketAddress var1) throws IOException {
      int var2;
      if (var1 instanceof InetSocketAddress) {
         InetSocketAddress var3 = (InetSocketAddress)var1;
         NativeInetAddress var4 = NativeInetAddress.newInstance(var3.getAddress());
         var2 = connect(this.fd, var4.address, var4.scopeId, var3.getPort());
      } else {
         if (!(var1 instanceof DomainSocketAddress)) {
            throw new Error("Unexpected SocketAddress implementation " + var1);
         }

         DomainSocketAddress var5 = (DomainSocketAddress)var1;
         var2 = connectDomainSocket(this.fd, var5.path().getBytes(CharsetUtil.UTF_8));
      }

      if (var2 < 0) {
         if (var2 == Errors.ERRNO_EINPROGRESS_NEGATIVE) {
            return false;
         }

         Errors.throwConnectException("connect", CONNECT_REFUSED_EXCEPTION, var2);
      }

      return true;
   }

   public final boolean finishConnect() throws IOException {
      int var1 = finishConnect(this.fd);
      if (var1 < 0) {
         if (var1 == Errors.ERRNO_EINPROGRESS_NEGATIVE) {
            return false;
         }

         Errors.throwConnectException("finishConnect", FINISH_CONNECT_REFUSED_EXCEPTION, var1);
      }

      return true;
   }

   public final void disconnect() throws IOException {
      int var1 = disconnect(this.fd);
      if (var1 < 0) {
         Errors.throwConnectException("disconnect", FINISH_CONNECT_REFUSED_EXCEPTION, var1);
      }

   }

   public final void bind(SocketAddress var1) throws IOException {
      if (var1 instanceof InetSocketAddress) {
         InetSocketAddress var2 = (InetSocketAddress)var1;
         NativeInetAddress var3 = NativeInetAddress.newInstance(var2.getAddress());
         int var4 = bind(this.fd, var3.address, var3.scopeId, var2.getPort());
         if (var4 < 0) {
            throw Errors.newIOException("bind", var4);
         }
      } else {
         if (!(var1 instanceof DomainSocketAddress)) {
            throw new Error("Unexpected SocketAddress implementation " + var1);
         }

         DomainSocketAddress var5 = (DomainSocketAddress)var1;
         int var6 = bindDomainSocket(this.fd, var5.path().getBytes(CharsetUtil.UTF_8));
         if (var6 < 0) {
            throw Errors.newIOException("bind", var6);
         }
      }

   }

   public final void listen(int var1) throws IOException {
      int var2 = listen(this.fd, var1);
      if (var2 < 0) {
         throw Errors.newIOException("listen", var2);
      }
   }

   public final int accept(byte[] var1) throws IOException {
      int var2 = accept(this.fd, var1);
      if (var2 >= 0) {
         return var2;
      } else if (var2 != Errors.ERRNO_EAGAIN_NEGATIVE && var2 != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("accept", var2);
      } else {
         return -1;
      }
   }

   public final InetSocketAddress remoteAddress() {
      byte[] var1 = remoteAddress(this.fd);
      return var1 == null ? null : NativeInetAddress.address(var1, 0, var1.length);
   }

   public final InetSocketAddress localAddress() {
      byte[] var1 = localAddress(this.fd);
      return var1 == null ? null : NativeInetAddress.address(var1, 0, var1.length);
   }

   public final int getReceiveBufferSize() throws IOException {
      return getReceiveBufferSize(this.fd);
   }

   public final int getSendBufferSize() throws IOException {
      return getSendBufferSize(this.fd);
   }

   public final boolean isKeepAlive() throws IOException {
      return isKeepAlive(this.fd) != 0;
   }

   public final boolean isTcpNoDelay() throws IOException {
      return isTcpNoDelay(this.fd) != 0;
   }

   public final boolean isReuseAddress() throws IOException {
      return isReuseAddress(this.fd) != 0;
   }

   public final boolean isReusePort() throws IOException {
      return isReusePort(this.fd) != 0;
   }

   public final boolean isBroadcast() throws IOException {
      return isBroadcast(this.fd) != 0;
   }

   public final int getSoLinger() throws IOException {
      return getSoLinger(this.fd);
   }

   public final int getSoError() throws IOException {
      return getSoError(this.fd);
   }

   public final int getTrafficClass() throws IOException {
      return getTrafficClass(this.fd);
   }

   public final void setKeepAlive(boolean var1) throws IOException {
      setKeepAlive(this.fd, var1 ? 1 : 0);
   }

   public final void setReceiveBufferSize(int var1) throws IOException {
      setReceiveBufferSize(this.fd, var1);
   }

   public final void setSendBufferSize(int var1) throws IOException {
      setSendBufferSize(this.fd, var1);
   }

   public final void setTcpNoDelay(boolean var1) throws IOException {
      setTcpNoDelay(this.fd, var1 ? 1 : 0);
   }

   public final void setSoLinger(int var1) throws IOException {
      setSoLinger(this.fd, var1);
   }

   public final void setReuseAddress(boolean var1) throws IOException {
      setReuseAddress(this.fd, var1 ? 1 : 0);
   }

   public final void setReusePort(boolean var1) throws IOException {
      setReusePort(this.fd, var1 ? 1 : 0);
   }

   public final void setBroadcast(boolean var1) throws IOException {
      setBroadcast(this.fd, var1 ? 1 : 0);
   }

   public final void setTrafficClass(int var1) throws IOException {
      setTrafficClass(this.fd, var1);
   }

   public String toString() {
      return "Socket{fd=" + this.fd + '}';
   }

   public static Socket newSocketStream() {
      return new Socket(newSocketStream0());
   }

   public static Socket newSocketDgram() {
      return new Socket(newSocketDgram0());
   }

   public static Socket newSocketDomain() {
      return new Socket(newSocketDomain0());
   }

   public static void initialize() {
      if (INITIALIZED.compareAndSet(false, true)) {
         initialize(NetUtil.isIpV4StackPreferred());
      }

   }

   protected static int newSocketStream0() {
      int var0 = newSocketStreamFd();
      if (var0 < 0) {
         throw new ChannelException(Errors.newIOException("newSocketStream", var0));
      } else {
         return var0;
      }
   }

   protected static int newSocketDgram0() {
      int var0 = newSocketDgramFd();
      if (var0 < 0) {
         throw new ChannelException(Errors.newIOException("newSocketDgram", var0));
      } else {
         return var0;
      }
   }

   protected static int newSocketDomain0() {
      int var0 = newSocketDomainFd();
      if (var0 < 0) {
         throw new ChannelException(Errors.newIOException("newSocketDomain", var0));
      } else {
         return var0;
      }
   }

   private static native int shutdown(int var0, boolean var1, boolean var2);

   private static native int connect(int var0, byte[] var1, int var2, int var3);

   private static native int connectDomainSocket(int var0, byte[] var1);

   private static native int finishConnect(int var0);

   private static native int disconnect(int var0);

   private static native int bind(int var0, byte[] var1, int var2, int var3);

   private static native int bindDomainSocket(int var0, byte[] var1);

   private static native int listen(int var0, int var1);

   private static native int accept(int var0, byte[] var1);

   private static native byte[] remoteAddress(int var0);

   private static native byte[] localAddress(int var0);

   private static native int sendTo(int var0, ByteBuffer var1, int var2, int var3, byte[] var4, int var5, int var6);

   private static native int sendToAddress(int var0, long var1, int var3, int var4, byte[] var5, int var6, int var7);

   private static native int sendToAddresses(int var0, long var1, int var3, byte[] var4, int var5, int var6);

   private static native DatagramSocketAddress recvFrom(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

   private static native DatagramSocketAddress recvFromAddress(int var0, long var1, int var3, int var4) throws IOException;

   private static native int recvFd(int var0);

   private static native int sendFd(int var0, int var1);

   private static native int newSocketStreamFd();

   private static native int newSocketDgramFd();

   private static native int newSocketDomainFd();

   private static native int isReuseAddress(int var0) throws IOException;

   private static native int isReusePort(int var0) throws IOException;

   private static native int getReceiveBufferSize(int var0) throws IOException;

   private static native int getSendBufferSize(int var0) throws IOException;

   private static native int isKeepAlive(int var0) throws IOException;

   private static native int isTcpNoDelay(int var0) throws IOException;

   private static native int isBroadcast(int var0) throws IOException;

   private static native int getSoLinger(int var0) throws IOException;

   private static native int getSoError(int var0) throws IOException;

   private static native int getTrafficClass(int var0) throws IOException;

   private static native void setReuseAddress(int var0, int var1) throws IOException;

   private static native void setReusePort(int var0, int var1) throws IOException;

   private static native void setKeepAlive(int var0, int var1) throws IOException;

   private static native void setReceiveBufferSize(int var0, int var1) throws IOException;

   private static native void setSendBufferSize(int var0, int var1) throws IOException;

   private static native void setTcpNoDelay(int var0, int var1) throws IOException;

   private static native void setSoLinger(int var0, int var1) throws IOException;

   private static native void setBroadcast(int var0, int var1) throws IOException;

   private static native void setTrafficClass(int var0, int var1) throws IOException;

   private static native void initialize(boolean var0);

   static {
      SEND_TO_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:sendto", Errors.ERRNO_EPIPE_NEGATIVE), Socket.class, "sendTo(..)");
      SEND_TO_ADDRESS_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:sendto", Errors.ERRNO_EPIPE_NEGATIVE), Socket.class, "sendToAddress");
      CONNECTION_RESET_EXCEPTION_SENDMSG = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:sendmsg", Errors.ERRNO_EPIPE_NEGATIVE), Socket.class, "sendToAddresses(..)");
      CONNECTION_RESET_SHUTDOWN_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:shutdown", Errors.ERRNO_ECONNRESET_NEGATIVE), Socket.class, "shutdown");
      FINISH_CONNECT_REFUSED_EXCEPTION = (Errors.NativeConnectException)ThrowableUtil.unknownStackTrace(new Errors.NativeConnectException("syscall:getsockopt", Errors.ERROR_ECONNREFUSED_NEGATIVE), Socket.class, "finishConnect(..)");
      CONNECT_REFUSED_EXCEPTION = (Errors.NativeConnectException)ThrowableUtil.unknownStackTrace(new Errors.NativeConnectException("syscall:connect", Errors.ERROR_ECONNREFUSED_NEGATIVE), Socket.class, "connect(..)");
      UDS_SUN_PATH_SIZE = LimitsStaticallyReferencedJniMethods.udsSunPathSize();
      INITIALIZED = new AtomicBoolean();
   }
}
