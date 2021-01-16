package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;
import java.util.Map;

public abstract class AbstractBootstrapConfig<B extends AbstractBootstrap<B, C>, C extends Channel> {
   protected final B bootstrap;

   protected AbstractBootstrapConfig(B var1) {
      super();
      this.bootstrap = (AbstractBootstrap)ObjectUtil.checkNotNull(var1, "bootstrap");
   }

   public final SocketAddress localAddress() {
      return this.bootstrap.localAddress();
   }

   public final ChannelFactory<? extends C> channelFactory() {
      return this.bootstrap.channelFactory();
   }

   public final ChannelHandler handler() {
      return this.bootstrap.handler();
   }

   public final Map<ChannelOption<?>, Object> options() {
      return this.bootstrap.options();
   }

   public final Map<AttributeKey<?>, Object> attrs() {
      return this.bootstrap.attrs();
   }

   public final EventLoopGroup group() {
      return this.bootstrap.group();
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder()).append(StringUtil.simpleClassName((Object)this)).append('(');
      EventLoopGroup var2 = this.group();
      if (var2 != null) {
         var1.append("group: ").append(StringUtil.simpleClassName((Object)var2)).append(", ");
      }

      ChannelFactory var3 = this.channelFactory();
      if (var3 != null) {
         var1.append("channelFactory: ").append(var3).append(", ");
      }

      SocketAddress var4 = this.localAddress();
      if (var4 != null) {
         var1.append("localAddress: ").append(var4).append(", ");
      }

      Map var5 = this.options();
      if (!var5.isEmpty()) {
         var1.append("options: ").append(var5).append(", ");
      }

      Map var6 = this.attrs();
      if (!var6.isEmpty()) {
         var1.append("attrs: ").append(var6).append(", ");
      }

      ChannelHandler var7 = this.handler();
      if (var7 != null) {
         var1.append("handler: ").append(var7).append(", ");
      }

      if (var1.charAt(var1.length() - 1) == '(') {
         var1.append(')');
      } else {
         var1.setCharAt(var1.length() - 2, ')');
         var1.setLength(var1.length() - 1);
      }

      return var1.toString();
   }
}
