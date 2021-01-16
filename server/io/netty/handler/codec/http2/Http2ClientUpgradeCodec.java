package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientUpgradeHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.collection.CharObjectMap;
import io.netty.util.internal.ObjectUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Http2ClientUpgradeCodec implements HttpClientUpgradeHandler.UpgradeCodec {
   private static final List<CharSequence> UPGRADE_HEADERS;
   private final String handlerName;
   private final Http2ConnectionHandler connectionHandler;
   private final ChannelHandler upgradeToHandler;

   public Http2ClientUpgradeCodec(Http2FrameCodec var1, ChannelHandler var2) {
      this((String)null, (Http2FrameCodec)var1, var2);
   }

   public Http2ClientUpgradeCodec(String var1, Http2FrameCodec var2, ChannelHandler var3) {
      this(var1, (Http2ConnectionHandler)var2, var3);
   }

   public Http2ClientUpgradeCodec(Http2ConnectionHandler var1) {
      this((String)null, var1);
   }

   public Http2ClientUpgradeCodec(String var1, Http2ConnectionHandler var2) {
      this(var1, (Http2ConnectionHandler)var2, var2);
   }

   private Http2ClientUpgradeCodec(String var1, Http2ConnectionHandler var2, ChannelHandler var3) {
      super();
      this.handlerName = var1;
      this.connectionHandler = (Http2ConnectionHandler)ObjectUtil.checkNotNull(var2, "connectionHandler");
      this.upgradeToHandler = (ChannelHandler)ObjectUtil.checkNotNull(var3, "upgradeToHandler");
   }

   public CharSequence protocol() {
      return Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME;
   }

   public Collection<CharSequence> setUpgradeHeaders(ChannelHandlerContext var1, HttpRequest var2) {
      CharSequence var3 = this.getSettingsHeaderValue(var1);
      var2.headers().set((CharSequence)Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER, (Object)var3);
      return UPGRADE_HEADERS;
   }

   public void upgradeTo(ChannelHandlerContext var1, FullHttpResponse var2) throws Exception {
      var1.pipeline().addAfter(var1.name(), this.handlerName, this.upgradeToHandler);
      this.connectionHandler.onHttpClientUpgrade();
   }

   private CharSequence getSettingsHeaderValue(ChannelHandlerContext var1) {
      ByteBuf var2 = null;
      ByteBuf var3 = null;

      try {
         Http2Settings var4 = this.connectionHandler.decoder().localSettings();
         int var5 = 6 * var4.size();
         var2 = var1.alloc().buffer(var5);
         Iterator var6 = var4.entries().iterator();

         while(var6.hasNext()) {
            CharObjectMap.PrimitiveEntry var7 = (CharObjectMap.PrimitiveEntry)var6.next();
            var2.writeChar(var7.key());
            var2.writeInt(((Long)var7.value()).intValue());
         }

         var3 = Base64.encode(var2, Base64Dialect.URL_SAFE);
         String var11 = var3.toString(CharsetUtil.UTF_8);
         return var11;
      } finally {
         ReferenceCountUtil.release(var2);
         ReferenceCountUtil.release(var3);
      }
   }

   static {
      UPGRADE_HEADERS = Collections.singletonList(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
   }
}
