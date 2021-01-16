package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTrafficShapingHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
   public static final long DEFAULT_CHECK_INTERVAL = 1000L;
   public static final long DEFAULT_MAX_TIME = 15000L;
   static final long DEFAULT_MAX_SIZE = 4194304L;
   static final long MINIMAL_WAIT = 10L;
   protected TrafficCounter trafficCounter;
   private volatile long writeLimit;
   private volatile long readLimit;
   protected volatile long maxTime;
   protected volatile long checkInterval;
   static final AttributeKey<Boolean> READ_SUSPENDED = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".READ_SUSPENDED");
   static final AttributeKey<Runnable> REOPEN_TASK = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".REOPEN_TASK");
   volatile long maxWriteDelay;
   volatile long maxWriteSize;
   final int userDefinedWritabilityIndex;
   static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;
   static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;
   static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;

   void setTrafficCounter(TrafficCounter var1) {
      this.trafficCounter = var1;
   }

   protected int userDefinedWritabilityIndex() {
      return 1;
   }

   protected AbstractTrafficShapingHandler(long var1, long var3, long var5, long var7) {
      super();
      this.maxTime = 15000L;
      this.checkInterval = 1000L;
      this.maxWriteDelay = 4000L;
      this.maxWriteSize = 4194304L;
      if (var7 <= 0L) {
         throw new IllegalArgumentException("maxTime must be positive");
      } else {
         this.userDefinedWritabilityIndex = this.userDefinedWritabilityIndex();
         this.writeLimit = var1;
         this.readLimit = var3;
         this.checkInterval = var5;
         this.maxTime = var7;
      }
   }

   protected AbstractTrafficShapingHandler(long var1, long var3, long var5) {
      this(var1, var3, var5, 15000L);
   }

   protected AbstractTrafficShapingHandler(long var1, long var3) {
      this(var1, var3, 1000L, 15000L);
   }

   protected AbstractTrafficShapingHandler() {
      this(0L, 0L, 1000L, 15000L);
   }

   protected AbstractTrafficShapingHandler(long var1) {
      this(0L, 0L, var1, 15000L);
   }

   public void configure(long var1, long var3, long var5) {
      this.configure(var1, var3);
      this.configure(var5);
   }

   public void configure(long var1, long var3) {
      this.writeLimit = var1;
      this.readLimit = var3;
      if (this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
      }

   }

   public void configure(long var1) {
      this.checkInterval = var1;
      if (this.trafficCounter != null) {
         this.trafficCounter.configure(this.checkInterval);
      }

   }

   public long getWriteLimit() {
      return this.writeLimit;
   }

   public void setWriteLimit(long var1) {
      this.writeLimit = var1;
      if (this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
      }

   }

   public long getReadLimit() {
      return this.readLimit;
   }

   public void setReadLimit(long var1) {
      this.readLimit = var1;
      if (this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
      }

   }

   public long getCheckInterval() {
      return this.checkInterval;
   }

   public void setCheckInterval(long var1) {
      this.checkInterval = var1;
      if (this.trafficCounter != null) {
         this.trafficCounter.configure(var1);
      }

   }

   public void setMaxTimeWait(long var1) {
      if (var1 <= 0L) {
         throw new IllegalArgumentException("maxTime must be positive");
      } else {
         this.maxTime = var1;
      }
   }

   public long getMaxTimeWait() {
      return this.maxTime;
   }

   public long getMaxWriteDelay() {
      return this.maxWriteDelay;
   }

   public void setMaxWriteDelay(long var1) {
      if (var1 <= 0L) {
         throw new IllegalArgumentException("maxWriteDelay must be positive");
      } else {
         this.maxWriteDelay = var1;
      }
   }

   public long getMaxWriteSize() {
      return this.maxWriteSize;
   }

   public void setMaxWriteSize(long var1) {
      this.maxWriteSize = var1;
   }

   protected void doAccounting(TrafficCounter var1) {
   }

   void releaseReadSuspended(ChannelHandlerContext var1) {
      Channel var2 = var1.channel();
      var2.attr(READ_SUSPENDED).set(false);
      var2.config().setAutoRead(true);
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      long var3 = this.calculateSize(var2);
      long var5 = TrafficCounter.milliSecondFromNano();
      if (var3 > 0L) {
         long var7 = this.trafficCounter.readTimeToWait(var3, this.readLimit, this.maxTime, var5);
         var7 = this.checkWaitReadTime(var1, var7, var5);
         if (var7 >= 10L) {
            Channel var9 = var1.channel();
            ChannelConfig var10 = var9.config();
            if (logger.isDebugEnabled()) {
               logger.debug("Read suspend: " + var7 + ':' + var10.isAutoRead() + ':' + isHandlerActive(var1));
            }

            if (var10.isAutoRead() && isHandlerActive(var1)) {
               var10.setAutoRead(false);
               var9.attr(READ_SUSPENDED).set(true);
               Attribute var11 = var9.attr(REOPEN_TASK);
               Object var12 = (Runnable)var11.get();
               if (var12 == null) {
                  var12 = new AbstractTrafficShapingHandler.ReopenReadTimerTask(var1);
                  var11.set(var12);
               }

               var1.executor().schedule((Runnable)var12, var7, TimeUnit.MILLISECONDS);
               if (logger.isDebugEnabled()) {
                  logger.debug("Suspend final status => " + var10.isAutoRead() + ':' + isHandlerActive(var1) + " will reopened at: " + var7);
               }
            }
         }
      }

      this.informReadOperation(var1, var5);
      var1.fireChannelRead(var2);
   }

   long checkWaitReadTime(ChannelHandlerContext var1, long var2, long var4) {
      return var2;
   }

   void informReadOperation(ChannelHandlerContext var1, long var2) {
   }

   protected static boolean isHandlerActive(ChannelHandlerContext var0) {
      Boolean var1 = (Boolean)var0.channel().attr(READ_SUSPENDED).get();
      return var1 == null || Boolean.FALSE.equals(var1);
   }

   public void read(ChannelHandlerContext var1) {
      if (isHandlerActive(var1)) {
         var1.read();
      }

   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      long var4 = this.calculateSize(var2);
      long var6 = TrafficCounter.milliSecondFromNano();
      if (var4 > 0L) {
         long var8 = this.trafficCounter.writeTimeToWait(var4, this.writeLimit, this.maxTime, var6);
         if (var8 >= 10L) {
            if (logger.isDebugEnabled()) {
               logger.debug("Write suspend: " + var8 + ':' + var1.channel().config().isAutoRead() + ':' + isHandlerActive(var1));
            }

            this.submitWrite(var1, var2, var4, var8, var6, var3);
            return;
         }
      }

      this.submitWrite(var1, var2, var4, 0L, var6, var3);
   }

   /** @deprecated */
   @Deprecated
   protected void submitWrite(ChannelHandlerContext var1, Object var2, long var3, ChannelPromise var5) {
      this.submitWrite(var1, var2, this.calculateSize(var2), var3, TrafficCounter.milliSecondFromNano(), var5);
   }

   abstract void submitWrite(ChannelHandlerContext var1, Object var2, long var3, long var5, long var7, ChannelPromise var9);

   public void channelRegistered(ChannelHandlerContext var1) throws Exception {
      this.setUserDefinedWritability(var1, true);
      super.channelRegistered(var1);
   }

   void setUserDefinedWritability(ChannelHandlerContext var1, boolean var2) {
      ChannelOutboundBuffer var3 = var1.channel().unsafe().outboundBuffer();
      if (var3 != null) {
         var3.setUserDefinedWritability(this.userDefinedWritabilityIndex, var2);
      }

   }

   void checkWriteSuspend(ChannelHandlerContext var1, long var2, long var4) {
      if (var4 > this.maxWriteSize || var2 > this.maxWriteDelay) {
         this.setUserDefinedWritability(var1, false);
      }

   }

   void releaseWriteSuspended(ChannelHandlerContext var1) {
      this.setUserDefinedWritability(var1, true);
   }

   public TrafficCounter trafficCounter() {
      return this.trafficCounter;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder(290)).append("TrafficShaping with Write Limit: ").append(this.writeLimit).append(" Read Limit: ").append(this.readLimit).append(" CheckInterval: ").append(this.checkInterval).append(" maxDelay: ").append(this.maxWriteDelay).append(" maxSize: ").append(this.maxWriteSize).append(" and Counter: ");
      if (this.trafficCounter != null) {
         var1.append(this.trafficCounter);
      } else {
         var1.append("none");
      }

      return var1.toString();
   }

   protected long calculateSize(Object var1) {
      if (var1 instanceof ByteBuf) {
         return (long)((ByteBuf)var1).readableBytes();
      } else {
         return var1 instanceof ByteBufHolder ? (long)((ByteBufHolder)var1).content().readableBytes() : -1L;
      }
   }

   static final class ReopenReadTimerTask implements Runnable {
      final ChannelHandlerContext ctx;

      ReopenReadTimerTask(ChannelHandlerContext var1) {
         super();
         this.ctx = var1;
      }

      public void run() {
         Channel var1 = this.ctx.channel();
         ChannelConfig var2 = var1.config();
         if (!var2.isAutoRead() && AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
            if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
               AbstractTrafficShapingHandler.logger.debug("Not unsuspend: " + var2.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
            }

            var1.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(false);
         } else {
            if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
               if (var2.isAutoRead() && !AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
                  AbstractTrafficShapingHandler.logger.debug("Unsuspend: " + var2.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
               } else {
                  AbstractTrafficShapingHandler.logger.debug("Normal unsuspend: " + var2.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
               }
            }

            var1.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(false);
            var2.setAutoRead(true);
            var1.read();
         }

         if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
            AbstractTrafficShapingHandler.logger.debug("Unsuspend final status => " + var2.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
         }

      }
   }
}
