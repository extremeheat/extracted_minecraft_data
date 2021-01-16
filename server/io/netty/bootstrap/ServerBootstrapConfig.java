package io.netty.bootstrap;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;
import java.util.Map;

public final class ServerBootstrapConfig extends AbstractBootstrapConfig<ServerBootstrap, ServerChannel> {
   ServerBootstrapConfig(ServerBootstrap var1) {
      super(var1);
   }

   public EventLoopGroup childGroup() {
      return ((ServerBootstrap)this.bootstrap).childGroup();
   }

   public ChannelHandler childHandler() {
      return ((ServerBootstrap)this.bootstrap).childHandler();
   }

   public Map<ChannelOption<?>, Object> childOptions() {
      return ((ServerBootstrap)this.bootstrap).childOptions();
   }

   public Map<AttributeKey<?>, Object> childAttrs() {
      return ((ServerBootstrap)this.bootstrap).childAttrs();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(super.toString());
      var1.setLength(var1.length() - 1);
      var1.append(", ");
      EventLoopGroup var2 = this.childGroup();
      if (var2 != null) {
         var1.append("childGroup: ");
         var1.append(StringUtil.simpleClassName((Object)var2));
         var1.append(", ");
      }

      Map var3 = this.childOptions();
      if (!var3.isEmpty()) {
         var1.append("childOptions: ");
         var1.append(var3);
         var1.append(", ");
      }

      Map var4 = this.childAttrs();
      if (!var4.isEmpty()) {
         var1.append("childAttrs: ");
         var1.append(var4);
         var1.append(", ");
      }

      ChannelHandler var5 = this.childHandler();
      if (var5 != null) {
         var1.append("childHandler: ");
         var1.append(var5);
         var1.append(", ");
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
