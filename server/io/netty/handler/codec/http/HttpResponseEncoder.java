package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class HttpResponseEncoder extends HttpObjectEncoder<HttpResponse> {
   public HttpResponseEncoder() {
      super();
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return super.acceptOutboundMessage(var1) && !(var1 instanceof HttpRequest);
   }

   protected void encodeInitialLine(ByteBuf var1, HttpResponse var2) throws Exception {
      var2.protocolVersion().encode(var1);
      var1.writeByte(32);
      var2.status().encode(var1);
      ByteBufUtil.writeShortBE(var1, 3338);
   }

   protected void sanitizeHeadersBeforeEncode(HttpResponse var1, boolean var2) {
      if (var2) {
         HttpResponseStatus var3 = var1.status();
         if (var3.codeClass() != HttpStatusClass.INFORMATIONAL && var3.code() != HttpResponseStatus.NO_CONTENT.code()) {
            if (var3.code() == HttpResponseStatus.RESET_CONTENT.code()) {
               var1.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
               var1.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, 0);
            }
         } else {
            var1.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
            var1.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
         }
      }

   }

   protected boolean isContentAlwaysEmpty(HttpResponse var1) {
      HttpResponseStatus var2 = var1.status();
      if (var2.codeClass() == HttpStatusClass.INFORMATIONAL) {
         return var2.code() == HttpResponseStatus.SWITCHING_PROTOCOLS.code() ? var1.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION) : true;
      } else {
         return var2.code() == HttpResponseStatus.NO_CONTENT.code() || var2.code() == HttpResponseStatus.NOT_MODIFIED.code() || var2.code() == HttpResponseStatus.RESET_CONTENT.code();
      }
   }
}
