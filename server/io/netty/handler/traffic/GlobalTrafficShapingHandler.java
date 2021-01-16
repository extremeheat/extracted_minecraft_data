package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class GlobalTrafficShapingHandler extends AbstractTrafficShapingHandler {
   private final ConcurrentMap<Integer, GlobalTrafficShapingHandler.PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();
   private final AtomicLong queuesSize = new AtomicLong();
   long maxGlobalWriteSize = 419430400L;

   void createGlobalTrafficCounter(ScheduledExecutorService var1) {
      if (var1 == null) {
         throw new NullPointerException("executor");
      } else {
         TrafficCounter var2 = new TrafficCounter(this, var1, "GlobalTC", this.checkInterval);
         this.setTrafficCounter(var2);
         var2.start();
      }
   }

   protected int userDefinedWritabilityIndex() {
      return 2;
   }

   public GlobalTrafficShapingHandler(ScheduledExecutorService var1, long var2, long var4, long var6, long var8) {
      super(var2, var4, var6, var8);
      this.createGlobalTrafficCounter(var1);
   }

   public GlobalTrafficShapingHandler(ScheduledExecutorService var1, long var2, long var4, long var6) {
      super(var2, var4, var6);
      this.createGlobalTrafficCounter(var1);
   }

   public GlobalTrafficShapingHandler(ScheduledExecutorService var1, long var2, long var4) {
      super(var2, var4);
      this.createGlobalTrafficCounter(var1);
   }

   public GlobalTrafficShapingHandler(ScheduledExecutorService var1, long var2) {
      super(var2);
      this.createGlobalTrafficCounter(var1);
   }

   public GlobalTrafficShapingHandler(EventExecutor var1) {
      super();
      this.createGlobalTrafficCounter(var1);
   }

   public long getMaxGlobalWriteSize() {
      return this.maxGlobalWriteSize;
   }

   public void setMaxGlobalWriteSize(long var1) {
      this.maxGlobalWriteSize = var1;
   }

   public long queuesSize() {
      return this.queuesSize.get();
   }

   public final void release() {
      this.trafficCounter.stop();
   }

   private GlobalTrafficShapingHandler.PerChannel getOrSetPerChannel(ChannelHandlerContext var1) {
      Channel var2 = var1.channel();
      Integer var3 = var2.hashCode();
      GlobalTrafficShapingHandler.PerChannel var4 = (GlobalTrafficShapingHandler.PerChannel)this.channelQueues.get(var3);
      if (var4 == null) {
         var4 = new GlobalTrafficShapingHandler.PerChannel();
         var4.messagesQueue = new ArrayDeque();
         var4.queueSize = 0L;
         var4.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
         var4.lastWriteTimestamp = var4.lastReadTimestamp;
         this.channelQueues.put(var3, var4);
      }

      return var4;
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.getOrSetPerChannel(var1);
      super.handlerAdded(var1);
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      Channel var2 = var1.channel();
      Integer var3 = var2.hashCode();
      GlobalTrafficShapingHandler.PerChannel var4 = (GlobalTrafficShapingHandler.PerChannel)this.channelQueues.remove(var3);
      if (var4 != null) {
         synchronized(var4) {
            Iterator var6;
            GlobalTrafficShapingHandler.ToSend var7;
            if (var2.isActive()) {
               var6 = var4.messagesQueue.iterator();

               while(var6.hasNext()) {
                  var7 = (GlobalTrafficShapingHandler.ToSend)var6.next();
                  long var8 = this.calculateSize(var7.toSend);
                  this.trafficCounter.bytesRealWriteFlowControl(var8);
                  var4.queueSize -= var8;
                  this.queuesSize.addAndGet(-var8);
                  var1.write(var7.toSend, var7.promise);
               }
            } else {
               this.queuesSize.addAndGet(-var4.queueSize);
               var6 = var4.messagesQueue.iterator();

               while(var6.hasNext()) {
                  var7 = (GlobalTrafficShapingHandler.ToSend)var6.next();
                  if (var7.toSend instanceof ByteBuf) {
                     ((ByteBuf)var7.toSend).release();
                  }
               }
            }

            var4.messagesQueue.clear();
         }
      }

      this.releaseWriteSuspended(var1);
      this.releaseReadSuspended(var1);
      super.handlerRemoved(var1);
   }

   long checkWaitReadTime(ChannelHandlerContext var1, long var2, long var4) {
      Integer var6 = var1.channel().hashCode();
      GlobalTrafficShapingHandler.PerChannel var7 = (GlobalTrafficShapingHandler.PerChannel)this.channelQueues.get(var6);
      if (var7 != null && var2 > this.maxTime && var4 + var2 - var7.lastReadTimestamp > this.maxTime) {
         var2 = this.maxTime;
      }

      return var2;
   }

   void informReadOperation(ChannelHandlerContext var1, long var2) {
      Integer var4 = var1.channel().hashCode();
      GlobalTrafficShapingHandler.PerChannel var5 = (GlobalTrafficShapingHandler.PerChannel)this.channelQueues.get(var4);
      if (var5 != null) {
         var5.lastReadTimestamp = var2;
      }

   }

   void submitWrite(final ChannelHandlerContext var1, Object var2, long var3, long var5, long var7, ChannelPromise var9) {
      Channel var10 = var1.channel();
      Integer var11 = var10.hashCode();
      final GlobalTrafficShapingHandler.PerChannel var12 = (GlobalTrafficShapingHandler.PerChannel)this.channelQueues.get(var11);
      if (var12 == null) {
         var12 = this.getOrSetPerChannel(var1);
      }

      long var14 = var5;
      boolean var16 = false;
      GlobalTrafficShapingHandler.ToSend var13;
      synchronized(var12) {
         if (var5 == 0L && var12.messagesQueue.isEmpty()) {
            this.trafficCounter.bytesRealWriteFlowControl(var3);
            var1.write(var2, var9);
            var12.lastWriteTimestamp = var7;
            return;
         }

         if (var14 > this.maxTime && var7 + var14 - var12.lastWriteTimestamp > this.maxTime) {
            var14 = this.maxTime;
         }

         var13 = new GlobalTrafficShapingHandler.ToSend(var14 + var7, var2, var3, var9);
         var12.messagesQueue.addLast(var13);
         var12.queueSize += var3;
         this.queuesSize.addAndGet(var3);
         this.checkWriteSuspend(var1, var14, var12.queueSize);
         if (this.queuesSize.get() > this.maxGlobalWriteSize) {
            var16 = true;
         }
      }

      if (var16) {
         this.setUserDefinedWritability(var1, false);
      }

      final long var17 = var13.relativeTimeAction;
      var1.executor().schedule(new Runnable() {
         public void run() {
            GlobalTrafficShapingHandler.this.sendAllValid(var1, var12, var17);
         }
      }, var14, TimeUnit.MILLISECONDS);
   }

   private void sendAllValid(ChannelHandlerContext var1, GlobalTrafficShapingHandler.PerChannel var2, long var3) {
      synchronized(var2) {
         GlobalTrafficShapingHandler.ToSend var6 = (GlobalTrafficShapingHandler.ToSend)var2.messagesQueue.pollFirst();

         while(true) {
            if (var6 != null) {
               if (var6.relativeTimeAction <= var3) {
                  long var7 = var6.size;
                  this.trafficCounter.bytesRealWriteFlowControl(var7);
                  var2.queueSize -= var7;
                  this.queuesSize.addAndGet(-var7);
                  var1.write(var6.toSend, var6.promise);
                  var2.lastWriteTimestamp = var3;
                  var6 = (GlobalTrafficShapingHandler.ToSend)var2.messagesQueue.pollFirst();
                  continue;
               }

               var2.messagesQueue.addFirst(var6);
            }

            if (var2.messagesQueue.isEmpty()) {
               this.releaseWriteSuspended(var1);
            }
            break;
         }
      }

      var1.flush();
   }

   private static final class ToSend {
      final long relativeTimeAction;
      final Object toSend;
      final long size;
      final ChannelPromise promise;

      private ToSend(long var1, Object var3, long var4, ChannelPromise var6) {
         super();
         this.relativeTimeAction = var1;
         this.toSend = var3;
         this.size = var4;
         this.promise = var6;
      }

      // $FF: synthetic method
      ToSend(long var1, Object var3, long var4, ChannelPromise var6, Object var7) {
         this(var1, var3, var4, var6);
      }
   }

   private static final class PerChannel {
      ArrayDeque<GlobalTrafficShapingHandler.ToSend> messagesQueue;
      long queueSize;
      long lastWriteTimestamp;
      long lastReadTimestamp;

      private PerChannel() {
         super();
      }

      // $FF: synthetic method
      PerChannel(Object var1) {
         this();
      }
   }
}
