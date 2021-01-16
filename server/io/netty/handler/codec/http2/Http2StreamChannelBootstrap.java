package io.netty.handler.codec.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Http2StreamChannelBootstrap {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Http2StreamChannelBootstrap.class);
   private final Map<ChannelOption<?>, Object> options = new LinkedHashMap();
   private final Map<AttributeKey<?>, Object> attrs = new LinkedHashMap();
   private final Channel channel;
   private volatile ChannelHandler handler;

   public Http2StreamChannelBootstrap(Channel var1) {
      super();
      this.channel = (Channel)ObjectUtil.checkNotNull(var1, "channel");
   }

   public <T> Http2StreamChannelBootstrap option(ChannelOption<T> var1, T var2) {
      if (var1 == null) {
         throw new NullPointerException("option");
      } else {
         if (var2 == null) {
            synchronized(this.options) {
               this.options.remove(var1);
            }
         } else {
            synchronized(this.options) {
               this.options.put(var1, var2);
            }
         }

         return this;
      }
   }

   public <T> Http2StreamChannelBootstrap attr(AttributeKey<T> var1, T var2) {
      if (var1 == null) {
         throw new NullPointerException("key");
      } else {
         if (var2 == null) {
            synchronized(this.attrs) {
               this.attrs.remove(var1);
            }
         } else {
            synchronized(this.attrs) {
               this.attrs.put(var1, var2);
            }
         }

         return this;
      }
   }

   public Http2StreamChannelBootstrap handler(ChannelHandler var1) {
      this.handler = (ChannelHandler)ObjectUtil.checkNotNull(var1, "handler");
      return this;
   }

   public Future<Http2StreamChannel> open() {
      return this.open(this.channel.eventLoop().newPromise());
   }

   public Future<Http2StreamChannel> open(final Promise<Http2StreamChannel> var1) {
      final ChannelHandlerContext var2 = this.channel.pipeline().context(Http2MultiplexCodec.class);
      if (var2 == null) {
         if (this.channel.isActive()) {
            var1.setFailure(new IllegalStateException(StringUtil.simpleClassName(Http2MultiplexCodec.class) + " must be in the ChannelPipeline of Channel " + this.channel));
         } else {
            var1.setFailure(new ClosedChannelException());
         }
      } else {
         EventExecutor var3 = var2.executor();
         if (var3.inEventLoop()) {
            this.open0(var2, var1);
         } else {
            var3.execute(new Runnable() {
               public void run() {
                  Http2StreamChannelBootstrap.this.open0(var2, var1);
               }
            });
         }
      }

      return var1;
   }

   public void open0(ChannelHandlerContext var1, final Promise<Http2StreamChannel> var2) {
      assert var1.executor().inEventLoop();

      final Http2StreamChannel var3 = ((Http2MultiplexCodec)var1.handler()).newOutboundStream();

      try {
         this.init(var3);
      } catch (Exception var5) {
         var3.unsafe().closeForcibly();
         var2.setFailure(var5);
         return;
      }

      ChannelFuture var4 = var1.channel().eventLoop().register(var3);
      var4.addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            if (var1.isSuccess()) {
               var2.setSuccess(var3);
            } else if (var1.isCancelled()) {
               var2.cancel(false);
            } else {
               if (var3.isRegistered()) {
                  var3.close();
               } else {
                  var3.unsafe().closeForcibly();
               }

               var2.setFailure(var1.cause());
            }

         }
      });
   }

   private void init(Channel var1) throws Exception {
      ChannelPipeline var2 = var1.pipeline();
      ChannelHandler var3 = this.handler;
      if (var3 != null) {
         var2.addLast(var3);
      }

      synchronized(this.options) {
         setChannelOptions(var1, this.options, logger);
      }

      synchronized(this.attrs) {
         Iterator var5 = this.attrs.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            var1.attr((AttributeKey)var6.getKey()).set(var6.getValue());
         }

      }
   }

   private static void setChannelOptions(Channel var0, Map<ChannelOption<?>, Object> var1, InternalLogger var2) {
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         setChannelOption(var0, (ChannelOption)var4.getKey(), var4.getValue(), var2);
      }

   }

   private static void setChannelOption(Channel var0, ChannelOption<?> var1, Object var2, InternalLogger var3) {
      try {
         if (!var0.config().setOption(var1, var2)) {
            var3.warn("Unknown channel option '{}' for channel '{}'", var1, var0);
         }
      } catch (Throwable var5) {
         var3.warn("Failed to set channel option '{}' with value '{}' for channel '{}'", var1, var2, var0, var5);
      }

   }
}
