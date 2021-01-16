package io.netty.channel;

import io.netty.util.concurrent.AbstractEventExecutor;

public abstract class AbstractEventLoop extends AbstractEventExecutor implements EventLoop {
   protected AbstractEventLoop() {
      super();
   }

   protected AbstractEventLoop(EventLoopGroup var1) {
      super(var1);
   }

   public EventLoopGroup parent() {
      return (EventLoopGroup)super.parent();
   }

   public EventLoop next() {
      return (EventLoop)super.next();
   }
}
