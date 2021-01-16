package io.netty.handler.ipfilter;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ConcurrentSet;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

@ChannelHandler.Sharable
public class UniqueIpFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
   private final Set<InetAddress> connected = new ConcurrentSet();

   public UniqueIpFilter() {
      super();
   }

   protected boolean accept(ChannelHandlerContext var1, InetSocketAddress var2) throws Exception {
      final InetAddress var3 = var2.getAddress();
      if (this.connected.contains(var3)) {
         return false;
      } else {
         this.connected.add(var3);
         var1.channel().closeFuture().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1) throws Exception {
               UniqueIpFilter.this.connected.remove(var3);
            }
         });
         return true;
      }
   }
}
