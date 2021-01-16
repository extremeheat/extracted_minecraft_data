package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class RuleBasedIpFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
   private final IpFilterRule[] rules;

   public RuleBasedIpFilter(IpFilterRule... var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("rules");
      } else {
         this.rules = var1;
      }
   }

   protected boolean accept(ChannelHandlerContext var1, InetSocketAddress var2) throws Exception {
      IpFilterRule[] var3 = this.rules;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         IpFilterRule var6 = var3[var5];
         if (var6 == null) {
            break;
         }

         if (var6.matches(var2)) {
            return var6.ruleType() == IpFilterRuleType.ACCEPT;
         }
      }

      return true;
   }
}
