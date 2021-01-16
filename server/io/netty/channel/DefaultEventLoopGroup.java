package io.netty.channel;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventLoopGroup extends MultithreadEventLoopGroup {
   public DefaultEventLoopGroup() {
      this(0);
   }

   public DefaultEventLoopGroup(int var1) {
      this(var1, (ThreadFactory)null);
   }

   public DefaultEventLoopGroup(int var1, ThreadFactory var2) {
      super(var1, var2);
   }

   public DefaultEventLoopGroup(int var1, Executor var2) {
      super(var1, var2);
   }

   protected EventLoop newChild(Executor var1, Object... var2) throws Exception {
      return new DefaultEventLoop(this, var1);
   }
}
