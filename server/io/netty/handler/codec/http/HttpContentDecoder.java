package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public abstract class HttpContentDecoder extends MessageToMessageDecoder<HttpObject> {
   static final String IDENTITY;
   protected ChannelHandlerContext ctx;
   private EmbeddedChannel decoder;
   private boolean continueResponse;

   public HttpContentDecoder() {
      super();
   }

   protected void decode(ChannelHandlerContext var1, HttpObject var2, List<Object> var3) throws Exception {
      if (var2 instanceof HttpResponse && ((HttpResponse)var2).status().code() == 100) {
         if (!(var2 instanceof LastHttpContent)) {
            this.continueResponse = true;
         }

         var3.add(ReferenceCountUtil.retain(var2));
      } else if (this.continueResponse) {
         if (var2 instanceof LastHttpContent) {
            this.continueResponse = false;
         }

         var3.add(ReferenceCountUtil.retain(var2));
      } else {
         if (var2 instanceof HttpMessage) {
            this.cleanup();
            HttpMessage var4 = (HttpMessage)var2;
            HttpHeaders var5 = var4.headers();
            String var6 = var5.get((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
            if (var6 != null) {
               var6 = var6.trim();
            } else {
               var6 = IDENTITY;
            }

            this.decoder = this.newContentDecoder(var6);
            if (this.decoder == null) {
               if (var4 instanceof HttpContent) {
                  ((HttpContent)var4).retain();
               }

               var3.add(var4);
               return;
            }

            if (var5.contains((CharSequence)HttpHeaderNames.CONTENT_LENGTH)) {
               var5.remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
               var5.set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
            }

            String var7 = this.getTargetContentEncoding(var6);
            if (HttpHeaderValues.IDENTITY.contentEquals(var7)) {
               var5.remove((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
            } else {
               var5.set((CharSequence)HttpHeaderNames.CONTENT_ENCODING, (Object)var7);
            }

            if (var4 instanceof HttpContent) {
               Object var8;
               if (var4 instanceof HttpRequest) {
                  HttpRequest var9 = (HttpRequest)var4;
                  var8 = new DefaultHttpRequest(var9.protocolVersion(), var9.method(), var9.uri());
               } else {
                  if (!(var4 instanceof HttpResponse)) {
                     throw new CodecException("Object of class " + var4.getClass().getName() + " is not a HttpRequest or HttpResponse");
                  }

                  HttpResponse var11 = (HttpResponse)var4;
                  var8 = new DefaultHttpResponse(var11.protocolVersion(), var11.status());
               }

               ((HttpMessage)var8).headers().set(var4.headers());
               ((HttpMessage)var8).setDecoderResult(var4.decoderResult());
               var3.add(var8);
            } else {
               var3.add(var4);
            }
         }

         if (var2 instanceof HttpContent) {
            HttpContent var10 = (HttpContent)var2;
            if (this.decoder == null) {
               var3.add(var10.retain());
            } else {
               this.decodeContent(var10, var3);
            }
         }

      }
   }

   private void decodeContent(HttpContent var1, List<Object> var2) {
      ByteBuf var3 = var1.content();
      this.decode(var3, var2);
      if (var1 instanceof LastHttpContent) {
         this.finishDecode(var2);
         LastHttpContent var4 = (LastHttpContent)var1;
         HttpHeaders var5 = var4.trailingHeaders();
         if (var5.isEmpty()) {
            var2.add(LastHttpContent.EMPTY_LAST_CONTENT);
         } else {
            var2.add(new ComposedLastHttpContent(var5));
         }
      }

   }

   protected abstract EmbeddedChannel newContentDecoder(String var1) throws Exception;

   protected String getTargetContentEncoding(String var1) throws Exception {
      return IDENTITY;
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cleanupSafely(var1);
      super.handlerRemoved(var1);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.cleanupSafely(var1);
      super.channelInactive(var1);
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
      super.handlerAdded(var1);
   }

   private void cleanup() {
      if (this.decoder != null) {
         this.decoder.finishAndReleaseAll();
         this.decoder = null;
      }

   }

   private void cleanupSafely(ChannelHandlerContext var1) {
      try {
         this.cleanup();
      } catch (Throwable var3) {
         var1.fireExceptionCaught(var3);
      }

   }

   private void decode(ByteBuf var1, List<Object> var2) {
      this.decoder.writeInbound(var1.retain());
      this.fetchDecoderOutput(var2);
   }

   private void finishDecode(List<Object> var1) {
      if (this.decoder.finish()) {
         this.fetchDecoderOutput(var1);
      }

      this.decoder = null;
   }

   private void fetchDecoderOutput(List<Object> var1) {
      while(true) {
         ByteBuf var2 = (ByteBuf)this.decoder.readInbound();
         if (var2 == null) {
            return;
         }

         if (!var2.isReadable()) {
            var2.release();
         } else {
            var1.add(new DefaultHttpContent(var2));
         }
      }
   }

   static {
      IDENTITY = HttpHeaderValues.IDENTITY.toString();
   }
}
