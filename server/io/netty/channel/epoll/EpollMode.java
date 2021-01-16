package io.netty.channel.epoll;

public enum EpollMode {
   EDGE_TRIGGERED,
   LEVEL_TRIGGERED;

   private EpollMode() {
   }
}
