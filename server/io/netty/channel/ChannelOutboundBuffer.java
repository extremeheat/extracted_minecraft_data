package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class ChannelOutboundBuffer {
   static final int CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.outboundBufferEntrySizeOverhead", 96);
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
   private static final FastThreadLocal<ByteBuffer[]> NIO_BUFFERS = new FastThreadLocal<ByteBuffer[]>() {
      protected ByteBuffer[] initialValue() throws Exception {
         return new ByteBuffer[1024];
      }
   };
   private final Channel channel;
   private ChannelOutboundBuffer.Entry flushedEntry;
   private ChannelOutboundBuffer.Entry unflushedEntry;
   private ChannelOutboundBuffer.Entry tailEntry;
   private int flushed;
   private int nioBufferCount;
   private long nioBufferSize;
   private boolean inFail;
   private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER = AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
   private volatile long totalPendingSize;
   private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "unwritable");
   private volatile int unwritable;
   private volatile Runnable fireChannelWritabilityChangedTask;

   ChannelOutboundBuffer(AbstractChannel var1) {
      super();
      this.channel = var1;
   }

   public void addMessage(Object var1, int var2, ChannelPromise var3) {
      ChannelOutboundBuffer.Entry var4 = ChannelOutboundBuffer.Entry.newInstance(var1, var2, total(var1), var3);
      if (this.tailEntry == null) {
         this.flushedEntry = null;
      } else {
         ChannelOutboundBuffer.Entry var5 = this.tailEntry;
         var5.next = var4;
      }

      this.tailEntry = var4;
      if (this.unflushedEntry == null) {
         this.unflushedEntry = var4;
      }

      this.incrementPendingOutboundBytes((long)var4.pendingSize, false);
   }

   public void addFlush() {
      ChannelOutboundBuffer.Entry var1 = this.unflushedEntry;
      if (var1 != null) {
         if (this.flushedEntry == null) {
            this.flushedEntry = var1;
         }

         do {
            ++this.flushed;
            if (!var1.promise.setUncancellable()) {
               int var2 = var1.cancel();
               this.decrementPendingOutboundBytes((long)var2, false, true);
            }

            var1 = var1.next;
         } while(var1 != null);

         this.unflushedEntry = null;
      }

   }

   void incrementPendingOutboundBytes(long var1) {
      this.incrementPendingOutboundBytes(var1, true);
   }

   private void incrementPendingOutboundBytes(long var1, boolean var3) {
      if (var1 != 0L) {
         long var4 = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, var1);
         if (var4 > (long)this.channel.config().getWriteBufferHighWaterMark()) {
            this.setUnwritable(var3);
         }

      }
   }

   void decrementPendingOutboundBytes(long var1) {
      this.decrementPendingOutboundBytes(var1, true, true);
   }

   private void decrementPendingOutboundBytes(long var1, boolean var3, boolean var4) {
      if (var1 != 0L) {
         long var5 = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -var1);
         if (var4 && var5 < (long)this.channel.config().getWriteBufferLowWaterMark()) {
            this.setWritable(var3);
         }

      }
   }

   private static long total(Object var0) {
      if (var0 instanceof ByteBuf) {
         return (long)((ByteBuf)var0).readableBytes();
      } else if (var0 instanceof FileRegion) {
         return ((FileRegion)var0).count();
      } else {
         return var0 instanceof ByteBufHolder ? (long)((ByteBufHolder)var0).content().readableBytes() : -1L;
      }
   }

   public Object current() {
      ChannelOutboundBuffer.Entry var1 = this.flushedEntry;
      return var1 == null ? null : var1.msg;
   }

   public void progress(long var1) {
      ChannelOutboundBuffer.Entry var3 = this.flushedEntry;

      assert var3 != null;

      ChannelPromise var4 = var3.promise;
      if (var4 instanceof ChannelProgressivePromise) {
         long var5 = var3.progress + var1;
         var3.progress = var5;
         ((ChannelProgressivePromise)var4).tryProgress(var5, var3.total);
      }

   }

   public boolean remove() {
      ChannelOutboundBuffer.Entry var1 = this.flushedEntry;
      if (var1 == null) {
         this.clearNioBuffers();
         return false;
      } else {
         Object var2 = var1.msg;
         ChannelPromise var3 = var1.promise;
         int var4 = var1.pendingSize;
         this.removeEntry(var1);
         if (!var1.cancelled) {
            ReferenceCountUtil.safeRelease(var2);
            safeSuccess(var3);
            this.decrementPendingOutboundBytes((long)var4, false, true);
         }

         var1.recycle();
         return true;
      }
   }

   public boolean remove(Throwable var1) {
      return this.remove0(var1, true);
   }

   private boolean remove0(Throwable var1, boolean var2) {
      ChannelOutboundBuffer.Entry var3 = this.flushedEntry;
      if (var3 == null) {
         this.clearNioBuffers();
         return false;
      } else {
         Object var4 = var3.msg;
         ChannelPromise var5 = var3.promise;
         int var6 = var3.pendingSize;
         this.removeEntry(var3);
         if (!var3.cancelled) {
            ReferenceCountUtil.safeRelease(var4);
            safeFail(var5, var1);
            this.decrementPendingOutboundBytes((long)var6, false, var2);
         }

         var3.recycle();
         return true;
      }
   }

   private void removeEntry(ChannelOutboundBuffer.Entry var1) {
      if (--this.flushed == 0) {
         this.flushedEntry = null;
         if (var1 == this.tailEntry) {
            this.tailEntry = null;
            this.unflushedEntry = null;
         }
      } else {
         this.flushedEntry = var1.next;
      }

   }

   public void removeBytes(long var1) {
      while(true) {
         Object var3 = this.current();
         if (!(var3 instanceof ByteBuf)) {
            assert var1 == 0L;
         } else {
            ByteBuf var4 = (ByteBuf)var3;
            int var5 = var4.readerIndex();
            int var6 = var4.writerIndex() - var5;
            if ((long)var6 <= var1) {
               if (var1 != 0L) {
                  this.progress((long)var6);
                  var1 -= (long)var6;
               }

               this.remove();
               continue;
            }

            if (var1 != 0L) {
               var4.readerIndex(var5 + (int)var1);
               this.progress(var1);
            }
         }

         this.clearNioBuffers();
         return;
      }
   }

   private void clearNioBuffers() {
      int var1 = this.nioBufferCount;
      if (var1 > 0) {
         this.nioBufferCount = 0;
         Arrays.fill((Object[])NIO_BUFFERS.get(), 0, var1, (Object)null);
      }

   }

   public ByteBuffer[] nioBuffers() {
      return this.nioBuffers(2147483647, 2147483647L);
   }

   public ByteBuffer[] nioBuffers(int var1, long var2) {
      assert var1 > 0;

      assert var2 > 0L;

      long var4 = 0L;
      int var6 = 0;
      InternalThreadLocalMap var7 = InternalThreadLocalMap.get();
      ByteBuffer[] var8 = (ByteBuffer[])NIO_BUFFERS.get(var7);

      for(ChannelOutboundBuffer.Entry var9 = this.flushedEntry; this.isFlushedEntry(var9) && var9.msg instanceof ByteBuf; var9 = var9.next) {
         if (!var9.cancelled) {
            ByteBuf var10 = (ByteBuf)var9.msg;
            int var11 = var10.readerIndex();
            int var12 = var10.writerIndex() - var11;
            if (var12 > 0) {
               if (var2 - (long)var12 < var4 && var6 != 0) {
                  break;
               }

               var4 += (long)var12;
               int var13 = var9.count;
               if (var13 == -1) {
                  var9.count = var13 = var10.nioBufferCount();
               }

               int var14 = Math.min(var1, var6 + var13);
               if (var14 > var8.length) {
                  var8 = expandNioBufferArray(var8, var14, var6);
                  NIO_BUFFERS.set(var7, var8);
               }

               if (var13 == 1) {
                  ByteBuffer var18 = var9.buf;
                  if (var18 == null) {
                     var9.buf = var18 = var10.internalNioBuffer(var11, var12);
                  }

                  var8[var6++] = var18;
               } else {
                  ByteBuffer[] var15 = var9.bufs;
                  if (var15 == null) {
                     var9.bufs = var15 = var10.nioBuffers();
                  }

                  for(int var16 = 0; var16 < var15.length && var6 < var1; ++var16) {
                     ByteBuffer var17 = var15[var16];
                     if (var17 == null) {
                        break;
                     }

                     if (var17.hasRemaining()) {
                        var8[var6++] = var17;
                     }
                  }
               }

               if (var6 == var1) {
                  break;
               }
            }
         }
      }

      this.nioBufferCount = var6;
      this.nioBufferSize = var4;
      return var8;
   }

   private static ByteBuffer[] expandNioBufferArray(ByteBuffer[] var0, int var1, int var2) {
      int var3 = var0.length;

      do {
         var3 <<= 1;
         if (var3 < 0) {
            throw new IllegalStateException();
         }
      } while(var1 > var3);

      ByteBuffer[] var4 = new ByteBuffer[var3];
      System.arraycopy(var0, 0, var4, 0, var2);
      return var4;
   }

   public int nioBufferCount() {
      return this.nioBufferCount;
   }

   public long nioBufferSize() {
      return this.nioBufferSize;
   }

   public boolean isWritable() {
      return this.unwritable == 0;
   }

   public boolean getUserDefinedWritability(int var1) {
      return (this.unwritable & writabilityMask(var1)) == 0;
   }

   public void setUserDefinedWritability(int var1, boolean var2) {
      if (var2) {
         this.setUserDefinedWritability(var1);
      } else {
         this.clearUserDefinedWritability(var1);
      }

   }

   private void setUserDefinedWritability(int var1) {
      int var2 = ~writabilityMask(var1);

      int var3;
      int var4;
      do {
         var3 = this.unwritable;
         var4 = var3 & var2;
      } while(!UNWRITABLE_UPDATER.compareAndSet(this, var3, var4));

      if (var3 != 0 && var4 == 0) {
         this.fireChannelWritabilityChanged(true);
      }

   }

   private void clearUserDefinedWritability(int var1) {
      int var2 = writabilityMask(var1);

      int var3;
      int var4;
      do {
         var3 = this.unwritable;
         var4 = var3 | var2;
      } while(!UNWRITABLE_UPDATER.compareAndSet(this, var3, var4));

      if (var3 == 0 && var4 != 0) {
         this.fireChannelWritabilityChanged(true);
      }

   }

   private static int writabilityMask(int var0) {
      if (var0 >= 1 && var0 <= 31) {
         return 1 << var0;
      } else {
         throw new IllegalArgumentException("index: " + var0 + " (expected: 1~31)");
      }
   }

   private void setWritable(boolean var1) {
      int var2;
      int var3;
      do {
         var2 = this.unwritable;
         var3 = var2 & -2;
      } while(!UNWRITABLE_UPDATER.compareAndSet(this, var2, var3));

      if (var2 != 0 && var3 == 0) {
         this.fireChannelWritabilityChanged(var1);
      }

   }

   private void setUnwritable(boolean var1) {
      int var2;
      int var3;
      do {
         var2 = this.unwritable;
         var3 = var2 | 1;
      } while(!UNWRITABLE_UPDATER.compareAndSet(this, var2, var3));

      if (var2 == 0 && var3 != 0) {
         this.fireChannelWritabilityChanged(var1);
      }

   }

   private void fireChannelWritabilityChanged(boolean var1) {
      final ChannelPipeline var2 = this.channel.pipeline();
      if (var1) {
         Runnable var3 = this.fireChannelWritabilityChangedTask;
         if (var3 == null) {
            this.fireChannelWritabilityChangedTask = var3 = new Runnable() {
               public void run() {
                  var2.fireChannelWritabilityChanged();
               }
            };
         }

         this.channel.eventLoop().execute(var3);
      } else {
         var2.fireChannelWritabilityChanged();
      }

   }

   public int size() {
      return this.flushed;
   }

   public boolean isEmpty() {
      return this.flushed == 0;
   }

   void failFlushed(Throwable var1, boolean var2) {
      if (!this.inFail) {
         try {
            this.inFail = true;

            while(this.remove0(var1, var2)) {
            }
         } finally {
            this.inFail = false;
         }

      }
   }

   void close(final Throwable var1, final boolean var2) {
      if (this.inFail) {
         this.channel.eventLoop().execute(new Runnable() {
            public void run() {
               ChannelOutboundBuffer.this.close(var1, var2);
            }
         });
      } else {
         this.inFail = true;
         if (!var2 && this.channel.isOpen()) {
            throw new IllegalStateException("close() must be invoked after the channel is closed.");
         } else if (!this.isEmpty()) {
            throw new IllegalStateException("close() must be invoked after all flushed writes are handled.");
         } else {
            try {
               for(ChannelOutboundBuffer.Entry var3 = this.unflushedEntry; var3 != null; var3 = var3.recycleAndGetNext()) {
                  int var4 = var3.pendingSize;
                  TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, (long)(-var4));
                  if (!var3.cancelled) {
                     ReferenceCountUtil.safeRelease(var3.msg);
                     safeFail(var3.promise, var1);
                  }
               }
            } finally {
               this.inFail = false;
            }

            this.clearNioBuffers();
         }
      }
   }

   void close(ClosedChannelException var1) {
      this.close(var1, false);
   }

   private static void safeSuccess(ChannelPromise var0) {
      PromiseNotificationUtil.trySuccess(var0, (Object)null, var0 instanceof VoidChannelPromise ? null : logger);
   }

   private static void safeFail(ChannelPromise var0, Throwable var1) {
      PromiseNotificationUtil.tryFailure(var0, var1, var0 instanceof VoidChannelPromise ? null : logger);
   }

   /** @deprecated */
   @Deprecated
   public void recycle() {
   }

   public long totalPendingWriteBytes() {
      return this.totalPendingSize;
   }

   public long bytesBeforeUnwritable() {
      long var1 = (long)this.channel.config().getWriteBufferHighWaterMark() - this.totalPendingSize;
      if (var1 > 0L) {
         return this.isWritable() ? var1 : 0L;
      } else {
         return 0L;
      }
   }

   public long bytesBeforeWritable() {
      long var1 = this.totalPendingSize - (long)this.channel.config().getWriteBufferLowWaterMark();
      if (var1 > 0L) {
         return this.isWritable() ? 0L : var1;
      } else {
         return 0L;
      }
   }

   public void forEachFlushedMessage(ChannelOutboundBuffer.MessageProcessor var1) throws Exception {
      if (var1 == null) {
         throw new NullPointerException("processor");
      } else {
         ChannelOutboundBuffer.Entry var2 = this.flushedEntry;
         if (var2 != null) {
            do {
               if (!var2.cancelled && !var1.processMessage(var2.msg)) {
                  return;
               }

               var2 = var2.next;
            } while(this.isFlushedEntry(var2));

         }
      }
   }

   private boolean isFlushedEntry(ChannelOutboundBuffer.Entry var1) {
      return var1 != null && var1 != this.unflushedEntry;
   }

   static final class Entry {
      private static final Recycler<ChannelOutboundBuffer.Entry> RECYCLER = new Recycler<ChannelOutboundBuffer.Entry>() {
         protected ChannelOutboundBuffer.Entry newObject(Recycler.Handle<ChannelOutboundBuffer.Entry> var1) {
            return new ChannelOutboundBuffer.Entry(var1);
         }
      };
      private final Recycler.Handle<ChannelOutboundBuffer.Entry> handle;
      ChannelOutboundBuffer.Entry next;
      Object msg;
      ByteBuffer[] bufs;
      ByteBuffer buf;
      ChannelPromise promise;
      long progress;
      long total;
      int pendingSize;
      int count;
      boolean cancelled;

      private Entry(Recycler.Handle<ChannelOutboundBuffer.Entry> var1) {
         super();
         this.count = -1;
         this.handle = var1;
      }

      static ChannelOutboundBuffer.Entry newInstance(Object var0, int var1, long var2, ChannelPromise var4) {
         ChannelOutboundBuffer.Entry var5 = (ChannelOutboundBuffer.Entry)RECYCLER.get();
         var5.msg = var0;
         var5.pendingSize = var1 + ChannelOutboundBuffer.CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD;
         var5.total = var2;
         var5.promise = var4;
         return var5;
      }

      int cancel() {
         if (!this.cancelled) {
            this.cancelled = true;
            int var1 = this.pendingSize;
            ReferenceCountUtil.safeRelease(this.msg);
            this.msg = Unpooled.EMPTY_BUFFER;
            this.pendingSize = 0;
            this.total = 0L;
            this.progress = 0L;
            this.bufs = null;
            this.buf = null;
            return var1;
         } else {
            return 0;
         }
      }

      void recycle() {
         this.next = null;
         this.bufs = null;
         this.buf = null;
         this.msg = null;
         this.promise = null;
         this.progress = 0L;
         this.total = 0L;
         this.pendingSize = 0;
         this.count = -1;
         this.cancelled = false;
         this.handle.recycle(this);
      }

      ChannelOutboundBuffer.Entry recycleAndGetNext() {
         ChannelOutboundBuffer.Entry var1 = this.next;
         this.recycle();
         return var1;
      }

      // $FF: synthetic method
      Entry(Recycler.Handle var1, Object var2) {
         this(var1);
      }
   }

   public interface MessageProcessor {
      boolean processMessage(Object var1) throws Exception;
   }
}
