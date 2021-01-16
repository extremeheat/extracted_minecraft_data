package io.netty.channel;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ConcurrentMap;

@ChannelHandler.Sharable
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
   private final ConcurrentMap<ChannelHandlerContext, Boolean> initMap = PlatformDependent.newConcurrentHashMap();

   public ChannelInitializer() {
      super();
   }

   protected abstract void initChannel(C var1) throws Exception;

   public final void channelRegistered(ChannelHandlerContext var1) throws Exception {
      if (this.initChannel(var1)) {
         var1.pipeline().fireChannelRegistered();
      } else {
         var1.fireChannelRegistered();
      }

   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      logger.warn("Failed to initialize a channel. Closing: " + var1.channel(), var2);
      var1.close();
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      if (var1.channel().isRegistered()) {
         this.initChannel(var1);
      }

   }

   private boolean initChannel(ChannelHandlerContext var1) throws Exception {
      if (this.initMap.putIfAbsent(var1, Boolean.TRUE) == null) {
         try {
            this.initChannel(var1.channel());
         } catch (Throwable var6) {
            this.exceptionCaught(var1, var6);
         } finally {
            this.remove(var1);
         }

         return true;
      } else {
         return false;
      }
   }

   private void remove(ChannelHandlerContext var1) {
      try {
         ChannelPipeline var2 = var1.pipeline();
         if (var2.context((ChannelHandler)this) != null) {
            var2.remove((ChannelHandler)this);
         }
      } finally {
         this.initMap.remove(var1);
      }

   }
}
