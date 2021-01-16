package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import java.util.List;

abstract class DeflateEncoder extends WebSocketExtensionEncoder {
   private final int compressionLevel;
   private final int windowSize;
   private final boolean noContext;
   private EmbeddedChannel encoder;

   public DeflateEncoder(int var1, int var2, boolean var3) {
      super();
      this.compressionLevel = var1;
      this.windowSize = var2;
      this.noContext = var3;
   }

   protected abstract int rsv(WebSocketFrame var1);

   protected abstract boolean removeFrameTail(WebSocketFrame var1);

   protected void encode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      if (this.encoder == null) {
         this.encoder = new EmbeddedChannel(new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder(ZlibWrapper.NONE, this.compressionLevel, this.windowSize, 8)});
      }

      this.encoder.writeOutbound(var2.content().retain());
      CompositeByteBuf var4 = var1.alloc().compositeBuffer();

      while(true) {
         ByteBuf var5 = (ByteBuf)this.encoder.readOutbound();
         if (var5 == null) {
            if (var4.numComponents() <= 0) {
               var4.release();
               throw new CodecException("cannot read compressed buffer");
            }

            if (var2.isFinalFragment() && this.noContext) {
               this.cleanup();
            }

            Object var7;
            if (this.removeFrameTail(var2)) {
               int var6 = var4.readableBytes() - PerMessageDeflateDecoder.FRAME_TAIL.length;
               var7 = var4.slice(0, var6);
            } else {
               var7 = var4;
            }

            Object var8;
            if (var2 instanceof TextWebSocketFrame) {
               var8 = new TextWebSocketFrame(var2.isFinalFragment(), this.rsv(var2), (ByteBuf)var7);
            } else if (var2 instanceof BinaryWebSocketFrame) {
               var8 = new BinaryWebSocketFrame(var2.isFinalFragment(), this.rsv(var2), (ByteBuf)var7);
            } else {
               if (!(var2 instanceof ContinuationWebSocketFrame)) {
                  throw new CodecException("unexpected frame type: " + var2.getClass().getName());
               }

               var8 = new ContinuationWebSocketFrame(var2.isFinalFragment(), this.rsv(var2), (ByteBuf)var7);
            }

            var3.add(var8);
            return;
         }

         if (!var5.isReadable()) {
            var5.release();
         } else {
            var4.addComponent(true, var5);
         }
      }
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cleanup();
      super.handlerRemoved(var1);
   }

   private void cleanup() {
      if (this.encoder != null) {
         if (this.encoder.finish()) {
            while(true) {
               ByteBuf var1 = (ByteBuf)this.encoder.readOutbound();
               if (var1 == null) {
                  break;
               }

               var1.release();
            }
         }

         this.encoder = null;
      }

   }
}
