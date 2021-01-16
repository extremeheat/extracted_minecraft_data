package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class GlobalChannelTrafficShapingHandler extends AbstractTrafficShapingHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalChannelTrafficShapingHandler.class);
   final ConcurrentMap<Integer, GlobalChannelTrafficShapingHandler.PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();
   private final AtomicLong queuesSize = new AtomicLong();
   private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
   private final AtomicLong cumulativeReadBytes = new AtomicLong();
   volatile long maxGlobalWriteSize = 419430400L;
   private volatile long writeChannelLimit;
   private volatile long readChannelLimit;
   private static final float DEFAULT_DEVIATION = 0.1F;
   private static final float MAX_DEVIATION = 0.4F;
   private static final float DEFAULT_SLOWDOWN = 0.4F;
   private static final float DEFAULT_ACCELERATION = -0.1F;
   private volatile float maxDeviation;
   private volatile float accelerationFactor;
   private volatile float slowDownFactor;
   private volatile boolean readDeviationActive;
   private volatile boolean writeDeviationActive;

   void createGlobalTrafficCounter(ScheduledExecutorService var1) {
      this.setMaxDeviation(0.1F, 0.4F, -0.1F);
      if (var1 == null) {
         throw new IllegalArgumentException("Executor must not be null");
      } else {
         GlobalChannelTrafficCounter var2 = new GlobalChannelTrafficCounter(this, var1, "GlobalChannelTC", this.checkInterval);
         this.setTrafficCounter(var2);
         var2.start();
      }
   }

   protected int userDefinedWritabilityIndex() {
      return 3;
   }

   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService var1, long var2, long var4, long var6, long var8, long var10, long var12) {
      super(var2, var4, var10, var12);
      this.createGlobalTrafficCounter(var1);
      this.writeChannelLimit = var6;
      this.readChannelLimit = var8;
   }

   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService var1, long var2, long var4, long var6, long var8, long var10) {
      super(var2, var4, var10);
      this.writeChannelLimit = var6;
      this.readChannelLimit = var8;
      this.createGlobalTrafficCounter(var1);
   }

   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService var1, long var2, long var4, long var6, long var8) {
      super(var2, var4);
      this.writeChannelLimit = var6;
      this.readChannelLimit = var8;
      this.createGlobalTrafficCounter(var1);
   }

   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService var1, long var2) {
      super(var2);
      this.createGlobalTrafficCounter(var1);
   }

   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService var1) {
      super();
      this.createGlobalTrafficCounter(var1);
   }

   public float maxDeviation() {
      return this.maxDeviation;
   }

   public float accelerationFactor() {
      return this.accelerationFactor;
   }

   public float slowDownFactor() {
      return this.slowDownFactor;
   }

   public void setMaxDeviation(float var1, float var2, float var3) {
      if (var1 > 0.4F) {
         throw new IllegalArgumentException("maxDeviation must be <= 0.4");
      } else if (var2 < 0.0F) {
         throw new IllegalArgumentException("slowDownFactor must be >= 0");
      } else if (var3 > 0.0F) {
         throw new IllegalArgumentException("accelerationFactor must be <= 0");
      } else {
         this.maxDeviation = var1;
         this.accelerationFactor = 1.0F + var3;
         this.slowDownFactor = 1.0F + var2;
      }
   }

   private void computeDeviationCumulativeBytes() {
      long var1 = 0L;
      long var3 = 0L;
      long var5 = 9223372036854775807L;
      long var7 = 9223372036854775807L;
      Iterator var9 = this.channelQueues.values().iterator();

      while(var9.hasNext()) {
         GlobalChannelTrafficShapingHandler.PerChannel var10 = (GlobalChannelTrafficShapingHandler.PerChannel)var9.next();
         long var11 = var10.channelTrafficCounter.cumulativeWrittenBytes();
         if (var1 < var11) {
            var1 = var11;
         }

         if (var5 > var11) {
            var5 = var11;
         }

         var11 = var10.channelTrafficCounter.cumulativeReadBytes();
         if (var3 < var11) {
            var3 = var11;
         }

         if (var7 > var11) {
            var7 = var11;
         }
      }

      boolean var13 = this.channelQueues.size() > 1;
      this.readDeviationActive = var13 && var7 < var3 / 2L;
      this.writeDeviationActive = var13 && var5 < var1 / 2L;
      this.cumulativeWrittenBytes.set(var1);
      this.cumulativeReadBytes.set(var3);
   }

   protected void doAccounting(TrafficCounter var1) {
      this.computeDeviationCumulativeBytes();
      super.doAccounting(var1);
   }

   private long computeBalancedWait(float var1, float var2, long var3) {
      if (var2 == 0.0F) {
         return var3;
      } else {
         float var5 = var1 / var2;
         if (var5 > this.maxDeviation) {
            if (var5 < 1.0F - this.maxDeviation) {
               return var3;
            }

            var5 = this.slowDownFactor;
            if (var3 < 10L) {
               var3 = 10L;
            }
         } else {
            var5 = this.accelerationFactor;
         }

         return (long)((float)var3 * var5);
      }
   }

   public long getMaxGlobalWriteSize() {
      return this.maxGlobalWriteSize;
   }

   public void setMaxGlobalWriteSize(long var1) {
      if (var1 <= 0L) {
         throw new IllegalArgumentException("maxGlobalWriteSize must be positive");
      } else {
         this.maxGlobalWriteSize = var1;
      }
   }

   public long queuesSize() {
      return this.queuesSize.get();
   }

   public void configureChannel(long var1, long var3) {
      this.writeChannelLimit = var1;
      this.readChannelLimit = var3;
      long var5 = TrafficCounter.milliSecondFromNano();
      Iterator var7 = this.channelQueues.values().iterator();

      while(var7.hasNext()) {
         GlobalChannelTrafficShapingHandler.PerChannel var8 = (GlobalChannelTrafficShapingHandler.PerChannel)var7.next();
         var8.channelTrafficCounter.resetAccounting(var5);
      }

   }

   public long getWriteChannelLimit() {
      return this.writeChannelLimit;
   }

   public void setWriteChannelLimit(long var1) {
      this.writeChannelLimit = var1;
      long var3 = TrafficCounter.milliSecondFromNano();
      Iterator var5 = this.channelQueues.values().iterator();

      while(var5.hasNext()) {
         GlobalChannelTrafficShapingHandler.PerChannel var6 = (GlobalChannelTrafficShapingHandler.PerChannel)var5.next();
         var6.channelTrafficCounter.resetAccounting(var3);
      }

   }

   public long getReadChannelLimit() {
      return this.readChannelLimit;
   }

   public void setReadChannelLimit(long var1) {
      this.readChannelLimit = var1;
      long var3 = TrafficCounter.milliSecondFromNano();
      Iterator var5 = this.channelQueues.values().iterator();

      while(var5.hasNext()) {
         GlobalChannelTrafficShapingHandler.PerChannel var6 = (GlobalChannelTrafficShapingHandler.PerChannel)var5.next();
         var6.channelTrafficCounter.resetAccounting(var3);
      }

   }

   public final void release() {
      this.trafficCounter.stop();
   }

   private GlobalChannelTrafficShapingHandler.PerChannel getOrSetPerChannel(ChannelHandlerContext var1) {
      Channel var2 = var1.channel();
      Integer var3 = var2.hashCode();
      GlobalChannelTrafficShapingHandler.PerChannel var4 = (GlobalChannelTrafficShapingHandler.PerChannel)this.channelQueues.get(var3);
      if (var4 == null) {
         var4 = new GlobalChannelTrafficShapingHandler.PerChannel();
         var4.messagesQueue = new ArrayDeque();
         var4.channelTrafficCounter = new TrafficCounter(this, (ScheduledExecutorService)null, "ChannelTC" + var1.channel().hashCode(), this.checkInterval);
         var4.queueSize = 0L;
         var4.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
         var4.lastWriteTimestamp = var4.lastReadTimestamp;
         this.channelQueues.put(var3, var4);
      }

      return var4;
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.getOrSetPerChannel(var1);
      this.trafficCounter.resetCumulativeTime();
      super.handlerAdded(var1);
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.trafficCounter.resetCumulativeTime();
      Channel var2 = var1.channel();
      Integer var3 = var2.hashCode();
      GlobalChannelTrafficShapingHandler.PerChannel var4 = (GlobalChannelTrafficShapingHandler.PerChannel)this.channelQueues.remove(var3);
      if (var4 != null) {
         synchronized(var4) {
            Iterator var6;
            GlobalChannelTrafficShapingHandler.ToSend var7;
            if (var2.isActive()) {
               var6 = var4.messagesQueue.iterator();

               while(var6.hasNext()) {
                  var7 = (GlobalChannelTrafficShapingHandler.ToSend)var6.next();
                  long var8 = this.calculateSize(var7.toSend);
                  this.trafficCounter.bytesRealWriteFlowControl(var8);
                  var4.channelTrafficCounter.bytesRealWriteFlowControl(var8);
                  var4.queueSize -= var8;
                  this.queuesSize.addAndGet(-var8);
                  var1.write(var7.toSend, var7.promise);
               }
            } else {
               this.queuesSize.addAndGet(-var4.queueSize);
               var6 = var4.messagesQueue.iterator();

               while(var6.hasNext()) {
                  var7 = (GlobalChannelTrafficShapingHandler.ToSend)var6.next();
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

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      long var3 = this.calculateSize(var2);
      long var5 = TrafficCounter.milliSecondFromNano();
      if (var3 > 0L) {
         long var7 = this.trafficCounter.readTimeToWait(var3, this.getReadLimit(), this.maxTime, var5);
         Integer var9 = var1.channel().hashCode();
         GlobalChannelTrafficShapingHandler.PerChannel var10 = (GlobalChannelTrafficShapingHandler.PerChannel)this.channelQueues.get(var9);
         long var11 = 0L;
         if (var10 != null) {
            var11 = var10.channelTrafficCounter.readTimeToWait(var3, this.readChannelLimit, this.maxTime, var5);
            if (this.readDeviationActive) {
               long var13 = var10.channelTrafficCounter.cumulativeReadBytes();
               long var15 = this.cumulativeReadBytes.get();
               if (var13 <= 0L) {
                  var13 = 0L;
               }

               if (var15 < var13) {
                  var15 = var13;
               }

               var11 = this.computeBalancedWait((float)var13, (float)var15, var11);
            }
         }

         if (var11 < var7) {
            var11 = var7;
         }

         var11 = this.checkWaitReadTime(var1, var11, var5);
         if (var11 >= 10L) {
            Channel var17 = var1.channel();
            ChannelConfig var14 = var17.config();
            if (logger.isDebugEnabled()) {
               logger.debug("Read Suspend: " + var11 + ':' + var14.isAutoRead() + ':' + isHandlerActive(var1));
            }

            if (var14.isAutoRead() && isHandlerActive(var1)) {
               var14.setAutoRead(false);
               var17.attr(READ_SUSPENDED).set(true);
               Attribute var18 = var17.attr(REOPEN_TASK);
               Object var16 = (Runnable)var18.get();
               if (var16 == null) {
                  var16 = new AbstractTrafficShapingHandler.ReopenReadTimerTask(var1);
                  var18.set(var16);
               }

               var1.executor().schedule((Runnable)var16, var11, TimeUnit.MILLISECONDS);
               if (logger.isDebugEnabled()) {
                  logger.debug("Suspend final status => " + var14.isAutoRead() + ':' + isHandlerActive(var1) + " will reopened at: " + var11);
               }
            }
         }
      }

      this.informReadOperation(var1, var5);
      var1.fireChannelRead(var2);
   }

   protected long checkWaitReadTime(ChannelHandlerContext var1, long var2, long var4) {
      Integer var6 = var1.channel().hashCode();
      GlobalChannelTrafficShapingHandler.PerChannel var7 = (GlobalChannelTrafficShapingHandler.PerChannel)this.channelQueues.get(var6);
      if (var7 != null && var2 > this.maxTime && var4 + var2 - var7.lastReadTimestamp > this.maxTime) {
         var2 = this.maxTime;
      }

      return var2;
   }

   protected void informReadOperation(ChannelHandlerContext var1, long var2) {
      Integer var4 = var1.channel().hashCode();
      GlobalChannelTrafficShapingHandler.PerChannel var5 = (GlobalChannelTrafficShapingHandler.PerChannel)this.channelQueues.get(var4);
      if (var5 != null) {
         var5.lastReadTimestamp = var2;
      }

   }

   protected long maximumCumulativeWrittenBytes() {
      return this.cumulativeWrittenBytes.get();
   }

   protected long maximumCumulativeReadBytes() {
      return this.cumulativeReadBytes.get();
   }

   public Collection<TrafficCounter> channelTrafficCounters() {
      return new AbstractCollection<TrafficCounter>() {
         public Iterator<TrafficCounter> iterator() {
            return new Iterator<TrafficCounter>() {
               final Iterator<GlobalChannelTrafficShapingHandler.PerChannel> iter;

               {
                  this.iter = GlobalChannelTrafficShapingHandler.this.channelQueues.values().iterator();
               }

               public boolean hasNext() {
                  return this.iter.hasNext();
               }

               public TrafficCounter next() {
                  return ((GlobalChannelTrafficShapingHandler.PerChannel)this.iter.next()).channelTrafficCounter;
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }

         public int size() {
            return GlobalChannelTrafficShapingHandler.this.channelQueues.size();
         }
      };
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      long var4 = this.calculateSize(var2);
      long var6 = TrafficCounter.milliSecondFromNano();
      if (var4 > 0L) {
         long var8 = this.trafficCounter.writeTimeToWait(var4, this.getWriteLimit(), this.maxTime, var6);
         Integer var10 = var1.channel().hashCode();
         GlobalChannelTrafficShapingHandler.PerChannel var11 = (GlobalChannelTrafficShapingHandler.PerChannel)this.channelQueues.get(var10);
         long var12 = 0L;
         if (var11 != null) {
            var12 = var11.channelTrafficCounter.writeTimeToWait(var4, this.writeChannelLimit, this.maxTime, var6);
            if (this.writeDeviationActive) {
               long var14 = var11.channelTrafficCounter.cumulativeWrittenBytes();
               long var16 = this.cumulativeWrittenBytes.get();
               if (var14 <= 0L) {
                  var14 = 0L;
               }

               if (var16 < var14) {
                  var16 = var14;
               }

               var12 = this.computeBalancedWait((float)var14, (float)var16, var12);
            }
         }

         if (var12 < var8) {
            var12 = var8;
         }

         if (var12 >= 10L) {
            if (logger.isDebugEnabled()) {
               logger.debug("Write suspend: " + var12 + ':' + var1.channel().config().isAutoRead() + ':' + isHandlerActive(var1));
            }

            this.submitWrite(var1, var2, var4, var12, var6, var3);
            return;
         }
      }

      this.submitWrite(var1, var2, var4, 0L, var6, var3);
   }

   protected void submitWrite(final ChannelHandlerContext var1, Object var2, long var3, long var5, long var7, ChannelPromise var9) {
      Channel var10 = var1.channel();
      Integer var11 = var10.hashCode();
      final GlobalChannelTrafficShapingHandler.PerChannel var12 = (GlobalChannelTrafficShapingHandler.PerChannel)this.channelQueues.get(var11);
      if (var12 == null) {
         var12 = this.getOrSetPerChannel(var1);
      }

      long var14 = var5;
      boolean var16 = false;
      GlobalChannelTrafficShapingHandler.ToSend var13;
      synchronized(var12) {
         if (var5 == 0L && var12.messagesQueue.isEmpty()) {
            this.trafficCounter.bytesRealWriteFlowControl(var3);
            var12.channelTrafficCounter.bytesRealWriteFlowControl(var3);
            var1.write(var2, var9);
            var12.lastWriteTimestamp = var7;
            return;
         }

         if (var14 > this.maxTime && var7 + var14 - var12.lastWriteTimestamp > this.maxTime) {
            var14 = this.maxTime;
         }

         var13 = new GlobalChannelTrafficShapingHandler.ToSend(var14 + var7, var2, var3, var9);
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
            GlobalChannelTrafficShapingHandler.this.sendAllValid(var1, var12, var17);
         }
      }, var14, TimeUnit.MILLISECONDS);
   }

   private void sendAllValid(ChannelHandlerContext var1, GlobalChannelTrafficShapingHandler.PerChannel var2, long var3) {
      synchronized(var2) {
         GlobalChannelTrafficShapingHandler.ToSend var6 = (GlobalChannelTrafficShapingHandler.ToSend)var2.messagesQueue.pollFirst();

         while(true) {
            if (var6 != null) {
               if (var6.relativeTimeAction <= var3) {
                  long var7 = var6.size;
                  this.trafficCounter.bytesRealWriteFlowControl(var7);
                  var2.channelTrafficCounter.bytesRealWriteFlowControl(var7);
                  var2.queueSize -= var7;
                  this.queuesSize.addAndGet(-var7);
                  var1.write(var6.toSend, var6.promise);
                  var2.lastWriteTimestamp = var3;
                  var6 = (GlobalChannelTrafficShapingHandler.ToSend)var2.messagesQueue.pollFirst();
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

   public String toString() {
      return (new StringBuilder(340)).append(super.toString()).append(" Write Channel Limit: ").append(this.writeChannelLimit).append(" Read Channel Limit: ").append(this.readChannelLimit).toString();
   }

   private static final class ToSend {
      final long relativeTimeAction;
      final Object toSend;
      final ChannelPromise promise;
      final long size;

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

   static final class PerChannel {
      ArrayDeque<GlobalChannelTrafficShapingHandler.ToSend> messagesQueue;
      TrafficCounter channelTrafficCounter;
      long queueSize;
      long lastWriteTimestamp;
      long lastReadTimestamp;

      PerChannel() {
         super();
      }
   }
}
