package io.netty.channel.pool;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public interface ChannelHealthChecker {
   ChannelHealthChecker ACTIVE = new ChannelHealthChecker() {
      public Future<Boolean> isHealthy(Channel var1) {
         EventLoop var2 = var1.eventLoop();
         return var1.isActive() ? var2.newSucceededFuture(Boolean.TRUE) : var2.newSucceededFuture(Boolean.FALSE);
      }
   };

   Future<Boolean> isHealthy(Channel var1);
}
