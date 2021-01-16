package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class BinaryMemcacheClientCodec extends CombinedChannelDuplexHandler<BinaryMemcacheResponseDecoder, BinaryMemcacheRequestEncoder> {
   private final boolean failOnMissingResponse;
   private final AtomicLong requestResponseCounter;

   public BinaryMemcacheClientCodec() {
      this(8192);
   }

   public BinaryMemcacheClientCodec(int var1) {
      this(var1, false);
   }

   public BinaryMemcacheClientCodec(int var1, boolean var2) {
      super();
      this.requestResponseCounter = new AtomicLong();
      this.failOnMissingResponse = var2;
      this.init(new BinaryMemcacheClientCodec.Decoder(var1), new BinaryMemcacheClientCodec.Encoder());
   }

   private final class Decoder extends BinaryMemcacheResponseDecoder {
      Decoder(int var2) {
         super(var2);
      }

      protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
         int var4 = var3.size();
         super.decode(var1, var2, var3);
         if (BinaryMemcacheClientCodec.this.failOnMissingResponse) {
            int var5 = var3.size();

            for(int var6 = var4; var6 < var5; ++var6) {
               Object var7 = var3.get(var6);
               if (var7 instanceof LastMemcacheContent) {
                  BinaryMemcacheClientCodec.this.requestResponseCounter.decrementAndGet();
               }
            }
         }

      }

      public void channelInactive(ChannelHandlerContext var1) throws Exception {
         super.channelInactive(var1);
         if (BinaryMemcacheClientCodec.this.failOnMissingResponse) {
            long var2 = BinaryMemcacheClientCodec.this.requestResponseCounter.get();
            if (var2 > 0L) {
               var1.fireExceptionCaught(new PrematureChannelClosureException("channel gone inactive with " + var2 + " missing response(s)"));
            }
         }

      }
   }

   private final class Encoder extends BinaryMemcacheRequestEncoder {
      private Encoder() {
         super();
      }

      protected void encode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
         super.encode(var1, var2, var3);
         if (BinaryMemcacheClientCodec.this.failOnMissingResponse && var2 instanceof LastMemcacheContent) {
            BinaryMemcacheClientCodec.this.requestResponseCounter.incrementAndGet();
         }

      }

      // $FF: synthetic method
      Encoder(Object var2) {
         this();
      }
   }
}
