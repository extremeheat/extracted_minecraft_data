package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Bootstrap extends AbstractBootstrap<Bootstrap, Channel> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Bootstrap.class);
   private static final AddressResolverGroup<?> DEFAULT_RESOLVER;
   private final BootstrapConfig config = new BootstrapConfig(this);
   private volatile AddressResolverGroup<SocketAddress> resolver;
   private volatile SocketAddress remoteAddress;

   public Bootstrap() {
      super();
      this.resolver = DEFAULT_RESOLVER;
   }

   private Bootstrap(Bootstrap var1) {
      super(var1);
      this.resolver = DEFAULT_RESOLVER;
      this.resolver = var1.resolver;
      this.remoteAddress = var1.remoteAddress;
   }

   public Bootstrap resolver(AddressResolverGroup<?> var1) {
      this.resolver = var1 == null ? DEFAULT_RESOLVER : var1;
      return this;
   }

   public Bootstrap remoteAddress(SocketAddress var1) {
      this.remoteAddress = var1;
      return this;
   }

   public Bootstrap remoteAddress(String var1, int var2) {
      this.remoteAddress = InetSocketAddress.createUnresolved(var1, var2);
      return this;
   }

   public Bootstrap remoteAddress(InetAddress var1, int var2) {
      this.remoteAddress = new InetSocketAddress(var1, var2);
      return this;
   }

   public ChannelFuture connect() {
      this.validate();
      SocketAddress var1 = this.remoteAddress;
      if (var1 == null) {
         throw new IllegalStateException("remoteAddress not set");
      } else {
         return this.doResolveAndConnect(var1, this.config.localAddress());
      }
   }

   public ChannelFuture connect(String var1, int var2) {
      return this.connect(InetSocketAddress.createUnresolved(var1, var2));
   }

   public ChannelFuture connect(InetAddress var1, int var2) {
      return this.connect(new InetSocketAddress(var1, var2));
   }

   public ChannelFuture connect(SocketAddress var1) {
      if (var1 == null) {
         throw new NullPointerException("remoteAddress");
      } else {
         this.validate();
         return this.doResolveAndConnect(var1, this.config.localAddress());
      }
   }

   public ChannelFuture connect(SocketAddress var1, SocketAddress var2) {
      if (var1 == null) {
         throw new NullPointerException("remoteAddress");
      } else {
         this.validate();
         return this.doResolveAndConnect(var1, var2);
      }
   }

   private ChannelFuture doResolveAndConnect(final SocketAddress var1, final SocketAddress var2) {
      ChannelFuture var3 = this.initAndRegister();
      final Channel var4 = var3.channel();
      if (var3.isDone()) {
         return !var3.isSuccess() ? var3 : this.doResolveAndConnect0(var4, var1, var2, var4.newPromise());
      } else {
         final AbstractBootstrap.PendingRegistrationPromise var5 = new AbstractBootstrap.PendingRegistrationPromise(var4);
         var3.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               Throwable var2x = var1x.cause();
               if (var2x != null) {
                  var5.setFailure(var2x);
               } else {
                  var5.registered();
                  Bootstrap.this.doResolveAndConnect0(var4, var1, var2, var5);
               }

            }
         });
         return var5;
      }
   }

   private ChannelFuture doResolveAndConnect0(final Channel var1, SocketAddress var2, final SocketAddress var3, final ChannelPromise var4) {
      try {
         EventLoop var5 = var1.eventLoop();
         AddressResolver var6 = this.resolver.getResolver(var5);
         if (!var6.isSupported(var2) || var6.isResolved(var2)) {
            doConnect(var2, var3, var4);
            return var4;
         }

         Future var7 = var6.resolve(var2);
         if (var7.isDone()) {
            Throwable var8 = var7.cause();
            if (var8 != null) {
               var1.close();
               var4.setFailure(var8);
            } else {
               doConnect((SocketAddress)var7.getNow(), var3, var4);
            }

            return var4;
         }

         var7.addListener(new FutureListener<SocketAddress>() {
            public void operationComplete(Future<SocketAddress> var1x) throws Exception {
               if (var1x.cause() != null) {
                  var1.close();
                  var4.setFailure(var1x.cause());
               } else {
                  Bootstrap.doConnect((SocketAddress)var1x.getNow(), var3, var4);
               }

            }
         });
      } catch (Throwable var9) {
         var4.tryFailure(var9);
      }

      return var4;
   }

   private static void doConnect(final SocketAddress var0, final SocketAddress var1, final ChannelPromise var2) {
      final Channel var3 = var2.channel();
      var3.eventLoop().execute(new Runnable() {
         public void run() {
            if (var1 == null) {
               var3.connect(var0, var2);
            } else {
               var3.connect(var0, var1, var2);
            }

            var2.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
         }
      });
   }

   void init(Channel var1) throws Exception {
      ChannelPipeline var2 = var1.pipeline();
      var2.addLast(this.config.handler());
      Map var3 = this.options0();
      synchronized(var3) {
         setChannelOptions(var1, var3, logger);
      }

      Map var4 = this.attrs0();
      synchronized(var4) {
         Iterator var6 = var4.entrySet().iterator();

         while(var6.hasNext()) {
            Entry var7 = (Entry)var6.next();
            var1.attr((AttributeKey)var7.getKey()).set(var7.getValue());
         }

      }
   }

   public Bootstrap validate() {
      super.validate();
      if (this.config.handler() == null) {
         throw new IllegalStateException("handler not set");
      } else {
         return this;
      }
   }

   public Bootstrap clone() {
      return new Bootstrap(this);
   }

   public Bootstrap clone(EventLoopGroup var1) {
      Bootstrap var2 = new Bootstrap(this);
      var2.group = var1;
      return var2;
   }

   public final BootstrapConfig config() {
      return this.config;
   }

   final SocketAddress remoteAddress() {
      return this.remoteAddress;
   }

   final AddressResolverGroup<?> resolver() {
      return this.resolver;
   }

   static {
      DEFAULT_RESOLVER = DefaultAddressResolverGroup.INSTANCE;
   }
}
