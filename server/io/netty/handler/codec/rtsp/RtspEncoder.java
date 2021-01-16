package io.netty.handler.codec.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

public class RtspEncoder extends HttpObjectEncoder<HttpMessage> {
   private static final int CRLF_SHORT = 3338;

   public RtspEncoder() {
      super();
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return super.acceptOutboundMessage(var1) && (var1 instanceof HttpRequest || var1 instanceof HttpResponse);
   }

   protected void encodeInitialLine(ByteBuf var1, HttpMessage var2) throws Exception {
      if (var2 instanceof HttpRequest) {
         HttpRequest var3 = (HttpRequest)var2;
         ByteBufUtil.copy(var3.method().asciiName(), var1);
         var1.writeByte(32);
         var1.writeCharSequence(var3.uri(), CharsetUtil.UTF_8);
         var1.writeByte(32);
         var1.writeCharSequence(var3.protocolVersion().toString(), CharsetUtil.US_ASCII);
         ByteBufUtil.writeShortBE(var1, 3338);
      } else {
         if (!(var2 instanceof HttpResponse)) {
            throw new UnsupportedMessageTypeException("Unsupported type " + StringUtil.simpleClassName((Object)var2));
         }

         HttpResponse var4 = (HttpResponse)var2;
         var1.writeCharSequence(var4.protocolVersion().toString(), CharsetUtil.US_ASCII);
         var1.writeByte(32);
         ByteBufUtil.copy(var4.status().codeAsText(), var1);
         var1.writeByte(32);
         var1.writeCharSequence(var4.status().reasonPhrase(), CharsetUtil.US_ASCII);
         ByteBufUtil.writeShortBE(var1, 3338);
      }

   }
}
