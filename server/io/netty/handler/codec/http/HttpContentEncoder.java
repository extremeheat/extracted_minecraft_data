package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class HttpContentEncoder extends MessageToMessageCodec<HttpRequest, HttpObject> {
   private static final CharSequence ZERO_LENGTH_HEAD = "HEAD";
   private static final CharSequence ZERO_LENGTH_CONNECT = "CONNECT";
   private static final int CONTINUE_CODE;
   private final Queue<CharSequence> acceptEncodingQueue = new ArrayDeque();
   private EmbeddedChannel encoder;
   private HttpContentEncoder.State state;

   public HttpContentEncoder() {
      super();
      this.state = HttpContentEncoder.State.AWAIT_HEADERS;
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return var1 instanceof HttpContent || var1 instanceof HttpResponse;
   }

   protected void decode(ChannelHandlerContext var1, HttpRequest var2, List<Object> var3) throws Exception {
      Object var4 = var2.headers().get((CharSequence)HttpHeaderNames.ACCEPT_ENCODING);
      if (var4 == null) {
         var4 = HttpContentDecoder.IDENTITY;
      }

      HttpMethod var5 = var2.method();
      if (var5 == HttpMethod.HEAD) {
         var4 = ZERO_LENGTH_HEAD;
      } else if (var5 == HttpMethod.CONNECT) {
         var4 = ZERO_LENGTH_CONNECT;
      }

      this.acceptEncodingQueue.add(var4);
      var3.add(ReferenceCountUtil.retain(var2));
   }

   protected void encode(ChannelHandlerContext var1, HttpObject var2, List<Object> var3) throws Exception {
      boolean var4 = var2 instanceof HttpResponse && var2 instanceof LastHttpContent;
      switch(this.state) {
      case AWAIT_HEADERS:
         ensureHeaders(var2);

         assert this.encoder == null;

         HttpResponse var5 = (HttpResponse)var2;
         int var6 = var5.status().code();
         CharSequence var7;
         if (var6 == CONTINUE_CODE) {
            var7 = null;
         } else {
            var7 = (CharSequence)this.acceptEncodingQueue.poll();
            if (var7 == null) {
               throw new IllegalStateException("cannot send more responses than requests");
            }
         }

         if (isPassthru(var5.protocolVersion(), var6, var7)) {
            if (var4) {
               var3.add(ReferenceCountUtil.retain(var5));
            } else {
               var3.add(var5);
               this.state = HttpContentEncoder.State.PASS_THROUGH;
            }
            break;
         } else if (var4 && !((ByteBufHolder)var5).content().isReadable()) {
            var3.add(ReferenceCountUtil.retain(var5));
            break;
         } else {
            HttpContentEncoder.Result var8 = this.beginEncode(var5, var7.toString());
            if (var8 == null) {
               if (var4) {
                  var3.add(ReferenceCountUtil.retain(var5));
               } else {
                  var3.add(var5);
                  this.state = HttpContentEncoder.State.PASS_THROUGH;
               }
               break;
            } else {
               this.encoder = var8.contentEncoder();
               var5.headers().set((CharSequence)HttpHeaderNames.CONTENT_ENCODING, (Object)var8.targetContentEncoding());
               if (var4) {
                  DefaultHttpResponse var9 = new DefaultHttpResponse(var5.protocolVersion(), var5.status());
                  var9.headers().set(var5.headers());
                  var3.add(var9);
                  ensureContent(var5);
                  this.encodeFullResponse(var9, (HttpContent)var5, var3);
                  break;
               } else {
                  var5.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
                  var5.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
                  var3.add(var5);
                  this.state = HttpContentEncoder.State.AWAIT_CONTENT;
                  if (!(var2 instanceof HttpContent)) {
                     break;
                  }
               }
            }
         }
      case AWAIT_CONTENT:
         ensureContent(var2);
         if (this.encodeContent((HttpContent)var2, var3)) {
            this.state = HttpContentEncoder.State.AWAIT_HEADERS;
         }
         break;
      case PASS_THROUGH:
         ensureContent(var2);
         var3.add(ReferenceCountUtil.retain(var2));
         if (var2 instanceof LastHttpContent) {
            this.state = HttpContentEncoder.State.AWAIT_HEADERS;
         }
      }

   }

   private void encodeFullResponse(HttpResponse var1, HttpContent var2, List<Object> var3) {
      int var4 = var3.size();
      this.encodeContent(var2, var3);
      if (HttpUtil.isContentLengthSet(var1)) {
         int var5 = 0;

         for(int var6 = var4; var6 < var3.size(); ++var6) {
            Object var7 = var3.get(var6);
            if (var7 instanceof HttpContent) {
               var5 += ((HttpContent)var7).content().readableBytes();
            }
         }

         HttpUtil.setContentLength(var1, (long)var5);
      } else {
         var1.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
      }

   }

   private static boolean isPassthru(HttpVersion var0, int var1, CharSequence var2) {
      return var1 < 200 || var1 == 204 || var1 == 304 || var2 == ZERO_LENGTH_HEAD || var2 == ZERO_LENGTH_CONNECT && var1 == 200 || var0 == HttpVersion.HTTP_1_0;
   }

   private static void ensureHeaders(HttpObject var0) {
      if (!(var0 instanceof HttpResponse)) {
         throw new IllegalStateException("unexpected message type: " + var0.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
      }
   }

   private static void ensureContent(HttpObject var0) {
      if (!(var0 instanceof HttpContent)) {
         throw new IllegalStateException("unexpected message type: " + var0.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
      }
   }

   private boolean encodeContent(HttpContent var1, List<Object> var2) {
      ByteBuf var3 = var1.content();
      this.encode(var3, var2);
      if (var1 instanceof LastHttpContent) {
         this.finishEncode(var2);
         LastHttpContent var4 = (LastHttpContent)var1;
         HttpHeaders var5 = var4.trailingHeaders();
         if (var5.isEmpty()) {
            var2.add(LastHttpContent.EMPTY_LAST_CONTENT);
         } else {
            var2.add(new ComposedLastHttpContent(var5));
         }

         return true;
      } else {
         return false;
      }
   }

   protected abstract HttpContentEncoder.Result beginEncode(HttpResponse var1, String var2) throws Exception;

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cleanupSafely(var1);
      super.handlerRemoved(var1);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.cleanupSafely(var1);
      super.channelInactive(var1);
   }

   private void cleanup() {
      if (this.encoder != null) {
         this.encoder.finishAndReleaseAll();
         this.encoder = null;
      }

   }

   private void cleanupSafely(ChannelHandlerContext var1) {
      try {
         this.cleanup();
      } catch (Throwable var3) {
         var1.fireExceptionCaught(var3);
      }

   }

   private void encode(ByteBuf var1, List<Object> var2) {
      this.encoder.writeOutbound(var1.retain());
      this.fetchEncoderOutput(var2);
   }

   private void finishEncode(List<Object> var1) {
      if (this.encoder.finish()) {
         this.fetchEncoderOutput(var1);
      }

      this.encoder = null;
   }

   private void fetchEncoderOutput(List<Object> var1) {
      while(true) {
         ByteBuf var2 = (ByteBuf)this.encoder.readOutbound();
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
      CONTINUE_CODE = HttpResponseStatus.CONTINUE.code();
   }

   public static final class Result {
      private final String targetContentEncoding;
      private final EmbeddedChannel contentEncoder;

      public Result(String var1, EmbeddedChannel var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException("targetContentEncoding");
         } else if (var2 == null) {
            throw new NullPointerException("contentEncoder");
         } else {
            this.targetContentEncoding = var1;
            this.contentEncoder = var2;
         }
      }

      public String targetContentEncoding() {
         return this.targetContentEncoding;
      }

      public EmbeddedChannel contentEncoder() {
         return this.contentEncoder;
      }
   }

   private static enum State {
      PASS_THROUGH,
      AWAIT_HEADERS,
      AWAIT_CONTENT;

      private State() {
      }
   }
}
