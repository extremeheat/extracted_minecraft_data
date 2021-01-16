package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public final class CleartextHttp2ServerUpgradeHandler extends ChannelHandlerAdapter {
   private static final ByteBuf CONNECTION_PREFACE = Unpooled.unreleasableBuffer(Http2CodecUtil.connectionPrefaceBuf());
   private final HttpServerCodec httpServerCodec;
   private final HttpServerUpgradeHandler httpServerUpgradeHandler;
   private final ChannelHandler http2ServerHandler;

   public CleartextHttp2ServerUpgradeHandler(HttpServerCodec var1, HttpServerUpgradeHandler var2, ChannelHandler var3) {
      super();
      this.httpServerCodec = (HttpServerCodec)ObjectUtil.checkNotNull(var1, "httpServerCodec");
      this.httpServerUpgradeHandler = (HttpServerUpgradeHandler)ObjectUtil.checkNotNull(var2, "httpServerUpgradeHandler");
      this.http2ServerHandler = (ChannelHandler)ObjectUtil.checkNotNull(var3, "http2ServerHandler");
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      var1.pipeline().addBefore(var1.name(), (String)null, new CleartextHttp2ServerUpgradeHandler.PriorKnowledgeHandler()).addBefore(var1.name(), (String)null, this.httpServerCodec).replace((ChannelHandler)this, (String)null, this.httpServerUpgradeHandler);
   }

   public static final class PriorKnowledgeUpgradeEvent {
      private static final CleartextHttp2ServerUpgradeHandler.PriorKnowledgeUpgradeEvent INSTANCE = new CleartextHttp2ServerUpgradeHandler.PriorKnowledgeUpgradeEvent();

      private PriorKnowledgeUpgradeEvent() {
         super();
      }
   }

   private final class PriorKnowledgeHandler extends ByteToMessageDecoder {
      private PriorKnowledgeHandler() {
         super();
      }

      protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
         int var4 = CleartextHttp2ServerUpgradeHandler.CONNECTION_PREFACE.readableBytes();
         int var5 = Math.min(var2.readableBytes(), var4);
         if (!ByteBufUtil.equals(CleartextHttp2ServerUpgradeHandler.CONNECTION_PREFACE, CleartextHttp2ServerUpgradeHandler.CONNECTION_PREFACE.readerIndex(), var2, var2.readerIndex(), var5)) {
            var1.pipeline().remove((ChannelHandler)this);
         } else if (var5 == var4) {
            var1.pipeline().remove((ChannelHandler)CleartextHttp2ServerUpgradeHandler.this.httpServerCodec).remove((ChannelHandler)CleartextHttp2ServerUpgradeHandler.this.httpServerUpgradeHandler);
            var1.pipeline().addAfter(var1.name(), (String)null, CleartextHttp2ServerUpgradeHandler.this.http2ServerHandler);
            var1.pipeline().remove((ChannelHandler)this);
            var1.fireUserEventTriggered(CleartextHttp2ServerUpgradeHandler.PriorKnowledgeUpgradeEvent.INSTANCE);
         }

      }

      // $FF: synthetic method
      PriorKnowledgeHandler(Object var2) {
         this();
      }
   }
}
