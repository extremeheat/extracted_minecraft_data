package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, ServerChannel> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);
   private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap();
   private final Map<AttributeKey<?>, Object> childAttrs = new LinkedHashMap();
   private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);
   private volatile EventLoopGroup childGroup;
   private volatile ChannelHandler childHandler;

   public ServerBootstrap() {
      super();
   }

   private ServerBootstrap(ServerBootstrap var1) {
      super(var1);
      this.childGroup = var1.childGroup;
      this.childHandler = var1.childHandler;
      synchronized(var1.childOptions) {
         this.childOptions.putAll(var1.childOptions);
      }

      synchronized(var1.childAttrs) {
         this.childAttrs.putAll(var1.childAttrs);
      }
   }

   public ServerBootstrap group(EventLoopGroup var1) {
      return this.group(var1, var1);
   }

   public ServerBootstrap group(EventLoopGroup var1, EventLoopGroup var2) {
      super.group(var1);
      if (var2 == null) {
         throw new NullPointerException("childGroup");
      } else if (this.childGroup != null) {
         throw new IllegalStateException("childGroup set already");
      } else {
         this.childGroup = var2;
         return this;
      }
   }

   public <T> ServerBootstrap childOption(ChannelOption<T> var1, T var2) {
      if (var1 == null) {
         throw new NullPointerException("childOption");
      } else {
         if (var2 == null) {
            synchronized(this.childOptions) {
               this.childOptions.remove(var1);
            }
         } else {
            synchronized(this.childOptions) {
               this.childOptions.put(var1, var2);
            }
         }

         return this;
      }
   }

   public <T> ServerBootstrap childAttr(AttributeKey<T> var1, T var2) {
      if (var1 == null) {
         throw new NullPointerException("childKey");
      } else {
         if (var2 == null) {
            this.childAttrs.remove(var1);
         } else {
            this.childAttrs.put(var1, var2);
         }

         return this;
      }
   }

   public ServerBootstrap childHandler(ChannelHandler var1) {
      if (var1 == null) {
         throw new NullPointerException("childHandler");
      } else {
         this.childHandler = var1;
         return this;
      }
   }

   void init(Channel var1) throws Exception {
      Map var2 = this.options0();
      synchronized(var2) {
         setChannelOptions(var1, var2, logger);
      }

      Map var3 = this.attrs0();
      synchronized(var3) {
         Iterator var5 = var3.entrySet().iterator();

         while(true) {
            if (!var5.hasNext()) {
               break;
            }

            Entry var6 = (Entry)var5.next();
            AttributeKey var7 = (AttributeKey)var6.getKey();
            var1.attr(var7).set(var6.getValue());
         }
      }

      ChannelPipeline var4 = var1.pipeline();
      final EventLoopGroup var16 = this.childGroup;
      final ChannelHandler var17 = this.childHandler;
      final Entry[] var18;
      synchronized(this.childOptions) {
         var18 = (Entry[])this.childOptions.entrySet().toArray(newOptionArray(this.childOptions.size()));
      }

      final Entry[] var8;
      synchronized(this.childAttrs) {
         var8 = (Entry[])this.childAttrs.entrySet().toArray(newAttrArray(this.childAttrs.size()));
      }

      var4.addLast(new ChannelInitializer<Channel>() {
         public void initChannel(final Channel var1) throws Exception {
            final ChannelPipeline var2 = var1.pipeline();
            ChannelHandler var3 = ServerBootstrap.this.config.handler();
            if (var3 != null) {
               var2.addLast(var3);
            }

            var1.eventLoop().execute(new Runnable() {
               public void run() {
                  var2.addLast(new ServerBootstrap.ServerBootstrapAcceptor(var1, var16, var17, var18, var8));
               }
            });
         }
      });
   }

   public ServerBootstrap validate() {
      super.validate();
      if (this.childHandler == null) {
         throw new IllegalStateException("childHandler not set");
      } else {
         if (this.childGroup == null) {
            logger.warn("childGroup is not set. Using parentGroup instead.");
            this.childGroup = this.config.group();
         }

         return this;
      }
   }

   private static Entry<AttributeKey<?>, Object>[] newAttrArray(int var0) {
      return new Entry[var0];
   }

   private static Entry<ChannelOption<?>, Object>[] newOptionArray(int var0) {
      return new Entry[var0];
   }

   public ServerBootstrap clone() {
      return new ServerBootstrap(this);
   }

   /** @deprecated */
   @Deprecated
   public EventLoopGroup childGroup() {
      return this.childGroup;
   }

   final ChannelHandler childHandler() {
      return this.childHandler;
   }

   final Map<ChannelOption<?>, Object> childOptions() {
      return copiedMap(this.childOptions);
   }

   final Map<AttributeKey<?>, Object> childAttrs() {
      return copiedMap(this.childAttrs);
   }

   public final ServerBootstrapConfig config() {
      return this.config;
   }

   private static class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {
      private final EventLoopGroup childGroup;
      private final ChannelHandler childHandler;
      private final Entry<ChannelOption<?>, Object>[] childOptions;
      private final Entry<AttributeKey<?>, Object>[] childAttrs;
      private final Runnable enableAutoReadTask;

      ServerBootstrapAcceptor(final Channel var1, EventLoopGroup var2, ChannelHandler var3, Entry<ChannelOption<?>, Object>[] var4, Entry<AttributeKey<?>, Object>[] var5) {
         super();
         this.childGroup = var2;
         this.childHandler = var3;
         this.childOptions = var4;
         this.childAttrs = var5;
         this.enableAutoReadTask = new Runnable() {
            public void run() {
               var1.config().setAutoRead(true);
            }
         };
      }

      public void channelRead(ChannelHandlerContext var1, Object var2) {
         final Channel var3 = (Channel)var2;
         var3.pipeline().addLast(this.childHandler);
         AbstractBootstrap.setChannelOptions(var3, this.childOptions, ServerBootstrap.logger);
         Entry[] var4 = this.childAttrs;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Entry var7 = var4[var6];
            var3.attr((AttributeKey)var7.getKey()).set(var7.getValue());
         }

         try {
            this.childGroup.register(var3).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1) throws Exception {
                  if (!var1.isSuccess()) {
                     ServerBootstrap.ServerBootstrapAcceptor.forceClose(var3, var1.cause());
                  }

               }
            });
         } catch (Throwable var8) {
            forceClose(var3, var8);
         }

      }

      private static void forceClose(Channel var0, Throwable var1) {
         var0.unsafe().closeForcibly();
         ServerBootstrap.logger.warn("Failed to register an accepted channel: {}", var0, var1);
      }

      public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
         ChannelConfig var3 = var1.channel().config();
         if (var3.isAutoRead()) {
            var3.setAutoRead(false);
            var1.channel().eventLoop().schedule(this.enableAutoReadTask, 1L, TimeUnit.SECONDS);
         }

         var1.fireExceptionCaught(var2);
      }
   }
}
