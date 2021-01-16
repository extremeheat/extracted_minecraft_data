package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;

public final class CoalescingBufferQueue extends AbstractCoalescingBufferQueue {
   private final Channel channel;

   public CoalescingBufferQueue(Channel var1) {
      this(var1, 4);
   }

   public CoalescingBufferQueue(Channel var1, int var2) {
      this(var1, var2, false);
   }

   public CoalescingBufferQueue(Channel var1, int var2, boolean var3) {
      super(var3 ? var1 : null, var2);
      this.channel = (Channel)ObjectUtil.checkNotNull(var1, "channel");
   }

   public ByteBuf remove(int var1, ChannelPromise var2) {
      return this.remove(this.channel.alloc(), var1, var2);
   }

   public void releaseAndFailAll(Throwable var1) {
      this.releaseAndFailAll(this.channel, var1);
   }

   protected ByteBuf compose(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
      if (var2 instanceof CompositeByteBuf) {
         CompositeByteBuf var4 = (CompositeByteBuf)var2;
         var4.addComponent(true, var3);
         return var4;
      } else {
         return this.composeIntoComposite(var1, var2, var3);
      }
   }

   protected ByteBuf removeEmptyValue() {
      return Unpooled.EMPTY_BUFFER;
   }
}
