package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;

public class HttpRequestEncoder extends HttpObjectEncoder<HttpRequest> {
   private static final char SLASH = '/';
   private static final char QUESTION_MARK = '?';
   private static final int SLASH_AND_SPACE_SHORT = 12064;
   private static final int SPACE_SLASH_AND_SPACE_MEDIUM = 2109216;

   public HttpRequestEncoder() {
      super();
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return super.acceptOutboundMessage(var1) && !(var1 instanceof HttpResponse);
   }

   protected void encodeInitialLine(ByteBuf var1, HttpRequest var2) throws Exception {
      ByteBufUtil.copy(var2.method().asciiName(), var1);
      String var3 = var2.uri();
      if (var3.isEmpty()) {
         ByteBufUtil.writeMediumBE(var1, 2109216);
      } else {
         Object var4 = var3;
         boolean var5 = false;
         int var6 = var3.indexOf("://");
         if (var6 != -1 && var3.charAt(0) != '/') {
            var6 += 3;
            int var7 = var3.indexOf(63, var6);
            if (var7 == -1) {
               if (var3.lastIndexOf(47) < var6) {
                  var5 = true;
               }
            } else if (var3.lastIndexOf(47, var7) < var6) {
               var4 = (new StringBuilder(var3)).insert(var7, '/');
            }
         }

         var1.writeByte(32).writeCharSequence((CharSequence)var4, CharsetUtil.UTF_8);
         if (var5) {
            ByteBufUtil.writeShortBE(var1, 12064);
         } else {
            var1.writeByte(32);
         }
      }

      var2.protocolVersion().encode(var1);
      ByteBufUtil.writeShortBE(var1, 3338);
   }
}
