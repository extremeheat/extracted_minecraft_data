package io.netty.handler.codec.redis;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class RedisArrayAggregator extends MessageToMessageDecoder<RedisMessage> {
   private final Deque<RedisArrayAggregator.AggregateState> depths = new ArrayDeque(4);

   public RedisArrayAggregator() {
      super();
   }

   protected void decode(ChannelHandlerContext var1, RedisMessage var2, List<Object> var3) throws Exception {
      if (var2 instanceof ArrayHeaderRedisMessage) {
         var2 = this.decodeRedisArrayHeader((ArrayHeaderRedisMessage)var2);
         if (var2 == null) {
            return;
         }
      } else {
         ReferenceCountUtil.retain(var2);
      }

      while(!this.depths.isEmpty()) {
         RedisArrayAggregator.AggregateState var4 = (RedisArrayAggregator.AggregateState)this.depths.peek();
         var4.children.add(var2);
         if (var4.children.size() != var4.length) {
            return;
         }

         var2 = new ArrayRedisMessage(var4.children);
         this.depths.pop();
      }

      var3.add(var2);
   }

   private RedisMessage decodeRedisArrayHeader(ArrayHeaderRedisMessage var1) {
      if (var1.isNull()) {
         return ArrayRedisMessage.NULL_INSTANCE;
      } else if (var1.length() == 0L) {
         return ArrayRedisMessage.EMPTY_INSTANCE;
      } else if (var1.length() > 0L) {
         if (var1.length() > 2147483647L) {
            throw new CodecException("this codec doesn't support longer length than 2147483647");
         } else {
            this.depths.push(new RedisArrayAggregator.AggregateState((int)var1.length()));
            return null;
         }
      } else {
         throw new CodecException("bad length: " + var1.length());
      }
   }

   private static final class AggregateState {
      private final int length;
      private final List<RedisMessage> children;

      AggregateState(int var1) {
         super();
         this.length = var1;
         this.children = new ArrayList(var1);
      }
   }
}
