package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Signal;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ReplayingDecoder<S> extends ByteToMessageDecoder {
   static final Signal REPLAY = Signal.valueOf(ReplayingDecoder.class, "REPLAY");
   private final ReplayingDecoderByteBuf replayable;
   private S state;
   private int checkpoint;

   protected ReplayingDecoder() {
      this((Object)null);
   }

   protected ReplayingDecoder(S var1) {
      super();
      this.replayable = new ReplayingDecoderByteBuf();
      this.checkpoint = -1;
      this.state = var1;
   }

   protected void checkpoint() {
      this.checkpoint = this.internalBuffer().readerIndex();
   }

   protected void checkpoint(S var1) {
      this.checkpoint();
      this.state(var1);
   }

   protected S state() {
      return this.state;
   }

   protected S state(S var1) {
      Object var2 = this.state;
      this.state = var1;
      return var2;
   }

   final void channelInputClosed(ChannelHandlerContext var1, List<Object> var2) throws Exception {
      try {
         this.replayable.terminate();
         if (this.cumulation != null) {
            this.callDecode(var1, this.internalBuffer(), var2);
         } else {
            this.replayable.setCumulation(Unpooled.EMPTY_BUFFER);
         }

         this.decodeLast(var1, this.replayable, var2);
      } catch (Signal var4) {
         var4.expect(REPLAY);
      }

   }

   protected void callDecode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) {
      this.replayable.setCumulation(var2);

      try {
         while(var2.isReadable()) {
            int var4 = this.checkpoint = var2.readerIndex();
            int var5 = var3.size();
            if (var5 > 0) {
               fireChannelRead(var1, var3, var5);
               var3.clear();
               if (var1.isRemoved()) {
                  break;
               }

               var5 = 0;
            }

            Object var6 = this.state;
            int var7 = var2.readableBytes();

            try {
               this.decodeRemovalReentryProtection(var1, this.replayable, var3);
               if (var1.isRemoved()) {
                  break;
               }

               if (var5 == var3.size()) {
                  if (var7 == var2.readableBytes() && var6 == this.state) {
                     throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() must consume the inbound data or change its state if it did not decode anything.");
                  }
                  continue;
               }
            } catch (Signal var10) {
               var10.expect(REPLAY);
               if (!var1.isRemoved()) {
                  int var9 = this.checkpoint;
                  if (var9 >= 0) {
                     var2.readerIndex(var9);
                  }
               }
               break;
            }

            if (var4 == var2.readerIndex() && var6 == this.state) {
               throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() method must consume the inbound data or change its state if it decoded something.");
            }

            if (this.isSingleDecode()) {
               break;
            }
         }

      } catch (DecoderException var11) {
         throw var11;
      } catch (Exception var12) {
         throw new DecoderException(var12);
      }
   }
}
