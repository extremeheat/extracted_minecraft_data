package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I> extends ChannelOutboundHandlerAdapter {
   private final TypeParameterMatcher matcher;

   protected MessageToMessageEncoder() {
      super();
      this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
   }

   protected MessageToMessageEncoder(Class<? extends I> var1) {
      super();
      this.matcher = TypeParameterMatcher.get(var1);
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return this.matcher.match(var1);
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      CodecOutputList var4 = null;
      boolean var20 = false;

      try {
         var20 = true;
         if (this.acceptOutboundMessage(var2)) {
            var4 = CodecOutputList.newInstance();
            Object var5 = var2;

            try {
               this.encode(var1, var5, var4);
            } finally {
               ReferenceCountUtil.release(var2);
            }

            if (var4.isEmpty()) {
               var4.recycle();
               var4 = null;
               throw new EncoderException(StringUtil.simpleClassName((Object)this) + " must produce at least one message.");
            }

            var20 = false;
         } else {
            var1.write(var2, var3);
            var20 = false;
         }
      } catch (EncoderException var26) {
         throw var26;
      } catch (Throwable var27) {
         throw new EncoderException(var27);
      } finally {
         if (var20) {
            if (var4 != null) {
               int var11 = var4.size() - 1;
               if (var11 == 0) {
                  var1.write(var4.get(0), var3);
               } else if (var11 > 0) {
                  ChannelPromise var12 = var1.voidPromise();
                  boolean var13 = var3 == var12;

                  for(int var14 = 0; var14 < var11; ++var14) {
                     ChannelPromise var15;
                     if (var13) {
                        var15 = var12;
                     } else {
                        var15 = var1.newPromise();
                     }

                     var1.write(var4.getUnsafe(var14), var15);
                  }

                  var1.write(var4.getUnsafe(var11), var3);
               }

               var4.recycle();
            }

         }
      }

      if (var4 != null) {
         int var29 = var4.size() - 1;
         if (var29 == 0) {
            var1.write(var4.get(0), var3);
         } else if (var29 > 0) {
            ChannelPromise var6 = var1.voidPromise();
            boolean var7 = var3 == var6;

            for(int var8 = 0; var8 < var29; ++var8) {
               ChannelPromise var9;
               if (var7) {
                  var9 = var6;
               } else {
                  var9 = var1.newPromise();
               }

               var1.write(var4.getUnsafe(var8), var9);
            }

            var1.write(var4.getUnsafe(var29), var3);
         }

         var4.recycle();
      }

   }

   protected abstract void encode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}
