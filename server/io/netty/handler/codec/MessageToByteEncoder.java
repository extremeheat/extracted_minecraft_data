package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class MessageToByteEncoder<I> extends ChannelOutboundHandlerAdapter {
   private final TypeParameterMatcher matcher;
   private final boolean preferDirect;

   protected MessageToByteEncoder() {
      this(true);
   }

   protected MessageToByteEncoder(Class<? extends I> var1) {
      this(var1, true);
   }

   protected MessageToByteEncoder(boolean var1) {
      super();
      this.matcher = TypeParameterMatcher.find(this, MessageToByteEncoder.class, "I");
      this.preferDirect = var1;
   }

   protected MessageToByteEncoder(Class<? extends I> var1, boolean var2) {
      super();
      this.matcher = TypeParameterMatcher.get(var1);
      this.preferDirect = var2;
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return this.matcher.match(var1);
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      ByteBuf var4 = null;

      try {
         if (this.acceptOutboundMessage(var2)) {
            Object var5 = var2;
            var4 = this.allocateBuffer(var1, var2, this.preferDirect);

            try {
               this.encode(var1, var5, var4);
            } finally {
               ReferenceCountUtil.release(var2);
            }

            if (var4.isReadable()) {
               var1.write(var4, var3);
            } else {
               var4.release();
               var1.write(Unpooled.EMPTY_BUFFER, var3);
            }

            var4 = null;
         } else {
            var1.write(var2, var3);
         }
      } catch (EncoderException var17) {
         throw var17;
      } catch (Throwable var18) {
         throw new EncoderException(var18);
      } finally {
         if (var4 != null) {
            var4.release();
         }

      }

   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, I var2, boolean var3) throws Exception {
      return var3 ? var1.alloc().ioBuffer() : var1.alloc().heapBuffer();
   }

   protected abstract void encode(ChannelHandlerContext var1, I var2, ByteBuf var3) throws Exception;

   protected boolean isPreferDirect() {
      return this.preferDirect;
   }
}
