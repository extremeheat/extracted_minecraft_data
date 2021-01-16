package io.netty.channel.pool;

import io.netty.channel.Channel;

public abstract class AbstractChannelPoolHandler implements ChannelPoolHandler {
   public AbstractChannelPoolHandler() {
      super();
   }

   public void channelAcquired(Channel var1) throws Exception {
   }

   public void channelReleased(Channel var1) throws Exception {
   }
}
