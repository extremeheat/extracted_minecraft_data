package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Http2ServerUpgradeCodec implements HttpServerUpgradeHandler.UpgradeCodec {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Http2ServerUpgradeCodec.class);
   private static final List<CharSequence> REQUIRED_UPGRADE_HEADERS;
   private static final ChannelHandler[] EMPTY_HANDLERS;
   private final String handlerName;
   private final Http2ConnectionHandler connectionHandler;
   private final ChannelHandler[] handlers;
   private final Http2FrameReader frameReader;
   private Http2Settings settings;

   public Http2ServerUpgradeCodec(Http2ConnectionHandler var1) {
      this((String)null, var1, EMPTY_HANDLERS);
   }

   public Http2ServerUpgradeCodec(Http2MultiplexCodec var1) {
      this((String)null, var1, EMPTY_HANDLERS);
   }

   public Http2ServerUpgradeCodec(String var1, Http2ConnectionHandler var2) {
      this(var1, var2, EMPTY_HANDLERS);
   }

   public Http2ServerUpgradeCodec(String var1, Http2MultiplexCodec var2) {
      this(var1, var2, EMPTY_HANDLERS);
   }

   public Http2ServerUpgradeCodec(Http2FrameCodec var1, ChannelHandler... var2) {
      this((String)null, var1, var2);
   }

   private Http2ServerUpgradeCodec(String var1, Http2ConnectionHandler var2, ChannelHandler... var3) {
      super();
      this.handlerName = var1;
      this.connectionHandler = var2;
      this.handlers = var3;
      this.frameReader = new DefaultHttp2FrameReader();
   }

   public Collection<CharSequence> requiredUpgradeHeaders() {
      return REQUIRED_UPGRADE_HEADERS;
   }

   public boolean prepareUpgradeResponse(ChannelHandlerContext var1, FullHttpRequest var2, HttpHeaders var3) {
      try {
         List var4 = var2.headers().getAll(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
         if (!var4.isEmpty() && var4.size() <= 1) {
            this.settings = this.decodeSettingsHeader(var1, (CharSequence)var4.get(0));
            return true;
         } else {
            throw new IllegalArgumentException("There must be 1 and only 1 " + Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER + " header.");
         }
      } catch (Throwable var5) {
         logger.info("Error during upgrade to HTTP/2", var5);
         return false;
      }
   }

   public void upgradeTo(ChannelHandlerContext var1, FullHttpRequest var2) {
      try {
         var1.pipeline().addAfter(var1.name(), this.handlerName, this.connectionHandler);
         this.connectionHandler.onHttpServerUpgrade(this.settings);
      } catch (Http2Exception var5) {
         var1.fireExceptionCaught(var5);
         var1.close();
         return;
      }

      if (this.handlers != null) {
         String var3 = var1.pipeline().context((ChannelHandler)this.connectionHandler).name();

         for(int var4 = this.handlers.length - 1; var4 >= 0; --var4) {
            var1.pipeline().addAfter(var3, (String)null, this.handlers[var4]);
         }
      }

   }

   private Http2Settings decodeSettingsHeader(ChannelHandlerContext var1, CharSequence var2) throws Http2Exception {
      ByteBuf var3 = ByteBufUtil.encodeString(var1.alloc(), CharBuffer.wrap(var2), CharsetUtil.UTF_8);

      Http2Settings var6;
      try {
         ByteBuf var4 = Base64.decode(var3, Base64Dialect.URL_SAFE);
         ByteBuf var5 = createSettingsFrame(var1, var4);
         var6 = this.decodeSettings(var1, var5);
      } finally {
         var3.release();
      }

      return var6;
   }

   private Http2Settings decodeSettings(ChannelHandlerContext var1, ByteBuf var2) throws Http2Exception {
      Http2Settings var4;
      try {
         final Http2Settings var3 = new Http2Settings();
         this.frameReader.readFrame(var1, var2, new Http2FrameAdapter() {
            public void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) {
               var3.copyFrom(var2);
            }
         });
         var4 = var3;
      } finally {
         var2.release();
      }

      return var4;
   }

   private static ByteBuf createSettingsFrame(ChannelHandlerContext var0, ByteBuf var1) {
      ByteBuf var2 = var0.alloc().buffer(9 + var1.readableBytes());
      Http2CodecUtil.writeFrameHeader(var2, var1.readableBytes(), (byte)4, new Http2Flags(), 0);
      var2.writeBytes(var1);
      var1.release();
      return var2;
   }

   static {
      REQUIRED_UPGRADE_HEADERS = Collections.singletonList(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
      EMPTY_HANDLERS = new ChannelHandler[0];
   }
}
