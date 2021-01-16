package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
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
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import java.util.List;

abstract class DeflateDecoder extends WebSocketExtensionDecoder {
   static final byte[] FRAME_TAIL = new byte[]{0, 0, -1, -1};
   private final boolean noContext;
   private EmbeddedChannel decoder;

   public DeflateDecoder(boolean var1) {
      super();
      this.noContext = var1;
   }

   protected abstract boolean appendFrameTail(WebSocketFrame var1);

   protected abstract int newRsv(WebSocketFrame var1);

   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      if (this.decoder == null) {
         if (!(var2 instanceof TextWebSocketFrame) && !(var2 instanceof BinaryWebSocketFrame)) {
            throw new CodecException("unexpected initial frame type: " + var2.getClass().getName());
         }

         this.decoder = new EmbeddedChannel(new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder(ZlibWrapper.NONE)});
      }

      boolean var4 = var2.content().isReadable();
      this.decoder.writeInbound(var2.content().retain());
      if (this.appendFrameTail(var2)) {
         this.decoder.writeInbound(Unpooled.wrappedBuffer(FRAME_TAIL));
      }

      CompositeByteBuf var5 = var1.alloc().compositeBuffer();

      while(true) {
         ByteBuf var6 = (ByteBuf)this.decoder.readInbound();
         if (var6 == null) {
            if (var4 && var5.numComponents() <= 0) {
               var5.release();
               throw new CodecException("cannot read uncompressed buffer");
            }

            if (var2.isFinalFragment() && this.noContext) {
               this.cleanup();
            }

            Object var7;
            if (var2 instanceof TextWebSocketFrame) {
               var7 = new TextWebSocketFrame(var2.isFinalFragment(), this.newRsv(var2), var5);
            } else if (var2 instanceof BinaryWebSocketFrame) {
               var7 = new BinaryWebSocketFrame(var2.isFinalFragment(), this.newRsv(var2), var5);
            } else {
               if (!(var2 instanceof ContinuationWebSocketFrame)) {
                  throw new CodecException("unexpected frame type: " + var2.getClass().getName());
               }

               var7 = new ContinuationWebSocketFrame(var2.isFinalFragment(), this.newRsv(var2), var5);
            }

            var3.add(var7);
            return;
         }

         if (!var6.isReadable()) {
            var6.release();
         } else {
            var5.addComponent(true, var6);
         }
      }
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cleanup();
      super.handlerRemoved(var1);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.cleanup();
      super.channelInactive(var1);
   }

   private void cleanup() {
      if (this.decoder != null) {
         if (this.decoder.finish()) {
            while(true) {
               ByteBuf var1 = (ByteBuf)this.decoder.readOutbound();
               if (var1 == null) {
                  break;
               }

               var1.release();
            }
         }

         this.decoder = null;
      }

   }
}
