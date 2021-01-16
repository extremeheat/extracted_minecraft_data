package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;

public abstract class AbstractCoalescingBufferQueue {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractCoalescingBufferQueue.class);
   private final ArrayDeque<Object> bufAndListenerPairs;
   private final PendingBytesTracker tracker;
   private int readableBytes;

   protected AbstractCoalescingBufferQueue(Channel var1, int var2) {
      super();
      this.bufAndListenerPairs = new ArrayDeque(var2);
      this.tracker = var1 == null ? null : PendingBytesTracker.newTracker(var1);
   }

   public final void addFirst(ByteBuf var1, ChannelPromise var2) {
      this.addFirst(var1, toChannelFutureListener(var2));
   }

   private void addFirst(ByteBuf var1, ChannelFutureListener var2) {
      if (var2 != null) {
         this.bufAndListenerPairs.addFirst(var2);
      }

      this.bufAndListenerPairs.addFirst(var1);
      this.incrementReadableBytes(var1.readableBytes());
   }

   public final void add(ByteBuf var1) {
      this.add(var1, (ChannelFutureListener)null);
   }

   public final void add(ByteBuf var1, ChannelPromise var2) {
      this.add(var1, toChannelFutureListener(var2));
   }

   public final void add(ByteBuf var1, ChannelFutureListener var2) {
      this.bufAndListenerPairs.add(var1);
      if (var2 != null) {
         this.bufAndListenerPairs.add(var2);
      }

      this.incrementReadableBytes(var1.readableBytes());
   }

   public final ByteBuf removeFirst(ChannelPromise var1) {
      Object var2 = this.bufAndListenerPairs.poll();
      if (var2 == null) {
         return null;
      } else {
         assert var2 instanceof ByteBuf;

         ByteBuf var3 = (ByteBuf)var2;
         this.decrementReadableBytes(var3.readableBytes());
         var2 = this.bufAndListenerPairs.peek();
         if (var2 instanceof ChannelFutureListener) {
            var1.addListener((ChannelFutureListener)var2);
            this.bufAndListenerPairs.poll();
         }

         return var3;
      }
   }

   public final ByteBuf remove(ByteBufAllocator var1, int var2, ChannelPromise var3) {
      ObjectUtil.checkPositiveOrZero(var2, "bytes");
      ObjectUtil.checkNotNull(var3, "aggregatePromise");
      if (this.bufAndListenerPairs.isEmpty()) {
         return this.removeEmptyValue();
      } else {
         var2 = Math.min(var2, this.readableBytes);
         ByteBuf var4 = null;
         ByteBuf var5 = null;

         try {
            while(true) {
               Object var7 = this.bufAndListenerPairs.poll();
               if (var7 == null) {
                  break;
               }

               if (var7 instanceof ChannelFutureListener) {
                  var3.addListener((ChannelFutureListener)var7);
               } else {
                  var5 = (ByteBuf)var7;
                  if (var5.readableBytes() > var2) {
                     this.bufAndListenerPairs.addFirst(var5);
                     if (var2 > 0) {
                        var5 = var5.readRetainedSlice(var2);
                        var4 = var4 == null ? this.composeFirst(var1, var5) : this.compose(var1, var4, var5);
                        var2 = 0;
                     }
                     break;
                  }

                  var2 -= var5.readableBytes();
                  var4 = var4 == null ? this.composeFirst(var1, var5) : this.compose(var1, var4, var5);
                  var5 = null;
               }
            }
         } catch (Throwable var8) {
            ReferenceCountUtil.safeRelease(var5);
            ReferenceCountUtil.safeRelease(var4);
            var3.setFailure(var8);
            PlatformDependent.throwException(var8);
         }

         this.decrementReadableBytes(var2 - var2);
         return var4;
      }
   }

   public final int readableBytes() {
      return this.readableBytes;
   }

   public final boolean isEmpty() {
      return this.bufAndListenerPairs.isEmpty();
   }

   public final void releaseAndFailAll(ChannelOutboundInvoker var1, Throwable var2) {
      this.releaseAndCompleteAll(var1.newFailedFuture(var2));
   }

   public final void copyTo(AbstractCoalescingBufferQueue var1) {
      var1.bufAndListenerPairs.addAll(this.bufAndListenerPairs);
      var1.incrementReadableBytes(this.readableBytes);
   }

   public final void writeAndRemoveAll(ChannelHandlerContext var1) {
      this.decrementReadableBytes(this.readableBytes);
      Throwable var2 = null;
      ByteBuf var3 = null;

      while(true) {
         Object var4 = this.bufAndListenerPairs.poll();

         try {
            if (var4 == null) {
               if (var3 != null) {
                  var1.write(var3, var1.voidPromise());
               }
               break;
            }

            if (var4 instanceof ByteBuf) {
               if (var3 != null) {
                  var1.write(var3, var1.voidPromise());
               }

               var3 = (ByteBuf)var4;
            } else if (var4 instanceof ChannelPromise) {
               var1.write(var3, (ChannelPromise)var4);
               var3 = null;
            } else {
               var1.write(var3).addListener((ChannelFutureListener)var4);
               var3 = null;
            }
         } catch (Throwable var6) {
            if (var2 == null) {
               var2 = var6;
            } else {
               logger.info("Throwable being suppressed because Throwable {} is already pending", var2, var6);
            }
         }
      }

      if (var2 != null) {
         throw new IllegalStateException(var2);
      }
   }

   protected abstract ByteBuf compose(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);

   protected final ByteBuf composeIntoComposite(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
      CompositeByteBuf var4 = var1.compositeBuffer(this.size() + 2);

      try {
         var4.addComponent(true, var2);
         var4.addComponent(true, var3);
      } catch (Throwable var6) {
         var4.release();
         ReferenceCountUtil.safeRelease(var3);
         PlatformDependent.throwException(var6);
      }

      return var4;
   }

   protected final ByteBuf copyAndCompose(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
      ByteBuf var4 = var1.ioBuffer(var2.readableBytes() + var3.readableBytes());

      try {
         var4.writeBytes(var2).writeBytes(var3);
      } catch (Throwable var6) {
         var4.release();
         ReferenceCountUtil.safeRelease(var3);
         PlatformDependent.throwException(var6);
      }

      var2.release();
      var3.release();
      return var4;
   }

   protected ByteBuf composeFirst(ByteBufAllocator var1, ByteBuf var2) {
      return var2;
   }

   protected abstract ByteBuf removeEmptyValue();

   protected final int size() {
      return this.bufAndListenerPairs.size();
   }

   private void releaseAndCompleteAll(ChannelFuture var1) {
      this.decrementReadableBytes(this.readableBytes);
      Throwable var2 = null;

      while(true) {
         Object var3 = this.bufAndListenerPairs.poll();
         if (var3 == null) {
            if (var2 != null) {
               throw new IllegalStateException(var2);
            }

            return;
         }

         try {
            if (var3 instanceof ByteBuf) {
               ReferenceCountUtil.safeRelease(var3);
            } else {
               ((ChannelFutureListener)var3).operationComplete(var1);
            }
         } catch (Throwable var5) {
            if (var2 == null) {
               var2 = var5;
            } else {
               logger.info("Throwable being suppressed because Throwable {} is already pending", var2, var5);
            }
         }
      }
   }

   private void incrementReadableBytes(int var1) {
      int var2 = this.readableBytes + var1;
      if (var2 < this.readableBytes) {
         throw new IllegalStateException("buffer queue length overflow: " + this.readableBytes + " + " + var1);
      } else {
         this.readableBytes = var2;
         if (this.tracker != null) {
            this.tracker.incrementPendingOutboundBytes((long)var1);
         }

      }
   }

   private void decrementReadableBytes(int var1) {
      this.readableBytes -= var1;

      assert this.readableBytes >= 0;

      if (this.tracker != null) {
         this.tracker.decrementPendingOutboundBytes((long)var1);
      }

   }

   private static ChannelFutureListener toChannelFutureListener(ChannelPromise var0) {
      return var0.isVoid() ? null : new DelegatingChannelPromiseNotifier(var0);
   }
}
