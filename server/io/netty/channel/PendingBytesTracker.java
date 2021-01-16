package io.netty.channel;

import io.netty.util.internal.ObjectUtil;

abstract class PendingBytesTracker implements MessageSizeEstimator.Handle {
   private final MessageSizeEstimator.Handle estimatorHandle;

   private PendingBytesTracker(MessageSizeEstimator.Handle var1) {
      super();
      this.estimatorHandle = (MessageSizeEstimator.Handle)ObjectUtil.checkNotNull(var1, "estimatorHandle");
   }

   public final int size(Object var1) {
      return this.estimatorHandle.size(var1);
   }

   public abstract void incrementPendingOutboundBytes(long var1);

   public abstract void decrementPendingOutboundBytes(long var1);

   static PendingBytesTracker newTracker(Channel var0) {
      if (var0.pipeline() instanceof DefaultChannelPipeline) {
         return new PendingBytesTracker.DefaultChannelPipelinePendingBytesTracker((DefaultChannelPipeline)var0.pipeline());
      } else {
         ChannelOutboundBuffer var1 = var0.unsafe().outboundBuffer();
         MessageSizeEstimator.Handle var2 = var0.config().getMessageSizeEstimator().newHandle();
         return (PendingBytesTracker)(var1 == null ? new PendingBytesTracker.NoopPendingBytesTracker(var2) : new PendingBytesTracker.ChannelOutboundBufferPendingBytesTracker(var1, var2));
      }
   }

   // $FF: synthetic method
   PendingBytesTracker(MessageSizeEstimator.Handle var1, Object var2) {
      this(var1);
   }

   private static final class NoopPendingBytesTracker extends PendingBytesTracker {
      NoopPendingBytesTracker(MessageSizeEstimator.Handle var1) {
         super(var1, null);
      }

      public void incrementPendingOutboundBytes(long var1) {
      }

      public void decrementPendingOutboundBytes(long var1) {
      }
   }

   private static final class ChannelOutboundBufferPendingBytesTracker extends PendingBytesTracker {
      private final ChannelOutboundBuffer buffer;

      ChannelOutboundBufferPendingBytesTracker(ChannelOutboundBuffer var1, MessageSizeEstimator.Handle var2) {
         super(var2, null);
         this.buffer = var1;
      }

      public void incrementPendingOutboundBytes(long var1) {
         this.buffer.incrementPendingOutboundBytes(var1);
      }

      public void decrementPendingOutboundBytes(long var1) {
         this.buffer.decrementPendingOutboundBytes(var1);
      }
   }

   private static final class DefaultChannelPipelinePendingBytesTracker extends PendingBytesTracker {
      private final DefaultChannelPipeline pipeline;

      DefaultChannelPipelinePendingBytesTracker(DefaultChannelPipeline var1) {
         super(var1.estimatorHandle(), null);
         this.pipeline = var1;
      }

      public void incrementPendingOutboundBytes(long var1) {
         this.pipeline.incrementPendingOutboundBytes(var1);
      }

      public void decrementPendingOutboundBytes(long var1) {
         this.pipeline.decrementPendingOutboundBytes(var1);
      }
   }
}
