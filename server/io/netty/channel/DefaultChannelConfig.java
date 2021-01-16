package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultChannelConfig implements ChannelConfig {
   private static final MessageSizeEstimator DEFAULT_MSG_SIZE_ESTIMATOR;
   private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
   private static final AtomicIntegerFieldUpdater<DefaultChannelConfig> AUTOREAD_UPDATER;
   private static final AtomicReferenceFieldUpdater<DefaultChannelConfig, WriteBufferWaterMark> WATERMARK_UPDATER;
   protected final Channel channel;
   private volatile ByteBufAllocator allocator;
   private volatile RecvByteBufAllocator rcvBufAllocator;
   private volatile MessageSizeEstimator msgSizeEstimator;
   private volatile int connectTimeoutMillis;
   private volatile int writeSpinCount;
   private volatile int autoRead;
   private volatile boolean autoClose;
   private volatile WriteBufferWaterMark writeBufferWaterMark;
   private volatile boolean pinEventExecutor;

   public DefaultChannelConfig(Channel var1) {
      this(var1, new AdaptiveRecvByteBufAllocator());
   }

   protected DefaultChannelConfig(Channel var1, RecvByteBufAllocator var2) {
      super();
      this.allocator = ByteBufAllocator.DEFAULT;
      this.msgSizeEstimator = DEFAULT_MSG_SIZE_ESTIMATOR;
      this.connectTimeoutMillis = 30000;
      this.writeSpinCount = 16;
      this.autoRead = 1;
      this.autoClose = true;
      this.writeBufferWaterMark = WriteBufferWaterMark.DEFAULT;
      this.pinEventExecutor = true;
      this.setRecvByteBufAllocator(var2, var1.metadata());
      this.channel = var1;
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions((Map)null, ChannelOption.CONNECT_TIMEOUT_MILLIS, ChannelOption.MAX_MESSAGES_PER_READ, ChannelOption.WRITE_SPIN_COUNT, ChannelOption.ALLOCATOR, ChannelOption.AUTO_READ, ChannelOption.AUTO_CLOSE, ChannelOption.RCVBUF_ALLOCATOR, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, ChannelOption.WRITE_BUFFER_WATER_MARK, ChannelOption.MESSAGE_SIZE_ESTIMATOR, ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
   }

   protected Map<ChannelOption<?>, Object> getOptions(Map<ChannelOption<?>, Object> var1, ChannelOption<?>... var2) {
      if (var1 == null) {
         var1 = new IdentityHashMap();
      }

      ChannelOption[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ChannelOption var6 = var3[var5];
         ((Map)var1).put(var6, this.getOption(var6));
      }

      return (Map)var1;
   }

   public boolean setOptions(Map<ChannelOption<?>, ?> var1) {
      if (var1 == null) {
         throw new NullPointerException("options");
      } else {
         boolean var2 = true;
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            if (!this.setOption((ChannelOption)var4.getKey(), var4.getValue())) {
               var2 = false;
            }
         }

         return var2;
      }
   }

   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("option");
      } else if (var1 == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
         return this.getConnectTimeoutMillis();
      } else if (var1 == ChannelOption.MAX_MESSAGES_PER_READ) {
         return this.getMaxMessagesPerRead();
      } else if (var1 == ChannelOption.WRITE_SPIN_COUNT) {
         return this.getWriteSpinCount();
      } else if (var1 == ChannelOption.ALLOCATOR) {
         return this.getAllocator();
      } else if (var1 == ChannelOption.RCVBUF_ALLOCATOR) {
         return this.getRecvByteBufAllocator();
      } else if (var1 == ChannelOption.AUTO_READ) {
         return this.isAutoRead();
      } else if (var1 == ChannelOption.AUTO_CLOSE) {
         return this.isAutoClose();
      } else if (var1 == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
         return this.getWriteBufferHighWaterMark();
      } else if (var1 == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
         return this.getWriteBufferLowWaterMark();
      } else if (var1 == ChannelOption.WRITE_BUFFER_WATER_MARK) {
         return this.getWriteBufferWaterMark();
      } else if (var1 == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
         return this.getMessageSizeEstimator();
      } else {
         return var1 == ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP ? this.getPinEventExecutorPerGroup() : null;
      }
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
         this.setConnectTimeoutMillis((Integer)var2);
      } else if (var1 == ChannelOption.MAX_MESSAGES_PER_READ) {
         this.setMaxMessagesPerRead((Integer)var2);
      } else if (var1 == ChannelOption.WRITE_SPIN_COUNT) {
         this.setWriteSpinCount((Integer)var2);
      } else if (var1 == ChannelOption.ALLOCATOR) {
         this.setAllocator((ByteBufAllocator)var2);
      } else if (var1 == ChannelOption.RCVBUF_ALLOCATOR) {
         this.setRecvByteBufAllocator((RecvByteBufAllocator)var2);
      } else if (var1 == ChannelOption.AUTO_READ) {
         this.setAutoRead((Boolean)var2);
      } else if (var1 == ChannelOption.AUTO_CLOSE) {
         this.setAutoClose((Boolean)var2);
      } else if (var1 == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
         this.setWriteBufferHighWaterMark((Integer)var2);
      } else if (var1 == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
         this.setWriteBufferLowWaterMark((Integer)var2);
      } else if (var1 == ChannelOption.WRITE_BUFFER_WATER_MARK) {
         this.setWriteBufferWaterMark((WriteBufferWaterMark)var2);
      } else if (var1 == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
         this.setMessageSizeEstimator((MessageSizeEstimator)var2);
      } else {
         if (var1 != ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) {
            return false;
         }

         this.setPinEventExecutorPerGroup((Boolean)var2);
      }

      return true;
   }

   protected <T> void validate(ChannelOption<T> var1, T var2) {
      if (var1 == null) {
         throw new NullPointerException("option");
      } else {
         var1.validate(var2);
      }
   }

   public int getConnectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   public ChannelConfig setConnectTimeoutMillis(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException(String.format("connectTimeoutMillis: %d (expected: >= 0)", var1));
      } else {
         this.connectTimeoutMillis = var1;
         return this;
      }
   }

   /** @deprecated */
   @Deprecated
   public int getMaxMessagesPerRead() {
      try {
         MaxMessagesRecvByteBufAllocator var1 = (MaxMessagesRecvByteBufAllocator)this.getRecvByteBufAllocator();
         return var1.maxMessagesPerRead();
      } catch (ClassCastException var2) {
         throw new IllegalStateException("getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", var2);
      }
   }

   /** @deprecated */
   @Deprecated
   public ChannelConfig setMaxMessagesPerRead(int var1) {
      try {
         MaxMessagesRecvByteBufAllocator var2 = (MaxMessagesRecvByteBufAllocator)this.getRecvByteBufAllocator();
         var2.maxMessagesPerRead(var1);
         return this;
      } catch (ClassCastException var3) {
         throw new IllegalStateException("getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", var3);
      }
   }

   public int getWriteSpinCount() {
      return this.writeSpinCount;
   }

   public ChannelConfig setWriteSpinCount(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("writeSpinCount must be a positive integer.");
      } else {
         if (var1 == 2147483647) {
            --var1;
         }

         this.writeSpinCount = var1;
         return this;
      }
   }

   public ByteBufAllocator getAllocator() {
      return this.allocator;
   }

   public ChannelConfig setAllocator(ByteBufAllocator var1) {
      if (var1 == null) {
         throw new NullPointerException("allocator");
      } else {
         this.allocator = var1;
         return this;
      }
   }

   public <T extends RecvByteBufAllocator> T getRecvByteBufAllocator() {
      return this.rcvBufAllocator;
   }

   public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      this.rcvBufAllocator = (RecvByteBufAllocator)ObjectUtil.checkNotNull(var1, "allocator");
      return this;
   }

   private void setRecvByteBufAllocator(RecvByteBufAllocator var1, ChannelMetadata var2) {
      if (var1 instanceof MaxMessagesRecvByteBufAllocator) {
         ((MaxMessagesRecvByteBufAllocator)var1).maxMessagesPerRead(var2.defaultMaxMessagesPerRead());
      } else if (var1 == null) {
         throw new NullPointerException("allocator");
      }

      this.setRecvByteBufAllocator(var1);
   }

   public boolean isAutoRead() {
      return this.autoRead == 1;
   }

   public ChannelConfig setAutoRead(boolean var1) {
      boolean var2 = AUTOREAD_UPDATER.getAndSet(this, var1 ? 1 : 0) == 1;
      if (var1 && !var2) {
         this.channel.read();
      } else if (!var1 && var2) {
         this.autoReadCleared();
      }

      return this;
   }

   protected void autoReadCleared() {
   }

   public boolean isAutoClose() {
      return this.autoClose;
   }

   public ChannelConfig setAutoClose(boolean var1) {
      this.autoClose = var1;
      return this;
   }

   public int getWriteBufferHighWaterMark() {
      return this.writeBufferWaterMark.high();
   }

   public ChannelConfig setWriteBufferHighWaterMark(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("writeBufferHighWaterMark must be >= 0");
      } else {
         WriteBufferWaterMark var2;
         do {
            var2 = this.writeBufferWaterMark;
            if (var1 < var2.low()) {
               throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + var2.low() + "): " + var1);
            }
         } while(!WATERMARK_UPDATER.compareAndSet(this, var2, new WriteBufferWaterMark(var2.low(), var1, false)));

         return this;
      }
   }

   public int getWriteBufferLowWaterMark() {
      return this.writeBufferWaterMark.low();
   }

   public ChannelConfig setWriteBufferLowWaterMark(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("writeBufferLowWaterMark must be >= 0");
      } else {
         WriteBufferWaterMark var2;
         do {
            var2 = this.writeBufferWaterMark;
            if (var1 > var2.high()) {
               throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + var2.high() + "): " + var1);
            }
         } while(!WATERMARK_UPDATER.compareAndSet(this, var2, new WriteBufferWaterMark(var1, var2.high(), false)));

         return this;
      }
   }

   public ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      this.writeBufferWaterMark = (WriteBufferWaterMark)ObjectUtil.checkNotNull(var1, "writeBufferWaterMark");
      return this;
   }

   public WriteBufferWaterMark getWriteBufferWaterMark() {
      return this.writeBufferWaterMark;
   }

   public MessageSizeEstimator getMessageSizeEstimator() {
      return this.msgSizeEstimator;
   }

   public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      if (var1 == null) {
         throw new NullPointerException("estimator");
      } else {
         this.msgSizeEstimator = var1;
         return this;
      }
   }

   private ChannelConfig setPinEventExecutorPerGroup(boolean var1) {
      this.pinEventExecutor = var1;
      return this;
   }

   private boolean getPinEventExecutorPerGroup() {
      return this.pinEventExecutor;
   }

   static {
      DEFAULT_MSG_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;
      AUTOREAD_UPDATER = AtomicIntegerFieldUpdater.newUpdater(DefaultChannelConfig.class, "autoRead");
      WATERMARK_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultChannelConfig.class, WriteBufferWaterMark.class, "writeBufferWaterMark");
   }
}
