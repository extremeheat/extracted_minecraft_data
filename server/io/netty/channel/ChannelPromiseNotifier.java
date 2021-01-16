package io.netty.channel;

import io.netty.util.concurrent.PromiseNotifier;

public final class ChannelPromiseNotifier extends PromiseNotifier<Void, ChannelFuture> implements ChannelFutureListener {
   public ChannelPromiseNotifier(ChannelPromise... var1) {
      super(var1);
   }

   public ChannelPromiseNotifier(boolean var1, ChannelPromise... var2) {
      super(var1, var2);
   }
}
