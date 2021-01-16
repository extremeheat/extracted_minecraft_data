package io.netty.handler.ipfilter;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.SocketAddress;

public abstract class AbstractRemoteAddressFilter<T extends SocketAddress> extends ChannelInboundHandlerAdapter {
   public AbstractRemoteAddressFilter() {
      super();
   }

   public void channelRegistered(ChannelHandlerContext var1) throws Exception {
      this.handleNewChannel(var1);
      var1.fireChannelRegistered();
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      if (!this.handleNewChannel(var1)) {
         throw new IllegalStateException("cannot determine to accept or reject a channel: " + var1.channel());
      } else {
         var1.fireChannelActive();
      }
   }

   private boolean handleNewChannel(ChannelHandlerContext var1) throws Exception {
      SocketAddress var2 = var1.channel().remoteAddress();
      if (var2 == null) {
         return false;
      } else {
         var1.pipeline().remove((ChannelHandler)this);
         if (this.accept(var1, var2)) {
            this.channelAccepted(var1, var2);
         } else {
            ChannelFuture var3 = this.channelRejected(var1, var2);
            if (var3 != null) {
               var3.addListener(ChannelFutureListener.CLOSE);
            } else {
               var1.close();
            }
         }

         return true;
      }
   }

   protected abstract boolean accept(ChannelHandlerContext var1, T var2) throws Exception;

   protected void channelAccepted(ChannelHandlerContext var1, T var2) {
   }

   protected ChannelFuture channelRejected(ChannelHandlerContext var1, T var2) {
      return null;
   }
}
