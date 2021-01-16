package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.unix.DomainSocketReadMode;
import io.netty.channel.unix.UnixChannelOption;
import java.util.Map;

public final class KQueueDomainSocketChannelConfig extends KQueueChannelConfig implements DomainSocketChannelConfig {
   private volatile DomainSocketReadMode mode;

   KQueueDomainSocketChannelConfig(AbstractKQueueChannel var1) {
      super(var1);
      this.mode = DomainSocketReadMode.BYTES;
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{UnixChannelOption.DOMAIN_SOCKET_READ_MODE});
   }

   public <T> T getOption(ChannelOption<T> var1) {
      return var1 == UnixChannelOption.DOMAIN_SOCKET_READ_MODE ? this.getReadMode() : super.getOption(var1);
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
         this.setReadMode((DomainSocketReadMode)var2);
         return true;
      } else {
         return super.setOption(var1, var2);
      }
   }

   public KQueueDomainSocketChannelConfig setRcvAllocTransportProvidesGuess(boolean var1) {
      super.setRcvAllocTransportProvidesGuess(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueDomainSocketChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setAutoClose(boolean var1) {
      super.setAutoClose(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueDomainSocketChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueDomainSocketChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   public KQueueDomainSocketChannelConfig setReadMode(DomainSocketReadMode var1) {
      if (var1 == null) {
         throw new NullPointerException("mode");
      } else {
         this.mode = var1;
         return this;
      }
   }

   public DomainSocketReadMode getReadMode() {
      return this.mode;
   }
}
