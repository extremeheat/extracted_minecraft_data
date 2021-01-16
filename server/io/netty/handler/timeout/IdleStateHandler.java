package io.netty.handler.timeout;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class IdleStateHandler extends ChannelDuplexHandler {
   private static final long MIN_TIMEOUT_NANOS;
   private final ChannelFutureListener writeListener;
   private final boolean observeOutput;
   private final long readerIdleTimeNanos;
   private final long writerIdleTimeNanos;
   private final long allIdleTimeNanos;
   private ScheduledFuture<?> readerIdleTimeout;
   private long lastReadTime;
   private boolean firstReaderIdleEvent;
   private ScheduledFuture<?> writerIdleTimeout;
   private long lastWriteTime;
   private boolean firstWriterIdleEvent;
   private ScheduledFuture<?> allIdleTimeout;
   private boolean firstAllIdleEvent;
   private byte state;
   private boolean reading;
   private long lastChangeCheckTimeStamp;
   private int lastMessageHashCode;
   private long lastPendingWriteBytes;

   public IdleStateHandler(int var1, int var2, int var3) {
      this((long)var1, (long)var2, (long)var3, TimeUnit.SECONDS);
   }

   public IdleStateHandler(long var1, long var3, long var5, TimeUnit var7) {
      this(false, var1, var3, var5, var7);
   }

   public IdleStateHandler(boolean var1, long var2, long var4, long var6, TimeUnit var8) {
      super();
      this.writeListener = new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            IdleStateHandler.this.lastWriteTime = IdleStateHandler.this.ticksInNanos();
            IdleStateHandler.this.firstWriterIdleEvent = IdleStateHandler.this.firstAllIdleEvent = true;
         }
      };
      this.firstReaderIdleEvent = true;
      this.firstWriterIdleEvent = true;
      this.firstAllIdleEvent = true;
      if (var8 == null) {
         throw new NullPointerException("unit");
      } else {
         this.observeOutput = var1;
         if (var2 <= 0L) {
            this.readerIdleTimeNanos = 0L;
         } else {
            this.readerIdleTimeNanos = Math.max(var8.toNanos(var2), MIN_TIMEOUT_NANOS);
         }

         if (var4 <= 0L) {
            this.writerIdleTimeNanos = 0L;
         } else {
            this.writerIdleTimeNanos = Math.max(var8.toNanos(var4), MIN_TIMEOUT_NANOS);
         }

         if (var6 <= 0L) {
            this.allIdleTimeNanos = 0L;
         } else {
            this.allIdleTimeNanos = Math.max(var8.toNanos(var6), MIN_TIMEOUT_NANOS);
         }

      }
   }

   public long getReaderIdleTimeInMillis() {
      return TimeUnit.NANOSECONDS.toMillis(this.readerIdleTimeNanos);
   }

   public long getWriterIdleTimeInMillis() {
      return TimeUnit.NANOSECONDS.toMillis(this.writerIdleTimeNanos);
   }

   public long getAllIdleTimeInMillis() {
      return TimeUnit.NANOSECONDS.toMillis(this.allIdleTimeNanos);
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      if (var1.channel().isActive() && var1.channel().isRegistered()) {
         this.initialize(var1);
      }

   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.destroy();
   }

   public void channelRegistered(ChannelHandlerContext var1) throws Exception {
      if (var1.channel().isActive()) {
         this.initialize(var1);
      }

      super.channelRegistered(var1);
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      this.initialize(var1);
      super.channelActive(var1);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.destroy();
      super.channelInactive(var1);
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
         this.reading = true;
         this.firstReaderIdleEvent = this.firstAllIdleEvent = true;
      }

      var1.fireChannelRead(var2);
   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      if ((this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) && this.reading) {
         this.lastReadTime = this.ticksInNanos();
         this.reading = false;
      }

      var1.fireChannelReadComplete();
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (this.writerIdleTimeNanos <= 0L && this.allIdleTimeNanos <= 0L) {
         var1.write(var2, var3);
      } else {
         var1.write(var2, var3.unvoid()).addListener(this.writeListener);
      }

   }

   private void initialize(ChannelHandlerContext var1) {
      switch(this.state) {
      case 1:
      case 2:
         return;
      default:
         this.state = 1;
         this.initOutputChanged(var1);
         this.lastReadTime = this.lastWriteTime = this.ticksInNanos();
         if (this.readerIdleTimeNanos > 0L) {
            this.readerIdleTimeout = this.schedule(var1, new IdleStateHandler.ReaderIdleTimeoutTask(var1), this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
         }

         if (this.writerIdleTimeNanos > 0L) {
            this.writerIdleTimeout = this.schedule(var1, new IdleStateHandler.WriterIdleTimeoutTask(var1), this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
         }

         if (this.allIdleTimeNanos > 0L) {
            this.allIdleTimeout = this.schedule(var1, new IdleStateHandler.AllIdleTimeoutTask(var1), this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
         }

      }
   }

   long ticksInNanos() {
      return System.nanoTime();
   }

   ScheduledFuture<?> schedule(ChannelHandlerContext var1, Runnable var2, long var3, TimeUnit var5) {
      return var1.executor().schedule(var2, var3, var5);
   }

   private void destroy() {
      this.state = 2;
      if (this.readerIdleTimeout != null) {
         this.readerIdleTimeout.cancel(false);
         this.readerIdleTimeout = null;
      }

      if (this.writerIdleTimeout != null) {
         this.writerIdleTimeout.cancel(false);
         this.writerIdleTimeout = null;
      }

      if (this.allIdleTimeout != null) {
         this.allIdleTimeout.cancel(false);
         this.allIdleTimeout = null;
      }

   }

   protected void channelIdle(ChannelHandlerContext var1, IdleStateEvent var2) throws Exception {
      var1.fireUserEventTriggered(var2);
   }

   protected IdleStateEvent newIdleStateEvent(IdleState var1, boolean var2) {
      switch(var1) {
      case ALL_IDLE:
         return var2 ? IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT : IdleStateEvent.ALL_IDLE_STATE_EVENT;
      case READER_IDLE:
         return var2 ? IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT : IdleStateEvent.READER_IDLE_STATE_EVENT;
      case WRITER_IDLE:
         return var2 ? IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT : IdleStateEvent.WRITER_IDLE_STATE_EVENT;
      default:
         throw new IllegalArgumentException("Unhandled: state=" + var1 + ", first=" + var2);
      }
   }

   private void initOutputChanged(ChannelHandlerContext var1) {
      if (this.observeOutput) {
         Channel var2 = var1.channel();
         Channel.Unsafe var3 = var2.unsafe();
         ChannelOutboundBuffer var4 = var3.outboundBuffer();
         if (var4 != null) {
            this.lastMessageHashCode = System.identityHashCode(var4.current());
            this.lastPendingWriteBytes = var4.totalPendingWriteBytes();
         }
      }

   }

   private boolean hasOutputChanged(ChannelHandlerContext var1, boolean var2) {
      if (this.observeOutput) {
         if (this.lastChangeCheckTimeStamp != this.lastWriteTime) {
            this.lastChangeCheckTimeStamp = this.lastWriteTime;
            if (!var2) {
               return true;
            }
         }

         Channel var3 = var1.channel();
         Channel.Unsafe var4 = var3.unsafe();
         ChannelOutboundBuffer var5 = var4.outboundBuffer();
         if (var5 != null) {
            int var6 = System.identityHashCode(var5.current());
            long var7 = var5.totalPendingWriteBytes();
            if (var6 != this.lastMessageHashCode || var7 != this.lastPendingWriteBytes) {
               this.lastMessageHashCode = var6;
               this.lastPendingWriteBytes = var7;
               if (!var2) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   static {
      MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
   }

   private final class AllIdleTimeoutTask extends IdleStateHandler.AbstractIdleTask {
      AllIdleTimeoutTask(ChannelHandlerContext var2) {
         super(var2);
      }

      protected void run(ChannelHandlerContext var1) {
         long var2 = IdleStateHandler.this.allIdleTimeNanos;
         if (!IdleStateHandler.this.reading) {
            var2 -= IdleStateHandler.this.ticksInNanos() - Math.max(IdleStateHandler.this.lastReadTime, IdleStateHandler.this.lastWriteTime);
         }

         if (var2 <= 0L) {
            IdleStateHandler.this.allIdleTimeout = IdleStateHandler.this.schedule(var1, this, IdleStateHandler.this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
            boolean var4 = IdleStateHandler.this.firstAllIdleEvent;
            IdleStateHandler.this.firstAllIdleEvent = false;

            try {
               if (IdleStateHandler.this.hasOutputChanged(var1, var4)) {
                  return;
               }

               IdleStateEvent var5 = IdleStateHandler.this.newIdleStateEvent(IdleState.ALL_IDLE, var4);
               IdleStateHandler.this.channelIdle(var1, var5);
            } catch (Throwable var6) {
               var1.fireExceptionCaught(var6);
            }
         } else {
            IdleStateHandler.this.allIdleTimeout = IdleStateHandler.this.schedule(var1, this, var2, TimeUnit.NANOSECONDS);
         }

      }
   }

   private final class WriterIdleTimeoutTask extends IdleStateHandler.AbstractIdleTask {
      WriterIdleTimeoutTask(ChannelHandlerContext var2) {
         super(var2);
      }

      protected void run(ChannelHandlerContext var1) {
         long var2 = IdleStateHandler.this.lastWriteTime;
         long var4 = IdleStateHandler.this.writerIdleTimeNanos - (IdleStateHandler.this.ticksInNanos() - var2);
         if (var4 <= 0L) {
            IdleStateHandler.this.writerIdleTimeout = IdleStateHandler.this.schedule(var1, this, IdleStateHandler.this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
            boolean var6 = IdleStateHandler.this.firstWriterIdleEvent;
            IdleStateHandler.this.firstWriterIdleEvent = false;

            try {
               if (IdleStateHandler.this.hasOutputChanged(var1, var6)) {
                  return;
               }

               IdleStateEvent var7 = IdleStateHandler.this.newIdleStateEvent(IdleState.WRITER_IDLE, var6);
               IdleStateHandler.this.channelIdle(var1, var7);
            } catch (Throwable var8) {
               var1.fireExceptionCaught(var8);
            }
         } else {
            IdleStateHandler.this.writerIdleTimeout = IdleStateHandler.this.schedule(var1, this, var4, TimeUnit.NANOSECONDS);
         }

      }
   }

   private final class ReaderIdleTimeoutTask extends IdleStateHandler.AbstractIdleTask {
      ReaderIdleTimeoutTask(ChannelHandlerContext var2) {
         super(var2);
      }

      protected void run(ChannelHandlerContext var1) {
         long var2 = IdleStateHandler.this.readerIdleTimeNanos;
         if (!IdleStateHandler.this.reading) {
            var2 -= IdleStateHandler.this.ticksInNanos() - IdleStateHandler.this.lastReadTime;
         }

         if (var2 <= 0L) {
            IdleStateHandler.this.readerIdleTimeout = IdleStateHandler.this.schedule(var1, this, IdleStateHandler.this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
            boolean var4 = IdleStateHandler.this.firstReaderIdleEvent;
            IdleStateHandler.this.firstReaderIdleEvent = false;

            try {
               IdleStateEvent var5 = IdleStateHandler.this.newIdleStateEvent(IdleState.READER_IDLE, var4);
               IdleStateHandler.this.channelIdle(var1, var5);
            } catch (Throwable var6) {
               var1.fireExceptionCaught(var6);
            }
         } else {
            IdleStateHandler.this.readerIdleTimeout = IdleStateHandler.this.schedule(var1, this, var2, TimeUnit.NANOSECONDS);
         }

      }
   }

   private abstract static class AbstractIdleTask implements Runnable {
      private final ChannelHandlerContext ctx;

      AbstractIdleTask(ChannelHandlerContext var1) {
         super();
         this.ctx = var1;
      }

      public void run() {
         if (this.ctx.channel().isOpen()) {
            this.run(this.ctx);
         }
      }

      protected abstract void run(ChannelHandlerContext var1);
   }
}
