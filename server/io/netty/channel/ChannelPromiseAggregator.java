package io.netty.channel;

import io.netty.util.concurrent.PromiseAggregator;

/** @deprecated */
@Deprecated
public final class ChannelPromiseAggregator extends PromiseAggregator<Void, ChannelFuture> implements ChannelFutureListener {
   public ChannelPromiseAggregator(ChannelPromise var1) {
      super(var1);
   }
}
