package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.oio.OioByteStreamChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class OioSocketChannel extends OioByteStreamChannel implements SocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSocketChannel.class);
   private final Socket socket;
   private final OioSocketChannelConfig config;

   public OioSocketChannel() {
      this(new Socket());
   }

   public OioSocketChannel(Socket var1) {
      this((Channel)null, var1);
   }

   public OioSocketChannel(Channel var1, Socket var2) {
      super(var1);
      this.socket = var2;
      this.config = new DefaultOioSocketChannelConfig(this, var2);
      boolean var3 = false;

      try {
         if (var2.isConnected()) {
            this.activate(var2.getInputStream(), var2.getOutputStream());
         }

         var2.setSoTimeout(1000);
         var3 = true;
      } catch (Exception var12) {
         throw new ChannelException("failed to initialize a socket", var12);
      } finally {
         if (!var3) {
            try {
               var2.close();
            } catch (IOException var11) {
               logger.warn("Failed to close a socket.", (Throwable)var11);
            }
         }

      }

   }

   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   public OioSocketChannelConfig config() {
      return this.config;
   }

   public boolean isOpen() {
      return !this.socket.isClosed();
   }

   public boolean isActive() {
      return !this.socket.isClosed() && this.socket.isConnected();
   }

   public boolean isOutputShutdown() {
      return this.socket.isOutputShutdown() || !this.isActive();
   }

   public boolean isInputShutdown() {
      return this.socket.isInputShutdown() || !this.isActive();
   }

   public boolean isShutdown() {
      return this.socket.isInputShutdown() && this.socket.isOutputShutdown() || !this.isActive();
   }

   protected final void doShutdownOutput() throws Exception {
      this.shutdownOutput0();
   }

   public ChannelFuture shutdownOutput() {
      return this.shutdownOutput(this.newPromise());
   }

   public ChannelFuture shutdownInput() {
      return this.shutdownInput(this.newPromise());
   }

   public ChannelFuture shutdown() {
      return this.shutdown(this.newPromise());
   }

   protected int doReadBytes(ByteBuf var1) throws Exception {
      if (this.socket.isClosed()) {
         return -1;
      } else {
         try {
            return super.doReadBytes(var1);
         } catch (SocketTimeoutException var3) {
            return 0;
         }
      }
   }

   public ChannelFuture shutdownOutput(final ChannelPromise var1) {
      EventLoop var2 = this.eventLoop();
      if (var2.inEventLoop()) {
         this.shutdownOutput0(var1);
      } else {
         var2.execute(new Runnable() {
            public void run() {
               OioSocketChannel.this.shutdownOutput0(var1);
            }
         });
      }

      return var1;
   }

   private void shutdownOutput0(ChannelPromise var1) {
      try {
         this.shutdownOutput0();
         var1.setSuccess();
      } catch (Throwable var3) {
         var1.setFailure(var3);
      }

   }

   private void shutdownOutput0() throws IOException {
      this.socket.shutdownOutput();
   }

   public ChannelFuture shutdownInput(final ChannelPromise var1) {
      EventLoop var2 = this.eventLoop();
      if (var2.inEventLoop()) {
         this.shutdownInput0(var1);
      } else {
         var2.execute(new Runnable() {
            public void run() {
               OioSocketChannel.this.shutdownInput0(var1);
            }
         });
      }

      return var1;
   }

   private void shutdownInput0(ChannelPromise var1) {
      try {
         this.socket.shutdownInput();
         var1.setSuccess();
      } catch (Throwable var3) {
         var1.setFailure(var3);
      }

   }

   public ChannelFuture shutdown(final ChannelPromise var1) {
      ChannelFuture var2 = this.shutdownOutput();
      if (var2.isDone()) {
         this.shutdownOutputDone(var2, var1);
      } else {
         var2.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               OioSocketChannel.this.shutdownOutputDone(var1x, var1);
            }
         });
      }

      return var1;
   }

   private void shutdownOutputDone(final ChannelFuture var1, final ChannelPromise var2) {
      ChannelFuture var3 = this.shutdownInput();
      if (var3.isDone()) {
         shutdownDone(var1, var3, var2);
      } else {
         var3.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               OioSocketChannel.shutdownDone(var1, var1x, var2);
            }
         });
      }

   }

   private static void shutdownDone(ChannelFuture var0, ChannelFuture var1, ChannelPromise var2) {
      Throwable var3 = var0.cause();
      Throwable var4 = var1.cause();
      if (var3 != null) {
         if (var4 != null) {
            logger.debug("Exception suppressed because a previous exception occurred.", var4);
         }

         var2.setFailure(var3);
      } else if (var4 != null) {
         var2.setFailure(var4);
      } else {
         var2.setSuccess();
      }

   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   protected SocketAddress localAddress0() {
      return this.socket.getLocalSocketAddress();
   }

   protected SocketAddress remoteAddress0() {
      return this.socket.getRemoteSocketAddress();
   }

   protected void doBind(SocketAddress var1) throws Exception {
      SocketUtils.bind(this.socket, var1);
   }

   protected void doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (var2 != null) {
         SocketUtils.bind(this.socket, var2);
      }

      boolean var3 = false;

      try {
         SocketUtils.connect(this.socket, var1, this.config().getConnectTimeoutMillis());
         this.activate(this.socket.getInputStream(), this.socket.getOutputStream());
         var3 = true;
      } catch (SocketTimeoutException var9) {
         ConnectTimeoutException var5 = new ConnectTimeoutException("connection timed out: " + var1);
         var5.setStackTrace(var9.getStackTrace());
         throw var5;
      } finally {
         if (!var3) {
            this.doClose();
         }

      }

   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      this.socket.close();
   }

   protected boolean checkInputShutdown() {
      if (this.isInputShutdown()) {
         try {
            Thread.sleep((long)this.config().getSoTimeout());
         } catch (Throwable var2) {
         }

         return true;
      } else {
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   protected void setReadPending(boolean var1) {
      super.setReadPending(var1);
   }

   final void clearReadPending0() {
      this.clearReadPending();
   }
}
