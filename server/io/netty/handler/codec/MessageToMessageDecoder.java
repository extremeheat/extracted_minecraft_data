package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter {
   private final TypeParameterMatcher matcher;

   protected MessageToMessageDecoder() {
      super();
      this.matcher = TypeParameterMatcher.find(this, MessageToMessageDecoder.class, "I");
   }

   protected MessageToMessageDecoder(Class<? extends I> var1) {
      super();
      this.matcher = TypeParameterMatcher.get(var1);
   }

   public boolean acceptInboundMessage(Object var1) throws Exception {
      return this.matcher.match(var1);
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      CodecOutputList var3 = CodecOutputList.newInstance();
      boolean var13 = false;

      try {
         var13 = true;
         if (this.acceptInboundMessage(var2)) {
            Object var4 = var2;

            try {
               this.decode(var1, var4, var3);
            } finally {
               ReferenceCountUtil.release(var2);
            }

            var13 = false;
         } else {
            var3.add(var2);
            var13 = false;
         }
      } catch (DecoderException var19) {
         throw var19;
      } catch (Exception var20) {
         throw new DecoderException(var20);
      } finally {
         if (var13) {
            int var7 = var3.size();

            for(int var8 = 0; var8 < var7; ++var8) {
               var1.fireChannelRead(var3.getUnsafe(var8));
            }

            var3.recycle();
         }
      }

      int var22 = var3.size();

      for(int var5 = 0; var5 < var22; ++var5) {
         var1.fireChannelRead(var3.getUnsafe(var5));
      }

      var3.recycle();
   }

   protected abstract void decode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}
