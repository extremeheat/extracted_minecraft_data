package io.netty.handler.codec.http;

import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map.Entry;

final class HttpMessageUtil {
   static StringBuilder appendRequest(StringBuilder var0, HttpRequest var1) {
      appendCommon(var0, var1);
      appendInitialLine(var0, var1);
      appendHeaders(var0, var1.headers());
      removeLastNewLine(var0);
      return var0;
   }

   static StringBuilder appendResponse(StringBuilder var0, HttpResponse var1) {
      appendCommon(var0, var1);
      appendInitialLine(var0, var1);
      appendHeaders(var0, var1.headers());
      removeLastNewLine(var0);
      return var0;
   }

   private static void appendCommon(StringBuilder var0, HttpMessage var1) {
      var0.append(StringUtil.simpleClassName((Object)var1));
      var0.append("(decodeResult: ");
      var0.append(var1.decoderResult());
      var0.append(", version: ");
      var0.append(var1.protocolVersion());
      var0.append(')');
      var0.append(StringUtil.NEWLINE);
   }

   static StringBuilder appendFullRequest(StringBuilder var0, FullHttpRequest var1) {
      appendFullCommon(var0, var1);
      appendInitialLine(var0, (HttpRequest)var1);
      appendHeaders(var0, var1.headers());
      appendHeaders(var0, var1.trailingHeaders());
      removeLastNewLine(var0);
      return var0;
   }

   static StringBuilder appendFullResponse(StringBuilder var0, FullHttpResponse var1) {
      appendFullCommon(var0, var1);
      appendInitialLine(var0, (HttpResponse)var1);
      appendHeaders(var0, var1.headers());
      appendHeaders(var0, var1.trailingHeaders());
      removeLastNewLine(var0);
      return var0;
   }

   private static void appendFullCommon(StringBuilder var0, FullHttpMessage var1) {
      var0.append(StringUtil.simpleClassName((Object)var1));
      var0.append("(decodeResult: ");
      var0.append(var1.decoderResult());
      var0.append(", version: ");
      var0.append(var1.protocolVersion());
      var0.append(", content: ");
      var0.append(var1.content());
      var0.append(')');
      var0.append(StringUtil.NEWLINE);
   }

   private static void appendInitialLine(StringBuilder var0, HttpRequest var1) {
      var0.append(var1.method());
      var0.append(' ');
      var0.append(var1.uri());
      var0.append(' ');
      var0.append(var1.protocolVersion());
      var0.append(StringUtil.NEWLINE);
   }

   private static void appendInitialLine(StringBuilder var0, HttpResponse var1) {
      var0.append(var1.protocolVersion());
      var0.append(' ');
      var0.append(var1.status());
      var0.append(StringUtil.NEWLINE);
   }

   private static void appendHeaders(StringBuilder var0, HttpHeaders var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var0.append((String)var3.getKey());
         var0.append(": ");
         var0.append((String)var3.getValue());
         var0.append(StringUtil.NEWLINE);
      }

   }

   private static void removeLastNewLine(StringBuilder var0) {
      var0.setLength(var0.length() - StringUtil.NEWLINE.length());
   }

   private HttpMessageUtil() {
      super();
   }
}
