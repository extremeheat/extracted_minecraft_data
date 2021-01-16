package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class SpdyHttpEncoder extends MessageToMessageEncoder<HttpObject> {
   private int currentStreamId;
   private final boolean validateHeaders;
   private final boolean headersToLowerCase;

   public SpdyHttpEncoder(SpdyVersion var1) {
      this(var1, true, true);
   }

   public SpdyHttpEncoder(SpdyVersion var1, boolean var2, boolean var3) {
      super();
      if (var1 == null) {
         throw new NullPointerException("version");
      } else {
         this.headersToLowerCase = var2;
         this.validateHeaders = var3;
      }
   }

   protected void encode(ChannelHandlerContext var1, HttpObject var2, List<Object> var3) throws Exception {
      boolean var4 = false;
      boolean var5 = false;
      if (var2 instanceof HttpRequest) {
         HttpRequest var6 = (HttpRequest)var2;
         SpdySynStreamFrame var7 = this.createSynStreamFrame(var6);
         var3.add(var7);
         var5 = var7.isLast() || var7.isUnidirectional();
         var4 = true;
      }

      if (var2 instanceof HttpResponse) {
         HttpResponse var14 = (HttpResponse)var2;
         SpdyHeadersFrame var16 = this.createHeadersFrame(var14);
         var3.add(var16);
         var5 = var16.isLast();
         var4 = true;
      }

      if (var2 instanceof HttpContent && !var5) {
         HttpContent var15 = (HttpContent)var2;
         var15.content().retain();
         DefaultSpdyDataFrame var17 = new DefaultSpdyDataFrame(this.currentStreamId, var15.content());
         if (!(var15 instanceof LastHttpContent)) {
            var3.add(var17);
         } else {
            LastHttpContent var8 = (LastHttpContent)var15;
            HttpHeaders var9 = var8.trailingHeaders();
            if (var9.isEmpty()) {
               var17.setLast(true);
               var3.add(var17);
            } else {
               DefaultSpdyHeadersFrame var10 = new DefaultSpdyHeadersFrame(this.currentStreamId, this.validateHeaders);
               var10.setLast(true);
               Iterator var11 = var9.iteratorCharSequence();

               while(var11.hasNext()) {
                  Entry var12 = (Entry)var11.next();
                  Object var13 = this.headersToLowerCase ? AsciiString.of((CharSequence)var12.getKey()).toLowerCase() : (CharSequence)var12.getKey();
                  var10.headers().add(var13, var12.getValue());
               }

               var3.add(var17);
               var3.add(var10);
            }
         }

         var4 = true;
      }

      if (!var4) {
         throw new UnsupportedMessageTypeException(var2, new Class[0]);
      }
   }

   private SpdySynStreamFrame createSynStreamFrame(HttpRequest var1) throws Exception {
      HttpHeaders var2 = var1.headers();
      int var3 = var2.getInt(SpdyHttpHeaders.Names.STREAM_ID);
      int var4 = var2.getInt(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, 0);
      byte var5 = (byte)var2.getInt(SpdyHttpHeaders.Names.PRIORITY, 0);
      String var6 = var2.get((CharSequence)SpdyHttpHeaders.Names.SCHEME);
      var2.remove((CharSequence)SpdyHttpHeaders.Names.STREAM_ID);
      var2.remove((CharSequence)SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID);
      var2.remove((CharSequence)SpdyHttpHeaders.Names.PRIORITY);
      var2.remove((CharSequence)SpdyHttpHeaders.Names.SCHEME);
      var2.remove((CharSequence)HttpHeaderNames.CONNECTION);
      var2.remove("Keep-Alive");
      var2.remove("Proxy-Connection");
      var2.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
      DefaultSpdySynStreamFrame var7 = new DefaultSpdySynStreamFrame(var3, var4, var5, this.validateHeaders);
      SpdyHeaders var8 = var7.headers();
      var8.set(SpdyHeaders.HttpNames.METHOD, var1.method().name());
      var8.set(SpdyHeaders.HttpNames.PATH, var1.uri());
      var8.set(SpdyHeaders.HttpNames.VERSION, var1.protocolVersion().text());
      String var9 = var2.get((CharSequence)HttpHeaderNames.HOST);
      var2.remove((CharSequence)HttpHeaderNames.HOST);
      var8.set(SpdyHeaders.HttpNames.HOST, var9);
      if (var6 == null) {
         var6 = "https";
      }

      var8.set(SpdyHeaders.HttpNames.SCHEME, var6);
      Iterator var10 = var2.iteratorCharSequence();

      while(var10.hasNext()) {
         Entry var11 = (Entry)var10.next();
         Object var12 = this.headersToLowerCase ? AsciiString.of((CharSequence)var11.getKey()).toLowerCase() : (CharSequence)var11.getKey();
         var8.add(var12, var11.getValue());
      }

      this.currentStreamId = var7.streamId();
      if (var4 == 0) {
         var7.setLast(isLast(var1));
      } else {
         var7.setUnidirectional(true);
      }

      return var7;
   }

   private SpdyHeadersFrame createHeadersFrame(HttpResponse var1) throws Exception {
      HttpHeaders var2 = var1.headers();
      int var3 = var2.getInt(SpdyHttpHeaders.Names.STREAM_ID);
      var2.remove((CharSequence)SpdyHttpHeaders.Names.STREAM_ID);
      var2.remove((CharSequence)HttpHeaderNames.CONNECTION);
      var2.remove("Keep-Alive");
      var2.remove("Proxy-Connection");
      var2.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
      Object var4;
      if (SpdyCodecUtil.isServerId(var3)) {
         var4 = new DefaultSpdyHeadersFrame(var3, this.validateHeaders);
      } else {
         var4 = new DefaultSpdySynReplyFrame(var3, this.validateHeaders);
      }

      SpdyHeaders var5 = ((SpdyHeadersFrame)var4).headers();
      var5.set(SpdyHeaders.HttpNames.STATUS, var1.status().codeAsText());
      var5.set(SpdyHeaders.HttpNames.VERSION, var1.protocolVersion().text());
      Iterator var6 = var2.iteratorCharSequence();

      while(var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         Object var8 = this.headersToLowerCase ? AsciiString.of((CharSequence)var7.getKey()).toLowerCase() : (CharSequence)var7.getKey();
         ((SpdyHeadersFrame)var4).headers().add(var8, var7.getValue());
      }

      this.currentStreamId = var3;
      ((SpdyHeadersFrame)var4).setLast(isLast(var1));
      return (SpdyHeadersFrame)var4;
   }

   private static boolean isLast(HttpMessage var0) {
      if (var0 instanceof FullHttpMessage) {
         FullHttpMessage var1 = (FullHttpMessage)var0;
         if (var1.trailingHeaders().isEmpty() && !var1.content().isReadable()) {
            return true;
         }
      }

      return false;
   }
}
