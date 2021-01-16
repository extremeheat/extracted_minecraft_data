package io.netty.channel;

import io.netty.util.concurrent.GenericFutureListener;

public interface ChannelFutureListener extends GenericFutureListener<ChannelFuture> {
   ChannelFutureListener CLOSE = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture var1) {
         var1.channel().close();
      }
   };
   ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture var1) {
         if (!var1.isSuccess()) {
            var1.channel().close();
         }

      }
   };
   ChannelFutureListener FIRE_EXCEPTION_ON_FAILURE = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture var1) {
         if (!var1.isSuccess()) {
            var1.channel().pipeline().fireExceptionCaught(var1.cause());
         }

      }
   };
}
